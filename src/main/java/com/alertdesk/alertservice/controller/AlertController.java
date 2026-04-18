package com.alertdesk.alertservice.controller;

import com.alertdesk.alertservice.dto.AlertCreateRequest;
import com.alertdesk.alertservice.dto.AlertDetailResponse;
import com.alertdesk.alertservice.dto.AlertFilters;
import com.alertdesk.alertservice.dto.AlertListResponse;
import com.alertdesk.alertservice.dto.AlertStatusUpdateRequest;
import com.alertdesk.alertservice.dto.AlertSummaryGroupsResponse;
import com.alertdesk.alertservice.service.AlertService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
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
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size,
            @RequestParam(defaultValue = "triggeredAt")
            @Pattern(regexp = "triggeredAt|amount|status|riskBand", message = "sortBy must be one of triggeredAt, amount, status, riskBand")
            String sortBy,
            @RequestParam(defaultValue = "desc")
            @Pattern(regexp = "asc|desc", message = "sortDir must be one of asc, desc")
            String sortDir
    ) {
        return alertService.listAlerts(
                filters,
                PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(sortDir), sortBy))
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
