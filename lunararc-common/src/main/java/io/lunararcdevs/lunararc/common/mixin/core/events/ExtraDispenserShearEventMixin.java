package io.lunararcdevs.lunararc.common.mixin.core.events;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import io.lunararcdevs.lunararc.common.event.LunarArcBlockEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.ShearsDispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.Shearable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

/**
 * BlockShearEntityEvent: a plugin may stop a dispenser shearing an entity. The dispenser's position is kept while
 * its behaviour runs, since the shearing helper is only given the target position.
 */
@Mixin(ShearsDispenseItemBehavior.class)
public abstract class ExtraDispenserShearEventMixin {
    @Unique private static BlockPos lunararc$dispenser;

    @WrapMethod(method = "execute", require = 0)
    private ItemStack lunararc$remember(BlockSource source, ItemStack item, Operation<ItemStack> original) {
        lunararc$dispenser = source.pos();
        try {
            return original.call(source, item);
        } finally {
            lunararc$dispenser = null;
        }
    }

    @WrapMethod(method = "tryShearEntity", require = 0)
    private static boolean lunararc$shear(ServerLevel level, BlockPos pos, ItemStack stack, Operation<Boolean> original) {
        BlockPos dispenser = lunararc$dispenser;
        if (dispenser == null) return original.call(level, pos, stack);
        for (Entity entity : level.getEntitiesOfClass(Entity.class, new AABB(pos), EntitySelector.NO_SPECTATORS)) {
            if (entity.shearOffAllLeashConnections(null)) return true;
            if (entity instanceof Shearable shearable && shearable.readyForShearing()) {
                if (!LunarArcBlockEvents.dispenserShear(level, dispenser, entity, stack)) continue;
                shearable.shear(level, SoundSource.BLOCKS, stack);
                level.gameEvent(null, GameEvent.SHEAR, pos);
                return true;
            }
        }
        return false;
    }
}
