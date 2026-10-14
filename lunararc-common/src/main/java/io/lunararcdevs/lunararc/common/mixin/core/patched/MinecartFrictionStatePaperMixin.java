package io.lunararcdevs.lunararc.common.mixin.core.patched;

import net.kyori.adventure.util.TriState;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Paper's minecart friction API: the state is saved with the cart; the movement classes honour it. */
@Mixin(AbstractMinecart.class)
public abstract class MinecartFrictionStatePaperMixin implements io.lunararcdevs.lunararc.common.bridge.entity.MinecartFrictionBridge {
    public TriState frictionState = TriState.NOT_SET;

    @Override
    public TriState lunararc$frictionState() {
        return this.frictionState;
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void lunararc$readFriction(ValueInput input, CallbackInfo ci) {
        input.getString("Paper.FrictionState").ifPresent(name -> {
            try {
                this.frictionState = TriState.valueOf(name);
            } catch (IllegalArgumentException ignored) {
                // an unknown value from a newer or foreign save is simply ignored
            }
        });
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void lunararc$writeFriction(ValueOutput output, CallbackInfo ci) {
        if (this.frictionState != TriState.NOT_SET) output.putString("Paper.FrictionState", this.frictionState.toString());
    }
}
