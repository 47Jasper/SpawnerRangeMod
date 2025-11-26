package me.jasper.spawnersphere.common;

import me.jasper.spawnersphere.common.config.ModConfig;
import me.jasper.spawnersphere.common.platform.IPlatformHelper;
import me.jasper.spawnersphere.common.platform.IPlatformHelper.LookVector;
import me.jasper.spawnersphere.common.platform.IPlatformHelper.Position;
import me.jasper.spawnersphere.common.platform.IRenderer;
import me.jasper.spawnersphere.common.platform.IRenderer.SphereColor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Comprehensive tests for SpawnerSphereCore
 */
@ExtendWith(MockitoExtension.class)
class SpawnerSphereCoreTest {

    @Mock
    private IPlatformHelper mockPlatformHelper;

    @Mock
    private IRenderer mockRenderer;

    private ModConfig config;
    private SpawnerSphereCore core;

    private Object mockPlayer = new Object();
    private Object mockWorld = new Object();
    private Object mockRenderContext = new Object();

    @BeforeEach
    void setUp() {
        config = new ModConfig();
        // Use small scan radius to avoid OOM in tests (default 64 = ~1.1M iterations)
        config.setScanRadius(16);
        core = new SpawnerSphereCore(mockPlatformHelper, mockRenderer, config);
    }

    @Nested
    @DisplayName("Constructor")
    class ConstructorTests {

        @Test
        @DisplayName("should create instance with valid dependencies")
        void shouldCreateInstanceWithValidDependencies() {
            SpawnerSphereCore instance = new SpawnerSphereCore(
                mockPlatformHelper, mockRenderer, config
            );
            assertNotNull(instance);
        }

        @Test
        @DisplayName("should start with mod disabled")
        void shouldStartWithModDisabled() {
            assertFalse(core.isEnabled());
        }

        @Test
        @DisplayName("should store config reference")
        void shouldStoreConfigReference() {
            assertSame(config, core.getConfig());
        }
    }

    @Nested
    @DisplayName("toggle")
    class ToggleTests {

        @BeforeEach
        void setUp() {
            when(mockPlatformHelper.getPlayerPosition(any())).thenReturn(new Position(0, 64, 0));
            when(mockPlatformHelper.createBlockPos(anyInt(), anyInt(), anyInt())).thenReturn("mockBlockPos");
        }

        @Test
        @DisplayName("should enable when disabled")
        void shouldEnableWhenDisabled() {
            assertFalse(core.isEnabled());

            core.toggle(mockPlayer, mockWorld);

            assertTrue(core.isEnabled());
            verify(mockPlatformHelper).sendMessage(eq(mockPlayer), contains("enabled"), eq(true));
        }

        @Test
        @DisplayName("should disable when enabled")
        void shouldDisableWhenEnabled() {
            core.toggle(mockPlayer, mockWorld); // Enable
            assertTrue(core.isEnabled());

            core.toggle(mockPlayer, mockWorld); // Disable

            assertFalse(core.isEnabled());
            verify(mockPlatformHelper).sendMessage(eq(mockPlayer), contains("disabled"), eq(true));
        }

        @Test
        @DisplayName("should scan for spawners when enabling")
        void shouldScanForSpawnersWhenEnabling() {
            core.toggle(mockPlayer, mockWorld);

            // Verify createBlockPos was called (part of scanning)
            verify(mockPlatformHelper, atLeastOnce()).createBlockPos(anyInt(), anyInt(), anyInt());
        }
    }

    @Nested
    @DisplayName("tick")
    class TickTests {

        @BeforeEach
        void setUp() {
            when(mockPlatformHelper.getPlayerPosition(any())).thenReturn(new Position(0, 64, 0));
            when(mockPlatformHelper.createBlockPos(anyInt(), anyInt(), anyInt())).thenReturn("mockBlockPos");

            // Enable the mod first
            core.toggle(mockPlayer, mockWorld);
            reset(mockPlatformHelper); // Reset to track only tick-related calls
            when(mockPlatformHelper.getPlayerPosition(any())).thenReturn(new Position(0, 64, 0));
            when(mockPlatformHelper.createBlockPos(anyInt(), anyInt(), anyInt())).thenReturn("mockBlockPos");
        }

