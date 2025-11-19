package com.example.spawnersphere.common.performance;

import com.example.spawnersphere.common.platform.IPlatformHelper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Complete unit tests for FrustumCuller with 100% coverage
 */
public class FrustumCullerCompleteTest {

    @Test
    public void testIsVisibleSphereDirectlyAhead() {
        // Sphere directly in front of player
        IPlatformHelper.Position sphereCenter = new IPlatformHelper.Position(0, 64, 10);
        float sphereRadius = 5.0f;
        IPlatformHelper.Position cameraPos = new IPlatformHelper.Position(0, 64, 0);

        // Looking straight ahead (positive Z)
        boolean visible = FrustumCuller.isVisible(
            sphereCenter, sphereRadius, cameraPos,
            0.0, 0.0, 1.0, // Looking towards +Z
            90.0f
        );

        assertTrue(visible);
    }

    @Test
    public void testIsVisibleSphereBehind() {
        // Sphere behind player
        IPlatformHelper.Position sphereCenter = new IPlatformHelper.Position(0, 64, -10);
        float sphereRadius = 5.0f;
        IPlatformHelper.Position cameraPos = new IPlatformHelper.Position(0, 64, 0);

        // Looking straight ahead (positive Z)
        boolean visible = FrustumCuller.isVisible(
            sphereCenter, sphereRadius, cameraPos,
            0.0, 0.0, 1.0, // Looking towards +Z
            90.0f
        );

        assertFalse(visible);
    }

    @Test
    public void testIsVisibleSphereToSide() {
        // Sphere to the side (beyond FOV)
        IPlatformHelper.Position sphereCenter = new IPlatformHelper.Position(50, 64, 0);
        float sphereRadius = 5.0f;
        IPlatformHelper.Position cameraPos = new IPlatformHelper.Position(0, 64, 0);

        // Looking straight ahead (positive Z)
        boolean visible = FrustumCuller.isVisible(
            sphereCenter, sphereRadius, cameraPos,
            0.0, 0.0, 1.0, // Looking towards +Z
            90.0f
        );

        assertFalse(visible);
    }

    @Test
    public void testIsVisibleSphereAtEdgeOfFOV() {
        // Sphere at approximately 45 degrees (within 90 degree FOV)
        IPlatformHelper.Position sphereCenter = new IPlatformHelper.Position(10, 64, 10);
        float sphereRadius = 5.0f;
        IPlatformHelper.Position cameraPos = new IPlatformHelper.Position(0, 64, 0);

        // Looking straight ahead (positive Z)
        boolean visible = FrustumCuller.isVisible(
            sphereCenter, sphereRadius, cameraPos,
            0.0, 0.0, 1.0, // Looking towards +Z
            90.0f
        );

        assertTrue(visible);
    }

    @Test
    public void testIsVisibleLargeSphereRadius() {
        // Large sphere that extends into view even if center is not
        IPlatformHelper.Position sphereCenter = new IPlatformHelper.Position(20, 64, 0);
        float sphereRadius = 25.0f; // Large radius
        IPlatformHelper.Position cameraPos = new IPlatformHelper.Position(0, 64, 0);

        // Looking straight ahead (positive Z)
        boolean visible = FrustumCuller.isVisible(
            sphereCenter, sphereRadius, cameraPos,
            0.0, 0.0, 1.0, // Looking towards +Z
            90.0f
        );

        // Large sphere should be visible because it extends into FOV
        assertTrue(visible);
    }

    @Test
    public void testIsVisibleVeryCloseToPlayer() {
        // Very close to player (within 2*radius)
        IPlatformHelper.Position sphereCenter = new IPlatformHelper.Position(0, 64, 3);
        float sphereRadius = 5.0f;
        IPlatformHelper.Position cameraPos = new IPlatformHelper.Position(0, 64, 0);

        // Looking behind (negative Z)
        boolean visible = FrustumCuller.isVisible(
            sphereCenter, sphereRadius, cameraPos,
            0.0, 0.0, -1.0, // Looking away
            90.0f
        );

        // Should be visible anyway because it's very close
        assertTrue(visible);
    }

    @Test
    public void testIsVisibleZeroLookVector() {
        // Zero look vector (edge case)
        IPlatformHelper.Position sphereCenter = new IPlatformHelper.Position(10, 64, 0);
        float sphereRadius = 5.0f;
        IPlatformHelper.Position cameraPos = new IPlatformHelper.Position(0, 64, 0);

        // Zero look vector
        boolean visible = FrustumCuller.isVisible(
            sphereCenter, sphereRadius, cameraPos,
            0.0, 0.0, 0.0,
            90.0f
        );

        // Should return true when can't determine
        assertTrue(visible);
    }

