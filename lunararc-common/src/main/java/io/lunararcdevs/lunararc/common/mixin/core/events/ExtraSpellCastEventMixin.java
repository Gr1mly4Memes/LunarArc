package io.lunararcdevs.lunararc.common.mixin.core.events;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.lunararcdevs.lunararc.common.event.LunarArcMobEvents;
import java.lang.reflect.Field;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.illager.SpellcasterIllager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * EntitySpellCastEvent: a plugin may stop an illager spell at the end of its warm-up. The goal is an inner class,
 * so its illager is read through the synthetic outer reference rather than a mixin shadow.
 */
@Mixin(targets = "net.minecraft.world.entity.monster.illager.SpellcasterIllager$SpellcasterUseSpellGoal")
public abstract class ExtraSpellCastEventMixin {
    private static Field lunararc$outer;

    private static SpellcasterIllager lunararc$caster(Goal goal) {
        try {
            if (lunararc$outer == null) {
                Class<?> type = goal.getClass();
                while (type != null && lunararc$outer == null) {
                    if (type.getName().endsWith("SpellcasterIllager$SpellcasterUseSpellGoal")) {
                        lunararc$outer = type.getDeclaredField("this$0");
                        lunararc$outer.setAccessible(true);
                    }
                    type = type.getSuperclass();
                }
            }
            return lunararc$outer == null ? null : (SpellcasterIllager) lunararc$outer.get(goal);
        } catch (ReflectiveOperationException | RuntimeException ignored) {
            return null;
        }
    }

    @WrapOperation(method = "tick", require = 0,
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/monster/illager/SpellcasterIllager$SpellcasterUseSpellGoal;performSpellCasting()V"))
    private void lunararc$cast(@org.spongepowered.asm.mixin.injection.Coerce Object goal, Operation<Void> original) {
        SpellcasterIllager caster = lunararc$caster((Goal) goal);
        if (caster != null && !LunarArcMobEvents.spellCast(caster, lunararc$spell(goal))) return;
        original.call(goal);
    }

    private static SpellcasterIllager.IllagerSpell lunararc$spell(Object goal) {
        try {
            java.lang.reflect.Method method = goal.getClass().getDeclaredMethod("getSpell");
            method.setAccessible(true);
            return (SpellcasterIllager.IllagerSpell) method.invoke(goal);
        } catch (ReflectiveOperationException | RuntimeException ignored) {
            return SpellcasterIllager.IllagerSpell.NONE;
        }
    }
}
