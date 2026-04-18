package com.alertdesk.alertservice.repository;

import com.alertdesk.alertservice.domain.Alert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AlertRepository extends JpaRepository<Alert, String>, JpaSpecificationExecutor<Alert> {
    boolean existsByAlertId(String alertId);
}
