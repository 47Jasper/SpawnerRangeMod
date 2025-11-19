package com.example.spawnersphere.config;

import com.example.spawnersphere.common.config.ModConfig;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

/**
 * Simple config screen for Legacy Fabric (MC 1.8.9-1.13.2)
 * Displays current config values - edit config file to change
 */
public class LegacyConfigScreen extends GuiScreen {

    private final GuiScreen parent;
    private final ModConfig config;

    public LegacyConfigScreen(GuiScreen parent, ModConfig config) {
        this.parent = parent;
        this.config = config;
    }

    @Override
    public void initGui() {
        this.buttonList.clear();
        this.buttonList.add(new GuiButton(0, this.width / 2 - 100, this.height - 28, 200, 20, "Done"));
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == 0) {
            this.mc.displayGuiScreen(parent);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();

        // Draw title
        this.drawCenteredString(this.fontRendererObj, "Spawner Sphere Configuration",
            this.width / 2, 8, 0xFFFFFF);

        // Draw config info
        int y = 30;
        int x = this.width / 2 - 150;

        this.drawString(this.fontRendererObj, "General Settings:", x, y, 0xFFFFFF);
        y += 12;
        this.drawString(this.fontRendererObj, "  Sphere Radius: " + config.getSphereRadius(), x, y, 0xAAAAAA);
        y += 10;
        this.drawString(this.fontRendererObj, "  Scan Radius: " + config.getScanRadius(), x, y, 0xAAAAAA);
        y += 10;
        this.drawString(this.fontRendererObj, "  Scan Interval: " + config.getScanInterval() + "ms", x, y, 0xAAAAAA);
        y += 10;
        this.drawString(this.fontRendererObj, "  Movement Threshold: " + config.getMovementThreshold(), x, y, 0xAAAAAA);

        y += 15;
        this.drawString(this.fontRendererObj, "Performance Settings:", x, y, 0xFFFFFF);
        y += 12;
        this.drawString(this.fontRendererObj, "  Spatial Indexing: " + (config.isEnableSpatialIndexing() ? "ON" : "OFF"), x, y, 0xAAAAAA);
        y += 10;
        this.drawString(this.fontRendererObj, "  Frustum Culling: " + (config.isEnableFrustumCulling() ? "ON" : "OFF"), x, y, 0xAAAAAA);
        y += 10;
        this.drawString(this.fontRendererObj, "  LOD: " + (config.isEnableLOD() ? "ON" : "OFF"), x, y, 0xAAAAAA);
        y += 10;
        this.drawString(this.fontRendererObj, "  LOD Segments: " + config.getLodMinSegments() + "-" + config.getLodMaxSegments(), x, y, 0xAAAAAA);

        y += 15;
        this.drawString(this.fontRendererObj, "Rendering Settings:", x, y, 0xFFFFFF);
        y += 12;
        this.drawString(this.fontRendererObj, "  Sphere Segments: " + config.getSphereSegments(), x, y, 0xAAAAAA);
        y += 10;
        this.drawString(this.fontRendererObj, "  Render Equator: " + (config.isRenderEquator() ? "ON" : "OFF"), x, y, 0xAAAAAA);

        y += 20;
        this.drawCenteredString(this.fontRendererObj, "Edit config/spawnersphere.json to change settings",
            this.width / 2, y, 0x888888);

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return true;
    }
}
