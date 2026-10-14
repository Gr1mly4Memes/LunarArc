package io.lunararcdevs.lunararc.common.mixin.core.world;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.lunararcdevs.lunararc.common.server.LunarArcServerSettings;
import net.minecraft.world.entity.MobCategory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/** Paper's per-category spawn limits and ticks-per-spawn cadence, applied where NaturalSpawner asks "may this category spawn". */
@Mixin(targets = "net.minecraft.world.level.NaturalSpawner$SpawnState", remap = false)
public abstract class SpawnStateCapMixin {
    @WrapOperation(method = "canSpawnForCategoryGlobal", remap = true, require = 1,
            at = @At(value = "INVOKE", remap = true,
                    target = "Lnet/minecraft/world/entity/MobCategory;getMaxInstancesPerChunk()I"))
    private int lunararc$categoryCap(MobCategory category, Operation<Integer> original) {
        return LunarArcServerSettings.limit(category, original.call(category));
    }

    @WrapMethod(method = "canSpawnForCategoryGlobal", remap = true, require = 1)
    private boolean lunararc$categoryCadence(MobCategory category, Operation<Boolean> original) {
        return LunarArcServerSettings.cadenceAllows(category) && original.call(category);
    }
}
