package io.lunararcdevs.lunararc.common.mixin.core.world;

import io.lunararcdevs.lunararc.common.bridge.ExplosionBridge;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ServerExplosion;
import net.minecraft.world.phys.Vec3;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Entity/BlockExplodeEvent plus the Bukkit yield, hooked where ServerExplosion starts touching blocks. */
@Mixin(ServerExplosion.class)
public abstract class ExplosionMixin implements ExplosionBridge {
    @Shadow @Final private ServerLevel level;
    @Shadow @Final private Vec3 center;
    @Shadow @Final private Entity source;
    @Shadow @Final private float radius;
    @Shadow @Final private Explosion.BlockInteraction blockInteraction;

    @Unique private Float lunararc$yield;

    @Override
    public float lunararc$getYield() {
        if (this.lunararc$yield == null) {
            // CraftBukkit's own initializer: a decaying explosion reports the fraction vanilla would have dropped.
            this.lunararc$yield = this.blockInteraction == Explosion.BlockInteraction.DESTROY_WITH_DECAY ? 1.0F / this.radius : 1.0F;
        }
        return this.lunararc$yield;
    }

    @Override
    public void lunararc$setYield(float yield) {
        this.lunararc$yield = yield;
    }

    @Inject(method = "interactWithBlocks", at = @At("HEAD"), require = 1)
    private void lunararc$callExplosionEvent(List<BlockPos> toBlow, CallbackInfo ci) {
        // Vanilla only reaches this method when the interaction is not KEEP, matching where CraftBukkit fires the event.
        List<org.bukkit.block.Block> blockList = new ArrayList<>(toBlow.size());
        for (int index = toBlow.size() - 1; index >= 0; index--) {
            org.bukkit.block.Block block = CraftBlock.at(this.level, toBlow.get(index));
            if (!block.getType().isAir()) blockList.add(block);
        }

        boolean cancelled;
        List<org.bukkit.block.Block> remaining;
        if (this.source != null) {
            org.bukkit.event.entity.EntityExplodeEvent event = CraftEventFactory.callEntityExplodeEvent(
                    this.source, blockList, this.lunararc$getYield(), this.blockInteraction);
            cancelled = event.isCancelled();
            remaining = event.blockList();
            this.lunararc$setYield(event.getYield());
        } else {
            org.bukkit.block.Block block = CraftBlock.at(this.level, BlockPos.containing(this.center));
            org.bukkit.event.block.BlockExplodeEvent event = CraftEventFactory.callBlockExplodeEvent(
                    block, block.getState(), blockList, this.lunararc$getYield(), this.blockInteraction);
            cancelled = event.isCancelled();
            remaining = event.blockList();
            this.lunararc$setYield(event.getYield());
        }

        toBlow.clear();
        if (cancelled) return;
        for (org.bukkit.block.Block block : remaining) {
            toBlow.add(new BlockPos(block.getX(), block.getY(), block.getZ()));
        }
    }
}
