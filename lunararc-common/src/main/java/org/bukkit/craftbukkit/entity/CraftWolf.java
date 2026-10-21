package org.bukkit.craftbukkit.entity;

import java.util.Objects;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.animal.wolf.WolfVariant;
import org.bukkit.DyeColor;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.Wolf;

public final class CraftWolf extends CraftTameableAnimal implements Wolf {
    public CraftWolf(CraftServer server, net.minecraft.world.entity.animal.wolf.Wolf entity) {
        super(server, entity);
    }

    @Override
    public net.minecraft.world.entity.animal.wolf.Wolf getHandle() {
        return (net.minecraft.world.entity.animal.wolf.Wolf) this.entity;
    }

    @Override
    public boolean isAngry() {
        return getHandle().isAngry();
    }

    @Override
    public void setAngry(boolean angry) {
        if (angry) getHandle().startPersistentAngerTimer();
        else getHandle().stopBeingAngry();
    }

    @Override
    public DyeColor getCollarColor() {
        return DyeColor.getByWoolData((byte) getHandle().getCollarColor().getId());
    }

    @Override
    public void setCollarColor(DyeColor color) {
        Objects.requireNonNull(color, "color");
        getHandle().setCollarColor(net.minecraft.world.item.DyeColor.byId(color.getWoolData()));
    }

    @Override
    public boolean isWet() {
        return getHandle().isWet;
    }

    @Override
    public float getTailAngle() {
        return getHandle().getTailAngle();
    }

    @Override
    public boolean isInterested() {
        return getHandle().isInterested();
    }

    @Override
    public void setInterested(boolean interested) {
        getHandle().setIsInterested(interested);
    }

    @Override
    public Variant getVariant() {
        Holder<WolfVariant> holder = getHandle().getVariant();
        Identifier location = holder.unwrapKey().orElseThrow(() -> new IllegalStateException("Wolf variant is not registry-backed")).identifier();
        Variant variant = Registry.WOLF_VARIANT.get(new NamespacedKey(location.getNamespace(), location.getPath()));
        if (variant == null) throw new IllegalStateException("No Bukkit wolf variant for " + location);
        return variant;
    }

    @Override
    public void setVariant(Variant variant) {
        Objects.requireNonNull(variant, "variant");
        NamespacedKey key = variant.getKey();
        Identifier location = Identifier.fromNamespaceAndPath(key.getNamespace(), key.getKey());
        ResourceKey<WolfVariant> resourceKey = ResourceKey.create(Registries.WOLF_VARIANT, location);
        Holder.Reference<WolfVariant> holder = server.getServer().registryAccess().lookupOrThrow(Registries.WOLF_VARIANT)
                .get(resourceKey).orElseThrow(() -> new IllegalArgumentException("Unknown wolf variant " + key));
        getHandle().setVariant(holder);
    }


    @Override
    public SoundVariant getSoundVariant() {
        return CraftSoundVariant.minecraftHolderToBukkit(this.getHandle().getSoundVariant());
    }

    @Override
    public void setSoundVariant(SoundVariant soundVariant) {
        com.google.common.base.Preconditions.checkArgument(soundVariant != null, "soundVariant cannot be null");
        this.getHandle().setSoundVariant(CraftSoundVariant.bukkitToMinecraftHolder(soundVariant));
    }

    public static class CraftSoundVariant extends io.papermc.paper.registry.HolderableBase<net.minecraft.world.entity.animal.wolf.WolfSoundVariant> implements SoundVariant {

        public static SoundVariant minecraftHolderToBukkit(net.minecraft.core.Holder<net.minecraft.world.entity.animal.wolf.WolfSoundVariant> minecraft) {
            return org.bukkit.craftbukkit.CraftRegistry.minecraftHolderToBukkit(minecraft, Registries.WOLF_SOUND_VARIANT);
        }

        public static net.minecraft.core.Holder<net.minecraft.world.entity.animal.wolf.WolfSoundVariant> bukkitToMinecraftHolder(SoundVariant bukkit) {
            return org.bukkit.craftbukkit.CraftRegistry.bukkitToMinecraftHolder(bukkit);
        }

        public CraftSoundVariant(final net.minecraft.core.Holder<net.minecraft.world.entity.animal.wolf.WolfSoundVariant> holder) {
            super(holder);
        }
    }

    @Override
    public String toString() {
        return "CraftWolf";
    }

    public static class CraftVariant extends io.papermc.paper.registry.HolderableBase<net.minecraft.world.entity.animal.wolf.WolfVariant> implements Variant {
        public static Variant minecraftHolderToBukkit(net.minecraft.core.Holder<net.minecraft.world.entity.animal.wolf.WolfVariant> minecraft) {
            return org.bukkit.craftbukkit.CraftRegistry.minecraftHolderToBukkit(minecraft, net.minecraft.core.registries.Registries.WOLF_VARIANT);
        }

        public static net.minecraft.core.Holder<net.minecraft.world.entity.animal.wolf.WolfVariant> bukkitToMinecraftHolder(Variant bukkit) {
            return org.bukkit.craftbukkit.CraftRegistry.bukkitToMinecraftHolder(bukkit);
        }

        public CraftVariant(final net.minecraft.core.Holder<net.minecraft.world.entity.animal.wolf.WolfVariant> holder) {
            super(holder);
        }
    }
}
