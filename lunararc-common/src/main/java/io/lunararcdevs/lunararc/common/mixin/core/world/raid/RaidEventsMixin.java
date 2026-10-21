package io.lunararcdevs.lunararc.common.mixin.core.world.raid;

import io.lunararcdevs.lunararc.common.event.LunarArcMoreEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.entity.raid.Raider;
import org.bukkit.event.raid.RaidStopEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Mixin(Raid.class)
public abstract class RaidEventsMixin {
    @Shadow private long ticksActive;
    @Shadow private int groupsSpawned;
    @Unique private ServerLevel lunararc$lastLevel;
    @Shadow @Final private Set<UUID> heroesOfTheVillage;
    @Shadow @Final private Map<Integer, Set<Raider>> groupRaiderMap;
    @Shadow public abstract net.minecraft.core.BlockPos getCenter();
    @Shadow public abstract Raider getLeader(int wave);

    @Unique private boolean lunararc$wasOver;

    @Inject(method = "tick", at = @At("HEAD"), require = 1)
    private void lunararc$tickStart(ServerLevel level, CallbackInfo ci) {
        this.lunararc$lastLevel = level;
        this.lunararc$wasOver = ((Raid) (Object) this).isOver();
    }

    @Inject(method = "tick", at = @At("RETURN"), require = 1)
    private void lunararc$tickEnd(ServerLevel level, CallbackInfo ci) {
        Raid self = (Raid) (Object) this;
        if (!this.lunararc$wasOver && (self.isVictory() || self.isLoss())) {
            LunarArcMoreEvents.raidFinish((Raid) (Object) this, this.lunararc$lastLevel, this.heroesOfTheVillage);
        }
    }

    @Inject(method = "stop", at = @At("HEAD"), require = 0)
    private void lunararc$stop(CallbackInfo ci) {
        Raid self = (Raid) (Object) this;
        RaidStopEvent.Reason reason = self.isVictory() || self.isLoss() ? RaidStopEvent.Reason.FINISHED
                : this.lunararc$lastLevel.getDifficulty() == Difficulty.PEACEFUL ? RaidStopEvent.Reason.PEACE
                : !this.lunararc$lastLevel.isVillage(this.getCenter()) ? RaidStopEvent.Reason.NOT_IN_VILLAGE
                : this.ticksActive >= 48000L ? RaidStopEvent.Reason.TIMEOUT : RaidStopEvent.Reason.UNSPAWNABLE;
        LunarArcMoreEvents.raidStop((Raid) (Object) this, this.lunararc$lastLevel, reason);
    }

    @Inject(method = "spawnGroup", at = @At("RETURN"), require = 1)
    private void lunararc$wave(ServerLevel level, net.minecraft.core.BlockPos pos, CallbackInfo ci) {
        LunarArcMoreEvents.raidWave((Raid) (Object) this, this.lunararc$lastLevel, this.getLeader(this.groupsSpawned), this.groupRaiderMap.get(this.groupsSpawned));
    }
}
