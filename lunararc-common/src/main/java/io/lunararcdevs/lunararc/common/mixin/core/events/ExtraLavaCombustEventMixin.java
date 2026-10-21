package io.lunararcdevs.lunararc.common.mixin.core.events;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.lunararcdevs.lunararc.common.event.LunarArcMobEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/** EntityCombustByBlockEvent for lava; like Paper, only fired when a living entity is not already burning. */
@Mixin(Entity.class)
public abstract class ExtraLavaCombustEventMixin {
    @WrapOperation(method = "lavaIgnite", require = 0,
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;igniteForSeconds(F)V"))
    private void lunararc$combust(Entity self, float seconds, Operation<Void> original) {
        if (!(self instanceof LivingEntity) || self.getRemainingFireTicks() > 0) {
            original.call(self, seconds);
            return;
        }
        float result = LunarArcMobEvents.combustByBlock(self, seconds);
        if (result < 0.0F) return;
        LunarArcMobEvents.combustSuppressed = true;
        try {
            original.call(self, result);
        } finally {
            LunarArcMobEvents.combustSuppressed = false;
        }
    }
}
