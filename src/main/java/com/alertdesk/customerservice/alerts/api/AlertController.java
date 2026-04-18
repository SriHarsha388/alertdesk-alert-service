package com.alertdesk.customerservice.alerts.api;

import com.alertdesk.customerservice.alerts.api.dto.AlertCreateRequest;
import com.alertdesk.customerservice.alerts.api.dto.AlertDetailResponse;
import com.alertdesk.customerservice.alerts.api.dto.AlertFilters;
import com.alertdesk.customerservice.alerts.api.dto.AlertListResponse;
import com.alertdesk.customerservice.alerts.api.dto.AlertStatusUpdateRequest;
import com.alertdesk.customerservice.alerts.api.dto.AlertSummaryGroupsResponse;
import com.alertdesk.customerservice.alerts.service.AlertService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequestMapping("/api/alerts")
@RequiredArgsConstructor
public class AlertController {

    private final AlertService alertService;

    @GetMapping
    public AlertListResponse listAlerts(
            AlertFilters filters,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) int size
    ) {
        return alertService.listAlerts(
                filters,
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "triggeredAt"))
        );
    }

    @GetMapping("/{id}")
    public AlertDetailResponse getAlert(@PathVariable("id") String id) {
        return alertService.getAlert(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AlertDetailResponse createAlert(@Valid @RequestBody AlertCreateRequest request) {
        return alertService.createAlert(request);
    }

    @PatchMapping("/{id}/status")
    public AlertDetailResponse updateStatus(
            @PathVariable("id") String id,
            @Valid @RequestBody AlertStatusUpdateRequest request
    ) {
        return alertService.updateStatus(id, request);
    }

    @GetMapping("/summary")
    public AlertSummaryGroupsResponse getSummary(@RequestParam(defaultValue = "status") String groupBy) {
        return alertService.getSummary(groupBy);
    }
}
