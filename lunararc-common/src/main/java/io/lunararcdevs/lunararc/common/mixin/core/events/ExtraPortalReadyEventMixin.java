package io.lunararcdevs.lunararc.common.mixin.core.events;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import io.lunararcdevs.lunararc.common.event.LunarArcMobEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.NetherPortalBlock;
import net.minecraft.world.level.portal.TeleportTransition;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

/** EntityPortalReadyEvent: a plugin may cancel an entity's trip through a Nether portal before the destination is searched. */
@Mixin(NetherPortalBlock.class)
public abstract class ExtraPortalReadyEventMixin {
    @WrapMethod(method = "getPortalDestination", require = 0)
    private @Nullable TeleportTransition lunararc$portalReady(ServerLevel level, Entity entity, BlockPos pos, Operation<TeleportTransition> original) {
        var key = level.dimension() == Level.NETHER ? Level.OVERWORLD : Level.NETHER;
        ServerLevel destination = level.getServer().getLevel(key);
        if (!LunarArcMobEvents.portalReady(entity, destination)) {
            entity.portalProcess = null;
            return null;
        }
        return original.call(level, entity, pos);
    }
}
