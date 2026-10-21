package io.lunararcdevs.lunararc.common.mixin.core.server;

import io.lunararcdevs.lunararc.common.bridge.EntityBridge;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/** Paper's ServerGamePacketListenerImpl#getCraftPlayer, which its game-connection API class calls. */
@Mixin(ServerGamePacketListenerImpl.class)
public abstract class ServerGameListenerPaperMixin {
    @Shadow public ServerPlayer player;

    public CraftPlayer getCraftPlayer() {
        return (CraftPlayer) ((EntityBridge) this.player).lunararc$getBukkitEntity();
    }
}
