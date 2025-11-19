package com.example.spawnersphere.common;

import com.example.spawnersphere.common.config.ModConfig;
import com.example.spawnersphere.common.performance.FrustumCuller;
import com.example.spawnersphere.common.performance.LODCalculator;
import com.example.spawnersphere.common.performance.SpatialIndex;
import com.example.spawnersphere.common.platform.IPlatformHelper;
import com.example.spawnersphere.common.platform.IPlatformHelper.Position;
import com.example.spawnersphere.common.platform.IRenderer;
import com.example.spawnersphere.common.platform.IRenderer.SphereColor;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Core mod logic - platform agnostic
 * This class handles spawner tracking, distance calculations, and orchestrates rendering
 */
public class SpawnerSphereCore {

    private final IPlatformHelper platformHelper;
    private final IRenderer renderer;
    private final ModConfig config;

    private boolean enabled = false;
    // Use HashMap for O(1) lookup by blockPos (fixes O(n*m) nested loop issue)
    private final Map<Object, SpawnerData> spawnerPositions = new HashMap<>();
    private final SpatialIndex spatialIndex = new SpatialIndex();
    private long lastScanTime = 0;
    private Position lastScanPosition = null;

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
            spatialIndex.clear(); // Fix: clear spatial index to prevent memory leak
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

        Position currentPos = platformHelper.getPlayerPosition(player);
        long currentTime = System.currentTimeMillis();
        boolean timePassed = currentTime - lastScanTime > config.getScanInterval();

        // Movement-based lazy scanning
        boolean movedSignificantly = false;
        if (lastScanPosition != null) {
            double distance = currentPos.distanceTo(lastScanPosition);
            movedSignificantly = distance >= config.getMovementThreshold();
        }

        if (timePassed || movedSignificantly || lastScanPosition == null) {
            scanForSpawners(player, world);
        }

        // Periodic cleanup: Remove spawners that no longer exist (optimization - moved from render)
        // This is less frequent than rendering, improving performance
        cleanupInvalidSpawners(world);
    }

    /**
     * Remove spawners that no longer exist in the world
     * Called periodically from tick to avoid expensive checks every frame
     */
    private void cleanupInvalidSpawners(Object world) {
        List<Object> toRemove = new ArrayList<>();
        for (Map.Entry<Object, SpawnerData> entry : spawnerPositions.entrySet()) {
            if (!platformHelper.isSpawner(world, entry.getKey())) {
                toRemove.add(entry.getKey());
            }
        }

        // Remove invalid spawners from both data structures
        for (Object blockPos : toRemove) {
            SpawnerData data = spawnerPositions.remove(blockPos);
            if (data != null && config.isEnableSpatialIndexing()) {
                spatialIndex.remove(blockPos, data.center);
            }
        }
    }

    /**
     * Scan for spawners around the player
     */
    public void scanForSpawners(Object player, Object world) {
        spawnerPositions.clear();
        spatialIndex.clear();
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
                        SpawnerData data = new SpawnerData(blockPos, center);
                        spawnerPositions.put(blockPos, data);  // Use put() for HashMap

                        // Add to spatial index for efficient queries
                        if (config.isEnableSpatialIndexing()) {
                            spatialIndex.add(blockPos, center);
                        }
                    }
                }
            }
        }

        lastScanTime = System.currentTimeMillis();
        lastScanPosition = playerPos;
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

        // Determine which spawners to render
        List<SpawnerData> spawnersToRender;
        if (config.isEnableSpatialIndexing()) {
            // Use spatial index for efficient nearby query
            List<SpatialIndex.SpawnerEntry> nearbyEntries = spatialIndex.getNearby(
                playerPos,
                scanRadius + sphereRadius
            );

            // Use HashMap.get() for O(1) lookup instead of O(n*m) nested loop
            spawnersToRender = new ArrayList<>();
            for (SpatialIndex.SpawnerEntry entry : nearbyEntries) {
                SpawnerData data = spawnerPositions.get(entry.blockPos);
                if (data != null) {
                    spawnersToRender.add(data);
                }
            }
        } else {
            spawnersToRender = new ArrayList<>(spawnerPositions.values());
        }

        // Track nearest spawner for action bar message (to avoid spam with multiple spawners)
        double nearestDistance = Double.MAX_VALUE;
        SpawnerData nearestSpawner = null;

        // Render all tracked spawners (validation moved to tick phase for performance)
        for (SpawnerData spawner : spawnersToRender) {
            // Calculate distance from player to spawner center
            double distance = playerPos.distanceTo(spawner.center);

            // Only render if within extended range
            if (distance < scanRadius + sphereRadius) {
                // Frustum culling (if enabled)
                if (config.isEnableFrustumCulling()) {
                    IPlatformHelper.LookVector lookVec = platformHelper.getPlayerLookVector(player);
                    boolean isVisible = FrustumCuller.isVisible(
                        spawner.center,
                        sphereRadius,
                        playerPos,
                        lookVec.x, lookVec.y, lookVec.z,
                        90.0f // Default FOV, could be made configurable
                    );
                    if (!isVisible) {
                        continue; // Skip rendering this sphere
                    }
                }

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

                // Calculate segment count based on distance (LOD)
                int segments;
                if (config.isEnableLOD()) {
                    segments = LODCalculator.calculateSegments(
                        distance,
                        config.getLodMaxSegments(),
                        config.getLodMinSegments(),
                        config.getLodDistance()
                    );
                } else {
                    segments = config.getSphereSegments();
                }

                // Render the sphere
                renderer.renderSphere(
                    renderContext,
                    spawner.center.x,
                    spawner.center.y,
                    spawner.center.z,
                    sphereRadius,
                    color,
                    segments
                );

                // Track nearest spawner in range for action bar message
                if (config.isShowDistanceInActionBar() && inRange && distance < nearestDistance) {
                    nearestDistance = distance;
                    nearestSpawner = spawner;
                }
            }
        }

        // Show distance to nearest spawner (avoids spam with multiple spawners)
        if (config.isShowDistanceInActionBar() && nearestSpawner != null) {
            String distanceText = String.format("§eSpawner: %.1f blocks", nearestDistance);
            platformHelper.sendMessage(player, distanceText, true);
        }
    }

    /**
     * Get the current configuration
     */
    public ModConfig getConfig() {
        return config;
    }

    /**
     * Trigger a rescan immediately (e.g., when spawner placed nearby)
     * This bypasses the normal scan interval and movement threshold
     */
    public void triggerRescan(Object player, Object world) {
        if (enabled) {
            scanForSpawners(player, world);
        }
    }

    /**
     * Check if a position is within scan radius to determine if rescan should be triggered
     */
    public boolean isWithinScanRadius(Position position, Object player) {
        if (!enabled) return false;
        Position playerPos = platformHelper.getPlayerPosition(player);
        double distance = playerPos.distanceTo(position);
        return distance <= config.getScanRadius();
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
