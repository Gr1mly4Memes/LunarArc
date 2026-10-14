package io.lunararcdevs.lunararc.common.mixin.core.item;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.lunararcdevs.lunararc.common.bridge.LivingEntityBridge;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;
import net.minecraft.world.level.Level;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/** Gives consumable status effects (ominous bottle, suspicious stew and other food) their Bukkit cause. */
@Mixin(ApplyStatusEffectsConsumeEffect.class)
public abstract class ApplyStatusEffectsConsumeEffectMixin {
    @WrapOperation(
            method = "apply",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;addEffect(Lnet/minecraft/world/effect/MobEffectInstance;)Z"),
            require = 0)
    private boolean lunararc$effectCause(LivingEntity entity, MobEffectInstance effect, Operation<Boolean> original,
                                         Level level, ItemStack stack, LivingEntity unused) {
        if (entity instanceof LivingEntityBridge bridge) {
            bridge.lunararc$pushEffectCause(io.lunararcdevs.lunararc.common.util.LunarArcEffectContext.totem() ? EntityPotionEffectEvent.Cause.TOTEM
                    : stack.is(Items.OMINOUS_BOTTLE) ? EntityPotionEffectEvent.Cause.POTION_DRINK : EntityPotionEffectEvent.Cause.FOOD);
        }
        return original.call(entity, effect);
    }
}
