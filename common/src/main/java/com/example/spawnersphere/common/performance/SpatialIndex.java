package com.example.spawnersphere.common.performance;

import com.example.spawnersphere.common.platform.IPlatformHelper.Position;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Chunk-based spatial index for efficient spawner lookup
 * Groups spawners by chunk coordinates for faster queries
 */
public class SpatialIndex {

    private final Map<ChunkCoord, List<SpawnerEntry>> chunkMap;
    private static final int CHUNK_SIZE = 16;

    public SpatialIndex() {
        this.chunkMap = new HashMap<ChunkCoord, List<SpawnerEntry>>();
    }

    /**
     * Add a spawner to the spatial index
     */
    public void add(Object blockPos, Position center) {
        ChunkCoord chunkCoord = getChunkCoord(center);
        List<SpawnerEntry> spawners = chunkMap.get(chunkCoord);
        if (spawners == null) {
            spawners = new ArrayList<SpawnerEntry>();
            chunkMap.put(chunkCoord, spawners);
        }
        spawners.add(new SpawnerEntry(blockPos, center));
    }

    /**
     * Get all spawners within a given radius of a position
     * Uses chunk-based lookup for efficiency
     */
    public List<SpawnerEntry> getNearby(Position center, int radius) {
        List<SpawnerEntry> result = new ArrayList<SpawnerEntry>();

        int chunkRadius = (radius / CHUNK_SIZE) + 1;
        ChunkCoord centerChunk = getChunkCoord(center);

        for (int cx = -chunkRadius; cx <= chunkRadius; cx++) {
            for (int cz = -chunkRadius; cz <= chunkRadius; cz++) {
                ChunkCoord coord = new ChunkCoord(
                    centerChunk.x + cx,
                    centerChunk.z + cz
                );

                List<SpawnerEntry> spawners = chunkMap.get(coord);
                if (spawners != null) {
                    // Filter by actual distance
                    for (SpawnerEntry entry : spawners) {
                        double distance = center.distanceTo(entry.center);
                        if (distance <= radius) {
                            result.add(entry);
                        }
                    }
                }
            }
        }

        return result;
    }

    /**
     * Remove a spawner from the spatial index
     */
    public void remove(Object blockPos, Position center) {
        ChunkCoord chunkCoord = getChunkCoord(center);
        List<SpawnerEntry> spawners = chunkMap.get(chunkCoord);
        if (spawners != null) {
            spawners.removeIf(entry -> entry.blockPos.equals(blockPos));
            // Clean up empty chunk lists to prevent memory waste
            if (spawners.isEmpty()) {
                chunkMap.remove(chunkCoord);
            }
        }
    }

    /**
     * Clear all entries
     */
    public void clear() {
        chunkMap.clear();
    }

    /**
     * Get chunk coordinate from world position
     */
    private ChunkCoord getChunkCoord(Position pos) {
        return new ChunkCoord(
            (int) Math.floor(pos.x / CHUNK_SIZE),
            (int) Math.floor(pos.z / CHUNK_SIZE)
        );
    }

    /**
     * Chunk coordinate for spatial indexing
     */
    private static class ChunkCoord {
        final int x;
        final int z;

        ChunkCoord(int x, int z) {
            this.x = x;
            this.z = z;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ChunkCoord that = (ChunkCoord) o;
            return x == that.x && z == that.z;
        }

        @Override
        public int hashCode() {
            return 31 * x + z;
        }
    }

    /**
     * Spawner entry in the spatial index
     */
    public static class SpawnerEntry {
        public final Object blockPos;
        public final Position center;

        public SpawnerEntry(Object blockPos, Position center) {
            this.blockPos = blockPos;
            this.center = center;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            SpawnerEntry that = (SpawnerEntry) o;
            return blockPos.equals(that.blockPos);
        }

        @Override
        public int hashCode() {
            return blockPos.hashCode();
        }
    }
}
