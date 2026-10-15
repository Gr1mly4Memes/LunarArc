package io.lunararcdevs.lunararc.common.mixin.core.events;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import io.lunararcdevs.lunararc.common.event.LunarArcExtraEvents;
import net.minecraft.stats.Stat;
import net.minecraft.stats.StatsCounter;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;

/** PlayerStatisticIncrementEvent: fired before a statistic goes up; cancelling leaves the value as it was. */
@Mixin(StatsCounter.class)
public abstract class ExtraStatisticEventMixin {
    @WrapMethod(method = "increment", require = 0)
    private void lunararc$statisticIncrease(Player player, Stat<?> stat, int amount, Operation<Void> original) {
        int current = ((StatsCounter) (Object) this).getValue(stat);
        int updated = (int) Math.min((long) current + amount, 2147483647L);
        if (LunarArcExtraEvents.statisticIncrease(player, stat, current, updated)) return;
        original.call(player, stat, amount);
    }
}
