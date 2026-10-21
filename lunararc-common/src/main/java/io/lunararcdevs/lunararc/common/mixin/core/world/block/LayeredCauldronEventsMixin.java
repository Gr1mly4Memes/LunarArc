package io.lunararcdevs.lunararc.common.mixin.core.world.block;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.lunararcdevs.lunararc.common.event.LunarArcPaperEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.event.block.CauldronLevelChangeEvent.ChangeReason;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * CauldronLevelChangeEvent for the changes nobody holds a cancel handle for: rain, dripstone and burning entities.
 * The player-driven fills and washes are not covered here, because cancelling them would still consume the item.
 */
@Mixin(LayeredCauldronBlock.class)
public abstract class LayeredCauldronEventsMixin {
    @WrapOperation(method = {"handlePrecipitation", "receiveStalactiteDrip"}, require = 0,
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;setBlockAndUpdate(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z"))
    private boolean lunararc$naturalFill(Level level, BlockPos pos, BlockState newState, Operation<Boolean> original) {
        if (!LunarArcPaperEvents.cauldronLevelChange(level, pos, newState, ChangeReason.NATURAL_FILL)) return false;
        return original.call(level, pos, newState);
    }

    @WrapOperation(method = "handleEntityOnFireInside", require = 0,
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/LayeredCauldronBlock;lowerFillLevel(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)V"))
    private void lunararc$extinguish(BlockState state, Level level, BlockPos pos, Operation<Void> original) {
        int lowered = state.getValue(LayeredCauldronBlock.LEVEL) - 1;
        BlockState newState = lowered == 0 ? Blocks.CAULDRON.defaultBlockState() : state.setValue(LayeredCauldronBlock.LEVEL, lowered);
        if (!LunarArcPaperEvents.cauldronLevelChange(level, pos, newState, ChangeReason.EXTINGUISH)) return;
        original.call(state, level, pos);
    }
}