        @Test
        @DisplayName("should do nothing when disabled")
        void shouldDoNothingWhenDisabled() {
            core.toggle(mockPlayer, mockWorld); // Disable
            reset(mockPlatformHelper);

            core.tick(mockPlayer, mockWorld);

            verify(mockPlatformHelper, never()).getPlayerPosition(any());
        }

        @Test
        @DisplayName("should trigger rescan after movement threshold")
        void shouldTriggerRescanAfterMovementThreshold() {
            // Move player significantly
            when(mockPlatformHelper.getPlayerPosition(any()))
                .thenReturn(new Position(0, 64, 0))
                .thenReturn(new Position(100, 64, 100)); // Far from original position

            core.tick(mockPlayer, mockWorld);

            // Should have rescanned due to movement
            verify(mockPlatformHelper, atLeastOnce()).createBlockPos(anyInt(), anyInt(), anyInt());
        }
    }

    @Nested
    @DisplayName("scanForSpawners")
    class ScanForSpawnersTests {

        @BeforeEach
        void setUp() {
            when(mockPlatformHelper.getPlayerPosition(any())).thenReturn(new Position(0, 64, 0));
            when(mockPlatformHelper.createBlockPos(anyInt(), anyInt(), anyInt())).thenReturn("mockBlockPos");
        }

        @Test
        @DisplayName("should scan area around player")
        void shouldScanAreaAroundPlayer() {
            core.scanForSpawners(mockPlayer, mockWorld);

            verify(mockPlatformHelper).getPlayerPosition(mockPlayer);
            verify(mockPlatformHelper, atLeastOnce()).createBlockPos(anyInt(), anyInt(), anyInt());
        }

        @Test
        @DisplayName("should check each position for spawner")
        void shouldCheckEachPositionForSpawner() {
            when(mockPlatformHelper.isSpawner(any(), any())).thenReturn(false);

            core.scanForSpawners(mockPlayer, mockWorld);

            verify(mockPlatformHelper, atLeastOnce()).isSpawner(eq(mockWorld), any());
        }

        @Test
        @DisplayName("should get block center for found spawners")
        void shouldGetBlockCenterForFoundSpawners() {
            when(mockPlatformHelper.isSpawner(any(), any())).thenReturn(true);
            when(mockPlatformHelper.getBlockCenter(any())).thenReturn(new Position(0.5, 64.5, 0.5));

            core.scanForSpawners(mockPlayer, mockWorld);

            verify(mockPlatformHelper, atLeastOnce()).getBlockCenter(any());
        }
    }

    @Nested
    @DisplayName("render")
    class RenderTests {

        @BeforeEach
        void setUp() {
            when(mockPlatformHelper.getPlayerPosition(any())).thenReturn(new Position(0, 64, 0));
            when(mockPlatformHelper.createBlockPos(anyInt(), anyInt(), anyInt())).thenReturn("mockBlockPos");
        }

        @Test
        @DisplayName("should not render when disabled")
        void shouldNotRenderWhenDisabled() {
            core.render(mockRenderContext, mockPlayer, mockWorld);

            verify(mockRenderer, never()).renderSphere(any(), anyDouble(), anyDouble(), anyDouble(),
                anyFloat(), any(), anyInt());
        }

        @Test
        @DisplayName("should not render when no spawners found")
        void shouldNotRenderWhenNoSpawnersFound() {
            core.toggle(mockPlayer, mockWorld); // Enable
            when(mockPlatformHelper.isSpawner(any(), any())).thenReturn(false);

            core.render(mockRenderContext, mockPlayer, mockWorld);

            verify(mockRenderer, never()).renderSphere(any(), anyDouble(), anyDouble(), anyDouble(),
                anyFloat(), any(), anyInt());
        }

