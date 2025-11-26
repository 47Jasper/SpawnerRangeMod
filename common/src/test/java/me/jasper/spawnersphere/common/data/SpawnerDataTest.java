package me.jasper.spawnersphere.common.data;

import me.jasper.spawnersphere.common.platform.IPlatformHelper.Position;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive tests for SpawnerData
 */
class SpawnerDataTest {

    @Nested
    @DisplayName("Constructor")
    class ConstructorTests {

        @Test
        @DisplayName("should create instance with valid parameters")
        void shouldCreateWithValidParameters() {
            Object blockPos = "testBlockPos";
            Position center = new Position(10, 64, 20);

            SpawnerData data = new SpawnerData(blockPos, center);

            assertEquals(blockPos, data.blockPos);
            assertEquals(center, data.center);
        }

        @Test
        @DisplayName("should throw when blockPos is null")
        void shouldThrowWhenBlockPosIsNull() {
            Position center = new Position(0, 0, 0);

            assertThrows(IllegalArgumentException.class, () ->
                new SpawnerData(null, center));
        }

        @Test
        @DisplayName("should throw when center is null")
        void shouldThrowWhenCenterIsNull() {
            Object blockPos = "testBlockPos";

            assertThrows(IllegalArgumentException.class, () ->
                new SpawnerData(blockPos, null));
        }

        @Test
        @DisplayName("should throw when both parameters are null")
        void shouldThrowWhenBothAreNull() {
            assertThrows(IllegalArgumentException.class, () ->
                new SpawnerData(null, null));
        }
    }

    @Nested
    @DisplayName("equals")
    class EqualsTests {

        @Test
        @DisplayName("should be equal when blockPos is the same")
        void shouldBeEqualWhenBlockPosIsSame() {
            Object blockPos = "sameBlockPos";
            Position center1 = new Position(0, 0, 0);
            Position center2 = new Position(100, 100, 100);

            SpawnerData data1 = new SpawnerData(blockPos, center1);
            SpawnerData data2 = new SpawnerData(blockPos, center2);

            assertEquals(data1, data2);
        }

        @Test
        @DisplayName("should not be equal when blockPos differs")
        void shouldNotBeEqualWhenBlockPosDiffers() {
            Position center = new Position(0, 0, 0);

            SpawnerData data1 = new SpawnerData("blockPos1", center);
            SpawnerData data2 = new SpawnerData("blockPos2", center);

            assertNotEquals(data1, data2);
        }

        @Test
        @DisplayName("should be equal to itself")
        void shouldBeEqualToItself() {
            SpawnerData data = new SpawnerData("blockPos", new Position(0, 0, 0));

            assertEquals(data, data);
        }

        @Test
        @DisplayName("should not be equal to null")
        void shouldNotBeEqualToNull() {
            SpawnerData data = new SpawnerData("blockPos", new Position(0, 0, 0));

            assertNotEquals(null, data);
        }

        @Test
        @DisplayName("should not be equal to different type")
        void shouldNotBeEqualToDifferentType() {
            SpawnerData data = new SpawnerData("blockPos", new Position(0, 0, 0));

            assertNotEquals("string", data);
        }
    }

    @Nested
    @DisplayName("hashCode")
    class HashCodeTests {

        @Test
        @DisplayName("should have same hashCode when blockPos is equal")
        void shouldHaveSameHashCodeWhenEqual() {
            Object blockPos = "sameBlockPos";

            SpawnerData data1 = new SpawnerData(blockPos, new Position(0, 0, 0));
            SpawnerData data2 = new SpawnerData(blockPos, new Position(100, 100, 100));

            assertEquals(data1.hashCode(), data2.hashCode());
        }

        @Test
        @DisplayName("should be consistent")
        void shouldBeConsistent() {
            SpawnerData data = new SpawnerData("blockPos", new Position(0, 0, 0));

            int hash1 = data.hashCode();
            int hash2 = data.hashCode();

            assertEquals(hash1, hash2);
        }
    }

    @Nested
    @DisplayName("toString")
    class ToStringTests {

        @Test
        @DisplayName("should contain blockPos and center info")
        void shouldContainBlockPosAndCenter() {
            SpawnerData data = new SpawnerData("testBlockPos", new Position(10, 20, 30));

            String str = data.toString();

            assertTrue(str.contains("blockPos"));
            assertTrue(str.contains("center"));
            assertTrue(str.contains("testBlockPos"));
        }

        @Test
        @DisplayName("should not return null")
        void shouldNotReturnNull() {
            SpawnerData data = new SpawnerData("blockPos", new Position(0, 0, 0));

            assertNotNull(data.toString());
        }
    }
}
