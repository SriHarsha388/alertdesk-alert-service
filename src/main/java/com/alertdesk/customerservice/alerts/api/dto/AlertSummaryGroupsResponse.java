package com.alertdesk.customerservice.alerts.api.dto;

import java.util.List;

public record AlertSummaryGroupsResponse(
        String groupBy,
        List<AlertSummaryGroupResponse> counts
) {
}
