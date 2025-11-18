package com.example.spawnersphere.common.platform;

import org.jetbrains.annotations.NotNull;

/**
 * Platform-agnostic renderer interface
 */
public interface IRenderer {

    /**
     * Render a sphere at the given position with the specified radius and color
     *
     * @param context Platform-specific rendering context
     * @param x Center X coordinate
     * @param y Center Y coordinate
     * @param z Center Z coordinate
     * @param radius Sphere radius
     * @param color Color information
     */
    void renderSphere(
        @NotNull Object context,
        double x,
        double y,
        double z,
        float radius,
        @NotNull SphereColor color
    );

    /**
     * Color configuration for sphere rendering
     */
    class SphereColor {
        public final float red;
        public final float green;
        public final float blue;
        public final float alpha;

        public SphereColor(float red, float green, float blue, float alpha) {
            this.red = red;
            this.green = green;
            this.blue = blue;
            this.alpha = alpha;
        }

        /**
         * Color for when player is outside activation range (Green/Yellow)
         */
        public static SphereColor outsideRange() {
            return new SphereColor(0.5f, 1.0f, 0.0f, 0.2f);
        }

        /**
         * Color for when player is inside activation range (Yellow/Red)
         */
        public static SphereColor insideRange() {
            return new SphereColor(1.0f, 0.5f, 0.0f, 0.4f);
        }
    }
}
