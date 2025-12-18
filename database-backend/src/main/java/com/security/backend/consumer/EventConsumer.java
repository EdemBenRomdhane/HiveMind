package com.security.backend.consumer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.security.backend.model.SecurityEvent;
import com.security.backend.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

@Service
public class EventConsumer {

    @Autowired
    private EventService eventService;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());

    @KafkaListener(topics = "processed-events", groupId = "backend-event-group")
    public void consume(String message) {
        System.out.println("Received processed event: " + message);
        try {
            JsonNode node = objectMapper.readTree(message);
            SecurityEvent event = new SecurityEvent();

            event.setEventId(UUID.randomUUID());
            event.setDeviceId(node.has("deviceId") ? node.get("deviceId").asText() : "UNKNOWN");
            event.setEventType(node.has("eventType") ? node.get("eventType").asText() : "LOG");
            event.setSeverity(node.has("severity") ? node.get("severity").asText() : "INFO");

            // Map timestamp
            long ts = node.has("eventTimestamp") ? node.get("eventTimestamp").asLong() : System.currentTimeMillis();
            event.setTimestamp(LocalDateTime.ofInstant(Instant.ofEpochMilli(ts), ZoneId.systemDefault()));

            // Build metadata string for display
            StringBuilder metadata = new StringBuilder();
            if (node.has("temperature")) {
                metadata.append("Temperature: ").append(node.get("temperature").asDouble()).append("°C");
            }
            if (node.has("filename") && !"N/A".equals(node.get("filename").asText())) {
                if (metadata.length() > 0)
                    metadata.append(" | ");
                metadata.append("File: ").append(node.get("filename").asText());
            }
            if (node.has("changeType") && !"N/A".equals(node.get("changeType").asText())) {
                if (metadata.length() > 0)
                    metadata.append(" | ");
                metadata.append("Details: ").append(node.get("changeType").asText());
            }

            if (metadata.length() == 0) {
                metadata.append("System log received.");
            }

            event.setMetadata(metadata.toString());

            eventService.saveEvent(event);
            System.out.println("Successfully saved security event from " + event.getDeviceId());

        } catch (Exception e) {
            System.err.println("Error consuming event: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
