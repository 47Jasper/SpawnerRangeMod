# TODO - Outstanding Work

## Current Status

**Project**: Production-ready with 14 modules (100% version coverage: MC 1.8.9 → 1.21.10)
**Tests**: 67 unit tests, ~100% core logic coverage
**Config**: All 14 modules have working configuration

## Outstanding Tasks

### 1. Localization (Medium Priority)
Add translations for international users.

**Languages Needed**:
- Spanish (es_es.json)
- French (fr_fr.json)
- German (de_de.json)
- Russian (ru_ru.json)
- Japanese (ja_jp.json)
- Chinese Simplified (zh_cn.json)
- Portuguese (pt_br.json)

**Strings to Translate**:
- Keybinding names
- Category names
- Toggle messages
- Config GUI labels

**Effort**: 2-3 hours

### 2. Additional Visualization Modes (Low Priority)
Expand visual options for users.

**Features**:
- Box mode (cube instead of sphere)
- Wireframe density options
- Fill transparency mode
- Rainbow color mode
- Pulse animation when active

**Effort**: 2-3 hours

### 3. Per-Spawner Information (Low Priority)
Show detailed spawner info.

**Features**:
- Hover tooltip with spawner info
- Mob type display
- Spawn rate information
- Active/inactive status
- Distance to player

**Effort**: 3-4 hours

### 4. Custom Color Profiles (Low Priority)
Allow users to save/load color schemes.

**Features**:
- Preset color themes
- User-defined profiles
- Save/load configurations
- Per-dimension color settings

**Effort**: 2 hours

### 5. Spawner Filtering (Low Priority)
Filter which spawners to display.

**Features**:
- Filter by mob type
- Show only active spawners
- Hide in specific dimensions
- Whitelist/blacklist

**Effort**: 2-3 hours

### 6. Integration Testing
Add Minecraft integration tests using Fabric @GameTest (MC 1.21.5+).

**Tests Needed**:
- In-game spawner detection
- Sphere rendering verification
- Config persistence
- Performance benchmarks

**Effort**: 4-5 hours

### 7. CI/CD Pipeline
Set up automated testing and building.

**Setup**:
- GitHub Actions workflow
- Automated test execution
- Multi-version builds
- Artifact uploads

**Effort**: 2-3 hours

## Future Ideas

- Integration with minimap mods
- Export spawner locations to file
- Share locations with team members
- Notification when entering range
- Custom sounds for activation
- Particle effects at boundaries
- Integration with other tech mods
- Data pack support for custom ranges
- Statistics tracking

## Release Checklist

Before releasing:
- [ ] All modules build successfully
- [ ] Toggle functionality works
- [ ] Spheres render correctly
- [ ] Colors change at 16-block boundary
- [ ] Scanning detects all spawners
- [ ] No performance issues with 10+ spawners
- [ ] Works on vanilla servers
- [ ] No console errors
- [ ] Config saves/loads correctly

## Release Process

1. Update version in `build.gradle`
2. Test all modules
3. Update `PATCH_NOTES.md`
4. Create git tag (e.g., `v1.1.0`)
5. Build: `./gradlew build`
6. Upload to CurseForge/Modrinth
7. Create GitHub release

## Contributing

To contribute:
1. Check this TODO for unclaimed tasks
2. Open issue stating which task you're working on
3. Fork repository
4. Follow `ARCHITECTURE.md` guidelines
5. Test thoroughly
6. Submit PR with clear description
