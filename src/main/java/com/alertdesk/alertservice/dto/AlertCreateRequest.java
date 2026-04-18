package com.alertdesk.alertservice.dto;

import com.alertdesk.alertservice.domain.AlertStatus;
import com.alertdesk.alertservice.domain.AlertType;
import com.alertdesk.alertservice.domain.RiskBand;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record AlertCreateRequest(
        @NotBlank
        @Pattern(regexp = "ALT-\\d{5}", message = "alertId must match ALT-00000 format")
        String alertId,
        @NotBlank
        String customerId,
        @NotNull
        AlertType alertType,
        @NotNull
        RiskBand riskBand,
        @NotNull
        @DecimalMin(value = "0.0", inclusive = false)
        BigDecimal amount,
        @NotBlank
        @Pattern(regexp = "^[A-Z]{3}$", message = "currency must be a 3-letter uppercase code")
        String currency,
        @NotNull
        Instant triggeredAt,
        @NotNull
        AlertStatus status,
        @Size(max = 100)
        String assignedAnalyst,
        @NotEmpty
        List<@NotBlank String> flaggedRules
) {
}
