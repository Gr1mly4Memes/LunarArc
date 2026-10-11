package io.lunararcdevs.lunararc.common.mixin.core.entity;

import io.lunararcdevs.lunararc.common.server.LunarArcServerSettings;
import net.minecraft.world.entity.projectile.AbstractArrow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(AbstractArrow.class)
public abstract class AbstractArrowSettingsMixin {
    @ModifyConstant(method = "tickDespawn", constant = @Constant(intValue = 1200), require = 0)
    private int lunararc$arrowDespawnRate(int vanilla) {
        AbstractArrow arrow = (AbstractArrow) (Object) this;
        io.papermc.paper.configuration.WorldConfiguration.Tuning tuning = io.lunararcdevs.lunararc.common.server.LunarArcServerSettings.tuning(arrow.level());
        net.minecraft.world.entity.Entity owner = arrow.getOwner();
        if (owner instanceof net.minecraft.world.entity.player.Player player) {
            if (player.isCreative() && tuning.creativeArrowDespawnRate > 0) return tuning.creativeArrowDespawnRate;
        } else if (tuning.nonPlayerArrowDespawnRate > 0) {
            return tuning.nonPlayerArrowDespawnRate;
        }
        int rate = (int) LunarArcServerSettings.number(arrow.level(), "arrow-despawn-rate", vanilla);
        return rate > 0 ? rate : vanilla;
    }
}
