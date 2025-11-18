# Config GUI Integration Guide

This document describes how to integrate configuration GUI support using Cloth Config API.

## Architecture

The config GUI system uses a platform-agnostic interface pattern:

- **IConfigScreen**: Interface for platform-specific config screens
- **ConfigScreenFactory**: Factory for registering and accessing config screens
- **ModConfig**: Core configuration class with all settings

## Integration Steps

### 1. Add Cloth Config Dependency

**For Fabric modules**, add to `build.gradle`:
```gradle
dependencies {
    modImplementation("me.shedaniel.cloth:cloth-config-fabric:11.0.99") {
        exclude(group: "net.fabricmc.fabric-api")
    }
}
```

**For Forge modules**, add to `build.gradle`:
```gradle
dependencies {
    implementation("me.shedaniel.cloth:cloth-config-forge:11.0.99")
}
```

**Versions**:
- Fabric 1.19+: Use Cloth Config 11.x
- Fabric 1.17-1.18: Use Cloth Config 8.x
- Fabric 1.14-1.16: Use Cloth Config 4.x
- Legacy Fabric: Not supported (use manual config files)
- Forge 1.20+: Use Cloth Config 11.x (Forge variant)
- Forge 1.16-1.19: Use Cloth Config 8.x (Forge variant)
- Forge 1.12: Not supported (use Forge config GUI)

### 2. Create Config Screen Implementation

Example for Fabric 1.21:

```java
package com.example.spawnersphere.config;

import com.example.spawnersphere.common.config.IConfigScreen;
import com.example.spawnersphere.common.config.ModConfig;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class ClothConfigScreen implements IConfigScreen {

    private final ModConfig config;

    public ClothConfigScreen(ModConfig config) {
        this.config = config;
    }

    @Override
    public Object createConfigScreen(Object parent) {
        ConfigBuilder builder = ConfigBuilder.create()
            .setParentScreen((Screen) parent)
            .setTitle(Text.literal("Spawner Sphere Configuration"));

        ConfigCategory general = builder.getOrCreateCategory(Text.literal("General"));
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        // Sphere Radius
        general.addEntry(entryBuilder.startIntSlider(
            Text.literal("Sphere Radius"),
            config.getSphereRadius(),
            1, 64
        )
        .setDefaultValue(16)
        .setTooltip(Text.literal("Radius of spawner activation sphere (blocks)"))
        .setSaveConsumer(config::setSphereRadius)
        .build());

        // Scan Radius
        general.addEntry(entryBuilder.startIntSlider(
            Text.literal("Scan Radius"),
            config.getScanRadius(),
            16, 256
        )
        .setDefaultValue(64)
        .setTooltip(Text.literal("Radius to scan for spawners (blocks)"))
        .setSaveConsumer(config::setScanRadius)
        .build());

        // Scan Interval
        general.addEntry(entryBuilder.startLongField(
            Text.literal("Scan Interval (ms)"),
            config.getScanInterval()
        )
        .setDefaultValue(60000L)
        .setTooltip(Text.literal("How often to scan for spawners (milliseconds)"))
        .setSaveConsumer(config::setScanInterval)
        .build());

        // Performance category
        ConfigCategory performance = builder.getOrCreateCategory(Text.literal("Performance"));

        performance.addEntry(entryBuilder.startBooleanToggle(
            Text.literal("Enable Spatial Indexing"),
            config.isEnableSpatialIndexing()
        )
        .setDefaultValue(true)
        .setTooltip(Text.literal("Use chunk-based indexing for better performance"))
        .setSaveConsumer(config::setEnableSpatialIndexing)
        .build());

        performance.addEntry(entryBuilder.startBooleanToggle(
            Text.literal("Enable LOD"),
            config.isEnableLOD()
        )
        .setDefaultValue(true)
        .setTooltip(Text.literal("Reduce detail for distant spheres"))
        .setSaveConsumer(config::setEnableLOD)
        .build());

        builder.setSavingRunnable(() -> {
            // Save config to file if needed
            // For now, changes are applied in-memory
        });

        return builder.build();
    }

    @Override
    public boolean isAvailable() {
        return true; // Cloth Config is available
    }
}
```

