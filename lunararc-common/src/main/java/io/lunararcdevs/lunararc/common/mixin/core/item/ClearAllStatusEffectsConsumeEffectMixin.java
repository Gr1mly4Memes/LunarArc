package io.lunararcdevs.lunararc.common.mixin.core.item;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.lunararcdevs.lunararc.common.bridge.LivingEntityBridge;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.consume_effects.ClearAllStatusEffectsConsumeEffect;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/** Gives milk (clear-all consume effect) removals their Bukkit cause while retaining the real LivingEntity effect path. */
@Mixin(ClearAllStatusEffectsConsumeEffect.class)
public abstract class ClearAllStatusEffectsConsumeEffectMixin {
    @WrapOperation(
            method = "apply",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;removeAllEffects()Z"),
            require = 0)
    private boolean lunararc$milkCause(LivingEntity entity, Operation<Boolean> original) {
        if (entity instanceof LivingEntityBridge bridge) {
            return bridge.lunararc$removeAllEffects(io.lunararcdevs.lunararc.common.util.LunarArcEffectContext.totem()
                    ? EntityPotionEffectEvent.Cause.TOTEM : EntityPotionEffectEvent.Cause.MILK);
        }
        return original.call(entity);
    }
}
