package io.lunararcdevs.lunararc.common.mixin.core.recipe;

import io.lunararcdevs.lunararc.common.bridge.recipe.IngredientBridge;
import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Paper exact RecipeChoice semantics without replacing the loader's Ingredient class. */
@Mixin(Ingredient.class)
public abstract class IngredientMixin implements IngredientBridge {
    @Unique private List<ItemStack> lunararc$exactStacks;

    @Override public List<ItemStack> lunararc$exactStacks() { return this.lunararc$exactStacks; }
    @Override public void lunararc$setExactStacks(List<ItemStack> stacks) { this.lunararc$exactStacks = stacks; }

    // --- Paper-compatible surface (patched vanilla): Paper classes call these directly ---
    public boolean isExact() { return this.lunararc$exactStacks != null; }
    public List<ItemStack> itemStacks() { return this.lunararc$exactStacks; }

    @Inject(method = "test(Lnet/minecraft/world/item/ItemStack;)Z", at = @At("HEAD"), cancellable = true)
    private void lunararc$testExact(ItemStack candidate, CallbackInfoReturnable<Boolean> cir) {
        if (this.lunararc$exactStacks == null) return;
        if (candidate == null || candidate.isEmpty()) {
            cir.setReturnValue(false);
            return;
        }
        for (ItemStack accepted : this.lunararc$exactStacks) {
            if (ItemStack.isSameItemSameComponents(accepted, candidate)) {
                cir.setReturnValue(true);
                return;
            }
        }
        cir.setReturnValue(false);
    }
}
