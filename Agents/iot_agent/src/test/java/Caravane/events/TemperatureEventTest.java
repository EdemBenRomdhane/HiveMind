package Caravane.events;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TemperatureEventTest {

    @Test
    void testConstructorAndGetters() {
        long now = System.currentTimeMillis();
        TemperatureEvent event = new TemperatureEvent(25.5, "CELSIUS", now);

        assertEquals(25.5, event.getValue());
        assertEquals("CELSIUS", event.getUnit());
        assertEquals(now, event.getTimestamp());
    }

    @Test
    void testToString() {
        TemperatureEvent event = new TemperatureEvent(25.5, "CELSIUS", 123456789L);
        assertEquals("TemperatureEvent{value=25.50, unit='CELSIUS', timestamp=123456789}", event.toString());
    }
}
