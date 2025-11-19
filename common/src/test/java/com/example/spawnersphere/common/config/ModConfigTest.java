package com.example.spawnersphere.common.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ModConfig
 */
public class ModConfigTest {

    private ModConfig config;

    @BeforeEach
    public void setUp() {
        config = new ModConfig();
    }

    @Test
    public void testDefaultValues() {
        // General settings
        assertEquals(16, config.getSphereRadius());
        assertEquals(64, config.getScanRadius());
        assertEquals(60000, config.getScanInterval());
        assertEquals(16.0, config.getMovementThreshold(), 0.01);
        assertFalse(config.isShowDistanceInActionBar());

        // Performance settings
        assertTrue(config.isEnableSpatialIndexing());
        assertFalse(config.isEnableFrustumCulling());
        assertTrue(config.isEnableLOD());
        assertEquals(32, config.getLodMaxSegments());
        assertEquals(16, config.getLodMinSegments());
        assertEquals(32.0, config.getLodDistance(), 0.01);

        // Rendering settings
        assertEquals(24, config.getSphereSegments());
        assertTrue(config.isRenderEquator());
    }

    @Test
    public void testSphereRadiusRange() {
        config.setSphereRadius(10);
        assertEquals(10, config.getSphereRadius());

        config.setSphereRadius(1);
        assertEquals(1, config.getSphereRadius());

        config.setSphereRadius(64);
        assertEquals(64, config.getSphereRadius());
    }

    @Test
    public void testScanRadiusRange() {
        config.setScanRadius(128);
        assertEquals(128, config.getScanRadius());

        config.setScanRadius(16);
        assertEquals(16, config.getScanRadius());

        config.setScanRadius(256);
        assertEquals(256, config.getScanRadius());
    }

    @Test
    public void testScanInterval() {
        config.setScanInterval(30000);
        assertEquals(30000, config.getScanInterval());

        config.setScanInterval(1000);
        assertEquals(1000, config.getScanInterval());
    }

    @Test
    public void testMovementThreshold() {
        config.setMovementThreshold(20.0);
        assertEquals(20.0, config.getMovementThreshold(), 0.01);

        config.setMovementThreshold(1.0);
        assertEquals(1.0, config.getMovementThreshold(), 0.01);

        config.setMovementThreshold(64.0);
        assertEquals(64.0, config.getMovementThreshold(), 0.01);
    }

    @Test
    public void testBooleanToggles() {
        config.setShowDistanceInActionBar(true);
        assertTrue(config.isShowDistanceInActionBar());

        config.setEnableSpatialIndexing(false);
        assertFalse(config.isEnableSpatialIndexing());

        config.setEnableFrustumCulling(true);
        assertTrue(config.isEnableFrustumCulling());

        config.setEnableLOD(false);
        assertFalse(config.isEnableLOD());

        config.setRenderEquator(false);
        assertFalse(config.isRenderEquator());
    }

    @Test
    public void testLODSettings() {
        config.setLodMaxSegments(48);
        assertEquals(48, config.getLodMaxSegments());

        config.setLodMinSegments(8);
        assertEquals(8, config.getLodMinSegments());

        config.setLodDistance(48.0);
        assertEquals(48.0, config.getLodDistance(), 0.01);
    }

    @Test
    public void testSphereSegments() {
        config.setSphereSegments(32);
        assertEquals(32, config.getSphereSegments());

        config.setSphereSegments(8);
        assertEquals(8, config.getSphereSegments());

        config.setSphereSegments(64);
        assertEquals(64, config.getSphereSegments());
    }
}
