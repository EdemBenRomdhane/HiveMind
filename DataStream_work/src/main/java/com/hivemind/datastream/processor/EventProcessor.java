package com.hivemind.datastream.processor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.flink.api.common.functions.MapFunction;

public class EventProcessor implements MapFunction<String, String> {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    // Regex for Syslog: "Timestamp Host Process: Message"
    // Example: ديسمبر 14 18:47:35 maya sddm-helper[4176]: [PAM] authenticate:
    // Authentication failure
    // Group 1: Timestamp (Everything before the host)
    // Group 2: Host (maya)
    // Group 3: Process (sddm-helper[4176] or systemd[1]) - characters before the
    // colon
    // Group 4: Message (Everything after the colon)
    private static final java.util.regex.Pattern SYSLOG_PATTERN = java.util.regex.Pattern
            .compile("^(\\S+\\s+\\d+\\s+\\d+:\\d+:\\d+)\\s+(\\S+)\\s+([^:]+):?\\s+(.*)$");

    @Override
    public String map(String value) throws Exception {
        ObjectNode processedEvent = objectMapper.createObjectNode();

        try {
            // STRATEGY 1: Try Parsing as JSON (Workstation Agent Events)
            JsonNode rawNode = objectMapper.readTree(value);

            // Extract fields (with defaults)
            String eventType = rawNode.has("eventType") ? rawNode.get("eventType").asText() : "UNKNOWN";
            String deviceId = rawNode.has("deviceId") ? rawNode.get("deviceId").asText() : "UNKNOWN";
            String severity = rawNode.has("severity") ? rawNode.get("severity").asText() : "UNKNOWN";
            String username = rawNode.has("username") ? rawNode.get("username").asText() : "N/A";
            String filename = rawNode.has("filename") ? rawNode.get("filename").asText() : "N/A";
            String changeType = rawNode.has("changeType") ? rawNode.get("changeType").asText() : "N/A";

            // Populate processed event
            processedEvent.put("eventType", eventType);
            processedEvent.put("deviceId", deviceId);
            processedEvent.put("severity", severity);
            processedEvent.put("username", username);
            processedEvent.put("filename", filename);
            processedEvent.put("changeType", changeType);

            // Keep original timestamp if present
            if (rawNode.has("timestamp")) {
                processedEvent.put("eventTimestamp", rawNode.get("timestamp").asLong());
            }

        } catch (Exception e) {
            // STRATEGY 2: Fallback to Syslog Regex (Raw Logs)
            java.util.regex.Matcher matcher = SYSLOG_PATTERN.matcher(value);
            if (matcher.find()) {
                String timestampStr = matcher.group(1); // e.g. "ديسمبر 14 18:47:35"
                String host = matcher.group(2); // e.g. "maya"
                String process = matcher.group(3); // e.g. "sddm-helper[4176]"
                String message = matcher.group(4); // e.g. "authentication failure..."

                // Auto-detect Severity - DISABLED (Handled by Anomaly Service)
                String severity = "UNKNOWN";
                String eventType = "SYS_LOG";
                // String lowerMsg = message.toLowerCase();

                // if (lowerMsg.contains("authentication failure") ||
                // lowerMsg.contains("fail") ||
                // lowerMsg.contains("error") ||
                // lowerMsg.contains("breach")) {
                // severity = "HIGH";
                // eventType = "AUTH_EVENT";
                // }

                // Map to Standard Schema
                processedEvent.put("eventType", eventType);
                processedEvent.put("deviceId", host); // Hostname as deviceId
                processedEvent.put("severity", severity);
                processedEvent.put("username", process); // Process as username/actor
                processedEvent.put("filename", "syslog");
                processedEvent.put("changeType", message); // Message as the "change" or description
                processedEvent.put("originalTimestamp", timestampStr); // Keep string version

            } else {
                // STRATEGY 3: Unknown Format (Wrap as Generic Log)
                processedEvent.put("eventType", "RAW_LOG");
                processedEvent.put("deviceId", "UNKNOWN_HOST");
                processedEvent.put("severity", "INFO");
                processedEvent.put("username", "N/A");
                processedEvent.put("filename", "N/A");
                processedEvent.put("changeType", value); // Store full content
            }
        }

        // Add Processing Timestamp (Always)
        processedEvent.put("processedTimestamp", System.currentTimeMillis());

        // Log HIGH severity for debugging console
        if ("HIGH".equals(processedEvent.get("severity").asText())) {
            System.out.println(
                    "⚠️ ALERT: High severity event detected! Device: " + processedEvent.get("deviceId").asText());
        }

        return objectMapper.writeValueAsString(processedEvent);
    }
}
