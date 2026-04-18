package com.alertdesk.customerservice.alerts.api.dto;

import java.util.List;

public record AlertListResponse(
        List<AlertSummaryResponse> content,
        int page,
        int size,
        long totalElements,
        int totalPages
) {
}
