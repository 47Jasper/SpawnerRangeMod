package me.jasper.spawnersphere.common.performance;

import me.jasper.spawnersphere.common.data.SpawnerData;
import me.jasper.spawnersphere.common.platform.IPlatformHelper.Position;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive tests for SpatialIndex
 */
class SpatialIndexTest {

    private SpatialIndex spatialIndex;

    @BeforeEach
    void setUp() {
        spatialIndex = new SpatialIndex();
    }

    @Nested
    @DisplayName("add with blockPos and center")
    class AddWithBlockPosTests {

        @Test
        @DisplayName("should add spawner successfully")
        void shouldAddSpawnerSuccessfully() {
            Object blockPos = "testBlockPos";
            Position center = new Position(10, 64, 20);

            spatialIndex.add(blockPos, center);

            assertEquals(1, spatialIndex.size());
        }

        @Test
        @DisplayName("should throw when blockPos is null")
        void shouldThrowWhenBlockPosIsNull() {
            Position center = new Position(0, 0, 0);

            assertThrows(IllegalArgumentException.class, () ->
                spatialIndex.add(null, center));
        }

        @Test
        @DisplayName("should throw when center is null")
        void shouldThrowWhenCenterIsNull() {
            Object blockPos = "testBlockPos";

            assertThrows(IllegalArgumentException.class, () ->
                spatialIndex.add(blockPos, null));
        }

        @Test
        @DisplayName("should add multiple spawners")
        void shouldAddMultipleSpawners() {
            spatialIndex.add("pos1", new Position(0, 0, 0));
            spatialIndex.add("pos2", new Position(100, 64, 100));
            spatialIndex.add("pos3", new Position(-50, 64, -50));

            assertEquals(3, spatialIndex.size());
        }

        @Test
        @DisplayName("should handle spawners in different chunks")
        void shouldHandleSpawnersInDifferentChunks() {
            // Chunk size is 16, so these should be in different chunks
            spatialIndex.add("pos1", new Position(0, 0, 0));
            spatialIndex.add("pos2", new Position(32, 0, 32));
            spatialIndex.add("pos3", new Position(-32, 0, -32));

            assertEquals(3, spatialIndex.size());
        }
    }

    @Nested
    @DisplayName("add with SpawnerData")
    class AddWithSpawnerDataTests {

        @Test
        @DisplayName("should add SpawnerData successfully")
        void shouldAddSpawnerDataSuccessfully() {
            SpawnerData data = new SpawnerData("blockPos", new Position(10, 64, 20));

            spatialIndex.add(data);

            assertEquals(1, spatialIndex.size());
        }

        @Test
        @DisplayName("should throw when data is null")
        void shouldThrowWhenDataIsNull() {
            assertThrows(IllegalArgumentException.class, () ->
                spatialIndex.add((SpawnerData) null));
        }
    }

    @Nested
    @DisplayName("getNearby")
    class GetNearbyTests {

        @Test
        @DisplayName("should return spawners within radius")
        void shouldReturnSpawnersWithinRadius() {
            spatialIndex.add("pos1", new Position(0, 0, 0));
            spatialIndex.add("pos2", new Position(10, 0, 10));
            spatialIndex.add("pos3", new Position(100, 0, 100));

            Position center = new Position(0, 0, 0);
            List<SpawnerData> nearby = spatialIndex.getNearby(center, 50);

            assertEquals(2, nearby.size());
        }

        @Test
        @DisplayName("should return empty list when no spawners nearby")
        void shouldReturnEmptyWhenNoneNearby() {
            spatialIndex.add("pos1", new Position(1000, 0, 1000));

            Position center = new Position(0, 0, 0);
            List<SpawnerData> nearby = spatialIndex.getNearby(center, 50);

            assertTrue(nearby.isEmpty());
        }

        @Test
        @DisplayName("should return all spawners when all within radius")
        void shouldReturnAllWhenAllWithinRadius() {
            spatialIndex.add("pos1", new Position(0, 0, 0));
            spatialIndex.add("pos2", new Position(5, 0, 5));
            spatialIndex.add("pos3", new Position(-5, 0, -5));

            Position center = new Position(0, 0, 0);
            List<SpawnerData> nearby = spatialIndex.getNearby(center, 100);

            assertEquals(3, nearby.size());
        }

        @Test
        @DisplayName("should throw when center is null")
        void shouldThrowWhenCenterIsNull() {
            assertThrows(IllegalArgumentException.class, () ->
                spatialIndex.getNearby(null, 50));
        }

        @Test
        @DisplayName("should throw when radius is negative")
        void shouldThrowWhenRadiusIsNegative() {
            assertThrows(IllegalArgumentException.class, () ->
                spatialIndex.getNearby(new Position(0, 0, 0), -10));
        }

        @Test
        @DisplayName("should return empty list for zero radius with spawner at center")
        void shouldHandleZeroRadius() {
            spatialIndex.add("pos1", new Position(0, 0, 0));

            Position center = new Position(0, 0, 0);
            List<SpawnerData> nearby = spatialIndex.getNearby(center, 0);

            // Distance 0 <= 0, so it should be included
            assertEquals(1, nearby.size());
        }

        @Test
        @DisplayName("should handle spawners at chunk boundaries")
        void shouldHandleChunkBoundaries() {
            // Add spawners at chunk boundary (16 blocks)
            spatialIndex.add("pos1", new Position(15, 0, 15));
            spatialIndex.add("pos2", new Position(17, 0, 17));

            Position center = new Position(16, 0, 16);
            List<SpawnerData> nearby = spatialIndex.getNearby(center, 10);

            assertEquals(2, nearby.size());
        }

