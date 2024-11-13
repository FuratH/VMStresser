package com.stresser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ThreadStresser {
    private static final Logger logger = LoggerFactory.getLogger(ThreadStresser.class);

    public void generateThreadLoad(int threadCount, int durationInSeconds) {
        logger.info("Starting thread stress with {} threads for {} seconds.", threadCount, durationInSeconds);

        long endTime = System.currentTimeMillis() + (durationInSeconds * 1000);

        for (int i = 0; i < threadCount; i++) {
            new Thread(() -> {
                while (System.currentTimeMillis() < endTime) {
                    // Simulate thread load
                    Math.sin(Math.random());
                }
            }).start();
        }
    }
}
