package com.example.spawnersphere.config;

import com.example.spawnersphere.SpawnerSphereMod;
import com.example.spawnersphere.common.config.ModConfig;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Forge 1.12 native config GUI using Forge Configuration API
 */
public class ForgeConfigGui extends GuiConfig {

    private static Configuration configuration;
    private static ModConfig modConfig;

    public ForgeConfigGui(GuiScreen parent) {
        super(parent, getConfigElements(), SpawnerSphereMod.MODID, false, false,
                "Spawner Sphere Configuration");
    }

    /**
     * Initialize the Forge configuration
     */
    public static void init(File configFile, ModConfig config) {
        modConfig = config;
        configuration = new Configuration(configFile);
        syncConfig();
    }

    /**
     * Sync config values between Forge config and ModConfig
     */
    public static void syncConfig() {
        if (configuration == null || modConfig == null) return;

        // General Settings
        String categoryGeneral = "general";
        configuration.addCustomCategoryComment(categoryGeneral, "General spawner sphere settings");

        int sphereRadius = configuration.getInt("sphereRadius", categoryGeneral, 16, 1, 64,
                "Radius of spawner activation sphere (blocks)");
        modConfig.setSphereRadius(sphereRadius);

        int scanRadius = configuration.getInt("scanRadius", categoryGeneral, 64, 16, 256,
                "Radius to scan for spawners (blocks)");
        modConfig.setScanRadius(scanRadius);

        long scanInterval = configuration.getInt("scanInterval", categoryGeneral, 60000, 1000, 600000,
                "How often to scan for spawners (milliseconds)");
        modConfig.setScanInterval(scanInterval);

        double movementThreshold = configuration.getFloat("movementThreshold", categoryGeneral, 16.0f, 1.0f, 64.0f,
                "Distance player must move to trigger rescan (blocks)");
        modConfig.setMovementThreshold(movementThreshold);

        boolean showDistance = configuration.getBoolean("showDistanceInActionBar", categoryGeneral, false,
                "Display distance to nearby spawners");
        modConfig.setShowDistanceInActionBar(showDistance);

        // Performance Settings
        String categoryPerformance = "performance";
        configuration.addCustomCategoryComment(categoryPerformance, "Performance optimization settings");

        boolean spatialIndexing = configuration.getBoolean("enableSpatialIndexing", categoryPerformance, true,
                "Use chunk-based indexing for better performance");
        modConfig.setEnableSpatialIndexing(spatialIndexing);

        boolean frustumCulling = configuration.getBoolean("enableFrustumCulling", categoryPerformance, false,
                "Don't render spheres outside camera view");
        modConfig.setEnableFrustumCulling(frustumCulling);

        boolean lod = configuration.getBoolean("enableLOD", categoryPerformance, true,
                "Reduce detail for distant spheres");
        modConfig.setEnableLOD(lod);

        int lodMaxSegments = configuration.getInt("lodMaxSegments", categoryPerformance, 32, 8, 64,
                "Maximum segments for close spheres");
        modConfig.setLodMaxSegments(lodMaxSegments);

        int lodMinSegments = configuration.getInt("lodMinSegments", categoryPerformance, 16, 4, 32,
                "Minimum segments for distant spheres");
        modConfig.setLodMinSegments(lodMinSegments);

        double lodDistance = configuration.getFloat("lodDistance", categoryPerformance, 32.0f, 16.0f, 128.0f,
                "Distance at which LOD starts reducing detail");
        modConfig.setLodDistance(lodDistance);

        // Rendering Settings
        String categoryRendering = "rendering";
        configuration.addCustomCategoryComment(categoryRendering, "Visual rendering settings");

        int sphereSegments = configuration.getInt("sphereSegments", categoryRendering, 24, 8, 64,
                "Detail level for sphere rendering (when LOD disabled)");
        modConfig.setSphereSegments(sphereSegments);

        boolean renderEquator = configuration.getBoolean("renderEquator", categoryRendering, true,
                "Render equator line on spheres");
        modConfig.setRenderEquator(renderEquator);

        if (configuration.hasChanged()) {
            configuration.save();
        }
    }

    private static List<IConfigElement> getConfigElements() {
        List<IConfigElement> list = new ArrayList<IConfigElement>();

        if (configuration != null) {
            list.addAll(new ConfigElement(configuration.getCategory("general")).getChildElements());
            list.addAll(new ConfigElement(configuration.getCategory("performance")).getChildElements());
            list.addAll(new ConfigElement(configuration.getCategory("rendering")).getChildElements());
        }

        return list;
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        syncConfig();
    }
}
