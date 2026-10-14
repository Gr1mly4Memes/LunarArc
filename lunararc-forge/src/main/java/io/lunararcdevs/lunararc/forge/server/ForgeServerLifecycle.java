package io.lunararcdevs.lunararc.forge.server;

import io.lunararcdevs.lunararc.common.mod.server.LunarArcServer;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;

public final class ForgeServerLifecycle {
    private ForgeServerLifecycle() {}

    public static void register() {
        ServerStartingEvent.BUS.addListener(ForgeServerLifecycle::onServerStarting);
        ServerStoppingEvent.BUS.addListener(ForgeServerLifecycle::onServerStopping);
    }

    private static void onServerStarting(ServerStartingEvent event) {
        LunarArcServer.attach(event.getServer());
    }

    private static void onServerStopping(ServerStoppingEvent event) {
        LunarArcServer.detach(event.getServer());
    }
}
