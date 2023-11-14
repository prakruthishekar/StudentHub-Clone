package com.cloudcomputing.assignment1.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class MetricServices {

    private final MeterRegistry meterRegistry;
    private final ConcurrentMap<String, Counter> counters = new ConcurrentHashMap<>();

    public MetricServices(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    public void incrementCounter(String mapping) {
        counters.computeIfAbsent(mapping, key -> Counter.builder(key)
                .description("Number of " + key + " requests")
                .register(meterRegistry))
                .increment();
    }

    public double getCount(String mapping) {
        return counters.computeIfAbsent(mapping, key -> Counter.builder(key)
                .description("Number of " + key + " requests")
                .register(meterRegistry))
                .count();
    }
}
