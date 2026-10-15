package io.lunararcdevs.lunararc.common.mixin.core.events;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.SpongeBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.craftbukkit.block.CraftBlockState;
import org.bukkit.craftbukkit.util.BlockStateListPopulator;
import org.bukkit.event.block.SpongeAbsorbEvent;
import org.spongepowered.asm.mixin.Mixin;

/**
 * SpongeAbsorbEvent. The absorb search runs against a block-state list first, the event sees what would change,
 * and only then are the changes applied; with no listener vanilla runs untouched.
 */
@Mixin(SpongeBlock.class)
public abstract class ExtraSpongeAbsorbEventMixin {
    @WrapMethod(method = "removeWaterBreadthFirstSearch", require = 0)
    private boolean lunararc$absorb(Level level, BlockPos pos, Operation<Boolean> original) {
        if (SpongeAbsorbEvent.getHandlerList().getRegisteredListeners().length == 0
                || !(level instanceof ServerLevel server) || !org.bukkit.Bukkit.isPrimaryThread()) {
            return original.call(level, pos);
        }

        BlockStateListPopulator list = new BlockStateListPopulator(server);
        BlockPos.breadthFirstTraversal(pos, 6, 65, (current, consumer) -> {
            for (Direction direction : Direction.values()) consumer.accept(current.relative(direction));
        }, node -> {
            if (node.equals(pos)) return BlockPos.TraversalNodeStatus.ACCEPT;
            BlockState state = list.getBlockState(node);
            FluidState fluid = list.getFluidState(node);
            if (!fluid.is(FluidTags.WATER)) return BlockPos.TraversalNodeStatus.SKIP;
            if (state.getBlock() instanceof BucketPickup pickup && !pickup.pickupBlock(null, list, node, state).isEmpty()) {
                return BlockPos.TraversalNodeStatus.ACCEPT;
            }
            if (state.getBlock() instanceof LiquidBlock) {
                list.setBlock(node, Blocks.AIR.defaultBlockState(), 3);
            } else {
                if (!state.is(Blocks.KELP) && !state.is(Blocks.KELP_PLANT) && !state.is(Blocks.SEAGRASS) && !state.is(Blocks.TALL_SEAGRASS)) {
                    return BlockPos.TraversalNodeStatus.SKIP;
                }
                list.setBlock(node, Blocks.AIR.defaultBlockState(), 3);
            }
            return BlockPos.TraversalNodeStatus.ACCEPT;
        });

        List<BlockStateListPopulator.CapturedState> captured = list.getCapturedStates();
        if (captured.isEmpty()) return false;

        List<org.bukkit.block.BlockState> snapshots = new ArrayList<>();
        for (BlockStateListPopulator.CapturedState entry : captured) snapshots.add(entry.state());
        SpongeAbsorbEvent event = new SpongeAbsorbEvent(CraftBlock.at(server, pos), snapshots);
        if (!event.callEvent()) return false;

        for (BlockStateListPopulator.CapturedState entry : captured) {
            CraftBlockState snapshot = entry.state();
            if (!event.getBlocks().contains(snapshot)) continue;
            BlockPos target = snapshot.getPosition();
            BlockState current = level.getBlockState(target);
            FluidState fluid = level.getFluidState(target);
            if (fluid.is(FluidTags.WATER)) {
                if (current.getBlock() instanceof BucketPickup pickup && !pickup.pickupBlock(null, level, target, current).isEmpty()) {
                    // taken as a bucket pickup; the snapshot below tidies up the block
                } else if (!(current.getBlock() instanceof LiquidBlock)
                        && (current.is(Blocks.KELP) || current.is(Blocks.KELP_PLANT) || current.is(Blocks.SEAGRASS) || current.is(Blocks.TALL_SEAGRASS))) {
                    BlockEntity blockEntity = current.hasBlockEntity() ? level.getBlockEntity(target) : null;
                    if (snapshot.getHandle().isAir()) Block.dropResources(current, level, target, blockEntity);
                }
            }
            level.setBlock(target, snapshot.getHandle(), entry.flags());
        }
        return true;
    }
}
