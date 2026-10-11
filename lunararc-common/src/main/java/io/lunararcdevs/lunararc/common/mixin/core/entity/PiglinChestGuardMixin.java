package io.lunararcdevs.lunararc.common.mixin.core.entity;

import io.lunararcdevs.lunararc.common.server.LunarArcServerSettings;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PiglinAi.class)
public abstract class PiglinChestGuardMixin {
    @Inject(method = "angerNearbyPiglins", at = @At("HEAD"), cancellable = true, require = 0)
    private static void lunararc$chestGuard(Player player, boolean requiresLineOfSight, CallbackInfo ci) {
        if (requiresLineOfSight && !LunarArcServerSettings.tuning(player.level()).piglinsGuardChests) ci.cancel();
    }
}
