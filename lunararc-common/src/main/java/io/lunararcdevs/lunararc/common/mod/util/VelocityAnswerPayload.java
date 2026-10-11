package io.lunararcdevs.lunararc.common.mod.util;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.login.custom.CustomQueryAnswerPayload;
import org.jetbrains.annotations.NotNull;

public record VelocityAnswerPayload(FriendlyByteBuf data) implements CustomQueryAnswerPayload {
    @Override
    public void write(@NotNull FriendlyByteBuf buf) {
        buf.writeBytes(this.data.slice());
    }
}
