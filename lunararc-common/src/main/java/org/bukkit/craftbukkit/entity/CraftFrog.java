package org.bukkit.craftbukkit.entity;
import net.minecraft.core.registries.Registries;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.craftbukkit.CraftServer;
public final class CraftFrog extends CraftAnimals implements org.bukkit.entity.Frog {
    public CraftFrog(CraftServer server, net.minecraft.world.entity.animal.frog.Frog entity) { super(server, entity); }
    @Override public net.minecraft.world.entity.animal.frog.Frog getHandle() { return (net.minecraft.world.entity.animal.frog.Frog) entity; }
    @Override public org.bukkit.entity.Entity getTongueTarget() { return getHandle().getTongueTarget().map(e -> CraftEntity.getEntity(server, e)).orElse(null); }
    @Override public void setTongueTarget(org.bukkit.entity.Entity target) {
        if (target == null) getHandle().eraseTongueTarget(); else getHandle().setTongueTarget(((CraftEntity)target).getHandle());
    }
    @Override public Variant getVariant() {
        var key = getHandle().getVariant().unwrapKey().orElseThrow().identifier();
        return Registry.FROG_VARIANT.get(new NamespacedKey(key.getNamespace(), key.getPath()));
    }
    @Override public void setVariant(Variant variant) {
        java.util.Objects.requireNonNull(variant, "variant");
        var reg = getHandle().registryAccess().lookupOrThrow(Registries.FROG_VARIANT);
        var key = net.minecraft.resources.ResourceKey.create(Registries.FROG_VARIANT, net.minecraft.resources.Identifier.fromNamespaceAndPath(variant.getKey().getNamespace(), variant.getKey().getKey()));
        getHandle().setVariant(reg.getOrThrow(key));
    }
    @Override public String toString() { return "CraftFrog"; }

    public static class CraftVariant extends io.papermc.paper.util.OldEnumHolderable<Variant, net.minecraft.world.entity.animal.frog.FrogVariant> implements Variant {
        private static int count = 0;

        public static Variant minecraftHolderToBukkit(net.minecraft.core.Holder<net.minecraft.world.entity.animal.frog.FrogVariant> minecraft) {
            return org.bukkit.craftbukkit.CraftRegistry.minecraftHolderToBukkit(minecraft, net.minecraft.core.registries.Registries.FROG_VARIANT);
        }

        public static net.minecraft.core.Holder<net.minecraft.world.entity.animal.frog.FrogVariant> bukkitToMinecraftHolder(Variant bukkit) {
            return org.bukkit.craftbukkit.CraftRegistry.bukkitToMinecraftHolder(bukkit);
        }

        public CraftVariant(final net.minecraft.core.Holder<net.minecraft.world.entity.animal.frog.FrogVariant> holder) {
            super(holder, count++);
        }
    }
}
