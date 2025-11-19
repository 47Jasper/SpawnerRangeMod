# Spawner Sphere Mod

A client-side Minecraft mod that displays a toggleable wireframe sphere around mob spawners, showing their 16-block activation range.

## Features

- **Visual Range Display**: 16-block radius sphere around mob spawners
- **Toggle Control**: Press 'B' to enable/disable (configurable keybind)
- **Auto-Scanning**: Finds spawners within 64 blocks
- **Color Indicators**: Green (outside range) / Red (inside activation range)
- **Performance Optimized**: Spatial indexing, LOD, frustum culling
- **Config GUI**: Full configuration via Mod Menu (Fabric) or Mods screen (Forge/NeoForge)
- **Client-Side Only**: Works on any server

## Version Support

**14 modules supporting MC 1.8.9 → 1.21.10**

### Fabric (8 modules)
- **legacy-fabric**: MC 1.8.9-1.13.2
- **fabric-1.14**: MC 1.14-1.16.5
- **fabric-1.17**: MC 1.17-1.18.2
- **fabric-1.19**: MC 1.19-1.19.4
- **fabric-1.20**: MC 1.20-1.20.4
- **fabric-1.21**: MC 1.21.0-1.21.4
- **fabric-1.21.5**: MC 1.21.5-1.21.8
- **fabric-1.21.9**: MC 1.21.9-1.21.10

### Forge (4 modules)
- **forge-1.12**: MC 1.8.9-1.12.2 (native config GUI)
- **forge-1.16**: MC 1.13-1.16.5 (TOML config)
- **forge-1.19**: MC 1.17-1.19.x
- **forge-1.20**: MC 1.20-1.20.4

### NeoForge (1 module)
- **neoforge-1.20**: MC 1.20.5+

### Common (1 module)
- **common**: Platform-agnostic core logic

## Installation

### Fabric
1. Install [Fabric Loader](https://fabricmc.net/use/) (or [Legacy Fabric](https://legacyfabric.net/) for 1.8.9-1.13.2)
2. Download Fabric API for your version
3. Place mod JAR and Fabric API in `.minecraft/mods/`

### Forge/NeoForge
1. Install [Forge](https://files.minecraftforge.net/) or [NeoForge](https://neoforged.net/)
2. Place mod JAR in `.minecraft/mods/`

## Building

Build specific version:
```bash
./gradlew :fabric-1.21.9:build  # MC 1.21.9-1.21.10
./gradlew :fabric-1.21.5:build  # MC 1.21.5-1.21.8
./gradlew :fabric-1.21:build    # MC 1.21.0-1.21.4
./gradlew :forge-1.20:build     # MC 1.20-1.20.4
```

Build all versions:
```bash
./gradlew build
```

Built JARs are in `<module>/build/libs/`

## Configuration

### Default Settings
- **Sphere Radius**: 16 blocks (Minecraft's spawner activation range)
- **Scan Radius**: 64 blocks
- **Scan Interval**: 60 seconds (or when moving 16+ blocks)
- **Colors**: Green (outside), Red (inside)
- **Performance**: Spatial indexing + LOD enabled

### Config GUI Access

**Fabric**: Mod Menu → Spawner Sphere → Config
**Forge/NeoForge**: Mods → Spawner Sphere → Config
**Forge 1.12**: Native Forge config GUI
**Forge 1.16**: Edit `config/spawnersphere.toml`
**Legacy Fabric**: Press 'O' key or edit `config/spawnersphere.json`

### Configurable Options
- Sphere/scan radius (1-256 blocks)
- Scan interval (1000+ms)
- Colors (RGBA)
- Performance features (spatial indexing, LOD, frustum culling)
- LOD settings (max/min segments, distance)
- Movement threshold (1-64 blocks)
- Rendering quality (segments, equator, distance display)

## Architecture

Uses common module pattern for multi-loader support:
- **common/**: Platform-agnostic core logic (SpawnerSphereCore, config, performance)
- **Platform modules**: Fabric/Forge/NeoForge-specific implementations
- **Clean abstraction**: IPlatformHelper, IRenderer interfaces

Key components:
- **SpawnerSphereCore**: Main business logic
- **SpatialIndex**: Chunk-based HashMap for efficient queries
- **LODCalculator**: Distance-based segment reduction
- **FrustumCuller**: View-based culling
- **ModConfig**: All configuration options

See `ARCHITECTURE.md` for detailed technical documentation.

## Testing

**67 comprehensive unit tests** with ~100% coverage of core logic:
- ModConfigTest (10 tests)
- SpatialIndexTest (10 tests)
- LODCalculatorTest (10 tests)
- FrustumCullerTest (10 tests)
- SpawnerSphereCoreTest (24 tests)
- ConfigScreenFactoryTest (7 tests)

Run tests:
```bash
./gradlew :common:test
```

## Performance

- **Spatial Indexing**: Chunk-based HashMap (O(1) queries)
- **Level of Detail**: 32→16 segments based on distance
- **Frustum Culling**: Skip rendering off-screen spheres
- **Lazy Scanning**: Movement/time-based triggers only

## License

MIT License - See LICENSE file

## Contributing

Pull requests welcome! For major changes, open an issue first.

1. Fork repository
2. Follow architecture in `ARCHITECTURE.md`
3. Test thoroughly (see `TODO.md` for needed work)
4. Submit PR with clear description

## Credits

Created for the Minecraft modding community.

Thanks to Fabric, Legacy Fabric, Forge, and NeoForge teams.
