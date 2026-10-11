package io.lunararcdevs.lunararc.common.mixin.core.entity;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.lunararcdevs.lunararc.common.event.LunarArcMoreEvents;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ZombifiedPiglin.class)
public abstract class MoreMobEvents_PiglinAngerMixin {
    @WrapOperation(method = "startPersistentAngerTimer", require = 0,
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/monster/ZombifiedPiglin;setRemainingPersistentAngerTime(I)V"))
    private void lunararc$anger(ZombifiedPiglin self, int time, Operation<Void> original) {
        int result = LunarArcMoreEvents.piglinAnger(self, time);
        if (result >= 0) original.call(self, result);
    }
}
