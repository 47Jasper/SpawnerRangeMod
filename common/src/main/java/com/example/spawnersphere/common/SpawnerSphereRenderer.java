package com.example.spawnersphere.common;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import java.util.HashSet;
import java.util.Set;

public class SpawnerSphereRenderer {
    private static boolean enabled = false;
    private static final Set<BlockPos> spawnerPositions = new HashSet<>();
    private static final int SPHERE_RADIUS = 16;
    private static final int SCAN_RADIUS = 64;
    
    public static void toggle() {
        enabled = !enabled;
        if (enabled) {
            scanForSpawners();
        } else {
            spawnerPositions.clear();
        }
    }
    
    public static boolean isEnabled() {
        return enabled;
    }
    
    public static Set<BlockPos> getSpawnerPositions() {
        return spawnerPositions;
    }
    
    public static void scanForSpawners() {
        spawnerPositions.clear();
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null || client.player == null) return;
        
        World world = client.world;
        BlockPos playerPos = client.player.getBlockPos();
        
        for (int x = -SCAN_RADIUS; x <= SCAN_RADIUS; x++) {
            for (int y = -SCAN_RADIUS; y <= SCAN_RADIUS; y++) {
                for (int z = -SCAN_RADIUS; z <= SCAN_RADIUS; z++) {
                    BlockPos pos = playerPos.add(x, y, z);
                    if (isSpawner(world, pos)) {
                        spawnerPositions.add(pos);
                    }
                }
            }
        }
    }
    
    private static boolean isSpawner(World world, BlockPos pos) {
        // This method will be implemented in version-specific modules
        // due to differences in block registry names across versions
        return false;
    }
    
    public static int getSphereRadius() {
        return SPHERE_RADIUS;
    }
}
