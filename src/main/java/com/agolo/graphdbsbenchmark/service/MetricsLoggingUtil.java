package com.agolo.graphdbsbenchmark.service;

import org.slf4j.Logger;

import java.util.List;

public class MetricsLoggingUtil {

    public static void logGraphCreationMetrics(Logger logger, long NUM_RECORDS, long startTime, List<Long> durations) {
        logger.info("Finished creating {} records in {} seconds", NUM_RECORDS, (System.currentTimeMillis() - startTime)/1000);
        System.out.println("Finished creating " + NUM_RECORDS + " records in " + (System.currentTimeMillis() - startTime)/1000 + " seconds");
        logger.info("Average time per 100 records: {} seconds", durations.stream().mapToLong(Long::longValue).average().getAsDouble());
        System.out.println("Average time per 100 records: " + durations.stream().mapToLong(Long::longValue).average().getAsDouble() + " seconds");
    }

    public static void logSampleMetrics(Logger logger, int i, long startTime, List<Long> durations) {
        long elapsedTimeInSeconds = (System.currentTimeMillis() - startTime)/1000;
        logger.info("Created {} records in {} seconds",
                i,
                elapsedTimeInSeconds);
        System.out.println("Created " + i + " records in " + elapsedTimeInSeconds + " seconds");
        durations.add(elapsedTimeInSeconds);

    }
}
