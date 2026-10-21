package io.lunararcdevs.lunararc.common.mixin.core.events;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import io.lunararcdevs.lunararc.common.event.LunarArcMobEvents;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.behavior.TransportItemsBetweenContainers;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/** ItemTransportingEntityValidateTargetEvent: a plugin may rule out a container as a delivery target. */
@Mixin(TransportItemsBetweenContainers.class)
public abstract class ExtraTransportTargetEventMixin {
    @WrapOperation(method = "isTargetValidToPick", require = 0,
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/behavior/TransportItemsBetweenContainers;isContainerLocked(Lnet/minecraft/world/entity/ai/behavior/TransportItemsBetweenContainers$TransportItemTarget;)Z"))
    private boolean lunararc$validate(TransportItemsBetweenContainers behavior, TransportItemsBetweenContainers.TransportItemTarget target,
            Operation<Boolean> original, @Local(argsOnly = true) PathfinderMob mob, @Local(argsOnly = true) Level level) {
        if (original.call(behavior, target)) return true;
        return !LunarArcMobEvents.transportTarget(mob, level, target.pos());
    }
}
