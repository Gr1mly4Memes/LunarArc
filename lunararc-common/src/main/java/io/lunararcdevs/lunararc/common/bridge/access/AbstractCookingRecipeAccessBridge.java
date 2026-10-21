package io.lunararcdevs.lunararc.common.bridge.access;

import net.minecraft.world.item.crafting.CookingBookCategory;

/** Normal runtime bridge implemented by the corresponding Mixin accessor/invoker (group/input/result: SingleItemRecipeAccessBridge). */
public interface AbstractCookingRecipeAccessBridge {
    CookingBookCategory lunararc$category();
    float lunararc$experience();
    int lunararc$cookingTime();
}
