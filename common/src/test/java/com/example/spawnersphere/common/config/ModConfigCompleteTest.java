package com.example.spawnersphere.common.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Complete unit tests for ModConfig with 100% coverage including ColorConfig
 */
public class ModConfigCompleteTest {

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
    public void testSphereRadiusValidRange() {
        config.setSphereRadius(10);
        assertEquals(10, config.getSphereRadius());

        config.setSphereRadius(1);
        assertEquals(1, config.getSphereRadius());

        config.setSphereRadius(64);
        assertEquals(64, config.getSphereRadius());
    }

    @Test
    public void testSphereRadiusClampingMin() {
        config.setSphereRadius(0);
        assertEquals(1, config.getSphereRadius()); // Clamped to min

        config.setSphereRadius(-10);
        assertEquals(1, config.getSphereRadius()); // Clamped to min
    }

    @Test
    public void testSphereRadiusClampingMax() {
        config.setSphereRadius(100);
        assertEquals(64, config.getSphereRadius()); // Clamped to max

        config.setSphereRadius(999);
        assertEquals(64, config.getSphereRadius()); // Clamped to max
    }

    @Test
    public void testScanRadiusValidRange() {
        config.setScanRadius(128);
        assertEquals(128, config.getScanRadius());

        config.setScanRadius(16);
        assertEquals(16, config.getScanRadius());

        config.setScanRadius(256);
        assertEquals(256, config.getScanRadius());
    }

    @Test
    public void testScanRadiusClampingMin() {
        config.setScanRadius(10);
        assertEquals(16, config.getScanRadius()); // Clamped to min

        config.setScanRadius(-5);
        assertEquals(16, config.getScanRadius()); // Clamped to min
    }

    @Test
    public void testScanRadiusClampingMax() {
        config.setScanRadius(300);
        assertEquals(256, config.getScanRadius()); // Clamped to max

        config.setScanRadius(9999);
        assertEquals(256, config.getScanRadius()); // Clamped to max
    }

    @Test
    public void testScanInterval() {
        config.setScanInterval(30000);
        assertEquals(30000, config.getScanInterval());

        config.setScanInterval(1000);
        assertEquals(1000, config.getScanInterval());
    }

    @Test
    public void testScanIntervalClampingMin() {
        config.setScanInterval(500);
        assertEquals(1000, config.getScanInterval()); // Clamped to min

        config.setScanInterval(-100);
        assertEquals(1000, config.getScanInterval()); // Clamped to min
    }

    @Test
    public void testMovementThresholdValidRange() {
        config.setMovementThreshold(20.0);
        assertEquals(20.0, config.getMovementThreshold(), 0.01);

        config.setMovementThreshold(1.0);
        assertEquals(1.0, config.getMovementThreshold(), 0.01);

        config.setMovementThreshold(64.0);
        assertEquals(64.0, config.getMovementThreshold(), 0.01);
    }

    @Test
    public void testMovementThresholdClampingMin() {
        config.setMovementThreshold(0.5);
        assertEquals(1.0, config.getMovementThreshold(), 0.01); // Clamped to min

        config.setMovementThreshold(-10.0);
        assertEquals(1.0, config.getMovementThreshold(), 0.01); // Clamped to min
    }

