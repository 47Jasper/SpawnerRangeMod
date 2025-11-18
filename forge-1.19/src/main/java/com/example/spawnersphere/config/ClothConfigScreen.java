package com.example.spawnersphere.config;

import com.example.spawnersphere.common.config.IConfigScreen;
import com.example.spawnersphere.common.config.ModConfig;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

/**
 * Cloth Config implementation for Forge 1.20
 */
public class ClothConfigScreen implements IConfigScreen {

    private final ModConfig config;

    public ClothConfigScreen(ModConfig config) {
        this.config = config;
    }

    @Override
    public Object createConfigScreen(Object parent) {
        ConfigBuilder builder = ConfigBuilder.create()
            .setParentScreen((Screen) parent)
            .setTitle(Component.literal("Spawner Sphere Configuration"))
            .setSavingRunnable(() -> {
                // Config changes are applied immediately via setters
                // Future: Save to file here
            });

        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        // General Settings Category
        ConfigCategory general = builder.getOrCreateCategory(Component.literal("General"));

        general.addEntry(entryBuilder.startIntSlider(
            Component.literal("Sphere Radius"),
            config.getSphereRadius(),
            1, 64
        )
        .setDefaultValue(16)
        .setTooltip(Component.literal("Radius of spawner activation sphere (blocks)"))
        .setSaveConsumer(config::setSphereRadius)
        .build());

        general.addEntry(entryBuilder.startIntSlider(
            Component.literal("Scan Radius"),
            config.getScanRadius(),
            16, 256
        )
        .setDefaultValue(64)
        .setTooltip(Component.literal("Radius to scan for spawners (blocks)"))
        .setSaveConsumer(config::setScanRadius)
        .build());

        general.addEntry(entryBuilder.startLongField(
            Component.literal("Scan Interval (ms)"),
            config.getScanInterval()
        )
        .setDefaultValue(60000L)
        .setMin(1000L)
        .setTooltip(Component.literal("How often to scan for spawners (milliseconds)"))
        .setSaveConsumer(config::setScanInterval)
        .build());

        general.addEntry(entryBuilder.startDoubleField(
            Component.literal("Movement Threshold"),
            config.getMovementThreshold()
        )
        .setDefaultValue(16.0)
        .setMin(1.0)
        .setMax(64.0)
        .setTooltip(Component.literal("Distance player must move to trigger rescan (blocks)"))
        .setSaveConsumer(config::setMovementThreshold)
        .build());

        general.addEntry(entryBuilder.startBooleanToggle(
            Component.literal("Show Distance in Action Bar"),
            config.isShowDistanceInActionBar()
        )
        .setDefaultValue(false)
        .setTooltip(Component.literal("Display distance to nearby spawners"))
        .setSaveConsumer(config::setShowDistanceInActionBar)
        .build());

        // Performance Category
        ConfigCategory performance = builder.getOrCreateCategory(Component.literal("Performance"));

        performance.addEntry(entryBuilder.startBooleanToggle(
            Component.literal("Enable Spatial Indexing"),
            config.isEnableSpatialIndexing()
        )
        .setDefaultValue(true)
        .setTooltip(Component.literal("Use chunk-based indexing for better performance"))
        .setSaveConsumer(config::setEnableSpatialIndexing)
        .build());

        performance.addEntry(entryBuilder.startBooleanToggle(
            Component.literal("Enable Frustum Culling"),
            config.isEnableFrustumCulling()
        )
        .setDefaultValue(false)
        .setTooltip(Component.literal("Don't render spheres outside camera view"))
        .setSaveConsumer(config::setEnableFrustumCulling)
        .build());

        performance.addEntry(entryBuilder.startBooleanToggle(
            Component.literal("Enable Level of Detail (LOD)"),
            config.isEnableLOD()
        )
        .setDefaultValue(true)
        .setTooltip(Component.literal("Reduce detail for distant spheres"))
        .setSaveConsumer(config::setEnableLOD)
        .build());

        performance.addEntry(entryBuilder.startIntSlider(
            Component.literal("LOD Max Segments"),
            config.getLodMaxSegments(),
            8, 64
        )
        .setDefaultValue(32)
        .setTooltip(Component.literal("Maximum segments for close spheres"))
        .setSaveConsumer(config::setLodMaxSegments)
        .build());

        performance.addEntry(entryBuilder.startIntSlider(
            Component.literal("LOD Min Segments"),
            config.getLodMinSegments(),
            4, 32
        )
        .setDefaultValue(16)
        .setTooltip(Component.literal("Minimum segments for distant spheres"))
        .setSaveConsumer(config::setLodMinSegments)
        .build());

        performance.addEntry(entryBuilder.startDoubleField(
            Component.literal("LOD Distance"),
            config.getLodDistance()
        )
        .setDefaultValue(32.0)
        .setMin(16.0)
        .setMax(128.0)
        .setTooltip(Component.literal("Distance at which LOD starts reducing detail"))
        .setSaveConsumer(config::setLodDistance)
        .build());

        // Rendering Category
        ConfigCategory rendering = builder.getOrCreateCategory(Component.literal("Rendering"));

        rendering.addEntry(entryBuilder.startIntSlider(
            Component.literal("Sphere Segments"),
            config.getSphereSegments(),
            8, 64
        )
        .setDefaultValue(24)
        .setTooltip(Component.literal("Detail level for sphere rendering (when LOD disabled)"))
        .setSaveConsumer(config::setSphereSegments)
        .build());

        rendering.addEntry(entryBuilder.startBooleanToggle(
            Component.literal("Render Equator"),
            config.isRenderEquator()
        )
        .setDefaultValue(true)
        .setTooltip(Component.literal("Render equator line on spheres"))
        .setSaveConsumer(config::setRenderEquator)
        .build());

        return builder.build();
    }

    @Override
    public boolean isAvailable() {
        try {
            Class.forName("me.shedaniel.clothconfig2.api.ConfigBuilder");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
