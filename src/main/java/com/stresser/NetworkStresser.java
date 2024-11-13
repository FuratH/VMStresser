package com.stresser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class NetworkStresser {
    private static final Logger logger = LoggerFactory.getLogger(NetworkStresser.class);

    public void generateNetworkLoad(String downloadUrl, int durationInSeconds, int networkMaxBandwidth) {
        logger.info("Starting network stress with URL: {} for {} seconds. Max bandwidth: {} Mbps",
                downloadUrl, durationInSeconds, networkMaxBandwidth == -1 ? "no limit" : networkMaxBandwidth);

        long endTime = System.currentTimeMillis() + (durationInSeconds * 1000);

        try {
            URL url = new URL(downloadUrl);
            URLConnection connection = url.openConnection();
            connection.setConnectTimeout(5000);  // Set a timeout to avoid hanging indefinitely
            connection.setReadTimeout(5000);

            InputStream in = connection.getInputStream();

            byte[] buffer = new byte[1024 * 1024];  // 1MB buffer
            long bytesRead;
            while (System.currentTimeMillis() < endTime) {
                bytesRead = in.read(buffer);
                if (bytesRead == -1) break;  // End of stream

                if (networkMaxBandwidth > 0) {
                    // Control download speed to simulate bandwidth usage
                    long currentTime = System.currentTimeMillis();
                    long elapsedTime = currentTime - (System.currentTimeMillis() - 1000);
                    if (elapsedTime > 1000 / networkMaxBandwidth) {
                        Thread.sleep(1000 / networkMaxBandwidth);  // Limit download speed
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Network stress failed.", e);
        }
    }
}
