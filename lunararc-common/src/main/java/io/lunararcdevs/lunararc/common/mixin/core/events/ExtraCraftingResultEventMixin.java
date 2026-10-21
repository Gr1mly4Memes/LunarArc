package io.lunararcdevs.lunararc.common.mixin.core.events;

import io.lunararcdevs.lunararc.common.event.LunarArcExtraEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** ItemCraftedEvent, fired when a player takes (or shift-crafts) a crafting result. */
@Mixin(ResultSlot.class)
public abstract class ExtraCraftingResultEventMixin {
    @Shadow @Final private Player player;

    @Inject(method = "onQuickCraft", at = @At("TAIL"), require = 0)
    private void lunararc$quickCrafted(ItemStack stack, int amount, CallbackInfo ci) {
        LunarArcExtraEvents.itemCrafted(this.player, stack);
    }

    @Inject(method = "onTake", at = @At("HEAD"), require = 0)
    private void lunararc$taken(Player taker, ItemStack stack, CallbackInfo ci) {
        LunarArcExtraEvents.itemCrafted(taker, stack);
    }
}
