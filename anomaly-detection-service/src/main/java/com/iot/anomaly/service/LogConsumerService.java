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
            com.fasterxml.jackson.databind.JsonNode logNode;
            String trimmed = logMessage.trim();
            if (trimmed.startsWith("{") || trimmed.startsWith("[")) {
                logNode = objectMapper.readTree(trimmed);
            } else {
                // Wrap plain string in a JSON-like structure for the detector
                com.fasterxml.jackson.databind.node.ObjectNode node = objectMapper.createObjectNode();
                node.put("changeType", logMessage);
                node.put("deviceId", "RAW_LOG");
                node.put("severity", "UNKNOWN");
                logNode = node;
            }

            if (anomalyDetectorService.isAnomaly(logNode)) {
                AnomalyAlert alert = new AnomalyAlert();
                alert.setAlertId(UUID.randomUUID().toString());

                // Extract fields from JSON
                String deviceId = logNode.has("deviceId") ? logNode.get("deviceId").asText() : "UNKNOWN";
                String username = logNode.has("username") ? logNode.get("username").asText() : "";

                alert.setDeviceId(deviceId);
                alert.setDescription(anomalyDetectorService.getAnomalyDescription(logNode));
                alert.setDetectedValue(1.0);
                alert.setSeverity("HIGH");
                alert.setTimestamp(LocalDateTime.now());

                anomalyDetectorService.addAnomaly(alert);
                anomalyProducerService.sendAlert(alert);
            }
        } catch (Exception e) {
            System.err.println("Error processing log: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
