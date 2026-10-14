package io.lunararcdevs.lunararc.common.mixin.core.events;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.lunararcdevs.lunararc.common.bridge.EntityBridge;
import io.papermc.paper.event.entity.EntityDyeEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.animal.feline.Cat;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/** Paper's EntityDyeEvent for wolf and cat collars. A cancelled dye reads as "already that colour", so nothing is consumed. */
@Mixin({Wolf.class, Cat.class})
public abstract class CollarDyeEventMixin {
    @WrapOperation(method = "mobInteract", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/DyeItem;getDyeColor()Lnet/minecraft/world/item/DyeColor;"))
    private DyeColor lunararc$dye(DyeItem item, Operation<DyeColor> original, Player player, InteractionHand hand) {
        DyeColor requested = original.call(item);
        Object self = this;
        DyeColor current = self instanceof Wolf wolf ? wolf.getCollarColor() : ((Cat) self).getCollarColor();
        org.bukkit.entity.Entity bukkit = ((EntityBridge) this).lunararc$getBukkitEntity();
        if (!(((EntityBridge) player).lunararc$getBukkitEntity() instanceof org.bukkit.entity.Player bukkitPlayer)) return requested;
        EntityDyeEvent event = new EntityDyeEvent(bukkit, org.bukkit.DyeColor.getByWoolData((byte) requested.getId()), bukkitPlayer);
        if (!event.callEvent()) return current;
        return DyeColor.byId(event.getColor().getWoolData());
    }
}
