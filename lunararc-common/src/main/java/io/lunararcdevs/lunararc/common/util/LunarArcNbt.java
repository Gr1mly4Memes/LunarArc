package io.lunararcdevs.lunararc.common.util;

import java.util.UUID;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.Nullable;

/** UUID helpers for CompoundTag, which lost putUUID/getUUID/hasUUID in 1.21.5 (UUIDs are int arrays now). */
public final class LunarArcNbt {
    private LunarArcNbt() {}

    public static void putUUID(CompoundTag tag, String key, UUID uuid) {
        tag.putIntArray(key, UUIDUtil.uuidToIntArray(uuid));
    }

    public static boolean hasUUID(CompoundTag tag, String key) {
        return tag.getIntArray(key).filter(array -> array.length == 4).isPresent();
    }

    public static @Nullable UUID getUUID(CompoundTag tag, String key) {
        return tag.getIntArray(key).filter(array -> array.length == 4).map(UUIDUtil::uuidFromIntArray).orElse(null);
    }
}
