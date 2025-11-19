package com.example.spawnersphere.common.config;

/**
 * Factory for creating configuration screens
 * Platforms can register their implementation here
 */
public class ConfigScreenFactory {

    private static IConfigScreen instance = null;

    /**
     * Register the platform-specific config screen implementation
     */
    public static void register(IConfigScreen configScreen) {
        instance = configScreen;
    }

    /**
     * Get the config screen implementation
     * @return The config screen, or null if not registered
     */
    public static IConfigScreen get() {
        return instance;
    }

    /**
     * Check if config screen is available
     */
    public static boolean isAvailable() {
        return instance != null && instance.isAvailable();
    }
}
