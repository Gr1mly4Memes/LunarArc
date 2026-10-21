package io.papermc.paper;

import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.PalettedContainerFactory;

/**
 * The slice of Paper's FeatureHooks that donated CraftBukkit classes call. Paper's full class wires Moonrise's chunk system,
 * which LunarArc does not ship, so only the vanilla-compatible hook lives here.
 */
public final class FeatureHooks {
    private FeatureHooks() {}

    public static LevelChunkSection createSection(final PalettedContainerFactory palettedContainerFactory, final Level level, final ChunkPos chunkPos, final int chunkSection) {
        return new LevelChunkSection(palettedContainerFactory);
    }
}
