package com.example.spawnersphere.config;

import com.example.spawnersphere.common.config.ModConfig;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig.Type;

/**
 * Forge 1.16 config using ForgeConfigSpec
 */
public class ForgeModConfig {

    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    // General Settings
    public static final ForgeConfigSpec.IntValue SPHERE_RADIUS;
    public static final ForgeConfigSpec.IntValue SCAN_RADIUS;
    public static final ForgeConfigSpec.IntValue SCAN_INTERVAL;
    public static final ForgeConfigSpec.DoubleValue MOVEMENT_THRESHOLD;
    public static final ForgeConfigSpec.BooleanValue SHOW_DISTANCE;

    // Performance Settings
    public static final ForgeConfigSpec.BooleanValue ENABLE_SPATIAL_INDEXING;
    public static final ForgeConfigSpec.BooleanValue ENABLE_FRUSTUM_CULLING;
    public static final ForgeConfigSpec.BooleanValue ENABLE_LOD;
    public static final ForgeConfigSpec.IntValue LOD_MAX_SEGMENTS;
    public static final ForgeConfigSpec.IntValue LOD_MIN_SEGMENTS;
    public static final ForgeConfigSpec.DoubleValue LOD_DISTANCE;

    // Rendering Settings
    public static final ForgeConfigSpec.IntValue SPHERE_SEGMENTS;
    public static final ForgeConfigSpec.BooleanValue RENDER_EQUATOR;

    static {
        BUILDER.comment("General Settings").push("general");

        SPHERE_RADIUS = BUILDER
            .comment("Radius of spawner activation sphere (blocks)")
            .defineInRange("sphereRadius", 16, 1, 64);

        SCAN_RADIUS = BUILDER
            .comment("Radius to scan for spawners (blocks)")
            .defineInRange("scanRadius", 64, 16, 256);

        SCAN_INTERVAL = BUILDER
            .comment("How often to scan for spawners (milliseconds)")
            .defineInRange("scanInterval", 60000, 1000, 600000);

        MOVEMENT_THRESHOLD = BUILDER
            .comment("Distance player must move to trigger rescan (blocks)")
            .defineInRange("movementThreshold", 16.0, 1.0, 64.0);

        SHOW_DISTANCE = BUILDER
            .comment("Display distance to nearby spawners")
            .define("showDistanceInActionBar", false);

        BUILDER.pop();
        BUILDER.comment("Performance Settings").push("performance");

        ENABLE_SPATIAL_INDEXING = BUILDER
            .comment("Use chunk-based indexing for better performance")
            .define("enableSpatialIndexing", true);

        ENABLE_FRUSTUM_CULLING = BUILDER
            .comment("Don't render spheres outside camera view")
            .define("enableFrustumCulling", false);

        ENABLE_LOD = BUILDER
            .comment("Reduce detail for distant spheres")
            .define("enableLOD", true);

        LOD_MAX_SEGMENTS = BUILDER
            .comment("Maximum segments for close spheres")
            .defineInRange("lodMaxSegments", 32, 8, 64);

        LOD_MIN_SEGMENTS = BUILDER
            .comment("Minimum segments for distant spheres")
            .defineInRange("lodMinSegments", 16, 4, 32);

        LOD_DISTANCE = BUILDER
            .comment("Distance at which LOD starts reducing detail")
            .defineInRange("lodDistance", 32.0, 16.0, 128.0);

        BUILDER.pop();
        BUILDER.comment("Rendering Settings").push("rendering");

        SPHERE_SEGMENTS = BUILDER
            .comment("Detail level for sphere rendering (when LOD disabled)")
            .defineInRange("sphereSegments", 24, 8, 64);

        RENDER_EQUATOR = BUILDER
            .comment("Render equator line on spheres")
            .define("renderEquator", true);

        BUILDER.pop();

        SPEC = BUILDER.build();
    }

    /**
     * Sync Forge config values to ModConfig
     */
    public static void syncToModConfig(ModConfig modConfig) {
        modConfig.setSphereRadius(SPHERE_RADIUS.get());
        modConfig.setScanRadius(SCAN_RADIUS.get());
        modConfig.setScanInterval(SCAN_INTERVAL.get());
        modConfig.setMovementThreshold(MOVEMENT_THRESHOLD.get());
        modConfig.setShowDistanceInActionBar(SHOW_DISTANCE.get());

        modConfig.setEnableSpatialIndexing(ENABLE_SPATIAL_INDEXING.get());
        modConfig.setEnableFrustumCulling(ENABLE_FRUSTUM_CULLING.get());
        modConfig.setEnableLOD(ENABLE_LOD.get());
        modConfig.setLodMaxSegments(LOD_MAX_SEGMENTS.get());
        modConfig.setLodMinSegments(LOD_MIN_SEGMENTS.get());
        modConfig.setLodDistance(LOD_DISTANCE.get());

        modConfig.setSphereSegments(SPHERE_SEGMENTS.get());
        modConfig.setRenderEquator(RENDER_EQUATOR.get());
    }

    /**
     * Register config with Forge
     */
    public static void register() {
        ModLoadingContext.get().registerConfig(Type.CLIENT, SPEC);
    }
}
