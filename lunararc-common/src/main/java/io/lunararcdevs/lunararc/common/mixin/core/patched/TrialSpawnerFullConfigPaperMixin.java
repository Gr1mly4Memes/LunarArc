package io.lunararcdevs.lunararc.common.mixin.core.patched;

import net.minecraft.core.Holder;
import net.minecraft.world.level.block.entity.trialspawner.TrialSpawner;
import net.minecraft.world.level.block.entity.trialspawner.TrialSpawnerConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/** Paper's TrialSpawner API builds modified copies of the (immutable) full config. */
@Mixin(TrialSpawner.FullConfig.class)
public abstract class TrialSpawnerFullConfigPaperMixin {
    @Shadow public abstract Holder<TrialSpawnerConfig> normal();
    @Shadow public abstract Holder<TrialSpawnerConfig> ominous();
    @Shadow public abstract int targetCooldownLength();
    @Shadow public abstract int requiredPlayerRange();

    public TrialSpawner.FullConfig overrideTargetCooldownLength(int targetCooldownLength) {
        return new TrialSpawner.FullConfig(this.normal(), this.ominous(), targetCooldownLength, this.requiredPlayerRange());
    }

    public TrialSpawner.FullConfig overrideRequiredPlayerRange(int requiredPlayerRange) {
        return new TrialSpawner.FullConfig(this.normal(), this.ominous(), this.targetCooldownLength(), requiredPlayerRange);
    }

    public TrialSpawner.FullConfig overrideConfigs(Holder<TrialSpawnerConfig> normal, Holder<TrialSpawnerConfig> ominous) {
        return new TrialSpawner.FullConfig(normal, ominous, this.targetCooldownLength(), this.requiredPlayerRange());
    }
}
