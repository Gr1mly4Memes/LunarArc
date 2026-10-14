package io.lunararcdevs.lunararc.common.mod.util;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

public record VelocityForwardQuery(FriendlyByteBuf data) implements net.minecraft.network.protocol.login.custom.CustomQueryPayload {
    @Override
    public @NotNull Identifier id() {
        return VelocitySupport.PLAYER_INFO_CHANNEL;
    }

    @Override
    public void write(@NotNull FriendlyByteBuf buf) {
        buf.writeBytes(this.data.slice());
    }
}
