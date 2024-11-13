package com.stresser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MemoryStresser {
    private static final Logger logger = LoggerFactory.getLogger(MemoryStresser.class);

    public void generateMemoryLoad(int arrays, int arraySizeInMB, int durationInSeconds, int memoryMaxUsage) {
        logger.info("Generating memory load with {} arrays of {} MB for {} seconds. Max usage: {} MB",
                arrays, arraySizeInMB, durationInSeconds, memoryMaxUsage == -1 ? "no limit" : memoryMaxUsage);

        long endTime = System.currentTimeMillis() + (durationInSeconds * 1000);

        // Create an array of arrays to consume memory
        for (int i = 0; i < arrays; i++) {
            byte[] memoryChunk = new byte[arraySizeInMB * 1024 * 1024];  // Allocate memory

            // Store the reference in a list or something so GC doesn't clean it up
            // You can have a check here to manage memory limits as needed

            try {
                Thread.sleep(500);  // Simulate delay while memory is being used
            } catch (InterruptedException e) {
                logger.error("Memory stress interrupted.", e);
            }

            if (memoryMaxUsage > 0) {
                long usedMemory = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1024 * 1024);
                if (usedMemory > memoryMaxUsage) {
                    logger.info("Memory usage has reached the limit. Exiting stress.");
                    break;
                }
            }
        }
    }
}
