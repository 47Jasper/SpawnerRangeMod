package com.example.spawnersphere;

import com.example.spawnersphere.common.SpawnerSphereCore;
import com.example.spawnersphere.common.config.ConfigScreenFactory;
import com.example.spawnersphere.common.config.ModConfig;
import com.example.spawnersphere.config.ClothConfigScreen;
import com.example.spawnersphere.platform.FabricPlatformHelper;
import com.example.spawnersphere.platform.FabricRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;

/**
 * Fabric 1.21+ implementation using common architecture
 */
public class SpawnerSphereMod implements ClientModInitializer {

    private static SpawnerSphereCore core;
    private static KeyBinding toggleKey;

    @Override
    public void onInitializeClient() {
        // Initialize the common core with platform-specific implementations
        ModConfig config = new ModConfig();
        FabricPlatformHelper platformHelper = new FabricPlatformHelper();
        FabricRenderer renderer = new FabricRenderer();

        core = new SpawnerSphereCore(platformHelper, renderer, config);

        // Register config screen (optional - only if Cloth Config is available)
        ConfigScreenFactory.register(new ClothConfigScreen(config));

        // Register keybinding
        toggleKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.spawnersphere.toggle",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_B,
            "category.spawnersphere"
        ));

        // Register tick event for keybinding and periodic updates
        ClientTickEvents.END_CLIENT_TICK.register(this::onClientTick);

        // Register render event
        WorldRenderEvents.AFTER_TRANSLUCENT.register(this::onRenderWorld);
    }

    private void onClientTick(MinecraftClient client) {
        // Handle toggle key
        while (toggleKey.wasPressed()) {
            if (client.player != null && client.world != null) {
                core.toggle(client.player, client.world);
            }
        }

        // Periodic tick for scanning
        if (client.player != null && client.world != null) {
            core.tick(client.player, client.world);
        }
    }

    private void onRenderWorld(WorldRenderContext context) {
        if (!core.isEnabled()) return;

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.world == null) return;

        // Prepare rendering context
        MatrixStack matrices = context.matrixStack();
        Vec3d cameraPos = context.camera().getPos();

        matrices.push();
        matrices.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);

        // Create render context and delegate to core
        FabricRenderer.RenderContext renderContext =
            FabricRenderer.RenderContext.from(context);

        core.render(renderContext, client.player, client.world);

        matrices.pop();

        // Force draw if needed
        if (context.consumers() != null) {
            context.consumers().draw();
        }
    }

    /**
     * Get the core instance (for testing or external access)
     */
    public static SpawnerSphereCore getCore() {
        return core;
    }
}
