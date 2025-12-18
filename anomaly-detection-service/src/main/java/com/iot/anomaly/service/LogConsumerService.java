package com.iot.anomaly.service;

import com.iot.anomaly.model.AnomalyAlert;
import com.iot.anomaly.model.DeviceLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class LogConsumerService {

    @Autowired
    private AnomalyDetectorService anomalyDetectorService;

    @Autowired
    private AnomalyProducerService anomalyProducerService;

    private final com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();

    @KafkaListener(topics = "processed-events", groupId = "anomaly-group")
    public void consume(String logMessage) {
        System.out.println("Received processed event: " + logMessage);

        try {
            com.fasterxml.jackson.databind.JsonNode logNode = objectMapper.readTree(logMessage);

            if (anomalyDetectorService.isAnomaly(logNode)) {
                AnomalyAlert alert = new AnomalyAlert();
                alert.setAlertId(UUID.randomUUID().toString());

                // Extract fields from JSON
                String deviceId = logNode.has("deviceId") ? logNode.get("deviceId").asText() : "UNKNOWN";
                String username = logNode.has("username") ? logNode.get("username").asText() : "";
                String changeType = logNode.has("changeType") ? logNode.get("changeType").asText() : "";

                alert.setDeviceId(deviceId);
                alert.setDescription(anomalyDetectorService.getAnomalyDescription(logNode));
                alert.setDetectedValue(1.0); // Flag value for non-numeric anomalies

                // If it's an Auth Event, mark as HIGH. Or let Detector decide.
                // Detector returns TRUE if anomaly. We need to set Severity on the Alert.
                alert.setSeverity("HIGH"); // If detector says yes, it's a threat.

                alert.setTimestamp(LocalDateTime.now());

                anomalyDetectorService.addAnomaly(alert);
                anomalyProducerService.sendAlert(alert);
            }
        } catch (Exception e) {
            System.err.println("Error processing log: " + e.getMessage());
        }
    }
}
