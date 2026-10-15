package io.lunararcdevs.lunararc.common.mixin.core.events;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.lunararcdevs.lunararc.common.event.LunarArcBlockEvents;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/** FurnaceExtractEvent and BlockExpEvent: a plugin may change how much experience a furnace gives out. */
@Mixin(AbstractFurnaceBlockEntity.class)
public abstract class ExtraFurnaceExpEventMixin {
    @WrapMethod(method = "getRecipesToAwardAndPopExperience", require = 0)
    private List<RecipeHolder<?>> lunararc$remember(ServerLevel level, Vec3 popVec, Operation<List<RecipeHolder<?>>> original) {
        BlockPos before = LunarArcBlockEvents.furnacePos;
        LunarArcBlockEvents.furnacePos = ((AbstractFurnaceBlockEntity) (Object) this).getBlockPos();
        try {
            return original.call(level, popVec);
        } finally {
            LunarArcBlockEvents.furnacePos = before;
        }
    }

    @WrapOperation(method = "createExperience", require = 0,
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ExperienceOrb;award(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/phys/Vec3;I)V"))
    private static void lunararc$award(ServerLevel level, Vec3 pos, int amount, Operation<Void> original) {
        BlockPos furnace = LunarArcBlockEvents.furnacePos;
        if (furnace == null) {
            original.call(level, pos, amount);
            return;
        }
        int changed = LunarArcBlockEvents.blockExp(level, furnace, LunarArcBlockEvents.furnacePlayer, LunarArcBlockEvents.furnaceStack,
                LunarArcBlockEvents.furnaceAmount, amount);
        original.call(level, pos, changed);
    }
}
