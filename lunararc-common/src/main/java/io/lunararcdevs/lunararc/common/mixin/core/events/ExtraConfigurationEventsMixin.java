package io.lunararcdevs.lunararc.common.mixin.core.events;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import io.lunararcdevs.lunararc.common.bridge.ConfigurationConnectionBridge;
import java.util.List;
import net.minecraft.network.protocol.common.ClientboundServerLinksPacket;
import net.minecraft.server.ServerLinks;
import net.minecraft.server.network.ServerConfigurationPacketListenerImpl;
import org.bukkit.craftbukkit.CraftServerLinks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/** PlayerLinksSendEvent: a plugin may edit the server links a client is sent while it is being configured. */
@Mixin(ServerConfigurationPacketListenerImpl.class)
public abstract class ExtraConfigurationEventsMixin {
    @WrapOperation(method = "startConfiguration", require = 0,
            at = @At(value = "NEW", target = "(Ljava/util/List;)Lnet/minecraft/network/protocol/common/ClientboundServerLinksPacket;", remap = false))
    private ClientboundServerLinksPacket lunararc$links(List<ServerLinks.UntrustedEntry> links, Operation<ClientboundServerLinksPacket> original,
            @Local ServerLinks serverLinks) {
        if (org.bukkit.event.player.PlayerLinksSendEvent.getHandlerList().getRegisteredListeners().length == 0) return original.call(links);
        var connection = ((ConfigurationConnectionBridge) (Object) this).lunararc$paperConnection();
        CraftServerLinks craft = new CraftServerLinks(serverLinks);
        new org.bukkit.event.player.PlayerLinksSendEvent(connection, craft).callEvent();
        return original.call(craft.getHandle().untrust());
    }
}
