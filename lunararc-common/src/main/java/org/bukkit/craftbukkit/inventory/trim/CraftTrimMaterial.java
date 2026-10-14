package org.bukkit.craftbukkit.inventory.trim;

import net.minecraft.network.chat.contents.TranslatableContents;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/** Concrete trim material backed by the active dynamic Minecraft registry. */
public final class CraftTrimMaterial implements TrimMaterial, org.bukkit.craftbukkit.util.Handleable<net.minecraft.world.item.equipment.trim.TrimMaterial> {
    private final NamespacedKey key;
    private final net.minecraft.world.item.equipment.trim.TrimMaterial handle;

    public CraftTrimMaterial(@NotNull NamespacedKey key,
                             @NotNull net.minecraft.world.item.equipment.trim.TrimMaterial handle) {
        this.key = Objects.requireNonNull(key, "key");
        this.handle = Objects.requireNonNull(handle, "handle");
    }

    public @NotNull net.minecraft.world.item.equipment.trim.TrimMaterial getHandle() { return this.handle; }
    @Override public @NotNull NamespacedKey getKey() { return this.key; }
    @Override public @NotNull String getTranslationKey() {
        if (this.handle.description().getContents() instanceof TranslatableContents translatable) return translatable.getKey();
        return net.minecraft.util.Util.makeDescriptionId(
                "trim_material",
                net.minecraft.resources.Identifier.fromNamespaceAndPath(key.getNamespace(), key.getKey()));
    }
    @Override public net.kyori.adventure.text.Component description() {
        return io.papermc.paper.adventure.PaperAdventure.asAdventure(this.handle.description());
    }
    @Override public String toString() { return "CraftTrimMaterial[" + this.key + "]"; }

    // CraftBukkit's conversion pair for this registry type. Plugins call these directly to cross
    // between the Bukkit handle and the NMS object, so they carry CraftBukkit's names verbatim.
    public static TrimMaterial minecraftToBukkit(net.minecraft.world.item.equipment.trim.TrimMaterial minecraft) {
        return org.bukkit.craftbukkit.CraftRegistry.minecraftToBukkit(minecraft, net.minecraft.core.registries.Registries.TRIM_MATERIAL, org.bukkit.Registry.TRIM_MATERIAL);
    }

    public static TrimMaterial minecraftHolderToBukkit(net.minecraft.core.Holder<net.minecraft.world.item.equipment.trim.TrimMaterial> minecraft) {
        return CraftTrimMaterial.minecraftToBukkit(minecraft.value());
    }

    public static net.minecraft.world.item.equipment.trim.TrimMaterial bukkitToMinecraft(TrimMaterial bukkit) {
        return org.bukkit.craftbukkit.CraftRegistry.bukkitToMinecraft(bukkit);
    }

    public static net.minecraft.core.Holder<net.minecraft.world.item.equipment.trim.TrimMaterial> bukkitToMinecraftHolder(TrimMaterial bukkit) {
        com.google.common.base.Preconditions.checkArgument(bukkit != null);

        net.minecraft.core.Registry<net.minecraft.world.item.equipment.trim.TrimMaterial> registry = org.bukkit.craftbukkit.CraftRegistry.getMinecraftRegistry(net.minecraft.core.registries.Registries.TRIM_MATERIAL);

        if (registry.wrapAsHolder(CraftTrimMaterial.bukkitToMinecraft(bukkit)) instanceof net.minecraft.core.Holder.Reference<net.minecraft.world.item.equipment.trim.TrimMaterial> holder) {
            return holder;
        }

        throw new IllegalArgumentException("No Reference holder found for " + bukkit
                + ", this can happen if a plugin creates its own trim material without properly registering it.");
    }
}
