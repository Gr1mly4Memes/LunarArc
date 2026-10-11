package io.lunararcdevs.lunararc.common.mixin.core.entity;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.lunararcdevs.lunararc.common.server.LunarArcServerSettings;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Phantom;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "net.minecraft.world.entity.monster.Phantom$PhantomAttackPlayerTargetGoal", remap = false)
public abstract class PhantomTargetInsomniacMixin {
    @WrapOperation(method = "canUse", require = 0, at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/monster/Phantom;setTarget(Lnet/minecraft/world/entity/LivingEntity;)V"))
    private void lunararc$onlyInsomniacs(Phantom phantom, LivingEntity target, Operation<Void> original) {
        var tuning = LunarArcServerSettings.tuning(phantom.level());
        if (tuning.phantomsOnlyInsomniacs && !(target instanceof ServerPlayer player
                && Mth.clamp(player.getStats().getValue(Stats.CUSTOM.get(Stats.TIME_SINCE_REST)), 1, Integer.MAX_VALUE)
                        >= tuning.insomniaStartTicks)) {
            return;
        }
        original.call(phantom, target);
    }
}
