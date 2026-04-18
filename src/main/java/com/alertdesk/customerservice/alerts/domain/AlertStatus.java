package com.alertdesk.customerservice.alerts.domain;

public enum AlertStatus {
    NEW,
    UNDER_REVIEW,
    ESCALATED,
    CLOSED;

    public boolean canTransitionTo(AlertStatus target) {
        return switch (this) {
            case NEW -> target == UNDER_REVIEW;
            case UNDER_REVIEW -> target == ESCALATED;
            case ESCALATED -> target == CLOSED;
            case CLOSED -> false;
        };
    }
}
