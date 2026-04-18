package com.alertdesk.alertservice.dto;

import com.alertdesk.alertservice.domain.AlertStatus;
import com.alertdesk.alertservice.domain.AlertType;
import com.alertdesk.alertservice.domain.RiskBand;

import java.math.BigDecimal;

public record AlertFilters(
        AlertType alertType,
        RiskBand riskBand,
        AlertStatus status,
        BigDecimal amountMin,
        BigDecimal amountMax
) {
}
