package com.security.backend.model;

import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;
import java.time.LocalDateTime;
import java.util.UUID;

@Table("threat_alerts")
public class ThreatAlert {

    @PrimaryKey
    private UUID alertId;
    private String deviceId;
    private String description;
    private String severity;
    private String source; // "AI_SERVICE" or "ANOMALY_SERVICE"
    private Double confidence;
    private LocalDateTime timestamp;

    public ThreatAlert() {
        this.alertId = UUID.randomUUID();
        this.timestamp = LocalDateTime.now();
    }

    // Getters and Setters
    public UUID getAlertId() {
        return alertId;
    }

    public void setAlertId(UUID alertId) {
        this.alertId = alertId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Double getConfidence() {
        return confidence;
    }

    public void setConfidence(Double confidence) {
        this.confidence = confidence;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "ThreatAlert{" +
                "alertId=" + alertId +
                ", deviceId='" + deviceId + '\'' +
                ", description='" + description + '\'' +
                ", severity='" + severity + '\'' +
                ", source='" + source + '\'' +
                ", confidence=" + confidence +
                ", timestamp=" + timestamp +
                '}';
    }
}
