package io.lunararcdevs.lunararc.common.mixin.core.world;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import io.lunararcdevs.lunararc.common.LunarArcServerAccess;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.craftbukkit.CraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Level.class)
public abstract class LevelMixin implements io.lunararcdevs.lunararc.common.bridge.LevelBridge {

    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger("LunarArc/LevelMixin");

    /**
     * Real Paper's anti-xray reveal trigger, verified against
     * {@code patches/server/0992-Anti-Xray.patch}'s {@code Level#setBlock} hook - see
     * {@link io.lunararcdevs.lunararc.common.server.LunarArcAntiXrayEngine#onBlockChange} for the
     * actual algorithm. Wraps the whole call rather than a HEAD/RETURN pair so the "before" state
     * lives in a local variable instead of shared per-thread state - a block's own
     * {@code onPlace}/shape-update side effects can synchronously call {@code setBlock} again on the
     * same thread before this call returns, and a local variable naturally gets a fresh copy per
     * call frame where a shared field or ThreadLocal would need its own reentrancy bookkeeping.
     */
    @WrapMethod(method = "setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;II)Z", require = 0)
    private boolean lunararc$antiXrayOnSetBlock(
            BlockPos pos, BlockState newState, int flags, int maxUpdateDepth, Operation<Boolean> original) {
        Level level = (Level) (Object) this;
        if (io.lunararcdevs.lunararc.common.event.LunarArcBlockCapture.intercept(level, pos, newState, flags)) return true;
        BlockState oldState = level instanceof ServerLevel ? level.getBlockState(pos) : null;
        boolean result = original.call(pos, newState, flags, maxUpdateDepth);
        if (oldState != null) {
            io.lunararcdevs.lunararc.common.server.LunarArcAntiXrayEngine.forLevel((ServerLevel) level)
                    .onBlockChange((ServerLevel) level, pos, newState, oldState);
        }
        return result;
    }

    @Inject(method = "getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;",
            at = @At("HEAD"), cancellable = true, require = 0)
    private void lunararc$capturedState(BlockPos pos, org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable<BlockState> cir) {
        BlockState captured = io.lunararcdevs.lunararc.common.event.LunarArcBlockCapture.read((Level) (Object) this, pos);
        if (captured != null) cir.setReturnValue(captured);
    }

    @Override
    public org.bukkit.craftbukkit.CraftWorld lunararc$getWorld() {
        return this.getWorld();
    }

    @Override
    public io.papermc.paper.configuration.WorldConfiguration lunararc$getPaperConfiguration() {
        return this.getWorld().getPaperConfiguration();
    }

    public io.papermc.paper.configuration.WorldConfiguration paperConfig() {
        return lunararc$getPaperConfiguration();
    }

    @Override
    public CraftServer lunararc$getCraftServer() {
        return this.getCraftServer();
    }

    public org.bukkit.craftbukkit.CraftWorld world;

    @Override
    public void lunararc$attachBukkitWorld(org.bukkit.craftbukkit.CraftWorld world) {
        this.world = world;
    }

    public org.bukkit.craftbukkit.CraftWorld getWorld() {
        if (this.world != null) return this.world;
        Level level = (Level) (Object) this;
        if (!(level instanceof ServerLevel serverLevel)) {
            throw new IllegalStateException("Bukkit world requested from a non-server level");
        }
        // Resolving also populates the field, so a Level that was never handed to CraftWorld's
        // constructor still ends up with it set rather than staying null for reflective readers.
        this.world = LunarArcServerAccess.getCraftServer(serverLevel.getServer()).getCraftWorld(serverLevel);
        return this.world;
    }

    public CraftServer getCraftServer() {
        Level level = (Level) (Object) this;
        if (!(level instanceof ServerLevel serverLevel)) {
            throw new IllegalStateException("CraftServer requested from a non-server level");
        }
        return LunarArcServerAccess.getCraftServer(serverLevel.getServer());
    }

}