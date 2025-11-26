package me.jasper.spawnersphere.common.config;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive tests for ConfigScreenFactory
 */
class ConfigScreenFactoryTest {

    @BeforeEach
    void setUp() {
        // Clear any previously registered instance
        ConfigScreenFactory.register(null);
    }

    @AfterEach
    void tearDown() {
        // Clean up after tests
        ConfigScreenFactory.register(null);
    }

    @Nested
    @DisplayName("register")
    class RegisterTests {

        @Test
        @DisplayName("should register config screen successfully")
        void shouldRegisterConfigScreenSuccessfully() {
            IConfigScreen mockScreen = new TestConfigScreen(true);

            ConfigScreenFactory.register(mockScreen);

            assertSame(mockScreen, ConfigScreenFactory.get());
        }

        @Test
        @DisplayName("should allow registering null")
        void shouldAllowRegisteringNull() {
            ConfigScreenFactory.register(new TestConfigScreen(true));
            ConfigScreenFactory.register(null);

            assertNull(ConfigScreenFactory.get());
        }

        @Test
        @DisplayName("should replace previously registered screen")
        void shouldReplacePreviousScreen() {
            IConfigScreen screen1 = new TestConfigScreen(true);
            IConfigScreen screen2 = new TestConfigScreen(false);

            ConfigScreenFactory.register(screen1);
            ConfigScreenFactory.register(screen2);

            assertSame(screen2, ConfigScreenFactory.get());
        }
    }

    @Nested
    @DisplayName("get")
    class GetTests {

        @Test
        @DisplayName("should return null when nothing registered")
        void shouldReturnNullWhenNothingRegistered() {
            assertNull(ConfigScreenFactory.get());
        }

        @Test
        @DisplayName("should return registered screen")
        void shouldReturnRegisteredScreen() {
            IConfigScreen screen = new TestConfigScreen(true);
            ConfigScreenFactory.register(screen);

            IConfigScreen result = ConfigScreenFactory.get();

            assertSame(screen, result);
        }
    }

    @Nested
    @DisplayName("isAvailable")
    class IsAvailableTests {

        @Test
        @DisplayName("should return false when nothing registered")
        void shouldReturnFalseWhenNothingRegistered() {
            assertFalse(ConfigScreenFactory.isAvailable());
        }

        @Test
        @DisplayName("should return false when registered screen is not available")
        void shouldReturnFalseWhenScreenNotAvailable() {
            ConfigScreenFactory.register(new TestConfigScreen(false));

            assertFalse(ConfigScreenFactory.isAvailable());
        }

        @Test
        @DisplayName("should return true when registered screen is available")
        void shouldReturnTrueWhenScreenAvailable() {
            ConfigScreenFactory.register(new TestConfigScreen(true));

            assertTrue(ConfigScreenFactory.isAvailable());
        }

        @Test
        @DisplayName("should return false after deregistering")
        void shouldReturnFalseAfterDeregistering() {
            ConfigScreenFactory.register(new TestConfigScreen(true));
            ConfigScreenFactory.register(null);

            assertFalse(ConfigScreenFactory.isAvailable());
        }
    }

    /**
     * Test implementation of IConfigScreen
     */
    private static class TestConfigScreen implements IConfigScreen {
        private final boolean available;

        TestConfigScreen(boolean available) {
            this.available = available;
        }

        @Override
        public Object createConfigScreen(Object parent) {
            return available ? new Object() : null;
        }

        @Override
        public boolean isAvailable() {
            return available;
        }
    }
}
