package com.metrics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;

public class MetricsModule {

    private static final Logger logger = LoggerFactory.getLogger(MetricsModule.class);
    private static BufferedWriter metricsWriter;

    // Initialize metrics recording
    public static void initializeMetrics() {
        try {
            metricsWriter = new BufferedWriter(new FileWriter("metrics.log", true));  // Log metrics to a file
            metricsWriter.write("Timestamp,CPU_Usage(%),Memory_Usage(MB)\n");  // Write header
        } catch (IOException e) {
            logger.error("Failed to initialize metrics logging", e);
        }
    }

    // Collect CPU usage
    public static double getCPUUsage() {
        OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
        if (osBean instanceof com.sun.management.OperatingSystemMXBean) {
            return ((com.sun.management.OperatingSystemMXBean) osBean).getSystemCpuLoad() * 100;
        } else {
            return -1;  // CPU metric not supported
        }
    }

    // Collect memory usage
    public static double getMemoryUsage() {
        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
        MemoryUsage memoryUsage = memoryMXBean.getHeapMemoryUsage();
        return (double) memoryUsage.getUsed() / (1024 * 1024);  // Convert to MB
    }

    // Log metrics at defined intervals
    public static void logMetrics() {
        double cpuUsage = getCPUUsage();
        double memoryUsage = getMemoryUsage();
        long timestamp = System.currentTimeMillis();

        String logEntry = String.format("%d,%.2f,%.2f\n", timestamp, cpuUsage, memoryUsage);
        try {
            metricsWriter.write(logEntry);
            metricsWriter.flush();
        } catch (IOException e) {
            logger.error("Failed to log metrics", e);
        }
    }

    // Close the metrics writer
    public static void closeMetrics() {
        try {
            if (metricsWriter != null) {
                metricsWriter.close();
            }
        } catch (IOException e) {
            logger.error("Failed to close metrics logging", e);
        }
    }
}
