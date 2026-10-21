package io.lunararcdevs.lunararc.common.mixin.core.events;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import io.lunararcdevs.lunararc.common.event.LunarArcBlockEvents;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;

/** BlockCanBuildEvent: a plugin may allow or deny placing a block where vanilla would decide otherwise. */
@Mixin(BlockItem.class)
public abstract class ExtraCanBuildEventMixin {
    @WrapMethod(method = "canPlace", require = 0)
    private boolean lunararc$canBuild(BlockPlaceContext context, BlockState state, Operation<Boolean> original) {
        return LunarArcBlockEvents.canBuild(context, state, original.call(context, state));
    }
}
