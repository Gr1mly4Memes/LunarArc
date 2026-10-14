package io.lunararcdevs.lunararc.common.mixin.core.entity;

import net.minecraft.world.entity.monster.Phantom;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.UUID;

@Mixin(Phantom.class)
public abstract class PhantomMixin implements io.lunararcdevs.lunararc.common.bridge.entity.BurnInDayBridge {
    @Override public boolean lunararc$shouldBurnInDay() { return this.shouldBurnInDay; }
    // Public and unprefixed: Paper's CraftPhantom reads these fields directly.
    public boolean shouldBurnInDay = true;
    public UUID spawningEntity;

    public boolean shouldBurnInDay() { return this.shouldBurnInDay; }
    public void setShouldBurnInDay(boolean shouldBurnInDay) { this.shouldBurnInDay = shouldBurnInDay; }
    public UUID getSpawningEntity() { return this.spawningEntity; }
    public void setSpawningEntity(UUID entity) { this.spawningEntity = entity; }
}
