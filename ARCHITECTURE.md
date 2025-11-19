# Spawner Sphere Mod - Architecture Documentation

## Overview

This document describes the architecture of the Spawner Sphere Mod, which has been refactored to support multiple mod loaders (Fabric, Forge, NeoForge) across Minecraft versions 1.8.8 through 1.21.x using a common code base.

## Architecture Goals

1. **Code Reusability**: Share core logic across all platforms and versions
2. **Maintainability**: Changes to core logic only need to be made once
3. **Extensibility**: Easy to add new platforms and versions
4. **Clean Separation**: Platform-specific code is isolated from business logic

## Module Structure

```
spawner-sphere-mod/
├── common/                    # Platform-agnostic core logic
│   └── src/main/java/com/example/spawnersphere/common/
│       ├── SpawnerSphereCore.java           # Main mod logic
│       ├── config/
│       │   └── ModConfig.java                # Configuration system
│       └── platform/
│           ├── IPlatformHelper.java          # Platform abstraction interface
│           └── IRenderer.java                 # Renderer abstraction interface
│
├── fabric-1.21/              # Fabric support for MC 1.21-1.21.x
├── fabric-1.20/              # Fabric support for MC 1.20-1.20.4
├── fabric-1.19/              # Fabric support for MC 1.19-1.19.4
├── fabric-1.17/              # Fabric support for MC 1.17-1.18.2
├── fabric-1.14/              # Fabric support for MC 1.14-1.16.5
├── legacy-fabric/            # Legacy Fabric for MC 1.8.8-1.13.2
│
├── forge-1.12/               # Forge support for MC 1.8.9-1.12.2 (planned)
├── forge-1.16/               # Forge support for MC 1.13-1.16.5 (planned)
├── forge-1.19/               # Forge support for MC 1.17-1.19.x (planned)
└── neoforge-1.20/            # NeoForge support for MC 1.20.5+ (planned)
```

## Common Module

### Core Classes

#### `SpawnerSphereCore`
The heart of the mod - contains all platform-agnostic logic:
- Spawner tracking and management
- Distance calculations
- Toggle functionality
- Periodic scanning
- Render orchestration

**Key Methods:**
```java
void toggle(Object player, Object world)
void tick(Object player, Object world)
void render(Object renderContext, Object player, Object world)
void scanForSpawners(Object player, Object world)
```

#### `IPlatformHelper`
Platform abstraction interface that each mod loader implements:
- Block type checking (`isSpawner`)
- Player position retrieval
- BlockPos creation and manipulation
- Distance calculations
- Message sending

#### `IRenderer`
Rendering abstraction interface:
- Sphere rendering with platform-specific graphics APIs
- Color configuration support

#### `ModConfig`
Configuration system:
- Sphere radius (default: 16 blocks)
- Scan radius (default: 64 blocks)
- Scan interval (default: 5000ms)
- Color configurations
- Rendering quality settings

## Platform-Specific Implementations

### Fabric (Modern: 1.19+)

**Location**: `fabric-1.21/`, `fabric-1.20/`, `fabric-1.19/`

**Classes:**
- `FabricPlatformHelper`: Implements `IPlatformHelper` using modern Fabric APIs
- `FabricRenderer`: Implements `IRenderer` using VertexConsumer API
- `SpawnerSphereMod`: Main mod class that wires everything together

**Key Features:**
- Uses `WorldRenderEvents.AFTER_TRANSLUCENT` for rendering
- Uses `ClientTickEvents.END_CLIENT_TICK` for tick handling
- Modern vertex consumer-based rendering

**Example Usage:**
```java
ModConfig config = new ModConfig();
FabricPlatformHelper platformHelper = new FabricPlatformHelper();
FabricRenderer renderer = new FabricRenderer();
SpawnerSphereCore core = new SpawnerSphereCore(platformHelper, renderer, config);
```

### Legacy Fabric (1.8.9-1.13.2)

**Location**: `legacy-fabric/`

**Classes:**
- `LegacyFabricPlatformHelper`: Implements `IPlatformHelper` for older Minecraft versions
- `LegacyFabricRenderer`: Implements `IRenderer` using OpenGL 1.x fixed pipeline
- `SpawnerSphereMod`: Main mod class with mixin-based rendering

**Key Differences:**
- Uses Mixin for rendering hooks instead of Fabric API events
- Direct OpenGL calls instead of vertex consumers
- Different block registry (`Blocks.MOB_SPAWNER` instead of `Blocks.SPAWNER`)
- No action bar support (messages go to chat)

### Forge (Planned)

**Planned Modules:**
- `forge-1.12/`: MC 1.8.9-1.12.2
- `forge-1.16/`: MC 1.13-1.16.5
- `forge-1.19/`: MC 1.17-1.19.x
- `neoforge-1.20/`: MC 1.20.5+

**Implementation Strategy:**
1. Create Forge-specific implementations of `IPlatformHelper` and `IRenderer`
2. Use Forge event bus for rendering and ticking
3. Integrate with Forge's configuration system
4. Use ForgeConfigRegistry for keybindings

