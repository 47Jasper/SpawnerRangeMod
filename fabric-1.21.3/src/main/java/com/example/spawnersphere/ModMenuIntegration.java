package com.example.spawnersphere;

import com.example.spawnersphere.common.config.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.minecraft.client.gui.screen.Screen;

/**
 * Mod Menu integration for config screen
 */
public class ModMenuIntegration implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> {
            if (com.example.spawnersphere.common.config.ConfigScreenFactory.isAvailable()) {
                Object screen = com.example.spawnersphere.common.config.ConfigScreenFactory.get()
                    .createConfigScreen(parent);
                return (Screen) screen;
            }
            return null;
        };
    }
}
