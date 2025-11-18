# TODO - Future Work and Known Issues

## ✅ Completed

### Core Features
- ✅ Common module architecture with platform abstraction
- ✅ All Fabric versions implemented (1.8.9 - 1.21.4)
- ✅ Forge support for modern versions (1.19, 1.20)
- ✅ NeoForge support (1.20.5+)
- ✅ Color indicators (green/yellow outside, yellow/red inside)
- ✅ Spawner activation detection (16 block radius)
- ✅ Localization support (en_us.json)
- ✅ Comprehensive documentation (ARCHITECTURE.md, SPAWNER_MECHANICS.md)

## 🔄 In Progress / Incomplete Modules

### Forge 1.16 (MC 1.13-1.16.5)
**Status**: Structure created, implementation needed

**Required Work**:
1. Create `ForgePlatformHelper` for 1.13-1.16.5 APIs
   - Adapt to older Forge Level/BlockPos APIs
   - Handle text component differences
2. Create `ForgeRenderer` using older rendering system
   - May need BufferBuilder instead of VertexConsumer
   - Different matrix stack APIs
3. Create main mod class with older event system
   - Different event registration patterns
   - Older keybinding system
4. Add language files (en_us.json)
5. Test with multiple 1.13-1.16.5 versions

**Files to Create**:
- `forge-1.16/src/main/java/com/example/spawnersphere/platform/ForgePlatformHelper.java`
- `forge-1.16/src/main/java/com/example/spawnersphere/platform/ForgeRenderer.java`
- `forge-1.16/src/main/java/com/example/spawnersphere/SpawnerSphereMod.java`
- `forge-1.16/src/main/resources/assets/spawnersphere/lang/en_us.json`

**Estimated Effort**: 2-4 hours

### Forge 1.12 (MC 1.8.9-1.12.2)
**Status**: Structure created, implementation needed

**Required Work**:
1. Create `ForgePlatformHelper` for very old APIs
   - Handle pre-1.13 block/item system
   - Legacy text components
   - Different world/player APIs
2. Create `ForgeRenderer` using legacy OpenGL
   - Direct GL11 calls like legacy-fabric
   - No modern rendering pipeline
3. Create main mod class with old event system
   - Pre-1.13 event patterns
   - Different initialization lifecycle
4. Add language files (en_us.lang format, not JSON)
5. Test with 1.8.9, 1.10.2, 1.12.2

**Files to Create**:
- `forge-1.12/src/main/java/com/example/spawnersphere/platform/ForgePlatformHelper.java`
- `forge-1.12/src/main/java/com/example/spawnersphere/platform/ForgeRenderer.java`
- `forge-1.12/src/main/java/com/example/spawnersphere/SpawnerSphereMod.java`
- `forge-1.12/src/main/resources/assets/spawnersphere/lang/en_us.lang` (not JSON!)

**Estimated Effort**: 3-5 hours (most complex due to legacy APIs)

## 🎯 High Priority Enhancements

### 1. Configuration GUI
**Priority**: High
**Estimated Effort**: 3-4 hours

**Description**: Add a configuration GUI using Cloth Config or YACL

**Features Needed**:
- Keybinding configuration (already works in controls)
- Sphere radius adjustment (default: 16)
- Scan radius adjustment (default: 64)
- Scan interval adjustment (default: 5000ms)
- Color customization (RGB sliders)
- Rendering quality (segment count)
- Toggle distance display in action bar

**Implementation**:
- Add Cloth Config or YACL dependency
- Create config screen registration
- Bind config to ModConfig class
- Save/load from config file
- Support for both Fabric and Forge config systems

**Files to Modify/Create**:
- Add config dependency to all build.gradle files
- Create `ConfigScreen.java` for each loader
- Modify `ModConfig` to support save/load
- Add config file handling

### 2. Additional Localization
**Priority**: Medium
**Estimated Effort**: 2-3 hours

**Description**: Add translations for multiple languages

**Languages to Add**:
- Spanish (es_es.json)
- French (fr_fr.json)
- German (de_de.json)
- Russian (ru_ru.json)
- Japanese (ja_jp.json)
- Chinese Simplified (zh_cn.json)
- Portuguese (pt_br.json)

**Translatable Strings**:
- Keybinding names
- Category names
- Enable/disable messages
- Configuration GUI labels
- Error messages

**Implementation**:
- Create translation files for each module
- Use TranslatableText instead of LiteralText where applicable
- Test with different language settings

### 3. Performance Optimizations ✅ COMPLETED
**Priority**: Medium
**Status**: ✅ Implemented

**Description**: Optimize for large numbers of spawners

**Optimizations Implemented**:
1. ✅ **Spatial Indexing**
   - Chunk-based spatial indexing (16x16 chunks)
   - HashMap-based lookup for efficient nearby queries
   - Only checks spawners in relevant chunks

2. ⏳ **Frustum Culling** (Framework ready, disabled by default)
   - Infrastructure added in `FrustumCuller.java`
   - Requires platform-specific look vector support
   - Can be enabled via config when platform supports it

3. ✅ **Level of Detail (LOD)**
   - Reduces segment count for distant spheres
   - Linear interpolation based on distance
   - Simple 3-tier system: 32/24/16 segments
   - Configurable LOD distance and min/max segments

4. ✅ **Lazy Scanning**
   - Only rescans when player moves significant distance (configurable)
   - Default threshold: 16 blocks
   - Reduces unnecessary world queries

