package io.lunararcdevs.lunararc.common.mixin.core.entity;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import io.lunararcdevs.lunararc.common.bridge.PlayerShieldBridge;
import io.lunararcdevs.lunararc.common.event.LunarArcPaperEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BlocksAttacks;
import org.spongepowered.asm.mixin.Mixin;

/** PlayerShieldDisableEvent: shield disabling lives in the BlocksAttacks component since 1.21.5. */
@Mixin(BlocksAttacks.class)
public abstract class BlocksAttacksDisableMixin {
    @WrapMethod(method = "disable", require = 1)
    private void lunararc$disableShield(ServerLevel level, LivingEntity entity, float duration, ItemStack stack, Operation<Void> original) {
        if (entity instanceof Player player && player instanceof PlayerShieldBridge bridge && bridge.lunararc$getShieldAttacker() != null) {
            int cooldown = LunarArcPaperEvents.shieldDisable(player, bridge.lunararc$getShieldAttacker());
            if (cooldown != 100) {
                if (cooldown >= 0) {
                    player.getCooldowns().addCooldown(stack, cooldown);
                    player.stopUsingItem();
                    level.broadcastEntityEvent(player, (byte) 30);
                }
                return;
            }
        }
        original.call(level, entity, duration, stack);
    }
}
