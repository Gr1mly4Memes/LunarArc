package io.lunararcdevs.lunararc.common.mixin.core.world;

import com.destroystokyo.paper.event.entity.PhantomPreSpawnEvent;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import io.lunararcdevs.lunararc.common.LunarArcServerAccess;
import io.lunararcdevs.lunararc.common.server.LunarArcServerSettings;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.PhantomSpawner;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PhantomSpawner.class)
public abstract class PhantomSpawnerTuningMixin {
    @Unique private boolean lunararc$aborted;

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true, require = 0)
    private void lunararc$disabled(ServerLevel level, boolean spawnEnemies, boolean spawnFriendlies, CallbackInfoReturnable<Integer> cir) {
        var tuning = LunarArcServerSettings.tuning(level);
        lunararc$aborted = false;
        if (tuning.phantomMaxSeconds <= 0 || tuning.insomniaStartTicks < 0) cir.setReturnValue(0);
    }

    @WrapOperation(method = "tick", require = 0, at = @At(value = "INVOKE", ordinal = 0,
            target = "Lnet/minecraft/util/RandomSource;nextInt(I)I"))
    private int lunararc$attemptDelay(RandomSource random, int bound, Operation<Integer> original,
                                      @Local(argsOnly = true) ServerLevel level) {
        var tuning = LunarArcServerSettings.tuning(level);
        int min = tuning.phantomMinSeconds;
        int max = Math.max(tuning.phantomMaxSeconds, min);
        return original.call(random, max - min + 1) + (min - 60);
    }

    @WrapOperation(method = "tick", require = 0, at = @At(value = "INVOKE", ordinal = 1,
            target = "Lnet/minecraft/util/RandomSource;nextInt(I)I"))
    private int lunararc$insomnia(RandomSource random, int bound, Operation<Integer> original,
                                  @Local(argsOnly = true) ServerLevel level) {
        int roll = original.call(random, bound);
        return roll >= LunarArcServerSettings.tuning(level).insomniaStartTicks ? 72000 : 0;
    }

    @WrapOperation(method = "tick", require = 0, at = @At(value = "INVOKE",
            target = "Lnet/minecraft/server/level/ServerPlayer;isSpectator()Z"))
    private boolean lunararc$skipCreative(ServerPlayer player, Operation<Boolean> original,
                                          @Local(argsOnly = true) ServerLevel level) {
        return original.call(player)
                || (LunarArcServerSettings.tuning(level).phantomsSkipCreative && player.isCreative());
    }

    @WrapOperation(method = "tick", require = 0, at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/EntityType;create(Lnet/minecraft/world/level/Level;)Lnet/minecraft/world/entity/Entity;"))
    private Entity lunararc$preSpawn(EntityType<?> type, Level level, Operation<Entity> original,
                                     @Local ServerPlayer player, @Local(ordinal = 1) BlockPos spawnPos) {
        if (lunararc$aborted) return null;
        if (Bukkit.isPrimaryThread() && PhantomPreSpawnEvent.getHandlerList().getRegisteredListeners().length != 0
                && level instanceof ServerLevel serverLevel) {
            org.bukkit.World world = LunarArcServerAccess.getCraftWorld(serverLevel);
            org.bukkit.entity.Entity bukkitPlayer = ((io.lunararcdevs.lunararc.common.bridge.EntityBridge) player).lunararc$getBukkitEntity();
            var event = new PhantomPreSpawnEvent(new Location(world, spawnPos.getX(), spawnPos.getY(), spawnPos.getZ()),
                    bukkitPlayer, CreatureSpawnEvent.SpawnReason.NATURAL);
            Bukkit.getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                if (event.shouldAbortSpawn()) lunararc$aborted = true;
                return null;
            }
        }
        return original.call(type, level);
    }
}
