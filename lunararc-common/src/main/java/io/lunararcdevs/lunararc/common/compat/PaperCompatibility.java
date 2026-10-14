package io.lunararcdevs.lunararc.common.compat;

/**
 * Shared Paper compatibility policy for the Minecraft 1.21 family.
 */
public final class PaperCompatibility {
    public static final String RUNTIME_MINECRAFT = "1.21.11";
    public static final String RUNTIME_API = "1.21.11";

    private PaperCompatibility() {
    }
    
    public static boolean isSupportedApiVersion(String apiVersion) {
        if (apiVersion == null || apiVersion.isBlank()) {
            return true; // legacy plugin.yml behaviour
        }
        String normalized = apiVersion.trim();
        if (normalized.equals("1.21")) return true;
        if (!normalized.startsWith("1.21.")) return false;
        try {
            return Integer.parseInt(normalized.substring("1.21.".length())) <= Integer.parseInt(RUNTIME_API.substring("1.21.".length()));
        } catch (NumberFormatException exception) {
            return false;
        }
    }
}
