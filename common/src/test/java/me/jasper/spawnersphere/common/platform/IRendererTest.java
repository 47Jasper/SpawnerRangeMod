package me.jasper.spawnersphere.common.platform;

import me.jasper.spawnersphere.common.config.ModConfig;
import me.jasper.spawnersphere.common.config.ModConfig.ColorConfig;
import me.jasper.spawnersphere.common.platform.IRenderer.SphereColor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive tests for IRenderer.SphereColor
 */
class IRendererTest {

    @Nested
    @DisplayName("SphereColor")
    class SphereColorTests {

        @Test
        @DisplayName("should store color components")
        void shouldStoreColorComponents() {
            SphereColor color = new SphereColor(0.5f, 0.6f, 0.7f, 0.8f);

            assertEquals(0.5f, color.red, 0.001f);
            assertEquals(0.6f, color.green, 0.001f);
            assertEquals(0.7f, color.blue, 0.001f);
            assertEquals(0.8f, color.alpha, 0.001f);
        }

        @Test
        @DisplayName("should handle zero values")
        void shouldHandleZeroValues() {
            SphereColor color = new SphereColor(0.0f, 0.0f, 0.0f, 0.0f);

            assertEquals(0.0f, color.red, 0.001f);
            assertEquals(0.0f, color.green, 0.001f);
            assertEquals(0.0f, color.blue, 0.001f);
            assertEquals(0.0f, color.alpha, 0.001f);
        }

        @Test
        @DisplayName("should handle max values")
        void shouldHandleMaxValues() {
            SphereColor color = new SphereColor(1.0f, 1.0f, 1.0f, 1.0f);

            assertEquals(1.0f, color.red, 0.001f);
            assertEquals(1.0f, color.green, 0.001f);
            assertEquals(1.0f, color.blue, 0.001f);
            assertEquals(1.0f, color.alpha, 0.001f);
        }

        @ParameterizedTest
        @CsvSource({
            "0.0, 0.0, 0.0, 0.0",
            "0.5, 0.5, 0.5, 0.5",
            "1.0, 1.0, 1.0, 1.0",
            "0.25, 0.5, 0.75, 1.0",
            "1.0, 0.0, 0.0, 0.5"
        })
        @DisplayName("should store various color values correctly")
        void shouldStoreVariousColorValues(float r, float g, float b, float a) {
            SphereColor color = new SphereColor(r, g, b, a);

            assertEquals(r, color.red, 0.001f);
            assertEquals(g, color.green, 0.001f);
            assertEquals(b, color.blue, 0.001f);
            assertEquals(a, color.alpha, 0.001f);
        }

        @Nested
        @DisplayName("outsideRange factory method")
        class OutsideRangeTests {

            @Test
            @DisplayName("should create color from ColorConfig")
            void shouldCreateColorFromColorConfig() {
                ColorConfig colorConfig = new ColorConfig(128, 255, 0, 51);

                SphereColor sphereColor = SphereColor.outsideRange(colorConfig);

                assertEquals(128 / 255.0f, sphereColor.red, 0.001f);
                assertEquals(1.0f, sphereColor.green, 0.001f);
                assertEquals(0.0f, sphereColor.blue, 0.001f);
                assertEquals(51 / 255.0f, sphereColor.alpha, 0.001f);
            }

            @Test
            @DisplayName("should handle full intensity color")
            void shouldHandleFullIntensityColor() {
                ColorConfig colorConfig = new ColorConfig(255, 255, 255, 255);

                SphereColor sphereColor = SphereColor.outsideRange(colorConfig);

                assertEquals(1.0f, sphereColor.red, 0.001f);
                assertEquals(1.0f, sphereColor.green, 0.001f);
                assertEquals(1.0f, sphereColor.blue, 0.001f);
                assertEquals(1.0f, sphereColor.alpha, 0.001f);
            }

            @Test
            @DisplayName("should handle zero intensity color")
            void shouldHandleZeroIntensityColor() {
                ColorConfig colorConfig = new ColorConfig(0, 0, 0, 0);

                SphereColor sphereColor = SphereColor.outsideRange(colorConfig);

                assertEquals(0.0f, sphereColor.red, 0.001f);
                assertEquals(0.0f, sphereColor.green, 0.001f);
                assertEquals(0.0f, sphereColor.blue, 0.001f);
                assertEquals(0.0f, sphereColor.alpha, 0.001f);
            }

