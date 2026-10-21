package io.lunararcdevs.lunararc.common.mixin.core.events;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import io.lunararcdevs.lunararc.common.event.LunarArcExtraEvents;
import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.LoomMenu;
import net.minecraft.world.level.block.entity.BannerPattern;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/** PlayerLoomPatternSelectEvent: a plugin may cancel the selection or swap the chosen pattern. */
@Mixin(LoomMenu.class)
public abstract class ExtraLoomPatternEventMixin {
    @Shadow private List<Holder<BannerPattern>> selectablePatterns;

    @WrapMethod(method = "clickMenuButton", require = 0)
    private boolean lunararc$patternSelected(Player player, int id, Operation<Boolean> original) {
        if (id < 0 || id >= this.selectablePatterns.size()) return original.call(player, id);
        Holder<BannerPattern> chosen = this.selectablePatterns.get(id);
        Holder<BannerPattern> result = LunarArcExtraEvents.loomPattern(player, (AbstractContainerMenu) (Object) this, chosen);
        if (result == null) {
            player.containerMenu.sendAllDataToRemote();
            return false;
        }
        int index = id;
        if (!result.equals(chosen)) {
            int found = this.selectablePatterns.indexOf(result);
            if (found < 0) return original.call(player, id);
            index = found;
        }
        return original.call(player, index);
    }
}
