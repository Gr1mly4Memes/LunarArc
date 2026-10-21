package io.lunararcdevs.lunararc.common.mixin.core.entity;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.cow.MushroomCow;
import net.minecraft.world.entity.animal.sheep.Sheep;
import net.minecraft.world.entity.animal.golem.CopperGolem;
import net.minecraft.world.entity.animal.golem.SnowGolem;
import net.minecraft.world.entity.monster.skeleton.Bogged;
import net.minecraft.world.entity.player.Player;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({Sheep.class, MushroomCow.class, SnowGolem.class, Bogged.class, CopperGolem.class})
public abstract class ShearableMobMixin {
    @Inject(method = "mobInteract", cancellable = true, require = 0, at = {
            @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/sheep/Sheep;shear(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/sounds/SoundSource;Lnet/minecraft/world/item/ItemStack;)V"),
            @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/cow/MushroomCow;shear(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/sounds/SoundSource;Lnet/minecraft/world/item/ItemStack;)V"),
            @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/golem/SnowGolem;shear(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/sounds/SoundSource;Lnet/minecraft/world/item/ItemStack;)V"),
            @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/golem/CopperGolem;shear(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/sounds/SoundSource;Lnet/minecraft/world/item/ItemStack;)V"),
            @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/monster/skeleton/Bogged;shear(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/sounds/SoundSource;Lnet/minecraft/world/item/ItemStack;)V")})
    private void lunararc$shear(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        if (CraftEventFactory.callPlayerShearEntityCancelled(player, (Entity) (Object) this, player.getItemInHand(hand), hand)) {
            cir.setReturnValue(InteractionResult.PASS);
        }
    }

    @Inject(method = "mobInteract", at = @At("RETURN"), require = 0)
    private void lunararc$clearShearDrops(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        io.lunararcdevs.lunararc.common.util.LunarArcShearing.clear();
    }
}
