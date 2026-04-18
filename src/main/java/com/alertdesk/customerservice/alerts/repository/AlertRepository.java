package com.alertdesk.customerservice.alerts.repository;

import com.alertdesk.customerservice.alerts.domain.Alert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AlertRepository extends JpaRepository<Alert, String>, JpaSpecificationExecutor<Alert> {
    boolean existsByAlertId(String alertId);
}
