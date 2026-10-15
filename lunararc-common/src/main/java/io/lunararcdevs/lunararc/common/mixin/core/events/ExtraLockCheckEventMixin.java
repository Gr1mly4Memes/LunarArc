package io.lunararcdevs.lunararc.common.mixin.core.events;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import io.lunararcdevs.lunararc.common.event.LunarArcBlockEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;

/** BlockLockCheckEvent: a plugin may open or keep shut a locked container, with its own message and sound. */
@Mixin(BaseContainerBlockEntity.class)
public abstract class ExtraLockCheckEventMixin {
    @WrapMethod(method = "canOpen", require = 0)
    private boolean lunararc$lockCheck(Player player, Operation<Boolean> original) {
        Boolean decided = LunarArcBlockEvents.lockCheck((BaseContainerBlockEntity) (Object) this,
                ((BaseContainerBlockEntity) (Object) this).getDisplayName(), player);
        return decided != null ? decided : original.call(player);
    }

    @WrapMethod(method = "sendChestLockedNotifications", require = 0)
    private static void lunararc$lockedNotification(Vec3 pos, Player player, Component displayName, Operation<Void> original) {
        if (LunarArcBlockEvents.lockedNotification(pos)) return;
        original.call(pos, player, displayName);
    }
}
