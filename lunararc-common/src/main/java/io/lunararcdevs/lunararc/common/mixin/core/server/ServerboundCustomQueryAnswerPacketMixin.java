package io.lunararcdevs.lunararc.common.mixin.core.server;

import io.lunararcdevs.lunararc.common.config.LunarArcConfig;
import io.lunararcdevs.lunararc.common.mod.util.VelocityAnswerPayload;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.login.ServerboundCustomQueryAnswerPacket;
import net.minecraft.network.protocol.login.custom.CustomQueryAnswerPayload;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ServerboundCustomQueryAnswerPacket.class, priority = 500)
public abstract class ServerboundCustomQueryAnswerPacketMixin {
    @Inject(method = "readPayload", at = @At("HEAD"), cancellable = true, require = 0)
    private static void lunararc$keepVelocityPayload(int id, FriendlyByteBuf buf, CallbackInfoReturnable<CustomQueryAnswerPayload> cir) {
        if (id != io.lunararcdevs.lunararc.common.mod.util.VelocitySupport.QUERY_ID || !LunarArcConfig.isVelocityEnabled()) return;
        if (!buf.readBoolean()) {
            cir.setReturnValue(null);
            return;
        }
        int length = buf.readableBytes();
        if (length < 0 || length > 1048576) {
            throw new IllegalArgumentException("Payload may not be larger than 1048576 bytes");
        }
        cir.setReturnValue(new VelocityAnswerPayload(new FriendlyByteBuf(buf.readBytes(length))));
    }
}
