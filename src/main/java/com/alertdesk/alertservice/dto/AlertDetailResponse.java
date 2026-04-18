package com.alertdesk.alertservice.dto;

import com.alertdesk.alertservice.domain.AlertStatus;
import com.alertdesk.alertservice.domain.AlertType;
import com.alertdesk.alertservice.domain.RiskBand;

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
