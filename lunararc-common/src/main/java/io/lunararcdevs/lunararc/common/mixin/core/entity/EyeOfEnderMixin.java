package io.lunararcdevs.lunararc.common.mixin.core.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.projectile.EyeOfEnder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(EyeOfEnder.class)
public abstract class EyeOfEnderMixin {
    @Shadow private int life;
    @Shadow private boolean surviveAfterDeath;

    public void signalTo(BlockPos pos, boolean resetLife) {
        EyeOfEnder self = (EyeOfEnder) (Object) this;
        double x = pos.getX();
        int y = pos.getY();
        double z = pos.getZ();
        double dx = x - self.getX();
        double dz = z - self.getZ();
        double distance = Math.sqrt(dx * dx + dz * dz);
        if (distance > 12.0D) {
            self.target = new net.minecraft.world.phys.Vec3(self.getX() + dx / distance * 12.0D, self.getY() + 8.0D, self.getZ() + dz / distance * 12.0D);
        } else {
            self.target = new net.minecraft.world.phys.Vec3(x, y, z);
        }
        if (resetLife) {
            this.life = 0;
            this.surviveAfterDeath = self.getRandom().nextInt(5) > 0;
        }
    }
}
