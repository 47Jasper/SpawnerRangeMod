package me.jasper.spawnersphere.common.performance;

import me.jasper.spawnersphere.common.platform.IPlatformHelper.Position;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive tests for FrustumCuller
 */
class FrustumCullerTest {

    @Nested
    @DisplayName("isVisible")
    class IsVisibleTests {

        @Test
        @DisplayName("should return true when sphere is directly ahead")
        void shouldReturnTrueWhenDirectlyAhead() {
            Position spherePos = new Position(0, 0, 50);
            Position playerPos = new Position(0, 0, 0);

            boolean result = FrustumCuller.isVisible(spherePos, 16.0f, playerPos, 0, 0, 1, 90.0f);

            assertTrue(result);
        }

        @Test
        @DisplayName("should return false when sphere is behind player")
        void shouldReturnFalseWhenBehind() {
            Position spherePos = new Position(0, 0, -50);
            Position playerPos = new Position(0, 0, 0);

            boolean result = FrustumCuller.isVisible(spherePos, 16.0f, playerPos, 0, 0, 1, 90.0f);

            assertFalse(result);
        }

        @Test
        @DisplayName("should return true when sphere is very close")
        void shouldReturnTrueWhenVeryClose() {
            Position spherePos = new Position(5, 0, 0);
            Position playerPos = new Position(0, 0, 0);

            // Very close spheres should always be visible
            boolean result = FrustumCuller.isVisible(spherePos, 16.0f, playerPos, 0, 0, 1, 90.0f);

            assertTrue(result);
        }

        @Test
        @DisplayName("should return true when look vector is zero")
        void shouldReturnTrueWithZeroLookVector() {
            Position spherePos = new Position(0, 0, 50);
            Position playerPos = new Position(0, 0, 0);

            // Zero look vector should default to visible
            boolean result = FrustumCuller.isVisible(spherePos, 16.0f, playerPos, 0, 0, 0, 90.0f);

            assertTrue(result);
        }

        @Test
        @DisplayName("should handle sphere at edge of FOV")
        void shouldHandleSphereAtEdgeOfFOV() {
            Position spherePos = new Position(50, 0, 50);
            Position playerPos = new Position(0, 0, 0);

            // 45-degree angle sphere with 90 degree FOV should be visible
            boolean result = FrustumCuller.isVisible(spherePos, 16.0f, playerPos, 0, 0, 1, 90.0f);

            assertTrue(result);
        }

        @Test
        @DisplayName("should return false when sphere is outside FOV")
        void shouldReturnFalseWhenOutsideFOV() {
            Position spherePos = new Position(100, 0, 10);
            Position playerPos = new Position(0, 0, 0);

            // Far to the side, outside narrow FOV
            boolean result = FrustumCuller.isVisible(spherePos, 5.0f, playerPos, 0, 0, 1, 30.0f);

            assertFalse(result);
        }

        @Test
        @DisplayName("should account for sphere radius in visibility")
        void shouldAccountForSphereRadius() {
            Position spherePos = new Position(50, 0, 20);
            Position playerPos = new Position(0, 0, 0);

            // Large sphere should be visible even if center is at edge
            boolean resultLarge = FrustumCuller.isVisible(spherePos, 30.0f, playerPos, 0, 0, 1, 60.0f);
            boolean resultSmall = FrustumCuller.isVisible(spherePos, 1.0f, playerPos, 0, 0, 1, 60.0f);

            // Large radius adds to the angle margin
            assertTrue(resultLarge);
        }

        @Test
        @DisplayName("should handle looking up")
        void shouldHandleLookingUp() {
            Position spherePos = new Position(0, 50, 0);
            Position playerPos = new Position(0, 0, 0);

            boolean result = FrustumCuller.isVisible(spherePos, 16.0f, playerPos, 0, 1, 0, 90.0f);

            assertTrue(result);
        }

        @Test
        @DisplayName("should handle looking down")
        void shouldHandleLookingDown() {
            Position spherePos = new Position(0, -50, 0);
            Position playerPos = new Position(0, 0, 0);

            boolean result = FrustumCuller.isVisible(spherePos, 16.0f, playerPos, 0, -1, 0, 90.0f);

            assertTrue(result);
        }

