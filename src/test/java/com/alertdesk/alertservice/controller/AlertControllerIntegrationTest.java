package com.alertdesk.alertservice.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AlertControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturnSeededAlertsWithPagination() throws Exception {
        mockMvc.perform(get("/api/alerts").param("page", "0").param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(5))
                .andExpect(jsonPath("$.totalElements").value(20));
    }

    @Test
    void shouldFilterAndSortAlertsUsingNewQueryParameters() throws Exception {
        mockMvc.perform(get("/api/alerts")
                        .param("status", "UNDER_REVIEW")
                        .param("alertType", "APP_SCAM")
                        .param("riskBand", "HIGH")
                        .param("amountMin", "20000")
                        .param("amountMax", "40000")
                        .param("sortBy", "amount")
                        .param("sortDir", "asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].alertId").value("ALT-00002"))
                .andExpect(jsonPath("$.content[1].alertId").value("ALT-00008"));
    }

    @Test
    void shouldRejectPageSizeGreaterThanMaximum() throws Exception {
        mockMvc.perform(get("/api/alerts").param("size", "101"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation failed"))
                .andExpect(jsonPath("$.path").value("/api/alerts"));
    }

    @Test
    void shouldRejectInvalidStatusTransition() throws Exception {
        mockMvc.perform(patch("/api/alerts/ALT-00001/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "status": "CLOSED"
                                }
                                """))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.error").value("Invalid status transition from NEW to CLOSED"))
                .andExpect(jsonPath("$.path").value("/api/alerts/ALT-00001/status"))
                .andExpect(jsonPath("$.violations.length()").value(0));
    }

    @Test
    void shouldReturnValidationErrorsWithViolations() throws Exception {
        mockMvc.perform(post("/api/alerts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "alertId": "bad-id",
                                  "customerId": "",
                                  "alertType": "APP_SCAM",
                                  "riskBand": "HIGH",
                                  "amount": 0,
                                  "currency": "gbp",
                                  "triggeredAt": "2024-03-10T10:00:00Z",
                                  "status": "NEW",
                                  "assignedAnalyst": null,
                                  "flaggedRules": [""]
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation failed"))
                .andExpect(jsonPath("$.path").value("/api/alerts"))
                .andExpect(jsonPath("$.violations.length()").value(5));
    }

    @Test
    void shouldCreateAlert() throws Exception {
        mockMvc.perform(post("/api/alerts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "alertId": "ALT-99999",
                                  "customerId": "CUST-0001",
                                  "alertType": "APP_SCAM",
                                  "riskBand": "HIGH",
                                  "amount": 100.50,
                                  "currency": "GBP",
                                  "triggeredAt": "2024-03-10T10:00:00Z",
                                  "status": "NEW",
                                  "assignedAnalyst": null,
                                  "flaggedRules": ["TEST_RULE"]
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.alertId").value("ALT-99999"))
                .andExpect(jsonPath("$.flaggedRules[0]").value("TEST_RULE"));
    }
}
