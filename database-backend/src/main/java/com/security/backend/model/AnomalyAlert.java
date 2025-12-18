package com.security.backend.model;

import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;
import java.time.LocalDateTime;
import java.util.UUID;

@Table("anomaly_alerts")
public class AnomalyAlert {

    @PrimaryKey
    private UUID alertId;
    private String deviceId;
    private String description;
    private double detectedValue;
    private String severity;
    private LocalDateTime timestamp;

    public AnomalyAlert() {
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

    public double getDetectedValue() {
        return detectedValue;
    }

    public void setDetectedValue(double detectedValue) {
        this.detectedValue = detectedValue;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "AnomalyAlert{" +
                "alertId=" + alertId +
                ", deviceId='" + deviceId + '\'' +
                ", description='" + description + '\'' +
                ", detectedValue=" + detectedValue +
                ", severity='" + severity + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
