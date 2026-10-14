package io.lunararcdevs.lunararc.common.mixin.core.events;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.lunararcdevs.lunararc.common.bridge.EntityBridge;
import io.papermc.paper.event.entity.ElderGuardianAppearanceEvent;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.monster.ElderGuardian;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/** Paper's ElderGuardianAppearanceEvent: the ghost-guardian curse animation can be cancelled per player. */
@Mixin(ElderGuardian.class)
public abstract class ElderGuardianAppearanceEventMixin {
    @WrapOperation(method = "customServerAiStep", at = @At(value = "INVOKE", target = "Ljava/util/List;forEach(Ljava/util/function/Consumer;)V"))
    private void lunararc$appearance(List<ServerPlayer> players, Consumer<ServerPlayer> animation, Operation<Void> original) {
        org.bukkit.entity.ElderGuardian guardian = (org.bukkit.entity.ElderGuardian) ((EntityBridge) this).lunararc$getBukkitEntity();
        original.call(players, (Consumer<ServerPlayer>) player -> {
            if (new ElderGuardianAppearanceEvent(guardian, (org.bukkit.entity.Player) ((EntityBridge) player).lunararc$getBukkitEntity()).callEvent()) {
                animation.accept(player);
            }
        });
    }
}
