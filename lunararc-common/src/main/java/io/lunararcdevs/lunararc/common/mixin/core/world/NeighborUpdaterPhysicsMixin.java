package io.lunararcdevs.lunararc.common.mixin.core.world;

import io.lunararcdevs.lunararc.common.LunarArcServerAccess;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.redstone.NeighborUpdater;
import net.minecraft.world.level.redstone.Orientation;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.craftbukkit.block.data.CraftBlockData;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** BlockPhysicsEvent: Paper fires it from NeighborUpdater#executeUpdate, right before the block handles the neighbour change. */
@Mixin(NeighborUpdater.class)
public interface NeighborUpdaterPhysicsMixin {
    @Inject(method = "executeUpdate", at = @At("HEAD"), cancellable = true, require = 1)
    private static void lunararc$physicsEvent(Level level, BlockState state, BlockPos pos, Block neighborBlock,
            @Nullable Orientation orientation, boolean movedByPiston, CallbackInfo ci) {
        if (!(level instanceof ServerLevel serverLevel) || BlockPhysicsEvent.getHandlerList().getRegisteredListeners().length == 0) return;
        // The source position is only tracked by Paper's patched updater; the changed block is the best stand-in.
        BlockPhysicsEvent event = new BlockPhysicsEvent(CraftBlock.at(serverLevel, pos), CraftBlockData.fromData(state), CraftBlock.at(serverLevel, pos));
        LunarArcServerAccess.getCraftServer(serverLevel.getServer()).getPluginManager().callEvent(event);
        if (event.isCancelled()) ci.cancel();
    }
}
