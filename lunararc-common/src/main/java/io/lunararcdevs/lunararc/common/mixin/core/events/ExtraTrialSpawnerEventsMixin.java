package io.lunararcdevs.lunararc.common.mixin.core.events;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import io.lunararcdevs.lunararc.common.event.LunarArcMobEvents;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.trialspawner.TrialSpawner;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/** TrialSpawnerSpawnEvent and BlockDispenseLootEvent for trial spawners. */
@Mixin(TrialSpawner.class)
public abstract class ExtraTrialSpawnerEventsMixin {
    @WrapOperation(method = "spawnMob", require = 0,
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;tryAddFreshEntityWithPassengers(Lnet/minecraft/world/entity/Entity;)Z"))
    private boolean lunararc$spawn(ServerLevel level, Entity entity, Operation<Boolean> original, @Local(argsOnly = true) BlockPos pos) {
        if (!LunarArcMobEvents.trialSpawn(entity, pos)) return false;
        return original.call(level, entity);
    }

    @WrapMethod(method = "ejectReward", require = 0)
    private void lunararc$eject(ServerLevel level, BlockPos pos, ResourceKey<LootTable> table, Operation<Void> original) {
        LootTable loot = level.getServer().reloadableRegistries().getLootTable(table);
        LootParams params = new LootParams.Builder(level).create(LootContextParamSets.EMPTY);
        ObjectArrayList<ItemStack> items = loot.getRandomItems(params);
        if (items.isEmpty()) {
            original.call(level, pos, table);
            return;
        }
        List<ItemStack> result = LunarArcMobEvents.dispenseLoot(level, pos, null, items, table);
        if (result == null) return;
        for (ItemStack item : result) {
            DefaultDispenseItemBehavior.spawnItem(level, item, 2, Direction.UP, Vec3.atBottomCenterOf(pos).relative(Direction.UP, 1.2));
        }
        level.levelEvent(3014, pos, 0);
    }
}
