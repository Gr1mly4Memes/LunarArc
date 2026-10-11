package io.lunararcdevs.lunararc.common.mixin.core.world;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import io.lunararcdevs.lunararc.common.server.LunarArcServerSettings;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.PatrolSpawner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PatrolSpawner.class)
public abstract class PatrolSpawnerTuningMixin {
    @WrapOperation(method = "tick", require = 0, at = @At(value = "INVOKE", ordinal = 0,
            target = "Lnet/minecraft/util/RandomSource;nextInt(I)I"))
    private int lunararc$delay(RandomSource random, int bound, Operation<Integer> original,
                               @Local(argsOnly = true) ServerLevel level) {
        return original.call(random, bound) + LunarArcServerSettings.tuning(level).pillagerPatrolSpawnDelay - 12000;
    }

    @WrapOperation(method = "tick", require = 0, at = @At(value = "INVOKE", ordinal = 1,
            target = "Lnet/minecraft/util/RandomSource;nextInt(I)I"))
    private int lunararc$chance(RandomSource random, int bound, Operation<Integer> original,
                                @Local(argsOnly = true) ServerLevel level) {
        original.call(random, bound);
        return random.nextDouble() >= LunarArcServerSettings.tuning(level).pillagerPatrolSpawnChance ? 1 : 0;
    }

    @WrapOperation(method = "tick", require = 0, at = @At(value = "INVOKE",
            target = "Lnet/minecraft/server/level/ServerLevel;getDayTime()J"))
    private long lunararc$startDay(ServerLevel level, Operation<Long> original) {
        long shift = (long) (5 - LunarArcServerSettings.tuning(level).pillagerPatrolStartDay) * 24000L;
        return original.call(level) + shift;
    }
}
