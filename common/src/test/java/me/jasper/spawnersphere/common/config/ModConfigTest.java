package me.jasper.spawnersphere.common.config;

import me.jasper.spawnersphere.common.config.ModConfig.ColorConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive tests for ModConfig
 */
class ModConfigTest {

    private ModConfig config;

    @BeforeEach
    void setUp() {
        config = new ModConfig();
    }

    @Nested
    @DisplayName("Default Values")
    class DefaultValuesTests {

        @Test
        @DisplayName("should have correct default sphere radius")
        void shouldHaveCorrectDefaultSphereRadius() {
            assertEquals(16, config.getSphereRadius());
        }

        @Test
        @DisplayName("should have correct default scan radius")
        void shouldHaveCorrectDefaultScanRadius() {
            assertEquals(32, config.getScanRadius());
        }

        @Test
        @DisplayName("should have correct default scan interval")
        void shouldHaveCorrectDefaultScanInterval() {
            assertEquals(60000, config.getScanInterval());
        }

        @Test
        @DisplayName("should have correct default sphere segments")
        void shouldHaveCorrectDefaultSphereSegments() {
            assertEquals(24, config.getSphereSegments());
        }

        @Test
        @DisplayName("should have render equator enabled by default")
        void shouldHaveRenderEquatorEnabledByDefault() {
            assertTrue(config.isRenderEquator());
        }

        @Test
        @DisplayName("should have show distance disabled by default")
        void shouldHaveShowDistanceDisabledByDefault() {
            assertFalse(config.isShowDistanceInActionBar());
        }

        @Test
        @DisplayName("should have spatial indexing enabled by default")
        void shouldHaveSpatialIndexingEnabledByDefault() {
            assertTrue(config.isEnableSpatialIndexing());
        }

        @Test
        @DisplayName("should have frustum culling disabled by default")
        void shouldHaveFrustumCullingDisabledByDefault() {
            assertFalse(config.isEnableFrustumCulling());
        }

        @Test
        @DisplayName("should have LOD enabled by default")
        void shouldHaveLODEnabledByDefault() {
            assertTrue(config.isEnableLOD());
        }

        @Test
        @DisplayName("should have correct default LOD max segments")
        void shouldHaveCorrectDefaultLodMaxSegments() {
            assertEquals(32, config.getLodMaxSegments());
        }

        @Test
        @DisplayName("should have correct default LOD min segments")
        void shouldHaveCorrectDefaultLodMinSegments() {
            assertEquals(16, config.getLodMinSegments());
        }

        @Test
        @DisplayName("should have correct default LOD distance")
        void shouldHaveCorrectDefaultLodDistance() {
            assertEquals(32.0, config.getLodDistance(), 0.001);
        }

        @Test
        @DisplayName("should have correct default movement threshold")
        void shouldHaveCorrectDefaultMovementThreshold() {
            assertEquals(16.0, config.getMovementThreshold(), 0.001);
        }
    }

    @Nested
    @DisplayName("setSphereRadius")
    class SetSphereRadiusTests {

        @Test
        @DisplayName("should set valid sphere radius")
        void shouldSetValidRadius() {
            config.setSphereRadius(32);
            assertEquals(32, config.getSphereRadius());
        }

        @Test
        @DisplayName("should clamp to minimum of 1")
        void shouldClampToMinimum() {
            config.setSphereRadius(0);
            assertEquals(1, config.getSphereRadius());

            config.setSphereRadius(-10);
            assertEquals(1, config.getSphereRadius());
        }

        @Test
        @DisplayName("should clamp to maximum of 64")
        void shouldClampToMaximum() {
            config.setSphereRadius(100);
            assertEquals(64, config.getSphereRadius());
        }

        @Test
        @DisplayName("should auto-adjust scan radius when sphere radius exceeds it")
        void shouldAutoAdjustScanRadius() {
            config.setScanRadius(32);
            config.setSphereRadius(48);
            assertEquals(48, config.getScanRadius());
        }
    }

    @Nested
    @DisplayName("setScanRadius")
    class SetScanRadiusTests {

        @Test
        @DisplayName("should set valid scan radius")
        void shouldSetValidRadius() {
            config.setScanRadius(128);
            assertEquals(128, config.getScanRadius());
        }

