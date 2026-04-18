package com.alertdesk.alertservice.service;

import com.alertdesk.alertservice.dto.AlertCreateRequest;
import com.alertdesk.alertservice.dto.AlertDetailResponse;
import com.alertdesk.alertservice.dto.AlertFilters;
import com.alertdesk.alertservice.dto.AlertListResponse;
import com.alertdesk.alertservice.dto.AlertStatusUpdateRequest;
import com.alertdesk.alertservice.dto.AlertSummaryGroupResponse;
import com.alertdesk.alertservice.dto.AlertSummaryGroupsResponse;
import com.alertdesk.alertservice.domain.Alert;
import com.alertdesk.alertservice.mapper.AlertMapper;
import com.alertdesk.alertservice.repository.AlertRepository;
import com.alertdesk.alertservice.repository.AlertSpecifications;
import com.alertdesk.alertservice.exception.BusinessRuleException;
import com.alertdesk.alertservice.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class AlertService {

    private final AlertRepository alertRepository;
    private final AlertMapper alertMapper;

    @Transactional(readOnly = true)
    public AlertListResponse listAlerts(AlertFilters filters, Pageable pageable) {
        log.info("Listing alerts with filters={} and pageable={}", filters, pageable);
        Page<Alert> page = alertRepository.findAll(AlertSpecifications.withFilters(filters), pageable);
        AlertListResponse response = new AlertListResponse(
                page.getContent().stream().map(alertMapper::toSummary).toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
        log.info("Listed {} alerts out of {} total results", response.content().size(), response.totalElements());
        return response;
    }

    @Transactional(readOnly = true)
    public AlertDetailResponse getAlert(String id) {
        log.info("Getting alert with id={}", id);
        AlertDetailResponse response = alertMapper.toDetail(findById(id));
        log.info("Retrieved alert with id={} and status={}", response.alertId(), response.status());
        return response;
    }

    @Transactional
    public AlertDetailResponse createAlert(AlertCreateRequest request) {
        log.info("Creating alert with id={} for customerId={}", request.alertId(), request.customerId());
        if (alertRepository.existsByAlertId(request.alertId())) {
            log.warn("Cannot create alert because id={} already exists", request.alertId());
            throw new BusinessRuleException("Alert with id %s already exists".formatted(request.alertId()));
        }
        Alert saved = alertRepository.save(alertMapper.toEntity(request));
        AlertDetailResponse response = alertMapper.toDetail(saved);
        log.info("Created alert with id={} and status={}", response.alertId(), response.status());
        return response;
    }

    @Transactional
    public AlertDetailResponse updateStatus(String id, AlertStatusUpdateRequest request) {
        log.info("Updating alert status for id={} to {}", id, request.status());
        Alert alert = findById(id);
        if (!alert.getStatus().canTransitionTo(request.status())) {
            log.warn("Invalid status transition for id={} from {} to {}", id, alert.getStatus(), request.status());
            throw new BusinessRuleException(
                    "Invalid status transition from %s to %s".formatted(alert.getStatus(), request.status())
            );
        }
        alert.setStatus(request.status());
        AlertDetailResponse response = alertMapper.toDetail(alert);
        log.info("Updated alert status for id={} to {}", response.alertId(), response.status());
        return response;
    }

    @Transactional(readOnly = true)
    public AlertSummaryGroupsResponse getSummary(String groupBy) {
        log.info("Getting alert summary grouped by={}", groupBy);
        Grouping grouping = Grouping.from(groupBy);
        Map<String, Long> counts = alertRepository.findAll().stream()
                .collect(Collectors.groupingBy(grouping.extractor, Collectors.counting()));

        List<AlertSummaryGroupResponse> groups = counts.entrySet().stream()
                .sorted(Map.Entry.comparingByKey(Comparator.naturalOrder()))
                .map(entry -> new AlertSummaryGroupResponse(entry.getKey(), entry.getValue()))
                .toList();

        return new AlertSummaryGroupsResponse(grouping.apiName, groups);
    }

    private Alert findById(String id) {
        log.debug("Looking up alert with id={}", id);
        return alertRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Alert not found for id={}", id);
                    return new ResourceNotFoundException("Alert %s not found".formatted(id));
                });
    }

    private enum Grouping {
        STATUS("status", alert -> alert.getStatus().name()),
        RISK_BAND("riskBand", alert -> alert.getRiskBand().name()),
        ALERT_TYPE("alertType", alert -> alert.getAlertType().name());

        private final String apiName;
        private final Function<Alert, String> extractor;

        Grouping(String apiName, Function<Alert, String> extractor) {
            this.apiName = apiName;
            this.extractor = extractor;
        }

        private static Grouping from(String value) {
            log.debug("Resolving summary grouping for value={}", value);
            for (Grouping grouping : values()) {
                if (grouping.apiName.equalsIgnoreCase(value)) {
                    return grouping;
                }
            }
            log.warn("Unsupported summary grouping requested: {}", value);
            throw new BusinessRuleException("Unsupported groupBy value: %s".formatted(value));
        }
    }
}
