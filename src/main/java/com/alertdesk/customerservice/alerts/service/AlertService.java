package com.alertdesk.customerservice.alerts.service;

import com.alertdesk.customerservice.alerts.api.dto.AlertCreateRequest;
import com.alertdesk.customerservice.alerts.api.dto.AlertDetailResponse;
import com.alertdesk.customerservice.alerts.api.dto.AlertFilters;
import com.alertdesk.customerservice.alerts.api.dto.AlertListResponse;
import com.alertdesk.customerservice.alerts.api.dto.AlertStatusUpdateRequest;
import com.alertdesk.customerservice.alerts.api.dto.AlertSummaryGroupResponse;
import com.alertdesk.customerservice.alerts.api.dto.AlertSummaryGroupsResponse;
import com.alertdesk.customerservice.alerts.domain.Alert;
import com.alertdesk.customerservice.alerts.repository.AlertRepository;
import com.alertdesk.customerservice.alerts.repository.AlertSpecifications;
import com.alertdesk.customerservice.shared.api.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AlertService {

    private final AlertRepository alertRepository;
    private final AlertMapper alertMapper;

    @Transactional(readOnly = true)
    public AlertListResponse listAlerts(AlertFilters filters, Pageable pageable) {
        Page<Alert> page = alertRepository.findAll(AlertSpecifications.withFilters(filters), pageable);
        return new AlertListResponse(
                page.getContent().stream().map(alertMapper::toSummary).toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }

    @Transactional(readOnly = true)
    public AlertDetailResponse getAlert(String id) {
        return alertMapper.toDetail(findById(id));
    }

    @Transactional
    public AlertDetailResponse createAlert(AlertCreateRequest request) {
        if (alertRepository.existsByAlertId(request.alertId())) {
            throw new ApiException(HttpStatus.CONFLICT, "Alert with id %s already exists".formatted(request.alertId()));
        }
        Alert saved = alertRepository.save(alertMapper.toEntity(request));
        return alertMapper.toDetail(saved);
    }

    @Transactional
    public AlertDetailResponse updateStatus(String id, AlertStatusUpdateRequest request) {
        Alert alert = findById(id);
        if (!alert.getStatus().canTransitionTo(request.status())) {
            throw new ApiException(
                    HttpStatus.UNPROCESSABLE_ENTITY,
                    "Invalid status transition from %s to %s".formatted(alert.getStatus(), request.status())
            );
        }
        alert.setStatus(request.status());
        return alertMapper.toDetail(alert);
    }

    @Transactional(readOnly = true)
    public AlertSummaryGroupsResponse getSummary(String groupBy) {
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
        return alertRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Alert %s not found".formatted(id)));
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
            for (Grouping grouping : values()) {
                if (grouping.apiName.equalsIgnoreCase(value)) {
                    return grouping;
                }
            }
            throw new ApiException(HttpStatus.BAD_REQUEST, "Unsupported groupBy value: %s".formatted(value));
        }
    }
}
