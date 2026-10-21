package io.lunararcdevs.lunararc.common.mixin.core.server;

import io.lunararcdevs.lunararc.common.event.LunarArcServerSideEvents;
import net.minecraft.network.protocol.game.ClientboundCommandSuggestionsPacket;
import net.minecraft.network.protocol.game.ServerboundCommandSuggestionPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** AsyncTabCompleteEvent and AsyncPlayerSendSuggestionsEvent for a player's tab-completion requests. */
@Mixin(ServerGamePacketListenerImpl.class)
public abstract class ServerSideTabCompleteMixin {
    @Shadow public ServerPlayer player;

    @Inject(method = "handleCustomCommandSuggestions", at = @At("HEAD"), cancellable = true, require = 0)
    private void lunararc$tabComplete(ServerboundCommandSuggestionPacket packet, CallbackInfo ci) {
        boolean listened = com.destroystokyo.paper.event.server.AsyncTabCompleteEvent.getHandlerList().getRegisteredListeners().length != 0
                || com.destroystokyo.paper.event.brigadier.AsyncPlayerSendSuggestionsEvent.getHandlerList().getRegisteredListeners().length != 0;
        if (!listened) return;
        int id = packet.getId();
        String command = packet.getCommand();
        LunarArcServerSideEvents.tabComplete(this.player, command, () -> {
                    net.minecraft.commands.Commands commands = this.player.level().getServer().getCommands();
                    com.mojang.brigadier.StringReader reader = new com.mojang.brigadier.StringReader(command);
                    if (reader.canRead() && reader.peek() == '/') reader.skip();
                    return commands.getDispatcher().getCompletionSuggestions(commands.getDispatcher().parse(reader, this.player.createCommandSourceStack()));
                },
                suggestions -> this.player.connection.send(new ClientboundCommandSuggestionsPacket(id, suggestions)));
        ci.cancel();
    }
}
