package com.example.spawnersphere;

import com.example.spawnersphere.common.SpawnerSphereCore;
import com.example.spawnersphere.common.config.ModConfig;
import com.example.spawnersphere.platform.ForgePlatformHelper;
import com.example.spawnersphere.platform.ForgeRenderer;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.lwjgl.glfw.GLFW;

/**
 * Forge implementation for MC 1.13-1.16.5 using common architecture
 */
@Mod("spawnersphere")
public class SpawnerSphereMod {

    public static final String MODID = "spawnersphere";

    private static SpawnerSphereCore core;
    private static KeyBinding toggleKey;

    public SpawnerSphereMod() {
        // Register ourselves for client setup
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        // Initialize the common core with platform-specific implementations
        ModConfig config = new ModConfig();
        ForgePlatformHelper platformHelper = new ForgePlatformHelper();
        ForgeRenderer renderer = new ForgeRenderer();

        core = new SpawnerSphereCore(platformHelper, renderer, config);

        // Register keybinding
        toggleKey = new KeyBinding(
            "key.spawnersphere.toggle",
            InputMappings.Type.KEYSYM,
            GLFW.GLFW_KEY_B,
            "category.spawnersphere"
        );
        ClientRegistry.registerKeyBinding(toggleKey);

        // Register Forge event listeners
        MinecraftForge.EVENT_BUS.register(new ClientEvents());
    }

    /**
     * Client-side event handlers
     */
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ClientEvents {

        @SubscribeEvent
        public static void onClientTick(TickEvent.ClientTickEvent event) {
            if (event.phase != TickEvent.Phase.END) return;

            Minecraft client = Minecraft.getInstance();

            // Handle toggle key
            if (toggleKey.isPressed()) {
                if (client.player != null && client.world != null) {
                    core.toggle(client.player, client.world);
                }
            }

            // Periodic tick for scanning
            if (client.player != null && client.world != null) {
                core.tick(client.player, client.world);
            }
        }

        @SubscribeEvent
        public static void onRenderWorldLast(RenderWorldLastEvent event) {
            if (!core.isEnabled()) return;

            Minecraft client = Minecraft.getInstance();
            if (client.player == null || client.world == null) return;

            // Prepare rendering context
            MatrixStack matrixStack = event.getMatrixStack();
            Vector3d cameraPos = client.gameRenderer.getActiveRenderInfo().getProjectedView();

            matrixStack.push();
            matrixStack.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);

            // Create render context and delegate to core
            ForgeRenderer.RenderContext renderContext = new ForgeRenderer.RenderContext(matrixStack);

            core.render(renderContext, client.player, client.world);

            matrixStack.pop();
        }
    }

    /**
     * Get the core instance (for testing or external access)
     */
    public static SpawnerSphereCore getCore() {
        return core;
    }
}
