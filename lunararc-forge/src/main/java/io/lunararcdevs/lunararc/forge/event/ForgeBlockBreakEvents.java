package io.lunararcdevs.lunararc.forge.event;

import io.lunararcdevs.lunararc.common.mod.util.LunarArcBlockBreakCapture;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.level.BlockEvent;

public final class ForgeBlockBreakEvents {
    private ForgeBlockBreakEvents() {}

    public static void register() {
        BlockEvent.BreakEvent.BUS.addListener((java.util.function.Predicate<BlockEvent.BreakEvent>) ForgeBlockBreakEvents::onBreak);
    }

    private static boolean onBreak(BlockEvent.BreakEvent forgeEvent) {
        if (!(forgeEvent.getPlayer() instanceof ServerPlayer player)) return false;
        org.bukkit.event.block.BlockBreakEvent bukkit = LunarArcBlockBreakCapture.matching(player, forgeEvent.getPos());
        if (bukkit == null) {
            bukkit = org.bukkit.craftbukkit.event.CraftEventFactory.callBlockBreakEvent(
                    serverLevel(player), forgeEvent.getPos(), player);
        }
        forgeEvent.setExpToDrop(bukkit.getExpToDrop());
        return bukkit.isCancelled();
    }

    private static net.minecraft.server.level.ServerLevel serverLevel(ServerPlayer player) {
        return io.lunararcdevs.lunararc.common.util.LunarArcMojangNames.level(player);
    }
}
