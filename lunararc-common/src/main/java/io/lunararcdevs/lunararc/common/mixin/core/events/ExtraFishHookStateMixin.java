package io.lunararcdevs.lunararc.common.mixin.core.events;

import io.lunararcdevs.lunararc.common.event.LunarArcExtraEvents;
import net.minecraft.world.entity.projectile.FishingHook;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** FishHookStateChangeEvent: vanilla assigns the state in several places, so compare it across the tick. */
@Mixin(FishingHook.class)
public abstract class ExtraFishHookStateMixin {
    @Shadow public FishingHook.FishHookState currentState;

    @Unique private FishingHook.FishHookState lunararc$stateBefore;

    @Inject(method = "tick", at = @At("HEAD"), require = 0)
    private void lunararc$rememberState(CallbackInfo ci) {
        this.lunararc$stateBefore = this.currentState;
    }

    @Inject(method = "tick", at = @At("RETURN"), require = 0)
    private void lunararc$stateEvent(CallbackInfo ci) {
        if (this.lunararc$stateBefore != null) {
            LunarArcExtraEvents.fishHookState((FishingHook) (Object) this, this.lunararc$stateBefore);
        }
    }
}
