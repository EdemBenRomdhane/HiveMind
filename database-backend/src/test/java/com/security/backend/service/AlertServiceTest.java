package com.security.backend.service;

import com.security.backend.model.ThreatAlert;
import com.security.backend.repository.ThreatAlertRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;
import static org.junit.jupiter.api.Assertions.*;
import org.mockito.MockitoAnnotations;

class AlertServiceTest {

    @Mock
    private ThreatAlertRepository alertRepository;

    @InjectMocks
    private AlertService alertService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSaveAlert() {
        ThreatAlert alert = new ThreatAlert();
        alert.setAlertId(UUID.randomUUID());
        alert.setDeviceId("TEST-DEVICE");
        alert.setSeverity("HIGH");

        when(alertRepository.save(any(ThreatAlert.class))).thenReturn(alert);

        ThreatAlert savedAlert = alertService.saveAlert(alert);

        assertNotNull(savedAlert);
        assertEquals("TEST-DEVICE", savedAlert.getDeviceId());
        verify(alertRepository, times(1)).save(alert);
    }

    @Test
    void testGetAllAlerts() {
        List<ThreatAlert> alerts = Arrays.asList(new ThreatAlert(), new ThreatAlert());
        when(alertRepository.findAll()).thenReturn(alerts);

        List<ThreatAlert> result = alertService.getAllAlerts();

        assertEquals(2, result.size());
        verify(alertRepository, times(1)).findAll();
    }

    @Test
    void testGetAlertById() {
        UUID id = UUID.randomUUID();
        ThreatAlert alert = new ThreatAlert();
        alert.setAlertId(id);

        when(alertRepository.findById(id)).thenReturn(Optional.of(alert));

        Optional<ThreatAlert> result = alertService.getAlertById(id);

        assertTrue(result.isPresent());
        assertEquals(id, result.get().getAlertId());
    }

    @Test
    void testGetAlertsBySeverity() {
        when(alertRepository.findBySeverity("CRITICAL")).thenReturn(Arrays.asList(new ThreatAlert()));

        List<ThreatAlert> result = alertService.getAlertsBySeverity("CRITICAL");

        assertEquals(1, result.size());
        verify(alertRepository, times(1)).findBySeverity("CRITICAL");
    }

    @Test
    void testDeleteAlert() {
        UUID id = UUID.randomUUID();
        doNothing().when(alertRepository).deleteById(id);

        alertService.deleteAlert(id);

        verify(alertRepository, times(1)).deleteById(id);
    }
}
