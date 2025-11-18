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

This mod supports a wide range of Minecraft versions through different modules:

- **Legacy Fabric** (1.8.9 - 1.13.2): `legacy-fabric/` module
- **Fabric 1.14 - 1.16.5**: `fabric-1.14/` module  
- **Fabric 1.17 - 1.18.2**: `fabric-1.17/` module
- **Fabric 1.19 - 1.19.4**: `fabric-1.19/` module
- **Fabric 1.20 - 1.20.4**: `fabric-1.20/` module
- **Fabric 1.21 - 1.21.4**: `fabric-1.21/` module

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

For Minecraft 1.21.x:
```bash
./gradlew :fabric-1.21:build
```

For Minecraft 1.20.x:
```bash
./gradlew :fabric-1.20:build
```

For Legacy versions (1.8.9-1.13.2):
```bash
./gradlew :legacy-fabric:build
```

3. The built JAR file will be in the respective module's `build/libs/` directory.

### Build All Versions

To build all supported versions at once:
```bash
./gradlew build
```

## Installation

1. Install the appropriate mod loader for your Minecraft version:
   - For 1.14+: Install [Fabric Loader](https://fabricmc.net/use/)
   - For 1.8.9-1.13.2: Install [Legacy Fabric](https://legacyfabric.net/)

2. Install Fabric API:
   - For 1.14+: Download [Fabric API](https://modrinth.com/mod/fabric-api) for your version
   - For 1.8.9-1.13.2: Download [Legacy Fabric API](https://github.com/Legacy-Fabric/fabric/releases)

3. Place both the mod JAR and Fabric API JAR in your `.minecraft/mods/` folder

4. Launch Minecraft with the Fabric profile

## Usage

- **Toggle Display**: Press 'B' key (default) to enable/disable the sphere display
- **Automatic Scanning**: The mod automatically scans for spawners within 64 blocks
- **Color Indicators**:
  - Green wireframe: Outside spawner activation range
  - Yellow/Red wireframe: Inside spawner activation range

## Configuration

The keybind can be changed in Minecraft's Controls settings under the "Spawner Sphere" category.

## Technical Details

- **Activation Range**: Mob spawners activate when a player is within 16 blocks (Euclidean distance)
- **Scan Range**: The mod scans for spawners within 64 blocks of the player
- **Rescan Interval**: Automatic rescan every 5 seconds when enabled
- **Rendering**: Uses wireframe rendering with transparency for minimal visual obstruction

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

## Credits

Created for the Minecraft modding community. Special thanks to the Fabric and Legacy Fabric teams for their mod loader frameworks.
