package org.bukkit.craftbukkit.entity;

import net.minecraft.world.entity.animal.equine.Markings;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.inventory.CraftInventoryHorse;
import org.bukkit.entity.Horse;
import org.bukkit.inventory.HorseInventory;

public final class CraftHorse extends CraftAbstractHorse implements Horse {
    public CraftHorse(CraftServer server, net.minecraft.world.entity.animal.equine.Horse entity) { super(server, entity); }
    @Override public net.minecraft.world.entity.animal.equine.Horse getHandle() { return (net.minecraft.world.entity.animal.equine.Horse) entity; }
    @Override public Variant getVariant() { return Variant.HORSE; }
    @Override public Color getColor() { return Color.values()[getHandle().getVariant().getId()]; }
    @Override public void setColor(Color color) {
        java.util.Objects.requireNonNull(color, "color");
        getHandle().setVariantAndMarkings(net.minecraft.world.entity.animal.equine.Variant.byId(color.ordinal()), getHandle().getMarkings());
    }
    @Override public Style getStyle() { return Style.values()[getHandle().getMarkings().getId()]; }
    @Override public void setStyle(Style style) {
        java.util.Objects.requireNonNull(style, "style");
        getHandle().setVariantAndMarkings(getHandle().getVariant(), Markings.byId(style.ordinal()));
    }
    @Override public boolean isCarryingChest() { return false; }
    @Override public void setCarryingChest(boolean chest) { if (chest) throw new UnsupportedOperationException("Horses cannot carry chests"); }
    @Override public HorseInventory getInventory() {
        io.lunararcdevs.lunararc.common.bridge.access.AbstractHorseAccessBridge a = (io.lunararcdevs.lunararc.common.bridge.access.AbstractHorseAccessBridge)(Object)getHandle();
        return new CraftInventoryHorse(getHandle().inventory,
                getHandle().createEquipmentSlotContainer(net.minecraft.world.entity.EquipmentSlot.BODY),
                getHandle().createEquipmentSlotContainer(net.minecraft.world.entity.EquipmentSlot.SADDLE));
    }
    @Override public String toString() { return "CraftHorse"; }
}
