package io.lunararcdevs.lunararc.neoforge.mixin.core.inventory;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.lunararcdevs.lunararc.common.bridge.AnvilMenuBridge;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

/**
 * NeoForge moves the anvil cost and enchantment logic into createResultInternal. The common
 * AnvilMenuMixin covers createResult for vanilla and Forge; this covers the NeoForge-only method.
 */
@Mixin(AnvilMenu.class)
public abstract class AnvilMenuMixin_NeoForge {
    @ModifyConstant(method = "createResultInternal", constant = @Constant(intValue = 40), require = 0)
    private int lunararc$maximumRepairCostConstant(int original) {
        return ((AnvilMenuBridge) this).lunararc$getMaximumRepairCost();
    }

    @WrapOperation(method = "createResultInternal",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/Enchantment;getMaxLevel()I"),
            require = 0)
    private int lunararc$enchantmentMaxLevel(Enchantment enchantment, Operation<Integer> original) {
        return ((AnvilMenuBridge) this).lunararc$bypassesEnchantmentLevelRestriction()
                ? Integer.MAX_VALUE : original.call(enchantment);
    }
}
