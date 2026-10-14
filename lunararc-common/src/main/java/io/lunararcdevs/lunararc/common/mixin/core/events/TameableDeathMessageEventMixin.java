package io.lunararcdevs.lunararc.common.mixin.core.events;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.lunararcdevs.lunararc.common.bridge.EntityBridge;
import io.papermc.paper.adventure.PaperAdventure;
import io.papermc.paper.event.entity.TameableDeathMessageEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.TamableAnimal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/** Paper's TameableDeathMessageEvent: the message a pet's owner receives when it dies. */
@Mixin(TamableAnimal.class)
public abstract class TameableDeathMessageEventMixin {
    @WrapOperation(method = "die", require = 0, at = @At(value = "INVOKE",
            target = "Lnet/minecraft/server/level/ServerPlayer;sendSystemMessage(Lnet/minecraft/network/chat/Component;)V"))
    private void lunararc$deathMessage(ServerPlayer owner, Component message, Operation<Void> original) {
        TameableDeathMessageEvent event = new TameableDeathMessageEvent(
                (org.bukkit.entity.Tameable) ((EntityBridge) this).lunararc$getBukkitEntity(), PaperAdventure.asAdventure(message));
        if (event.callEvent()) original.call(owner, PaperAdventure.asVanilla(event.deathMessage()));
    }
}
