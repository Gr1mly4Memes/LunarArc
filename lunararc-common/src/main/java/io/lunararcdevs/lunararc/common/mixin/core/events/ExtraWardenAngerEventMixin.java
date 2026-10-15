package io.lunararcdevs.lunararc.common.mixin.core.events;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import io.lunararcdevs.lunararc.common.event.LunarArcExtraEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.warden.AngerManagement;
import net.minecraft.world.entity.monster.warden.Warden;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/** WardenAngerChangeEvent: a plugin may cancel an anger increase or change how much it adds. */
@Mixin(Warden.class)
public abstract class ExtraWardenAngerEventMixin {
    @Shadow AngerManagement angerManagement;

    @WrapMethod(method = "increaseAngerAt(Lnet/minecraft/world/entity/Entity;IZ)V", require = 0)
    private void lunararc$angerChange(Entity entity, int offset, boolean playListeningSound, Operation<Void> original) {
        Warden warden = (Warden) (Object) this;
        if (entity == null || warden.isNoAi() || !warden.canTargetEntity(entity)) {
            original.call(entity, offset, playListeningSound);
            return;
        }
        int adjusted = LunarArcExtraEvents.wardenAnger(warden, entity, this.angerManagement.getActiveAnger(entity), offset);
        if (adjusted == Integer.MIN_VALUE) return;
        original.call(entity, adjusted, playListeningSound);
    }
}
