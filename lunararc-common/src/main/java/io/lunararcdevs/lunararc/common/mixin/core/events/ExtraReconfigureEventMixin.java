package io.lunararcdevs.lunararc.common.mixin.core.events;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.lunararcdevs.lunararc.common.bridge.ConfigurationConnectionBridge;
import net.minecraft.network.Connection;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.network.ServerConfigurationPacketListenerImpl;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/** PlayerConnectionReconfigureEvent: fired when a connected player is sent back to the configuration phase. */
@Mixin(ServerGamePacketListenerImpl.class)
public abstract class ExtraReconfigureEventMixin {
    @WrapOperation(method = "handleConfigurationAcknowledged", require = 0,
            at = @At(value = "NEW", target = "(Lnet/minecraft/server/MinecraftServer;Lnet/minecraft/network/Connection;Lnet/minecraft/server/network/CommonListenerCookie;)Lnet/minecraft/server/network/ServerConfigurationPacketListenerImpl;", remap = false))
    private ServerConfigurationPacketListenerImpl lunararc$reconfigure(MinecraftServer server, Connection connection, CommonListenerCookie cookie,
            Operation<ServerConfigurationPacketListenerImpl> original) {
        ServerConfigurationPacketListenerImpl listener = original.call(server, connection, cookie);
        if (io.papermc.paper.event.connection.configuration.PlayerConnectionReconfigureEvent.getHandlerList().getRegisteredListeners().length != 0) {
            new io.papermc.paper.event.connection.configuration.PlayerConnectionReconfigureEvent(
                    ((ConfigurationConnectionBridge) (Object) listener).lunararc$paperConnection()).callEvent();
        }
        return listener;
    }
}
