package io.lunararcdevs.lunararc.common.mixin.core.events;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import io.lunararcdevs.lunararc.common.event.LunarArcBlockEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;

/** Remembers which cell a fluid spread from so BlockBreakBlockEvent can name its source. */
@Mixin(FlowingFluid.class)
public abstract class ExtraFluidSpreadSourceMixin {
    @WrapMethod(method = "spreadTo", require = 0)
    private void lunararc$rememberSource(LevelAccessor level, BlockPos pos, BlockState state, Direction direction, FluidState fluid, Operation<Void> original) {
        LunarArcBlockEvents.fluidSource = pos.relative(direction.getOpposite());
        try {
            original.call(level, pos, state, direction, fluid);
        } finally {
            LunarArcBlockEvents.fluidSource = null;
        }
    }
}
