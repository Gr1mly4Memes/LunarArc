package io.lunararcdevs.lunararc.common.mixin.core.world.block;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import io.lunararcdevs.lunararc.common.event.LunarArcPaperEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.CampfireBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

/** BlockCookEvent for food finishing on a campfire. A cancelled event leaves the item cooking, as in CraftBukkit. */
@Mixin(CampfireBlockEntity.class)
public abstract class CampfireCookEventMixin {
    @Unique private static final ThreadLocal<ItemStack> lunararc$replacement = new ThreadLocal<>();

    @WrapOperation(method = "cookTick", require = 1,
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;isItemEnabled(Lnet/minecraft/world/flag/FeatureFlagSet;)Z"))
    private static boolean lunararc$cookEvent(ItemStack result, FeatureFlagSet enabled, Operation<Boolean> original,
            @Local(argsOnly = true) ServerLevel level, @Local(argsOnly = true) BlockPos pos, @Local(ordinal = 0) ItemStack source) {
        ItemStack adjusted = LunarArcPaperEvents.campfireCook(level, pos, source, result);
        if (adjusted == null) return false;
        lunararc$replacement.set(adjusted);
        return original.call(result, enabled);
    }

    @WrapOperation(method = "cookTick", require = 1,
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/Containers;dropItemStack(Lnet/minecraft/world/level/Level;DDDLnet/minecraft/world/item/ItemStack;)V"))
    private static void lunararc$dropResult(Level level, double x, double y, double z, ItemStack stack, Operation<Void> original) {
        ItemStack replacement = lunararc$replacement.get();
        lunararc$replacement.remove();
        original.call(level, x, y, z, replacement == null ? stack : replacement);
    }
}
