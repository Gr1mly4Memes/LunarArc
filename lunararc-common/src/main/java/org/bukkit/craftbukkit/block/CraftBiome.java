package org.bukkit.craftbukkit.block;

import io.papermc.paper.util.OldEnumHolderable;
import java.util.Objects;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Biome;
import org.bukkit.craftbukkit.CraftRegistry;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public class CraftBiome extends OldEnumHolderable<Biome, net.minecraft.world.level.biome.Biome> implements Biome {

    private static int count = 0;

    public static Biome minecraftHolderToBukkit(Holder<net.minecraft.world.level.biome.Biome> minecraft) {
        return CraftRegistry.minecraftHolderToBukkit(minecraft, Registries.BIOME);
    }

    public static @Nullable Holder<net.minecraft.world.level.biome.Biome> bukkitToMinecraftHolder(Biome bukkit) {
        if (bukkit == Biome.CUSTOM) {
            return null;
        }
        return CraftRegistry.bukkitToMinecraftHolder(bukkit);
    }

    // ---- LunarArc helpers kept for callers that convert raw NMS biomes ----

    public static Biome minecraftToBukkit(net.minecraft.world.level.biome.Biome minecraft) {
        if (minecraft == null) throw new IllegalArgumentException("minecraft cannot be null");
        var registry = CraftRegistry.getMinecraftRegistry().lookupOrThrow(Registries.BIOME);
        net.minecraft.resources.Identifier id = registry.getKey(minecraft);
        if (id == null) return Biome.CUSTOM;
        Biome biome = org.bukkit.Registry.BIOME.get(new NamespacedKey(id.getNamespace(), id.getPath()));
        return biome == null ? Biome.CUSTOM : biome;
    }

    public static net.minecraft.world.level.biome.Biome bukkitToMinecraft(Biome bukkit) {
        if (bukkit == null || bukkit == Biome.CUSTOM) return null;
        Holder<net.minecraft.world.level.biome.Biome> holder = bukkitToMinecraftHolder(bukkit);
        return holder == null ? null : holder.value();
    }

    public CraftBiome(final Holder<net.minecraft.world.level.biome.Biome> holder) {
        super(holder, count++);
    }

    /**
     * Implementation for the deprecated, API only, CUSTOM biome.
     * As per {@link #bukkitToMinecraftHolder(Biome)} it cannot be
     * converted into an internal biome and only serves backwards compatibility reasons.
     */
    @Deprecated(forRemoval = true, since = "1.21.5")
    @ApiStatus.ScheduledForRemoval(inVersion = "1.22")
    public static class LegacyCustomBiomeImpl implements Biome {

        private static final NamespacedKey LEGACY_CUSTOM_KEY = new NamespacedKey("minecraft", "custom");
        private final int ordinal;

        public LegacyCustomBiomeImpl() {
            this.ordinal = count++;
        }

        @Override
        public NamespacedKey getKey() {
            return LEGACY_CUSTOM_KEY;
        }

        @Override
        public int compareTo(final Biome other) {
            return this.ordinal - other.ordinal();
        }

        @Override
        public String name() {
            return "CUSTOM";
        }

        @Override
        public int ordinal() {
            return this.ordinal;
        }

        @Override
        public boolean equals(final Object object) {
            if (object == null || getClass() != object.getClass()) return false;
            final LegacyCustomBiomeImpl that = (LegacyCustomBiomeImpl) object;
            return ordinal == that.ordinal;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(ordinal);
        }

        @Override
        public String toString() {
            return "CUSTOM";
        }
    }
}
