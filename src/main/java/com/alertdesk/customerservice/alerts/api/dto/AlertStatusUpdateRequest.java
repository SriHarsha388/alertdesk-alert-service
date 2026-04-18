package com.alertdesk.customerservice.alerts.api.dto;

import com.alertdesk.customerservice.alerts.domain.AlertStatus;
import jakarta.validation.constraints.NotNull;

public record AlertStatusUpdateRequest(
        @NotNull
        AlertStatus status
) {
}
