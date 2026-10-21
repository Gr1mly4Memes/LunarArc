package io.lunararcdevs.lunararc.common.mixin.core.entity;

import io.lunararcdevs.lunararc.common.event.LunarArcMoreEvents;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.enderdragon.phases.EnderDragonPhase;
import net.minecraft.world.entity.boss.enderdragon.phases.EnderDragonPhaseManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EnderDragonPhaseManager.class)
public abstract class MoreMobEvents_DragonPhaseMixin {
    @Shadow @Final private EnderDragon dragon;
    @Shadow private net.minecraft.world.entity.boss.enderdragon.phases.DragonPhaseInstance currentPhase;

    @Inject(method = "setPhase", at = @At("HEAD"), cancellable = true, require = 0)
    private void lunararc$phase(EnderDragonPhase<?> phase, CallbackInfo ci) {
        // currentPhase is still null while the constructor sets the initial phase; getCurrentPhase() would throw
        if (this.currentPhase == null) return;
        if (!LunarArcMoreEvents.dragonPhase(this.dragon, this.currentPhase.getPhase().getId(), phase.getId())) ci.cancel();
    }
}
