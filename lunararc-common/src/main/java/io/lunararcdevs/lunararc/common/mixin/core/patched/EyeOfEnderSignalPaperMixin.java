package io.lunararcdevs.lunararc.common.mixin.core.patched;

import net.minecraft.world.entity.projectile.EyeOfEnder;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/** Paper lets the eye's target change without restarting its life or re-rolling survival. */
@Mixin(EyeOfEnder.class)
public abstract class EyeOfEnderSignalPaperMixin {
    @Shadow private Vec3 target;

    public void signalTo(Vec3 pos, boolean update) {
        EyeOfEnder self = (EyeOfEnder) (Object) this;
        if (update) {
            self.signalTo(pos);
            return;
        }
        Vec3 offset = pos.subtract(self.position());
        double distance = offset.horizontalDistance();
        this.target = distance > 12.0
                ? self.position().add(offset.x / distance * 12.0, 8.0, offset.z / distance * 12.0)
                : pos;
    }
}
