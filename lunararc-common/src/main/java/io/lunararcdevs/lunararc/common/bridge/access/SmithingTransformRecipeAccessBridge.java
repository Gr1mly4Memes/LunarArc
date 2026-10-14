package io.lunararcdevs.lunararc.common.bridge.access;

import net.minecraft.world.item.crafting.TransmuteResult;

/** Normal runtime bridge implemented by the corresponding Mixin accessor/invoker. */
public interface SmithingTransformRecipeAccessBridge {
    TransmuteResult lunararc$result();
}
