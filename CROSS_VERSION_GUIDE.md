# Cross-Version Support Guide

## Architecture Overview

This mod uses a **multi-module Gradle architecture** to support multiple Minecraft versions simultaneously. Each Minecraft version gets its own independent build module.

```
SpawnerRangeMod/
├── common/                    # Platform-agnostic core logic (shared by all)
├── fabric-1.14.4/            # Targets MC 1.14.4
├── fabric-1.15.2/            # Targets MC 1.15.2
├── fabric-1.16.5/            # Targets MC 1.16.5
├── fabric-1.17.1/            # Targets MC 1.17.1
├── fabric-1.18.2/            # Targets MC 1.18.2
├── fabric-1.19.2/            # Targets MC 1.19.2
├── fabric-1.19.4/            # Targets MC 1.19.4
├── fabric-1.20/              # Targets MC 1.20.4
├── fabric-1.20.1/            # Targets MC 1.20.1
├── fabric-1.20.6/            # Targets MC 1.20.6
├── fabric-1.21.1/            # Targets MC 1.21.1
├── fabric-1.21.3/            # Targets MC 1.21.3
├── fabric-1.21.4/            # Targets MC 1.21.4
├── fabric-1.21.5/            # Targets MC 1.21.5
├── fabric-1.21.8/            # Targets MC 1.21.8
├── fabric-1.21.10/           # Targets MC 1.21.10
├── forge-*/                  # Forge modules
├── legacy-fabric/            # MC 1.8.9-1.13.2
└── neoforge-*/               # NeoForge modules
```

## How It Works

### 1. Independent Builds per Version

Each module folder contains:
- **`build.gradle`**: Version-specific dependencies
- **`src/main/java/`**: Platform-specific implementation code
- **`src/main/resources/fabric.mod.json`**: Mod metadata and compatibility

### 2. Shared Core Logic

The `common/` module contains:
- All game logic (spawner detection, rendering, config)
- Platform interfaces (`IPlatformHelper`, `IRenderer`)
- Performance optimizations (LOD, frustum culling, spatial indexing)

**Each platform module implements the interfaces** to bridge Minecraft APIs to common code.

### 3. Version-Specific Dependencies

Each `build.gradle` specifies exact versions:

```gradle
dependencies {
    implementation project(':common')  // Shared code

    minecraft 'com.mojang:minecraft:1.21.10'
    mappings "net.fabricmc:yarn:1.21.10+build.1:v2"
    modImplementation "net.fabricmc:fabric-loader:0.17.0"
    modImplementation "net.fabricmc.fabric-api:fabric-api:0.138.3+1.21.10"  // MC version-specific!
}
```

## Updating Fabric API Versions

### Current Fabric API Versions

| Module | MC Version | Fabric API Version | Notes |
|--------|-----------|-------------------|-------|
| `fabric-1.21.10` | 1.21.10 | **0.138.3+1.21.10** | Latest |
| `fabric-1.21.8` | 1.21.8 | 0.136.1+1.21.8 | |
| `fabric-1.21.5` | 1.21.5 | 0.128.2+1.21.5 | |
| `fabric-1.21.4` | 1.21.4 | 0.119.4+1.21.4 | |
| `fabric-1.21.3` | 1.21.3 | 0.114.1+1.21.3 | |
| `fabric-1.21.1` | 1.21.1 | 0.116.7+1.21.1 | |
| `fabric-1.20.6` | 1.20.6 | 0.100.8+1.20.6 | |
| `fabric-1.20` | 1.20.4 | 0.97.3+1.20.4 | |
| `fabric-1.20.1` | 1.20.1 | 0.92.5+1.20.1 | |
| `fabric-1.19.4` | 1.19.4 | 0.87.2+1.19.4 | |
| `fabric-1.19.2` | 1.19.2 | 0.77.0+1.19.2 | |
| `fabric-1.18.2` | 1.18.2 | 0.76.0+1.18.2 | JOML Matrix4f |
| `fabric-1.17.1` | 1.17.1 | 0.46.1+1.17 | JOML Matrix4f |
| `fabric-1.16.5` | 1.16.5 | 0.42.0+1.16 | Mojang Matrix4f |
| `fabric-1.15.2` | 1.15.2 | 0.28.5+1.15 | Mojang Matrix4f |
| `fabric-1.14.4` | 1.14.4 | 0.28.5+1.14 | Mojang Matrix4f |

### How to Update

1. **Check the latest Fabric API version** for your MC version at:
   - https://modrinth.com/mod/fabric-api/versions
   - https://maven.fabricmc.net/net/fabricmc/fabric-api/fabric-api/

2. **Update only the matching MC version** in that module's `build.gradle`:
   ```gradle
   modImplementation "net.fabricmc.fabric-api:fabric-api:0.138.3+1.21.10"
   //                                                       ^^^^^^^^ Must match MC version
   ```