        @Test
        @DisplayName("should clamp to minimum of 16")
        void shouldClampToMinimum() {
            config.setScanRadius(8);
            assertEquals(16, config.getScanRadius());
        }

        @Test
        @DisplayName("should clamp to maximum of 256")
        void shouldClampToMaximum() {
            config.setScanRadius(300);
            assertEquals(256, config.getScanRadius());
        }

        @Test
        @DisplayName("should auto-adjust sphere radius when below scan radius")
        void shouldAutoAdjustSphereRadius() {
            config.setSphereRadius(48);
            config.setScanRadius(32);
            assertEquals(32, config.getSphereRadius());
        }
    }

    @Nested
    @DisplayName("setScanInterval")
    class SetScanIntervalTests {

        @Test
        @DisplayName("should set valid scan interval")
        void shouldSetValidInterval() {
            config.setScanInterval(30000);
            assertEquals(30000, config.getScanInterval());
        }

        @Test
        @DisplayName("should clamp to minimum of 1000")
        void shouldClampToMinimum() {
            config.setScanInterval(500);
            assertEquals(1000, config.getScanInterval());
        }
    }

    @Nested
    @DisplayName("setSphereSegments")
    class SetSphereSegmentsTests {

        @Test
        @DisplayName("should set valid sphere segments")
        void shouldSetValidSegments() {
            config.setSphereSegments(32);
            assertEquals(32, config.getSphereSegments());
        }

        @Test
        @DisplayName("should clamp to minimum of 8")
        void shouldClampToMinimum() {
            config.setSphereSegments(4);
            assertEquals(8, config.getSphereSegments());
        }

        @Test
        @DisplayName("should clamp to maximum of 64")
        void shouldClampToMaximum() {
            config.setSphereSegments(100);
            assertEquals(64, config.getSphereSegments());
        }
    }

    @Nested
    @DisplayName("setLodMaxSegments")
    class SetLodMaxSegmentsTests {

        @Test
        @DisplayName("should set valid LOD max segments")
        void shouldSetValidSegments() {
            config.setLodMaxSegments(48);
            assertEquals(48, config.getLodMaxSegments());
        }

        @Test
        @DisplayName("should clamp to minimum of 8")
        void shouldClampToMinimum() {
            config.setLodMaxSegments(4);
            assertEquals(8, config.getLodMaxSegments());
        }

        @Test
        @DisplayName("should clamp to maximum of 64")
        void shouldClampToMaximum() {
            config.setLodMaxSegments(100);
            assertEquals(64, config.getLodMaxSegments());
        }

        @Test
        @DisplayName("should auto-adjust min segments when max drops below min")
        void shouldAutoAdjustMinSegments() {
            config.setLodMinSegments(24);
            config.setLodMaxSegments(16);
            assertEquals(16, config.getLodMinSegments());
        }
    }

    @Nested
    @DisplayName("setLodMinSegments")
    class SetLodMinSegmentsTests {

        @Test
        @DisplayName("should set valid LOD min segments")
        void shouldSetValidSegments() {
            config.setLodMinSegments(12);
            assertEquals(12, config.getLodMinSegments());
        }

        @Test
        @DisplayName("should clamp to minimum of 4")
        void shouldClampToMinimum() {
            config.setLodMinSegments(2);
            assertEquals(4, config.getLodMinSegments());
        }

        @Test
        @DisplayName("should clamp to maximum of 32")
        void shouldClampToMaximum() {
            config.setLodMinSegments(64);
            assertEquals(32, config.getLodMinSegments());
        }

        @Test
        @DisplayName("should auto-adjust max segments when min exceeds max")
        void shouldAutoAdjustMaxSegments() {
            config.setLodMaxSegments(16);
            config.setLodMinSegments(24);
            assertEquals(24, config.getLodMaxSegments());
        }
    }

    @Nested
    @DisplayName("setLodDistance")
    class SetLodDistanceTests {

        @Test
        @DisplayName("should set valid LOD distance")
        void shouldSetValidDistance() {
            config.setLodDistance(64.0);
            assertEquals(64.0, config.getLodDistance(), 0.001);
        }

        @Test
        @DisplayName("should clamp to minimum of 16")
        void shouldClampToMinimum() {
            config.setLodDistance(8.0);
            assertEquals(16.0, config.getLodDistance(), 0.001);
        }

