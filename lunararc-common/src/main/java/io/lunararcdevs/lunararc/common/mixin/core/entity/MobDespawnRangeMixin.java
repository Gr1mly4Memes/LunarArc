package io.lunararcdevs.lunararc.common.mixin.core.entity;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.lunararcdevs.lunararc.common.server.LunarArcServerSettings;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Mob.class)
public abstract class MobDespawnRangeMixin {
    @WrapOperation(method = "checkDespawn", require = 0,
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/MobCategory;getDespawnDistance()I"))
    private int lunararc$hardRange(MobCategory category, Operation<Integer> original) {
        int[] range = LunarArcServerSettings.tuning(((Mob) (Object) this).level()).despawnRanges.get(LunarArcServerSettings.categoryKey(category));
        return range != null && range[1] > 0 ? range[1] : original.call(category);
    }

    @WrapOperation(method = "checkDespawn", require = 0,
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/MobCategory;getNoDespawnDistance()I"))
    private int lunararc$softRange(MobCategory category, Operation<Integer> original) {
        int[] range = LunarArcServerSettings.tuning(((Mob) (Object) this).level()).despawnRanges.get(LunarArcServerSettings.categoryKey(category));
        return range != null && range[0] > 0 ? range[0] : original.call(category);
    }
}
