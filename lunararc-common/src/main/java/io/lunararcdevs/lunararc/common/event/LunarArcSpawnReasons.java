package io.lunararcdevs.lunararc.common.event;

import org.bukkit.event.entity.CreatureSpawnEvent;

public final class LunarArcSpawnReasons {
    private static final ThreadLocal<CreatureSpawnEvent.SpawnReason> OVERRIDE = new ThreadLocal<>();

    private LunarArcSpawnReasons() {}

    public static void set(CreatureSpawnEvent.SpawnReason reason) {
        if (reason == null) OVERRIDE.remove();
        else OVERRIDE.set(reason);
    }

    public static CreatureSpawnEvent.SpawnReason get() {
        return OVERRIDE.get();
    }
}
