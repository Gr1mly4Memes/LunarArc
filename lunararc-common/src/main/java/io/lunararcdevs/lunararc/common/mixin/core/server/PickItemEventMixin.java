package io.lunararcdevs.lunararc.common.mixin.core.server;

import io.lunararcdevs.lunararc.common.event.LunarArcMoreEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundSetHeldSlotPacket;
import net.minecraft.network.protocol.game.ServerboundPickItemFromBlockPacket;
import net.minecraft.network.protocol.game.ServerboundPickItemFromEntityPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Paper's PlayerPickItemEvent (PlayerPickBlockEvent / PlayerPickEntityEvent): vanilla splits picking into a block and an
 * entity packet that both funnel into the private tryPickItem. The handlers record who/what is being picked; tryPickItem
 * then runs Paper's source/target slot logic in place of vanilla's.
 */
@Mixin(ServerGamePacketListenerImpl.class)
public abstract class PickItemEventMixin {
    @Shadow public ServerPlayer player;

    @Unique private BlockPos lunararc$pickBlock;
    @Unique private Entity lunararc$pickEntity;
    @Unique private boolean lunararc$pickIncludeData;

    @Inject(method = "handlePickItemFromBlock", at = @At("HEAD"), require = 0)
    private void lunararc$beginBlockPick(ServerboundPickItemFromBlockPacket packet, CallbackInfo ci) {
        if (!this.player.server.isSameThread()) return;
        this.lunararc$pickBlock = packet.pos();
        this.lunararc$pickEntity = null;
        this.lunararc$pickIncludeData = packet.includeData();
    }

    @Inject(method = "handlePickItemFromEntity", at = @At("HEAD"), require = 0)
    private void lunararc$beginEntityPick(ServerboundPickItemFromEntityPacket packet, CallbackInfo ci) {
        if (!this.player.server.isSameThread()) return;
        this.lunararc$pickBlock = null;
        this.lunararc$pickEntity = this.player.level().getEntityOrPart(packet.id());
        this.lunararc$pickIncludeData = packet.includeData();
    }

    @Inject(method = "handlePickItemFromBlock", at = @At("RETURN"), require = 1)
    private void lunararc$endBlockPick(ServerboundPickItemFromBlockPacket packet, CallbackInfo ci) {
        this.lunararc$pickBlock = null;
        this.lunararc$pickEntity = null;
    }

    @Inject(method = "handlePickItemFromEntity", at = @At("RETURN"), require = 1)
    private void lunararc$endEntityPick(ServerboundPickItemFromEntityPacket packet, CallbackInfo ci) {
        this.lunararc$pickBlock = null;
        this.lunararc$pickEntity = null;
    }

    @Inject(method = "tryPickItem", at = @At("HEAD"), cancellable = true, require = 0)
    private void lunararc$tryPickItem(ItemStack stack, CallbackInfo ci) {
        if (this.lunararc$pickBlock == null && this.lunararc$pickEntity == null) return;
        if (!stack.isItemEnabled(this.player.level().enabledFeatures())) return;
        Inventory inventory = this.player.getInventory();
        int sourceSlot = inventory.findSlotMatchingItem(stack);
        int targetSlot = Inventory.isHotbarSlot(sourceSlot) ? sourceSlot : inventory.getSuitableHotbarSlot();
        io.papermc.paper.event.player.PlayerPickItemEvent event = LunarArcMoreEvents.pickItem(
                this.player, this.lunararc$pickBlock, this.lunararc$pickEntity, this.lunararc$pickIncludeData, targetSlot, sourceSlot);
        ci.cancel();
        if (event != null) {
            if (event.isCancelled()) return;
            sourceSlot = event.getSourceSlot();
            targetSlot = event.getTargetSlot();
        }
        var items = inventory.getNonEquipmentItems();
        if (sourceSlot != -1) {
            if (Inventory.isHotbarSlot(sourceSlot) && Inventory.isHotbarSlot(targetSlot)) {
                inventory.setSelectedSlot(targetSlot);
            } else {
                inventory.setSelectedSlot(targetSlot);
                ItemStack previous = items.get(targetSlot);
                items.set(targetSlot, items.get(sourceSlot));
                items.set(sourceSlot, previous);
            }
        } else if (this.player.hasInfiniteMaterials()) {
            inventory.setSelectedSlot(targetSlot);
            if (!items.get(targetSlot).isEmpty()) {
                int free = inventory.getFreeSlot();
                if (free != -1) items.set(free, items.get(targetSlot));
            }
            items.set(targetSlot, stack);
        }
        ((ServerGamePacketListenerImpl) (Object) this).send(new ClientboundSetHeldSlotPacket(inventory.getSelectedSlot()));
        this.player.inventoryMenu.broadcastChanges();
    }
}
