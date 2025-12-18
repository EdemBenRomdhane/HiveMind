package com.security.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.security.backend.model.ThreatAlert;
import com.security.backend.service.AlertService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AlertController.class, properties = "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.data.cassandra.CassandraDataAutoConfiguration,org.springframework.boot.autoconfigure.cassandra.CassandraAutoConfiguration")
@ContextConfiguration(classes = caravane.HiveMind.HiveMindApplication.class)
@AutoConfigureMockMvc(addFilters = false) // Disable security filters for tests
class AlertControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AlertService alertService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCreateAlert() throws Exception {
        ThreatAlert alert = new ThreatAlert();
        alert.setDeviceId("TEST-CONTROLLER");
        alert.setSeverity("HIGH");

        when(alertService.saveAlert(any(ThreatAlert.class))).thenReturn(alert);

        mockMvc.perform(post("/api/alerts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(alert)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.deviceId").value("TEST-CONTROLLER"));
    }

    @Test
    void testGetAllAlerts() throws Exception {
        when(alertService.getAllAlerts()).thenReturn(Arrays.asList(new ThreatAlert()));

        mockMvc.perform(get("/api/alerts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void testGetAlertById() throws Exception {
        UUID id = UUID.randomUUID();
        ThreatAlert alert = new ThreatAlert();
        alert.setAlertId(id);

        when(alertService.getAlertById(id)).thenReturn(Optional.of(alert));

        mockMvc.perform(get("/api/alerts/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.alertId").exists());
    }

    @Test
    void testGetAlertsBySeverity() throws Exception {
        when(alertService.getAlertsBySeverity("HIGH")).thenReturn(Arrays.asList(new ThreatAlert()));

        mockMvc.perform(get("/api/alerts/severity/HIGH"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }
}
