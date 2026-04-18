package com.alertdesk.alertservice.dto;

import java.util.List;

public record AlertSummaryGroupsResponse(
        String groupBy,
        List<AlertSummaryGroupResponse> counts
) {
}
