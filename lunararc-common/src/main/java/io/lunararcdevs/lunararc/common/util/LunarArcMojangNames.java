package io.lunararcdevs.lunararc.common.util;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;

/**
 * Mojang-named accessors for loader modules whose compile classpath uses SRG names (Forge), so they cannot
 * reference these members directly.
 */
public final class LunarArcMojangNames {
    private LunarArcMojangNames() {}

    public static ServerLevel level(ServerPlayer player) { return player.level(); }

    public static InteractionResult pass() { return InteractionResult.PASS; }
}
