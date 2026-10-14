package io.lunararcdevs.lunararc.common.mixin.core.world.block;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.lunararcdevs.lunararc.common.bridge.ServerLevelBridge;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.golem.CopperGolem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.WeatheringCopperGolemStatueBlock;
import net.minecraft.world.level.block.entity.CopperGolemStatueBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/** Scraping a statue back into a golem: EntityChangeBlockEvent for the removed block and the REANIMATE spawn reason. */
@Mixin(WeatheringCopperGolemStatueBlock.class)
public abstract class WeatheringCopperGolemStatueEventsMixin {
    @WrapOperation(method = "useItemOn", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/level/block/entity/CopperGolemStatueBlockEntity;removeStatue(Lnet/minecraft/world/level/block/state/BlockState;)Lnet/minecraft/world/entity/animal/golem/CopperGolem;"))
    private CopperGolem lunararc$remove(CopperGolemStatueBlockEntity statue, BlockState state, Operation<CopperGolem> original,
            ItemStack stack, BlockState stateArg, Level level, BlockPos pos, Player player) {
        BlockState replacement = level.getFluidState(pos).createLegacyBlock();
        if (!CraftEventFactory.callEntityChangeBlockEvent(player, pos, replacement)) return null;
        return original.call(statue, state);
    }

    @WrapOperation(method = "useItemOn", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"))
    private boolean lunararc$reanimate(Level level, Entity golem, Operation<Boolean> original) {
        return ((ServerLevelBridge) level).lunararc$addFreshEntity(golem, CreatureSpawnEvent.SpawnReason.REANIMATE);
    }
}