        @Test
        @DisplayName("should clamp to maximum of 128")
        void shouldClampToMaximum() {
            config.setLodDistance(200.0);
            assertEquals(128.0, config.getLodDistance(), 0.001);
        }
    }

    @Nested
    @DisplayName("setMovementThreshold")
    class SetMovementThresholdTests {

        @Test
        @DisplayName("should set valid movement threshold")
        void shouldSetValidThreshold() {
            config.setMovementThreshold(32.0);
            assertEquals(32.0, config.getMovementThreshold(), 0.001);
        }

        @Test
        @DisplayName("should clamp to minimum of 1")
        void shouldClampToMinimum() {
            config.setMovementThreshold(0.5);
            assertEquals(1.0, config.getMovementThreshold(), 0.001);
        }

        @Test
        @DisplayName("should clamp to maximum of 64")
        void shouldClampToMaximum() {
            config.setMovementThreshold(100.0);
            assertEquals(64.0, config.getMovementThreshold(), 0.001);
        }
    }

    @Nested
    @DisplayName("Boolean Setters")
    class BooleanSettersTests {

        @Test
        @DisplayName("should set render equator")
        void shouldSetRenderEquator() {
            config.setRenderEquator(false);
            assertFalse(config.isRenderEquator());

            config.setRenderEquator(true);
            assertTrue(config.isRenderEquator());
        }

        @Test
        @DisplayName("should set show distance in action bar")
        void shouldSetShowDistanceInActionBar() {
            config.setShowDistanceInActionBar(true);
            assertTrue(config.isShowDistanceInActionBar());
        }

        @Test
        @DisplayName("should set enable spatial indexing")
        void shouldSetEnableSpatialIndexing() {
            config.setEnableSpatialIndexing(false);
            assertFalse(config.isEnableSpatialIndexing());
        }

        @Test
        @DisplayName("should set enable frustum culling")
        void shouldSetEnableFrustumCulling() {
            config.setEnableFrustumCulling(true);
            assertTrue(config.isEnableFrustumCulling());
        }

        @Test
        @DisplayName("should set enable LOD")
        void shouldSetEnableLOD() {
            config.setEnableLOD(false);
            assertFalse(config.isEnableLOD());
        }
    }

    @Nested
    @DisplayName("ColorConfig")
    class ColorConfigTests {

        @Test
        @DisplayName("should store color components")
        void shouldStoreColorComponents() {
            ColorConfig color = new ColorConfig(100, 150, 200, 128);

            assertEquals(100, color.getRed());
            assertEquals(150, color.getGreen());
            assertEquals(200, color.getBlue());
            assertEquals(128, color.getAlpha());
        }

        @Test
        @DisplayName("should clamp values below 0")
        void shouldClampBelowZero() {
            ColorConfig color = new ColorConfig(-50, -100, -10, -20);

            assertEquals(0, color.getRed());
            assertEquals(0, color.getGreen());
            assertEquals(0, color.getBlue());
            assertEquals(0, color.getAlpha());
        }

        @Test
        @DisplayName("should clamp values above 255")
        void shouldClampAbove255() {
            ColorConfig color = new ColorConfig(300, 400, 500, 600);

            assertEquals(255, color.getRed());
            assertEquals(255, color.getGreen());
            assertEquals(255, color.getBlue());
            assertEquals(255, color.getAlpha());
        }

        @Test
        @DisplayName("should convert to float correctly")
        void shouldConvertToFloatCorrectly() {
            ColorConfig color = new ColorConfig(255, 128, 0, 51);

            assertEquals(1.0f, color.getRedFloat(), 0.001f);
            assertEquals(128 / 255.0f, color.getGreenFloat(), 0.001f);
            assertEquals(0.0f, color.getBlueFloat(), 0.001f);
            assertEquals(51 / 255.0f, color.getAlphaFloat(), 0.001f);
        }

        @Test
        @DisplayName("should return default outside range color")
        void shouldReturnDefaultOutsideRangeColor() {
            ColorConfig outsideColor = config.getOutsideRangeColor();
            assertNotNull(outsideColor);
            assertEquals(128, outsideColor.getRed());
            assertEquals(255, outsideColor.getGreen());
            assertEquals(0, outsideColor.getBlue());
            assertEquals(51, outsideColor.getAlpha());
        }

