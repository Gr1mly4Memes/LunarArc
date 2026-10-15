package io.lunararcdevs.lunararc.common.mixin.core.events;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import io.lunararcdevs.lunararc.common.event.LunarArcBlockEvents;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.EquipmentDispenseItemBehavior;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;

/** BlockDispenseArmorEvent: a plugin may stop a dispenser equipping armor, or change what it equips. */
@Mixin(EquipmentDispenseItemBehavior.class)
public abstract class ExtraDispenseArmorEventMixin {
    @WrapMethod(method = "dispenseEquipment", require = 0)
    private static boolean lunararc$dispenseArmor(BlockSource source, ItemStack item, Operation<Boolean> original) {
        BlockPos target = source.pos().relative(source.state().getValue(DispenserBlock.FACING));
        List<LivingEntity> candidates = source.level().getEntitiesOfClass(LivingEntity.class, new AABB(target),
                entity -> entity.canEquipWithDispenser(item));
        if (candidates.isEmpty()) return original.call(source, item);

        LivingEntity living = candidates.getFirst();
        EquipmentSlot slot = living.getEquipmentSlotForItem(item);
        ItemStack single = item.copyWithCount(1);
        ItemStack equipped = LunarArcBlockEvents.dispenseArmor(source.level(), source.pos(), single, living);
        if (equipped == null) return false;

        boolean unchanged = ItemStack.matches(single, equipped);
        if (unchanged) {
            return original.call(source, item);
        }
        living.setItemSlot(slot, equipped);
        if (living instanceof Mob mob) {
            mob.setGuaranteedDrop(slot);
            mob.setPersistenceRequired();
        }
        return true;
    }
}
