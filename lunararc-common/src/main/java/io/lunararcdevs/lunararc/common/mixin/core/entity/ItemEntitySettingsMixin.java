package io.lunararcdevs.lunararc.common.mixin.core.entity;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.lunararcdevs.lunararc.common.server.LunarArcServerSettings;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public abstract class ItemEntitySettingsMixin {
    @org.spongepowered.asm.mixin.Shadow private int age;

    @Inject(method = "<init>(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/level/Level;)V", at = @At("TAIL"), require = 0)
    private void lunararc$despawnRate(EntityType<? extends ItemEntity> type, Level level, CallbackInfo ci) {
        if (!(level instanceof ServerLevel)) return;
        int rate = (int) LunarArcServerSettings.number(level, "item-despawn-rate", 6000.0D);
        if (rate > 0 && rate != 6000) this.age = 6000 - rate;
    }

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true, require = 0)
    private void lunararc$altDespawn(CallbackInfo ci) {
        ItemEntity self = (ItemEntity) (Object) this;
        Level level = self.level();
        if (level.isClientSide) return;
        io.papermc.paper.configuration.WorldConfiguration.Tuning tuning = io.lunararcdevs.lunararc.common.server.LunarArcServerSettings.tuning(level);
        if (!tuning.altItemDespawnEnabled || tuning.altItemDespawn.isEmpty()) return;
        net.minecraft.resources.Identifier id = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(self.getItem().getItem());
        Integer rate = tuning.altItemDespawn.get(id.getPath());
        if (rate != null && this.age >= rate) {
            self.discard();
            ci.cancel();
        }
    }

    @WrapOperation(method = "mergeWithNeighbours", require = 0,
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/AABB;inflate(DDD)Lnet/minecraft/world/phys/AABB;"))
    private AABB lunararc$mergeRadius(AABB box, double x, double y, double z, Operation<AABB> original) {
        Level level = ((ItemEntity) (Object) this).level();
        double radius = LunarArcServerSettings.number(level, "merge-radius.item", x);
        return original.call(box, radius, y, radius);
    }
}
