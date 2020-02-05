package chat.squirrel.core;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

import de.mxro.metrics.jre.Metrics;
import delight.async.properties.PropertyNode;
import delight.async.properties.PropertyOperation;

public final class MetricsManager {
    private static MetricsManager instance = new MetricsManager();

    private final PropertyNode metrics;

    public MetricsManager() {
        metrics = Metrics.create();
    }

    public void saveToStream(OutputStream out, Charset charset) throws IOException {
        out.write(metrics.render().get().getBytes(charset));
    }

    public static MetricsManager getInstance() {
        return instance;
    }

    public static PropertyNode getMetrics() {
        return getInstance().metrics;
    }

    public static void record(PropertyOperation<?> op) {
        getMetrics().record(op);
    }

}
