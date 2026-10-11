package io.lunararcdevs.lunararc.common.mixin.core.world.blockentity;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import io.lunararcdevs.lunararc.common.event.LunarArcMoreEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BeaconBlockEntity.class)
public abstract class BeaconEffectEventMixin {
    @WrapOperation(method = "applyEffects", require = 0,
            at = @At(value = "INVOKE", ordinal = 0, target = "Lnet/minecraft/world/entity/player/Player;addEffect(Lnet/minecraft/world/effect/MobEffectInstance;)Z"))
    private static boolean lunararc$primaryEffect(Player player, MobEffectInstance effect, Operation<Boolean> original,
            @Local(argsOnly = true) Level level, @Local(argsOnly = true) BlockPos pos) {
        MobEffectInstance applied = LunarArcMoreEvents.beaconEffect(level, pos, player, effect, true);
        return applied != null && original.call(player, applied);
    }

    @WrapOperation(method = "applyEffects", require = 0,
            at = @At(value = "INVOKE", ordinal = 1, target = "Lnet/minecraft/world/entity/player/Player;addEffect(Lnet/minecraft/world/effect/MobEffectInstance;)Z"))
    private static boolean lunararc$secondaryEffect(Player player, MobEffectInstance effect, Operation<Boolean> original,
            @Local(argsOnly = true) Level level, @Local(argsOnly = true) BlockPos pos) {
        MobEffectInstance applied = LunarArcMoreEvents.beaconEffect(level, pos, player, effect, false);
        return applied != null && original.call(player, applied);
    }
}
