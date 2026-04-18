package com.alertdesk.alertservice.config;

import com.alertdesk.alertservice.domain.Alert;
import com.alertdesk.alertservice.domain.AlertStatus;
import com.alertdesk.alertservice.domain.AlertType;
import com.alertdesk.alertservice.domain.RiskBand;
import com.alertdesk.alertservice.repository.AlertRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seedAlerts(AlertRepository alertRepository) {
        return args -> {
            if (alertRepository.count() > 0) {
                return;
            }

            alertRepository.saveAll(List.of(
                    alert("ALT-00001", "CUST-1042", AlertType.STRUCTURING, RiskBand.HIGH, "9800.00", "GBP", "2024-01-15T09:12:00Z", AlertStatus.NEW, null, List.of("CASH_THRESHOLD", "MULTI_BRANCH")),
                    alert("ALT-00002", "CUST-2187", AlertType.APP_SCAM, RiskBand.HIGH, "24500.00", "GBP", "2024-01-18T14:33:00Z", AlertStatus.UNDER_REVIEW, "s.okafor", List.of("RAPID_DISPERSAL", "NEW_ACCOUNT", "VICTIM_REPORT")),
                    alert("ALT-00003", "CUST-3301", AlertType.UNUSUAL_ACTIVITY, RiskBand.MEDIUM, "4200.00", "GBP", "2024-01-22T11:05:00Z", AlertStatus.NEW, null, List.of("VELOCITY_BREACH")),
                    alert("ALT-00004", "CUST-4455", AlertType.SANCTIONS_HIT, RiskBand.HIGH, "78000.00", "GBP", "2024-01-25T08:47:00Z", AlertStatus.ESCALATED, "t.bergmann", List.of("OFAC_MATCH", "HMT_MATCH")),
                    alert("ALT-00005", "CUST-5509", AlertType.APP_SCAM, RiskBand.MEDIUM, "3750.00", "GBP", "2024-01-28T16:22:00Z", AlertStatus.NEW, null, List.of("RAPID_DISPERSAL")),
                    alert("ALT-00006", "CUST-6612", AlertType.ACCOUNT_TAKEOVER, RiskBand.HIGH, "15200.00", "GBP", "2024-02-01T07:58:00Z", AlertStatus.UNDER_REVIEW, "j.rahman", List.of("NEW_DEVICE", "UNUSUAL_LOCATION", "LARGE_TRANSFER")),
                    alert("ALT-00007", "CUST-7734", AlertType.UNUSUAL_ACTIVITY, RiskBand.LOW, "1100.00", "GBP", "2024-02-03T13:14:00Z", AlertStatus.CLOSED, "p.nwosu", List.of("VELOCITY_BREACH")),
                    alert("ALT-00008", "CUST-2187", AlertType.APP_SCAM, RiskBand.HIGH, "31000.00", "GBP", "2024-02-05T10:30:00Z", AlertStatus.UNDER_REVIEW, "s.okafor", List.of("RAPID_DISPERSAL", "CRYPTO_TRANSFER", "VICTIM_REPORT")),
                    alert("ALT-00009", "CUST-8821", AlertType.STRUCTURING, RiskBand.MEDIUM, "7400.00", "GBP", "2024-02-08T09:01:00Z", AlertStatus.NEW, null, List.of("CASH_THRESHOLD", "MULTI_BRANCH")),
                    alert("ALT-00010", "CUST-9043", AlertType.SANCTIONS_HIT, RiskBand.HIGH, "145000.00", "GBP", "2024-02-10T15:44:00Z", AlertStatus.ESCALATED, "t.bergmann", List.of("OFAC_MATCH", "HIGH_RISK_JURISDICTION")),
                    alert("ALT-00011", "CUST-1120", AlertType.UNUSUAL_ACTIVITY, RiskBand.LOW, "850.00", "GBP", "2024-02-12T12:00:00Z", AlertStatus.CLOSED, "p.nwosu", List.of("VELOCITY_BREACH")),
                    alert("ALT-00012", "CUST-3301", AlertType.ACCOUNT_TAKEOVER, RiskBand.MEDIUM, "6800.00", "GBP", "2024-02-14T08:25:00Z", AlertStatus.NEW, null, List.of("NEW_DEVICE", "UNUSUAL_LOCATION")),
                    alert("ALT-00013", "CUST-4455", AlertType.SANCTIONS_HIT, RiskBand.MEDIUM, "12000.00", "GBP", "2024-02-16T10:55:00Z", AlertStatus.UNDER_REVIEW, "t.bergmann", List.of("HMT_MATCH")),
                    alert("ALT-00014", "CUST-1234", AlertType.APP_SCAM, RiskBand.MEDIUM, "5100.00", "GBP", "2024-02-19T14:40:00Z", AlertStatus.CLOSED, "s.okafor", List.of("RAPID_DISPERSAL")),
                    alert("ALT-00015", "CUST-5678", AlertType.STRUCTURING, RiskBand.LOW, "1950.00", "GBP", "2024-02-21T09:30:00Z", AlertStatus.CLOSED, "p.nwosu", List.of("CASH_THRESHOLD")),
                    alert("ALT-00016", "CUST-6612", AlertType.UNUSUAL_ACTIVITY, RiskBand.MEDIUM, "3300.00", "GBP", "2024-02-24T11:18:00Z", AlertStatus.NEW, null, List.of("VELOCITY_BREACH")),
                    alert("ALT-00017", "CUST-7890", AlertType.APP_SCAM, RiskBand.HIGH, "47500.00", "GBP", "2024-02-27T08:05:00Z", AlertStatus.ESCALATED, "s.okafor", List.of("RAPID_DISPERSAL", "VICTIM_REPORT", "CRYPTO_TRANSFER")),
                    alert("ALT-00018", "CUST-2345", AlertType.STRUCTURING, RiskBand.MEDIUM, "8750.00", "GBP", "2024-03-01T10:20:00Z", AlertStatus.NEW, null, List.of("CASH_THRESHOLD", "MULTI_BRANCH")),
                    alert("ALT-00019", "CUST-9876", AlertType.ACCOUNT_TAKEOVER, RiskBand.LOW, "900.00", "GBP", "2024-03-04T13:45:00Z", AlertStatus.CLOSED, "p.nwosu", List.of("NEW_DEVICE")),
                    alert("ALT-00020", "CUST-1042", AlertType.STRUCTURING, RiskBand.HIGH, "9600.00", "GBP", "2024-03-07T09:55:00Z", AlertStatus.NEW, null, List.of("CASH_THRESHOLD", "MULTI_BRANCH", "REPEAT_OFFENDER"))
            ));
        };
    }

    private Alert alert(
            String alertId,
            String customerId,
            AlertType alertType,
            RiskBand riskBand,
            String amount,
            String currency,
            String triggeredAt,
            AlertStatus status,
            String assignedAnalyst,
            List<String> flaggedRules
    ) {
        Alert alert = new Alert();
        alert.setAlertId(alertId);
        alert.setCustomerId(customerId);
        alert.setAlertType(alertType);
        alert.setRiskBand(riskBand);
        alert.setAmount(new BigDecimal(amount));
        alert.setCurrency(currency);
        alert.setTriggeredAt(Instant.parse(triggeredAt));
        alert.setStatus(status);
        alert.setAssignedAnalyst(assignedAnalyst);
        alert.setFlaggedRules(flaggedRules);
        return alert;
    }
}
