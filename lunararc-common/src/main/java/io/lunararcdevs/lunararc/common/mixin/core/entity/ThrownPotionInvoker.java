package io.lunararcdevs.lunararc.common.mixin.core.entity;

import net.minecraft.world.entity.projectile.throwableitemprojectile.AbstractThrownPotion;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(AbstractThrownPotion.class)
public interface ThrownPotionInvoker extends io.lunararcdevs.lunararc.common.bridge.access.ThrownPotionInvokeBridge {
    @Invoker("onHit")
    void lunararc$invokeOnHit(HitResult result);
}
