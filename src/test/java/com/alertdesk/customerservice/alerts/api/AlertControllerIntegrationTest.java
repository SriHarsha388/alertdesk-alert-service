package com.alertdesk.customerservice.alerts.api;

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
    void shouldRejectInvalidStatusTransition() throws Exception {
        mockMvc.perform(patch("/api/alerts/ALT-00001/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "status": "CLOSED"
                                }
                                """))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").value("Invalid status transition from NEW to CLOSED"));
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
