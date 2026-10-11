package io.lunararcdevs.lunararc.common.mixin.core.world;

import io.lunararcdevs.lunararc.common.server.LunarArcServerSettings;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.FarmBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FarmBlock.class)
public abstract class FarmlandRateMixin {
    @Inject(method = "randomTick", at = @At("HEAD"), cancellable = true, require = 0)
    private void lunararc$tickRate(BlockState state, ServerLevel level, BlockPos pos, RandomSource random, CallbackInfo ci) {
        var tuning = LunarArcServerSettings.tuning(level);
        if (tuning.dryFarmlandRate == 1 && tuning.wetFarmlandRate == 1) return;
        int rate = state.getValue(FarmBlock.MOISTURE) > 0 ? tuning.wetFarmlandRate : tuning.dryFarmlandRate;
        if (!LunarArcServerSettings.tickAllowed(level, pos, rate)) ci.cancel();
    }
}
