package io.lunararcdevs.lunararc.common.mixin.core.events;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.lunararcdevs.lunararc.common.event.LunarArcMobEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.BaseFireBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/** EntityCombustByBlockEvent for standing in fire. */
@Mixin(BaseFireBlock.class)
public abstract class ExtraFireCombustEventMixin {
    @WrapOperation(method = "fireIgnite", require = 0,
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;igniteForSeconds(F)V"))
    private static void lunararc$combust(Entity entity, float seconds, Operation<Void> original) {
        float result = LunarArcMobEvents.combustByBlock(entity, seconds);
        if (result < 0.0F) {
            // A cancelled ignition must not leave the entity counting as freshly lit.
            entity.setRemainingFireTicks(entity.getRemainingFireTicks() - 1);
            return;
        }
        LunarArcMobEvents.combustSuppressed = true;
        try {
            original.call(entity, result);
        } finally {
            LunarArcMobEvents.combustSuppressed = false;
        }
    }
}
