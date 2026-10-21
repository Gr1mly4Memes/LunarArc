package io.lunararcdevs.lunararc.common.mixin.core.events;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.lunararcdevs.lunararc.common.bridge.EntityBridge;
import io.lunararcdevs.lunararc.common.bridge.ServerLevelBridge;
import io.papermc.paper.event.entity.ShulkerDuplicateEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.level.Level;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/** A shulker hit by a shulker bullet may spawn a copy: Paper's ShulkerDuplicateEvent, and the BREEDING spawn reason. */
@Mixin(Shulker.class)
public abstract class ShulkerDuplicateEventMixin {
    @WrapOperation(method = "hitByShulkerBullet", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"))
    private boolean lunararc$duplicate(Level level, Entity copy, Operation<Boolean> original) {
        ShulkerDuplicateEvent event = new ShulkerDuplicateEvent(
                (org.bukkit.entity.Shulker) ((EntityBridge) copy).lunararc$getBukkitEntity(),
                (org.bukkit.entity.Shulker) ((EntityBridge) this).lunararc$getBukkitEntity());
        if (!event.callEvent()) return false;
        return level instanceof ServerLevelBridge bridge ? bridge.lunararc$addFreshEntity(copy, CreatureSpawnEvent.SpawnReason.BREEDING) : original.call(level, copy);
    }
}
