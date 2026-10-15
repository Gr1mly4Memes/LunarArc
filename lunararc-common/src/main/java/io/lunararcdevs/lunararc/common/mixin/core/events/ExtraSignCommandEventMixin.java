package io.lunararcdevs.lunararc.common.mixin.core.events;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import io.lunararcdevs.lunararc.common.event.LunarArcExtraEvents;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/** PlayerSignCommandPreprocessEvent: commands run from a sign click now go through a cancellable event. */
@Mixin(SignBlockEntity.class)
public abstract class ExtraSignCommandEventMixin {
    @WrapOperation(method = "executeClickCommandsIfPresent", require = 0,
            at = @At(value = "INVOKE", target = "Lnet/minecraft/commands/Commands;performPrefixedCommand(Lnet/minecraft/commands/CommandSourceStack;Ljava/lang/String;)V"))
    private void lunararc$signCommand(Commands commands, CommandSourceStack source, String command, Operation<Void> original,
            @Local(argsOnly = true) ServerLevel level, @Local(argsOnly = true) Player player,
            @Local(argsOnly = true) BlockPos pos, @Local(argsOnly = true) boolean isFrontText) {
        String result = LunarArcExtraEvents.signCommand(player, level, pos, isFrontText, command);
        if (result == null) return;
        original.call(commands, source, result);
    }
}
