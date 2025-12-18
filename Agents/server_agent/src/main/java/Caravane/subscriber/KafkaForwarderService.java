package Caravane.subscriber;

import Caravane.events.FileChangedEvent;
import Caravane.service.KafkaProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaForwarderService {

    @Autowired
    private KafkaProducer kp;

    @EventListener
    public void handlefilechanged(FileChangedEvent event) {
        String json = String.format(
                "{\"eventType\":\"FILE_CHANGED\",\"deviceId\":\"SERVER-AGENT\",\"severity\":\"LOW\",\"filename\":\"%s\",\"changeType\":\"%s\",\"timestamp\":%d}",
                event.getFilename(),
                event.getChngetype(),
                System.currentTimeMillis());
        kp.send("device-events-server", json);
    }
}
