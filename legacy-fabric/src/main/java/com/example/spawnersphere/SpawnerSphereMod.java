package com.example.spawnersphere;

import net.fabricmc.api.ClientModInitializer;
import net.legacyfabric.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexBuffer;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.init.Blocks;
import net.minecraft.text.LiteralText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.util.HashSet;
import java.util.Set;

public class SpawnerSphereMod implements ClientModInitializer {
    private static KeyBinding toggleKey;
    private static boolean enabled = false;
    private static final Set<BlockPos> spawnerPositions = new HashSet<>();
    private static final int SPHERE_RADIUS = 16;
    private static final int SCAN_RADIUS = 64;
    private static long lastScanTime = 0;
    private static final long SCAN_INTERVAL = 5000;
    private static MinecraftClient client;
    
    @Override
    public void onInitializeClient() {
        client = MinecraftClient.getInstance();
        
        // Register keybinding
        toggleKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.spawnersphere.toggle",
            Keyboard.KEY_B,
            "category.spawnersphere"
        ));
    }
    
    public static void onTick() {
        if (toggleKey != null && toggleKey.wasPressed()) {
            toggle();
        }
        
        // Periodic rescan when enabled
        if (enabled && System.currentTimeMillis() - lastScanTime > SCAN_INTERVAL) {
            scanForSpawners();
        }
    }
    
    private static void toggle() {
        enabled = !enabled;
        if (enabled) {
            scanForSpawners();
            if (client.player != null) {
                client.player.addChatMessage(new LiteralText("§aSpawner spheres enabled"));
            }
        } else {
            spawnerPositions.clear();
            if (client.player != null) {
                client.player.addChatMessage(new LiteralText("§cSpawner spheres disabled"));
            }
        }
    }
    
    private static void scanForSpawners() {
        if (client.world == null || client.player == null) return;
        
        spawnerPositions.clear();
        World world = client.world;
        BlockPos playerPos = new BlockPos(client.player);
        
        for (int x = -SCAN_RADIUS; x <= SCAN_RADIUS; x++) {
            for (int y = -SCAN_RADIUS; y <= SCAN_RADIUS; y++) {
                for (int z = -SCAN_RADIUS; z <= SCAN_RADIUS; z++) {
                    BlockPos pos = playerPos.add(x, y, z);
                    Block block = world.getBlockState(pos).getBlock();
                    if (block == Blocks.MOB_SPAWNER) {
                        spawnerPositions.add(new BlockPos(pos));
                    }
                }
            }
        }
        lastScanTime = System.currentTimeMillis();
    }
    
    public static void renderSpheres(float partialTicks) {
        if (!enabled || spawnerPositions.isEmpty()) return;
        if (client.player == null) return;
        
        double playerX = client.player.lastTickPosX + (client.player.posX - client.player.lastTickPosX) * partialTicks;
        double playerY = client.player.lastTickPosY + (client.player.posY - client.player.lastTickPosY) * partialTicks;
        double playerZ = client.player.lastTickPosZ + (client.player.posZ - client.player.lastTickPosZ) * partialTicks;
        
        GL11.glPushMatrix();
        GL11.glTranslated(-playerX, -playerY, -playerZ);
        
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glLineWidth(2.0f);
        
        for (BlockPos spawnerPos : new HashSet<>(spawnerPositions)) {
            // Check if spawner still exists
            Block block = client.world.getBlockState(spawnerPos).getBlock();
            if (block != Blocks.MOB_SPAWNER) {
                spawnerPositions.remove(spawnerPos);
                continue;
            }
            
            double distance = client.player.getDistanceSq(
                spawnerPos.getX() + 0.5,
                spawnerPos.getY() + 0.5,
                spawnerPos.getZ() + 0.5
            );
            distance = Math.sqrt(distance);
            
            if (distance < SCAN_RADIUS + SPHERE_RADIUS) {
                renderSphere(
                    spawnerPos.getX() + 0.5,
                    spawnerPos.getY() + 0.5,
                    spawnerPos.getZ() + 0.5,
                    SPHERE_RADIUS,
                    distance <= SPHERE_RADIUS ? 0.4f : 0.2f
                );
            }
        }
        
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
    }
    
    private static void renderSphere(double x, double y, double z, float radius, float alpha) {
        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer buffer = tessellator.getBuffer();
        
        int segments = 32;
        
        // Draw latitude circles
        GL11.glColor4f(0.0f, 1.0f, 0.0f, alpha);
        for (int lat = -8; lat <= 8; lat++) {
            if (lat == 0) continue;
            float latAngle = (float) (lat * Math.PI / 16);
            float circleRadius = radius * (float) Math.cos(latAngle);
            float circleY = radius * (float) Math.sin(latAngle);
            
            buffer.begin(GL11.GL_LINE_LOOP, VertexFormats.POSITION);
            for (int i = 0; i <= segments; i++) {
                float angle = (float) (2 * Math.PI * i / segments);
                float dx = circleRadius * (float) Math.cos(angle);
                float dz = circleRadius * (float) Math.sin(angle);
                buffer.vertex(x + dx, y + circleY, z + dz).next();
            }
            tessellator.draw();
        }
        
        // Draw equator
        GL11.glColor4f(1.0f, 1.0f, 0.0f, alpha * 1.5f);
        buffer.begin(GL11.GL_LINE_LOOP, VertexFormats.POSITION);
        for (int i = 0; i <= segments; i++) {
            float angle = (float) (2 * Math.PI * i / segments);
            float dx = radius * (float) Math.cos(angle);
            float dz = radius * (float) Math.sin(angle);
            buffer.vertex(x + dx, y, z + dz).next();
        }
        tessellator.draw();
        
        // Draw longitude circles
        GL11.glColor4f(0.0f, 1.0f, 0.0f, alpha);
        for (int lon = 0; lon < 8; lon++) {
            float lonAngle = (float) (lon * Math.PI / 8);
            
            buffer.begin(GL11.GL_LINE_LOOP, VertexFormats.POSITION);
            for (int i = 0; i <= segments; i++) {
                float angle = (float) (2 * Math.PI * i / segments);
                float dx = radius * (float) (Math.sin(angle) * Math.cos(lonAngle));
                float dy = radius * (float) Math.cos(angle);
                float dz = radius * (float) (Math.sin(angle) * Math.sin(lonAngle));
                buffer.vertex(x + dx, y + dy, z + dz).next();
            }
            tessellator.draw();
        }
    }
}
