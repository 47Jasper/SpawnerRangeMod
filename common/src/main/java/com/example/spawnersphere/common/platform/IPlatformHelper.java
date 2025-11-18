package com.example.spawnersphere.common.platform;

import org.jetbrains.annotations.NotNull;

/**
 * Platform abstraction interface for Fabric/Forge compatibility
 */
public interface IPlatformHelper {

    /**
     * Get the current mod loader platform
     */
    @NotNull
    Platform getPlatform();

    /**
     * Check if a block at the given position is a spawner
     */
    boolean isSpawner(Object world, Object blockPos);

    /**
     * Get the player's current position
     * @return Vec3-like object with x, y, z coordinates
     */
    @NotNull
    Position getPlayerPosition(Object player);

    /**
     * Get a block position from coordinates
     */
    @NotNull
    Object createBlockPos(int x, int y, int z);

    /**
     * Get the center position of a block
     */
    @NotNull
    Position getBlockCenter(Object blockPos);

    /**
     * Calculate Euclidean distance between two positions
     */
    double calculateDistance(Position pos1, Position pos2);

    /**
     * Send a message to the player
     */
    void sendMessage(Object player, String message, boolean actionBar);

    /**
     * Get the player's look direction vector
     * @return LookVector with normalized x, y, z components
     */
    @NotNull
    LookVector getPlayerLookVector(Object player);

    enum Platform {
        FABRIC,
        FORGE,
        NEOFORGE
    }

    /**
     * Simple position holder
     */
    class Position {
        public final double x;
        public final double y;
        public final double z;

        public Position(double x, double y, double z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public double distanceTo(Position other) {
            double dx = this.x - other.x;
            double dy = this.y - other.y;
            double dz = this.z - other.z;
            return Math.sqrt(dx * dx + dy * dy + dz * dz);
        }
    }

    /**
     * Player look direction vector
     */
    class LookVector {
        public final double x;
        public final double y;
        public final double z;

        public LookVector(double x, double y, double z) {
            // Normalize on construction
            double length = Math.sqrt(x * x + y * y + z * z);
            if (length > 0) {
                this.x = x / length;
                this.y = y / length;
                this.z = z / length;
            } else {
                this.x = 0;
                this.y = 0;
                this.z = 1; // Default forward
            }
        }
    }
}
