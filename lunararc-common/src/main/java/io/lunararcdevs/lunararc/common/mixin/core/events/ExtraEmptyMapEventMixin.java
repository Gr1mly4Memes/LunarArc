package io.lunararcdevs.lunararc.common.mixin.core.events;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.lunararcdevs.lunararc.common.event.LunarArcExtraEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.EmptyMapItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** PlayerMapFilledEvent: remember the empty map before vanilla consumes it, then report the created map. */
@Mixin(EmptyMapItem.class)
public abstract class ExtraEmptyMapEventMixin {
    @Unique private ItemStack lunararc$emptyMapBefore = ItemStack.EMPTY;

    @Inject(method = "use", at = @At("HEAD"), require = 0)
    private void lunararc$rememberEmptyMap(Level level, Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        this.lunararc$emptyMapBefore = player.getItemInHand(hand).copy();
    }

    @WrapOperation(method = "use", require = 0,
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/MapItem;create(Lnet/minecraft/server/level/ServerLevel;IIBZZ)Lnet/minecraft/world/item/ItemStack;"))
    private ItemStack lunararc$mapFilled(ServerLevel level, int x, int z, byte scale, boolean trackingPosition, boolean unlimitedTracking,
            Operation<ItemStack> original, Level useLevel, Player player, InteractionHand hand) {
        ItemStack created = original.call(level, x, z, scale, trackingPosition, unlimitedTracking);
        return LunarArcExtraEvents.mapFilled(player, this.lunararc$emptyMapBefore, created);
    }
}
