package io.lunararcdevs.lunararc.common.mixin.core.events;

import io.lunararcdevs.lunararc.common.event.LunarArcExtraEvents;
import java.util.EnumMap;
import java.util.Map;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** EntityEquipmentChangedEvent and PlayerArmorChangeEvent, fired when vanilla detects an equipment change. */
@Mixin(LivingEntity.class)
public abstract class ExtraEquipmentEventsMixin {
    @Shadow @Final private Map<EquipmentSlot, ItemStack> lastEquipmentItems;

    // The old stacks are still in lastEquipmentItems here; vanilla refreshes them after this method returns.
    @Inject(method = "collectEquipmentChanges", at = @At("RETURN"), require = 0)
    private void lunararc$equipmentChanged(CallbackInfoReturnable<Map<EquipmentSlot, ItemStack>> cir) {
        Map<EquipmentSlot, ItemStack> added = cir.getReturnValue();
        if (added == null) return;
        Map<EquipmentSlot, ItemStack> previous = new EnumMap<>(EquipmentSlot.class);
        for (EquipmentSlot slot : added.keySet()) previous.put(slot, this.lastEquipmentItems.get(slot));
        LunarArcExtraEvents.equipmentChanged((LivingEntity) (Object) this, added, previous);
    }
}