## Data Flow

### Initialization
```
SpawnerSphereMod.onInitializeClient()
    ├── Create ModConfig
    ├── Create Platform-specific IPlatformHelper
    ├── Create Platform-specific IRenderer
    └── Initialize SpawnerSphereCore with above dependencies
```

### Toggle
```
User presses 'B' key
    └── SpawnerSphereMod detects keypress
        └── SpawnerSphereCore.toggle()
            ├── enabled = !enabled
            ├── if enabled: scanForSpawners()
            └── send message to player
```

### Tick Cycle
```
ClientTickEvents.END_CLIENT_TICK
    └── SpawnerSphereMod.onClientTick()
        ├── Handle toggle key if pressed
        └── SpawnerSphereCore.tick()
            └── if scan interval elapsed: scanForSpawners()
```

### Rendering
```
WorldRenderEvents.AFTER_TRANSLUCENT
    └── SpawnerSphereMod.onRenderWorld()
        ├── Setup matrices (translate to world coordinates)
        ├── Create platform-specific render context
        └── SpawnerSphereCore.render()
            └── For each tracked spawner:
                ├── Verify spawner still exists
                ├── Calculate distance to player
                ├── Determine color based on distance
                └── IRenderer.renderSphere()
                    └── Platform-specific sphere rendering
```

## Color System

### Outside Activation Range (distance > 16 blocks)
- **Color**: Green/Yellow blend
- **RGB**: (0.5, 1.0, 0.0) = (128, 255, 0)
- **Alpha**: 0.2 (51/255)
- **Visual**: Translucent lime-green

### Inside Activation Range (distance ≤ 16 blocks)
- **Color**: Yellow/Red blend
- **RGB**: (1.0, 0.5, 0.0) = (255, 128, 0)
- **Alpha**: 0.4 (102/255)
- **Visual**: More opaque orange

### Configurable
Both colors are configurable through `ModConfig` and can be changed programmatically or via config GUI (when implemented).

## Distance Calculation

The mod accurately implements Minecraft's spawner activation mechanics:

- **Activation Range**: 16 blocks (Euclidean/spherical distance)
- **Measurement**: From spawner center `(x+0.5, y+0.5, z+0.5)` to player foot position
- **Formula**: `√((Δx)² + (Δy)² + (Δz)²)`
- **Activation Threshold**: `distance ≤ 16.0`

See `SPAWNER_MECHANICS.md` for detailed information.

## Best Practices Implemented

### 1. Dependency Injection
Platform-specific implementations are injected into the core, not hard-coded.

### 2. Interface Segregation
Separate interfaces for different concerns (`IPlatformHelper`, `IRenderer`, `ModConfig`)

### 3. Single Responsibility
Each class has one clear purpose:
- `SpawnerSphereCore`: Core logic only
- `FabricPlatformHelper`: Fabric API translation only
- `FabricRenderer`: Rendering only

### 4. Open/Closed Principle
Core logic is closed for modification but open for extension via new platform implementations.

### 5. Don't Repeat Yourself (DRY)
All common logic is in one place. Platform-specific modules are thin wrappers.

## Future Enhancements

### Configuration GUI
- Integration with Mod Menu (Fabric)
- Integration with Forge Config GUI
- Use YACL or Cloth Config for advanced configuration

### Localization
- Multi-language support via i18n
- Translatable strings for all user-facing text

### Additional Features
- Customizable sphere rendering (segments, styles)
- Multiple sphere radius options
- Distance display in action bar (configurable)
- Per-spawner type filtering
- Custom keybindings

### Performance Optimizations
- Spatial indexing for large numbers of spawners
- Culling spheres outside view frustum
- LOD (Level of Detail) based on distance

## Building

### Build Single Module
```bash
./gradlew :fabric-1.21:build
```

### Build All Modules
```bash
./gradlew build
```

### Build Specific Platform
```bash
./gradlew :common:build :fabric-1.21:build :fabric-1.20:build
```

## Testing

### Manual Testing Checklist
1. Toggle mod on/off with 'B' key
2. Verify spheres appear around spawners
3. Check color changes when entering/leaving 16-block radius
4. Verify spawners are scanned correctly
5. Test with multiple spawners
6. Test at various distances
7. Verify performance with many spawners

### Automated Testing (Future)
- Unit tests for `SpawnerSphereCore` logic
- Integration tests for platform implementations
- Performance benchmarks

## Contributing

When adding a new platform or version:

1. Create a new module directory (e.g., `forge-1.19/`)
2. Add appropriate `build.gradle` with common module dependency
3. Implement `IPlatformHelper` for that platform
4. Implement `IRenderer` for that platform's graphics API
5. Create main mod class that wires dependencies to `SpawnerSphereCore`
6. Update `settings.gradle` to include new module
7. Test thoroughly

## License

MIT License - See LICENSE file for details.

## Credits

- Original mod concept: For Minecraft community
- Refactored architecture: Best practices implementation
- Platform support: Fabric, Forge, and NeoForge teams
