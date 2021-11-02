package org.traccar.helper;

import jetbrains.exodus.ByteIterable;
import jetbrains.exodus.bindings.LongBinding;
import jetbrains.exodus.env.Environment;
import jetbrains.exodus.env.Environments;
import jetbrains.exodus.env.Store;
import jetbrains.exodus.env.StoreConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.traccar.Context;
import org.traccar.config.Keys;

public final class OmnicommPersistenceUtil {

    private static final String STORE_NAME = "devices";

    private static final Logger LOGGER = LoggerFactory.getLogger(OmnicommPersistenceUtil.class);

    private final Environment environment;

    private final Store store;

    public OmnicommPersistenceUtil() {
        LOGGER.debug("Creating Xodus environment");

        environment = Environments.newInstance(Context.getConfig().getString(Keys.OMNICOMM_CACHE_PATH));
        store = environment.computeInTransaction(txn -> {
            return environment.openStore(STORE_NAME, StoreConfig.WITHOUT_DUPLICATES, txn);
        });

        LOGGER.debug("Successfully opened store");
    }

    public Long getStoredOffsetFor(Long deviceId) {
        LOGGER.debug(String.format("Retrieving stored offset for device %d", deviceId));

        final ByteIterable key = LongBinding.longToEntry(deviceId);

        return environment.computeInReadonlyTransaction(txn -> {
            ByteIterable entry = store.get(txn, key);

            if (entry == null) {
                LOGGER.debug(String.format("No stored offset for device %d", deviceId));
                return 0L;
            } else {
                Long offset = LongBinding.entryToLong(entry);
                LOGGER.debug(String.format("Returning offset %d for device %d", offset, deviceId));

                return offset;
            }
        });
    }

    public void setStoredOffset(Long deviceId, Long offset) {
        LOGGER.debug(String.format("Storing offset %d for device %d", offset, deviceId));

        environment.executeInTransaction(txn -> {
            store.put(txn, LongBinding.longToEntry(deviceId), LongBinding.longToEntry(offset));
        });
    }
}
