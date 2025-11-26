package me.jasper.spawnersphere.common.platform;

import me.jasper.spawnersphere.common.platform.IPlatformHelper.LookVector;
import me.jasper.spawnersphere.common.platform.IPlatformHelper.Position;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive tests for IPlatformHelper inner classes
 */
class IPlatformHelperTest {

    @Nested
    @DisplayName("Position")
    class PositionTests {

        @Test
        @DisplayName("should store coordinates correctly")
        void shouldStoreCoordinatesCorrectly() {
            Position pos = new Position(10.5, 64.0, -20.25);

            assertEquals(10.5, pos.x, 0.001);
            assertEquals(64.0, pos.y, 0.001);
            assertEquals(-20.25, pos.z, 0.001);
        }

        @Test
        @DisplayName("should handle zero coordinates")
        void shouldHandleZeroCoordinates() {
            Position pos = new Position(0, 0, 0);

            assertEquals(0, pos.x, 0.001);
            assertEquals(0, pos.y, 0.001);
            assertEquals(0, pos.z, 0.001);
        }

        @Test
        @DisplayName("should handle negative coordinates")
        void shouldHandleNegativeCoordinates() {
            Position pos = new Position(-100.5, -64.0, -200.75);

            assertEquals(-100.5, pos.x, 0.001);
            assertEquals(-64.0, pos.y, 0.001);
            assertEquals(-200.75, pos.z, 0.001);
        }

        @Test
        @DisplayName("should handle very large coordinates")
        void shouldHandleVeryLargeCoordinates() {
            Position pos = new Position(1000000.0, 320.0, -1000000.0);

            assertEquals(1000000.0, pos.x, 0.001);
            assertEquals(320.0, pos.y, 0.001);
            assertEquals(-1000000.0, pos.z, 0.001);
        }

        @Nested
        @DisplayName("distanceTo")
        class DistanceToTests {

            @Test
            @DisplayName("should return zero for same position")
            void shouldReturnZeroForSamePosition() {
                Position pos = new Position(10, 20, 30);

                assertEquals(0.0, pos.distanceTo(pos), 0.001);
            }

            @Test
            @DisplayName("should calculate correct distance along X axis")
            void shouldCalculateDistanceAlongX() {
                Position pos1 = new Position(0, 0, 0);
                Position pos2 = new Position(10, 0, 0);

                assertEquals(10.0, pos1.distanceTo(pos2), 0.001);
            }

            @Test
            @DisplayName("should calculate correct distance along Y axis")
            void shouldCalculateDistanceAlongY() {
                Position pos1 = new Position(0, 0, 0);
                Position pos2 = new Position(0, 10, 0);

                assertEquals(10.0, pos1.distanceTo(pos2), 0.001);
            }

            @Test
            @DisplayName("should calculate correct distance along Z axis")
            void shouldCalculateDistanceAlongZ() {
                Position pos1 = new Position(0, 0, 0);
                Position pos2 = new Position(0, 0, 10);

                assertEquals(10.0, pos1.distanceTo(pos2), 0.001);
            }

            @Test
            @DisplayName("should calculate correct 3D diagonal distance")
            void shouldCalculate3DDiagonalDistance() {
                Position pos1 = new Position(0, 0, 0);
                Position pos2 = new Position(3, 4, 0);

                assertEquals(5.0, pos1.distanceTo(pos2), 0.001);
            }

            @Test
            @DisplayName("should calculate correct distance with negative coordinates")
            void shouldCalculateDistanceWithNegativeCoordinates() {
                Position pos1 = new Position(-5, -5, -5);
                Position pos2 = new Position(5, 5, 5);

                // sqrt(10^2 + 10^2 + 10^2) = sqrt(300) ≈ 17.32
                assertEquals(Math.sqrt(300), pos1.distanceTo(pos2), 0.001);
            }

            @Test
            @DisplayName("should be commutative")
            void shouldBeCommutative() {
                Position pos1 = new Position(10, 20, 30);
                Position pos2 = new Position(40, 50, 60);

                assertEquals(pos1.distanceTo(pos2), pos2.distanceTo(pos1), 0.001);
            }

            @ParameterizedTest
            @CsvSource({
                "0, 0, 0, 10, 0, 0, 10.0",
                "0, 0, 0, 0, 10, 0, 10.0",
                "0, 0, 0, 0, 0, 10, 10.0",
                "0, 0, 0, 3, 4, 0, 5.0",
                "1, 1, 1, 4, 5, 1, 5.0"
            })
            @DisplayName("should calculate correct distances")
            void shouldCalculateCorrectDistances(
                double x1, double y1, double z1,
                double x2, double y2, double z2,
                double expected
            ) {
                Position pos1 = new Position(x1, y1, z1);
                Position pos2 = new Position(x2, y2, z2);

                assertEquals(expected, pos1.distanceTo(pos2), 0.001);
            }
        }
    }

