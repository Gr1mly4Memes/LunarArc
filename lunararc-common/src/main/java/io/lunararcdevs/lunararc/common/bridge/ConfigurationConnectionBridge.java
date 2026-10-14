package io.lunararcdevs.lunararc.common.bridge;

import io.papermc.paper.connection.PaperPlayerConfigurationConnection;

/** Paper's configuration-phase API connection for a client that is still being configured. */
public interface ConfigurationConnectionBridge {
    PaperPlayerConfigurationConnection lunararc$paperConnection();
}
