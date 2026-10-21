package io.lunararcdevs.lunararc.common.mixin.core.entity;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.lunararcdevs.lunararc.common.bridge.EntityBridge;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.illager.Illusioner;
import net.minecraft.world.entity.monster.skeleton.AbstractSkeleton;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({AbstractSkeleton.class, Illusioner.class})
public abstract class MobShootBowMixin {
    @WrapOperation(method = "performRangedAttack", require = 1,
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/Projectile;spawnProjectileUsingShoot(Lnet/minecraft/world/entity/projectile/Projectile;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/item/ItemStack;DDDFF)Lnet/minecraft/world/entity/projectile/Projectile;"))
    private Projectile lunararc$shootBow(Projectile projectile, ServerLevel level, ItemStack ammo, double x, double y, double z,
            float velocity, float inaccuracy, Operation<Projectile> original) {
        if (!(projectile instanceof AbstractArrow arrow) || !org.bukkit.Bukkit.isPrimaryThread()) {
            return original.call(projectile, level, ammo, x, y, z, velocity, inaccuracy);
        }
        LivingEntity self = (LivingEntity) (Object) this;
        var event = CraftEventFactory.callEntityShootBowEvent(self, self.getMainHandItem(), arrow.getPickupItemStackOrigin(),
                arrow, InteractionHand.MAIN_HAND, 0.8F, true);
        if (event.isCancelled()) {
            event.getProjectile().remove();
            return projectile;
        }
        if (event.getProjectile() != ((EntityBridge) arrow).lunararc$getBukkitEntity()) {
            return projectile;
        }
        return original.call(projectile, level, ammo, x, y, z, velocity, inaccuracy);
    }
}
