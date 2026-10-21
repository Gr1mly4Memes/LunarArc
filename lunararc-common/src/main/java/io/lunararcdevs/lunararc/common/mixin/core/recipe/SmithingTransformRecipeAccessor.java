package io.lunararcdevs.lunararc.common.mixin.core.recipe;

import net.minecraft.world.item.crafting.SmithingTransformRecipe;
import net.minecraft.world.item.crafting.TransmuteResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** Read-only CraftBukkit view of vanilla 1.21.11 smithing-transform state. */
@Mixin(SmithingTransformRecipe.class)
public interface SmithingTransformRecipeAccessor extends io.lunararcdevs.lunararc.common.bridge.access.SmithingTransformRecipeAccessBridge {
    @Accessor("result") TransmuteResult lunararc$result();
}