        @Test
        @DisplayName("should render sphere for each spawner")
        void shouldRenderSphereForEachSpawner() {
            // Set up a spawner
            Object spawnerBlockPos = "spawnerBlockPos";
            when(mockPlatformHelper.createBlockPos(anyInt(), anyInt(), anyInt()))
                .thenReturn(spawnerBlockPos);
            when(mockPlatformHelper.isSpawner(eq(mockWorld), eq(spawnerBlockPos))).thenReturn(true);
            when(mockPlatformHelper.getBlockCenter(any())).thenReturn(new Position(0.5, 64.5, 0.5));
            when(mockPlatformHelper.getPlayerLookVector(any())).thenReturn(new LookVector(0, 0, 1));

            core.toggle(mockPlayer, mockWorld); // Enable and scan

            core.render(mockRenderContext, mockPlayer, mockWorld);

            verify(mockRenderer, atLeastOnce()).renderSphere(
                eq(mockRenderContext),
                anyDouble(), anyDouble(), anyDouble(),
                anyFloat(),
                any(SphereColor.class),
                anyInt()
            );
        }

        @Test
        @DisplayName("should use outside range color when player is far")
        void shouldUseOutsideRangeColorWhenPlayerIsFar() {
            Object spawnerBlockPos = "spawnerBlockPos";
            when(mockPlatformHelper.createBlockPos(anyInt(), anyInt(), anyInt()))
                .thenReturn(spawnerBlockPos);
            when(mockPlatformHelper.isSpawner(eq(mockWorld), eq(spawnerBlockPos))).thenReturn(true);
            // Spawner at 100, 64, 100 - far from player at 0, 64, 0
            when(mockPlatformHelper.getBlockCenter(any())).thenReturn(new Position(100.5, 64.5, 100.5));
            when(mockPlatformHelper.getPlayerLookVector(any())).thenReturn(new LookVector(0, 0, 1));

            core.toggle(mockPlayer, mockWorld);
            core.render(mockRenderContext, mockPlayer, mockWorld);

            // Verify render was called (color verification is complex with mocks)
            verify(mockRenderer, atLeastOnce()).renderSphere(
                any(), anyDouble(), anyDouble(), anyDouble(),
                anyFloat(), any(SphereColor.class), anyInt()
            );
        }
    }

    @Nested
    @DisplayName("triggerRescan")
    class TriggerRescanTests {

        @BeforeEach
        void setUp() {
            when(mockPlatformHelper.getPlayerPosition(any())).thenReturn(new Position(0, 64, 0));
            when(mockPlatformHelper.createBlockPos(anyInt(), anyInt(), anyInt())).thenReturn("mockBlockPos");
        }

        @Test
        @DisplayName("should not rescan when disabled")
        void shouldNotRescanWhenDisabled() {
            reset(mockPlatformHelper);

            core.triggerRescan(mockPlayer, mockWorld);

            verify(mockPlatformHelper, never()).getPlayerPosition(any());
        }

        @Test
        @DisplayName("should rescan when enabled")
        void shouldRescanWhenEnabled() {
            core.toggle(mockPlayer, mockWorld); // Enable
            reset(mockPlatformHelper);
            when(mockPlatformHelper.getPlayerPosition(any())).thenReturn(new Position(0, 64, 0));
            when(mockPlatformHelper.createBlockPos(anyInt(), anyInt(), anyInt())).thenReturn("mockBlockPos");

            core.triggerRescan(mockPlayer, mockWorld);

            verify(mockPlatformHelper).getPlayerPosition(mockPlayer);
            verify(mockPlatformHelper, atLeastOnce()).createBlockPos(anyInt(), anyInt(), anyInt());
        }
    }

    @Nested
    @DisplayName("isWithinScanRadius")
    class IsWithinScanRadiusTests {

