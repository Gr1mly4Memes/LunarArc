package io.lunararcdevs.lunararc.common.mixin.core.entity;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerEntity.class)
public abstract class ServerEntityMixin {
    @Shadow @Final private Entity entity;

    @WrapOperation(method = "sendChanges", require = 1,
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerEntity$Synchronizer;sendToTrackingPlayersAndSelf(Lnet/minecraft/network/protocol/Packet;)V"))
    private void lunararc$velocityEvent(net.minecraft.server.level.ServerEntity.Synchronizer self, Packet<?> packet, Operation<Void> original) {
        if (this.entity instanceof ServerPlayer player) {
            packet = io.lunararcdevs.lunararc.common.event.LunarArcPaperEvents.velocity(player, packet);
            if (packet == null) return;
        }
        original.call(self, packet);
    }
}
