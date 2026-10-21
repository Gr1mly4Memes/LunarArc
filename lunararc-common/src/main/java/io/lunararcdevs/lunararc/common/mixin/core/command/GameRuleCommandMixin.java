package io.lunararcdevs.lunararc.common.mixin.core.command;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.brigadier.context.CommandContext;
import io.lunararcdevs.lunararc.common.bridge.CommandSourceStackBridge;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.commands.GameRuleCommand;
import net.minecraft.world.level.gamerules.GameRule;
import net.minecraft.world.level.gamerules.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(GameRuleCommand.class)
public abstract class GameRuleCommandMixin {

    /** Fire Paper's WorldGameRuleChangeEvent for {@code /gamerule}; plugins may veto or rewrite the value. */
    @WrapOperation(method = "setRule", require = 0,
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/gamerules/GameRules;set(Lnet/minecraft/world/level/gamerules/GameRule;Ljava/lang/Object;Lnet/minecraft/server/MinecraftServer;)V"))
    private static <T> void lunararc$gameRule(GameRules rules, GameRule<T> rule, T value, MinecraftServer server,
            Operation<Void> original, @Local(argsOnly = true) CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        org.bukkit.command.CommandSender sender = source instanceof CommandSourceStackBridge bridge ? bridge.lunararc$getBukkitSender() : null;
        T accepted = org.bukkit.craftbukkit.event.CraftEventFactory.fireGameRuleChange(rule, value, source.getLevel(), sender);
        if (accepted != null) original.call(rules, rule, accepted, server);
    }
}
