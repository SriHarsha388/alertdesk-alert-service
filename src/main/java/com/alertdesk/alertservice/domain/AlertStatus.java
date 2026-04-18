package com.alertdesk.alertservice.domain;

public enum AlertStatus {
    NEW,
    UNDER_REVIEW,
    ESCALATED,
    CLOSED;

    public boolean canTransitionTo(AlertStatus target) {
        return switch (this) {
            case NEW -> target == UNDER_REVIEW;
            case UNDER_REVIEW -> target == ESCALATED || target == CLOSED;
            case CLOSED -> false;
            default -> throw new IllegalStateException("Unexpected value: " + this);
        };
    }
}

