package com.example.spawnersphere.common.data;

import com.example.spawnersphere.common.platform.IPlatformHelper.Position;

/**
 * Shared data class for spawner position information
 * Used by both SpawnerSphereCore and SpatialIndex to avoid code duplication
 */
public class SpawnerData {
    public final Object blockPos;
    public final Position center;

    public SpawnerData(Object blockPos, Position center) {
        if (blockPos == null) {
            throw new IllegalArgumentException("blockPos cannot be null");
        }
        if (center == null) {
            throw new IllegalArgumentException("center cannot be null");
        }
        this.blockPos = blockPos;
        this.center = center;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SpawnerData that = (SpawnerData) o;
        return blockPos.equals(that.blockPos);
    }

    @Override
    public int hashCode() {
        return blockPos.hashCode();
    }

    @Override
    public String toString() {
        return "SpawnerData{" +
            "blockPos=" + blockPos +
            ", center=" + center +
            '}';
    }
}
