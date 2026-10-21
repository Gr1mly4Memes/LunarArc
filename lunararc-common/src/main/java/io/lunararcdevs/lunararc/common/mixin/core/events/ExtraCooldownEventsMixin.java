package io.lunararcdevs.lunararc.common.mixin.core.events;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import io.lunararcdevs.lunararc.common.event.LunarArcExtraEvents;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemCooldowns;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ServerItemCooldowns;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

/** PlayerItemCooldownEvent and PlayerItemGroupCooldownEvent for a player's cooldowns. */
@Mixin(ItemCooldowns.class)
public abstract class ExtraCooldownEventsMixin {
    @Shadow public abstract Identifier getCooldownGroup(ItemStack stack);

    @Unique private boolean lunararc$itemEventFired;

    @Unique
    private ServerPlayer lunararc$owner() {
        return (Object) this instanceof ServerItemCooldowns ? ((ServerItemCooldownsAccessor) this).lunararc$getPlayer() : null;
    }

    @WrapMethod(method = "addCooldown(Lnet/minecraft/world/item/ItemStack;I)V", require = 0)
    private void lunararc$itemCooldown(ItemStack stack, int cooldown, Operation<Void> original) {
        ServerPlayer owner = this.lunararc$owner();
        if (owner == null) {
            original.call(stack, cooldown);
            return;
        }
        org.bukkit.Material material = org.bukkit.craftbukkit.inventory.CraftItemStack.asBukkitCopy(stack).getType();
        int ticks = LunarArcExtraEvents.cooldown(owner, material, this.getCooldownGroup(stack), cooldown);
        if (ticks < 0) return;
        this.lunararc$itemEventFired = true;
        try {
            original.call(stack, ticks);
        } finally {
            this.lunararc$itemEventFired = false;
        }
    }

    @WrapMethod(method = "addCooldown(Lnet/minecraft/resources/Identifier;I)V", require = 0)
    private void lunararc$groupCooldown(Identifier group, int cooldown, Operation<Void> original) {
        ServerPlayer owner = this.lunararc$owner();
        if (owner == null || this.lunararc$itemEventFired) {
            original.call(group, cooldown);
            return;
        }
        int ticks = LunarArcExtraEvents.cooldown(owner, null, group, cooldown);
        if (ticks < 0) return;
        original.call(group, ticks);
    }
}
