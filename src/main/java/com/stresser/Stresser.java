package com.stresser;

import com.metrics.MetricsModule;
import com.metrics.ReportGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Stresser {

    // Logger instance
    private static final Logger logger = LoggerFactory.getLogger(Stresser.class);
    private static ScheduledExecutorService metricsExecutor;

    public static void main(String[] args) {
        // Load configuration from YAML
        Map<String, Object> config = ConfigLoader.loadConfig("config.yaml");

        // Global duration for all stress tasks
        int durationInSeconds = (int) config.getOrDefault("duration", 60);  // Default 60 seconds if not present
        int metricsInterval = (int) config.getOrDefault("metrics_interval", 1); // Collect metrics every 5 seconds by default
        String reportPath = (String) config.getOrDefault("report_path", "stresser_report.txt");

        // Initialize metrics collection
        MetricsModule.initializeMetrics();

        // Set up periodic metrics collection
        metricsExecutor = Executors.newScheduledThreadPool(1);
        metricsExecutor.scheduleAtFixedRate(() -> {
            MetricsModule.logMetrics();
        }, 0, metricsInterval, TimeUnit.SECONDS);


        // CPU Stress Configuration
        Map<String, Object> cpuConfig = (Map<String, Object>) config.get("cpu");
        boolean isCpuEnabled = (boolean) cpuConfig.getOrDefault("enabled", true);  // Default to true
        int cpuThreads = (int) cpuConfig.getOrDefault("threads", 4);  // Default to 4 threads

        // Memory Stress Configuration
        Map<String, Object> memoryConfig = (Map<String, Object>) config.get("memory");
        boolean isMemoryEnabled = (boolean) memoryConfig.getOrDefault("enabled", true);
        int memoryArrays = (int) memoryConfig.getOrDefault("arrays", 2);
        int arraySizeInMB = (int) memoryConfig.getOrDefault("array_size_mb", 512);

        // Disk Stress Configuration
        Map<String, Object> diskConfig = (Map<String, Object>) config.get("disk");
        boolean isDiskEnabled = (boolean) diskConfig.getOrDefault("enabled", true);
        int diskFileCount = (int) diskConfig.getOrDefault("file_count", 10);
        int diskFileSizeMB = (int) diskConfig.getOrDefault("file_size_mb", 100);

        // Network Stress Configuration
        Map<String, Object> networkConfig = (Map<String, Object>) config.get("network");
        boolean isNetworkEnabled = (boolean) networkConfig.getOrDefault("enabled", true);
        int networkDuration = (int) networkConfig.getOrDefault("duration", 60);
        String downloadUrl = (String) networkConfig.getOrDefault("download_url", "http://example.com");

        // File Descriptor Stress Configuration
        Map<String, Object> fdConfig = (Map<String, Object>) config.get("file_descriptors");
        boolean isFdEnabled = (boolean) fdConfig.getOrDefault("enabled", true);
        int fileDescriptorOpenFiles = (int) fdConfig.getOrDefault("open_files", 100);

        // Thread Stress Configuration
        Map<String, Object> threadConfig = (Map<String, Object>) config.get("threads");
        boolean isThreadEnabled = (boolean) threadConfig.getOrDefault("enabled", true);
        int threadCount = (int) threadConfig.getOrDefault("count", 4);

        // Behavior Control (optional)
        Map<String, Object> behaviorConfig = (Map<String, Object>) config.get("behavior");
        int rampUpTime = behaviorConfig != null ? (int) behaviorConfig.getOrDefault("ramp_up_time", 10) : 10;  // Default to 10 seconds
        int rampDownTime = behaviorConfig != null ? (int) behaviorConfig.getOrDefault("ramp_down_time", 10) : 10;  // Default to 10 seconds
        boolean burstMode = behaviorConfig != null && (boolean) behaviorConfig.getOrDefault("burst_mode", false);  // Default to false
        int burstDuration = behaviorConfig != null ? (int) behaviorConfig.getOrDefault("burst_duration_seconds", 10) : 10;  // Default to 10 seconds
        int burstPause = behaviorConfig != null ? (int) behaviorConfig.getOrDefault("burst_pause_seconds", 5) : 5;  // Default to 5 seconds

        // Logging Configuration (optional)
        Map<String, Object> loggingConfig = (Map<String, Object>) config.get("logging");
        boolean isLoggingEnabled = loggingConfig != null && (boolean) loggingConfig.getOrDefault("enabled", false);
        if (isLoggingEnabled) {
            String logFile = (String) loggingConfig.getOrDefault("log_file", "stresser.log");
            String logLevel = (String) loggingConfig.getOrDefault("log_level", "INFO");
            logger.info("Logging enabled. Output to file: {}", logFile);
        }

        // Resource Limits Configuration (optional)
        Map<String, Object> limitsConfig = (Map<String, Object>) config.get("limits");
        int cpuMaxUsage = (limitsConfig != null && limitsConfig.containsKey("cpu_max_usage_pct"))
                ? (int) limitsConfig.get("cpu_max_usage_pct")
                : -1;  // No limit if not specified
        int memoryMaxUsage = (limitsConfig != null && limitsConfig.containsKey("memory_max_usage_mb"))
                ? (int) limitsConfig.get("memory_max_usage_mb")
                : -1;  // No limit if not specified
        int diskMaxIO = (limitsConfig != null && limitsConfig.containsKey("disk_max_io_speed_mbps"))
                ? (int) limitsConfig.get("disk_max_io_speed_mbps")
                : -1;  // No limit if not specified
        int networkMaxBandwidth = (limitsConfig != null && limitsConfig.containsKey("network_max_bandwidth_mbps"))
                ? (int) limitsConfig.get("network_max_bandwidth_mbps")
                : -1;  // No limit if not specified

        // Create instances of the stress generators
        CpuStresser cpuStresser = new CpuStresser();
        MemoryStresser memoryStresser = new MemoryStresser();
        DiskStresser diskStresser = new DiskStresser();
        NetworkStresser networkStresser = new NetworkStresser();
        FileDescriptorStresser fileDescriptorStresser = new FileDescriptorStresser();
        ThreadStresser threadStresser = new ThreadStresser();

        // Conditionally run each stressor based on configuration
        if (isCpuEnabled) {
            logger.info("Starting CPU stress.");
            cpuStresser.generateCPULoad(cpuThreads, durationInSeconds, cpuMaxUsage);
        }

        if (isMemoryEnabled) {
            logger.info("Starting memory stress.");
            memoryStresser.generateMemoryLoad(memoryArrays, arraySizeInMB, durationInSeconds, memoryMaxUsage);
        }

        if (isDiskEnabled) {
            logger.info("Starting disk stress.");
            diskStresser.generateDiskLoad(diskFileCount, diskFileSizeMB, durationInSeconds, diskMaxIO);
        }

        if (isNetworkEnabled) {
            logger.info("Starting network stress.");
            networkStresser.generateNetworkLoad(downloadUrl, networkDuration, networkMaxBandwidth);
        }

        if (isFdEnabled) {
            logger.info("Starting file descriptor stress.");
            fileDescriptorStresser.generateFileDescriptorLoad(fileDescriptorOpenFiles, durationInSeconds);
        }

        if (isThreadEnabled) {
            logger.info("Starting thread stress.");
            threadStresser.generateThreadLoad(threadCount, durationInSeconds);
        }
// Wait for stress test to complete
        try {
            Thread.sleep(durationInSeconds * 1000);
        } catch (InterruptedException e) {
            logger.error("Stresser was interrupted", e);
        }

        // Shut down the metrics collection after the stress test duration
        logger.info("Stress test completed. Shutting down metrics collection.");
        metricsExecutor.shutdown();
        try {
            if (!metricsExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                logger.warn("Metrics executor did not terminate in the expected time.");
            }
        } catch (InterruptedException e) {
            logger.error("Error during metrics executor shutdown", e);
            Thread.currentThread().interrupt();
        }

        MetricsModule.closeMetrics();

        // Generate report
        ReportGenerator reportGenerator = new ReportGenerator(reportPath);
        reportGenerator.generateReport("System Stress Test", durationInSeconds, -1, -1);

        logger.info("Stress test report generated at: {}", reportPath);
    }

}
