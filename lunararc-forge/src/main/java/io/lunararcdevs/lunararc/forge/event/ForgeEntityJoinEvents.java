package io.lunararcdevs.lunararc.forge.event;

import io.lunararcdevs.lunararc.common.event.LunarArcNativeEntityEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;

public final class ForgeEntityJoinEvents {
    private ForgeEntityJoinEvents() {}

    public static void register() {
        EntityJoinLevelEvent.BUS.addListener((java.util.function.Predicate<EntityJoinLevelEvent>) ForgeEntityJoinEvents::onJoin);
    }

    private static boolean onJoin(EntityJoinLevelEvent forgeEvent) {
        if (forgeEvent.loadedFromDisk()
                || !(forgeEvent.getLevel() instanceof ServerLevel)
                || forgeEvent.getEntity() instanceof ServerPlayer) {
            return false;
        }
        return LunarArcNativeEntityEvents.fireFreshEntityJoin(forgeEvent.getEntity());
    }
}
