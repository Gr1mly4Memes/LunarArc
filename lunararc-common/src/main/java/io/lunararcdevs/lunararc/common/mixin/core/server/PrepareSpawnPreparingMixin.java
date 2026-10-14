package io.lunararcdevs.lunararc.common.mixin.core.server;

import io.lunararcdevs.lunararc.common.bridge.ConfigurationConnectionBridge;
import io.lunararcdevs.lunararc.common.bridge.ConnectionBridge;
import io.lunararcdevs.lunararc.common.bridge.EntityBridge;
import io.lunararcdevs.lunararc.common.bridge.PrepareSpawnTaskBridge;
import io.lunararcdevs.lunararc.common.bridge.ServerCommonPacketListenerBridge;
import java.util.concurrent.CompletableFuture;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerConfigurationPacketListenerImpl;
import net.minecraft.server.network.config.PrepareSpawnTask;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftWorld;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Paper decides the spawn location before any chunk is loaded: PlayerSpawnLocationEvent (synchronous, legacy) and
 * AsyncPlayerSpawnLocationEvent (run off the main thread). Vanilla's task then loads the chunks around whatever they chose.
 */
@Mixin(targets = "net.minecraft.server.network.config.PrepareSpawnTask$Preparing", remap = false)
public abstract class PrepareSpawnPreparingMixin {
    @Shadow @Final @Mutable private ServerLevel spawnLevel;
    @Shadow @Final @Mutable private CompletableFuture<Vec3> spawnPosition;
    @Shadow @Final @Mutable private Vec2 spawnAngle;
    @Shadow private CompletableFuture<?> chunkLoadFuture;
    @Unique private PrepareSpawnTask lunararc$task;

    @Unique private boolean lunararc$syncEventFired;
    @Unique private CompletableFuture<Location> lunararc$asyncEvent;
    @Unique private boolean lunararc$asyncEventApplied;

    /** The outer task is a synthetic field whose name differs between mapping environments, so capture it at construction. */
    @Inject(method = "<init>", at = @At("RETURN"), require = 1)
    private void lunararc$captureTask(PrepareSpawnTask task, ServerLevel level, CompletableFuture<Vec3> position, Vec2 angle,
            org.spongepowered.asm.mixin.injection.callback.CallbackInfo ci) {
        this.lunararc$task = task;
    }

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true, require = 1)
    private void lunararc$spawnLocationEvents(CallbackInfoReturnable<Object> cir) {
        if (this.chunkLoadFuture != null || !this.spawnPosition.isDone()) return;
        ServerConfigurationPacketListenerImpl listener = ((PrepareSpawnTaskBridge) this.lunararc$task).lunararc$getListener();
        if (listener == null) return;
        Vec3 position = this.spawnPosition.join();

        if (!this.lunararc$syncEventFired) {
            this.lunararc$syncEventFired = true;
            ServerPlayer player = ((ConnectionBridge) ((ServerCommonPacketListenerBridge) (Object) listener).lunararc$getConnection()).lunararc$getLoginPlayer();
            if (player != null && org.spigotmc.event.player.PlayerSpawnLocationEvent.getHandlerList().getRegisteredListeners().length != 0
                    && ((EntityBridge) player).lunararc$getBukkitEntity() instanceof org.bukkit.entity.Player bukkit) {
                org.spigotmc.event.player.PlayerSpawnLocationEvent event = new org.spigotmc.event.player.PlayerSpawnLocationEvent(bukkit, lunararc$location(position));
                event.callEvent();
                lunararc$apply(event.getSpawnLocation());
                position = this.spawnPosition.join();
            }
        }

        if (!this.lunararc$asyncEventApplied) {
            if (this.lunararc$asyncEvent == null
                    && io.papermc.paper.event.player.AsyncPlayerSpawnLocationEvent.getHandlerList().getRegisteredListeners().length != 0) {
                Location suggested = lunararc$location(position);
                this.lunararc$asyncEvent = CompletableFuture.supplyAsync(() -> {
                    io.papermc.paper.event.player.AsyncPlayerSpawnLocationEvent event = new io.papermc.paper.event.player.AsyncPlayerSpawnLocationEvent(
                            ((ConfigurationConnectionBridge) (Object) listener).lunararc$paperConnection(), suggested, false);
                    event.callEvent();
                    return event.getSpawnLocation();
                }, io.papermc.paper.connection.PaperConfigurationTask.CONFIGURATION_POOL);
            }
            if (this.lunararc$asyncEvent != null) {
                if (!this.lunararc$asyncEvent.isDone()) {
                    cir.setReturnValue(null);
                    return;
                }
                this.lunararc$asyncEventApplied = true;
                lunararc$apply(this.lunararc$asyncEvent.join());
            } else {
                this.lunararc$asyncEventApplied = true;
            }
        }
    }

    @Unique
    private Location lunararc$location(Vec3 position) {
        CraftWorld world = io.lunararcdevs.lunararc.common.LunarArcServerAccess.getCraftWorld(this.spawnLevel);
        return new Location(world, position.x, position.y, position.z, this.spawnAngle.x, this.spawnAngle.y);
    }

    @Unique
    private void lunararc$apply(Location location) {
        if (location == null || !(location.getWorld() instanceof CraftWorld world)) return;
        this.spawnLevel = world.getHandle();
        this.spawnPosition = CompletableFuture.completedFuture(new Vec3(location.getX(), location.getY(), location.getZ()));
        this.spawnAngle = new Vec2(location.getYaw(), location.getPitch());
    }
}
