package io.lunararcdevs.lunararc.common.mixin.core.events;

import io.lunararcdevs.lunararc.common.event.LunarArcExtraEvents;
import net.minecraft.world.level.border.WorldBorder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * WorldBorderBoundsChangeFinishEvent: fires when a timed border move reaches its target. The move's start, end
 * and length are remembered when it begins, and the finish is the tick where the moving extent is replaced by a
 * static one.
 */
@Mixin(WorldBorder.class)
public abstract class ExtraBorderFinishEventMixin {
    @Unique private double lunararc$from;
    @Unique private double lunararc$to;
    @Unique private double lunararc$duration;

    @Inject(method = "lerpSizeBetween", at = @At("HEAD"), require = 0)
    private void lunararc$rememberMove(double from, double to, long duration, long begin, CallbackInfo ci) {
        this.lunararc$from = from;
        this.lunararc$to = to;
        this.lunararc$duration = duration;
    }

    @Unique private long lunararc$lerpBefore;

    @Inject(method = "tick", at = @At("HEAD"), require = 0)
    private void lunararc$tickStart(CallbackInfo ci) {
        this.lunararc$lerpBefore = ((WorldBorder) (Object) this).getLerpTime();
    }

    @Inject(method = "tick", at = @At("RETURN"), require = 0)
    private void lunararc$tickEnd(CallbackInfo ci) {
        WorldBorder self = (WorldBorder) (Object) this;
        if (this.lunararc$lerpBefore > 0 && self.getLerpTime() <= 0) {
            LunarArcExtraEvents.borderFinished(self, this.lunararc$from, this.lunararc$to, this.lunararc$duration);
        }
    }
}
