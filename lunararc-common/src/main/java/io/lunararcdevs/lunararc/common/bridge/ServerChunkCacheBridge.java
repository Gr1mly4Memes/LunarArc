package io.lunararcdevs.lunararc.common.bridge;

/** Paper's "allow animals" spawn flag, carried on the loader-owned ServerChunkCache. */
public interface ServerChunkCacheBridge {
    boolean lunararc$isSpawnFriendlies();
    void lunararc$setSpawnFriendlies(boolean spawnFriendlies);
}
