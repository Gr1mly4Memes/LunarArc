package io.lunararcdevs.lunararc.common.mixin.core.events;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import io.lunararcdevs.lunararc.common.event.LunarArcBlockEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.vibrations.VibrationSystem;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/** BlockReceiveGameEvent: a plugin may let a block listener receive a game event vanilla refused, or refuse one it would accept. */
@Mixin(VibrationSystem.Listener.class)
public abstract class ExtraReceiveGameEventMixin {
    @WrapOperation(method = "handleGameEvent", require = 0,
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/gameevent/vibrations/VibrationSystem$User;canReceiveVibration(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Holder;Lnet/minecraft/world/level/gameevent/GameEvent$Context;)Z"))
    private boolean lunararc$receive(VibrationSystem.User user, ServerLevel level, BlockPos pos, Holder<GameEvent> gameEvent,
            GameEvent.Context context, Operation<Boolean> original, @Local(ordinal = 0) Vec3 listenerPosition) {
        boolean vanilla = original.call(user, level, pos, gameEvent, context);
        return LunarArcBlockEvents.receiveGameEvent(level, listenerPosition, gameEvent, context, vanilla);
    }
}
