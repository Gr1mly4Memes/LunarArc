package io.lunararcdevs.lunararc.common.mixin.core.world;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import io.lunararcdevs.lunararc.common.server.LunarArcServerSettings;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(ServerChunkCache.class)
public abstract class ServerChunkCacheSpawnMixin implements io.lunararcdevs.lunararc.common.bridge.ServerChunkCacheBridge {
    /** Public and un-prefixed so Paper classes compiled against patched vanilla keep linking. */
    public boolean spawnFriendlies = true;

    @Override public boolean lunararc$isSpawnFriendlies() { return this.spawnFriendlies; }
    @Override public void lunararc$setSpawnFriendlies(boolean spawnFriendlies) { this.spawnFriendlies = spawnFriendlies; }

    @org.spongepowered.asm.mixin.injection.ModifyArg(method = "tickChunks", index = 1, require = 0,
            at = @org.spongepowered.asm.mixin.injection.At(value = "INVOKE", target = "Lnet/minecraft/world/level/NaturalSpawner;getFilteredSpawningCategories(Lnet/minecraft/world/level/NaturalSpawner$SpawnState;ZZZ)Ljava/util/List;"))
    private boolean lunararc$spawnFriendlies(boolean vanilla) {
        return this.spawnFriendlies;
    }

    @Shadow @Final ServerLevel level;

    @WrapMethod(method = "tickChunks", require = 0)
    private void lunararc$spawnSettings(Operation<Void> original) {
        ServerLevel previous = LunarArcServerSettings.enter(this.level);
        try {
            original.call();
        } finally {
            LunarArcServerSettings.leave(previous);
        }
    }

    @ModifyConstant(method = "tickChunks", constant = @Constant(longValue = 400L), require = 0)
    private long lunararc$animalCadence(long vanilla) {
        return LunarArcServerSettings.animalTicks(vanilla);
    }
}
