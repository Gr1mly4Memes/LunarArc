package io.lunararcdevs.lunararc.common.mixin.core.item;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import io.lunararcdevs.lunararc.common.bridge.EntityBridge;
import java.util.function.Consumer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/** EntityShootBowEvent for bows and crossbows: runs after the vanilla adapter has set the launch velocity. */
@Mixin(ProjectileWeaponItem.class)
public abstract class ProjectileWeaponItemMixin {
    @WrapOperation(method = "shoot", require = 1,
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/Projectile;spawnProjectile(Lnet/minecraft/world/entity/projectile/Projectile;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/item/ItemStack;Ljava/util/function/Consumer;)Lnet/minecraft/world/entity/projectile/Projectile;"))
    private Projectile lunararc$shootBow(Projectile projectile, ServerLevel level, ItemStack ammo, Consumer<Projectile> adapter,
            Operation<Projectile> original,
            @Local(argsOnly = true, ordinal = 0) LivingEntity shooter, @Local(argsOnly = true) InteractionHand hand,
            @Local(argsOnly = true) ItemStack weapon, @Local(argsOnly = true, ordinal = 0) float velocity) {
        if (!org.bukkit.Bukkit.isPrimaryThread()) return original.call(projectile, level, ammo, adapter);
        adapter.accept(projectile);
        var event = CraftEventFactory.callEntityShootBowEvent(shooter, weapon, ammo, projectile, hand, velocity, true);
        if (event.isCancelled()) {
            event.getProjectile().remove();
            return projectile;
        }
        if (event.getProjectile() != ((EntityBridge) projectile).lunararc$getBukkitEntity()) {
            return projectile; // a plugin replaced the projectile and spawned its own
        }
        return original.call(projectile, level, ammo, (Consumer<Projectile>) p -> {});
    }
}
