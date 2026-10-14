package io.lunararcdevs.lunararc.common.mod.util;

import net.minecraft.world.entity.LightningBolt;

/** The bolt currently converting a mob, set for the duration of Pig/Villager#thunderHit. */
public final class LunarArcLightning {
    public static final ThreadLocal<LightningBolt> CURRENT = new ThreadLocal<>();

    private LunarArcLightning() {}
}
