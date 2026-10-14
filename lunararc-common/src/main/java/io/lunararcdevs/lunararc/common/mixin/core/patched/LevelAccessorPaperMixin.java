package io.lunararcdevs.lunararc.common.mixin.core.patched;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

/** CraftBukkit's LevelAccessor#getMinecraftWorld and Paper's non-loading chunk lookup. */
@Mixin(LevelAccessor.class)
public interface LevelAccessorPaperMixin {
    default @Nullable ServerLevel getMinecraftWorld() {
        Object self = this;
        if (self instanceof ServerLevel level) return level;
        if (self instanceof WorldGenLevel generating) return generating.getLevel();
        return null;
    }

    default @Nullable ChunkAccess getChunkIfLoadedImmediately(int x, int z) {
        LevelAccessor self = (LevelAccessor) this;
        if (self instanceof ServerLevel level) return level.getChunkSource().getChunkNow(x, z);
        return self.getChunk(x, z, ChunkStatus.FULL, false);
    }
}
