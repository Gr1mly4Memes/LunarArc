package io.lunararcdevs.lunararc.common.mixin.core.damage;

import io.lunararcdevs.lunararc.common.bridge.CombatTrackerBridge;
import io.papermc.paper.world.damagesource.PaperCombatTrackerWrapper;
import net.minecraft.world.damagesource.CombatTracker;
import net.minecraft.world.damagesource.CombatEntry;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/**
 * Paper's combat tracker API. The field is deliberately named {@code paperCombatTracker} and left public so Paper
 * classes that were compiled against patched vanilla keep linking.
 */
@Mixin(CombatTracker.class)
public abstract class CombatTrackerMixin implements CombatTrackerBridge {
    public final PaperCombatTrackerWrapper paperCombatTracker = new PaperCombatTrackerWrapper((CombatTracker) (Object) this);

    @Override
    public PaperCombatTrackerWrapper lunararc$getPaperCombatTracker() {
        return this.paperCombatTracker;
    }

    @Shadow public java.util.List<CombatEntry> entries;
    @Shadow public LivingEntity mob;
    @Shadow private int lastDamageTime;
    @Shadow private int combatStartTime;
    @Shadow private int combatEndTime;
    @Shadow public boolean inCombat;
    @Shadow public boolean takingDamage;

    /** The second half of CombatTracker#recordDamage, split out by Paper so the API can add entries it built itself. */
    public void recordDamageAndCheckCombatState(CombatEntry combatEntry) {
        this.entries.add(combatEntry);
        this.lastDamageTime = this.mob.tickCount;
        this.takingDamage = true;
        if (!this.inCombat && this.mob.isAlive() && combatEntry.source().getEntity() instanceof LivingEntity) {
            this.inCombat = true;
            this.combatStartTime = this.mob.tickCount;
            this.combatEndTime = this.combatStartTime;
            this.mob.onEnterCombat();
        }
    }

    /** The reset half of CombatTracker#recheckStatus, callable without the timeout condition. */
    public void resetCombatState() {
        boolean wasInCombat = this.inCombat;
        this.takingDamage = false;
        this.inCombat = false;
        this.combatEndTime = this.mob.tickCount;
        if (wasInCombat) this.mob.onLeaveCombat();
        this.entries.clear();
    }
}