        @Test
        @DisplayName("should return default inside range color")
        void shouldReturnDefaultInsideRangeColor() {
            ColorConfig insideColor = config.getInsideRangeColor();
            assertNotNull(insideColor);
            assertEquals(255, insideColor.getRed());
            assertEquals(128, insideColor.getGreen());
            assertEquals(0, insideColor.getBlue());
            assertEquals(102, insideColor.getAlpha());
        }
    }

    @Nested
    @DisplayName("Config File Name")
    class ConfigFileNameTests {

        @Test
        @DisplayName("should return correct config file name")
        void shouldReturnCorrectConfigFileName() {
            assertEquals("spawner-sphere-mod.properties", ModConfig.getDefaultConfigFileName());
        }

        @Test
        @DisplayName("should have correct CONFIG_FILE_NAME constant")
        void shouldHaveCorrectConstant() {
            assertEquals("spawner-sphere-mod.properties", ModConfig.CONFIG_FILE_NAME);
        }
    }

    @Nested
    @DisplayName("Load and Save")
    class LoadSaveTests {

        @TempDir
        Path tempDir;

        @Test
        @DisplayName("should handle load with no config file set")
        void shouldHandleLoadWithNoConfigFile() {
            assertDoesNotThrow(() -> config.load());
        }

        @Test
        @DisplayName("should handle load with non-existent file")
        void shouldHandleLoadWithNonExistentFile() {
            config.setConfigFile(new File(tempDir.toFile(), "nonexistent.properties"));
            assertDoesNotThrow(() -> config.load());
        }

        @Test
        @DisplayName("should save and load config correctly")
        void shouldSaveAndLoadCorrectly() throws IOException {
            File configFile = new File(tempDir.toFile(), "test.properties");
            config.setConfigFile(configFile);

            // Set some non-default values
            config.setSphereRadius(24);
            config.setScanRadius(128);
            config.setScanInterval(30000);
            config.setSphereSegments(32);
            config.setRenderEquator(false);
            config.setShowDistanceInActionBar(true);
            config.setEnableSpatialIndexing(false);
            config.setEnableFrustumCulling(true);
            config.setEnableLOD(false);
            config.setLodMaxSegments(48);
            config.setLodMinSegments(8);
            config.setLodDistance(64.0);
            config.setMovementThreshold(32.0);

            // Save
            config.save();

            // Create new config and load
            ModConfig loadedConfig = new ModConfig();
            loadedConfig.setConfigFile(configFile);
            loadedConfig.load();

            // Verify values
            assertEquals(24, loadedConfig.getSphereRadius());
            assertEquals(128, loadedConfig.getScanRadius());
            assertEquals(30000, loadedConfig.getScanInterval());
            assertEquals(32, loadedConfig.getSphereSegments());
            assertFalse(loadedConfig.isRenderEquator());
            assertTrue(loadedConfig.isShowDistanceInActionBar());
            assertFalse(loadedConfig.isEnableSpatialIndexing());
            assertTrue(loadedConfig.isEnableFrustumCulling());
            assertFalse(loadedConfig.isEnableLOD());
            assertEquals(48, loadedConfig.getLodMaxSegments());
            assertEquals(8, loadedConfig.getLodMinSegments());
            assertEquals(64.0, loadedConfig.getLodDistance(), 0.001);
            assertEquals(32.0, loadedConfig.getMovementThreshold(), 0.001);
        }

        @Test
        @DisplayName("should handle save with no config file set")
        void shouldHandleSaveWithNoConfigFile() {
            assertDoesNotThrow(() -> config.save());
        }

        @Test
        @DisplayName("should create parent directories on save")
        void shouldCreateParentDirectoriesOnSave() {
            File configFile = new File(tempDir.toFile(), "subdir/test.properties");
            config.setConfigFile(configFile);

            config.save();

            assertTrue(configFile.getParentFile().exists());
        }

        @Test
        @DisplayName("should handle invalid number format in config")
        void shouldHandleInvalidNumberFormat() throws IOException {
            File configFile = new File(tempDir.toFile(), "invalid.properties");

            // Write invalid properties
            Properties props = new Properties();
            props.setProperty("sphereRadius", "not_a_number");
            try (FileOutputStream fos = new FileOutputStream(configFile)) {
                props.store(fos, "Test");
            }

            config.setConfigFile(configFile);
            assertDoesNotThrow(() -> config.load());
            // Should keep default value
            assertEquals(16, config.getSphereRadius());
        }
    }
}
