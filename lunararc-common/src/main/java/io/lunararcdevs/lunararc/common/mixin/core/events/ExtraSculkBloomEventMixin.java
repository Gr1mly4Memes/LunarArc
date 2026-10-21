package io.lunararcdevs.lunararc.common.mixin.core.events;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import io.lunararcdevs.lunararc.common.event.LunarArcExtraEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.SculkSpreader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

/**
 * SculkBloomEvent. A spreader does not know its level, so it is remembered from its last update (a catalyst
 * updates its spreader every tick, before any bloom adds cursors).
 */
@Mixin(SculkSpreader.class)
public abstract class ExtraSculkBloomEventMixin {
    @Unique private Level lunararc$level;

    @Shadow private void addCursor(SculkSpreader.ChargeCursor cursor) {}

    @Shadow public abstract boolean isWorldGeneration();

    @WrapMethod(method = "updateCursors", require = 0)
    private void lunararc$rememberLevel(LevelAccessor level, BlockPos pos, RandomSource random, boolean shouldConvertBlocks, Operation<Void> original) {
        if (level instanceof Level real) this.lunararc$level = real;
        original.call(level, pos, random, shouldConvertBlocks);
    }

    @WrapMethod(method = "addCursors", require = 0)
    private void lunararc$bloom(BlockPos pos, int charge, Operation<Void> original) {
        if (this.lunararc$level == null || this.isWorldGeneration()
                || org.bukkit.event.block.SculkBloomEvent.getHandlerList().getRegisteredListeners().length == 0) {
            original.call(pos, charge);
            return;
        }
        int remaining = charge;
        while (remaining > 0) {
            int part = Math.min(remaining, 1000);
            int result = LunarArcExtraEvents.sculkBloom(this.lunararc$level, pos, part);
            if (result >= 0) this.addCursor(new SculkSpreader.ChargeCursor(pos, result));
            remaining -= part;
        }
    }
}
