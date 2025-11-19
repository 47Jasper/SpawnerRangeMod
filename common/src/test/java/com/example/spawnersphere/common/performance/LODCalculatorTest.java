package com.example.spawnersphere.common.performance;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for LODCalculator
 */
public class LODCalculatorTest {

    @Test
    public void testCloseDistance() {
        // Close distances should use max segments
        int segments = LODCalculator.calculateSegments(5.0, 32, 16, 32.0);
        assertEquals(32, segments);

        segments = LODCalculator.calculateSegments(15.0, 32, 16, 32.0);
        assertEquals(32, segments);
    }

    @Test
    public void testMediumDistance() {
        // Medium distances should use interpolated segments
        int segments = LODCalculator.calculateSegments(48.0, 32, 16, 32.0);
        assertTrue(segments >= 16 && segments <= 32);
        assertTrue(segments < 32); // Should be less than max
    }

    @Test
    public void testFarDistance() {
        // Far distances should use min segments
        int segments = LODCalculator.calculateSegments(100.0, 32, 16, 32.0);
        assertEquals(16, segments);

        segments = LODCalculator.calculateSegments(200.0, 32, 16, 32.0);
        assertEquals(16, segments);
    }

    @Test
    public void testExactLODDistance() {
        // At exactly LOD distance
        int segments = LODCalculator.calculateSegments(32.0, 32, 16, 32.0);
        assertTrue(segments >= 16 && segments <= 32);
    }

    @Test
    public void testCustomSegmentRange() {
        // Test with different segment ranges
        int segments = LODCalculator.calculateSegments(10.0, 64, 8, 20.0);
        assertEquals(64, segments);

        segments = LODCalculator.calculateSegments(50.0, 64, 8, 20.0);
        assertEquals(8, segments);
    }

    @Test
    public void testZeroDistance() {
        // At zero distance (player standing on spawner)
        int segments = LODCalculator.calculateSegments(0.0, 32, 16, 32.0);
        assertEquals(32, segments);
    }

    @Test
    public void testDoubleLODDistance() {
        // At double the LOD distance
        int segments = LODCalculator.calculateSegments(64.0, 32, 16, 32.0);
        assertEquals(16, segments);
    }

    @Test
    public void testLinearInterpolation() {
        // Test that interpolation is smooth
        double lodDistance = 32.0;
        int maxSegments = 32;
        int minSegments = 16;

        int seg1 = LODCalculator.calculateSegments(32.0, maxSegments, minSegments, lodDistance);
        int seg2 = LODCalculator.calculateSegments(48.0, maxSegments, minSegments, lodDistance);
        int seg3 = LODCalculator.calculateSegments(64.0, maxSegments, minSegments, lodDistance);

        // Should be decreasing
        assertTrue(seg1 >= seg2);
        assertTrue(seg2 >= seg3);
    }

    @Test
    public void testMinMaxSame() {
        // When min and max are the same, should always return that value
        int segments = LODCalculator.calculateSegments(10.0, 24, 24, 32.0);
        assertEquals(24, segments);

        segments = LODCalculator.calculateSegments(50.0, 24, 24, 32.0);
        assertEquals(24, segments);
    }
}
