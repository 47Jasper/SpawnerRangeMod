package com.example.spawnersphere.common.config;

/**
 * Platform-agnostic config screen interface
 * Each platform implementation provides their own config GUI
 */
public interface IConfigScreen {

    /**
     * Create a config screen for the given parent screen
     * @param parent The parent screen to return to (platform-specific)
     * @return The config screen object (platform-specific)
     */
    Object createConfigScreen(Object parent);

    /**
     * Check if config GUI is available on this platform
     * @return true if config GUI can be shown
     */
    boolean isAvailable();
}
