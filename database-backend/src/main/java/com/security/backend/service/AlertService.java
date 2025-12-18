package com.security.backend.service;

import com.security.backend.model.ThreatAlert;
import com.security.backend.repository.ThreatAlertRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AlertService {

    @Autowired
    private ThreatAlertRepository alertRepository;

    public ThreatAlert saveAlert(ThreatAlert alert) {
        return alertRepository.save(alert);
    }

    public List<ThreatAlert> getAllAlerts() {
        return alertRepository.findAll();
    }

    public Optional<ThreatAlert> getAlertById(UUID id) {
        return alertRepository.findById(id);
    }

    public List<ThreatAlert> getAlertsByDevice(String deviceId) {
        return alertRepository.findByDeviceId(deviceId);
    }

    public List<ThreatAlert> getAlertsBySeverity(String severity) {
        return alertRepository.findBySeverity(severity);
    }

    public List<ThreatAlert> getAlertsBySource(String source) {
        return alertRepository.findBySource(source);
    }

    public void deleteAlert(UUID id) {
        alertRepository.deleteById(id);
    }
}
