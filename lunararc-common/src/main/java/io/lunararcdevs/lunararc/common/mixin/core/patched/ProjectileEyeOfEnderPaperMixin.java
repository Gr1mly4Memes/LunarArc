package io.lunararcdevs.lunararc.common.mixin.core.patched;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.Projectile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/** Paper's public hit test on projectiles. */
@Mixin(Projectile.class)
public abstract class ProjectileEyeOfEnderPaperMixin {
    @Shadow protected abstract boolean canHitEntity(Entity target);

    public boolean canHitEntityPublic(Entity target) {
        return this.canHitEntity(target);
    }
}
