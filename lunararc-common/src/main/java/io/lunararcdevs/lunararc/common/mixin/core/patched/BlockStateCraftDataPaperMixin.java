package io.lunararcdevs.lunararc.common.mixin.core.patched;

import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.craftbukkit.block.data.CraftBlockData;
import org.spongepowered.asm.mixin.Mixin;

/** Paper's BlockState#createCraftBlockData, used by the data-component API classes. */
@Mixin(BlockBehaviour.BlockStateBase.class)
public abstract class BlockStateCraftDataPaperMixin {
    public CraftBlockData createCraftBlockData() {
        return CraftBlockData.fromData((BlockState) (Object) this);
    }
}
