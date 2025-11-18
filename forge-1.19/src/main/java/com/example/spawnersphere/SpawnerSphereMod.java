package com.example.spawnersphere;

import com.example.spawnersphere.common.SpawnerSphereCore;
import com.example.spawnersphere.common.config.ModConfig;
import com.example.spawnersphere.platform.ForgePlatformHelper;
import com.example.spawnersphere.platform.ForgeRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

/**
 * Forge implementation for MC 1.20+ using common architecture
 */
@Mod("spawnersphere")
public class SpawnerSphereMod {

    public static final String MODID = "spawnersphere";

    private static SpawnerSphereCore core;
    private static KeyMapping toggleKey;

    public SpawnerSphereMod() {
        // Register ourselves for client setup
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::registerKeyMappings);
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        // Initialize the common core with platform-specific implementations
        ModConfig config = new ModConfig();
        ForgePlatformHelper platformHelper = new ForgePlatformHelper();
        ForgeRenderer renderer = new ForgeRenderer();

        core = new SpawnerSphereCore(platformHelper, renderer, config);

        // Register Forge event listeners
        MinecraftForge.EVENT_BUS.register(new ClientEvents());
    }

    private void registerKeyMappings(final RegisterKeyMappingsEvent event) {
        toggleKey = new KeyMapping(
            "key.spawnersphere.toggle",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_B,
            "category.spawnersphere"
        );
        event.register(toggleKey);
    }

    /**
     * Client-side event handlers
     */
    @Mod.EventBusSubscriber(modid = MODID, value = Dist.CLIENT)
    public static class ClientEvents {

        @SubscribeEvent
        public static void onClientTick(TickEvent.ClientTickEvent event) {
            if (event.phase != TickEvent.Phase.END) return;

            Minecraft client = Minecraft.getInstance();

            // Handle toggle key
            while (toggleKey.consumeClick()) {
                if (client.player != null && client.level != null) {
                    core.toggle(client.player, client.level);
                }
            }

            // Periodic tick for scanning
            if (client.player != null && client.level != null) {
                core.tick(client.player, client.level);
            }
        }

        @SubscribeEvent
        public static void onRenderLevelStage(RenderLevelStageEvent event) {
            if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) return;
            if (!core.isEnabled()) return;

            Minecraft client = Minecraft.getInstance();
            if (client.player == null || client.level == null) return;

            // Prepare rendering context
            PoseStack poseStack = event.getPoseStack();
            Vec3 cameraPos = event.getCamera().getPosition();

            poseStack.pushPose();
            poseStack.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);

            // Get buffer source from event or create one
            MultiBufferSource.BufferSource bufferSource = client.renderBuffers().bufferSource();

            // Create render context and delegate to core
            ForgeRenderer.RenderContext renderContext =
                new ForgeRenderer.RenderContext(poseStack, bufferSource);

            core.render(renderContext, client.player, client.level);

            poseStack.popPose();

            // Ensure everything is drawn
            bufferSource.endBatch();
        }
    }

    /**
     * Get the core instance (for testing or external access)
     */
    public static SpawnerSphereCore getCore() {
        return core;
    }
}
