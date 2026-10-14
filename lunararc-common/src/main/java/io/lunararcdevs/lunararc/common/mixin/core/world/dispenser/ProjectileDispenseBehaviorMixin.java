package io.lunararcdevs.lunararc.common.mixin.core.world.dispenser;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.ProjectileDispenseBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ProjectileDispenseBehavior.class)
public abstract class ProjectileDispenseBehaviorMixin {
    @Unique private boolean lunararc$dispenseCancelled;

    @WrapOperation(method = "execute", require = 1,
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/Projectile;spawnProjectileUsingShoot(Lnet/minecraft/world/entity/projectile/Projectile;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/item/ItemStack;DDDFF)Lnet/minecraft/world/entity/projectile/Projectile;"))
    private Projectile lunararc$dispense(Projectile projectile, ServerLevel level, ItemStack stack, double x, double y, double z,
            float power, float uncertainty, Operation<Projectile> original, @Local(argsOnly = true) BlockSource source) {
        this.lunararc$dispenseCancelled = false;
        if (!org.bukkit.Bukkit.isPrimaryThread() || CraftEventFactory.dispenseEventFired) {
            return original.call(projectile, level, stack, x, y, z, power, uncertainty);
        }
        var event = CraftEventFactory.callBlockDispenseEvent(source, stack.copyWithCount(1), new Vec3(x, y, z));
        if (event.isCancelled()) {
            this.lunararc$dispenseCancelled = true;
            return projectile;
        }
        return original.call(projectile, level, stack, event.getVelocity().getX(), event.getVelocity().getY(), event.getVelocity().getZ(), power, uncertainty);
    }

    /** A vetoed dispense must not consume the item. */
    @Inject(method = "execute", require = 1, cancellable = true,
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;shrink(I)V"))
    private void lunararc$keepStack(BlockSource source, ItemStack item, CallbackInfoReturnable<ItemStack> cir) {
        if (this.lunararc$dispenseCancelled) {
            this.lunararc$dispenseCancelled = false;
            cir.setReturnValue(item);
        }
    }
}
