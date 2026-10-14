package io.lunararcdevs.lunararc.common.mixin.core.entity;

import io.lunararcdevs.lunararc.common.bridge.entity.AbstractSkeletonBridge;
import net.minecraft.world.entity.monster.skeleton.AbstractSkeleton;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(AbstractSkeleton.class)
public abstract class AbstractSkeletonMixin implements AbstractSkeletonBridge {
    @Unique private boolean lunararc$shouldBurnInDay = true;
    @Override public boolean lunararc$shouldBurnInDay() { return this.lunararc$shouldBurnInDay; }
    @Override public void lunararc$setShouldBurnInDay(boolean burn) { this.lunararc$shouldBurnInDay = burn; }
}
