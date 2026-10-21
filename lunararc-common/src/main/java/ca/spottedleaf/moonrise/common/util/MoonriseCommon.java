package ca.spottedleaf.moonrise.common.util;

/**
 * Paper's chunk-system worker settings are applied through Moonrise. LunarArc keeps the loader's own chunk system, so the
 * donated Paper configuration class has nothing to adjust; this keeps its post-processing from failing to link.
 */
public final class MoonriseCommon {
    private MoonriseCommon() {}

    public static void adjustWorkerThreads(final int configWorkerThreads, final int configIoThreads) {
    }
}
