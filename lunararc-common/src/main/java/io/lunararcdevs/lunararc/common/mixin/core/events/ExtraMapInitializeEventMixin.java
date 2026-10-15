package io.lunararcdevs.lunararc.common.mixin.core.events;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import io.lunararcdevs.lunararc.common.LunarArcServerAccess;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.maps.MapId;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

/**
 * MapInitializeEvent: fired the first time each map is created or loaded this run. Vanilla keeps map data in the
 * overworld's data storage, so the id is the only thing that tells a map apart.
 */
@Mixin(ServerLevel.class)
public abstract class ExtraMapInitializeEventMixin {
    @Unique private static final Set<Integer> lunararc$initialised = ConcurrentHashMap.newKeySet();

    @Unique
    private static void lunararc$fire(MapId id) {
        if (org.bukkit.event.server.MapInitializeEvent.getHandlerList().getRegisteredListeners().length == 0
                || !org.bukkit.Bukkit.isPrimaryThread() || !lunararc$initialised.add(id.id())) {
            return;
        }
        org.bukkit.map.MapView view = LunarArcServerAccess.getCraftServer().getMap(id.id());
        if (view != null) new org.bukkit.event.server.MapInitializeEvent(view).callEvent();
    }

    @WrapMethod(method = "getMapData", require = 0)
    private @Nullable MapItemSavedData lunararc$load(MapId id, Operation<MapItemSavedData> original) {
        MapItemSavedData data = original.call(id);
        if (data != null) lunararc$fire(id);
        return data;
    }

    @WrapMethod(method = "setMapData", require = 0)
    private void lunararc$create(MapId id, MapItemSavedData data, Operation<Void> original) {
        original.call(id, data);
        lunararc$fire(id);
    }
}