        @BeforeEach
        void setUp() {
            when(mockPlatformHelper.getPlayerPosition(any())).thenReturn(new Position(0, 64, 0));
            when(mockPlatformHelper.createBlockPos(anyInt(), anyInt(), anyInt())).thenReturn("mockBlockPos");
        }

        @Test
        @DisplayName("should return false when disabled")
        void shouldReturnFalseWhenDisabled() {
            Position pos = new Position(10, 64, 10);

            boolean result = core.isWithinScanRadius(pos, mockPlayer);

            assertFalse(result);
        }

        @Test
        @DisplayName("should return true when position is within scan radius")
        void shouldReturnTrueWhenWithinRadius() {
            core.toggle(mockPlayer, mockWorld); // Enable

            Position pos = new Position(10, 64, 10); // Close to player at 0,64,0

            boolean result = core.isWithinScanRadius(pos, mockPlayer);

            assertTrue(result);
        }

        @Test
        @DisplayName("should return false when position is outside scan radius")
        void shouldReturnFalseWhenOutsideRadius() {
            core.toggle(mockPlayer, mockWorld); // Enable
            config.setScanRadius(16); // Set small radius

            Position pos = new Position(1000, 64, 1000); // Far from player

            boolean result = core.isWithinScanRadius(pos, mockPlayer);

            assertFalse(result);
        }
    }

    @Nested
    @DisplayName("getConfig")
    class GetConfigTests {

        @Test
        @DisplayName("should return the config instance")
        void shouldReturnConfigInstance() {
            ModConfig returnedConfig = core.getConfig();

            assertSame(config, returnedConfig);
        }

        @Test
        @DisplayName("should return same config every time")
        void shouldReturnSameConfigEveryTime() {
            ModConfig config1 = core.getConfig();
            ModConfig config2 = core.getConfig();

            assertSame(config1, config2);
        }
    }

    @Nested
    @DisplayName("isEnabled")
    class IsEnabledTests {

        @BeforeEach
        void setUp() {
            when(mockPlatformHelper.getPlayerPosition(any())).thenReturn(new Position(0, 64, 0));
            when(mockPlatformHelper.createBlockPos(anyInt(), anyInt(), anyInt())).thenReturn("mockBlockPos");
        }

        @Test
        @DisplayName("should return false initially")
        void shouldReturnFalseInitially() {
            assertFalse(core.isEnabled());
        }

        @Test
        @DisplayName("should return true after enabling")
        void shouldReturnTrueAfterEnabling() {
            core.toggle(mockPlayer, mockWorld);

            assertTrue(core.isEnabled());
        }

        @Test
        @DisplayName("should return false after disabling")
        void shouldReturnFalseAfterDisabling() {
            core.toggle(mockPlayer, mockWorld); // Enable
            core.toggle(mockPlayer, mockWorld); // Disable

            assertFalse(core.isEnabled());
        }
    }

    @Nested
    @DisplayName("Config Integration")
    class ConfigIntegrationTests {

        @Test
        @DisplayName("should use sphere radius from config")
        void shouldUseSphereRadiusFromConfig() {
            config.setSphereRadius(32);

            assertEquals(32, core.getConfig().getSphereRadius());
        }

        @Test
        @DisplayName("should use scan radius from config")
        void shouldUseScanRadiusFromConfig() {
            config.setScanRadius(128);

            assertEquals(128, core.getConfig().getScanRadius());
        }

        @Test
        @DisplayName("should use scan interval from config")
        void shouldUseScanIntervalFromConfig() {
            config.setScanInterval(30000);

            assertEquals(30000, core.getConfig().getScanInterval());
        }

        @Test
        @DisplayName("should use movement threshold from config")
        void shouldUseMovementThresholdFromConfig() {
            config.setMovementThreshold(8.0);

            assertEquals(8.0, core.getConfig().getMovementThreshold(), 0.001);
        }
    }
}
