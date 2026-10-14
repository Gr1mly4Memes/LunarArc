package io.lunararcdevs.lunararc.common.mixin.core.server;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.authlib.GameProfile;
import io.lunararcdevs.lunararc.common.bridge.ConnectionBridge;
import net.minecraft.network.Connection;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Paper creates the ServerPlayer at login so PlayerLoginEvent can carry it. Vanilla now builds it in the spawn task, so hand the
 * login-phase instance (kept on the connection) back instead of constructing a second one.
 */
@Mixin(targets = "net.minecraft.server.network.config.PrepareSpawnTask$Ready")
public abstract class PrepareSpawnReadyMixin {
    @WrapOperation(method = "spawn", require = 1,
            at = @At(value = "NEW", target = "(Lnet/minecraft/server/MinecraftServer;Lnet/minecraft/server/level/ServerLevel;Lcom/mojang/authlib/GameProfile;Lnet/minecraft/server/level/ClientInformation;)Lnet/minecraft/server/level/ServerPlayer;", remap = false))
    private ServerPlayer lunararc$reuseLoginPlayer(MinecraftServer server, ServerLevel level, GameProfile profile, ClientInformation information,
            Operation<ServerPlayer> original, @Local(argsOnly = true) Connection connection) {
        ConnectionBridge bridge = (ConnectionBridge) connection;
        ServerPlayer loginPlayer = bridge.lunararc$getLoginPlayer();
        if (loginPlayer == null) return original.call(server, level, profile, information);
        loginPlayer.setServerLevel(level);
        loginPlayer.updateOptions(information);
        return loginPlayer;
    }
}
