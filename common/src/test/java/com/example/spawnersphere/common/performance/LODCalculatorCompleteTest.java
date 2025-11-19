package com.example.spawnersphere.common.performance;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Complete unit tests for LODCalculator with 100% coverage
 */
public class LODCalculatorCompleteTest {

    @Test
    public void testCalculateSegmentsCloseDistance() {
        // Close distances should use max segments
        int segments = LODCalculator.calculateSegments(5.0, 32, 16, 32.0);
        assertEquals(32, segments);

        segments = LODCalculator.calculateSegments(15.0, 32, 16, 32.0);
        assertEquals(32, segments);
    }

    @Test
    public void testCalculateSegmentsMediumDistance() {
        // Medium distances should use interpolated segments
        int segments = LODCalculator.calculateSegments(48.0, 32, 16, 32.0);
        assertTrue(segments >= 16 && segments <= 32);
        assertTrue(segments < 32); // Should be less than max
    }

    @Test
    public void testCalculateSegmentsFarDistance() {
        // Far distances should use min segments
        int segments = LODCalculator.calculateSegments(100.0, 32, 16, 32.0);
        assertEquals(16, segments);

        segments = LODCalculator.calculateSegments(200.0, 32, 16, 32.0);
        assertEquals(16, segments);
    }

    @Test
    public void testCalculateSegmentsExactLODDistance() {
        // At exactly LOD distance
        int segments = LODCalculator.calculateSegments(32.0, 32, 16, 32.0);
        assertTrue(segments >= 16 && segments <= 32);
    }

    @Test
    public void testCalculateSegmentsCustomSegmentRange() {
        // Test with different segment ranges
        int segments = LODCalculator.calculateSegments(10.0, 64, 8, 20.0);
        assertEquals(64, segments);

        segments = LODCalculator.calculateSegments(50.0, 64, 8, 20.0);
        assertEquals(8, segments);
    }

    @Test
    public void testCalculateSegmentsZeroDistance() {
        // At zero distance (player standing on spawner)
        int segments = LODCalculator.calculateSegments(0.0, 32, 16, 32.0);
        assertEquals(32, segments);
    }

    @Test
    public void testCalculateSegmentsDoubleLODDistance() {
        // At double the LOD distance
        int segments = LODCalculator.calculateSegments(64.0, 32, 16, 32.0);
        assertEquals(16, segments);
    }

    @Test
    public void testCalculateSegmentsLinearInterpolation() {
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
    public void testCalculateSegmentsMinMaxSame() {
        // When min and max are the same, should always return that value
        int segments = LODCalculator.calculateSegments(10.0, 24, 24, 32.0);
        assertEquals(24, segments);

        segments = LODCalculator.calculateSegments(50.0, 24, 24, 32.0);
        assertEquals(24, segments);
    }

    // Tests for calculateSegmentsSimple() method
    @Test
    public void testCalculateSegmentsSimpleCloseDistance() {
        int segments = LODCalculator.calculateSegmentsSimple(10.0);
        assertEquals(32, segments); // High detail for distance < 32
    }

    @Test
    public void testCalculateSegmentsSimpleMediumDistance() {
        int segments = LODCalculator.calculateSegmentsSimple(50.0);
        assertEquals(24, segments); // Medium detail for 32 <= distance < 64
    }

    @Test
    public void testCalculateSegmentsSimpleFarDistance() {
        int segments = LODCalculator.calculateSegmentsSimple(100.0);
        assertEquals(16, segments); // Low detail for distance >= 64
    }

    @Test
    public void testCalculateSegmentsSimpleBoundary32() {
        // Exactly at 32 blocks
        int segments = LODCalculator.calculateSegmentsSimple(32.0);
        assertEquals(24, segments); // Should be medium detail
    }

    @Test
    public void testCalculateSegmentsSimpleBoundary64() {
        // Exactly at 64 blocks
        int segments = LODCalculator.calculateSegmentsSimple(64.0);
        assertEquals(16, segments); // Should be low detail
    }

    @Test
    public void testCalculateSegmentsSimpleZeroDistance() {
        int segments = LODCalculator.calculateSegmentsSimple(0.0);
        assertEquals(32, segments); // High detail
    }

    @Test
    public void testCalculateSegmentsSimpleNearBoundary() {
        // Just below 32
        int segments1 = LODCalculator.calculateSegmentsSimple(31.9);
        assertEquals(32, segments1);

        // Just below 64
        int segments2 = LODCalculator.calculateSegmentsSimple(63.9);
        assertEquals(24, segments2);
    }
}
