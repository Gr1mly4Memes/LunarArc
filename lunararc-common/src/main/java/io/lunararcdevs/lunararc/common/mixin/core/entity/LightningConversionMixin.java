package io.lunararcdevs.lunararc.common.mixin.core.entity;

import io.lunararcdevs.lunararc.common.bridge.MobBridge;
import io.lunararcdevs.lunararc.common.mod.util.LunarArcLightning;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.animal.pig.Pig;
import net.minecraft.world.entity.npc.villager.Villager;
import org.bukkit.event.entity.EntityTransformEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Marks a pig or villager conversion as lightning-caused so Mob#convertTo fires Paper's EntityZapEvent for it. */
@Mixin({Pig.class, Villager.class})
public abstract class LightningConversionMixin {
    @Inject(method = "thunderHit", at = @At("HEAD"))
    private void lunararc$beginZap(ServerLevel level, LightningBolt bolt, CallbackInfo ci) {
        ((MobBridge) (Object) this).lunararc$pushTransformReason(EntityTransformEvent.TransformReason.LIGHTNING);
        LunarArcLightning.CURRENT.set(bolt);
    }

    @Inject(method = "thunderHit", at = @At("RETURN"))
    private void lunararc$endZap(ServerLevel level, LightningBolt bolt, CallbackInfo ci) {
        LunarArcLightning.CURRENT.remove();
    }
}
