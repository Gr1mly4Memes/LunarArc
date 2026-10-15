package io.lunararcdevs.lunararc.common.mixin.core.events;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import io.lunararcdevs.lunararc.common.event.LunarArcMobEvents;
import java.util.List;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.hurtingprojectile.DragonFireball;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/** EnderDragonFireballHitEvent: a plugin may stop the lingering cloud a dragon fireball leaves. */
@Mixin(DragonFireball.class)
public abstract class ExtraDragonFireballHitEventMixin {
    @WrapOperation(method = "onHit", require = 0,
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"))
    private boolean lunararc$cloud(Level level, Entity cloud, Operation<Boolean> original, @Local(ordinal = 0) List<LivingEntity> hit) {
        if (cloud instanceof AreaEffectCloud areaCloud
                && !LunarArcMobEvents.dragonFireballHit((DragonFireball) (Object) this, hit, areaCloud)) {
            cloud.discard();
            return false;
        }
        return original.call(level, cloud);
    }
}