        @ParameterizedTest
        @ValueSource(floats = {30.0f, 60.0f, 90.0f, 110.0f})
        @DisplayName("should handle various FOV values")
        void shouldHandleVariousFOVValues(float fov) {
            Position spherePos = new Position(0, 0, 50);
            Position playerPos = new Position(0, 0, 0);

            boolean result = FrustumCuller.isVisible(spherePos, 16.0f, playerPos, 0, 0, 1, fov);

            assertTrue(result);
        }

        @Test
        @DisplayName("should handle negative coordinates")
        void shouldHandleNegativeCoordinates() {
            Position spherePos = new Position(-50, -20, -100);
            Position playerPos = new Position(0, 0, 0);

            boolean result = FrustumCuller.isVisible(spherePos, 16.0f, playerPos, 0, 0, -1, 90.0f);

            assertTrue(result);
        }
    }

    @Nested
    @DisplayName("isVisibleSimple")
    class IsVisibleSimpleTests {

        @Test
        @DisplayName("should return true when within max distance")
        void shouldReturnTrueWhenWithinMaxDistance() {
            Position spherePos = new Position(0, 0, 50);
            Position playerPos = new Position(0, 0, 0);

            boolean result = FrustumCuller.isVisibleSimple(spherePos, 16.0f, playerPos, 100.0f);

            assertTrue(result);
        }

        @Test
        @DisplayName("should return false when beyond max distance")
        void shouldReturnFalseWhenBeyondMaxDistance() {
            Position spherePos = new Position(0, 0, 150);
            Position playerPos = new Position(0, 0, 0);

            boolean result = FrustumCuller.isVisibleSimple(spherePos, 16.0f, playerPos, 100.0f);

            assertFalse(result);
        }

        @Test
        @DisplayName("should account for sphere radius in visibility check")
        void shouldAccountForSphereRadius() {
            Position spherePos = new Position(0, 0, 110);
            Position playerPos = new Position(0, 0, 0);

            // Without radius: 110 > 100, would be false
            // With radius 16: 110 <= 100 + 16 = 116, should be true
            boolean result = FrustumCuller.isVisibleSimple(spherePos, 16.0f, playerPos, 100.0f);

            assertTrue(result);
        }

        @Test
        @DisplayName("should return true at exact max distance")
        void shouldReturnTrueAtExactMaxDistance() {
            Position spherePos = new Position(0, 0, 100);
            Position playerPos = new Position(0, 0, 0);

            boolean result = FrustumCuller.isVisibleSimple(spherePos, 0.0f, playerPos, 100.0f);

            assertTrue(result);
        }

        @Test
        @DisplayName("should return true when sphere is at player position")
        void shouldReturnTrueWhenAtPlayerPosition() {
            Position spherePos = new Position(10, 20, 30);
            Position playerPos = new Position(10, 20, 30);

            boolean result = FrustumCuller.isVisibleSimple(spherePos, 16.0f, playerPos, 100.0f);

            assertTrue(result);
        }

        @Test
        @DisplayName("should handle zero max distance")
        void shouldHandleZeroMaxDistance() {
            Position spherePos = new Position(0, 0, 10);
            Position playerPos = new Position(0, 0, 0);

            // With sphere radius 16: 10 <= 0 + 16 = 16, should be true
            boolean result = FrustumCuller.isVisibleSimple(spherePos, 16.0f, playerPos, 0.0f);

            assertTrue(result);
        }

        @Test
        @DisplayName("should handle 3D diagonal distance")
        void shouldHandle3DDiagonalDistance() {
            Position spherePos = new Position(60, 60, 60);
            Position playerPos = new Position(0, 0, 0);

            // Distance = sqrt(60^2 + 60^2 + 60^2) = sqrt(10800) ≈ 103.9
            boolean result = FrustumCuller.isVisibleSimple(spherePos, 16.0f, playerPos, 100.0f);

            // 103.9 <= 100 + 16 = 116, should be true
            assertTrue(result);
        }
    }
}