### 3. Register Config Screen

In your main mod class initialization:

```java
import com.example.spawnersphere.common.config.ConfigScreenFactory;
import com.example.spawnersphere.config.ClothConfigScreen;

public void onInitializeClient() {
    // ... other initialization

    // Register config screen
    ConfigScreenFactory.register(new ClothConfigScreen(core.getConfig()));
}
```

### 4. Register with Mod Menu (Fabric)

Add to `fabric.mod.json`:
```json
{
  "entrypoints": {
    "modmenu": [
      "com.example.spawnersphere.ModMenuIntegration"
    ]
  }
}
```

Create ModMenuIntegration class:
```java
package com.example.spawnersphere;

import com.example.spawnersphere.common.config.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> {
            if (ConfigScreenFactory.isAvailable()) {
                return (Screen) ConfigScreenFactory.get().createConfigScreen(parent);
            }
            return null;
        };
    }
}
```

## Configuration Options

All available configuration options:

### General
- **Sphere Radius** (1-64, default: 16): Size of activation sphere
- **Scan Radius** (16-256, default: 64): How far to scan for spawners
- **Scan Interval** (1000+, default: 60000): Time between scans in milliseconds
- **Show Distance** (boolean, default: false): Show distance in action bar

### Colors
- **Outside Range Color** (RGBA, default: 128,255,0,51): Green/Yellow
- **Inside Range Color** (RGBA, default: 255,128,0,102): Yellow/Red

### Performance
- **Enable Spatial Indexing** (boolean, default: true): Use chunk-based optimization
- **Enable Frustum Culling** (boolean, default: false): Cull off-screen spheres
- **Enable LOD** (boolean, default: true): Level of detail system
- **LOD Max Segments** (8-64, default: 32): Maximum segments for close spheres
- **LOD Min Segments** (4-32, default: 16): Minimum segments for distant spheres
- **LOD Distance** (16-128, default: 32): Distance to start LOD
- **Movement Threshold** (1-64, default: 16): Distance player must move to trigger rescan

### Rendering
- **Sphere Segments** (8-64, default: 24): Detail level for sphere rendering
- **Render Equator** (boolean, default: true): Render equator line

## Optional Dependency

Cloth Config is **optional**. If not present:
1. Config GUI will not be available in Mod Menu
2. All settings will use default values
3. Manual config file editing can still be used (future feature)
4. Mod will function normally with defaults

## Platform Support

| Platform | Cloth Config Support | Alternative |
|----------|---------------------|-------------|
| Fabric 1.19+ | ✅ Supported | - |
| Fabric 1.17-1.18 | ✅ Supported (v8.x) | - |
| Fabric 1.14-1.16 | ✅ Supported (v4.x) | - |
| Legacy Fabric | ❌ Not available | Manual config |
| Forge 1.20+ | ✅ Supported | Forge Config GUI |
| Forge 1.16-1.19 | ✅ Supported (v8.x) | Forge Config GUI |
| Forge 1.12 | ❌ Use Forge Config | Forge Config GUI |
| NeoForge 1.20+ | ✅ Supported | - |

## Future Enhancements

1. **Config File Persistence**: Save/load config from JSON file
2. **Live Updates**: Apply config changes without restart
3. **Presets**: Pre-configured performance/quality presets
4. **Color Picker**: Visual color selection UI
5. **Per-World Config**: Different settings for different dimensions

## Testing

To test config GUI:
1. Build module with Cloth Config dependency
2. Launch Minecraft
3. Open Mod Menu
4. Click on "Spawner Sphere" mod
5. Click "Config" button
6. Verify all options are present and functional
7. Test saving and loading settings
