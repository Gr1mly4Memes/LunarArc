package io.lunararcdevs.lunararc.common.bridge;

import io.papermc.paper.world.damagesource.PaperCombatTrackerWrapper;

/** Exposes Paper's combat tracker API wrapper that lives on the loader-owned vanilla CombatTracker. */
public interface CombatTrackerBridge {
    PaperCombatTrackerWrapper lunararc$getPaperCombatTracker();
}