**Files Modified**:
- ✅ `SpawnerSphereCore.java` - Integrated spatial indexing and LOD
- ✅ All platform renderers - Support variable segment counts
- ✅ `ModConfig.java` - Added performance options
- ✅ `IRenderer.java` - Added segments parameter

**New Files Created**:
- ✅ `common/src/main/java/com/example/spawnersphere/common/performance/SpatialIndex.java`
- ✅ `common/src/main/java/com/example/spawnersphere/common/performance/LODCalculator.java`
- ✅ `common/src/main/java/com/example/spawnersphere/common/performance/FrustumCuller.java`

**Configuration Options Added**:
- `enableSpatialIndexing` (default: true)
- `enableFrustumCulling` (default: false - requires platform support)
- `enableLOD` (default: true)
- `lodMaxSegments` (default: 32)
- `lodMinSegments` (default: 16)
- `lodDistance` (default: 32.0)
- `movementThreshold` (default: 16.0)

## 🔧 Nice-to-Have Features

### 4. Multiple Visualization Modes
**Priority**: Low
**Estimated Effort**: 2-3 hours

**Features**:
- Box mode (render cube instead of sphere)
- Wireframe density options
- Fill transparency mode
- Rainbow color mode
- Pulse animation when active

### 5. Per-Spawner Information
**Priority**: Low
**Estimated Effort**: 3-4 hours

**Features**:
- Hover tooltip showing spawner info
- Mob type display
- Spawn rate information
- Active/inactive status indicator
- Distance to player display

### 6. Custom Color Profiles
**Priority**: Low
**Estimated Effort**: 2 hours

**Features**:
- Preset color themes
- User-defined color profiles
- Save/load color configurations
- Per-dimension color settings

### 7. Spawner Filtering
**Priority**: Low
**Estimated Effort**: 2-3 hours

**Features**:
- Filter by mob type
- Show only active spawners
- Hide spawners in specific dimensions
- Whitelist/blacklist specific spawner types

## 🐛 Known Issues

### Minor Issues

1. **Rendering Flicker on Old Versions**
   - **Status**: Documented in README
   - **Affected**: Legacy versions (1.8.9-1.13.2)
   - **Severity**: Low
   - **Fix**: May require different rendering approach

2. **Performance with 100+ Spawners**
   - **Status**: Documented in README
   - **Severity**: Low
   - **Fix**: Implement spatial indexing (see #3 above)

3. **No Integration with Mod Menu/Forge Config GUI**
   - **Status**: Known limitation
   - **Severity**: Low
   - **Fix**: Implement configuration GUI (#1 above)

### Platform-Specific Notes

**Fabric 1.14-1.16**:
- Uses Mojang's Matrix4f instead of JOML
- Slightly different vertex consumer API

**Legacy Fabric**:
- No action bar support (messages go to chat)
- Uses direct OpenGL calls
- May have rendering artifacts on some systems

**Forge**:
- Event system differs from Fabric
- Different configuration approach
- Module 1.16 and 1.12 not yet implemented

**NeoForge**:
- Very similar to modern Forge
- May need updates as NeoForge API evolves

## 📋 Testing Checklist

Before releasing any version:

- [ ] Build succeeds for all modules
- [ ] Toggle functionality works ('B' key)
- [ ] Spheres render correctly
- [ ] Colors change at 16 block boundary
- [ ] Scanning detects all nearby spawners
- [ ] No performance issues with 10-20 spawners
- [ ] Works on vanilla servers
- [ ] No console errors or warnings
- [ ] Language files are correct
- [ ] Config saves/loads properly (when implemented)

## 🚀 Release Process

1. Update version in `build.gradle`
2. Test all modules
3. Update CHANGELOG.md
4. Create git tag
5. Build all modules: `./gradlew build`
6. Upload to CurseForge/Modrinth
7. Create GitHub release with artifacts

## 📝 Version Support Matrix

| Version | Fabric | Forge | NeoForge | Status |
|---------|--------|-------|----------|--------|
| 1.8.9-1.12.2 | ✅ | ⚠️ | N/A | Forge incomplete |
| 1.13-1.16.5 | ✅ | ⚠️ | N/A | Forge incomplete |
| 1.17-1.18.2 | ✅ | ✅ | N/A | Complete |
| 1.19-1.19.4 | ✅ | ✅ | N/A | Complete |
| 1.20-1.20.4 | ✅ | ✅ | N/A | Complete |
| 1.20.5+ | N/A | N/A | ✅ | Complete |
| 1.21-1.21.4 | ✅ | N/A | ✅ | Complete |

Legend:
- ✅ Complete and tested
- ⚠️ Structure created, implementation needed
- N/A Not applicable for this loader/version

## 💡 Ideas for Future Versions

- Integration with minimap mods
- Export spawner locations to file
- Share spawner locations with team members
- Notification when entering spawner range
- Custom sounds for activation
- Particle effects at sphere boundary
- Integration with other tech mods (showing conduits, etc.)
- Data pack support for custom ranges
- Server-side option for server owners
- Statistics tracking (spawners found, etc.)

## 🤝 Contributing

If you'd like to help with any of these items:

1. Check this TODO for unclaimed items
2. Open an issue stating which item you want to work on
3. Fork the repository
4. Implement the feature following the architecture guidelines in ARCHITECTURE.md
5. Test thoroughly
6. Submit a pull request with clear description

## 📚 Additional Documentation Needed

- Developer setup guide
- Contribution guidelines (CONTRIBUTING.md)
- Code style guide
- Testing guide
- Build troubleshooting guide
