package com.alertdesk.customerservice.shared.api;

public class BusinessRuleException extends RuntimeException {

    public BusinessRuleException(String message) {
        super(message);
    }
}
