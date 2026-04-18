package com.alertdesk.alertservice.alerts.service;

import com.alertdesk.alertservice.dto.AlertCreateRequest;
import com.alertdesk.alertservice.dto.AlertDetailResponse;
import com.alertdesk.alertservice.dto.AlertFilters;
import com.alertdesk.alertservice.dto.AlertListResponse;
import com.alertdesk.alertservice.dto.AlertStatusUpdateRequest;
import com.alertdesk.alertservice.dto.AlertSummaryGroupsResponse;
import com.alertdesk.alertservice.domain.Alert;
import com.alertdesk.alertservice.domain.AlertStatus;
import com.alertdesk.alertservice.domain.AlertType;
import com.alertdesk.alertservice.domain.RiskBand;
import com.alertdesk.alertservice.mapper.AlertMapper;
import com.alertdesk.alertservice.repository.AlertRepository;
import com.alertdesk.alertservice.exception.BusinessRuleException;
import com.alertdesk.alertservice.exception.ResourceNotFoundException;
import com.alertdesk.alertservice.service.AlertService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AlertServiceTest {

    @Mock
    private AlertRepository alertRepository;

    private AlertService alertService;

    @BeforeEach
    void setUp() {
        alertService = new AlertService(alertRepository, new AlertMapper());
    }

    @Test
    void shouldListAlerts() {
        Alert alert = alert("ALT-00001", AlertStatus.NEW, AlertType.APP_SCAM, RiskBand.HIGH);
        Pageable pageable = PageRequest.of(0, 10);

        when(alertRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(alert), pageable, 1));

        AlertListResponse response = alertService.listAlerts(
                new AlertFilters(null, null, null, null, null),
                pageable
        );

        assertThat(response.content()).hasSize(1);
        assertThat(response.content().get(0).alertId()).isEqualTo("ALT-00001");
        assertThat(response.totalElements()).isEqualTo(1);
        verify(alertRepository).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void shouldGetAlertById() {
        Alert alert = alert("ALT-00001", AlertStatus.NEW, AlertType.APP_SCAM, RiskBand.HIGH);
        when(alertRepository.findById("ALT-00001")).thenReturn(Optional.of(alert));

        AlertDetailResponse response = alertService.getAlert("ALT-00001");

        assertThat(response.alertId()).isEqualTo("ALT-00001");
        assertThat(response.status()).isEqualTo(AlertStatus.NEW);
    }

    @Test
    void shouldThrowWhenAlertDoesNotExist() {
        when(alertRepository.findById("ALT-404")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> alertService.getAlert("ALT-404"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Alert ALT-404 not found");
    }

    @Test
    void shouldCreateAlert() {
        AlertCreateRequest request = createRequest("ALT-99999");
        when(alertRepository.existsByAlertId("ALT-99999")).thenReturn(false);
        when(alertRepository.save(any(Alert.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AlertDetailResponse response = alertService.createAlert(request);

        assertThat(response.alertId()).isEqualTo("ALT-99999");
        assertThat(response.flaggedRules()).containsExactly("TEST_RULE");
        verify(alertRepository).save(any(Alert.class));
    }

    @Test
    void shouldRejectDuplicateAlertId() {
        AlertCreateRequest request = createRequest("ALT-99999");
        when(alertRepository.existsByAlertId("ALT-99999")).thenReturn(true);

        assertThatThrownBy(() -> alertService.createAlert(request))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessage("Alert with id ALT-99999 already exists");

        verify(alertRepository, never()).save(any(Alert.class));
    }

    @Test
    void shouldUpdateStatusWhenTransitionIsValid() {
        Alert alert = alert("ALT-00001", AlertStatus.NEW, AlertType.APP_SCAM, RiskBand.HIGH);
        when(alertRepository.findById("ALT-00001")).thenReturn(Optional.of(alert));

        AlertDetailResponse response = alertService.updateStatus(
                "ALT-00001",
                new AlertStatusUpdateRequest(AlertStatus.UNDER_REVIEW)
        );

        assertThat(response.status()).isEqualTo(AlertStatus.UNDER_REVIEW);
        assertThat(alert.getStatus()).isEqualTo(AlertStatus.UNDER_REVIEW);
    }

    @Test
    void shouldRejectInvalidStatusTransition() {
        Alert alert = alert("ALT-00001", AlertStatus.NEW, AlertType.APP_SCAM, RiskBand.HIGH);
        when(alertRepository.findById("ALT-00001")).thenReturn(Optional.of(alert));

        assertThatThrownBy(() -> alertService.updateStatus(
                "ALT-00001",
                new AlertStatusUpdateRequest(AlertStatus.CLOSED)
        ))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessage("Invalid status transition from NEW to CLOSED");
    }

    @Test
    void shouldRejectStatusUpdateWhenAlertDoesNotExist() {
        when(alertRepository.findById("ALT-404")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> alertService.updateStatus(
                "ALT-404",
                new AlertStatusUpdateRequest(AlertStatus.UNDER_REVIEW)
        ))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Alert ALT-404 not found");
    }

    @Test
    void shouldReturnSummaryGroupedByStatus() {
        when(alertRepository.findAll()).thenReturn(List.of(
                alert("ALT-00001", AlertStatus.NEW, AlertType.APP_SCAM, RiskBand.HIGH),
                alert("ALT-00002", AlertStatus.NEW, AlertType.STRUCTURING, RiskBand.MEDIUM),
                alert("ALT-00003", AlertStatus.CLOSED, AlertType.APP_SCAM, RiskBand.LOW)
        ));

        AlertSummaryGroupsResponse response = alertService.getSummary("status");

        assertThat(response.groupBy()).isEqualTo("status");
        assertThat(response.groups()).hasSize(2);
        assertThat(response.groups().get(0).label()).isEqualTo("CLOSED");
        assertThat(response.groups().get(0).count()).isEqualTo(1);
        assertThat(response.groups().get(1).label()).isEqualTo("NEW");
        assertThat(response.groups().get(1).count()).isEqualTo(2);

    }

    @Test
    void shouldRejectUnsupportedSummaryGrouping() {
        assertThatThrownBy(() -> alertService.getSummary("priority"))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessage("Unsupported groupBy value: priority");

        verify(alertRepository, never()).findAll();
    }

    private AlertCreateRequest createRequest(String alertId) {
        return new AlertCreateRequest(
                alertId,
                "CUST-0001",
                AlertType.APP_SCAM,
                RiskBand.HIGH,
                new BigDecimal("100.50"),
                "GBP",
                Instant.parse("2024-03-10T10:00:00Z"),
                AlertStatus.NEW,
                null,
                List.of("TEST_RULE")
        );
    }

    private Alert alert(String alertId, AlertStatus status, AlertType alertType, RiskBand riskBand) {
        Alert alert = new Alert();
        alert.setAlertId(alertId);
        alert.setCustomerId("CUST-0001");
        alert.setAlertType(alertType);
        alert.setRiskBand(riskBand);
        alert.setAmount(new BigDecimal("100.50"));
        alert.setCurrency("GBP");
        alert.setTriggeredAt(Instant.parse("2024-03-10T10:00:00Z"));
        alert.setStatus(status);
        alert.setAssignedAnalyst("s.okafor");
        alert.setFlaggedRules(List.of("TEST_RULE"));
        return alert;
    }
}
