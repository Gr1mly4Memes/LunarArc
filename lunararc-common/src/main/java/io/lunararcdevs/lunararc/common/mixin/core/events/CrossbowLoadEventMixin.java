package io.lunararcdevs.lunararc.common.mixin.core.events;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.lunararcdevs.lunararc.common.bridge.EntityBridge;
import io.papermc.paper.event.entity.EntityLoadCrossbowEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/** Paper's EntityLoadCrossbowEvent, fired when a crossbow finishes charging and is about to take its ammunition. */
@Mixin(CrossbowItem.class)
public abstract class CrossbowLoadEventMixin {
    @WrapOperation(method = "onUseTick", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/item/CrossbowItem;tryLoadProjectiles(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;)Z"))
    private static boolean lunararc$load(LivingEntity shooter, ItemStack crossbow, Operation<Boolean> original) {
        org.bukkit.entity.LivingEntity bukkit = (org.bukkit.entity.LivingEntity) ((EntityBridge) shooter).lunararc$getBukkitEntity();
        org.bukkit.inventory.EquipmentSlot hand = shooter.getUsedItemHand() == InteractionHand.OFF_HAND
                ? org.bukkit.inventory.EquipmentSlot.OFF_HAND : org.bukkit.inventory.EquipmentSlot.HAND;
        EntityLoadCrossbowEvent event = new EntityLoadCrossbowEvent(bukkit, CraftItemStack.asCraftMirror(crossbow), hand);
        if (!event.callEvent()) return false;
        return original.call(shooter, crossbow);
    }
}
