package io.lunararcdevs.lunararc.common.mixin.core.events;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import io.lunararcdevs.lunararc.common.bridge.MerchantBukkitBridge;
import io.lunararcdevs.lunararc.common.event.LunarArcExtraEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MerchantContainer;
import net.minecraft.world.inventory.MerchantResultSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.Merchant;
import net.minecraft.world.item.trading.MerchantOffer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/** PlayerPurchaseEvent for custom (plugin-made) merchants: a plugin may cancel the trade as the result is taken. */
@Mixin(MerchantResultSlot.class)
public abstract class ExtraPurchaseEventMixin {
    @Shadow @Final private MerchantContainer slots;
    @Shadow @Final private Merchant merchant;

    @WrapMethod(method = "onTake", require = 0)
    private void lunararc$purchase(Player player, ItemStack stack, Operation<Void> original) {
        MerchantOffer offer = this.slots.getActiveOffer();
        if (offer != null && player instanceof ServerPlayer serverPlayer && this.merchant instanceof MerchantBukkitBridge bridge
                && LunarArcExtraEvents.purchaseCancelled(serverPlayer, bridge.lunararc$getBukkitMerchant(), offer)) {
            stack.setCount(0);
            player.containerMenu.sendAllDataToRemote();
            serverPlayer.sendMerchantOffers(player.containerMenu.containerId, this.merchant.getOffers(), 1,
                    this.merchant.getVillagerXp(), this.merchant.showProgressBar(), this.merchant.canRestock());
            return;
        }
        original.call(player, stack);
    }
}
