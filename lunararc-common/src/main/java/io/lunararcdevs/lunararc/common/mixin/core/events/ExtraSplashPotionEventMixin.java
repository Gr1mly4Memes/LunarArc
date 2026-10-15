package io.lunararcdevs.lunararc.common.mixin.core.events;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import io.lunararcdevs.lunararc.common.event.LunarArcPotionEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.projectile.throwableitemprojectile.AbstractThrownPotion;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrownSplashPotion;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;

/** PotionSplashEvent for splash potions; with no listener vanilla runs untouched. */
@Mixin(ThrownSplashPotion.class)
public abstract class ExtraSplashPotionEventMixin {
    @WrapMethod(method = "onHitAsPotion", require = 0)
    private void lunararc$splashEvent(ServerLevel level, ItemStack stack, HitResult hit, Operation<Void> original) {
        if (LunarArcPotionEvents.splash((AbstractThrownPotion) (Object) this, level, stack, hit)) return;
        original.call(level, stack, hit);
    }
}
