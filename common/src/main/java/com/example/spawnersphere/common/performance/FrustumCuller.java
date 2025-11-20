package com.example.spawnersphere.common.performance;

import com.example.spawnersphere.common.platform.IPlatformHelper.Position;

/**
 * Simple frustum culling for sphere visibility
 * Checks if a sphere might be visible without heavy matrix math
 */
public class FrustumCuller {

    /**
     * Check if a sphere is potentially visible
     * Simple check using player view direction and FOV
     *
     * @param spherePos Center of the sphere
     * @param sphereRadius Radius of the sphere
     * @param playerPos Player's position
     * @param lookX Look direction X component
     * @param lookY Look direction Y component
     * @param lookZ Look direction Z component
     * @param fov Field of view in degrees (typically 70-110)
     * @return true if sphere might be visible
     */
    public static boolean isVisible(
        Position spherePos,
        float sphereRadius,
        Position playerPos,
        double lookX,
        double lookY,
        double lookZ,
        float fov
    ) {
        // Vector from player to sphere
        double dx = spherePos.x - playerPos.x;
        double dy = spherePos.y - playerPos.y;
        double dz = spherePos.z - playerPos.z;

        // Distance to sphere
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);

        // Always render if very close (avoid edge cases)
        if (distance < sphereRadius * 2) {
            return true;
        }

        // Normalize direction to sphere
        double ndx = dx / distance;
        double ndy = dy / distance;
        double ndz = dz / distance;

        // Check for zero look vector (invalid) - return true as safe fallback
        double lookLengthSq = lookX * lookX + lookY * lookY + lookZ * lookZ;
        if (lookLengthSq < 0.0001) {
            return true; // Can't determine direction, render to be safe
        }

        // Look direction is already normalized by LookVector constructor
        // No need to normalize again (saves sqrt calculation)

        // Dot product gives us angle
        double dot = ndx * lookX + ndy * lookY + ndz * lookZ;

        // Convert FOV to radians and add sphere size margin
        double fovRadians = Math.toRadians(fov / 2.0);
        double sphereAngle = Math.atan(sphereRadius / distance);
        double threshold = Math.cos(fovRadians + sphereAngle);

        // If dot product > threshold, sphere is in view
        return dot > threshold;
    }

    /**
     * Simplified version when look direction is not available
     * Uses a more conservative check
     */
    public static boolean isVisibleSimple(
        Position spherePos,
        float sphereRadius,
        Position playerPos,
        float maxDistance
    ) {
        double distance = playerPos.distanceTo(spherePos);
        // Conservative: render if within reasonable distance
        return distance <= maxDistance;
    }
}