            @Test
            @DisplayName("should create correct color from ModConfig defaults")
            void shouldCreateCorrectColorFromModConfigDefaults() {
                ModConfig config = new ModConfig();
                ColorConfig outsideColor = config.getOutsideRangeColor();

                SphereColor sphereColor = SphereColor.outsideRange(outsideColor);

                // Default outside color is (128, 255, 0, 51)
                assertEquals(128 / 255.0f, sphereColor.red, 0.001f);
                assertEquals(1.0f, sphereColor.green, 0.001f);
                assertEquals(0.0f, sphereColor.blue, 0.001f);
                assertEquals(51 / 255.0f, sphereColor.alpha, 0.001f);
            }
        }

        @Nested
        @DisplayName("insideRange factory method")
        class InsideRangeTests {

            @Test
            @DisplayName("should create color from ColorConfig")
            void shouldCreateColorFromColorConfig() {
                ColorConfig colorConfig = new ColorConfig(255, 128, 0, 102);

                SphereColor sphereColor = SphereColor.insideRange(colorConfig);

                assertEquals(1.0f, sphereColor.red, 0.001f);
                assertEquals(128 / 255.0f, sphereColor.green, 0.001f);
                assertEquals(0.0f, sphereColor.blue, 0.001f);
                assertEquals(102 / 255.0f, sphereColor.alpha, 0.001f);
            }

            @Test
            @DisplayName("should handle full intensity color")
            void shouldHandleFullIntensityColor() {
                ColorConfig colorConfig = new ColorConfig(255, 255, 255, 255);

                SphereColor sphereColor = SphereColor.insideRange(colorConfig);

                assertEquals(1.0f, sphereColor.red, 0.001f);
                assertEquals(1.0f, sphereColor.green, 0.001f);
                assertEquals(1.0f, sphereColor.blue, 0.001f);
                assertEquals(1.0f, sphereColor.alpha, 0.001f);
            }

            @Test
            @DisplayName("should handle zero intensity color")
            void shouldHandleZeroIntensityColor() {
                ColorConfig colorConfig = new ColorConfig(0, 0, 0, 0);

                SphereColor sphereColor = SphereColor.insideRange(colorConfig);

                assertEquals(0.0f, sphereColor.red, 0.001f);
                assertEquals(0.0f, sphereColor.green, 0.001f);
                assertEquals(0.0f, sphereColor.blue, 0.001f);
                assertEquals(0.0f, sphereColor.alpha, 0.001f);
            }

            @Test
            @DisplayName("should create correct color from ModConfig defaults")
            void shouldCreateCorrectColorFromModConfigDefaults() {
                ModConfig config = new ModConfig();
                ColorConfig insideColor = config.getInsideRangeColor();

                SphereColor sphereColor = SphereColor.insideRange(insideColor);

                // Default inside color is (255, 128, 0, 102)
                assertEquals(1.0f, sphereColor.red, 0.001f);
                assertEquals(128 / 255.0f, sphereColor.green, 0.001f);
                assertEquals(0.0f, sphereColor.blue, 0.001f);
                assertEquals(102 / 255.0f, sphereColor.alpha, 0.001f);
            }
        }

        @Nested
        @DisplayName("Factory method equivalence")
        class FactoryMethodEquivalenceTests {

            @Test
            @DisplayName("outsideRange and insideRange should produce same result for same input")
            void factoryMethodsShouldProduceSameResultForSameInput() {
                ColorConfig colorConfig = new ColorConfig(128, 128, 128, 128);

                SphereColor outsideColor = SphereColor.outsideRange(colorConfig);
                SphereColor insideColor = SphereColor.insideRange(colorConfig);

                assertEquals(outsideColor.red, insideColor.red, 0.001f);
                assertEquals(outsideColor.green, insideColor.green, 0.001f);
                assertEquals(outsideColor.blue, insideColor.blue, 0.001f);
                assertEquals(outsideColor.alpha, insideColor.alpha, 0.001f);
            }
        }
    }
}
