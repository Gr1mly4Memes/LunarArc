package io.lunararcdevs.lunararc.common.mixin.core.events;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import io.lunararcdevs.lunararc.common.event.LunarArcBlockEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.FurnaceResultSlot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/** Tells the furnace experience hook who took which item, so FurnaceExtractEvent can name them. */
@Mixin(FurnaceResultSlot.class)
public abstract class ExtraFurnaceResultSlotEventMixin {
    @Shadow @Final private Player player;
    @Shadow private int removeCount;

    @WrapMethod(method = "checkTakeAchievements", require = 0)
    private void lunararc$context(ItemStack stack, Operation<Void> original) {
        LunarArcBlockEvents.furnaceStack = stack;
        LunarArcBlockEvents.furnaceAmount = this.removeCount;
        LunarArcBlockEvents.furnacePlayer = this.player instanceof ServerPlayer serverPlayer ? serverPlayer : null;
        try {
            original.call(stack);
        } finally {
            LunarArcBlockEvents.furnaceStack = ItemStack.EMPTY;
            LunarArcBlockEvents.furnaceAmount = 0;
            LunarArcBlockEvents.furnacePlayer = null;
        }
    }
}
