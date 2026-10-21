package io.lunararcdevs.lunararc.common.mixin.core.recipe;

import io.lunararcdevs.lunararc.common.bridge.recipe.CopyDataComponentsBridge;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.SmithingRecipeInput;
import net.minecraft.world.item.crafting.SmithingTransformRecipe;
import net.minecraft.world.item.crafting.TransmuteResult;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SmithingTransformRecipe.class)
public abstract class SmithingTransformRecipeMixin implements CopyDataComponentsBridge {
    @Shadow @Final TransmuteResult result;
    @Unique private boolean lunararc$copyDataComponents = true;
    @Override public boolean lunararc$copyDataComponents() { return this.lunararc$copyDataComponents; }
    @Override public void lunararc$copyDataComponents(boolean copy) { this.lunararc$copyDataComponents = copy; }

    /** Paper's "copyDataComponents=false": the result is built only from the recipe result, not the base item. */
    @Inject(method = "assemble(Lnet/minecraft/world/item/crafting/SmithingRecipeInput;Lnet/minecraft/core/HolderLookup$Provider;)Lnet/minecraft/world/item/ItemStack;",
            at = @At("HEAD"), cancellable = true)
    private void lunararc$assembleWithoutResultComponents(SmithingRecipeInput input, HolderLookup.Provider registries,
                                                           CallbackInfoReturnable<ItemStack> cir) {
        if (!this.lunararc$copyDataComponents) {
            ItemStack stack = new ItemStack(this.result.item(), this.result.count());
            stack.applyComponents(this.result.components());
            cir.setReturnValue(stack);
        }
    }
}
