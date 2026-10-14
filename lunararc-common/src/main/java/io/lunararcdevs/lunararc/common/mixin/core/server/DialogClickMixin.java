package io.lunararcdevs.lunararc.common.mixin.core.server;

import io.papermc.paper.adventure.PaperAdventure;
import io.papermc.paper.adventure.providers.ClickCallbackProviderImpl;
import io.papermc.paper.event.player.PaperPlayerCustomClickEvent;
import net.minecraft.network.protocol.common.ServerboundCustomClickActionPacket;
import net.minecraft.server.network.ServerCommonPacketListenerImpl;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Dialog buttons and click callbacks arrive as a custom click action. Vanilla only hands them to the server; Paper also fires
 * PlayerCustomClickEvent and runs Adventure's registered click callbacks.
 */
@Mixin(ServerCommonPacketListenerImpl.class)
public abstract class DialogClickMixin {
    @Inject(method = "handleCustomClickAction", at = @At("RETURN"))
    private void lunararc$customClick(ServerboundCustomClickActionPacket packet, CallbackInfo ci) {
        if (!((Object) this instanceof ServerGamePacketListenerImpl game)) return;
        if (!(((io.lunararcdevs.lunararc.common.bridge.EntityBridge) game.player).lunararc$getBukkitEntity() instanceof CraftPlayer player)) return;
        new PaperPlayerCustomClickEvent(PaperAdventure.asAdventure(packet.id()), player.getConnection(), packet.payload().orElse(null)).callEvent();
        ClickCallbackProviderImpl.DIALOG_CLICK_MANAGER.tryRunCallback(player, packet.id(), packet.payload());
        ClickCallbackProviderImpl.ADVENTURE_CLICK_MANAGER.tryRunCallback(player, packet.id(), packet.payload());
    }
}
