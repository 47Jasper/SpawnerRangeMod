# Spawner Sphere Mod

A client-side Minecraft mod that displays a toggleable wireframe sphere around mob spawners, showing their 16-block activation range.

## Features

- **Visual Spawner Range**: Displays a 16-block radius sphere around mob spawners
- **Toggle On/Off**: Press 'B' key (configurable) to toggle the display
- **Dynamic Scanning**: Automatically scans for spawners within 64 blocks
- **Range Indicator**: Sphere changes color when you're within activation range
  - Green/Yellow: Outside activation range
  - Yellow/Red: Inside activation range (spawner active)
- **Performance Optimized**: Only renders spheres for nearby spawners
- **Client-Side Only**: Works on any server without server-side installation

## Version Support

This mod supports a wide range of Minecraft versions through different modules and mod loaders:

### Fabric
- **Legacy Fabric** (1.8.9 - 1.13.2): `legacy-fabric/` module
- **Fabric 1.14 - 1.16.5**: `fabric-1.14/` module
- **Fabric 1.17 - 1.18.2**: `fabric-1.17/` module
- **Fabric 1.19 - 1.19.4**: `fabric-1.19/` module
- **Fabric 1.20 - 1.20.4**: `fabric-1.20/` module
- **Fabric 1.21 - 1.21.4**: `fabric-1.21/` module

### Forge
- **Forge 1.12** (1.8.9 - 1.12.2): `forge-1.12/` module
- **Forge 1.16** (1.13 - 1.16.5): `forge-1.16/` module
- **Forge 1.19** (1.17 - 1.19.x): `forge-1.19/` module
- **Forge 1.20** (1.20 - 1.20.4): `forge-1.20/` module

### NeoForge
- **NeoForge 1.20** (1.20.5+): `neoforge-1.20/` module

## Building

### Prerequisites

- Java Development Kit (JDK) 17 or higher
- Git

### Build Instructions

1. Clone the repository:
```bash
git clone https://github.com/example/spawner-sphere-mod.git
cd spawner-sphere-mod
```

2. Build the specific version you need:

**Fabric Examples:**
```bash
./gradlew :fabric-1.21:build      # MC 1.21.x
./gradlew :fabric-1.20:build      # MC 1.20.x
./gradlew :legacy-fabric:build    # MC 1.8.9-1.13.2
```

**Forge Examples:**
```bash
./gradlew :forge-1.20:build       # MC 1.20-1.20.4
./gradlew :forge-1.19:build       # MC 1.17-1.19.x
./gradlew :forge-1.16:build       # MC 1.13-1.16.5
./gradlew :forge-1.12:build       # MC 1.8.9-1.12.2
```

**NeoForge Example:**
```bash
./gradlew :neoforge-1.20:build    # MC 1.20.5+
```

3. The built JAR file will be in the respective module's `build/libs/` directory.

### Build All Versions

To build all supported versions at once:
```bash
./gradlew build
```

## Installation

### For Fabric

1. Install the appropriate Fabric loader for your Minecraft version:
   - For 1.14+: Install [Fabric Loader](https://fabricmc.net/use/)
   - For 1.8.9-1.13.2: Install [Legacy Fabric](https://legacyfabric.net/)

2. Install Fabric API:
   - For 1.14+: Download [Fabric API](https://modrinth.com/mod/fabric-api) for your version
   - For 1.8.9-1.13.2: Download [Legacy Fabric API](https://github.com/Legacy-Fabric/fabric/releases)

3. Place both the mod JAR and Fabric API JAR in your `.minecraft/mods/` folder

4. Launch Minecraft with the Fabric profile

### For Forge

1. Install [Forge](https://files.minecraftforge.net/) for your Minecraft version

2. Place the mod JAR in your `.minecraft/mods/` folder

3. Launch Minecraft with the Forge profile

### For NeoForge

1. Install [NeoForge](https://neoforged.net/) for your Minecraft version (1.20.5+)

2. Place the mod JAR in your `.minecraft/mods/` folder

3. Launch Minecraft with the NeoForge profile

## Usage

- **Toggle Display**: Press 'B' key (default) to enable/disable the sphere display
- **Automatic Scanning**: The mod automatically scans for spawners within 64 blocks
- **Color Indicators**:
  - Green wireframe: Outside spawner activation range
  - Yellow/Red wireframe: Inside spawner activation range

## Configuration

### Keybindings
The toggle keybind can be changed in Minecraft's Controls settings under the "Spawner Sphere" category.

### Default Values
- **Sphere Radius**: 16 blocks (Minecraft's spawner activation range)
- **Scan Radius**: 64 blocks (how far to search for spawners)
- **Scan Interval**: 60 seconds (how often to scan for new spawners)
- **Colors**: Green/yellow when outside range, yellow/red when inside
- **Performance**: Spatial indexing and LOD enabled by default

### Configuration GUI (Optional)

The mod includes infrastructure for configuration GUI using Cloth Config:
- **Fabric**: Integrates with Mod Menu for easy configuration
- **Forge**: Uses Forge config screen system
- **See**: `CONFIG_GUI_INTEGRATION.md` for integration guide

**Available Settings**:
- Sphere and scan radius adjustment
- Scan interval and movement threshold
- Custom colors (RGB/Alpha)
- Performance optimizations (spatial indexing, LOD, frustum culling)
- Rendering quality (segment count, LOD settings)
- Distance display toggle

**Note**: Config GUI is optional. Mod works with sensible defaults if not configured.

## Technical Details

- **Activation Range**: Mob spawners activate when a player is within 16 blocks (Euclidean distance)
- **Scan Range**: The mod scans for spawners within 64 blocks of the player
- **Rescan Interval**: Automatic rescan every 60 seconds, or when player moves 16+ blocks
- **Rendering**: Uses wireframe rendering with transparency for minimal visual obstruction
- **Performance**: Chunk-based spatial indexing and level-of-detail system for efficiency

## Compatibility

- **Client-Side Only**: Works on any server, including vanilla servers
- **Multiplayer Compatible**: Each player can toggle their own display independently
- **Performance**: Minimal impact on FPS due to optimized rendering

## Known Issues

- On some older versions, the sphere may flicker slightly when moving
- Very large numbers of spawners in one area may impact performance

## License

MIT License - See LICENSE file for details

## Contributing

Pull requests are welcome! For major changes, please open an issue first to discuss what you would like to change.

## Architecture

This mod uses a common module architecture that allows it to run on multiple mod loaders (Fabric, Forge, NeoForge) with shared core logic. See [ARCHITECTURE.md](ARCHITECTURE.md) for detailed technical documentation.

## Credits

Created for the Minecraft modding community. Special thanks to:
- The Fabric and Legacy Fabric teams for their mod loader frameworks
- The Forge and NeoForge teams for their mod loader frameworks
- The Minecraft modding community for their continuous support
