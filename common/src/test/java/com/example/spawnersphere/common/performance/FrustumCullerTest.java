package com.example.spawnersphere.common.performance;

import com.example.spawnersphere.common.platform.IPlatformHelper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for FrustumCuller
 */
public class FrustumCullerTest {

    @Test
    public void testSphereDirectlyAhead() {
        // Sphere directly in front of player
        IPlatformHelper.Position sphereCenter = new IPlatformHelper.Position(0, 64, 10);
        double sphereRadius = 5.0;
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
    public void testSphereBehind() {
        // Sphere behind player
        IPlatformHelper.Position sphereCenter = new IPlatformHelper.Position(0, 64, -10);
        double sphereRadius = 5.0;
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
    public void testSphereToSide() {
        // Sphere to the side (beyond FOV)
        IPlatformHelper.Position sphereCenter = new IPlatformHelper.Position(50, 64, 0);
        double sphereRadius = 5.0;
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
    public void testSphereAtEdgeOfFOV() {
        // Sphere at approximately 45 degrees (within 90 degree FOV)
        IPlatformHelper.Position sphereCenter = new IPlatformHelper.Position(10, 64, 10);
        double sphereRadius = 5.0;
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
    public void testLargeSphereRadius() {
        // Large sphere that extends into view even if center is not
        IPlatformHelper.Position sphereCenter = new IPlatformHelper.Position(20, 64, 0);
        double sphereRadius = 25.0; // Large radius
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
    public void testNarrowFOV() {
        // Test with narrow FOV (30 degrees)
        IPlatformHelper.Position sphereCenter = new IPlatformHelper.Position(10, 64, 10);
        double sphereRadius = 2.0;
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
    public void testWideFOV() {
        // Test with wide FOV (120 degrees)
        IPlatformHelper.Position sphereCenter = new IPlatformHelper.Position(15, 64, 10);
        double sphereRadius = 2.0;
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
    public void testDifferentLookDirections() {
        IPlatformHelper.Position sphereCenter = new IPlatformHelper.Position(10, 64, 0);
        double sphereRadius = 5.0;
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

    @Test
    public void testYAxisIgnored() {
        // Spheres at different Y levels but same X,Z position
        IPlatformHelper.Position camera = new IPlatformHelper.Position(0, 64, 0);

        boolean visible1 = FrustumCuller.isVisible(
            new IPlatformHelper.Position(0, 32, 10), // Low Y
            5.0, camera,
            0.0, 0.0, 1.0,
            90.0f
        );

        boolean visible2 = FrustumCuller.isVisible(
            new IPlatformHelper.Position(0, 128, 10), // High Y
            5.0, camera,
            0.0, 0.0, 1.0,
            90.0f
        );

        // Both should have same visibility (Y is ignored in 2D frustum culling)
        assertEquals(visible1, visible2);
        assertTrue(visible1);
    }
}
