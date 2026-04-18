package com.alertdesk.alertservice.repository;

import com.alertdesk.alertservice.dto.AlertFilters;
import com.alertdesk.alertservice.domain.Alert;
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

            if (filters.alertType() != null) {
                predicates.add(criteriaBuilder.equal(root.get("alertType"), filters.alertType()));
            }
            if (filters.riskBand() != null) {
                predicates.add(criteriaBuilder.equal(root.get("riskBand"), filters.riskBand()));
            }
            if (filters.status() != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), filters.status()));
            }
            if (filters.amountMin() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("amount"), filters.amountMin()));
            }
            if (filters.amountMax() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("amount"), filters.amountMax()));
            }

            return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
        };
    }
}
