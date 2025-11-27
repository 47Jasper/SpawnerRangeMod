package me.jasper.spawnersphere;

import me.jasper.spawnersphere.common.SpawnerSphereCore;
import me.jasper.spawnersphere.common.config.ConfigScreenFactory;
import me.jasper.spawnersphere.common.config.ModConfig;
import me.jasper.spawnersphere.config.ClothConfigScreen;
import me.jasper.spawnersphere.platform.FabricPlatformHelper;
import me.jasper.spawnersphere.platform.FabricRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;

import java.io.File;

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

        // Set up config file location and load
        File configDir = FabricLoader.getInstance().getConfigDir().toFile();
        File configFile = new File(configDir, "spawner-sphere-mod.properties");
        config.setConfigFile(configFile);
        config.load();

        FabricPlatformHelper platformHelper = new FabricPlatformHelper();
        FabricRenderer renderer = new FabricRenderer();

        core = new SpawnerSphereCore(platformHelper, renderer, config);

        // Register config screen (optional - only if Cloth Config is available)
        // Check for Cloth Config BEFORE instantiating ClothConfigScreen to avoid class loading errors
        if (isClothConfigAvailable()) {
            ConfigScreenFactory.register(new ClothConfigScreen(config));
        }

        // Register keybinding
        toggleKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.spawnersphere.toggle",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_B,
            KeyBinding.Category.MISC
        ));

        // Register tick event for keybinding and periodic updates
        ClientTickEvents.END_CLIENT_TICK.register(this::onClientTick);

        // Note: Render event is handled via mixin (WorldRendererMixin) since
        // WorldRenderEvents was removed in Fabric API for 1.21.9+
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

    /**
     * Called from WorldRendererMixin to render spheres after world rendering.
     * This replaces the WorldRenderEvents.AFTER_TRANSLUCENT callback.
     */
    @SuppressWarnings("unused")
    public static void onWorldRender(
            Camera camera,
            Matrix4f positionMatrix,
            Matrix4f projectionMatrix,
            RenderTickCounter tickCounter
    ) {
        if (core == null || !core.isEnabled()) return;

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.world == null) return;

        // Create our own immediate vertex consumer provider for rendering
        VertexConsumerProvider.Immediate immediate = client.getBufferBuilders().getEntityVertexConsumers();

        // Create matrix stack and apply camera transform
        MatrixStack matrices = new MatrixStack();
        matrices.multiplyPositionMatrix(positionMatrix);

        Vec3d cameraPos = camera.getPos();
        matrices.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);

        // Create render context and delegate to core
        FabricRenderer.RenderContext renderContext =
            new FabricRenderer.RenderContext(matrices, immediate);

        core.render(renderContext, client.player, client.world);

        // Force draw to ensure our lines are rendered
        immediate.draw(RenderLayer.getLines());
    }

    /**
     * Get the core instance (for testing or external access)
     */
    @SuppressWarnings("unused")
    public static SpawnerSphereCore getCore() {
        return core;
    }

    /**
     * Check if Cloth Config is available without loading ClothConfigScreen class.
     * This must be called BEFORE any reference to ClothConfigScreen to avoid ClassNotFoundException.
     */
    private static boolean isClothConfigAvailable() {
        try {
            Class.forName("me.shedaniel.clothconfig2.api.ConfigBuilder");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
