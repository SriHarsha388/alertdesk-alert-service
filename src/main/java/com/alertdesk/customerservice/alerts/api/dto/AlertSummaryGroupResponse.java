package com.alertdesk.customerservice.alerts.api.dto;

public record AlertSummaryGroupResponse(
        String group,
        long count
) {
}
