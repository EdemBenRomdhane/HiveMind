package Caravane.subscriber;

import Caravane.events.TemperatureEvent;
import Caravane.service.KafkaProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaForwarderService {

    @Autowired
    private KafkaProducer kafkaProducer;

    @EventListener
    public void handleTemperatureReport(TemperatureEvent event) {
        // Construct JSON payload
        String json = String.format(
                "{\"eventType\":\"TEMPERATURE_REPORT\",\"deviceId\":\"IOT-AGENT-01\",\"severity\":\"INFO\",\"temperature\":%.2f,\"unit\":\"%s\",\"timestamp\":%d}",
                event.getValue(),
                event.getUnit(),
                event.getTimestamp());

        kafkaProducer.send("device-events-iot", json);
    }
}
