package com.alertdesk.alertservice.dto;

import com.alertdesk.alertservice.domain.AlertStatus;
import jakarta.validation.constraints.NotNull;

public record AlertStatusUpdateRequest(
        @NotNull
        AlertStatus status
) {
}
