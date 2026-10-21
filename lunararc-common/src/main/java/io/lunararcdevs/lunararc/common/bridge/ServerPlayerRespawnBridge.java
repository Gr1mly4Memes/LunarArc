package io.lunararcdevs.lunararc.common.bridge;

import net.minecraft.world.level.portal.TeleportTransition;

/** Narrow vanilla 1.21.11 respawn bridge implemented by ServerPlayerMixin. */
public interface ServerPlayerRespawnBridge {
    TeleportTransition lunararc$findRespawnPositionAndUseSpawnBlock(
            boolean keepEverything,
            TeleportTransition.PostTeleportTransition postTransition);
}
