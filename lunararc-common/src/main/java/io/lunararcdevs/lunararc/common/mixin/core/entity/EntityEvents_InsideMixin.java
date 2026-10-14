package io.lunararcdevs.lunararc.common.mixin.core.entity;

import io.lunararcdevs.lunararc.common.event.LunarArcMoreEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** EntityInsideBlockEvent: entityInside is now invoked from a lambda in Entity, so gate it on the block state itself. */
@Mixin(BlockBehaviour.BlockStateBase.class)
public abstract class EntityEvents_InsideMixin {
    @Inject(method = "entityInside", at = @At("HEAD"), cancellable = true, require = 1)
    private void lunararc$inside(Level level, BlockPos pos, Entity entity, InsideBlockEffectApplier applier, boolean intersects, CallbackInfo ci) {
        if (!LunarArcMoreEvents.inside(entity, level, pos)) ci.cancel();
    }
}
