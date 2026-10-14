package io.lunararcdevs.lunararc.common.mixin.core.server;

import io.lunararcdevs.lunararc.common.bridge.PrepareSpawnTaskBridge;
import net.minecraft.server.network.ServerConfigurationPacketListenerImpl;
import net.minecraft.server.network.config.PrepareSpawnTask;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(PrepareSpawnTask.class)
public abstract class PrepareSpawnTaskMixin implements PrepareSpawnTaskBridge {
    @Unique private ServerConfigurationPacketListenerImpl lunararc$listener;

    @Override public void lunararc$setListener(ServerConfigurationPacketListenerImpl listener) { this.lunararc$listener = listener; }
    @Override public ServerConfigurationPacketListenerImpl lunararc$getListener() { return this.lunararc$listener; }
}
