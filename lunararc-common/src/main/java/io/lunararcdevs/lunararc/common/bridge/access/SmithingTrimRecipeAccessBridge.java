package io.lunararcdevs.lunararc.common.bridge.access;

import net.minecraft.core.Holder;
import net.minecraft.world.item.equipment.trim.TrimPattern;

/** Normal runtime bridge implemented by the corresponding Mixin accessor/invoker. */
public interface SmithingTrimRecipeAccessBridge {
    Holder<TrimPattern> lunararc$pattern();
}
