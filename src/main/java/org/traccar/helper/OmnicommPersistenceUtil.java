package org.traccar.helper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.traccar.Context;
import org.traccar.config.Keys;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class OmnicommPersistenceUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(OmnicommPersistenceUtil.class);

    private final Map<Long, Long> cache;

    public OmnicommPersistenceUtil() {
        cache = new HashMap<>();

        // populate the in-memory cache with persisted data
        try {
            String path = Context.getConfig().getString(Keys.OMNICOMM_CACHE_PATH);
            BufferedReader reader = new BufferedReader(new FileReader(path));

            reader.lines().forEach((String line) -> {
                String[] values = line.split(" ");

                cache.put(Long.parseLong(values[0]), Long.parseLong(values[1]));
            });
        } catch (Exception exception) {
            LOGGER.error("Failed to read omnicomm cache data from file", exception);
        }
    }

    public void persistCache() {
        LOGGER.debug("Saving omnicomm cache to file");
        // store the cache in a text file
        try {
            String path = Context.getConfig().getString(Keys.OMNICOMM_CACHE_PATH);
            BufferedWriter writer = new BufferedWriter(new FileWriter(path));

            cache.forEach((Long device, Long index) -> {
                try {
                    writer.write(String.format("%d %d", device, index));
                } catch (IOException exception) {
                    String errorMessage = String.format("Failed to write omnicomm index cache element (deviceId=%d, index=%d)", device, index);
                    LOGGER.error(errorMessage, exception);
                }
            });

            writer.flush();
            LOGGER.debug("Saved omnicomm cache to file successfully");
        } catch (Exception exception) {
            LOGGER.error("Failed to persist omnicomm index cache to disk", exception);
        }
    }

    public Long getStoredOffsetFor(Long deviceId) {
        return cache.getOrDefault(deviceId, 0L);
    }

    public void setStoredOffset(Long deviceId, Long offset) {
        cache.put(deviceId, offset);
    }
}
