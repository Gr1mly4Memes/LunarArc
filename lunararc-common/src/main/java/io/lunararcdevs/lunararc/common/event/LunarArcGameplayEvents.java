package io.lunararcdevs.lunararc.common.event;

import io.lunararcdevs.lunararc.common.LunarArcServerAccess;
import io.lunararcdevs.lunararc.common.bridge.EntityBridge;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/** Paper gameplay events added alongside the server-side set: collisions, golem construction, age locking, bundles, lunging, item sorting. */
public final class LunarArcGameplayEvents {
    private LunarArcGameplayEvents() {
    }

    private static boolean listened(HandlerList handlers) {
        return handlers.getRegisteredListeners().length != 0;
    }

    private static boolean call(Event event) {
        LunarArcServerAccess.getCraftServer().getPluginManager().callEvent(event);
        return !(event instanceof Cancellable cancellable) || !cancellable.isCancelled();
    }

    private static org.bukkit.entity.Entity bukkit(Entity entity) {
        return entity != null && ((EntityBridge) entity).lunararc$getBukkitEntity() instanceof org.bukkit.entity.Entity found ? found : null;
    }

    /** PlayerInventorySlotChangeEvent for a slot backed by the player's own inventory. */
    public static void inventorySlotChanged(net.minecraft.world.inventory.Slot slot, int rawSlot, ItemStack previous, ItemStack current) {
        if (!listened(io.papermc.paper.event.player.PlayerInventorySlotChangeEvent.getHandlerList())) return;
        if (slot instanceof net.minecraft.world.inventory.ResultSlot
                || !(slot.container instanceof net.minecraft.world.entity.player.Inventory inventory)
                || !(inventory.player instanceof ServerPlayer player)
                || !(bukkit(player) instanceof org.bukkit.entity.Player bukkitPlayer)) return;
        call(new io.papermc.paper.event.player.PlayerInventorySlotChangeEvent(bukkitPlayer, rawSlot,
                CraftItemStack.asBukkitCopy(previous), CraftItemStack.asBukkitCopy(current)));
    }

    // PlayerNaturallySpawnCreaturesEvent -------------------------------------------------------------------------------

    /** Returned by {@link #naturalSpawnRadius} when no plugin listens, so vanilla's own range applies. */
    public static final int NO_SPAWN_EVENT = Integer.MIN_VALUE;

    private static final java.util.Map<ServerPlayer, com.destroystokyo.paper.event.entity.PlayerNaturallySpawnCreaturesEvent> SPAWN_EVENTS =
            java.util.Collections.synchronizedMap(new java.util.WeakHashMap<>());

    /** Fires the event for every player of the level; the results are read back while the chunks of that tick are examined. */
    public static void fireNaturalSpawn(net.minecraft.server.level.ServerLevel level) {
        if (!listened(com.destroystokyo.paper.event.entity.PlayerNaturallySpawnCreaturesEvent.getHandlerList())) {
            SPAWN_EVENTS.clear();
            return;
        }
        for (ServerPlayer player : level.players()) {
            if (!(bukkit(player) instanceof org.bukkit.entity.Player bukkitPlayer)) continue;
            int range = Math.min(Math.min(8, level.getServer().getPlayerList().getViewDistance()), bukkitPlayer.getViewDistance());
            com.destroystokyo.paper.event.entity.PlayerNaturallySpawnCreaturesEvent event =
                    new com.destroystokyo.paper.event.entity.PlayerNaturallySpawnCreaturesEvent(bukkitPlayer, (byte) range);
            call(event);
            SPAWN_EVENTS.put(player, event);
        }
    }

    /** @return the player's spawn radius in chunks, -1 when a plugin cancelled spawning around them, or {@link #NO_SPAWN_EVENT} */
    public static int naturalSpawnRadius(ServerPlayer player) {
        com.destroystokyo.paper.event.entity.PlayerNaturallySpawnCreaturesEvent event = SPAWN_EVENTS.get(player);
        if (event == null) return NO_SPAWN_EVENT;
        return event.isCancelled() ? -1 : event.getSpawnRadius();
    }
}
