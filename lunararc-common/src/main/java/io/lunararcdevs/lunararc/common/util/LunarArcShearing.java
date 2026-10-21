package io.lunararcdevs.lunararc.common.util;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.cow.MushroomCow;
import net.minecraft.world.entity.animal.golem.CopperGolem;
import net.minecraft.world.entity.animal.golem.SnowGolem;
import net.minecraft.world.entity.animal.sheep.Sheep;
import net.minecraft.world.entity.monster.skeleton.Bogged;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.resources.ResourceKey;

/**
 * Carries the (plugin-editable) drops of a PlayerShearEntityEvent from the event to the vanilla shearing code, which
 * would otherwise roll its own. Only the shearable mobs whose drops LunarArc controls take part.
 */
public final class LunarArcShearing {
    private static final ThreadLocal<Entity> TARGET = new ThreadLocal<>();
    private static final ThreadLocal<List<ItemStack>> FORCED = new ThreadLocal<>();

    private LunarArcShearing() {}

    public static boolean handles(Entity entity) {
        return entity instanceof Sheep || entity instanceof MushroomCow || entity instanceof SnowGolem
                || entity instanceof Bogged || entity instanceof CopperGolem;
    }

    private static ResourceKey<LootTable> table(Entity entity) {
        if (entity instanceof Sheep) return BuiltInLootTables.SHEAR_SHEEP;
        if (entity instanceof MushroomCow) return BuiltInLootTables.SHEAR_MOOSHROOM;
        if (entity instanceof SnowGolem) return BuiltInLootTables.SHEAR_SNOW_GOLEM;
        if (entity instanceof Bogged) return BuiltInLootTables.BOGGED_SHEAR;
        return null;
    }

    /** The drops a shearing would produce, rolled now so plugins can see and change them. */
    public static List<ItemStack> defaultDrops(Entity entity, ItemStack shears) {
        if (!handles(entity) || !(entity.level() instanceof ServerLevel level)) return List.of();
        if (entity instanceof CopperGolem golem) {
            ItemStack antenna = golem.getItemBySlot(CopperGolem.EQUIPMENT_SLOT_ANTENNA).copy();
            return antenna.isEmpty() ? List.of() : List.of(antenna);
        }
        ResourceKey<LootTable> key = table(entity);
        if (key == null) return List.of();
        LootTable loot = level.getServer().reloadableRegistries().getLootTable(key);
        LootParams params = new LootParams.Builder(level)
                .withParameter(LootContextParams.ORIGIN, entity.position())
                .withParameter(LootContextParams.THIS_ENTITY, entity)
                .withParameter(LootContextParams.TOOL, shears)
                .create(LootContextParamSets.SHEARING);
        return new ArrayList<>(loot.getRandomItems(params));
    }

    public static void force(Entity target, List<ItemStack> drops) {
        TARGET.set(target);
        FORCED.set(drops);
    }

    public static void clear() {
        TARGET.remove();
        FORCED.remove();
    }

    /** Drops to use instead of vanilla's roll for this entity, consumed once; null when none were set. */
    public static List<ItemStack> take(Entity entity) {
        if (TARGET.get() != entity) return null;
        List<ItemStack> drops = FORCED.get();
        clear();
        return drops;
    }

    public static boolean isLiving(Entity entity) {
        return entity instanceof LivingEntity;
    }
}
