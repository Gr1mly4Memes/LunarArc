package io.lunararcdevs.lunararc.common.mixin.core.entity;

import io.lunararcdevs.lunararc.common.bridge.LivingEntityBridge;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.arrow.Arrow;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Carries the Bukkit ARROW effect cause into the tipped-arrow effect additions. */
@Mixin(Arrow.class)
public abstract class ArrowMixin {
    @Inject(method = "doPostHurtEffects", at = @At("HEAD"), require = 1)
    private void lunararc$arrowCause(LivingEntity target, CallbackInfo ci) {
        ((LivingEntityBridge) target).lunararc$pushEffectCause(EntityPotionEffectEvent.Cause.ARROW);
    }
}
