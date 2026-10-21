package io.lunararcdevs.lunararc.common.util;

/** Marks the totem-of-undying consume effects so they report the TOTEM cause instead of MILK / FOOD. */
public final class LunarArcEffectContext {
    private static final ThreadLocal<Boolean> TOTEM = ThreadLocal.withInitial(() -> Boolean.FALSE);
    private LunarArcEffectContext() {}
    public static boolean totem() { return TOTEM.get(); }
    public static void totem(boolean active) { if (active) TOTEM.set(Boolean.TRUE); else TOTEM.remove(); }
}
