package io.lunararcdevs.lunararc.common.mixin.core.item;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import io.lunararcdevs.lunararc.common.bridge.EntityBridge;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.FishingRodItem;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(FishingRodItem.class)
public abstract class FishingRodItemMixin {
    @WrapOperation(method = "use", require = 1,
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/Projectile;spawnProjectile(Lnet/minecraft/world/entity/projectile/Projectile;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/world/entity/projectile/Projectile;"))
    private Projectile lunararc$cast(Projectile entity, ServerLevel level, ItemStack stack, Operation<Projectile> original,
            @Local(argsOnly = true) Player player, @Local(argsOnly = true) InteractionHand hand) {
        if (entity instanceof FishingHook hook
                && ((EntityBridge) player).lunararc$getBukkitEntity() instanceof org.bukkit.entity.Player bukkitPlayer
                && ((EntityBridge) hook).lunararc$getBukkitEntity() instanceof org.bukkit.entity.FishHook bukkitHook) {
            var event = new org.bukkit.event.player.PlayerFishEvent(bukkitPlayer, null, bukkitHook,
                    org.bukkit.craftbukkit.CraftEquipmentSlot.getHand(hand), org.bukkit.event.player.PlayerFishEvent.State.FISHING);
            org.bukkit.Bukkit.getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                player.fishing = null;
                return entity;
            }
        }
        return original.call(entity, level, stack);
    }
}
