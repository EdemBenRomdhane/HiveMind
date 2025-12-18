package com.security.backend.controller;

import com.security.backend.model.ThreatAlert;
import com.security.backend.service.AlertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/alerts")
@CrossOrigin(origins = "*")
public class AlertController {

    @Autowired
    private AlertService alertService;

    @PostMapping
    public ResponseEntity<ThreatAlert> createAlert(@RequestBody ThreatAlert alert) {
        ThreatAlert savedAlert = alertService.saveAlert(alert);
        return new ResponseEntity<>(savedAlert, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ThreatAlert>> getAllAlerts() {
        List<ThreatAlert> alerts = alertService.getAllAlerts();
        return new ResponseEntity<>(alerts, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ThreatAlert> getAlertById(@PathVariable UUID id) {
        return alertService.getAlertById(id)
                .map(alert -> new ResponseEntity<>(alert, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/device/{deviceId}")
    public ResponseEntity<List<ThreatAlert>> getAlertsByDevice(@PathVariable String deviceId) {
        List<ThreatAlert> alerts = alertService.getAlertsByDevice(deviceId);
        return new ResponseEntity<>(alerts, HttpStatus.OK);
    }

    @GetMapping("/severity/{severity}")
    public ResponseEntity<List<ThreatAlert>> getAlertsBySeverity(@PathVariable String severity) {
        List<ThreatAlert> alerts = alertService.getAlertsBySeverity(severity);
        return new ResponseEntity<>(alerts, HttpStatus.OK);
    }

    @GetMapping("/source/{source}")
    public ResponseEntity<List<ThreatAlert>> getAlertsBySource(@PathVariable String source) {
        List<ThreatAlert> alerts = alertService.getAlertsBySource(source);
        return new ResponseEntity<>(alerts, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAlert(@PathVariable UUID id) {
        alertService.deleteAlert(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
