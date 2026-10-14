package io.lunararcdevs.lunararc.common.mixin.core.world.blockentity;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(BeehiveBlockEntity.class)
public abstract class BeehiveBlockEntityMixin {
    @Shadow @org.spongepowered.asm.mixin.Final private List<?> stored;
    @Shadow private List<Entity> releaseAllOccupants(BlockState state, BeehiveBlockEntity.BeeReleaseStatus status) { throw new AssertionError(); }

    @Unique private static boolean lunararc$forceRelease;

    public int maxBees = 3;

    @Inject(method = "isFull", at = @At("HEAD"), cancellable = true)
    private void lunararc$maxBees(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(this.stored.size() >= this.maxBees);
    }

    public void clearBees() {
        this.stored.clear();
    }

    public List<Entity> releaseBees(BlockState state, BeehiveBlockEntity.BeeReleaseStatus status, boolean force) {
        boolean previous = lunararc$forceRelease;
        lunararc$forceRelease = force;
        try {
            return this.releaseAllOccupants(state, status);
        } finally {
            lunararc$forceRelease = previous;
        }
    }

    /** Paper's forced release: ignore the "bees stay in the hive" environment attribute (night / rain). */
    @WrapOperation(method = "releaseOccupant", require = 1, at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/attribute/EnvironmentAttributeSystem;getValue(Lnet/minecraft/world/attribute/EnvironmentAttribute;Lnet/minecraft/core/BlockPos;)Ljava/lang/Object;"))
    private static Object lunararc$forceRelease(net.minecraft.world.attribute.EnvironmentAttributeSystem system,
            net.minecraft.world.attribute.EnvironmentAttribute<?> attribute, BlockPos pos, Operation<Object> original) {
        if (lunararc$forceRelease && attribute == net.minecraft.world.attribute.EnvironmentAttributes.BEES_STAY_IN_HIVE) return Boolean.FALSE;
        return original.call(system, attribute, pos);
    }
}
