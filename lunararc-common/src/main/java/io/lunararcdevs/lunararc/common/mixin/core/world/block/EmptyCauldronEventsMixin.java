package io.lunararcdevs.lunararc.common.mixin.core.world.block;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.lunararcdevs.lunararc.common.event.LunarArcPaperEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.event.block.CauldronLevelChangeEvent.ChangeReason;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/** CauldronLevelChangeEvent for an empty cauldron filled by rain, snow or dripstone. */
@Mixin(CauldronBlock.class)
public abstract class EmptyCauldronEventsMixin {
    @WrapOperation(method = {"handlePrecipitation", "receiveStalactiteDrip"}, require = 0,
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;setBlockAndUpdate(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z"))
    private boolean lunararc$naturalFill(Level level, BlockPos pos, BlockState newState, Operation<Boolean> original) {
        if (!LunarArcPaperEvents.cauldronLevelChange(level, pos, newState, ChangeReason.NATURAL_FILL)) return false;
        return original.call(level, pos, newState);
    }
}
