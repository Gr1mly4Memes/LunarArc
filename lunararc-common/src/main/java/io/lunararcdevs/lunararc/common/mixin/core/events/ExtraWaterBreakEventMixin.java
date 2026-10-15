package io.lunararcdevs.lunararc.common.mixin.core.events;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.lunararcdevs.lunararc.common.event.LunarArcBlockEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.WaterFluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/** BlockBreakBlockEvent for blocks that flowing water destroys. */
@Mixin(WaterFluid.class)
public abstract class ExtraWaterBreakEventMixin {
    @WrapOperation(method = "beforeDestroyingBlock", require = 0,
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Block;dropResources(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/entity/BlockEntity;)V"))
    private void lunararc$breakBlockEvent(BlockState state, LevelAccessor level, BlockPos pos, BlockEntity blockEntity, Operation<Void> original) {
        if (LunarArcBlockEvents.dropAfterBreak(state, level, pos, blockEntity, LunarArcBlockEvents.fluidSource)) return;
        original.call(state, level, pos, blockEntity);
    }
}
