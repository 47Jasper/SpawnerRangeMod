package com.example.spawnersphere.common;

import com.example.spawnersphere.common.config.ModConfig;
import com.example.spawnersphere.common.platform.IPlatformHelper;
import com.example.spawnersphere.common.platform.IRenderer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive tests for SpawnerSphereCore
 */
public class SpawnerSphereCoreTest {

    private SpawnerSphereCore core;
    private MockPlatformHelper platformHelper;
    private MockRenderer renderer;
    private ModConfig config;
    private MockPlayer player;
    private MockWorld world;

    @BeforeEach
    public void setUp() {
        config = new ModConfig();
        platformHelper = new MockPlatformHelper();
        renderer = new MockRenderer();
        core = new SpawnerSphereCore(platformHelper, renderer, config);
        player = new MockPlayer(0, 64, 0);
        world = new MockWorld();
    }

    @Test
    public void testInitialState() {
        assertFalse(core.isEnabled());
        assertEquals(config, core.getConfig());
    }

    @Test
    public void testToggleOn() {
        core.toggle(player, world);
        assertTrue(core.isEnabled());
        assertEquals("§aSpawner spheres enabled", platformHelper.lastMessage);
        assertTrue(platformHelper.lastActionBar);
    }

    @Test
    public void testToggleOff() {
        core.toggle(player, world);
        assertTrue(core.isEnabled());

        core.toggle(player, world);
        assertFalse(core.isEnabled());
        assertEquals("§cSpawner spheres disabled", platformHelper.lastMessage);
    }

    @Test
    public void testScanForSpawners() {
        // Add spawners to world
        world.addSpawner(5, 64, 5);
        world.addSpawner(-5, 64, -5);
        world.addSpawner(100, 64, 100); // Far away

        core.toggle(player, world);

        // Should find nearby spawners (within scan radius)
        assertTrue(core.isEnabled());
        // Verify scanning occurred (tested via render)
    }

    @Test
    public void testRenderWhenDisabled() {
        world.addSpawner(5, 64, 5);

        assertFalse(core.isEnabled());
        core.render(new Object(), player, world);

        // Should not render when disabled
        assertEquals(0, renderer.renderedSpheres.size());
    }

    @Test
    public void testRenderWhenEnabled() {
        world.addSpawner(5, 64, 5);

        core.toggle(player, world);
        core.render(new Object(), player, world);

        // Should render the spawner
        assertEquals(1, renderer.renderedSpheres.size());
    }

    @Test
    public void testColorChangesBasedOnDistance() {
        // Place spawner exactly at sphere radius boundary
        config.setSphereRadius(16);
        world.addSpawner(10, 64, 0); // Distance ~10 blocks

        core.toggle(player, world);
        core.render(new Object(), player, world);

        // Player is within range (10 < 16), should be red-ish (inside range color)
        assertEquals(1, renderer.renderedSpheres.size());
        MockRenderer.RenderedSphere sphere = renderer.renderedSpheres.get(0);
        // Inside range color (red-ish)
        assertTrue(sphere.color.red > sphere.color.green);
    }

    @Test
    public void testColorOutsideRange() {
        config.setSphereRadius(16);
        world.addSpawner(20, 64, 0); // Distance 20 blocks

        core.toggle(player, world);
        core.render(new Object(), player, world);

        // Player is outside range (20 > 16), should be green-ish (outside range color)
        assertEquals(1, renderer.renderedSpheres.size());
        MockRenderer.RenderedSphere sphere = renderer.renderedSpheres.get(0);
        // Outside range color (green-ish)
        assertTrue(sphere.color.green > sphere.color.red);
    }

    @Test
    public void testLODEnabled() {
        config.setEnableLOD(true);
        config.setLodMaxSegments(32);
        config.setLodMinSegments(16);
        config.setLodDistance(32.0);

        // Close spawner
        world.addSpawner(5, 64, 0);
        core.toggle(player, world);
        core.render(new Object(), player, world);

        MockRenderer.RenderedSphere sphere = renderer.renderedSpheres.get(0);
        // Close spawners should have max segments
        assertEquals(32, sphere.segments);
    }

    @Test
    public void testLODDisabled() {
        config.setEnableLOD(false);
        config.setSphereSegments(24);

        world.addSpawner(5, 64, 0);
        core.toggle(player, world);
        core.render(new Object(), player, world);

        MockRenderer.RenderedSphere sphere = renderer.renderedSpheres.get(0);
        // Should use configured sphere segments
        assertEquals(24, sphere.segments);
    }

