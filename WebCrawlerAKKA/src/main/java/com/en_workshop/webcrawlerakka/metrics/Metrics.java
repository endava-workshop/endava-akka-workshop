package com.en_workshop.webcrawlerakka.metrics;

import com.codahale.metrics.JmxReporter;
import com.codahale.metrics.MetricRegistry;

import java.util.concurrent.TimeUnit;

/**
 * Created by ionut on 27.04.2014.
 */
public class Metrics {

    private static class MetricsConfigHolder {
        private static final Metrics instance = new Metrics();
    }
    private MetricRegistry metrics;

    public Metrics() {
        metrics = new MetricRegistry();
        final JmxReporter reporter = JmxReporter.forRegistry(metrics).convertDurationsTo(TimeUnit.SECONDS).build();
        reporter.start();
    }

    public MetricRegistry getMetrics() {
        return metrics;
    }

    public static MetricRegistry getInstance() {
        return MetricsConfigHolder.instance.getMetrics();
    }

}
