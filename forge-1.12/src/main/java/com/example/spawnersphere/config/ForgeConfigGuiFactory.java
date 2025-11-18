package com.example.spawnersphere.config;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.IModGuiFactory;

import java.util.Set;

/**
 * GUI Factory for Forge 1.12 config screen integration
 */
public class ForgeConfigGuiFactory implements IModGuiFactory {

    @Override
    public void initialize(Minecraft minecraftInstance) {
        // Nothing to initialize
    }

    @Override
    public boolean hasConfigGui() {
        return true;
    }

    @Override
    public GuiScreen createConfigGui(GuiScreen parentScreen) {
        return new ForgeConfigGui(parentScreen);
    }

    @Override
    public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
        return null;
    }
}
