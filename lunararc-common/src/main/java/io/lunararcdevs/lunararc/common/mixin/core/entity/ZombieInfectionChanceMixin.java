package io.lunararcdevs.lunararc.common.mixin.core.entity;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import io.lunararcdevs.lunararc.common.server.LunarArcServerSettings;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.monster.zombie.Zombie;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Zombie.class)
public abstract class ZombieInfectionChanceMixin {
    @WrapOperation(method = "killedEntity", require = 0, at = @At(value = "INVOKE", ordinal = 0,
            target = "Lnet/minecraft/server/level/ServerLevel;getDifficulty()Lnet/minecraft/world/Difficulty;"))
    private Difficulty lunararc$gate(ServerLevel level, Operation<Difficulty> original) {
        return LunarArcServerSettings.tuning(level).zombieVillagerInfectionChance >= 0 ? Difficulty.NORMAL : original.call(level);
    }

    @WrapOperation(method = "killedEntity", require = 0, at = @At(value = "INVOKE", ordinal = 2,
            target = "Lnet/minecraft/server/level/ServerLevel;getDifficulty()Lnet/minecraft/world/Difficulty;"))
    private Difficulty lunararc$roll(ServerLevel level, Operation<Difficulty> original) {
        return LunarArcServerSettings.tuning(level).zombieVillagerInfectionChance >= 0 ? Difficulty.NORMAL : original.call(level);
    }

    @WrapOperation(method = "killedEntity", require = 0, at = @At(value = "INVOKE",
            target = "Lnet/minecraft/util/RandomSource;nextBoolean()Z"))
    private boolean lunararc$chance(RandomSource random, Operation<Boolean> original, @Local(argsOnly = true) ServerLevel level) {
        double chance = LunarArcServerSettings.tuning(level).zombieVillagerInfectionChance;
        return chance < 0 ? original.call(random) : random.nextDouble() * 100.0D >= chance;
    }
}