    @Test
    public void testFrustumCulling() {
        config.setEnableFrustumCulling(true);

        // Spawner behind player (must be > sphereRadius * 2 to avoid "very close" check)
        world.addSpawner(0, 64, -40); // Distance 40 > 16*2=32
        // Player looking forward (+Z direction)
        player.lookVector = new IPlatformHelper.LookVector(0, 0, 1);

        core.toggle(player, world);
        core.render(new Object(), player, world);

        // Spawner behind should be culled
        assertEquals(0, renderer.renderedSpheres.size());
    }

    @Test
    public void testFrustumCullingDisabled() {
        config.setEnableFrustumCulling(false);

        // Spawner behind player
        world.addSpawner(0, 64, -20);
        player.lookVector = new IPlatformHelper.LookVector(0, 0, 1);

        core.toggle(player, world);
        core.render(new Object(), player, world);

        // Should still render when frustum culling disabled
        assertEquals(1, renderer.renderedSpheres.size());
    }

    @Test
    public void testSpatialIndexing() {
        config.setEnableSpatialIndexing(true);

        // Add many spawners
        for (int i = 0; i < 10; i++) {
            world.addSpawner(i * 5, 64, 0);
        }

        core.toggle(player, world);
        core.render(new Object(), player, world);

        // Should use spatial index for efficient queries
        assertTrue(renderer.renderedSpheres.size() > 0);
    }

    @Test
    public void testPeriodicTick() throws InterruptedException {
        config.setScanInterval(10); // Very short 10ms interval
        world.addSpawner(5, 64, 5);

        core.toggle(player, world);

        // Verify first spawner found
        renderer.renderedSpheres.clear();
        core.render(new Object(), player, world);
        assertEquals(1, renderer.renderedSpheres.size());

        // Add second spawner
        world.addSpawner(10, 64, 10);

        // Immediate tick should not rescan (too soon)
        renderer.renderedSpheres.clear();
        core.tick(player, world);
        core.render(new Object(), player, world);
        assertEquals(1, renderer.renderedSpheres.size());

        // Wait 100ms (10x the interval) to ensure time has passed
        Thread.sleep(100);

        // Now tick should trigger rescan and find both spawners
        renderer.renderedSpheres.clear();
        core.tick(player, world);
        core.render(new Object(), player, world);
        assertEquals(2, renderer.renderedSpheres.size());
    }

    @Test
    public void testMovementTriggersScan() {
        config.setMovementThreshold(10.0);
        world.addSpawner(5, 64, 5);

        core.toggle(player, world);
        core.tick(player, world);

        // Move player significantly
        player.x = 20;
        player.z = 20;

        // Add new spawner at new location
        world.addSpawner(25, 64, 25);

        core.tick(player, world);

        // Should have rescanned due to movement
        // Both spawners are still within scan radius (64) from new position (20,64,20):
        // - Spawner at (5,64,5): distance ~21.2 blocks
        // - Spawner at (25,64,25): distance ~7.07 blocks
        core.render(new Object(), player, world);
        assertEquals(2, renderer.renderedSpheres.size());
    }

    @Test
    public void testTriggerRescan() {
        world.addSpawner(5, 64, 5);

        core.toggle(player, world);

        // Add new spawner
        world.addSpawner(10, 64, 10);

        // Manually trigger rescan
        core.triggerRescan(player, world);

        core.render(new Object(), player, world);
        assertEquals(2, renderer.renderedSpheres.size());
    }

    @Test
    public void testIsWithinScanRadius() {
        config.setScanRadius(64);
        core.toggle(player, world);

        IPlatformHelper.Position nearPos = new IPlatformHelper.Position(30, 64, 30);
        IPlatformHelper.Position farPos = new IPlatformHelper.Position(100, 64, 100);

        assertTrue(core.isWithinScanRadius(nearPos, player));
        assertFalse(core.isWithinScanRadius(farPos, player));
    }

    @Test
    public void testIsWithinScanRadiusWhenDisabled() {
        IPlatformHelper.Position pos = new IPlatformHelper.Position(30, 64, 30);

        assertFalse(core.isEnabled());
        assertFalse(core.isWithinScanRadius(pos, player));
    }

