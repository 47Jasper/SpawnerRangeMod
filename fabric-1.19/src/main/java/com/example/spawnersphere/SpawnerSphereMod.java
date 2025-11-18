package com.example.spawnersphere;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.*;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;

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
    
    @Override
    public void onInitializeClient() {
        toggleKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.spawnersphere.toggle",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_B,
            "category.spawnersphere"
        ));
        
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (toggleKey.wasPressed()) {
                toggle(client);
            }
            
            if (enabled && System.currentTimeMillis() - lastScanTime > SCAN_INTERVAL) {
                scanForSpawners(client);
            }
        });
        
        WorldRenderEvents.AFTER_TRANSLUCENT.register(this::renderSpheres);
    }
    
    private void toggle(MinecraftClient client) {
        enabled = !enabled;
        if (enabled) {
            scanForSpawners(client);
            if (client.player != null) {
                client.player.sendMessage(Text.literal("§aSpawner spheres enabled"), true);
            }
        } else {
            spawnerPositions.clear();
            if (client.player != null) {
                client.player.sendMessage(Text.literal("§cSpawner spheres disabled"), true);
            }
        }
    }
    
    private void scanForSpawners(MinecraftClient client) {
        if (client.world == null || client.player == null) return;
        
        spawnerPositions.clear();
        World world = client.world;
        BlockPos playerPos = client.player.getBlockPos();
        
        for (int x = -SCAN_RADIUS; x <= SCAN_RADIUS; x++) {
            for (int y = -SCAN_RADIUS; y <= SCAN_RADIUS; y++) {
                for (int z = -SCAN_RADIUS; z <= SCAN_RADIUS; z++) {
                    BlockPos pos = playerPos.add(x, y, z);
                    if (world.getBlockState(pos).isOf(Blocks.SPAWNER)) {
                        spawnerPositions.add(new BlockPos(pos));
                    }
                }
            }
        }
        lastScanTime = System.currentTimeMillis();
    }
    
    private void renderSpheres(WorldRenderContext context) {
        if (!enabled || spawnerPositions.isEmpty()) return;
        
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;
        
        Camera camera = context.camera();
        Vec3d cameraPos = camera.getPos();
        MatrixStack matrices = context.matrixStack();
        VertexConsumerProvider.Immediate vertexConsumers = context.consumers();
        
        if (vertexConsumers == null) {
            vertexConsumers = client.getBufferBuilders().getEntityVertexConsumers();
        }
        
        matrices.push();
        matrices.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);
        
        for (BlockPos spawnerPos : new HashSet<>(spawnerPositions)) {
            if (!client.world.getBlockState(spawnerPos).isOf(Blocks.SPAWNER)) {
                spawnerPositions.remove(spawnerPos);
                continue;
            }
            
            double distance = client.player.getPos().distanceTo(Vec3d.ofCenter(spawnerPos));

            if (distance < SCAN_RADIUS + SPHERE_RADIUS) {
                renderSphere(
                    matrices,
                    vertexConsumers,
                    spawnerPos.getX() + 0.5,
                    spawnerPos.getY() + 0.5,
                    spawnerPos.getZ() + 0.5,
                    SPHERE_RADIUS,
                    distance <= SPHERE_RADIUS
                );
            }
        }
        
        matrices.pop();
    }
    
    private void renderSphere(MatrixStack matrices, VertexConsumerProvider vertexConsumers,
                              double x, double y, double z, float radius, boolean inRange) {
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getLines());
        Matrix4f matrix = matrices.peek().getPositionMatrix();

        // Color based on whether player is in range
        // Outside range: Green/Yellow (0.5, 1.0, 0.0)
        // Inside range: Yellow/Red (1.0, 0.5, 0.0)
        float r = inRange ? 1.0f : 0.5f;
        float g = inRange ? 0.5f : 1.0f;
        float b = 0.0f;
        float alpha = inRange ? 0.4f : 0.2f;

        int segments = 24;
        
        // Draw latitude circles
        for (int lat = -6; lat <= 6; lat += 2) {
            float latAngle = (float) (lat * Math.PI / 12);
            float circleRadius = radius * (float) Math.cos(latAngle);
            float circleY = radius * (float) Math.sin(latAngle);
            
            for (int i = 0; i < segments; i++) {
                float angle1 = (float) (2 * Math.PI * i / segments);
                float angle2 = (float) (2 * Math.PI * (i + 1) / segments);
                
                float x1 = circleRadius * (float) Math.cos(angle1);
                float z1 = circleRadius * (float) Math.sin(angle1);
                float x2 = circleRadius * (float) Math.cos(angle2);
                float z2 = circleRadius * (float) Math.sin(angle2);

                vertexConsumer.vertex(matrix, (float)(x + x1), (float)(y + circleY), (float)(z + z1))
                    .color(r, g, b, alpha).normal(0, 1, 0);
                vertexConsumer.vertex(matrix, (float)(x + x2), (float)(y + circleY), (float)(z + z2))
                    .color(r, g, b, alpha).normal(0, 1, 0);
            }
        }
        
        // Draw longitude circles
        for (int lon = 0; lon < 6; lon++) {
            float lonAngle = (float) (lon * Math.PI / 6);
            
            for (int i = 0; i < segments; i++) {
                float angle1 = (float) (2 * Math.PI * i / segments);
                float angle2 = (float) (2 * Math.PI * (i + 1) / segments);
                
                float x1 = radius * (float) (Math.sin(angle1) * Math.cos(lonAngle));
                float y1 = radius * (float) Math.cos(angle1);
                float z1 = radius * (float) (Math.sin(angle1) * Math.sin(lonAngle));
                
                float x2 = radius * (float) (Math.sin(angle2) * Math.cos(lonAngle));
                float y2 = radius * (float) Math.cos(angle2);
                float z2 = radius * (float) (Math.sin(angle2) * Math.sin(lonAngle));

                vertexConsumer.vertex(matrix, (float)(x + x1), (float)(y + y1), (float)(z + z1))
                    .color(r, g, b, alpha).normal(0, 1, 0);
                vertexConsumer.vertex(matrix, (float)(x + x2), (float)(y + y2), (float)(z + z2))
                    .color(r, g, b, alpha).normal(0, 1, 0);
            }
        }
    }
}
