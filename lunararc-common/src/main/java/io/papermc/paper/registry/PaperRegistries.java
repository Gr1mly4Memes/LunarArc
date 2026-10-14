package io.papermc.paper.registry;

import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;

public final class PaperRegistries {
    private PaperRegistries() {
    }

    private static volatile java.util.Map<Identifier, RegistryKey<?>> byNmsKey;

    /** Resolve the API {@link RegistryKey} for a vanilla registry key; the API exposes them as constants of {@link RegistryKey}. */
    @SuppressWarnings("unchecked")
    public static <T extends org.bukkit.Keyed> RegistryKey<T> registryFromNms(ResourceKey<? extends net.minecraft.core.Registry<?>> nmsKey) {
        java.util.Map<Identifier, RegistryKey<?>> map = byNmsKey;
        if (map == null) {
            java.util.Map<Identifier, RegistryKey<?>> built = new java.util.HashMap<>();
            for (java.lang.reflect.Field field : RegistryKey.class.getFields()) {
                if (!java.lang.reflect.Modifier.isStatic(field.getModifiers()) || !RegistryKey.class.isAssignableFrom(field.getType())) continue;
                try {
                    RegistryKey<?> constant = (RegistryKey<?>) field.get(null);
                    built.put(Identifier.parse(constant.key().asString()), constant);
                } catch (ReflectiveOperationException ignored) {
                }
            }
            byNmsKey = map = built;
        }
        RegistryKey<?> key = map.get(nmsKey.identifier());
        if (key == null) throw new IllegalArgumentException("No API registry key for " + nmsKey);
        return (RegistryKey<T>) key;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <T> ResourceKey registryToNms(RegistryKey<T> key) {
        return ResourceKey.createRegistryKey(Identifier.parse(key.key().asString()));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <M, T> io.papermc.paper.registry.TypedKey<T> fromNms(final ResourceKey<M> resourceKey) {
        return io.papermc.paper.registry.TypedKey.create((RegistryKey) registryFromNms((ResourceKey) resourceKey.registryKey()),
                org.bukkit.craftbukkit.util.CraftNamespacedKey.fromMinecraft(resourceKey.identifier()));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <M, T> ResourceKey<M> toNms(final io.papermc.paper.registry.TypedKey<T> typedKey) {
        return ResourceKey.create((ResourceKey) registryToNms(typedKey.registryKey()),
                io.papermc.paper.adventure.PaperAdventure.asVanilla(typedKey.key()));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <M, T> io.papermc.paper.registry.tag.TagKey<T> fromNms(final net.minecraft.tags.TagKey<M> tagKey) {
        return io.papermc.paper.registry.tag.TagKey.create((RegistryKey) registryFromNms((ResourceKey) tagKey.registry()),
                org.bukkit.craftbukkit.util.CraftNamespacedKey.fromMinecraft(tagKey.location()));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <M, T> net.minecraft.tags.TagKey<M> toNms(final io.papermc.paper.registry.tag.TagKey<T> tagKey) {
        return net.minecraft.tags.TagKey.create((ResourceKey) registryToNms(tagKey.registryKey()),
                io.papermc.paper.adventure.PaperAdventure.asVanilla(tagKey.key()));
    }
}
