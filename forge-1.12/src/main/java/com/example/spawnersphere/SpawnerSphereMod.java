package com.example.spawnersphere;

import com.example.spawnersphere.common.SpawnerSphereCore;
import com.example.spawnersphere.common.config.ModConfig;
import com.example.spawnersphere.platform.ForgePlatformHelper;
import com.example.spawnersphere.platform.ForgeRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

/**
 * Forge implementation for MC 1.8.9-1.12.2 using common architecture
 */
@Mod(
    modid = SpawnerSphereMod.MODID,
    name = "Spawner Sphere",
    version = "1.0.0",
    clientSideOnly = true,
    acceptedMinecraftVersions = "[1.8.9,1.13)"
)
public class SpawnerSphereMod {

    public static final String MODID = "spawnersphere";

    private static SpawnerSphereCore core;
    private static KeyBinding toggleKey;
    private static Minecraft mc;

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        mc = Minecraft.getMinecraft();

        // Initialize the common core with platform-specific implementations
        ModConfig config = new ModConfig();
        ForgePlatformHelper platformHelper = new ForgePlatformHelper();
        ForgeRenderer renderer = new ForgeRenderer();

        core = new SpawnerSphereCore(platformHelper, renderer, config);

        // Register keybinding
        toggleKey = new KeyBinding(
            "key.spawnersphere.toggle",
            KeyConflictContext.IN_GAME,
            Keyboard.KEY_B,
            "category.spawnersphere"
        );
        ClientRegistry.registerKeyBinding(toggleKey);

        // Register event handler
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        // Handle toggle key
        if (toggleKey.isPressed()) {
            if (mc.player != null && mc.world != null) {
                core.toggle(mc.player, mc.world);
            }
        }

        // Periodic tick for scanning
        if (mc.player != null && mc.world != null) {
            core.tick(mc.player, mc.world);
        }
    }

    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        if (!core.isEnabled()) return;
        if (mc.player == null || mc.world == null) return;

        // Setup OpenGL state
        GL11.glPushMatrix();

        // Calculate camera position for translation
        double playerX = mc.player.lastTickPosX + (mc.player.posX - mc.player.lastTickPosX) * event.getPartialTicks();
        double playerY = mc.player.lastTickPosY + (mc.player.posY - mc.player.lastTickPosY) * event.getPartialTicks();
        double playerZ = mc.player.lastTickPosZ + (mc.player.posZ - mc.player.lastTickPosZ) * event.getPartialTicks();

        GL11.glTranslated(-playerX, -playerY, -playerZ);

        // Setup rendering state
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glLineWidth(2.0f);

        // Delegate to core
        ForgeRenderer.RenderContext renderContext =
            new ForgeRenderer.RenderContext(event.getPartialTicks());
        core.render(renderContext, mc.player, mc.world);

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
