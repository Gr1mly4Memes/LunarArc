package io.lunararcdevs.lunararc.common.mixin.core.item;

import io.lunararcdevs.lunararc.common.bridge.LivingEntityBridge;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.level.Level;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Tags potion-drink effects (the item class is gone; drinking is a consume callback on PotionContents). */
@Mixin(PotionContents.class)
public abstract class PotionContentsMixin {
    @Inject(method = "onConsume", at = @At("HEAD"), require = 1)
    private void lunararc$potionDrinkCause(Level level, LivingEntity entity, ItemStack stack, Consumable consumable, CallbackInfo ci) {
        ((LivingEntityBridge) entity).lunararc$pushEffectCause(EntityPotionEffectEvent.Cause.POTION_DRINK);
    }
}
