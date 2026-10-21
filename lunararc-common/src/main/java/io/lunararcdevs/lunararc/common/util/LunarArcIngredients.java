package io.lunararcdevs.lunararc.common.util;

import io.lunararcdevs.lunararc.common.bridge.recipe.IngredientBridge;
import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

/** Exact-stack ingredient factory (mixins cannot expose public statics on Ingredient). */
public final class LunarArcIngredients {
    private LunarArcIngredients() {}

    public static Ingredient ofStacks(List<ItemStack> stacks) {
        Ingredient ingredient = Ingredient.of(stacks.stream().map(ItemStack::getItem));
        ((IngredientBridge) (Object) ingredient).lunararc$setExactStacks(stacks);
        return ingredient;
    }
}
