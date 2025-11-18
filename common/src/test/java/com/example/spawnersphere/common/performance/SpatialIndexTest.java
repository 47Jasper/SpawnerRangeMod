package com.example.spawnersphere.common.performance;

import com.example.spawnersphere.common.platform.IPlatformHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for SpatialIndex
 */
public class SpatialIndexTest {

    private SpatialIndex index;

    @BeforeEach
    public void setUp() {
        index = new SpatialIndex();
    }

    @Test
    public void testEmptyIndex() {
        IPlatformHelper.Position center = new IPlatformHelper.Position(0, 0, 0);
        List<SpatialIndex.SpawnerEntry> nearby = index.getNearby(center, 32);
        assertTrue(nearby.isEmpty());
    }

    @Test
    public void testAddAndRetrieve() {
        // Add a spawner at origin
        IPlatformHelper.Position pos1 = new IPlatformHelper.Position(0, 64, 0);
        Object spawner1 = new Object();
        index.add(pos1, spawner1);

        // Add a spawner far away
        IPlatformHelper.Position pos2 = new IPlatformHelper.Position(1000, 64, 1000);
        Object spawner2 = new Object();
        index.add(pos2, spawner2);

        // Query from origin with radius 32
        IPlatformHelper.Position center = new IPlatformHelper.Position(0, 64, 0);
        List<SpatialIndex.SpawnerEntry> nearby = index.getNearby(center, 32);

        // Should find spawner1 but not spawner2
        assertEquals(1, nearby.size());
        assertEquals(spawner1, nearby.get(0).spawner);
    }

    @Test
    public void testMultipleSpawnersInRange() {
        // Add 4 spawners in a square around origin
        index.add(new IPlatformHelper.Position(10, 64, 10), new Object());
        index.add(new IPlatformHelper.Position(-10, 64, 10), new Object());
        index.add(new IPlatformHelper.Position(10, 64, -10), new Object());
        index.add(new IPlatformHelper.Position(-10, 64, -10), new Object());

        // Query from origin with radius 20
        IPlatformHelper.Position center = new IPlatformHelper.Position(0, 64, 0);
        List<SpatialIndex.SpawnerEntry> nearby = index.getNearby(center, 20);

        // Should find all 4 spawners
        assertEquals(4, nearby.size());
    }

    @Test
    public void testClear() {
        index.add(new IPlatformHelper.Position(0, 64, 0), new Object());
        index.add(new IPlatformHelper.Position(10, 64, 10), new Object());

        index.clear();

        IPlatformHelper.Position center = new IPlatformHelper.Position(0, 64, 0);
        List<SpatialIndex.SpawnerEntry> nearby = index.getNearby(center, 32);

        assertTrue(nearby.isEmpty());
    }

    @Test
    public void testChunkBoundaries() {
        // Add spawners exactly on chunk boundaries (chunks are 16x16)
        index.add(new IPlatformHelper.Position(0, 64, 0), "spawner1");
        index.add(new IPlatformHelper.Position(16, 64, 0), "spawner2");
        index.add(new IPlatformHelper.Position(0, 64, 16), "spawner3");
        index.add(new IPlatformHelper.Position(16, 64, 16), "spawner4");

        // Query from center with radius that should catch all
        IPlatformHelper.Position center = new IPlatformHelper.Position(8, 64, 8);
        List<SpatialIndex.SpawnerEntry> nearby = index.getNearby(center, 20);

        assertEquals(4, nearby.size());
    }

    @Test
    public void testDistanceCalculation() {
        IPlatformHelper.Position spawnerPos = new IPlatformHelper.Position(10, 64, 0);
        index.add(spawnerPos, "spawner");

        IPlatformHelper.Position center = new IPlatformHelper.Position(0, 64, 0);
        List<SpatialIndex.SpawnerEntry> nearby = index.getNearby(center, 15);

        assertEquals(1, nearby.size());
        assertEquals(10.0, nearby.get(0).distance, 0.1);
    }

    @Test
    public void testExactRadiusBoundary() {
        // Spawner exactly at radius distance
        IPlatformHelper.Position spawnerPos = new IPlatformHelper.Position(10, 64, 0);
        index.add(spawnerPos, "spawner");

        // Query with radius = distance
        IPlatformHelper.Position center = new IPlatformHelper.Position(0, 64, 0);
        List<SpatialIndex.SpawnerEntry> nearby = index.getNearby(center, 10);

        // Should include spawner at exactly radius distance
        assertEquals(1, nearby.size());
    }

    @Test
    public void testYCoordinateIgnored() {
        // Spawners at different Y levels but same X,Z
        index.add(new IPlatformHelper.Position(10, 0, 0), "spawner1");
        index.add(new IPlatformHelper.Position(10, 128, 0), "spawner2");
        index.add(new IPlatformHelper.Position(10, 256, 0), "spawner3");

        // Query from origin at Y=64
        IPlatformHelper.Position center = new IPlatformHelper.Position(0, 64, 0);
        List<SpatialIndex.SpawnerEntry> nearby = index.getNearby(center, 15);

        // Should find all 3 spawners (Y is not considered in distance)
        assertEquals(3, nearby.size());
    }
}
