package com.alertdesk.alertservice.dto;

import java.util.List;

public record AlertListResponse(
        List<AlertSummaryResponse> content,
        int page,
        int size,
        long totalElements,
        int totalPages
) {
}