3. **Test that specific build**:
   ```bash
   ./gradlew :fabric-1.21.9:build
   ```

### Important Rules

✅ **DO:**
- Update Fabric API to the latest stable version for each MC version
- Keep each module's dependencies independent
- Test each module after updating

❌ **DON'T:**
- Mix Fabric API versions between MC versions (e.g., don't use `+1.21.10` for MC 1.21.9)
- Add support for SNAPSHOT Minecraft versions (unstable)
- Share Fabric API dependencies across modules

## Adding Support for New Minecraft Versions

### When to Add a New Module

Only add modules for **stable Minecraft releases**, not snapshots (e.g., 1.21.11-SNAPSHOT).

Wait until:
1. Minecraft version is officially released
2. Fabric Loader supports it
3. Fabric API has a stable release for it

### Steps to Add a New Version

1. **Copy the most recent module**:
   ```bash
   cp -r fabric-1.21.9 fabric-1.21.11  # When 1.21.11 is stable
   ```

2. **Update `build.gradle`**:
   ```gradle
   minecraft 'com.mojang:minecraft:1.21.11'
   mappings "net.fabricmc:yarn:1.21.11+build.1:v2"
   modImplementation "net.fabricmc:fabric-loader:0.17.x"  # Latest
   modImplementation "net.fabricmc.fabric-api:fabric-api:0.xxx.x+1.21.11"  # New version
   ```

3. **Update `fabric.mod.json`**:
   ```json
   "minecraft": ">=1.21.11 <=1.21.11"
   ```

4. **Add to `settings.gradle`**:
   ```gradle
   include 'fabric-1.21.11'
   ```

5. **Test the build**:
   ```bash
   ./gradlew :fabric-1.21.11:build
   ```

## Building All Versions

```bash
# Build everything
./gradlew build

# Build specific module
./gradlew :fabric-1.21.9:build

# Build all Fabric modules
./gradlew :fabric-1.14:build :fabric-1.17:build :fabric-1.19:build :fabric-1.20:build :fabric-1.21:build :fabric-1.21.5:build :fabric-1.21.9:build
```

## Finding Latest Versions

### Fabric API
- **Modrinth**: https://modrinth.com/mod/fabric-api/versions
- **Maven**: https://maven.fabricmc.net/net/fabricmc/fabric-api/fabric-api/

### Fabric Loader
- **Maven**: https://maven.fabricmc.net/net/fabricmc/fabric-loader/

### Yarn Mappings
- **Maven**: https://maven.fabricmc.net/net/fabricmc/yarn/

## Why This Architecture?

### Advantages
✅ **Each MC version uses correct APIs** - No compatibility hacks
✅ **Update one version without breaking others** - Independent modules
✅ **Code reuse** - Common logic written once in `common/`
✅ **Clear separation** - Easy to maintain and understand

### Trade-offs
⚠️ **Multiple JARs to distribute** - One per MC version
⚠️ **More modules to maintain** - Each needs dependency updates
⚠️ **Larger repository** - More code duplication in platform layers

## Compatibility Notes

### Matrix4f API Change (MC 1.17)

Minecraft 1.17 introduced a **breaking change** in the rendering API:

**MC 1.14-1.16.5** (Mojang Matrix4f):
```java
import net.minecraft.util.math.Matrix4f;
Matrix4f matrix = matrices.peek().getModel();
```

**MC 1.17+** (JOML Matrix4f):
```java
import org.joml.Matrix4f;
Matrix4f matrix = matrices.peek().getPositionMatrix();
```

Our platform implementations handle this automatically:
- `fabric-1.14.4`, `fabric-1.15.2`, `fabric-1.16.5` use Mojang's Matrix4f
- `fabric-1.17.1` and newer use JOML Matrix4f

No changes needed in `common/` module - it's platform-agnostic!

### Legacy Fabric Loader Support

Older Minecraft versions (1.14.4, 1.15.2) require:
- Fabric Loader 0.11.0+ (instead of 0.14.0+)
- Different Loom versions for building
- May have limited Fabric API features

## Best Practices

1. **Keep `common/` pure** - No Minecraft imports, only interfaces
2. **Update dependencies regularly** - Security and bug fixes
3. **Test each module independently** - Don't assume they all work
4. **Document version support** - Keep README and mod metadata up to date
5. **Don't support snapshots** - Only stable Minecraft releases
6. **Be aware of API changes** - Major MC versions may require platform code updates

## Troubleshooting

### Build fails with "Could not find fabric-api"
- Check that the Fabric API version exists for your MC version
- Verify the version string matches exactly: `0.xxx.x+1.21.10`

### "Incompatible mod" error in-game
- Check `fabric.mod.json` minecraft version range
- Ensure Fabric API version matches the MC version you're running

### Multiple modules building wrong version
- Each module must have its own `build.gradle` with correct MC version
- Check `settings.gradle` includes the right modules
