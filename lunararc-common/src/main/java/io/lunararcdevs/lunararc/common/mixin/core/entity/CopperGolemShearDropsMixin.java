package io.lunararcdevs.lunararc.common.mixin.core.entity;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.lunararcdevs.lunararc.common.util.LunarArcShearing;
import java.util.List;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.animal.golem.CopperGolem;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/** A sheared copper golem drops what PlayerShearEntityEvent left, not just its antenna item. */
@Mixin(CopperGolem.class)
public abstract class CopperGolemShearDropsMixin {
    @WrapOperation(method = "shear", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/animal/golem/CopperGolem;spawnAtLocation(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/item/ItemStack;F)Lnet/minecraft/world/entity/item/ItemEntity;"))
    private ItemEntity lunararc$drops(CopperGolem golem, ServerLevel level, ItemStack antenna, float offset, Operation<ItemEntity> original) {
        List<ItemStack> forced = LunarArcShearing.take(golem);
        if (forced == null) return original.call(golem, level, antenna, offset);
        ItemEntity last = null;
        for (ItemStack drop : forced) {
            if (!drop.isEmpty()) last = original.call(golem, level, drop, offset);
        }
        return last;
    }
}
