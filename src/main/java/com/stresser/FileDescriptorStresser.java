package com.stresser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileDescriptorStresser {
    private static final Logger logger = LoggerFactory.getLogger(FileDescriptorStresser.class);

    public void generateFileDescriptorLoad(int openFiles, int durationInSeconds) {
        logger.info("Generating file descriptor stress by opening {} files for {} seconds.", openFiles, durationInSeconds);

        long endTime = System.currentTimeMillis() + (durationInSeconds * 1000);

        for (int i = 0; i < openFiles; i++) {
            try {
                File file = new File("/tmp/stresser/test_file_" + i + ".txt");
                FileOutputStream fos = new FileOutputStream(file);
                // Simulate file descriptor usage
                fos.write(new byte[1024 * 1024]);  // Write 1MB to file

                if (System.currentTimeMillis() > endTime) {
                    fos.close();
                    break;
                }

                // Optional: Limit the number of file descriptors opened at once
            } catch (IOException e) {
                logger.error("Error during file descriptor stress.", e);
            }
        }
    }
}
