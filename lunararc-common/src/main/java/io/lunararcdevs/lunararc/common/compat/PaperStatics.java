package io.lunararcdevs.lunararc.common.compat;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.ConduitBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/**
 * Bodies of the static helpers Paper patches into vanilla classes. A mixin cannot declare public statics, so
 * LunarArcMixinConfigPlugin adds thin static methods to the vanilla classes that delegate here.
 */
public final class PaperStatics {
    private PaperStatics() {}

    public static AABB itemFrameBox(BlockPos pos, Direction direction, boolean hasFramedMap) {
        Vec3 center = Vec3.atCenterOf(pos).relative(direction, -0.46875);
        float width = hasFramedMap ? 1.0F : 0.75F;
        Direction.Axis axis = direction.getAxis();
        double x = axis == Direction.Axis.X ? 0.0625 : width;
        double y = axis == Direction.Axis.Y ? 0.0625 : width;
        double z = axis == Direction.Axis.Z ? 0.0625 : width;
        return AABB.ofSize(center, x, y, z);
    }

    public static AABB paintingBox(BlockPos pos, Direction direction, int width, int height) {
        Vec3 center = Vec3.atCenterOf(pos).relative(direction, -0.46875);
        double offsetX = width % 2 == 0 ? 0.5 : 0.0;
        double offsetY = height % 2 == 0 ? 0.5 : 0.0;
        Vec3 shifted = center.relative(direction.getCounterClockWise(), offsetX).relative(Direction.UP, offsetY);
        Direction.Axis axis = direction.getAxis();
        double x = axis == Direction.Axis.X ? 0.0625 : width;
        double z = axis == Direction.Axis.Z ? 0.0625 : width;
        return AABB.ofSize(shifted, x, height, z);
    }

    public static int totalCookTime(ServerLevel level, AbstractFurnaceBlockEntity furnace, RecipeType<?> type, double speedMultiplier) {
        return (int) Math.ceil(AbstractFurnaceBlockEntity.getTotalCookTime(level, furnace) / speedMultiplier);
    }

    /** ConduitBlockEntity#updateAndAttackTarget with CraftBukkit's switch to refresh the target without hurting it. */
    public static void conduitUpdate(ServerLevel level, BlockPos pos, BlockState state, ConduitBlockEntity conduit, boolean canDestroy, boolean damageTarget) {
        EntityReference<LivingEntity> target = ConduitBlockEntity.updateDestroyTarget(conduit.destroyTarget, level, pos, canDestroy);
        LivingEntity living = EntityReference.getLivingEntity(target, level);
        if (damageTarget && living != null) {
            level.playSound(null, living.getX(), living.getY(), living.getZ(), net.minecraft.sounds.SoundEvents.CONDUIT_ATTACK_TARGET,
                    net.minecraft.sounds.SoundSource.BLOCKS, 1.0F, 1.0F);
            living.hurtServer(level, level.damageSources().magic(), 4.0F);
        }
        if (!java.util.Objects.equals(target, conduit.destroyTarget)) {
            conduit.destroyTarget = target;
            level.sendBlockUpdated(pos, state, state, 2);
        }
    }

    public static net.minecraft.world.level.storage.ValueInput createGlobal(net.minecraft.util.ProblemReporter reporter, net.minecraft.nbt.CompoundTag tag) {
        return net.minecraft.world.level.storage.TagValueInput.create(reporter,
                io.lunararcdevs.lunararc.common.mod.server.LunarArcServer.requireMinecraftServer().registryAccess(), tag);
    }

    public static net.minecraft.world.item.component.AttackRange attackRangeDefault() {
        return new net.minecraft.world.item.component.AttackRange(0.0F, 3.0F, 0.0F, 5.0F, 0.3F, 1.0F);
    }
}
