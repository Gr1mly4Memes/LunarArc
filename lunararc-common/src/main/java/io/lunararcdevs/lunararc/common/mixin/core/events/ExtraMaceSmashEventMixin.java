package io.lunararcdevs.lunararc.common.mixin.core.events;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import io.lunararcdevs.lunararc.common.event.LunarArcExtraEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MaceItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/** EntityAttemptSmashAttackEvent: a plugin may force or deny the mace smash on a hit. */
@Mixin(MaceItem.class)
public abstract class ExtraMaceSmashEventMixin {
    @WrapOperation(method = "hurtEnemy", require = 0,
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/MaceItem;canSmashAttack(Lnet/minecraft/world/entity/LivingEntity;)Z"))
    private boolean lunararc$smash(LivingEntity attacker, Operation<Boolean> original,
            @Local(argsOnly = true) ItemStack stack, @Local(argsOnly = true, ordinal = 0) LivingEntity target) {
        return LunarArcExtraEvents.smashAttack(attacker, target, stack, original.call(attacker));
    }
}
