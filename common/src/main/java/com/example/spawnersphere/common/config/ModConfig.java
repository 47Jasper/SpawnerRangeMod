package com.example.spawnersphere.common.config;

/**
 * Configuration for the Spawner Sphere mod
 */
public class ModConfig {

    // Sphere rendering
    private int sphereRadius = 16;
    private int scanRadius = 64;
    private long scanInterval = 60000; // milliseconds (60 seconds)

    // Colors (RGB 0-255)
    private ColorConfig outsideRangeColor = new ColorConfig(128, 255, 0, 51);  // Green/Yellow
    private ColorConfig insideRangeColor = new ColorConfig(255, 128, 0, 102);  // Yellow/Red

    // Rendering quality
    private int sphereSegments = 24;
    private boolean renderEquator = true;
    private boolean showDistanceInActionBar = false;

    // Performance optimizations
    private boolean enableSpatialIndexing = true;
    private boolean enableFrustumCulling = false; // Disabled by default (requires player look vector)
    private boolean enableLOD = true;
    private int lodMaxSegments = 32;
    private int lodMinSegments = 16;
    private double lodDistance = 32.0; // Distance at which LOD starts reducing detail
    private double movementThreshold = 16.0; // Player must move this far to trigger rescan

    public int getSphereRadius() {
        return sphereRadius;
    }

    public void setSphereRadius(int sphereRadius) {
        this.sphereRadius = Math.max(1, Math.min(64, sphereRadius));
    }

    public int getScanRadius() {
        return scanRadius;
    }

    public void setScanRadius(int scanRadius) {
        this.scanRadius = Math.max(16, Math.min(256, scanRadius));
    }

    public long getScanInterval() {
        return scanInterval;
    }

    public void setScanInterval(long scanInterval) {
        this.scanInterval = Math.max(1000, scanInterval);
    }

    public ColorConfig getOutsideRangeColor() {
        return outsideRangeColor;
    }

    public ColorConfig getInsideRangeColor() {
        return insideRangeColor;
    }

    public int getSphereSegments() {
        return sphereSegments;
    }

    public void setSphereSegments(int sphereSegments) {
        this.sphereSegments = Math.max(8, Math.min(64, sphereSegments));
    }

    public boolean isRenderEquator() {
        return renderEquator;
    }

    public void setRenderEquator(boolean renderEquator) {
        this.renderEquator = renderEquator;
    }

    public boolean isShowDistanceInActionBar() {
        return showDistanceInActionBar;
    }

    public void setShowDistanceInActionBar(boolean showDistanceInActionBar) {
        this.showDistanceInActionBar = showDistanceInActionBar;
    }

    public boolean isEnableSpatialIndexing() {
        return enableSpatialIndexing;
    }

    public void setEnableSpatialIndexing(boolean enableSpatialIndexing) {
        this.enableSpatialIndexing = enableSpatialIndexing;
    }

    public boolean isEnableFrustumCulling() {
        return enableFrustumCulling;
    }

    public void setEnableFrustumCulling(boolean enableFrustumCulling) {
        this.enableFrustumCulling = enableFrustumCulling;
    }

    public boolean isEnableLOD() {
        return enableLOD;
    }

    public void setEnableLOD(boolean enableLOD) {
        this.enableLOD = enableLOD;
    }

    public int getLodMaxSegments() {
        return lodMaxSegments;
    }

    public void setLodMaxSegments(int lodMaxSegments) {
        this.lodMaxSegments = Math.max(8, Math.min(64, lodMaxSegments));
    }

    public int getLodMinSegments() {
        return lodMinSegments;
    }

    public void setLodMinSegments(int lodMinSegments) {
        this.lodMinSegments = Math.max(4, Math.min(32, lodMinSegments));
    }

    public double getLodDistance() {
        return lodDistance;
    }

    public void setLodDistance(double lodDistance) {
        this.lodDistance = Math.max(16.0, Math.min(128.0, lodDistance));
    }

    public double getMovementThreshold() {
        return movementThreshold;
    }

    public void setMovementThreshold(double movementThreshold) {
        this.movementThreshold = Math.max(1.0, Math.min(64.0, movementThreshold));
    }

    public static class ColorConfig {
        private int red;
        private int green;
        private int blue;
        private int alpha;

        public ColorConfig(int red, int green, int blue, int alpha) {
            this.red = clamp(red);
            this.green = clamp(green);
            this.blue = clamp(blue);
            this.alpha = clamp(alpha);
        }

        private int clamp(int value) {
            return Math.max(0, Math.min(255, value));
        }

        public float getRedFloat() {
            return red / 255.0f;
        }

        public float getGreenFloat() {
            return green / 255.0f;
        }

        public float getBlueFloat() {
            return blue / 255.0f;
        }

        public float getAlphaFloat() {
            return alpha / 255.0f;
        }

        public int getRed() { return red; }
        public int getGreen() { return green; }
        public int getBlue() { return blue; }
        public int getAlpha() { return alpha; }
    }
}
