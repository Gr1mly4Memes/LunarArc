package io.lunararcdevs.lunararc.common.bridge;

/** Small per-mob compatibility state attached directly to the loader-owned NMS Mob. */
public interface MobBridge {
    boolean lunararc$isAware();
    boolean lunararc$shouldDespawnInPeaceful();
    net.kyori.adventure.util.TriState lunararc$getDespawnInPeacefulOverride();
    void lunararc$setDespawnInPeacefulOverride(net.kyori.adventure.util.TriState state);
    void lunararc$setAware(boolean aware);

    void lunararc$pushTargetReason(org.bukkit.event.entity.EntityTargetEvent.TargetReason reason);
    void lunararc$pushTransformReason(org.bukkit.event.entity.EntityTransformEvent.TransformReason reason);
    void lunararc$pushTransformContext(org.bukkit.event.entity.EntityTransformEvent.TransformReason reason, org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason spawnReason);
}
