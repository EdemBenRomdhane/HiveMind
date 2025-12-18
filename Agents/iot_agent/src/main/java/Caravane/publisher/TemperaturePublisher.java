package Caravane.publisher;

import Caravane.events.TemperatureEvent;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class TemperaturePublisher {

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    private final Random random = new Random();

    @PostConstruct
    public void init() {
        startReporting();
    }

    public void startReporting() {
        new Thread(() -> {
            while (true) {
                try {
                    // Generate random temperature between 20.0 and 30.0
                    double temp = 20.0 + (10.0 * random.nextDouble());
                    TemperatureEvent event = new TemperatureEvent(temp, "CELSIUS", System.currentTimeMillis());

                    System.out.println("🌡️ Internal Sensor Read: " + String.format("%.2f", temp) + "°C");
                    eventPublisher.publishEvent(event);

                    // Report every 5 seconds
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }).start();
    }
}
