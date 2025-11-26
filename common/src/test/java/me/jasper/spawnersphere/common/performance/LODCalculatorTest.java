package me.jasper.spawnersphere.common.performance;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive tests for LODCalculator
 */
class LODCalculatorTest {

    @Nested
    @DisplayName("calculateSegments")
    class CalculateSegmentsTests {

        @Test
        @DisplayName("should return max segments when distance equals lodDistance")
        void shouldReturnMaxSegmentsAtLodDistance() {
            int result = LODCalculator.calculateSegments(32.0, 32, 16, 32.0);
            assertEquals(32, result);
        }

        @Test
        @DisplayName("should return max segments when distance is less than lodDistance")
        void shouldReturnMaxSegmentsWhenClose() {
            int result = LODCalculator.calculateSegments(16.0, 32, 16, 32.0);
            assertEquals(32, result);
        }

        @Test
        @DisplayName("should return min segments at twice lodDistance")
        void shouldReturnMinSegmentsAtTwiceLodDistance() {
            int result = LODCalculator.calculateSegments(64.0, 32, 16, 32.0);
            assertEquals(16, result);
        }

        @Test
        @DisplayName("should interpolate linearly between max and min")
        void shouldInterpolateLinearly() {
            // At 1.5x lodDistance, should be 50% between max and min
            int result = LODCalculator.calculateSegments(48.0, 32, 16, 32.0);
            assertEquals(24, result);
        }

        @Test
        @DisplayName("should handle zero distance")
        void shouldHandleZeroDistance() {
            int result = LODCalculator.calculateSegments(0.0, 32, 16, 32.0);
            assertEquals(32, result);
        }

        @Test
        @DisplayName("should handle negative distance by treating as zero")
        void shouldHandleNegativeDistance() {
            int result = LODCalculator.calculateSegments(-10.0, 32, 16, 32.0);
            assertEquals(32, result);
        }

        @Test
        @DisplayName("should swap max and min when max is less than min")
        void shouldSwapWhenMaxLessThanMin() {
            // When max < min, they should be swapped
            int result = LODCalculator.calculateSegments(0.0, 16, 32, 32.0);
            // After swap: max=32, min=16, result should be 32
            assertEquals(32, result);
        }

        @Test
        @DisplayName("should handle equal max and min segments")
        void shouldHandleEqualMaxAndMin() {
            int result = LODCalculator.calculateSegments(100.0, 24, 24, 32.0);
            assertEquals(24, result);
        }

        @Test
        @DisplayName("should cap reduction at min segments for very far distances")
        void shouldCapAtMinForFarDistances() {
            int result = LODCalculator.calculateSegments(1000.0, 32, 16, 32.0);
            assertEquals(16, result);
        }

        @ParameterizedTest
        @CsvSource({
            "0.0, 32",
            "16.0, 32",
            "32.0, 32",
            "40.0, 28",
            "48.0, 24",
            "56.0, 20",
            "64.0, 16",
            "100.0, 16"
        })
        @DisplayName("should produce expected segments at various distances")
        void shouldProduceExpectedSegments(double distance, int expectedSegments) {
            int result = LODCalculator.calculateSegments(distance, 32, 16, 32.0);
            assertEquals(expectedSegments, result);
        }

        @Test
        @DisplayName("should handle very small lodDistance")
        void shouldHandleSmallLodDistance() {
            int result = LODCalculator.calculateSegments(1.0, 32, 16, 1.0);
            assertEquals(32, result);
        }

        @Test
        @DisplayName("should handle very large lodDistance")
        void shouldHandleLargeLodDistance() {
            int result = LODCalculator.calculateSegments(100.0, 32, 16, 1000.0);
            assertEquals(32, result);
        }
    }

    @Nested
    @DisplayName("calculateSegmentsSimple")
    class CalculateSegmentsSimpleTests {

        @Test
        @DisplayName("should return high detail for close distances")
        void shouldReturnHighDetailForClose() {
            assertEquals(32, LODCalculator.calculateSegmentsSimple(0.0));
            assertEquals(32, LODCalculator.calculateSegmentsSimple(16.0));
            assertEquals(32, LODCalculator.calculateSegmentsSimple(31.9));
        }

        @Test
        @DisplayName("should return medium detail for medium distances")
        void shouldReturnMediumDetailForMedium() {
            assertEquals(24, LODCalculator.calculateSegmentsSimple(32.0));
            assertEquals(24, LODCalculator.calculateSegmentsSimple(48.0));
            assertEquals(24, LODCalculator.calculateSegmentsSimple(63.9));
        }

        @Test
        @DisplayName("should return low detail for far distances")
        void shouldReturnLowDetailForFar() {
            assertEquals(16, LODCalculator.calculateSegmentsSimple(64.0));
            assertEquals(16, LODCalculator.calculateSegmentsSimple(100.0));
            assertEquals(16, LODCalculator.calculateSegmentsSimple(1000.0));
        }

        @ParameterizedTest
        @ValueSource(doubles = {0.0, 10.0, 20.0, 31.0})
        @DisplayName("should return 32 for distances under 32")
        void shouldReturn32ForDistancesUnder32(double distance) {
            assertEquals(32, LODCalculator.calculateSegmentsSimple(distance));
        }

        @ParameterizedTest
        @ValueSource(doubles = {32.0, 40.0, 50.0, 63.0})
        @DisplayName("should return 24 for distances 32-63")
        void shouldReturn24ForDistances32to63(double distance) {
            assertEquals(24, LODCalculator.calculateSegmentsSimple(distance));
        }

        @ParameterizedTest
        @ValueSource(doubles = {64.0, 80.0, 100.0, 500.0})
        @DisplayName("should return 16 for distances 64+")
        void shouldReturn16ForDistances64Plus(double distance) {
            assertEquals(16, LODCalculator.calculateSegmentsSimple(distance));
        }
    }
}