    @Test
    public void testMovementThresholdClampingMax() {
        config.setMovementThreshold(100.0);
        assertEquals(64.0, config.getMovementThreshold(), 0.01); // Clamped to max

        config.setMovementThreshold(999.0);
        assertEquals(64.0, config.getMovementThreshold(), 0.01); // Clamped to max
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
    public void testLODMaxSegmentsValidRange() {
        config.setLodMaxSegments(48);
        assertEquals(48, config.getLodMaxSegments());

        config.setLodMaxSegments(8);
        assertEquals(8, config.getLodMaxSegments());

        config.setLodMaxSegments(64);
        assertEquals(64, config.getLodMaxSegments());
    }

    @Test
    public void testLodMaxSegmentsClampingMin() {
        config.setLodMaxSegments(5);
        assertEquals(8, config.getLodMaxSegments()); // Clamped to min

        config.setLodMaxSegments(-10);
        assertEquals(8, config.getLodMaxSegments()); // Clamped to min
    }

    @Test
    public void testLodMaxSegmentsClampingMax() {
        config.setLodMaxSegments(100);
        assertEquals(64, config.getLodMaxSegments()); // Clamped to max

        config.setLodMaxSegments(999);
        assertEquals(64, config.getLodMaxSegments()); // Clamped to max
    }

    @Test
    public void testLodMinSegmentsValidRange() {
        config.setLodMinSegments(8);
        assertEquals(8, config.getLodMinSegments());

        config.setLodMinSegments(4);
        assertEquals(4, config.getLodMinSegments());

        config.setLodMinSegments(32);
        assertEquals(32, config.getLodMinSegments());
    }

    @Test
    public void testLodMinSegmentsClampingMin() {
        config.setLodMinSegments(2);
        assertEquals(4, config.getLodMinSegments()); // Clamped to min

        config.setLodMinSegments(-5);
        assertEquals(4, config.getLodMinSegments()); // Clamped to min
    }

    @Test
    public void testLodMinSegmentsClampingMax() {
        config.setLodMinSegments(50);
        assertEquals(32, config.getLodMinSegments()); // Clamped to max

        config.setLodMinSegments(999);
        assertEquals(32, config.getLodMinSegments()); // Clamped to max
    }

    @Test
    public void testLodDistanceValidRange() {
        config.setLodDistance(48.0);
        assertEquals(48.0, config.getLodDistance(), 0.01);

        config.setLodDistance(16.0);
        assertEquals(16.0, config.getLodDistance(), 0.01);

        config.setLodDistance(128.0);
        assertEquals(128.0, config.getLodDistance(), 0.01);
    }

    @Test
    public void testLodDistanceClampingMin() {
        config.setLodDistance(10.0);
        assertEquals(16.0, config.getLodDistance(), 0.01); // Clamped to min

        config.setLodDistance(-5.0);
        assertEquals(16.0, config.getLodDistance(), 0.01); // Clamped to min
    }

    @Test
    public void testLodDistanceClampingMax() {
        config.setLodDistance(200.0);
        assertEquals(128.0, config.getLodDistance(), 0.01); // Clamped to max

        config.setLodDistance(999.0);
        assertEquals(128.0, config.getLodDistance(), 0.01); // Clamped to max
    }

    @Test
    public void testSphereSegmentsValidRange() {
        config.setSphereSegments(32);
        assertEquals(32, config.getSphereSegments());

        config.setSphereSegments(8);
        assertEquals(8, config.getSphereSegments());

        config.setSphereSegments(64);
        assertEquals(64, config.getSphereSegments());
    }

    @Test
    public void testSphereSegmentsClampingMin() {
        config.setSphereSegments(4);
        assertEquals(8, config.getSphereSegments()); // Clamped to min

        config.setSphereSegments(-10);
        assertEquals(8, config.getSphereSegments()); // Clamped to min
    }

    @Test
    public void testSphereSegmentsClampingMax() {
        config.setSphereSegments(100);
        assertEquals(64, config.getSphereSegments()); // Clamped to max

        config.setSphereSegments(999);
        assertEquals(64, config.getSphereSegments()); // Clamped to max
    }

    @Test
    public void testDefaultColorConfigs() {
        ModConfig.ColorConfig outsideColor = config.getOutsideRangeColor();
        assertNotNull(outsideColor);
        assertEquals(128, outsideColor.getRed());
        assertEquals(255, outsideColor.getGreen());
        assertEquals(0, outsideColor.getBlue());
        assertEquals(51, outsideColor.getAlpha());

        ModConfig.ColorConfig insideColor = config.getInsideRangeColor();
        assertNotNull(insideColor);
        assertEquals(255, insideColor.getRed());
        assertEquals(128, insideColor.getGreen());
        assertEquals(0, insideColor.getBlue());
        assertEquals(102, insideColor.getAlpha());
    }

    // ColorConfig Tests
    @Test
    public void testColorConfigValidValues() {
        ModConfig.ColorConfig color = new ModConfig.ColorConfig(100, 150, 200, 255);
        assertEquals(100, color.getRed());
        assertEquals(150, color.getGreen());
        assertEquals(200, color.getBlue());
        assertEquals(255, color.getAlpha());
    }

    @Test
    public void testColorConfigClampingMin() {
        ModConfig.ColorConfig color = new ModConfig.ColorConfig(-10, -20, -30, -40);
        assertEquals(0, color.getRed());
        assertEquals(0, color.getGreen());
        assertEquals(0, color.getBlue());
        assertEquals(0, color.getAlpha());
    }

    @Test
    public void testColorConfigClampingMax() {
        ModConfig.ColorConfig color = new ModConfig.ColorConfig(300, 400, 500, 600);
        assertEquals(255, color.getRed());
        assertEquals(255, color.getGreen());
        assertEquals(255, color.getBlue());
        assertEquals(255, color.getAlpha());
    }

    @Test
    public void testColorConfigFloatConversions() {
        ModConfig.ColorConfig color = new ModConfig.ColorConfig(0, 127, 255, 51);
        
        assertEquals(0.0f, color.getRedFloat(), 0.01f);
        assertEquals(127.0f / 255.0f, color.getGreenFloat(), 0.01f);
        assertEquals(1.0f, color.getBlueFloat(), 0.01f);
        assertEquals(51.0f / 255.0f, color.getAlphaFloat(), 0.01f);
    }

    @Test
    public void testColorConfigFloatConversionsBoundaries() {
        ModConfig.ColorConfig color1 = new ModConfig.ColorConfig(0, 0, 0, 0);
        assertEquals(0.0f, color1.getRedFloat(), 0.01f);
        assertEquals(0.0f, color1.getGreenFloat(), 0.01f);
        assertEquals(0.0f, color1.getBlueFloat(), 0.01f);
        assertEquals(0.0f, color1.getAlphaFloat(), 0.01f);

        ModConfig.ColorConfig color2 = new ModConfig.ColorConfig(255, 255, 255, 255);
        assertEquals(1.0f, color2.getRedFloat(), 0.01f);
        assertEquals(1.0f, color2.getGreenFloat(), 0.01f);
        assertEquals(1.0f, color2.getBlueFloat(), 0.01f);
        assertEquals(1.0f, color2.getAlphaFloat(), 0.01f);
    }

    @Test
    public void testColorConfigMixedValues() {
        ModConfig.ColorConfig color = new ModConfig.ColorConfig(64, 128, 192, 255);
        assertEquals(64, color.getRed());
        assertEquals(128, color.getGreen());
        assertEquals(192, color.getBlue());
        assertEquals(255, color.getAlpha());

        assertEquals(64.0f / 255.0f, color.getRedFloat(), 0.01f);
        assertEquals(128.0f / 255.0f, color.getGreenFloat(), 0.01f);
        assertEquals(192.0f / 255.0f, color.getBlueFloat(), 0.01f);
        assertEquals(1.0f, color.getAlphaFloat(), 0.01f);
    }
}
