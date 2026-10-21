package io.lunararcdevs.lunararc.common.mixin.core.events;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.lunararcdevs.lunararc.common.event.LunarArcPotionEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.projectile.throwableitemprojectile.AbstractThrownPotion;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/** WaterBottleSplashEvent: wraps the water branch of onHit, which is where vanilla calls onHitAsWater. */
@Mixin(AbstractThrownPotion.class)
public abstract class ExtraWaterBottleEventMixin {
    @WrapOperation(method = "onHit", require = 0,
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/throwableitemprojectile/AbstractThrownPotion;onHitAsWater(Lnet/minecraft/server/level/ServerLevel;)V"))
    private void lunararc$waterSplash(AbstractThrownPotion potion, ServerLevel level, Operation<Void> original, HitResult hit) {
        if (LunarArcPotionEvents.waterSplash(potion, level, hit)) return;
        original.call(potion, level);
    }
}
