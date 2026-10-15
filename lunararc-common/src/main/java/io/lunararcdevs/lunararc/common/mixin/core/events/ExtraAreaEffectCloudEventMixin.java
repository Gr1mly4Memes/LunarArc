package io.lunararcdevs.lunararc.common.mixin.core.events;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.lunararcdevs.lunararc.common.event.LunarArcPotionEvents;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/** AreaEffectCloudApplyEvent: narrows or cancels the entities a cloud applies its effects to this tick. */
@Mixin(AreaEffectCloud.class)
public abstract class ExtraAreaEffectCloudEventMixin {
    @WrapOperation(method = "serverTick", require = 0,
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getEntitiesOfClass(Ljava/lang/Class;Lnet/minecraft/world/phys/AABB;)Ljava/util/List;"))
    private <T extends net.minecraft.world.entity.Entity> List<T> lunararc$cloudApply(Level level, Class<T> type, AABB box, Operation<List<T>> original) {
        List<T> found = original.call(level, type, box);
        if (type != LivingEntity.class) return found;
        @SuppressWarnings("unchecked")
        List<LivingEntity> narrowed = LunarArcPotionEvents.cloudApply((AreaEffectCloud) (Object) this, (List<LivingEntity>) found);
        @SuppressWarnings("unchecked")
        List<T> result = (List<T>) narrowed;
        return result;
    }
}
