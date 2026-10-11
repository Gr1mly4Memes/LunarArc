package io.lunararcdevs.lunararc.common.mixin.core.world;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.lunararcdevs.lunararc.common.server.LunarArcServerSettings;
import net.minecraft.world.entity.MobCategory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "net.minecraft.world.level.LocalMobCapCalculator$MobCounts", remap = false)
public abstract class LocalMobCapSettingsMixin {
    @WrapOperation(method = "canSpawn", remap = true, require = 0,
            at = @At(value = "INVOKE", remap = true,
                    target = "Lnet/minecraft/world/entity/MobCategory;getMaxInstancesPerChunk()I"))
    private int lunararc$localCap(MobCategory category, Operation<Integer> original) {
        return LunarArcServerSettings.limit(category, original.call(category));
    }
}
