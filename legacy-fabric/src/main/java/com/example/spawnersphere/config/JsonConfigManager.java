package com.example.spawnersphere.config;

import com.example.spawnersphere.common.config.ModConfig;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

/**
 * Simple JSON-based config manager for Legacy Fabric
 */
public class JsonConfigManager {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static File configFile;

    /**
     * Load config from JSON file
     */
    public static void load(File file, ModConfig config) {
        configFile = file;

        if (!file.exists()) {
            // Create default config
            save(config);
            return;
        }

        try {
            FileReader reader = new FileReader(file);
            JsonObject json = GSON.fromJson(reader, JsonObject.class);
            reader.close();

            if (json == null) return;

            // Load general settings
            if (json.has("sphereRadius")) {
                config.setSphereRadius(json.get("sphereRadius").getAsInt());
            }
            if (json.has("scanRadius")) {
                config.setScanRadius(json.get("scanRadius").getAsInt());
            }
            if (json.has("scanInterval")) {
                config.setScanInterval(json.get("scanInterval").getAsLong());
            }
            if (json.has("movementThreshold")) {
                config.setMovementThreshold(json.get("movementThreshold").getAsDouble());
            }
            if (json.has("showDistanceInActionBar")) {
                config.setShowDistanceInActionBar(json.get("showDistanceInActionBar").getAsBoolean());
            }

            // Load performance settings
            if (json.has("enableSpatialIndexing")) {
                config.setEnableSpatialIndexing(json.get("enableSpatialIndexing").getAsBoolean());
            }
            if (json.has("enableFrustumCulling")) {
                config.setEnableFrustumCulling(json.get("enableFrustumCulling").getAsBoolean());
            }
            if (json.has("enableLOD")) {
                config.setEnableLOD(json.get("enableLOD").getAsBoolean());
            }
            if (json.has("lodMaxSegments")) {
                config.setLodMaxSegments(json.get("lodMaxSegments").getAsInt());
            }
            if (json.has("lodMinSegments")) {
                config.setLodMinSegments(json.get("lodMinSegments").getAsInt());
            }
            if (json.has("lodDistance")) {
                config.setLodDistance(json.get("lodDistance").getAsDouble());
            }

            // Load rendering settings
            if (json.has("sphereSegments")) {
                config.setSphereSegments(json.get("sphereSegments").getAsInt());
            }
            if (json.has("renderEquator")) {
                config.setRenderEquator(json.get("renderEquator").getAsBoolean());
            }

        } catch (Exception e) {
            System.err.println("Failed to load config: " + e.getMessage());
        }
    }

    /**
     * Save config to JSON file
     */
    public static void save(ModConfig config) {
        if (configFile == null) return;

        try {
            JsonObject json = new JsonObject();

            // Save general settings
            json.addProperty("sphereRadius", config.getSphereRadius());
            json.addProperty("scanRadius", config.getScanRadius());
            json.addProperty("scanInterval", config.getScanInterval());
            json.addProperty("movementThreshold", config.getMovementThreshold());
            json.addProperty("showDistanceInActionBar", config.isShowDistanceInActionBar());

            // Save performance settings
            json.addProperty("enableSpatialIndexing", config.isEnableSpatialIndexing());
            json.addProperty("enableFrustumCulling", config.isEnableFrustumCulling());
            json.addProperty("enableLOD", config.isEnableLOD());
            json.addProperty("lodMaxSegments", config.getLodMaxSegments());
            json.addProperty("lodMinSegments", config.getLodMinSegments());
            json.addProperty("lodDistance", config.getLodDistance());

            // Save rendering settings
            json.addProperty("sphereSegments", config.getSphereSegments());
            json.addProperty("renderEquator", config.isRenderEquator());

            // Ensure parent directory exists
            if (!configFile.getParentFile().exists()) {
                configFile.getParentFile().mkdirs();
            }

            FileWriter writer = new FileWriter(configFile);
            GSON.toJson(json, writer);
            writer.close();

        } catch (Exception e) {
            System.err.println("Failed to save config: " + e.getMessage());
        }
    }
}
