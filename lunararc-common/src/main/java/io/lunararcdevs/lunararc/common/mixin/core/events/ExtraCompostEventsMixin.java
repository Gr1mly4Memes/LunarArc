package io.lunararcdevs.lunararc.common.mixin.core.events;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import io.lunararcdevs.lunararc.common.event.LunarArcExtraEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

/**
 * CompostItemEvent and EntityCompostItemEvent. With no listener vanilla runs untouched. With one, the fill is
 * replayed here so the event sees the real roll; a cancelled fill is reported through a flag so the callers
 * give the item back, which is what Paper does by returning null.
 */
@Mixin(ComposterBlock.class)
public abstract class ExtraCompostEventsMixin {
    @Unique private static boolean lunararc$cancelled;

    @WrapMethod(method = "addItem(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/world/level/block/state/BlockState;", require = 0)
    private static BlockState lunararc$compost(Entity entity, BlockState state, LevelAccessor level, BlockPos pos, ItemStack stack,
            Operation<BlockState> original) {
        if (!LunarArcExtraEvents.compostListened(entity != null) || !(level instanceof ServerLevel)) {
            return original.call(entity, state, level, pos, stack);
        }
        int levelValue = state.getValue(ComposterBlock.LEVEL);
        float chance = ComposterBlock.COMPOSTABLES.getFloat(stack.getItem());
        double roll = level.getRandom().nextDouble();
        boolean willRaise = !((levelValue != 0 || chance <= 0.0F) && roll >= chance);
        Boolean decision = LunarArcExtraEvents.compost(entity, level, pos, stack, willRaise);
        if (decision == null) {
            lunararc$cancelled = true;
            return state;
        }
        if (!decision) return state;

        BlockState raised = state.setValue(ComposterBlock.LEVEL, levelValue + 1);
        if (entity != null && !CraftEventFactory.callEntityChangeBlockEvent(entity, pos, raised)) {
            lunararc$cancelled = true;
            return state;
        }
        level.setBlock(pos, raised, 3);
        level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(entity, raised));
        if (levelValue + 1 == 7) level.scheduleTick(pos, state.getBlock(), 20);
        return raised;
    }

    @WrapMethod(method = "useItemOn", require = 0)
    private InteractionResult lunararc$useItem(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player,
            InteractionHand hand, BlockHitResult hit, Operation<InteractionResult> original) {
        int before = stack.getCount();
        lunararc$cancelled = false;
        InteractionResult result = original.call(stack, state, level, pos, player, hand, hit);
        if (lunararc$cancelled) {
            lunararc$cancelled = false;
            stack.setCount(before);
            return InteractionResult.PASS;
        }
        return result;
    }

    @WrapMethod(method = "insertItem", require = 0)
    private static BlockState lunararc$insert(Entity entity, BlockState state, ServerLevel level, ItemStack stack, BlockPos pos,
            Operation<BlockState> original) {
        int before = stack.getCount();
        lunararc$cancelled = false;
        BlockState result = original.call(entity, state, level, stack, pos);
        if (lunararc$cancelled) {
            lunararc$cancelled = false;
            stack.setCount(before);
            return state;
        }
        return result;
    }
}
