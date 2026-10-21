package io.lunararcdevs.lunararc.common.mixin.core.events;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import io.lunararcdevs.lunararc.common.event.LunarArcMobEvents;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.pathfinder.Path;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

/** EntityPathfindEvent: a plugin may remove positions a mob is allowed to path to. */
@Mixin(PathNavigation.class)
public abstract class ExtraPathfindEventMixin {
    @Shadow @Final protected Mob mob;

    @Unique private Entity lunararc$targetEntity;

    @WrapMethod(method = "createPath(Lnet/minecraft/world/entity/Entity;I)Lnet/minecraft/world/level/pathfinder/Path;", require = 0)
    private @Nullable Path lunararc$rememberTarget(Entity entity, int accuracy, Operation<Path> original) {
        this.lunararc$targetEntity = entity;
        try {
            return original.call(entity, accuracy);
        } finally {
            this.lunararc$targetEntity = null;
        }
    }

    @WrapMethod(method = "createPath(Ljava/util/Set;IZIF)Lnet/minecraft/world/level/pathfinder/Path;", require = 0)
    private @Nullable Path lunararc$pathfind(Set<BlockPos> targets, int regionOffset, boolean offsetUpward, int accuracy, float followRange,
            Operation<Path> original) {
        if (!LunarArcMobEvents.pathfindListened() || targets.isEmpty()) {
            return original.call(targets, regionOffset, offsetUpward, accuracy, followRange);
        }
        Set<BlockPos> allowed = new HashSet<>(targets);
        for (BlockPos target : targets) {
            if (!LunarArcMobEvents.pathfind(this.mob, target, this.lunararc$targetEntity)) {
                allowed.remove(target);
                if (allowed.isEmpty()) return null;
            }
        }
        return original.call(allowed, regionOffset, offsetUpward, accuracy, followRange);
    }
}
