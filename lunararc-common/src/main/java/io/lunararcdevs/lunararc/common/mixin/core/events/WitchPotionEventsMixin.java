package io.lunararcdevs.lunararc.common.mixin.core.events;

import com.destroystokyo.paper.event.entity.WitchConsumePotionEvent;
import com.destroystokyo.paper.event.entity.WitchThrowPotionEvent;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import io.lunararcdevs.lunararc.common.bridge.EntityBridge;
import java.util.function.Consumer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/** Paper's WitchConsumePotionEvent and WitchThrowPotionEvent. */
@Mixin(Witch.class)
public abstract class WitchPotionEventsMixin {
    @WrapOperation(method = "aiStep", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/item/alchemy/PotionContents;forEachEffect(Ljava/util/function/Consumer;F)V"))
    private void lunararc$consume(PotionContents contents, Consumer<MobEffectInstance> consumer, float durationScale,
            Operation<Void> original, @Local ItemStack drunk) {
        WitchConsumePotionEvent event = new WitchConsumePotionEvent(
                (org.bukkit.entity.Witch) ((EntityBridge) this).lunararc$getBukkitEntity(), CraftItemStack.asCraftMirror(drunk));
        if (!event.callEvent() || event.getPotion() == null) return;
        PotionContents replacement = CraftItemStack.asNMSCopy(event.getPotion()).get(DataComponents.POTION_CONTENTS);
        if (replacement != null) original.call(replacement, consumer, durationScale);
    }

    @WrapOperation(method = "performRangedAttack", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/projectile/Projectile;spawnProjectileUsingShoot(Lnet/minecraft/world/entity/projectile/Projectile$ProjectileFactory;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/LivingEntity;DDDFF)Lnet/minecraft/world/entity/projectile/Projectile;"))
    private Projectile lunararc$throw(Projectile.ProjectileFactory<?> factory, ServerLevel level, ItemStack potion, LivingEntity shooter,
            double x, double y, double z, float power, float uncertainty, Operation<Projectile> original, LivingEntity target, float velocity) {
        WitchThrowPotionEvent event = new WitchThrowPotionEvent(
                (org.bukkit.entity.Witch) ((EntityBridge) this).lunararc$getBukkitEntity(),
                (org.bukkit.entity.LivingEntity) ((EntityBridge) target).lunararc$getBukkitEntity(), CraftItemStack.asCraftMirror(potion));
        if (!event.callEvent()) return null;
        return original.call(factory, level, CraftItemStack.asNMSCopy(event.getPotion()), shooter, x, y, z, power, uncertainty);
    }
}
