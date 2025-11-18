package com.example.spawnersphere.config;

import com.example.spawnersphere.common.config.ModConfig;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.list.OptionsRowList;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

/**
 * Simple native config screen for Forge 1.16
 * Using Minecraft's native GUI system since Cloth Config has limited support
 */
public class ForgeConfigScreen extends Screen {

    private final Screen parent;
    private final ModConfig config;
    private OptionsRowList list;

    public ForgeConfigScreen(Screen parent, ModConfig config) {
        super(new StringTextComponent("Spawner Sphere Configuration"));
        this.parent = parent;
        this.config = config;
    }

    @Override
    protected void init() {
        // Add done button
        this.addButton(new Button(
            this.width / 2 - 100,
            this.height - 28,
            200,
            20,
            new TranslationTextComponent("gui.done"),
            button -> this.minecraft.displayGuiScreen(parent)
        ));
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        this.renderBackground();

        // Draw title
        drawCenteredString(this.font, this.title.getFormattedText(),
            this.width / 2, 8, 0xFFFFFF);

        // Draw config info
        int y = 30;
        int x = this.width / 2 - 150;

        drawString(this.font, "General Settings:", x, y, 0xFFFFFF);
        y += 12;
        drawString(this.font, "  Sphere Radius: " + config.getSphereRadius(), x, y, 0xAAAAAA);
        y += 10;
        drawString(this.font, "  Scan Radius: " + config.getScanRadius(), x, y, 0xAAAAAA);
        y += 10;
        drawString(this.font, "  Scan Interval: " + config.getScanInterval() + "ms", x, y, 0xAAAAAA);
        y += 10;
        drawString(this.font, "  Movement Threshold: " + config.getMovementThreshold(), x, y, 0xAAAAAA);

        y += 15;
        drawString(this.font, "Performance Settings:", x, y, 0xFFFFFF);
        y += 12;
        drawString(this.font, "  Spatial Indexing: " + (config.isEnableSpatialIndexing() ? "ON" : "OFF"), x, y, 0xAAAAAA);
        y += 10;
        drawString(this.font, "  Frustum Culling: " + (config.isEnableFrustumCulling() ? "ON" : "OFF"), x, y, 0xAAAAAA);
        y += 10;
        drawString(this.font, "  LOD: " + (config.isEnableLOD() ? "ON" : "OFF"), x, y, 0xAAAAAA);
        y += 10;
        drawString(this.font, "  LOD Segments: " + config.getLodMinSegments() + "-" + config.getLodMaxSegments(), x, y, 0xAAAAAA);

        y += 15;
        drawString(this.font, "Rendering Settings:", x, y, 0xFFFFFF);
        y += 12;
        drawString(this.font, "  Sphere Segments: " + config.getSphereSegments(), x, y, 0xAAAAAA);
        y += 10;
        drawString(this.font, "  Render Equator: " + (config.isRenderEquator() ? "ON" : "OFF"), x, y, 0xAAAAAA);

        y += 20;
        drawCenteredString(this.font, "Edit config/spawnersphere.toml to change settings",
            this.width / 2, y, 0x888888);

        super.render(mouseX, mouseY, partialTicks);
    }

    @Override
    public void onClose() {
        this.minecraft.displayGuiScreen(parent);
    }
}
