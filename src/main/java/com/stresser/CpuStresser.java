package com.stresser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CpuStresser {
    private static final Logger logger = LoggerFactory.getLogger(CpuStresser.class);

    public void generateCPULoad(int threads, int durationInSeconds, int cpuMaxUsage) {
        logger.info("Generating CPU load for {} threads with max usage {}% for {} seconds.",
                threads, cpuMaxUsage == -1 ? "no limit" : cpuMaxUsage, durationInSeconds);

        long endTime = System.currentTimeMillis() + (durationInSeconds * 1000);

        for (int i = 0; i < threads; i++) {
            new Thread(() -> {
                while (System.currentTimeMillis() < endTime) {
                    // Simulate CPU load
                    if (cpuMaxUsage > 0) {
                        // Implement CPU throttling to ensure the usage does not exceed the specified limit
                        long startTime = System.nanoTime();
                        while (System.nanoTime() - startTime < 1000000) {
                            // Busy loop to simulate high CPU usage
                        }
                        // Sleep to limit the load
                        try {
                            Thread.sleep(100 - cpuMaxUsage); // Adjust the sleep duration based on max CPU usage
                        } catch (InterruptedException e) {
                            logger.error("Thread interrupted.", e);
                        }
                    } else {
                        // No limit: fully utilize CPU
                        while (System.currentTimeMillis() < endTime) {
                            Math.sin(Math.random());  // Simple operation for CPU load
                        }
                    }
                }
            }).start();
        }
    }
}