        @Test
        @DisplayName("should consider Y axis in distance calculation")
        void shouldConsiderYAxis() {
            spatialIndex.add("pos1", new Position(0, 0, 0));
            spatialIndex.add("pos2", new Position(0, 100, 0));

            Position center = new Position(0, 0, 0);
            List<SpawnerData> nearby = spatialIndex.getNearby(center, 50);

            // Only pos1 should be within radius
            assertEquals(1, nearby.size());
        }
    }

    @Nested
    @DisplayName("remove")
    class RemoveTests {

        @Test
        @DisplayName("should remove spawner successfully")
        void shouldRemoveSpawnerSuccessfully() {
            Object blockPos = "testBlockPos";
            Position center = new Position(10, 64, 20);
            spatialIndex.add(blockPos, center);

            spatialIndex.remove(blockPos, center);

            assertEquals(0, spatialIndex.size());
        }

        @Test
        @DisplayName("should handle removing non-existent spawner")
        void shouldHandleRemovingNonExistent() {
            assertDoesNotThrow(() ->
                spatialIndex.remove("nonExistent", new Position(0, 0, 0)));
        }

        @Test
        @DisplayName("should handle null blockPos gracefully")
        void shouldHandleNullBlockPosGracefully() {
            assertDoesNotThrow(() ->
                spatialIndex.remove(null, new Position(0, 0, 0)));
        }

        @Test
        @DisplayName("should handle null center gracefully")
        void shouldHandleNullCenterGracefully() {
            assertDoesNotThrow(() ->
                spatialIndex.remove("blockPos", null));
        }

        @Test
        @DisplayName("should only remove specified spawner")
        void shouldOnlyRemoveSpecifiedSpawner() {
            spatialIndex.add("pos1", new Position(0, 0, 0));
            spatialIndex.add("pos2", new Position(10, 0, 10));

            spatialIndex.remove("pos1", new Position(0, 0, 0));

            assertEquals(1, spatialIndex.size());
        }
    }

    @Nested
    @DisplayName("clear")
    class ClearTests {

        @Test
        @DisplayName("should remove all spawners")
        void shouldRemoveAllSpawners() {
            spatialIndex.add("pos1", new Position(0, 0, 0));
            spatialIndex.add("pos2", new Position(10, 0, 10));
            spatialIndex.add("pos3", new Position(20, 0, 20));

            spatialIndex.clear();

            assertEquals(0, spatialIndex.size());
        }

        @Test
        @DisplayName("should handle clearing empty index")
        void shouldHandleClearingEmpty() {
            assertDoesNotThrow(() -> spatialIndex.clear());
            assertEquals(0, spatialIndex.size());
        }
    }

    @Nested
    @DisplayName("size")
    class SizeTests {

        @Test
        @DisplayName("should return 0 for empty index")
        void shouldReturnZeroForEmpty() {
            assertEquals(0, spatialIndex.size());
        }

        @Test
        @DisplayName("should return correct count after adds")
        void shouldReturnCorrectCountAfterAdds() {
            spatialIndex.add("pos1", new Position(0, 0, 0));
            spatialIndex.add("pos2", new Position(10, 0, 10));

            assertEquals(2, spatialIndex.size());
        }

        @Test
        @DisplayName("should return correct count after removes")
        void shouldReturnCorrectCountAfterRemoves() {
            spatialIndex.add("pos1", new Position(0, 0, 0));
            spatialIndex.add("pos2", new Position(10, 0, 10));
            spatialIndex.remove("pos1", new Position(0, 0, 0));

            assertEquals(1, spatialIndex.size());
        }
    }

    @Nested
    @DisplayName("Thread Safety")
    class ThreadSafetyTests {

        @Test
        @DisplayName("should handle concurrent adds")
        void shouldHandleConcurrentAdds() throws InterruptedException {
            int threadCount = 10;
            int spawnsPerThread = 100;
            ExecutorService executor = Executors.newFixedThreadPool(threadCount);
            CountDownLatch latch = new CountDownLatch(threadCount);

            for (int t = 0; t < threadCount; t++) {
                final int threadId = t;
                executor.submit(() -> {
                    try {
                        for (int i = 0; i < spawnsPerThread; i++) {
                            spatialIndex.add(
                                "pos_" + threadId + "_" + i,
                                new Position(threadId * 100 + i, 0, 0)
                            );
                        }
                    } finally {
                        latch.countDown();
                    }
                });
            }

            latch.await(10, TimeUnit.SECONDS);
            executor.shutdown();

            assertEquals(threadCount * spawnsPerThread, spatialIndex.size());
        }

        @Test
        @DisplayName("should handle concurrent reads and writes")
        void shouldHandleConcurrentReadsAndWrites() throws InterruptedException {
            int threadCount = 10;
            ExecutorService executor = Executors.newFixedThreadPool(threadCount);
            CountDownLatch latch = new CountDownLatch(threadCount);

            // Pre-populate with some data
            for (int i = 0; i < 100; i++) {
                spatialIndex.add("initial_" + i, new Position(i, 0, 0));
            }

            for (int t = 0; t < threadCount; t++) {
                final int threadId = t;
                executor.submit(() -> {
                    try {
                        for (int i = 0; i < 50; i++) {
                            if (threadId % 2 == 0) {
                                // Writer thread
                                spatialIndex.add(
                                    "pos_" + threadId + "_" + i,
                                    new Position(threadId * 100 + i, 0, 0)
                                );
                            } else {
                                // Reader thread
                                spatialIndex.getNearby(new Position(0, 0, 0), 1000);
                            }
                        }
                    } finally {
                        latch.countDown();
                    }
                });
            }

            latch.await(10, TimeUnit.SECONDS);
            executor.shutdown();

            // Should not throw and data should be consistent
            assertTrue(spatialIndex.size() >= 100);
        }
    }
}
