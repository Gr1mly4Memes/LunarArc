package io.lunararcdevs.lunararc.common.mixin.core.entity;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.entity.vehicle.minecart.OldMinecartBehavior;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

/** VehicleEntityCollisionEvent: minecart physics moved into behaviour classes in 1.21.2+. */
@Mixin(OldMinecartBehavior.class)
public abstract class MinecartOldBehaviorCollisionMixin {
    @org.spongepowered.asm.mixin.Unique
    private AbstractMinecart lunararc$cartCollision() { return ((net.minecraft.world.entity.vehicle.minecart.MinecartBehavior) (Object) this).minecart; }

    @WrapOperation(method = "pushAndPickupEntities", require = 1,
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;startRiding(Lnet/minecraft/world/entity/Entity;)Z"))
    private boolean lunararc$collideRide(Entity passenger, Entity vehicle, Operation<Boolean> original) {
        if (CraftEventFactory.callVehicleEntityCollisionCancelled(this.lunararc$cartCollision(), passenger)) return false;
        return original.call(passenger, vehicle);
    }

    @WrapOperation(method = "pushAndPickupEntities", require = 1,
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;push(Lnet/minecraft/world/entity/Entity;)V"))
    private void lunararc$collidePush(Entity entity, Entity other, Operation<Void> original) {
        if (CraftEventFactory.callVehicleEntityCollisionCancelled(this.lunararc$cartCollision(), entity == this.lunararc$cartCollision() ? other : entity)) return;
        original.call(entity, other);
    }
}
