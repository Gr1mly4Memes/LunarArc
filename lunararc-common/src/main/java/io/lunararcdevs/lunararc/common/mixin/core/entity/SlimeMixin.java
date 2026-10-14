package io.lunararcdevs.lunararc.common.mixin.core.entity;
import io.lunararcdevs.lunararc.common.bridge.entity.SlimeBridge;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.monster.Slime;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(Slime.class)
public abstract class SlimeMixin implements SlimeBridge {
    @Unique private boolean lunararc$canWander = true;
    @Override public boolean lunararc$canWander() { return lunararc$canWander; }
    @Override public void lunararc$setCanWander(boolean value) { lunararc$canWander = value; }
    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"), require = 1)
    private void lunararc$saveWander(net.minecraft.world.level.storage.ValueOutput output, CallbackInfo ci) { output.putBoolean("Paper.canWander", lunararc$canWander); }
    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"), require = 1)
    private void lunararc$loadWander(net.minecraft.world.level.storage.ValueInput input, CallbackInfo ci) { lunararc$canWander = input.getBooleanOr("Paper.canWander", true); }
}
