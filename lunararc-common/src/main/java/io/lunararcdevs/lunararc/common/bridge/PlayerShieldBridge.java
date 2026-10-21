package io.lunararcdevs.lunararc.common.bridge;

/** The attacker whose hit is currently being blocked by a player's shield (Paper's PlayerShieldDisableEvent). */
public interface PlayerShieldBridge {
    net.minecraft.world.entity.LivingEntity lunararc$getShieldAttacker();
}
