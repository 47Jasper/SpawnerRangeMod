# Testing Guide for SpawnerRangeMod

## Overview

This project includes comprehensive unit tests for the core functionality, config management, and performance features.

## Test Structure

### Common Module Tests (`common/src/test/`)

All tests are located in the common module to ensure platform-agnostic testing:

#### 1. **ModConfigTest** (`config/ModConfigTest.java`)
Tests configuration management:
- Default values validation
- Range constraints for all settings
- Boolean toggles
- Sphere radius, scan radius, scan interval
- Performance settings (spatial indexing, frustum culling, LOD)
- Rendering settings (segments, equator)

#### 2. **SpatialIndexTest** (`performance/SpatialIndexTest.java`)
Tests chunk-based spatial indexing:
- Empty index behavior
- Add and retrieve spawners
- Multiple spawners in range
- Chunk boundary handling
- Distance calculations
- Radius boundary conditions
- Y-coordinate handling (2D indexing)

#### 3. **LODCalculatorTest** (`performance/LODCalculatorTest.java`)
Tests Level of Detail calculations:
- Close distance (max segments)
- Medium distance (interpolated segments)
- Far distance (min segments)
- Custom segment ranges
- Linear interpolation behavior
- Edge cases (zero distance, exact LOD distance)

#### 4. **FrustumCullerTest** (`performance/FrustumCullerTest.java`)
Tests view frustum culling:
- Spheres directly ahead (visible)
- Spheres behind camera (not visible)
- Spheres to the side (outside FOV)
- Edge of FOV detection
- Large sphere radius handling
- Different FOV angles (narrow, wide)
- Different look directions
- Y-axis independence (2D frustum)

## Running Tests

### Prerequisites

- Java 8 or higher
- Gradle 7.0+

### Run All Tests

```bash
gradle :common:test
```

### Run Specific Test Class

```bash
gradle :common:test --tests ModConfigTest
gradle :common:test --tests SpatialIndexTest
gradle :common:test --tests LODCalculatorTest
gradle :common:test --tests FrustumCullerTest
```

### View Test Results

After running tests, view the HTML report at:
```
common/build/reports/tests/test/index.html
```

## Test Coverage

The test suite covers:

### Configuration (ModConfigTest)
- ✅ All 20+ configuration options
- ✅ Default values
- ✅ Value ranges and constraints
- ✅ Getters and setters

### Performance Features (SpatialIndexTest, LODCalculatorTest, FrustumCullerTest)
- ✅ Spatial indexing with chunk-based HashMap
- ✅ Level of Detail calculation
- ✅ View frustum culling
- ✅ Distance calculations
- ✅ Edge cases and boundary conditions

### Total Test Count
- **ModConfigTest**: 10 tests
- **SpatialIndexTest**: 10 tests
- **LODCalculatorTest**: 10 tests
- **FrustumCullerTest**: 10 tests
- **Total**: 40 comprehensive unit tests

## Testing in Minecraft

### Fabric 1.21.4+

The mod is tested to work on:
- ✅ Minecraft 1.21.4
- ✅ Minecraft 1.21.5
- ✅ Minecraft 1.21.6/7/8
- ✅ Minecraft 1.21.9/10

### Manual Testing Checklist

1. **Basic Functionality**
   - [ ] Mod loads without errors
   - [ ] Press 'B' to toggle sphere rendering
   - [ ] Spheres appear around mob spawners
   - [ ] Spheres show correct radius (default 16 blocks)

2. **Config GUI** (Fabric 1.14+, Forge 1.19+, NeoForge 1.20+)
   - [ ] Config screen opens via Mod Menu (Fabric) or Mods list (Forge)
   - [ ] All settings are visible and editable
   - [ ] Changes apply immediately

3. **Config File** (Legacy Fabric, Forge 1.12, Forge 1.16)
   - [ ] Press 'O' to view config screen (Legacy Fabric)
   - [ ] Edit config file to change settings
   - [ ] Settings load correctly on restart

4. **Performance Features**
   - [ ] Spatial indexing: Fast scanning even with many spawners
   - [ ] LOD: Distant spheres have fewer segments (if enabled)
   - [ ] Frustum culling: Spheres behind camera not rendered (if enabled)

5. **Color System**
   - [ ] Red sphere when player is outside activation range
   - [ ] Green sphere when player is inside activation range
   - [ ] Color changes instantly when crossing boundary

## Continuous Integration

To add CI testing to this project:

1. Add GitHub Actions workflow (`.github/workflows/test.yml`):
```yaml
name: Tests

on: [push, pull_request]

jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - uses: actions/setup-java@v3
        with:
          java-version: '8'
          distribution: 'temurin'
      - name: Run tests
        run: gradle :common:test
      - name: Upload test results
        uses: actions/upload-artifact@v3
        with:
          name: test-results
          path: common/build/reports/tests/
```

## Contributing

When adding new features:

1. Write tests first (TDD approach recommended)
2. Ensure all tests pass before committing
3. Aim for >80% code coverage
4. Test edge cases and error conditions
5. Add integration tests for Minecraft-specific features

## Known Limitations

- Tests are unit tests only (no Minecraft integration tests)
- Platform helpers cannot be easily unit tested (require Minecraft runtime)
- Rendering code requires manual testing in-game
- Mixin functionality requires in-game testing

## Troubleshooting

### Tests fail to compile
- Ensure Java 8+ is installed
- Check that JUnit Jupiter 5.9.3 dependencies are available
- Run `gradle clean` and try again

### Tests fail unexpectedly
- Check that you're using the correct Java version (8+)
- Ensure no conflicting dependencies
- Review test output for specific failure reasons
