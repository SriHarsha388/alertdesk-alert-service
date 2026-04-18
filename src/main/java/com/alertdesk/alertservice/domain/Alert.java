package com.alertdesk.alertservice.domain;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "alerts")
public class Alert {

    @Id
    @Column(name = "alert_id", nullable = false, length = 32)
    private String alertId;

    @Column(name = "customer_id", nullable = false, length = 32)
    private String customerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "alert_type", nullable = false, length = 32)
    private AlertType alertType;

    @Enumerated(EnumType.STRING)
    @Column(name = "risk_band", nullable = false, length = 16)
    private RiskBand riskBand;

    @Column(name = "amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(name = "currency", nullable = false, length = 3)
    private String currency;

    @Column(name = "triggered_at", nullable = false)
    private Instant triggeredAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 32)
    private AlertStatus status;

    @Column(name = "assigned_analyst")
    private String assignedAnalyst;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "alert_flagged_rules", joinColumns = @JoinColumn(name = "alert_id"))
    @Column(name = "rule_name", nullable = false)
    @OrderColumn(name = "rule_order")
    private List<String> flaggedRules = new ArrayList<>();

}
