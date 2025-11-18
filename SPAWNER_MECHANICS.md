# Minecraft Spawner Activation Mechanics

## Overview
This document details the exact mechanics of how mob spawners activate in Minecraft, which is critical for the accuracy of the Spawner Range Mod's visual indicators.

## Activation Range

### Distance Calculation
- **Activation Radius**: 16 blocks (spherical/Euclidean distance)
- **Measurement Point**: From the **center** of the spawner block
- **Spawner Center Coordinates**: `(blockX + 0.5, blockY + 0.5, blockZ + 0.5)`

### Distance Formula
The game uses Euclidean distance calculation:
```
distance = √(Δx² + Δy² + Δz²)
```

Where:
- `Δx = playerX - (spawnerX + 0.5)`
- `Δy = playerY - (spawnerY + 0.5)`
- `Δz = playerZ - (spawnerZ + 0.5)`

A spawner activates when: `distance ≤ 16.0`

## Player Position

### Critical Details
1. **Player position is measured at foot level** (not head or center of player model)
2. The position returned by `player.getPos()` in modern versions or `player.posX/Y/Z` in legacy versions represents the player's feet
3. This means a player standing exactly 15.5 blocks **below** a spawner will NOT activate it, even though their head is within range

### Examples
- Player at (0, 64, 0), Spawner at (16, 64, 0):
  - Distance = 16.0 blocks → **ACTIVATES**

- Player at (0, 64, 0), Spawner at (0, 80, 0):
  - Distance = 16.0 blocks → **ACTIVATES**

- Player at (0, 64, 0), Spawner at (11, 75, 11):
  - Distance = √(11² + 11² + 11²) = √363 ≈ 19.05 blocks → **DOES NOT ACTIVATE**

- Player at (0, 64, 0), Spawner at (8, 72, 8):
  - Distance = √(8² + 8² + 8²) = √192 ≈ 13.86 blocks → **ACTIVATES**

## Spawning Volume

Once activated, the spawner attempts to spawn mobs within:
- **Horizontal Range**: 4 blocks from spawner center (9×9 area)
- **Vertical Range**: 1 block from spawner center (3 block height)
- **Total Volume**: 9×3×9 centered on the spawner

## Implementation in This Mod

### Color Coding
The mod uses color to indicate activation status:

1. **Outside Activation Range** (player > 16 blocks away):
   - Color: Green/Yellow blend `RGB(0.5, 1.0, 0.0)`
   - Alpha: 0.2 (more transparent)
   - Meaning: Spawner is inactive

2. **Inside Activation Range** (player ≤ 16 blocks away):
   - Color: Yellow/Red blend `RGB(1.0, 0.5, 0.0)`
   - Alpha: 0.4 (more opaque)
   - Meaning: Spawner is active and spawning

### Rendering Details
- **Sphere Radius**: 16 blocks (matches activation range exactly)
- **Sphere Center**: Spawner block center at `(x + 0.5, y + 0.5, z + 0.5)`
- **Scan Radius**: 64 blocks (for performance optimization)
- **Rescan Interval**: Every 5 seconds when enabled

### Distance Check Implementation
```java
// Get player foot position
Vec3d playerPos = client.player.getPos();

// Get spawner center position
Vec3d spawnerCenter = Vec3d.ofCenter(spawnerPos);

// Calculate Euclidean distance
double distance = playerPos.distanceTo(spawnerCenter);

// Check if in range
boolean inRange = distance <= 16.0;
```

## Technical Notes

### Why 16 Blocks?
This is a hardcoded value in Minecraft's `MobSpawnerLogic` class and has been consistent across most Minecraft versions.

### Edge Cases
1. **Spectator Mode**: Players in spectator mode do not activate spawners
2. **Multiple Players**: Any non-spectator player within range activates the spawner
3. **Render Distance**: The mod only scans for spawners within 64 blocks for performance reasons

## References
- Minecraft Wiki: Monster Spawner
- Source: Minecraft Java Edition spawner mechanics
- Tested across versions 1.8.9 through 1.21.4

## Version Compatibility
This mechanic has remained consistent across all Minecraft versions supported by this mod:
- Legacy versions (1.8.9 - 1.13.2)
- Modern Fabric versions (1.14 - 1.21.4)

The distance calculation and 16-block activation range have not changed between these versions.
