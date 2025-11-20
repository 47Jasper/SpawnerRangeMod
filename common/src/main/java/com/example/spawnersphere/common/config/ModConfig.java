package com.example.spawnersphere.common.config;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Configuration for the Spawner Sphere mod
 */
public class ModConfig {

    private static final String CONFIG_FILE_NAME = "spawner-sphere-mod.properties";
    private File configFile;

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
        // Ensure sphereRadius <= scanRadius (logical validation)
        if (this.sphereRadius > this.scanRadius) {
            this.scanRadius = this.sphereRadius;
        }
    }

    public int getScanRadius() {
        return scanRadius;
    }

    public void setScanRadius(int scanRadius) {
        this.scanRadius = Math.max(16, Math.min(256, scanRadius));
        // Ensure sphereRadius <= scanRadius (logical validation)
        if (this.sphereRadius > this.scanRadius) {
            this.sphereRadius = this.scanRadius;
        }
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
        // Ensure max >= min (cross-validation)
        if (this.lodMaxSegments < this.lodMinSegments) {
            this.lodMinSegments = this.lodMaxSegments;
        }
    }

    public int getLodMinSegments() {
        return lodMinSegments;
    }

    public void setLodMinSegments(int lodMinSegments) {
        this.lodMinSegments = Math.max(4, Math.min(32, lodMinSegments));
        // Ensure max >= min (cross-validation)
        if (this.lodMinSegments > this.lodMaxSegments) {
            this.lodMaxSegments = this.lodMinSegments;
        }
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

    /**
     * Set the config file location (should be called during mod initialization)
     */
    public void setConfigFile(File configFile) {
        this.configFile = configFile;
    }

    /**
     * Load configuration from file
     * Uses setters to ensure validation logic is applied
     */
    public void load() {
        if (configFile == null || !configFile.exists()) {
            return;
        }

        try (FileInputStream fis = new FileInputStream(configFile)) {
            Properties props = new Properties();
            props.load(fis);

            // Load all config values using setters to apply validation
            // Load scanRadius first to avoid validation issues with sphereRadius
            setScanRadius(Integer.parseInt(props.getProperty("scanRadius", String.valueOf(scanRadius))));
            setSphereRadius(Integer.parseInt(props.getProperty("sphereRadius", String.valueOf(sphereRadius))));
            setScanInterval(Long.parseLong(props.getProperty("scanInterval", String.valueOf(scanInterval))));

            setSphereSegments(Integer.parseInt(props.getProperty("sphereSegments", String.valueOf(sphereSegments))));
            setRenderEquator(Boolean.parseBoolean(props.getProperty("renderEquator", String.valueOf(renderEquator))));
            setShowDistanceInActionBar(Boolean.parseBoolean(props.getProperty("showDistanceInActionBar", String.valueOf(showDistanceInActionBar))));

            setEnableSpatialIndexing(Boolean.parseBoolean(props.getProperty("enableSpatialIndexing", String.valueOf(enableSpatialIndexing))));
            setEnableFrustumCulling(Boolean.parseBoolean(props.getProperty("enableFrustumCulling", String.valueOf(enableFrustumCulling))));
            setEnableLOD(Boolean.parseBoolean(props.getProperty("enableLOD", String.valueOf(enableLOD))));

            // Load LOD max segments first to avoid validation issues with min segments
            setLodMaxSegments(Integer.parseInt(props.getProperty("lodMaxSegments", String.valueOf(lodMaxSegments))));
            setLodMinSegments(Integer.parseInt(props.getProperty("lodMinSegments", String.valueOf(lodMinSegments))));
            setLodDistance(Double.parseDouble(props.getProperty("lodDistance", String.valueOf(lodDistance))));
            setMovementThreshold(Double.parseDouble(props.getProperty("movementThreshold", String.valueOf(movementThreshold))));

            // Load colors
            outsideRangeColor = new ColorConfig(
                Integer.parseInt(props.getProperty("outsideRangeColor.red", "128")),
                Integer.parseInt(props.getProperty("outsideRangeColor.green", "255")),
                Integer.parseInt(props.getProperty("outsideRangeColor.blue", "0")),
                Integer.parseInt(props.getProperty("outsideRangeColor.alpha", "51"))
            );
            insideRangeColor = new ColorConfig(
                Integer.parseInt(props.getProperty("insideRangeColor.red", "255")),
                Integer.parseInt(props.getProperty("insideRangeColor.green", "128")),
                Integer.parseInt(props.getProperty("insideRangeColor.blue", "0")),
                Integer.parseInt(props.getProperty("insideRangeColor.alpha", "102"))
            );

            // Validate the final configuration state
            validateConfig();
        } catch (IOException e) {
            System.err.println("Failed to load config file: " + e.getMessage());
            // Keep default values
        } catch (NumberFormatException e) {
            System.err.println("Invalid number format in config file: " + e.getMessage());
            // Keep default values
        } catch (IllegalStateException e) {
            System.err.println("Config validation failed: " + e.getMessage());
            // Reset to safe defaults
            resetToDefaults();
        }
    }

    /**
     * Validate the entire configuration state
     * @throws IllegalStateException if configuration is invalid
     */
    private void validateConfig() throws IllegalStateException {
        List<String> errors = new ArrayList<>();

        // Validate sphereRadius <= scanRadius
        if (sphereRadius > scanRadius) {
            errors.add("sphereRadius (" + sphereRadius + ") must be <= scanRadius (" + scanRadius + ")");
        }

        // Validate LOD parameters
        if (lodMaxSegments < lodMinSegments) {
            errors.add("lodMaxSegments (" + lodMaxSegments + ") must be >= lodMinSegments (" + lodMinSegments + ")");
        }

        // Validate ranges
        if (sphereRadius < 1 || sphereRadius > 64) {
            errors.add("sphereRadius must be between 1 and 64");
        }
        if (scanRadius < 16 || scanRadius > 256) {
            errors.add("scanRadius must be between 16 and 256");
        }
        if (sphereSegments < 8 || sphereSegments > 64) {
            errors.add("sphereSegments must be between 8 and 64");
        }
        if (lodMaxSegments < 8 || lodMaxSegments > 64) {
            errors.add("lodMaxSegments must be between 8 and 64");
        }
        if (lodMinSegments < 4 || lodMinSegments > 32) {
            errors.add("lodMinSegments must be between 4 and 32");
        }
        if (lodDistance < 16.0 || lodDistance > 128.0) {
            errors.add("lodDistance must be between 16.0 and 128.0");
        }
        if (movementThreshold < 1.0 || movementThreshold > 64.0) {
            errors.add("movementThreshold must be between 1.0 and 64.0");
        }
        if (scanInterval < 1000) {
            errors.add("scanInterval must be >= 1000 milliseconds");
        }

        if (!errors.isEmpty()) {
            throw new IllegalStateException("Configuration validation failed:\n" + String.join("\n", errors));
        }
    }

    /**
     * Reset all configuration values to safe defaults
     */
    private void resetToDefaults() {
        sphereRadius = 16;
        scanRadius = 64;
        scanInterval = 60000;
        outsideRangeColor = new ColorConfig(128, 255, 0, 51);
        insideRangeColor = new ColorConfig(255, 128, 0, 102);
        sphereSegments = 24;
        renderEquator = true;
        showDistanceInActionBar = false;
        enableSpatialIndexing = true;
        enableFrustumCulling = false;
        enableLOD = true;
        lodMaxSegments = 32;
        lodMinSegments = 16;
        lodDistance = 32.0;
        movementThreshold = 16.0;
    }

    /**
     * Save configuration to file
     */
    public void save() {
        if (configFile == null) {
            return;
        }

        // Ensure parent directory exists
        File parentDir = configFile.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }

        try (FileOutputStream fos = new FileOutputStream(configFile)) {
            Properties props = new Properties();

            // Save all config values
            props.setProperty("sphereRadius", String.valueOf(sphereRadius));
            props.setProperty("scanRadius", String.valueOf(scanRadius));
            props.setProperty("scanInterval", String.valueOf(scanInterval));

            props.setProperty("sphereSegments", String.valueOf(sphereSegments));
            props.setProperty("renderEquator", String.valueOf(renderEquator));
            props.setProperty("showDistanceInActionBar", String.valueOf(showDistanceInActionBar));

            props.setProperty("enableSpatialIndexing", String.valueOf(enableSpatialIndexing));
            props.setProperty("enableFrustumCulling", String.valueOf(enableFrustumCulling));
            props.setProperty("enableLOD", String.valueOf(enableLOD));
            props.setProperty("lodMaxSegments", String.valueOf(lodMaxSegments));
            props.setProperty("lodMinSegments", String.valueOf(lodMinSegments));
            props.setProperty("lodDistance", String.valueOf(lodDistance));
            props.setProperty("movementThreshold", String.valueOf(movementThreshold));

            // Save colors
            props.setProperty("outsideRangeColor.red", String.valueOf(outsideRangeColor.getRed()));
            props.setProperty("outsideRangeColor.green", String.valueOf(outsideRangeColor.getGreen()));
            props.setProperty("outsideRangeColor.blue", String.valueOf(outsideRangeColor.getBlue()));
            props.setProperty("outsideRangeColor.alpha", String.valueOf(outsideRangeColor.getAlpha()));
            props.setProperty("insideRangeColor.red", String.valueOf(insideRangeColor.getRed()));
            props.setProperty("insideRangeColor.green", String.valueOf(insideRangeColor.getGreen()));
            props.setProperty("insideRangeColor.blue", String.valueOf(insideRangeColor.getBlue()));
            props.setProperty("insideRangeColor.alpha", String.valueOf(insideRangeColor.getAlpha()));

            props.store(fos, "Spawner Sphere Mod Configuration");
        } catch (IOException e) {
            System.err.println("Failed to save config: " + e.getMessage());
        }
    }
}
