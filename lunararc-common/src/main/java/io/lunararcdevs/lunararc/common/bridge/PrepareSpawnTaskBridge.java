package io.lunararcdevs.lunararc.common.bridge;

import net.minecraft.server.network.ServerConfigurationPacketListenerImpl;

/** Gives the spawn-preparation task the configuration listener it belongs to, as Paper's patched constructor does. */
public interface PrepareSpawnTaskBridge {
    void lunararc$setListener(ServerConfigurationPacketListenerImpl listener);

    ServerConfigurationPacketListenerImpl lunararc$getListener();
}
