package io.lunararcdevs.lunararc.common.mixin.core.recipe;

import net.minecraft.core.Holder;
import net.minecraft.world.item.crafting.SmithingTrimRecipe;
import net.minecraft.world.item.equipment.trim.TrimPattern;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** Read-only CraftBukkit view of vanilla 1.21.11 smithing-trim state. */
@Mixin(SmithingTrimRecipe.class)
public interface SmithingTrimRecipeAccessor extends io.lunararcdevs.lunararc.common.bridge.access.SmithingTrimRecipeAccessBridge {
    @Accessor("pattern") Holder<TrimPattern> lunararc$pattern();
}
