package Caravane.events;

public class TemperatureEvent {
    private final double value;
    private final String unit;
    private final long timestamp;

    public TemperatureEvent(double value, String unit, long timestamp) {
        this.value = value;
        this.unit = unit;
        this.timestamp = timestamp;
    }

    public double getValue() {
        return value;
    }

    public String getUnit() {
        return unit;
    }

    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return String.format("TemperatureEvent{value=%.2f, unit='%s', timestamp=%d}", value, unit, timestamp);
    }
}
