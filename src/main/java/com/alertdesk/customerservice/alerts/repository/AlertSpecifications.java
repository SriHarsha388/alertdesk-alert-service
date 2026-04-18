package com.alertdesk.customerservice.alerts.repository;

import com.alertdesk.customerservice.alerts.api.dto.AlertFilters;
import com.alertdesk.customerservice.alerts.domain.Alert;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public final class AlertSpecifications {

    private AlertSpecifications() {
    }

    public static Specification<Alert> withFilters(AlertFilters filters) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filters.customerId() != null && !filters.customerId().isBlank()) {
                predicates.add(criteriaBuilder.equal(root.get("customerId"), filters.customerId()));
            }
            if (filters.alertType() != null) {
                predicates.add(criteriaBuilder.equal(root.get("alertType"), filters.alertType()));
            }
            if (filters.riskBand() != null) {
                predicates.add(criteriaBuilder.equal(root.get("riskBand"), filters.riskBand()));
            }
            if (filters.status() != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), filters.status()));
            }
            if (filters.assignedAnalyst() != null && !filters.assignedAnalyst().isBlank()) {
                predicates.add(criteriaBuilder.equal(root.get("assignedAnalyst"), filters.assignedAnalyst()));
            }
            if (filters.triggeredFrom() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("triggeredAt"), filters.triggeredFrom()));
            }
            if (filters.triggeredTo() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("triggeredAt"), filters.triggeredTo()));
            }

            return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
        };
    }
}
