package io.lunararcdevs.lunararc.common.mixin.core.recipe;

import io.lunararcdevs.lunararc.common.bridge.recipe.CopyDataComponentsBridge;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.SmithingRecipeInput;
import net.minecraft.world.item.crafting.SmithingTrimRecipe;
import net.minecraft.world.item.equipment.trim.ArmorTrim;
import net.minecraft.world.item.equipment.trim.TrimMaterial;
import net.minecraft.world.item.equipment.trim.TrimMaterials;
import net.minecraft.world.item.equipment.trim.TrimPattern;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SmithingTrimRecipe.class)
public abstract class SmithingTrimRecipeMixin implements CopyDataComponentsBridge {
    @Shadow @Final Holder<TrimPattern> pattern;
    @Unique private boolean lunararc$copyDataComponents = true;

    @Override public boolean lunararc$copyDataComponents() { return this.lunararc$copyDataComponents; }
    @Override public void lunararc$copyDataComponents(boolean copy) { this.lunararc$copyDataComponents = copy; }

    /** Paper's "copyDataComponents=false": build the trimmed result from the base item type only. */
    @Inject(method = "assemble(Lnet/minecraft/world/item/crafting/SmithingRecipeInput;Lnet/minecraft/core/HolderLookup$Provider;)Lnet/minecraft/world/item/ItemStack;",
            at = @At("HEAD"), cancellable = true)
    private void lunararc$controlComponentCopy(SmithingRecipeInput input, HolderLookup.Provider registries, CallbackInfoReturnable<ItemStack> cir) {
        if (this.lunararc$copyDataComponents) return;
        ItemStack base = input.base();
        Optional<Holder<TrimMaterial>> material = TrimMaterials.getFromIngredient(registries, input.addition());
        if (material.isEmpty()) {
            cir.setReturnValue(ItemStack.EMPTY);
            return;
        }
        ArmorTrim wanted = new ArmorTrim(material.get(), this.pattern);
        if (Objects.equals(base.get(DataComponents.TRIM), wanted)) {
            cir.setReturnValue(ItemStack.EMPTY);
            return;
        }
        ItemStack result = new ItemStack(base.getItem(), 1);
        result.set(DataComponents.TRIM, wanted);
        cir.setReturnValue(result);
    }
}
