package io.lunararcdevs.lunararc.common.mixin.core.patched;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

/** The never-load-a-chunk accessors Paper adds to BlockGetter, which donated CraftBukkit classes call. */
@Mixin(BlockGetter.class)
public interface BlockGetterPaperMixin {
    default @Nullable BlockState getBlockStateIfLoaded(BlockPos pos) {
        BlockGetter self = (BlockGetter) this;
        if (self instanceof LevelReader reader && !reader.hasChunkAt(pos)) return null;
        return self.getBlockState(pos);
    }

    default @Nullable FluidState getFluidIfLoaded(BlockPos pos) {
        BlockGetter self = (BlockGetter) this;
        if (self instanceof LevelReader reader && !reader.hasChunkAt(pos)) return null;
        return self.getFluidState(pos);
    }

    default @Nullable Block getBlockIfLoaded(BlockPos pos) {
        BlockState state = this.getBlockStateIfLoaded(pos);
        return state == null ? null : state.getBlock();
    }

    /** One step of {@link BlockGetter#clip(ClipContext)} that does not load the chunk it is looking at. */
    default @Nullable BlockHitResult clip(ClipContext context, BlockPos pos) {
        BlockGetter self = (BlockGetter) this;
        BlockState blockState = this.getBlockStateIfLoaded(pos);
        if (blockState == null) {
            Vec3 away = context.getFrom().subtract(context.getTo());
            return BlockHitResult.miss(context.getTo(), Direction.getApproximateNearest(away.x, away.y, away.z), BlockPos.containing(context.getTo()));
        }
        if (blockState.isAir()) return null;
        FluidState fluidState = blockState.getFluidState();
        Vec3 from = context.getFrom();
        Vec3 to = context.getTo();
        VoxelShape blockShape = context.getBlockShape(blockState, self, pos);
        BlockHitResult blockHit = self.clipWithInteractionOverride(from, to, pos, blockShape, blockState);
        VoxelShape fluidShape = context.getFluidShape(fluidState, self, pos);
        BlockHitResult fluidHit = fluidShape.clip(from, to, pos);
        double blockDistance = blockHit == null ? Double.MAX_VALUE : from.distanceToSqr(blockHit.getLocation());
        double fluidDistance = fluidHit == null ? Double.MAX_VALUE : from.distanceToSqr(fluidHit.getLocation());
        return blockDistance <= fluidDistance ? blockHit : fluidHit;
    }
}
