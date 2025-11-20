package com.example.spawnersphere.common.performance;

/**
 * Level of Detail calculator for sphere rendering
 * Reduces segment count for distant spheres to improve performance
 */
public class LODCalculator {

    /**
     * Calculate appropriate segment count based on distance
     *
     * @param distance Distance from player to sphere
     * @param maxSegments Maximum segments (for close spheres)
     * @param minSegments Minimum segments (for far spheres)
     * @param lodDistance Distance at which to start reducing detail
     * @return Segment count to use
     */
    public static int calculateSegments(
        double distance,
        int maxSegments,
        int minSegments,
        double lodDistance
    ) {
        // Defensive: validate distance is non-negative
        if (distance < 0) {
            distance = 0;
        }

        // Defensive: ensure maxSegments >= minSegments
        if (maxSegments < minSegments) {
            int temp = maxSegments;
            maxSegments = minSegments;
            minSegments = temp;
        }

        if (distance <= lodDistance) {
            // Full detail for close spheres
            return maxSegments;
        }

        // Linear interpolation based on distance
        double ratio = (distance - lodDistance) / lodDistance;
        ratio = Math.min(ratio, 1.0); // Cap at 1.0

        int segmentRange = maxSegments - minSegments;
        int reduction = (int) (segmentRange * ratio);

        return maxSegments - reduction;
    }

    /**
     * Simple 3-tier LOD system
     */
    public static int calculateSegmentsSimple(double distance) {
        if (distance < 32) {
            return 32; // High detail
        } else if (distance < 64) {
            return 24; // Medium detail
        } else {
            return 16; // Low detail
        }
    }
}
