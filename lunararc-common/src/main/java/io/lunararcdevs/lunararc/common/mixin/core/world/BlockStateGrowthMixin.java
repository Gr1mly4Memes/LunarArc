package io.lunararcdevs.lunararc.common.mixin.core.world;

import io.lunararcdevs.lunararc.common.server.LunarArcServerSettings;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockBehaviour.BlockStateBase.class)
public abstract class BlockStateGrowthMixin {
    private static final ThreadLocal<Boolean> LUNARARC_REPEATING = new ThreadLocal<>();

    @Inject(method = "randomTick", at = @At("HEAD"), cancellable = true, require = 0)
    private void lunararc$growthModifier(ServerLevel level, BlockPos pos, RandomSource random, CallbackInfo ci) {
        if (!LunarArcServerSettings.growthConfigured() || LUNARARC_REPEATING.get() != null) return;
        BlockState state = (BlockState) (Object) this;
        int modifier = LunarArcServerSettings.growthModifier(level, state.getBlock());
        if (modifier == 100) return;
        if (modifier < 100) {
            if (random.nextInt(100) >= Math.max(0, modifier)) ci.cancel();
            return;
        }
        int extra = modifier / 100 - 1;
        if (random.nextInt(100) < modifier % 100) extra++;
        LUNARARC_REPEATING.set(Boolean.TRUE);
        try {
            for (int i = 0; i < extra; i++) {
                BlockState current = level.getBlockState(pos);
                if (current.getBlock() != state.getBlock()) break;
                current.randomTick(level, pos, random);
            }
        } finally {
            LUNARARC_REPEATING.remove();
        }
    }
}
