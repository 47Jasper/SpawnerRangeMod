package com.example.spawnersphere.common;

import com.example.spawnersphere.common.config.ModConfig;
import com.example.spawnersphere.common.platform.IPlatformHelper;
import com.example.spawnersphere.common.platform.IPlatformHelper.Position;
import com.example.spawnersphere.common.platform.IRenderer;
import com.example.spawnersphere.common.platform.IRenderer.SphereColor;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Core mod logic - platform agnostic
 * This class handles spawner tracking, distance calculations, and orchestrates rendering
 */
public class SpawnerSphereCore {

    private final IPlatformHelper platformHelper;
    private final IRenderer renderer;
    private final ModConfig config;

    private boolean enabled = false;
    private final Set<SpawnerData> spawnerPositions = new HashSet<>();
    private long lastScanTime = 0;

    public SpawnerSphereCore(
        @NotNull IPlatformHelper platformHelper,
        @NotNull IRenderer renderer,
        @NotNull ModConfig config
    ) {
        this.platformHelper = platformHelper;
        this.renderer = renderer;
        this.config = config;
    }

    /**
     * Toggle the mod on/off
     */
    public void toggle(Object player, Object world) {
        enabled = !enabled;
        if (enabled) {
            scanForSpawners(player, world);
            platformHelper.sendMessage(player, "§aSpawner spheres enabled", true);
        } else {
            spawnerPositions.clear();
            platformHelper.sendMessage(player, "§cSpawner spheres disabled", true);
        }
    }

    /**
     * Check if the mod is enabled
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Periodic tick - should be called from the platform's tick event
     */
    public void tick(Object player, Object world) {
        if (!enabled) return;

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastScanTime > config.getScanInterval()) {
            scanForSpawners(player, world);
        }
    }

    /**
     * Scan for spawners around the player
     */
    public void scanForSpawners(Object player, Object world) {
        spawnerPositions.clear();
        Position playerPos = platformHelper.getPlayerPosition(player);

        int scanRadius = config.getScanRadius();
        int playerBlockX = (int) Math.floor(playerPos.x);
        int playerBlockY = (int) Math.floor(playerPos.y);
        int playerBlockZ = (int) Math.floor(playerPos.z);

        // Scan in a cube around the player
        for (int x = -scanRadius; x <= scanRadius; x++) {
            for (int y = -scanRadius; y <= scanRadius; y++) {
                for (int z = -scanRadius; z <= scanRadius; z++) {
                    Object blockPos = platformHelper.createBlockPos(
                        playerBlockX + x,
                        playerBlockY + y,
                        playerBlockZ + z
                    );

                    if (platformHelper.isSpawner(world, blockPos)) {
                        Position center = platformHelper.getBlockCenter(blockPos);
                        spawnerPositions.add(new SpawnerData(blockPos, center));
                    }
                }
            }
        }

        lastScanTime = System.currentTimeMillis();
    }

    /**
     * Render all tracked spawners
     * Should be called from the platform's render event
     */
    public void render(Object renderContext, Object player, Object world) {
        if (!enabled || spawnerPositions.isEmpty()) return;

        Position playerPos = platformHelper.getPlayerPosition(player);
        int sphereRadius = config.getSphereRadius();
        int scanRadius = config.getScanRadius();

        // Remove spawners that no longer exist and render the rest
        Iterator<SpawnerData> iterator = spawnerPositions.iterator();
        while (iterator.hasNext()) {
            SpawnerData spawner = iterator.next();

            // Verify spawner still exists
            if (!platformHelper.isSpawner(world, spawner.blockPos)) {
                iterator.remove();
                continue;
            }

            // Calculate distance from player to spawner center
            double distance = playerPos.distanceTo(spawner.center);

            // Only render if within extended range
            if (distance < scanRadius + sphereRadius) {
                // Determine if player is within activation range
                boolean inRange = distance <= sphereRadius;

                // Select color based on range
                SphereColor color = inRange ?
                    new SphereColor(
                        config.getInsideRangeColor().getRedFloat(),
                        config.getInsideRangeColor().getGreenFloat(),
                        config.getInsideRangeColor().getBlueFloat(),
                        config.getInsideRangeColor().getAlphaFloat()
                    ) :
                    new SphereColor(
                        config.getOutsideRangeColor().getRedFloat(),
                        config.getOutsideRangeColor().getGreenFloat(),
                        config.getOutsideRangeColor().getBlueFloat(),
                        config.getOutsideRangeColor().getAlphaFloat()
                    );

                // Render the sphere
                renderer.renderSphere(
                    renderContext,
                    spawner.center.x,
                    spawner.center.y,
                    spawner.center.z,
                    sphereRadius,
                    color
                );

                // Optionally show distance in action bar
                if (config.isShowDistanceInActionBar() && inRange) {
                    String distanceText = String.format("§eSpawner: %.1f blocks", distance);
                    platformHelper.sendMessage(player, distanceText, true);
                }
            }
        }
    }

    /**
     * Get the current configuration
     */
    public ModConfig getConfig() {
        return config;
    }

    /**
     * Data class to hold spawner position information
     */
    private static class SpawnerData {
        final Object blockPos;
        final Position center;

        SpawnerData(Object blockPos, Position center) {
            this.blockPos = blockPos;
            this.center = center;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            SpawnerData that = (SpawnerData) o;
            return blockPos.equals(that.blockPos);
        }

        @Override
        public int hashCode() {
            return blockPos.hashCode();
        }
    }
}
