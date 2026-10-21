package io.lunararcdevs.lunararc.common.mixin.core.world;

import com.llamalad7.mixinextras.sugar.Local;
import io.lunararcdevs.lunararc.common.server.LunarArcServerSettings;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.npc.wanderingtrader.WanderingTraderSpawner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(WanderingTraderSpawner.class)
public abstract class WanderingTraderTuningMixin {
    @ModifyConstant(method = "tick", require = 0, constant = @Constant(intValue = 1200))
    private int lunararc$minute(int vanilla, @Local(argsOnly = true) ServerLevel level) {
        return LunarArcServerSettings.tuning(level).traderMinuteLength;
    }

    @ModifyConstant(method = "tick", require = 0, constant = @Constant(intValue = 24000))
    private int lunararc$day(int vanilla, @Local(argsOnly = true) ServerLevel level) {
        return LunarArcServerSettings.tuning(level).traderDayLength;
    }

    @ModifyConstant(method = "tick", require = 0, constant = @Constant(intValue = 25, ordinal = 0))
    private int lunararc$increment(int vanilla, @Local(argsOnly = true) ServerLevel level) {
        return LunarArcServerSettings.tuning(level).traderChanceIncrement;
    }

    @ModifyConstant(method = "tick", require = 0, constant = @Constant(intValue = 25, ordinal = 1))
    private int lunararc$clampMin(int vanilla, @Local(argsOnly = true) ServerLevel level) {
        return LunarArcServerSettings.tuning(level).traderChanceMin;
    }

    @ModifyConstant(method = "tick", require = 0, constant = @Constant(intValue = 25, ordinal = 2))
    private int lunararc$resetChance(int vanilla, @Local(argsOnly = true) ServerLevel level) {
        return LunarArcServerSettings.tuning(level).traderChanceMin;
    }

    @ModifyConstant(method = "tick", require = 0, constant = @Constant(intValue = 75))
    private int lunararc$clampMax(int vanilla, @Local(argsOnly = true) ServerLevel level) {
        return LunarArcServerSettings.tuning(level).traderChanceMax;
    }
}
