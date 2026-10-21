package io.lunararcdevs.lunararc.common.compat.worldedit;

import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.progress.LevelLoadListener;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

/**
 * WorldEdit's PaperweightAdapter passes its own PaperweightAdapter$NoOpWorldLoadListener as the
 * level-load listener for the throwaway //regen level - it was compiled against Paper's version
 * of this interface, where onStatusChange(ChunkPos, ChunkStatus) has a default no-op body, so it
 * never overrides it. Vanilla's interface keeps that method abstract, so running that class here
 * throws AbstractMethodError the instant the chunk system calls it - not on construction, but
 * later from a background generation-task thread, which kills that task with nothing left to
 * unstick its queue and wedges //regen forever until the watchdog kills the server (confirmed:
 * this reproduces identically with Sable completely removed, ruling that mod out). Real Paper's
 * server never hits this because their LevelLoadListener default-implements the method;
 * LunarArcIntegratedPatcher retargets WorldEdit's construction of the broken listener to this
 * class instead, which just implements the interface properly.
 */
public final class LunarArcNoOpChunkProgressListener implements LevelLoadListener {
    @Override public void start(Stage stage, int totalChunks) {}
    @Override public void update(Stage stage, int readyChunks, int totalChunks) {}
    @Override public void finish(Stage stage) {}
    @Override public void updateFocus(ResourceKey<Level> dimension, ChunkPos chunkPos) {}
}