    @Test
    public void testIsVisibleNarrowFOV() {
        // Test with narrow FOV (30 degrees)
        IPlatformHelper.Position sphereCenter = new IPlatformHelper.Position(10, 64, 10);
        float sphereRadius = 2.0f;
        IPlatformHelper.Position cameraPos = new IPlatformHelper.Position(0, 64, 0);

        // Looking straight ahead with narrow FOV
        boolean visible = FrustumCuller.isVisible(
            sphereCenter, sphereRadius, cameraPos,
            0.0, 0.0, 1.0, // Looking towards +Z
            30.0f // Narrow FOV
        );

        // Should be outside narrow FOV
        assertFalse(visible);
    }

    @Test
    public void testIsVisibleWideFOV() {
        // Test with wide FOV (120 degrees)
        IPlatformHelper.Position sphereCenter = new IPlatformHelper.Position(15, 64, 10);
        float sphereRadius = 2.0f;
        IPlatformHelper.Position cameraPos = new IPlatformHelper.Position(0, 64, 0);

        // Looking straight ahead with wide FOV
        boolean visible = FrustumCuller.isVisible(
            sphereCenter, sphereRadius, cameraPos,
            0.0, 0.0, 1.0, // Looking towards +Z
            120.0f // Wide FOV
        );

        assertTrue(visible);
    }

    @Test
    public void testIsVisibleDifferentLookDirections() {
        IPlatformHelper.Position sphereCenter = new IPlatformHelper.Position(10, 64, 0);
        float sphereRadius = 5.0f;
        IPlatformHelper.Position cameraPos = new IPlatformHelper.Position(0, 64, 0);

        // Looking towards +X
        boolean visibleX = FrustumCuller.isVisible(
            sphereCenter, sphereRadius, cameraPos,
            1.0, 0.0, 0.0,
            90.0f
        );
        assertTrue(visibleX);

        // Looking towards -X
        boolean visibleNegX = FrustumCuller.isVisible(
            sphereCenter, sphereRadius, cameraPos,
            -1.0, 0.0, 0.0,
            90.0f
        );
        assertFalse(visibleNegX);
    }

    // Tests for isVisibleSimple() method
    @Test
    public void testIsVisibleSimpleWithinDistance() {
        IPlatformHelper.Position sphereCenter = new IPlatformHelper.Position(10, 64, 0);
        float sphereRadius = 5.0f;
        IPlatformHelper.Position cameraPos = new IPlatformHelper.Position(0, 64, 0);

        boolean visible = FrustumCuller.isVisibleSimple(
            sphereCenter, sphereRadius, cameraPos,
            20.0f
        );

        assertTrue(visible); // Within maxDistance
    }

    @Test
    public void testIsVisibleSimpleBeyondDistance() {
        IPlatformHelper.Position sphereCenter = new IPlatformHelper.Position(100, 64, 0);
        float sphereRadius = 5.0f;
        IPlatformHelper.Position cameraPos = new IPlatformHelper.Position(0, 64, 0);

        boolean visible = FrustumCuller.isVisibleSimple(
            sphereCenter, sphereRadius, cameraPos,
            50.0f
        );

        assertFalse(visible); // Beyond maxDistance
    }

    @Test
    public void testIsVisibleSimpleExactDistance() {
        IPlatformHelper.Position sphereCenter = new IPlatformHelper.Position(10, 64, 0);
        float sphereRadius = 5.0f;
        IPlatformHelper.Position cameraPos = new IPlatformHelper.Position(0, 64, 0);

        boolean visible = FrustumCuller.isVisibleSimple(
            sphereCenter, sphereRadius, cameraPos,
            10.0f
        );

        assertTrue(visible); // Exactly at maxDistance
    }

    @Test
    public void testIsVisibleSimpleZeroDistance() {
        IPlatformHelper.Position sphereCenter = new IPlatformHelper.Position(0, 64, 0);
        float sphereRadius = 5.0f;
        IPlatformHelper.Position cameraPos = new IPlatformHelper.Position(0, 64, 0);

        boolean visible = FrustumCuller.isVisibleSimple(
            sphereCenter, sphereRadius, cameraPos,
            10.0f
        );

        assertTrue(visible); // At same position
    }

    @Test
    public void testIsVisibleSimpleDiagonal3D() {
        // Test 3D distance calculation
        IPlatformHelper.Position sphereCenter = new IPlatformHelper.Position(10, 100, 10);
        float sphereRadius = 5.0f;
        IPlatformHelper.Position cameraPos = new IPlatformHelper.Position(0, 64, 0);

        boolean visible = FrustumCuller.isVisibleSimple(
            sphereCenter, sphereRadius, cameraPos,
            50.0f
        );

        assertTrue(visible); // Should calculate 3D distance correctly
    }
}
