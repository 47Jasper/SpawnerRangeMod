package com.example.spawnersphere.common.performance;

import com.example.spawnersphere.common.data.SpawnerData;
import com.example.spawnersphere.common.platform.IPlatformHelper.Position;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Chunk-based spatial index for efficient spawner lookup
 * Groups spawners by chunk coordinates for faster queries
 * Thread-safe implementation using read-write locks
 */
public class SpatialIndex {

    private final Map<ChunkCoord, List<SpawnerData>> chunkMap;
    private static final int CHUNK_SIZE = 16;

    // Use read-write lock for better concurrency (multiple readers, single writer)
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    public SpatialIndex() {
        this.chunkMap = new ConcurrentHashMap<ChunkCoord, List<SpawnerData>>();
    }

    /**
     * Add a spawner to the spatial index
     */
    public void add(Object blockPos, Position center) {
        if (blockPos == null || center == null) {
            throw new IllegalArgumentException("blockPos and center cannot be null");
        }
        lock.writeLock().lock();
        try {
            ChunkCoord chunkCoord = getChunkCoord(center);
            List<SpawnerData> spawners = chunkMap.get(chunkCoord);
            if (spawners == null) {
                spawners = new ArrayList<SpawnerData>();
                chunkMap.put(chunkCoord, spawners);
            }
            // Synchronize list access since ArrayList is not thread-safe
            synchronized (spawners) {
                spawners.add(new SpawnerData(blockPos, center));
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Add a spawner to the spatial index using existing SpawnerData
     */
    public void add(SpawnerData data) {
        if (data == null) {
            throw new IllegalArgumentException("data cannot be null");
        }
        lock.writeLock().lock();
        try {
            ChunkCoord chunkCoord = getChunkCoord(data.center);
            List<SpawnerData> spawners = chunkMap.get(chunkCoord);
            if (spawners == null) {
                spawners = new ArrayList<SpawnerData>();
                chunkMap.put(chunkCoord, spawners);
            }
            // Synchronize list access since ArrayList is not thread-safe
            synchronized (spawners) {
                spawners.add(data);
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Get all spawners within a given radius of a position
     * Uses chunk-based lookup for efficiency
     * Thread-safe with read lock for concurrent queries
     */
    public List<SpawnerData> getNearby(Position center, int radius) {
        if (center == null) {
            throw new IllegalArgumentException("center cannot be null");
        }
        if (radius < 0) {
            throw new IllegalArgumentException("radius must be non-negative");
        }

        lock.readLock().lock();
        try {
            List<SpawnerData> result = new ArrayList<SpawnerData>();

            int chunkRadius = (radius / CHUNK_SIZE) + 1;
            ChunkCoord centerChunk = getChunkCoord(center);

            for (int cx = -chunkRadius; cx <= chunkRadius; cx++) {
                for (int cz = -chunkRadius; cz <= chunkRadius; cz++) {
                    ChunkCoord coord = new ChunkCoord(
                        centerChunk.x + cx,
                        centerChunk.z + cz
                    );

                    List<SpawnerData> spawners = chunkMap.get(coord);
                    if (spawners != null) {
                        // Synchronize list access to create thread-safe snapshot
                        synchronized (spawners) {
                            // Filter by actual distance
                            for (SpawnerData entry : spawners) {
                                double distance = center.distanceTo(entry.center);
                                if (distance <= radius) {
                                    result.add(entry);
                                }
                            }
                        }
                    }
                }
            }

            return result;
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Remove a spawner from the spatial index
     */
    public void remove(Object blockPos, Position center) {
        if (blockPos == null || center == null) {
            return; // Gracefully handle null inputs
        }
        lock.writeLock().lock();
        try {
            ChunkCoord chunkCoord = getChunkCoord(center);
            List<SpawnerData> spawners = chunkMap.get(chunkCoord);
            if (spawners != null) {
                // Synchronize list access and cleanup in single block
                synchronized (spawners) {
                    spawners.removeIf(entry -> entry.blockPos.equals(blockPos));
                    // Clean up empty chunk lists to prevent memory waste
                    if (spawners.isEmpty()) {
                        chunkMap.remove(chunkCoord);
                    }
                }
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Clear all entries
     */
    public void clear() {
        lock.writeLock().lock();
        try {
            chunkMap.clear();
        } finally {
            lock.writeLock().unlock();
        }
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
     * Get the number of spawners in the index
     */
    public int size() {
        lock.readLock().lock();
        try {
            int count = 0;
            for (List<SpawnerData> spawners : chunkMap.values()) {
                synchronized (spawners) {
                    count += spawners.size();
                }
            }
            return count;
        } finally {
            lock.readLock().unlock();
        }
    }
}
