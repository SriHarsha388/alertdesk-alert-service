package com.alertdesk.alertservice.mapper;

import com.alertdesk.alertservice.dto.AlertCreateRequest;
import com.alertdesk.alertservice.dto.AlertDetailResponse;
import com.alertdesk.alertservice.dto.AlertSummaryResponse;
import com.alertdesk.alertservice.domain.Alert;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class AlertMapper {

    public Alert toEntity(AlertCreateRequest request) {
        Alert alert = new Alert();
        alert.setAlertId(request.alertId());
        alert.setCustomerId(request.customerId());
        alert.setAlertType(request.alertType());
        alert.setRiskBand(request.riskBand());
        alert.setAmount(request.amount());
        alert.setCurrency(request.currency());
        alert.setTriggeredAt(request.triggeredAt());
        alert.setStatus(request.status());
        alert.setAssignedAnalyst(request.assignedAnalyst());
        alert.setFlaggedRules(new ArrayList<>(request.flaggedRules()));
        return alert;
    }

    public AlertSummaryResponse toSummary(Alert alert) {
        return new AlertSummaryResponse(
                alert.getAlertId(),
                alert.getCustomerId(),
                alert.getAlertType(),
                alert.getRiskBand(),
                alert.getAmount(),
                alert.getCurrency(),
                alert.getTriggeredAt(),
                alert.getStatus(),
                alert.getAssignedAnalyst()
        );
    }

    public AlertDetailResponse toDetail(Alert alert) {
        return new AlertDetailResponse(
                alert.getAlertId(),
                alert.getCustomerId(),
                alert.getAlertType(),
                alert.getRiskBand(),
                alert.getAmount(),
                alert.getCurrency(),
                alert.getTriggeredAt(),
                alert.getStatus(),
                alert.getAssignedAnalyst(),
                new ArrayList<>(alert.getFlaggedRules())
        );
    }
}
