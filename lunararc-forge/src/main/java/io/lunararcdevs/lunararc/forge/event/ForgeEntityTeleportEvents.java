package io.lunararcdevs.lunararc.forge.event;

import io.lunararcdevs.lunararc.common.event.LunarArcNativeEntityEvents;
import net.minecraftforge.event.entity.EntityTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public final class ForgeEntityTeleportEvents {
    private ForgeEntityTeleportEvents() {}

    public static void register() {
        EntityTeleportEvent.BUS.addListener((java.util.function.Predicate<EntityTeleportEvent>) ForgeEntityTeleportEvents::onTeleport);
    }

    private static boolean onTeleport(EntityTeleportEvent forgeEvent) {
        LunarArcNativeEntityEvents.TeleportResult result = LunarArcNativeEntityEvents.fireNativeTeleport(
                forgeEvent.getEntity(),
                forgeEvent.getTargetX(),
                forgeEvent.getTargetY(),
                forgeEvent.getTargetZ(),
                cause(forgeEvent));
        if (result.cancelled()) {
            return true;
        }
        forgeEvent.setTargetX(result.x());
        forgeEvent.setTargetY(result.y());
        forgeEvent.setTargetZ(result.z());
        return false;
    }

    private static PlayerTeleportEvent.TeleportCause cause(EntityTeleportEvent event) {
        if (event instanceof EntityTeleportEvent.EnderPearl) return PlayerTeleportEvent.TeleportCause.ENDER_PEARL;
        if (event instanceof EntityTeleportEvent.ChorusFruit) return PlayerTeleportEvent.TeleportCause.CHORUS_FRUIT;
        if (event instanceof EntityTeleportEvent.TeleportCommand || event instanceof EntityTeleportEvent.SpreadPlayersCommand) {
            return PlayerTeleportEvent.TeleportCause.COMMAND;
        }
        return PlayerTeleportEvent.TeleportCause.UNKNOWN;
    }
}
