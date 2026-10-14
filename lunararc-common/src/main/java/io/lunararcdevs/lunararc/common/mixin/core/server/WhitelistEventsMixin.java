package io.lunararcdevs.lunararc.common.mixin.core.server;

import com.mojang.authlib.GameProfile;
import io.lunararcdevs.lunararc.common.event.LunarArcMoreEvents;
import net.minecraft.server.players.NameAndId;
import net.minecraft.server.players.UserWhiteList;
import net.minecraft.server.players.UserWhiteListEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Paper's WhitelistStateUpdateEvent. */
@Mixin(UserWhiteList.class)
public abstract class WhitelistEventsMixin {
    @Inject(method = "add(Lnet/minecraft/server/players/UserWhiteListEntry;)Z", at = @At("HEAD"), cancellable = true, require = 1)
    private void lunararc$add(UserWhiteListEntry entry, CallbackInfoReturnable<Boolean> cir) {
        NameAndId user = entry.getUser();
        if (user != null && !LunarArcMoreEvents.whitelistUpdate(new GameProfile(user.id(), user.name()), true)) cir.setReturnValue(false);
    }

    @Inject(method = "remove(Lnet/minecraft/server/players/NameAndId;)Z", at = @At("HEAD"), cancellable = true, require = 1)
    private void lunararc$remove(NameAndId user, CallbackInfoReturnable<Boolean> cir) {
        if (user != null && !LunarArcMoreEvents.whitelistUpdate(new GameProfile(user.id(), user.name()), false)) cir.setReturnValue(false);
    }
}
