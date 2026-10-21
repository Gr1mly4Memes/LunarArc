package io.lunararcdevs.lunararc.common.mixin.core.events;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.lunararcdevs.lunararc.common.event.LunarArcMobEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/** EntityPushedByEntityAttackEvent for the knockback an ender dragon's wings give nearby entities. */
@Mixin(EnderDragon.class)
public abstract class ExtraDragonPushEventMixin {
    @WrapOperation(method = "knockBack", require = 0,
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;push(DDD)V"))
    private void lunararc$push(Entity target, double x, double y, double z, Operation<Void> original) {
        Vec3 push = LunarArcMobEvents.pushedByAttack(target, (Entity) (Object) this, x, y, z);
        if (push == null) return;
        original.call(target, push.x, push.y, push.z);
    }
}
