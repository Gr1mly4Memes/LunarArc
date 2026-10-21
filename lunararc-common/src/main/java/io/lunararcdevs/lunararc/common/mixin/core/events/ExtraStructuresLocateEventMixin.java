package io.lunararcdevs.lunararc.common.mixin.core.events;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.datafixers.util.Pair;
import io.lunararcdevs.lunararc.common.LunarArcServerAccess;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.Structure;
import org.bukkit.craftbukkit.generator.structure.CraftStructure;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

/** StructuresLocateEvent: a plugin may cancel a structure search, answer it itself, or change what is searched for. */
@Mixin(ChunkGenerator.class)
public abstract class ExtraStructuresLocateEventMixin {
    @WrapMethod(method = "findNearestMapStructure", require = 0)
    private @Nullable Pair<BlockPos, Holder<Structure>> lunararc$locate(ServerLevel level, HolderSet<Structure> structures, BlockPos pos,
            int radius, boolean skipKnown, Operation<Pair<BlockPos, Holder<Structure>>> original) {
        if (io.papermc.paper.event.world.StructuresLocateEvent.getHandlerList().getRegisteredListeners().length == 0
                || !org.bukkit.Bukkit.isPrimaryThread()) {
            return original.call(level, structures, pos, radius, skipKnown);
        }
        List<org.bukkit.generator.structure.Structure> api = new ArrayList<>();
        structures.forEach(holder -> api.add(CraftStructure.minecraftToBukkit(holder.value())));
        org.bukkit.World world = LunarArcServerAccess.getCraftWorld(level);
        if (api.isEmpty() || world == null) return original.call(level, structures, pos, radius, skipKnown);

        org.bukkit.Location origin = new org.bukkit.Location(world, pos.getX(), pos.getY(), pos.getZ());
        io.papermc.paper.event.world.StructuresLocateEvent event = new io.papermc.paper.event.world.StructuresLocateEvent(world, origin, api, radius, skipKnown);
        if (!event.callEvent()) return null;

        var registry = level.registryAccess().lookupOrThrow(Registries.STRUCTURE);
        if (event.getResult() != null) {
            var result = event.getResult();
            BlockPos found = new BlockPos(result.pos().blockX(), result.pos().blockY(), result.pos().blockZ());
            return Pair.of(found, registry.wrapAsHolder(CraftStructure.bukkitToMinecraft(result.structure())));
        }
        BlockPos newOrigin = new BlockPos(event.getOrigin().getBlockX(), event.getOrigin().getBlockY(), event.getOrigin().getBlockZ());
        List<Holder<Structure>> holders = new ArrayList<>();
        for (org.bukkit.generator.structure.Structure structure : event.getStructures()) {
            holders.add(registry.wrapAsHolder(CraftStructure.bukkitToMinecraft(structure)));
        }
        return original.call(level, HolderSet.direct(holders), newOrigin, event.getRadius(), event.shouldFindUnexplored());
    }
}
