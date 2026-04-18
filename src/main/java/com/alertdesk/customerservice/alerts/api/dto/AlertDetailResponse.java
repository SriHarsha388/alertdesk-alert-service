package com.alertdesk.customerservice.alerts.api.dto;

import com.alertdesk.customerservice.alerts.domain.AlertStatus;
import com.alertdesk.customerservice.alerts.domain.AlertType;
import com.alertdesk.customerservice.alerts.domain.RiskBand;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record AlertDetailResponse(
        String alertId,
        String customerId,
        AlertType alertType,
        RiskBand riskBand,
        BigDecimal amount,
        String currency,
        Instant triggeredAt,
        AlertStatus status,
        String assignedAnalyst,
        List<String> flaggedRules
) {
}
