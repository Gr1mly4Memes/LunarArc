package io.lunararcdevs.lunararc.common.mixin.core.events;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.lunararcdevs.lunararc.common.event.LunarArcExtraEvents;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/** EntityEffectTickEvent, fired before an active effect applies its tick; cancelling skips that tick. */
@Mixin(MobEffectInstance.class)
public abstract class ExtraEffectTickEventMixin {
    @WrapOperation(method = "tickServer", require = 0,
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/effect/MobEffect;applyEffectTick(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/LivingEntity;I)Z"))
    private boolean lunararc$effectTickEvent(MobEffect effect, ServerLevel level, LivingEntity entity, int amplifier, Operation<Boolean> original) {
        Holder<MobEffect> holder = ((MobEffectInstance) (Object) this).getEffect();
        if (!LunarArcExtraEvents.effectTick(entity, holder, amplifier)) return true;
        return original.call(effect, level, entity, amplifier);
    }
}
