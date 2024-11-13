package com.stresser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class DiskStresser {
    private static final Logger logger = LoggerFactory.getLogger(DiskStresser.class);

    public void generateDiskLoad(int fileCount, int fileSizeMB, int durationInSeconds, int diskMaxIO) {
        logger.info("Starting disk stress with {} files of {} MB for {} seconds. Max I/O speed: {} Mbps",
                fileCount, fileSizeMB, durationInSeconds, diskMaxIO == -1 ? "no limit" : diskMaxIO);

        long endTime = System.currentTimeMillis() + (durationInSeconds * 1000);
        File directory = new File("/tmp/stresser");  // Use a temp directory for disk stress
        directory.mkdirs();

        for (int i = 0; i < fileCount; i++) {
            File file = new File(directory, "test_file_" + i + ".dat");
            try (FileOutputStream fos = new FileOutputStream(file)) {
                byte[] data = new byte[1024 * 1024];  // Write 1MB blocks
                while (System.currentTimeMillis() < endTime) {
                    fos.write(data);

                    if (diskMaxIO > 0) {
                        // Implement I/O speed control here (e.g., sleep to limit I/O rate)
                        Thread.sleep(1000 / diskMaxIO);  // Adjust sleep to control I/O speed
                    }
                }
            } catch (IOException | InterruptedException e) {
                logger.error("Disk stress failed.", e);
            }
        }
    }
}
