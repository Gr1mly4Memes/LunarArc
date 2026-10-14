package io.lunararcdevs.lunararc.common.mixin.core.patched;

import net.minecraft.world.item.ItemStack;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.spongepowered.asm.mixin.Mixin;

/** Paper's ItemStack#asBukkitCopy, used by donated CraftBukkit classes. */
@Mixin(ItemStack.class)
public abstract class ItemStackBukkitCopyMixin {
    public org.bukkit.inventory.ItemStack asBukkitCopy() {
        return CraftItemStack.asBukkitCopy((ItemStack) (Object) this);
    }
}
