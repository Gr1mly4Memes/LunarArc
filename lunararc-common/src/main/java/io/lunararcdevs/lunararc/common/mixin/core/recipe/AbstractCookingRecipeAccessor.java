package io.lunararcdevs.lunararc.common.mixin.core.recipe;

import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.CookingBookCategory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** Read-only CraftBukkit view of vanilla 1.21.11 cooking-recipe state (group/input/result live on SingleItemRecipe). */
@Mixin(AbstractCookingRecipe.class)
public interface AbstractCookingRecipeAccessor extends io.lunararcdevs.lunararc.common.bridge.access.AbstractCookingRecipeAccessBridge {
    @Accessor("category") CookingBookCategory lunararc$category();
    @Accessor("experience") float lunararc$experience();
    @Accessor("cookingTime") int lunararc$cookingTime();
}
