package io.lunararcdevs.lunararc.common.bridge.recipe;

import java.util.List;
import net.minecraft.world.item.ItemStack;

/** Paper exact-choice state attached directly to the real NMS Ingredient. */
public interface IngredientBridge {
    /** The exact stacks a Paper ExactChoice demands, or {@code null} for an ordinary item-set ingredient. */
    List<ItemStack> lunararc$exactStacks();
    void lunararc$setExactStacks(List<ItemStack> stacks);

    default boolean lunararc$isExact() { return lunararc$exactStacks() != null; }
}