    @Nested
    @DisplayName("LookVector")
    class LookVectorTests {

        @Test
        @DisplayName("should normalize on construction")
        void shouldNormalizeOnConstruction() {
            LookVector vec = new LookVector(3, 4, 0);

            // Length should be 1
            double length = Math.sqrt(vec.x * vec.x + vec.y * vec.y + vec.z * vec.z);
            assertEquals(1.0, length, 0.001);

            // Components should be normalized
            assertEquals(0.6, vec.x, 0.001);
            assertEquals(0.8, vec.y, 0.001);
            assertEquals(0.0, vec.z, 0.001);
        }

        @Test
        @DisplayName("should handle already normalized vector")
        void shouldHandleAlreadyNormalizedVector() {
            LookVector vec = new LookVector(1, 0, 0);

            assertEquals(1.0, vec.x, 0.001);
            assertEquals(0.0, vec.y, 0.001);
            assertEquals(0.0, vec.z, 0.001);
        }

        @Test
        @DisplayName("should handle zero vector with default forward")
        void shouldHandleZeroVectorWithDefaultForward() {
            LookVector vec = new LookVector(0, 0, 0);

            assertEquals(0.0, vec.x, 0.001);
            assertEquals(0.0, vec.y, 0.001);
            assertEquals(1.0, vec.z, 0.001);
        }

        @Test
        @DisplayName("should handle very small vector as zero")
        void shouldHandleVerySmallVectorAsZero() {
            // Extremely small vector should still normalize if length > 0
            LookVector vec = new LookVector(0.001, 0, 0);

            assertEquals(1.0, vec.x, 0.001);
            assertEquals(0.0, vec.y, 0.001);
            assertEquals(0.0, vec.z, 0.001);
        }

        @Test
        @DisplayName("should normalize negative components")
        void shouldNormalizeNegativeComponents() {
            LookVector vec = new LookVector(-3, -4, 0);

            double length = Math.sqrt(vec.x * vec.x + vec.y * vec.y + vec.z * vec.z);
            assertEquals(1.0, length, 0.001);

            assertEquals(-0.6, vec.x, 0.001);
            assertEquals(-0.8, vec.y, 0.001);
        }

        @Test
        @DisplayName("should normalize 3D vector correctly")
        void shouldNormalize3DVectorCorrectly() {
            LookVector vec = new LookVector(1, 1, 1);

            double length = Math.sqrt(vec.x * vec.x + vec.y * vec.y + vec.z * vec.z);
            assertEquals(1.0, length, 0.001);

            double expected = 1.0 / Math.sqrt(3);
            assertEquals(expected, vec.x, 0.001);
            assertEquals(expected, vec.y, 0.001);
            assertEquals(expected, vec.z, 0.001);
        }

        @Test
        @DisplayName("should handle large magnitude vectors")
        void shouldHandleLargeMagnitudeVectors() {
            LookVector vec = new LookVector(1000, 0, 0);

            assertEquals(1.0, vec.x, 0.001);
            assertEquals(0.0, vec.y, 0.001);
            assertEquals(0.0, vec.z, 0.001);
        }

        @ParameterizedTest
        @CsvSource({
            "1, 0, 0, 1, 0, 0",
            "0, 1, 0, 0, 1, 0",
            "0, 0, 1, 0, 0, 1",
            "-1, 0, 0, -1, 0, 0",
            "0, -1, 0, 0, -1, 0",
            "0, 0, -1, 0, 0, -1"
        })
        @DisplayName("should preserve direction for unit vectors")
        void shouldPreserveDirectionForUnitVectors(
            double inX, double inY, double inZ,
            double outX, double outY, double outZ
        ) {
            LookVector vec = new LookVector(inX, inY, inZ);

            assertEquals(outX, vec.x, 0.001);
            assertEquals(outY, vec.y, 0.001);
            assertEquals(outZ, vec.z, 0.001);
        }
    }

    @Nested
    @DisplayName("Platform enum")
    class PlatformEnumTests {

        @Test
        @DisplayName("should have FABRIC platform")
        void shouldHaveFabricPlatform() {
            assertNotNull(IPlatformHelper.Platform.FABRIC);
        }

        @Test
        @DisplayName("should have FORGE platform")
        void shouldHaveForgePlatform() {
            assertNotNull(IPlatformHelper.Platform.FORGE);
        }

        @Test
        @DisplayName("should have NEOFORGE platform")
        void shouldHaveNeoforgePlatform() {
            assertNotNull(IPlatformHelper.Platform.NEOFORGE);
        }

        @Test
        @DisplayName("should have exactly 3 platforms")
        void shouldHaveExactlyThreePlatforms() {
            assertEquals(3, IPlatformHelper.Platform.values().length);
        }
    }
}
