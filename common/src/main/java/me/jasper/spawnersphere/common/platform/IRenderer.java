package me.jasper.spawnersphere.common.platform;

import me.jasper.spawnersphere.common.config.ModConfig;
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
     * @param segments Number of segments for sphere rendering (LOD support)
     */
    void renderSphere(
        @NotNull Object context,
        double x,
        double y,
        double z,
        float radius,
        @NotNull SphereColor color,
        int segments
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
         * Color for when player is outside activation range
         */
        public static SphereColor outsideRange(@NotNull ModConfig.ColorConfig colorConfig) {
            return new SphereColor(
                colorConfig.getRedFloat(),
                colorConfig.getGreenFloat(),
                colorConfig.getBlueFloat(),
                colorConfig.getAlphaFloat()
            );
        }

        /**
         * Color for when player is inside activation range
         */
        public static SphereColor insideRange(@NotNull ModConfig.ColorConfig colorConfig) {
            return new SphereColor(
                colorConfig.getRedFloat(),
                colorConfig.getGreenFloat(),
                colorConfig.getBlueFloat(),
                colorConfig.getAlphaFloat()
            );
        }
    }
}
