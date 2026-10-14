package io.lunararcdevs.lunararc.common.mixin.core.world.blockentity;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.CampfireBlockEntity;
import com.llamalad7.mixinextras.sugar.Local;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(CampfireBlockEntity.class)
public abstract class CampfireBlockEntityMixin implements io.lunararcdevs.lunararc.common.bridge.world.StopCookingBridge {
    public final boolean[] stopCooking = new boolean[4];

    @Override
    public boolean[] lunararc$stopCooking() {
        return this.stopCooking;
    }

    @WrapOperation(method = "cookTick", at = @At(value = "INVOKE", ordinal = 0,
            target = "Lnet/minecraft/core/NonNullList;get(I)Ljava/lang/Object;"))
    private static Object lunararc$skipStopped(NonNullList<ItemStack> items, int slot, Operation<Object> original,
            net.minecraft.server.level.ServerLevel level, net.minecraft.core.BlockPos pos,
            net.minecraft.world.level.block.state.BlockState state, CampfireBlockEntity campfire) {
        boolean[] stopped = ((io.lunararcdevs.lunararc.common.bridge.world.StopCookingBridge) campfire).lunararc$stopCooking();
        return slot < stopped.length && stopped[slot] ? ItemStack.EMPTY : original.call(items, slot);
    }

    /** CampfireStartEvent: the cook time is read from the recipe while food is placed. */
    @WrapOperation(method = "placeFood", require = 1,
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/crafting/CampfireCookingRecipe;cookingTime()I"))
    private int lunararc$startCooking(net.minecraft.world.item.crafting.CampfireCookingRecipe recipe, Operation<Integer> original,
            @Local(argsOnly = true) net.minecraft.server.level.ServerLevel level, @Local(argsOnly = true) ItemStack food,
            @Local java.util.Optional<net.minecraft.world.item.crafting.RecipeHolder<net.minecraft.world.item.crafting.CampfireCookingRecipe>> holder) {
        int cookTime = original.call(recipe);
        CampfireBlockEntity self = (CampfireBlockEntity) (Object) this;
        return io.lunararcdevs.lunararc.common.event.LunarArcPaperEvents.campfireStart(level, self.getBlockPos(), food, holder.orElse(null), cookTime);
    }
}
