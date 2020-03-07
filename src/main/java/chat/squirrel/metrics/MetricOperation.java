package chat.squirrel.metrics;

public interface MetricOperation {
    static MetricOperation happened(String name) {
        return null;
    }

    static MetricOperation value(String name, double value) {
        return null;
    }
}
