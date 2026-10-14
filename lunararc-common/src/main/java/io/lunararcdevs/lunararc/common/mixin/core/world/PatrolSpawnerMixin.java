package io.lunararcdevs.lunararc.common.mixin.core.world;

import io.lunararcdevs.lunararc.common.server.LunarArcServerSettings;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.levelgen.PatrolSpawner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PatrolSpawner.class)
public abstract class PatrolSpawnerMixin {
    @Inject(method = "tick", at = @At("HEAD"), cancellable = true, require = 1)
    private void lunararc$disabled(ServerLevel level, boolean spawnEnemies, org.spongepowered.asm.mixin.injection.callback.CallbackInfo ci) {
        if (LunarArcServerSettings.tuning(level).pillagerPatrolsDisabled || LunarArcServerSettings.tuning(level).pillagerPatrolSpawnChance == 0) ci.cancel();
    }
}
