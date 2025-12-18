package com.security.backend.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.security.backend.model.AIAlert;
import com.security.backend.model.AnomalyAlert;
import com.security.backend.repository.AIAlertRepository;
import com.security.backend.repository.AnomalyAlertRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.kafka.support.KafkaHeaders;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AlertConsumerTest {

    @Mock
    private AIAlertRepository aiAlertRepository;

    @Mock
    private AnomalyAlertRepository anomalyAlertRepository;

    @InjectMocks
    private AlertConsumer alertConsumer;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testConsumeAIAlert() throws Exception {
        AIAlert aiAlert = new AIAlert();
        aiAlert.setDeviceId("TEST-AI");
        aiAlert.setSeverity("HIGH");
        String message = objectMapper.writeValueAsString(aiAlert);

        alertConsumer.consume(message, "ai-alerts");

        verify(aiAlertRepository, times(1)).save(any(AIAlert.class));
        verify(anomalyAlertRepository, never()).save(any());
    }

    @Test
    void testConsumeAnomalyAlert() throws Exception {
        AnomalyAlert anomalyAlert = new AnomalyAlert();
        anomalyAlert.setDeviceId("TEST-ANOMALY");
        anomalyAlert.setSeverity("MEDIUM");
        String message = objectMapper.writeValueAsString(anomalyAlert);

        alertConsumer.consume(message, "anomaly-alerts");

        verify(anomalyAlertRepository, times(1)).save(any(AnomalyAlert.class));
        verify(aiAlertRepository, never()).save(any());
    }

    @Test
    void testConsumeInvalidMessage() {
        alertConsumer.consume("invalid json", "ai-alerts");
        verify(aiAlertRepository, never()).save(any());
    }
}
