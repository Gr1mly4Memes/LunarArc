package io.lunararcdevs.lunararc.common.mixin.core.events;

import io.lunararcdevs.lunararc.common.event.LunarArcExtraEvents;
import java.net.SocketAddress;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.NameAndId;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** PlayerServerFullCheckEvent: lets a plugin let a player in even though the server is full (or keep them out). */
@Mixin(PlayerList.class)
public abstract class ExtraServerFullCheckEventMixin {
    @Shadow @Final private List<ServerPlayer> players;

    @Shadow public abstract int getMaxPlayers();

    @Shadow public abstract boolean canBypassPlayerLimit(NameAndId id);

    @Inject(method = "canPlayerLogin(Ljava/net/SocketAddress;Lnet/minecraft/server/players/NameAndId;)Lnet/minecraft/network/chat/Component;",
            at = @At("RETURN"), cancellable = true, require = 0)
    private void lunararc$fullCheck(SocketAddress address, NameAndId id, CallbackInfoReturnable<Component> cir) {
        boolean full = this.players.size() >= this.getMaxPlayers() && !this.canBypassPlayerLimit(id);
        Component original = cir.getReturnValue();
        Component result = LunarArcExtraEvents.serverFullCheck(id, original, full);
        if (result != original) cir.setReturnValue(result);
    }
}
