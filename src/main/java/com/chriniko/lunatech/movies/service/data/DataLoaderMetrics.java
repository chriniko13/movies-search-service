package com.chriniko.lunatech.movies.service.data;

import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.LongAdder;

@Component
public class DataLoaderMetrics {

    private LongAdder successfulLoads = new LongAdder();

    void addSuccessfulRun() {
        successfulLoads.increment();
    }

    public long successfulRuns() {
        return successfulLoads.sum();
    }
}
