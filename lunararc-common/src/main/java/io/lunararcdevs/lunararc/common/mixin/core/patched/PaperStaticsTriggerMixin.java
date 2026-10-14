package io.lunararcdevs.lunararc.common.mixin.core.patched;

import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.decoration.painting.Painting;
import org.spongepowered.asm.mixin.Mixin;

/**
 * Applies to the classes that need Paper's static helpers added: LunarArcMixinConfigPlugin only gets to modify a class
 * that has at least one mixin.
 */
@Mixin({ItemFrame.class, Painting.class, net.minecraft.world.level.storage.TagValueInput.class, net.minecraft.world.item.component.AttackRange.class})
public abstract class PaperStaticsTriggerMixin {
}
