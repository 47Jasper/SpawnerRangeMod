package com.example.spawnersphere;

import com.example.spawnersphere.common.SpawnerSphereCore;
import com.example.spawnersphere.common.config.ModConfig;
import com.example.spawnersphere.platform.LegacyFabricPlatformHelper;
import com.example.spawnersphere.platform.LegacyFabricRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.legacyfabric.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

/**
 * Legacy Fabric (1.8.9-1.13.2) implementation using common architecture
 */
public class SpawnerSphereMod implements ClientModInitializer {

    private static SpawnerSphereCore core;
    private static KeyBinding toggleKey;
    private static MinecraftClient client;

    @Override
    public void onInitializeClient() {
        client = MinecraftClient.getInstance();

        // Initialize the common core with platform-specific implementations
        ModConfig config = new ModConfig();
        LegacyFabricPlatformHelper platformHelper = new LegacyFabricPlatformHelper();
        LegacyFabricRenderer renderer = new LegacyFabricRenderer();

        core = new SpawnerSphereCore(platformHelper, renderer, config);

        // Register keybinding
        toggleKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.spawnersphere.toggle",
            Keyboard.KEY_B,
            "category.spawnersphere"
        ));
    }

    /**
     * Called from mixin - client tick
     */
    public static void onTick() {
        if (toggleKey != null && toggleKey.wasPressed()) {
            if (client.player != null && client.world != null) {
                core.toggle(client.player, client.world);
            }
        }

        // Periodic tick for scanning
        if (client.player != null && client.world != null) {
            core.tick(client.player, client.world);
        }
    }

    /**
     * Called from mixin - render
     */
    public static void renderSpheres(float partialTicks) {
        if (!core.isEnabled()) return;
        if (client.player == null || client.world == null) return;

        // Setup OpenGL state
        GL11.glPushMatrix();

        // Calculate camera position for translation
        double playerX = client.player.lastTickPosX + (client.player.posX - client.player.lastTickPosX) * partialTicks;
        double playerY = client.player.lastTickPosY + (client.player.posY - client.player.lastTickPosY) * partialTicks;
        double playerZ = client.player.lastTickPosZ + (client.player.posZ - client.player.lastTickPosZ) * partialTicks;

        GL11.glTranslated(-playerX, -playerY, -playerZ);

        // Setup rendering state
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glLineWidth(2.0f);

        // Delegate to core (context is null for legacy - not used)
        LegacyFabricRenderer.RenderContext renderContext =
            new LegacyFabricRenderer.RenderContext(partialTicks);
        core.render(renderContext, client.player, client.world);

        // Restore OpenGL state
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
    }

    /**
     * Get the core instance (for testing or external access)
     */
    public static SpawnerSphereCore getCore() {
        return core;
    }
}
