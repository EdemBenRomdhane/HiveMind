package com.security.backend.consumer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.security.backend.model.ThreatAlert;
import com.security.backend.repository.ThreatAlertRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AlertConsumer {

    @Autowired
    private ThreatAlertRepository threatAlertRepository;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());

    @KafkaListener(topics = { "ai-alerts", "anomaly-alerts" }, groupId = "backend-alert-group")
    public void consume(String message, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        System.out.println("Received message from topic [" + topic + "]: " + message);

        try {
            JsonNode node = objectMapper.readTree(message);
            ThreatAlert alert = new ThreatAlert();

            // Common fields mapping
            if (node.has("alertId"))
                alert.setAlertId(UUID.fromString(node.get("alertId").asText()));
            if (node.has("deviceId"))
                alert.setDeviceId(node.get("deviceId").asText());
            if (node.has("description"))
                alert.setDescription(node.get("description").asText());
            if (node.has("severity"))
                alert.setSeverity(node.get("severity").asText());

            // Handle timestamp (it might be an array or string)
            if (node.has("timestamp")) {
                JsonNode tsNode = node.get("timestamp");
                if (tsNode.isArray()) {
                    alert.setTimestamp(LocalDateTime.of(
                            tsNode.get(0).asInt(), tsNode.get(1).asInt(), tsNode.get(2).asInt(),
                            tsNode.get(3).asInt(), tsNode.get(4).asInt(), tsNode.get(5).asInt(),
                            tsNode.has(6) ? tsNode.get(6).asInt() : 0));
                } else {
                    alert.setTimestamp(LocalDateTime.now());
                }
            }

            if ("ai-alerts".equals(topic)) {
                alert.setSource("AI_SERVICE");
                alert.setConfidence(node.has("confidence") ? node.get("confidence").asDouble() : 0.0);
            } else if ("anomaly-alerts".equals(topic)) {
                alert.setSource("ANOMALY_SERVICE");
                alert.setConfidence(1.0);
            }

            threatAlertRepository.save(alert);
            System.out.println(
                    "Successfully saved consolidated alert from " + alert.getSource() + " to 'threat_alerts' table.");

        } catch (Exception e) {
            System.err.println("Error consuming alert message from topic " + topic + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
}
