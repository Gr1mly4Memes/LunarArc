package io.lunararcdevs.lunararc.common.mixin.core.events;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.lunararcdevs.lunararc.common.event.LunarArcMobEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.boss.enderdragon.phases.DragonStrafePlayerPhase;
import net.minecraft.world.entity.projectile.hurtingprojectile.DragonFireball;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/** EnderDragonShootFireballEvent: a plugin may stop the dragon shooting a fireball. */
@Mixin(DragonStrafePlayerPhase.class)
public abstract class ExtraDragonShootEventMixin {
    @WrapOperation(method = "doServerTick", require = 0,
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"))
    private boolean lunararc$shoot(ServerLevel level, Entity fireball, Operation<Boolean> original) {
        if (fireball instanceof DragonFireball dragonFireball && !LunarArcMobEvents.dragonShoot(dragonFireball)) {
            dragonFireball.discard();
            return false;
        }
        return original.call(level, fireball);
    }
}
