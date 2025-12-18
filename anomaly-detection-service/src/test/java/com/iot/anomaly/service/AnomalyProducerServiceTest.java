package com.iot.anomaly.service;

import com.iot.anomaly.model.AnomalyAlert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.kafka.core.KafkaTemplate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class AnomalyProducerServiceTest {

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    private AnomalyProducerService producerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSendAlert() {
        AnomalyAlert alert = new AnomalyAlert();
        alert.setDeviceId("IOT-001");
        alert.setSeverity("HIGH");

        producerService.sendAlert(alert);

        verify(kafkaTemplate, times(1)).send(eq("anomaly-alerts"), eq("IOT-001"), eq(alert));
    }
}
