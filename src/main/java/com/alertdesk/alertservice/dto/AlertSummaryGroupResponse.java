package com.alertdesk.alertservice.dto;

public record AlertSummaryGroupResponse(
        String group,
        long count
) {
}
