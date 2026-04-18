package com.alertdesk.customerservice.alerts.api.dto;

import com.alertdesk.customerservice.alerts.domain.AlertStatus;
import com.alertdesk.customerservice.alerts.domain.AlertType;
import com.alertdesk.customerservice.alerts.domain.RiskBand;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.Instant;

public record AlertFilters(
        String customerId,
        AlertType alertType,
        RiskBand riskBand,
        AlertStatus status,
        String assignedAnalyst,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        Instant triggeredFrom,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        Instant triggeredTo
) {
}
