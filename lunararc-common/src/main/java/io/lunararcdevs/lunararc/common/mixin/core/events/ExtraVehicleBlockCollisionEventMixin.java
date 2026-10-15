package io.lunararcdevs.lunararc.common.mixin.core.events;

import com.llamalad7.mixinextras.sugar.Local;
import io.lunararcdevs.lunararc.common.event.LunarArcMobEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** VehicleBlockCollisionEvent: fired when a vehicle's sideways movement is stopped by a block. */
@Mixin(Entity.class)
public abstract class ExtraVehicleBlockCollisionEventMixin {
    @Unique private Vec3 lunararc$intendedMove = Vec3.ZERO;

    @Inject(method = "move", at = @At("HEAD"), require = 0)
    private void lunararc$rememberMove(MoverType type, Vec3 movement, CallbackInfo ci) {
        this.lunararc$intendedMove = movement;
    }

    @Inject(method = "move", require = 0,
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;getMovementEmission()Lnet/minecraft/world/entity/Entity$MovementEmission;"))
    private void lunararc$collision(MoverType type, Vec3 movement, CallbackInfo ci, @Local(ordinal = 0) Vec3 collided) {
        Entity self = (Entity) (Object) this;
        if (self.horizontalCollision) {
            LunarArcMobEvents.vehicleBlockCollision(self, this.lunararc$intendedMove, movement, collided);
        }
    }
}
