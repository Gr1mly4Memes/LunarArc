package io.lunararcdevs.lunararc.common.mixin.core.events;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import io.lunararcdevs.lunararc.common.event.LunarArcExtraEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.sniffer.Sniffer;
import org.spongepowered.asm.mixin.Mixin;

/** EntityFertilizeEggEvent for sniffers: a plugin may stop the pregnancy. */
@Mixin(Sniffer.class)
public abstract class ExtraSnifferFertilizeEventMixin {
    @WrapMethod(method = "spawnChildFromBreeding", require = 0)
    private void lunararc$fertilize(ServerLevel level, Animal mate, Operation<Void> original) {
        if (!LunarArcExtraEvents.fertilizeEgg((Animal) (Object) this, mate)) return;
        original.call(level, mate);
    }
}
