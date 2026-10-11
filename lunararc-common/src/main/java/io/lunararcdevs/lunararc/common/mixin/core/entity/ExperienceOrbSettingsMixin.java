package io.lunararcdevs.lunararc.common.mixin.core.entity;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.lunararcdevs.lunararc.common.server.LunarArcServerSettings;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ExperienceOrb.class)
public abstract class ExperienceOrbSettingsMixin {
    @WrapOperation(method = "tryMergeToExisting", require = 0,
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/AABB;ofSize(Lnet/minecraft/world/phys/Vec3;DDD)Lnet/minecraft/world/phys/AABB;"))
    private static AABB lunararc$mergeBox(Vec3 center, double x, double y, double z, Operation<AABB> original, ServerLevel level) {
        double radius = LunarArcServerSettings.number(level, "merge-radius.exp", x / 2.0D);
        double size = radius * 2.0D;
        return original.call(center, size, size, size);
    }

    @WrapOperation(method = "scanForEntities", require = 0,
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/AABB;inflate(D)Lnet/minecraft/world/phys/AABB;"))
    private AABB lunararc$scanRadius(AABB box, double amount, Operation<AABB> original) {
        double radius = LunarArcServerSettings.number(((ExperienceOrb) (Object) this).level(), "merge-radius.exp", amount);
        return original.call(box, radius);
    }
}
