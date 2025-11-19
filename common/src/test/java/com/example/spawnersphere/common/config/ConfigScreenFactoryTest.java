package com.example.spawnersphere.common.config;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for ConfigScreenFactory
 */
public class ConfigScreenFactoryTest {

    @BeforeEach
    public void setUp() {
        // Reset factory before each test
        ConfigScreenFactory.register(null);
    }

    @AfterEach
    public void tearDown() {
        // Clean up after tests
        ConfigScreenFactory.register(null);
    }

    @Test
    public void testInitiallyNull() {
        assertNull(ConfigScreenFactory.get());
        assertFalse(ConfigScreenFactory.isAvailable());
    }

    @Test
    public void testRegisterAndGet() {
        MockConfigScreen mockScreen = new MockConfigScreen(true);
        ConfigScreenFactory.register(mockScreen);

        assertEquals(mockScreen, ConfigScreenFactory.get());
    }

    @Test
    public void testIsAvailableWhenRegisteredAndAvailable() {
        MockConfigScreen mockScreen = new MockConfigScreen(true);
        ConfigScreenFactory.register(mockScreen);

        assertTrue(ConfigScreenFactory.isAvailable());
    }

    @Test
    public void testIsAvailableWhenRegisteredButNotAvailable() {
        MockConfigScreen mockScreen = new MockConfigScreen(false);
        ConfigScreenFactory.register(mockScreen);

        assertFalse(ConfigScreenFactory.isAvailable());
    }

    @Test
    public void testRegisterOverwritesPrevious() {
        MockConfigScreen firstScreen = new MockConfigScreen(true);
        MockConfigScreen secondScreen = new MockConfigScreen(true);

        ConfigScreenFactory.register(firstScreen);
        assertEquals(firstScreen, ConfigScreenFactory.get());

        ConfigScreenFactory.register(secondScreen);
        assertEquals(secondScreen, ConfigScreenFactory.get());
    }

    @Test
    public void testRegisterNull() {
        MockConfigScreen mockScreen = new MockConfigScreen(true);
        ConfigScreenFactory.register(mockScreen);

        assertNotNull(ConfigScreenFactory.get());

        ConfigScreenFactory.register(null);
        assertNull(ConfigScreenFactory.get());
        assertFalse(ConfigScreenFactory.isAvailable());
    }

    // Mock implementation
    private static class MockConfigScreen implements IConfigScreen {
        private final boolean available;
        Object lastParent;
        int createCallCount = 0;

        MockConfigScreen(boolean available) {
            this.available = available;
        }

        @Override
        public Object createConfigScreen(Object parent) {
            this.lastParent = parent;
            this.createCallCount++;
            return new Object(); // Return dummy screen
        }

        @Override
        public boolean isAvailable() {
            return available;
        }
    }
}
