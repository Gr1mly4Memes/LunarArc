package io.lunararcdevs.lunararc.common.mixin.core.events;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import io.lunararcdevs.lunararc.common.event.LunarArcExtraEvents;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.commands.DifficultyCommand;
import net.minecraft.world.Difficulty;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/** WorldDifficultyChangeEvent for the /difficulty command. */
@Mixin(DifficultyCommand.class)
public abstract class ExtraDifficultyEventMixin {
    @WrapOperation(method = "setDifficulty", require = 0,
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;setDifficulty(Lnet/minecraft/world/Difficulty;Z)V"))
    private static void lunararc$difficultyEvent(MinecraftServer server, Difficulty difficulty, boolean forced, Operation<Void> original,
            @Local(argsOnly = true) CommandSourceStack source) {
        original.call(server, difficulty, forced);
        LunarArcExtraEvents.difficultyChanged(source, difficulty);
    }
}
