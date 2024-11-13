package com.metrics;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ReportGenerator {

    private String reportPath;

    public ReportGenerator(String reportPath) {
        this.reportPath = reportPath;
    }

    public void generateReport(String testName, long durationSeconds, double maxCpuUsage, long maxMemoryUsageMB) {
        File reportFile = new File(reportPath);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(reportFile, true))) {
            // Write the report header
            writer.write("Stress Test Report\n");
            writer.write("===================\n");
            writer.write("Test Name: " + testName + "\n");
            writer.write("Test Started: " + LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME) + "\n");
            writer.write("Duration: " + durationSeconds + " seconds\n");
            writer.write("Maximum CPU Usage: " + maxCpuUsage + "%\n");
            writer.write("Maximum Memory Usage: " + maxMemoryUsageMB + " MB\n");

            // Add more metrics as needed
            writer.write("\nEnd of Report\n");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
