package io.lunararcdevs.lunararc.common.mixin.core.entity;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.lunararcdevs.lunararc.common.bridge.entity.BurnInDayBridge;
import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/** Paper's Skeleton/Phantom#setShouldBurnInDay: burning is decided in Mob#burnUndead since 1.21.5. */
@Mixin(Mob.class)
public abstract class MobBurnInDayMixin {
    @WrapOperation(method = "burnUndead", require = 1,
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Mob;isSunBurnTick()Z"))
    private boolean lunararc$paperBurnInDay(Mob mob, Operation<Boolean> original) {
        if (mob instanceof BurnInDayBridge bridge && !bridge.lunararc$shouldBurnInDay()) return false;
        return original.call(mob);
    }
}
