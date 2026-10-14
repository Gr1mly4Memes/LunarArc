package io.lunararcdevs.lunararc.common.mixin.core.entity;

import io.lunararcdevs.lunararc.common.bridge.entity.AbstractVillagerBridge;
import net.minecraft.world.entity.npc.villager.AbstractVillager;
import net.minecraft.world.entity.npc.villager.VillagerTrades;
import net.minecraft.world.item.trading.MerchantOffers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(AbstractVillager.class)
public abstract class AbstractVillagerMixin implements AbstractVillagerBridge {
    @Accessor("offers") public abstract void lunararc$setOffers(MerchantOffers offers);
    @Invoker("addOffersFromItemListings") public abstract void lunararc$invokeAddOffers(net.minecraft.server.level.ServerLevel level, MerchantOffers offers, VillagerTrades.ItemListing[] listings, int amount);

    @Override public void lunararc$resetOffers() { lunararc$setOffers(null); }
    @Override public void lunararc$replaceOffers(MerchantOffers offers) { lunararc$setOffers(offers); }
    @Override public void lunararc$addOffers(MerchantOffers offers, VillagerTrades.ItemListing[] listings, int amount) {
        if (((net.minecraft.world.entity.Entity) (Object) this).level() instanceof net.minecraft.server.level.ServerLevel level) lunararc$invokeAddOffers(level, offers, listings, amount);
    }
}
