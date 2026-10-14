package io.lunararcdevs.lunararc.common.mixin.core.entity;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.lunararcdevs.lunararc.common.event.LunarArcMoreEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** PlayerAttackEntityCooldownResetEvent: vanilla resets the strength ticker from Player#onAttack. */
@Mixin(Player.class)
public abstract class PlayerAttackCooldownMixin {
    @Unique private Entity lunararc$attackTarget;

    @Inject(method = "attack", at = @At("HEAD"), require = 1)
    private void lunararc$captureTarget(Entity target, CallbackInfo ci) {
        this.lunararc$attackTarget = target;
    }

    @Inject(method = "attack", at = @At("RETURN"), require = 1)
    private void lunararc$clearTarget(Entity target, CallbackInfo ci) {
        this.lunararc$attackTarget = null;
    }

    @WrapOperation(method = "onAttack", require = 1,
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;resetOnlyAttackStrengthTicker()V"))
    private void lunararc$cooldownReset(Player player, Operation<Void> original) {
        Entity target = this.lunararc$attackTarget;
        if (target == null || LunarArcMoreEvents.attackCooldownReset(player, target)) original.call(player);
    }
}
