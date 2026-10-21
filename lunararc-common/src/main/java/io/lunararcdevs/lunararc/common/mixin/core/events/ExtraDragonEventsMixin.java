package io.lunararcdevs.lunararc.common.mixin.core.events;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.lunararcdevs.lunararc.common.event.LunarArcMobEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.boss.enderdragon.phases.DragonSittingFlamingPhase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/** EnderDragonFlameEvent: a plugin may stop the dragon's ground flame. */
@Mixin(DragonSittingFlamingPhase.class)
public abstract class ExtraDragonEventsMixin {
    @WrapOperation(method = "doServerTick", require = 0,
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"))
    private boolean lunararc$flame(ServerLevel level, Entity flame, Operation<Boolean> original) {
        if (flame instanceof AreaEffectCloud cloud && !LunarArcMobEvents.dragonFlame(cloud)) {
            ((DragonSittingFlamingPhase) (Object) this).end();
            return false;
        }
        return original.call(level, flame);
    }
}
