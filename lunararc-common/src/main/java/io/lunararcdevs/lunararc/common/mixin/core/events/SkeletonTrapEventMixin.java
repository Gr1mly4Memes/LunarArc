package io.lunararcdevs.lunararc.common.mixin.core.events;

import com.destroystokyo.paper.event.entity.SkeletonHorseTrapEvent;
import io.lunararcdevs.lunararc.common.bridge.EntityBridge;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.animal.equine.SkeletonHorse;
import net.minecraft.world.entity.animal.equine.SkeletonTrapGoal;
import org.bukkit.entity.HumanEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Paper's SkeletonHorseTrapEvent, fired when the trap horse springs. */
@Mixin(SkeletonTrapGoal.class)
public abstract class SkeletonTrapEventMixin {
    @Shadow @Final private SkeletonHorse horse;

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void lunararc$trap(CallbackInfo ci) {
        List<HumanEntity> eligible = new ArrayList<>();
        for (net.minecraft.world.entity.player.Player player : this.horse.level().players()) {
            if (player.isSpectator() || !player.isAlive()) continue;
            if (player.distanceToSqr(this.horse) <= 100.0 && ((EntityBridge) player).lunararc$getBukkitEntity() instanceof HumanEntity human) eligible.add(human);
        }
        org.bukkit.entity.SkeletonHorse bukkit = (org.bukkit.entity.SkeletonHorse) ((EntityBridge) this.horse).lunararc$getBukkitEntity();
        if (!new SkeletonHorseTrapEvent(bukkit, eligible).callEvent()) {
            ci.cancel();
        }
    }
}
