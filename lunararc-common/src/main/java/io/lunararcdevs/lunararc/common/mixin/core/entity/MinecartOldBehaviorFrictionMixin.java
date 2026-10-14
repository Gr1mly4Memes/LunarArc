package io.lunararcdevs.lunararc.common.mixin.core.entity;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.kyori.adventure.util.TriState;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.entity.vehicle.minecart.OldMinecartBehavior;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

/** Paper's friction API: a cart whose friction is switched off keeps all its speed. */
@Mixin(OldMinecartBehavior.class)
public abstract class MinecartOldBehaviorFrictionMixin {
    @org.spongepowered.asm.mixin.Unique
    private AbstractMinecart lunararc$cartFriction() { return ((net.minecraft.world.entity.vehicle.minecart.MinecartBehavior) (Object) this).minecart; }

    @ModifyReturnValue(method = "getSlowdownFactor", at = @At("RETURN"), require = 1)
    private double lunararc$friction(double factor) {
        io.lunararcdevs.lunararc.common.bridge.entity.MinecartFrictionBridge bridge = (io.lunararcdevs.lunararc.common.bridge.entity.MinecartFrictionBridge) (Object) this.lunararc$cartFriction();
        return bridge.lunararc$frictionState() == TriState.FALSE ? 1.0D : factor;
    }
}
