package io.lunararcdevs.lunararc.common.mixin.core.item;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import io.lunararcdevs.lunararc.common.event.LunarArcPaperEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.entity.projectile.arrow.ThrownTrident;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Paper's PlayerLaunchProjectileEvent. Since 1.21.5 every thrown item (egg, snowball, pearl, bottle, potion, wind charge,
 * trident) spawns through Projectile#spawnProjectile; arrows, fireworks and hooks have dedicated events elsewhere.
 */
@Mixin(Projectile.class)
public abstract class ThrownItemsMixin {
    @WrapOperation(method = "spawnProjectile(Lnet/minecraft/world/entity/projectile/Projectile;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/item/ItemStack;Ljava/util/function/Consumer;)Lnet/minecraft/world/entity/projectile/Projectile;", require = 1,
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"))
    private static boolean lunararc$launch(ServerLevel level, Entity projectile, Operation<Boolean> original,
            @Local(argsOnly = true) ItemStack stack) {
        boolean dedicated = projectile instanceof AbstractArrow && !(projectile instanceof ThrownTrident)
                || projectile instanceof FireworkRocketEntity || projectile instanceof FishingHook;
        if (dedicated) return original.call(level, projectile);
        return !LunarArcPaperEvents.launchProjectile(projectile, stack) && original.call(level, projectile);
    }
}
