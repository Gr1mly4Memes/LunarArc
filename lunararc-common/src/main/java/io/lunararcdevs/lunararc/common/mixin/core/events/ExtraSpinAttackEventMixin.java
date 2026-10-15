package io.lunararcdevs.lunararc.common.mixin.core.events;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import io.lunararcdevs.lunararc.common.event.LunarArcExtraEvents;
import java.util.List;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/**
 * EntityAttemptSpinAttackEvent: a plugin may stop a riptide spin from hitting a particular entity. Skipped
 * entities are counted so a wall hit still ends the spin, as in Paper. With no listener vanilla runs untouched.
 */
@Mixin(LivingEntity.class)
public abstract class ExtraSpinAttackEventMixin {
    @Shadow protected int autoSpinAttackTicks;
    @Shadow protected float autoSpinAttackDmg;
    @Shadow protected ItemStack autoSpinAttackItemStack;

    @Shadow protected abstract void doAutoAttackOnTouch(LivingEntity target);

    @Shadow protected abstract void setLivingEntityFlag(int flag, boolean value);

    @WrapMethod(method = "checkAutoSpinAttack", require = 0)
    private void lunararc$spinAttack(AABB before, AABB after, Operation<Void> original) {
        if (io.papermc.paper.event.entity.EntityAttemptSpinAttackEvent.getHandlerList().getRegisteredListeners().length == 0) {
            original.call(before, after);
            return;
        }
        LivingEntity self = (LivingEntity) (Object) this;
        List<Entity> touched = self.level().getEntities(self, before.minmax(after));
        int skipped = 0;
        for (Entity entity : touched) {
            if (!(entity instanceof LivingEntity target)) continue;
            if (!LunarArcExtraEvents.spinAttack(self, target)) {
                skipped++;
                continue;
            }
            this.doAutoAttackOnTouch(target);
            this.autoSpinAttackTicks = 0;
            self.setDeltaMovement(self.getDeltaMovement().scale(-0.2));
            break;
        }
        if (self.horizontalCollision && skipped == touched.size()) this.autoSpinAttackTicks = 0;

        if (!self.level().isClientSide() && this.autoSpinAttackTicks <= 0) {
            this.setLivingEntityFlag(4, false);
            this.autoSpinAttackDmg = 0.0F;
            this.autoSpinAttackItemStack = null;
        }
    }
}
