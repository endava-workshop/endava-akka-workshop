package metrics;

import com.codahale.metrics.JmxReporter;
import com.codahale.metrics.MetricRegistry;
import org.springframework.stereotype.Component;

/**
 * Created by ionut on 27.04.2014.
 */
@Component
public class MetricsConfig {

    private MetricRegistry metrics;

    public MetricsConfig() {
        metrics = new MetricRegistry();
        final JmxReporter reporter = JmxReporter.forRegistry(metrics).build();
        reporter.start();
    }

    public MetricRegistry getMetrics() {
        return metrics;
    }

}
