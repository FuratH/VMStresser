package com.stresser;

import com.metrics.MetricsModule;
import com.metrics.ReportGenerator;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Stresser {

    private static final Logger logger = LoggerFactory.getLogger(Stresser.class);
    private static ScheduledExecutorService metricsExecutor;

    // Command-line options
    @Option(name = "--help", help = true, usage = "Displays this help message.")
    private boolean help;

    @Option(name = "--list-stressors", usage = "Lists all available stressors.")
    private boolean listStressors;

    // Stressor activation options
    @Option(name = "--cpu", usage = "Enable CPU stressor.")
    private boolean cpu = false;

    @Option(name = "--memory", usage = "Enable memory stressor.")
    private boolean memory = false;

    @Option(name = "--disk", usage = "Enable disk stressor.")
    private boolean disk = false;

    @Option(name = "--network", usage = "Enable network stressor.")
    private boolean network = false;

    @Option(name = "--file-descriptors", usage = "Enable file descriptor stressor.")
    private boolean fileDescriptors = false;

    @Option(name = "--threads", usage = "Enable thread stressor.")
    private boolean threads = false;

    // Configuration overrides
    @Option(name = "--duration", usage = "Duration of the stress test in minutes (default: from config).")
    private int duration = -1;
    
    @Option(name = "--wait-time", usage = "Time to wait before starting the stress test (in minutes). Default is 0.")
    private int waitTime = 0;  // Default wait time is 0 minutes


    @Option(name = "--cpu-threads", usage = "Number of threads for CPU stress (default: from config).")
    private int cpuThreads = -1;

    @Option(name = "--memory-arrays", usage = "Number of arrays for memory stress (default: from config).")
    private int memoryArrays = -1;

    @Option(name = "--array-size", usage = "Size of each array in MB for memory stress (default: from config).")
    private int arraySize = -1;

    @Option(name = "--disk-file-count", usage = "Number of files for disk stress (default: from config).")
    private int diskFileCount = -1;

    @Option(name = "--disk-file-size", usage = "Size of each file in MB for disk stress (default: from config).")
    private int diskFileSize = -1;

    @Option(name = "--network-url", usage = "URL for network stress download (default: from config).")
    private String networkUrl;

    // Limit configuration


    @Option(name = "--limit-cpu", usage = "Set maximum CPU usage percentage (default: from config).")
    private int limitCpu = -1;

    @Option(name = "--limit-memory", usage = "Set maximum memory usage in MB (default: from config).")
    private int limitMemory = -1;

    @Option(name = "--limit-disk-io", usage = "Set maximum disk I/O speed in MB/s (default: from config).")
    private int limitDiskIo = -1;

    @Option(name = "--limit-network", usage = "Set maximum network bandwidth in Mbps (default: from config).")
    private int limitNetwork = -1;

    public static void main(String[] args) {
        new Stresser().run(args);
    }

    private void run(String[] args) {
        CmdLineParser parser = new CmdLineParser(this);
        try {
            parser.parseArgument(args);

            if (help) {
                System.out.println("Usage: java -jar Stresser.jar [options]");
                parser.printUsage(System.out);
                return;
            }

            if (listStressors) {
                System.out.println("Available stressors:");
                System.out.println("- CPU");
                System.out.println("- Memory");
                System.out.println("- Disk");
                System.out.println("- Network");
                System.out.println("- File Descriptors");
                System.out.println("- Threads");
                return;
            }

            // Load configuration from YAML
            Map<String, Object> config = ConfigLoader.loadConfig("config.yaml");

            // Load the wait time either from the command line or the config file (if set)
            int waitTimeFromConfig = (int) config.getOrDefault("wait_time", 0);  // Default wait time is 0 minutes
            int waitTimeInMinutes = waitTime > 0 ? waitTime : waitTimeFromConfig;  // Command line argument takes precedence

            // Wait before starting the stress test (convert minutes to milliseconds)
            if (waitTimeInMinutes > 0) {
                logger.info("Waiting for {} minute(s) before starting the stress test...", waitTimeInMinutes);
                try {
                    Thread.sleep(waitTimeInMinutes * 60 * 1000);  // Convert minutes to milliseconds
                } catch (InterruptedException e) {
                    logger.error("Waiting period was interrupted", e);
                    Thread.currentThread().interrupt();
                }
            }

            // Resolve configuration values (CLI takes precedence)

            int durationInMinutes = duration > 0 ? duration : (int) config.getOrDefault("duration", 1);
            int durationInSeconds = durationInMinutes * 60;

            String reportPath = (String) config.getOrDefault("report_path", "stresser_report.txt");

            int cpuThreadsConfig = cpuThreads > 0 ? cpuThreads : (int) config.getOrDefault("cpu.threads", 4);
            int memoryArraysConfig = memoryArrays > 0 ? memoryArrays : (int) config.getOrDefault("memory.arrays", 2);
            int arraySizeConfig = arraySize > 0 ? arraySize : (int) config.getOrDefault("memory.array_size_mb", 512);
            int diskFileCountConfig = diskFileCount > 0 ? diskFileCount : (int) config.getOrDefault("disk.file_count", 10);
            int diskFileSizeConfig = diskFileSize > 0 ? diskFileSize : (int) config.getOrDefault("disk.file_size_mb", 100);
            String networkUrlConfig = networkUrl != null ? networkUrl : (String) config.getOrDefault("network.download_url", "http://example.com");

            // Resolve limits
            int cpuMaxUsage = limitCpu > 0 ? limitCpu : (int) config.getOrDefault("limits.cpu_max_usage_pct", -1);
            int memoryMaxUsage = limitMemory > 0 ? limitMemory : (int) config.getOrDefault("limits.memory_max_usage_mb", -1);
            int diskMaxIo = limitDiskIo > 0 ? limitDiskIo : (int) config.getOrDefault("limits.disk_max_io_speed_mbps", -1);
            int networkMaxBandwidth = limitNetwork > 0 ? limitNetwork : (int) config.getOrDefault("limits.network_max_bandwidth_mbps", -1);

            // Initialize metrics collection
            MetricsModule.initializeMetrics();
            metricsExecutor = Executors.newScheduledThreadPool(1);
            metricsExecutor.scheduleAtFixedRate(MetricsModule::logMetrics, 0, 1, TimeUnit.SECONDS);

            boolean isAnyStressorEnabled = cpu || memory || disk || network || fileDescriptors || threads;
            if (!isAnyStressorEnabled) {
                System.out.println("No stressor selected. Use --help to view available options.");
                return;
            }

            if (cpu) {
                logger.info("Starting CPU stress with {} threads. Limit: {}% CPU usage.", cpuThreadsConfig, cpuMaxUsage);
                CpuStresser cpuStresser = new CpuStresser();
                cpuStresser.generateCPULoad(cpuThreadsConfig, durationInSeconds, cpuMaxUsage);
            }

            if (memory) {
                logger.info("Starting memory stress with {} arrays of {} MB each. Limit: {} MB.", memoryArraysConfig, arraySizeConfig, memoryMaxUsage);
                MemoryStresser memoryStresser = new MemoryStresser();
                memoryStresser.generateMemoryLoad(memoryArraysConfig, arraySizeConfig, durationInSeconds, memoryMaxUsage);
            }

            if (disk) {
                logger.info("Starting disk stress with {} files of {} MB each. Limit: {} MB/s I/O.", diskFileCountConfig, diskFileSizeConfig, diskMaxIo);
                DiskStresser diskStresser = new DiskStresser();
                diskStresser.generateDiskLoad(diskFileCountConfig, diskFileSizeConfig, durationInSeconds, diskMaxIo);
            }

            if (network) {
                logger.info("Starting network stress with URL: {}. Limit: {} Mbps.", networkUrlConfig, networkMaxBandwidth);
                NetworkStresser networkStresser = new NetworkStresser();
                networkStresser.generateNetworkLoad(networkUrlConfig, durationInSeconds, networkMaxBandwidth);
            }

            // Add similar logic for fileDescriptors and threads if limits are relevant...

            Thread.sleep(durationInSeconds * 1000);
            logger.info("Stress test completed. Shutting down.");

        } catch (CmdLineException e) {
            System.err.println("Error: " + e.getMessage());
            System.err.println("Use --help to see usage.");
        } catch (InterruptedException e) {
            logger.error("Stresser was interrupted", e);
            Thread.currentThread().interrupt();
        } finally {
            if (metricsExecutor != null) {
                metricsExecutor.shutdown();
            }
            MetricsModule.closeMetrics();
        }
    }
}
