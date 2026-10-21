package io.lunararcdevs.lunararc.forge.event;

import io.lunararcdevs.lunararc.common.mod.util.LunarArcBlockPlaceCapture;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraftforge.event.level.BlockEvent;

public final class ForgeBlockPlaceEvents {
    private ForgeBlockPlaceEvents() {}

    public static void register() {
        BlockEvent.EntityPlaceEvent.BUS.addListener((java.util.function.Predicate<BlockEvent.EntityPlaceEvent>) ForgeBlockPlaceEvents::onPlace);
    }

    private static boolean onPlace(BlockEvent.EntityPlaceEvent forgeEvent) {
        if (!(forgeEvent.getEntity() instanceof ServerPlayer player)) return false;

        org.bukkit.event.block.BlockPlaceEvent bukkit = LunarArcBlockPlaceCapture.matching(player, forgeEvent.getPos());
        if (bukkit == null) {
            net.minecraftforge.common.util.BlockSnapshot snapshot = forgeEvent.getBlockSnapshot();
            bukkit = org.bukkit.craftbukkit.event.CraftEventFactory.callBlockPlaceEvent(
                    serverLevel(player), forgeEvent.getPos(), player, InteractionHand.MAIN_HAND,
                    forgeEvent.getPlacedBlock(), snapshot.getReplacedBlock(), snapshot.getTag());
        }
        return bukkit != null && bukkit.isCancelled();
    }

    private static net.minecraft.server.level.ServerLevel serverLevel(ServerPlayer player) {
        return io.lunararcdevs.lunararc.common.util.LunarArcMojangNames.level(player);
    }
}