    @Test
    public void testShowDistanceInActionBar() {
        config.setShowDistanceInActionBar(true);
        config.setSphereRadius(20);

        // Spawner within range
        world.addSpawner(5, 64, 0);

        core.toggle(player, world);
        platformHelper.lastMessage = null; // Clear toggle message

        core.render(new Object(), player, world);

        // Should show distance message
        assertNotNull(platformHelper.lastMessage);
        assertTrue(platformHelper.lastMessage.contains("Spawner:"));
        assertTrue(platformHelper.lastMessage.contains("blocks"));
    }

    @Test
    public void testSpawnerRemovalDetection() {
        MockBlockPos spawnerPos = world.addSpawner(5, 64, 5);

        core.toggle(player, world);
        core.render(new Object(), player, world);

        assertEquals(1, renderer.renderedSpheres.size());

        // Remove spawner
        world.removeSpawner(spawnerPos);

        renderer.renderedSpheres.clear();
        core.render(new Object(), player, world);

        // Should not render removed spawner
        assertEquals(0, renderer.renderedSpheres.size());
    }

    // ===== Mock Classes =====

    private static class MockPlayer {
        double x, y, z;
        IPlatformHelper.LookVector lookVector;

        MockPlayer(double x, double y, double z) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.lookVector = new IPlatformHelper.LookVector(0, 0, 1); // Looking forward by default
        }
    }

    private static class MockWorld {
        private final Map<MockBlockPos, Boolean> spawners = new HashMap<>();

        MockBlockPos addSpawner(int x, int y, int z) {
            MockBlockPos pos = new MockBlockPos(x, y, z);
            spawners.put(pos, true);
            return pos;
        }

        void removeSpawner(MockBlockPos pos) {
            spawners.remove(pos);
        }

        boolean isSpawner(MockBlockPos pos) {
            return spawners.getOrDefault(pos, false);
        }
    }

    private static class MockBlockPos {
        final int x, y, z;

        MockBlockPos(int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof MockBlockPos)) return false;
            MockBlockPos that = (MockBlockPos) o;
            return x == that.x && y == that.y && z == that.z;
        }

        @Override
        public int hashCode() {
            return 31 * (31 * x + y) + z;
        }
    }

    private class MockPlatformHelper implements IPlatformHelper {
        String lastMessage;
        boolean lastActionBar;

        @Override
        public Platform getPlatform() {
            return Platform.FABRIC;
        }

        @Override
        public boolean isSpawner(Object world, Object blockPos) {
            if (world instanceof MockWorld && blockPos instanceof MockBlockPos) {
                return ((MockWorld) world).isSpawner((MockBlockPos) blockPos);
            }
            return false;
        }

        @Override
        public Position getPlayerPosition(Object player) {
            if (player instanceof MockPlayer) {
                MockPlayer p = (MockPlayer) player;
                return new Position(p.x, p.y, p.z);
            }
            return new Position(0, 0, 0);
        }

        @Override
        public Object createBlockPos(int x, int y, int z) {
            return new MockBlockPos(x, y, z);
        }

        @Override
        public Position getBlockCenter(Object blockPos) {
            if (blockPos instanceof MockBlockPos) {
                MockBlockPos pos = (MockBlockPos) blockPos;
                return new Position(pos.x + 0.5, pos.y + 0.5, pos.z + 0.5);
            }
            return new Position(0, 0, 0);
        }

        @Override
        public double calculateDistance(Position pos1, Position pos2) {
            return pos1.distanceTo(pos2);
        }

        @Override
        public void sendMessage(Object player, String message, boolean actionBar) {
            this.lastMessage = message;
            this.lastActionBar = actionBar;
        }

        @Override
        public LookVector getPlayerLookVector(Object player) {
            if (player instanceof MockPlayer) {
                return ((MockPlayer) player).lookVector;
            }
            return new LookVector(0, 0, 1);
        }
    }

    private static class MockRenderer implements IRenderer {
        final List<RenderedSphere> renderedSpheres = new ArrayList<>();

        @Override
        public void renderSphere(Object context, double x, double y, double z, float radius, SphereColor color, int segments) {
            renderedSpheres.add(new RenderedSphere(x, y, z, radius, color, segments));
        }

        static class RenderedSphere {
            final double x, y, z;
            final float radius;
            final SphereColor color;
            final int segments;

            RenderedSphere(double x, double y, double z, float radius, SphereColor color, int segments) {
                this.x = x;
                this.y = y;
                this.z = z;
                this.radius = radius;
                this.color = color;
                this.segments = segments;
            }
        }
    }
}
