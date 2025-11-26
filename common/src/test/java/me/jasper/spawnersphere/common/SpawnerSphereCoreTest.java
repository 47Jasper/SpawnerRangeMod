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
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Comprehensive tests for SpawnerSphereCore
 * Uses lenient strictness due to nested test classes with shared setup
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
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
        // Use small scan radius to avoid OOM in tests (default 32 = ~137K iterations)
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

        @Test
        @DisplayName("should do nothing when disabled")
        void shouldDoNothingWhenDisabled() {
            core.tick(mockPlayer, mockWorld);

            verify(mockPlatformHelper, never()).getPlayerPosition(any());
        }

        @Test
        @DisplayName("should get player position when enabled")
        void shouldGetPlayerPositionWhenEnabled() {
            // Setup and enable
            when(mockPlatformHelper.getPlayerPosition(any())).thenReturn(new Position(0, 64, 0));
            when(mockPlatformHelper.createBlockPos(anyInt(), anyInt(), anyInt())).thenReturn("mockBlockPos");
            core.toggle(mockPlayer, mockWorld);

            // Reset and setup for tick
            reset(mockPlatformHelper);
            when(mockPlatformHelper.getPlayerPosition(any())).thenReturn(new Position(0, 64, 0));

            core.tick(mockPlayer, mockWorld);

            verify(mockPlatformHelper).getPlayerPosition(mockPlayer);
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
            // Setup and enable (no spawners)
            when(mockPlatformHelper.getPlayerPosition(any())).thenReturn(new Position(0, 64, 0));
            when(mockPlatformHelper.createBlockPos(anyInt(), anyInt(), anyInt())).thenReturn("mockBlockPos");
            when(mockPlatformHelper.isSpawner(any(), any())).thenReturn(false);

            core.toggle(mockPlayer, mockWorld); // Enable
            core.render(mockRenderContext, mockPlayer, mockWorld);

            verify(mockRenderer, never()).renderSphere(any(), anyDouble(), anyDouble(), anyDouble(),
                anyFloat(), any(), anyInt());
        }

        @Test
        @DisplayName("should render sphere for each spawner")
        void shouldRenderSphereForEachSpawner() {
            // Setup mocks for spawner within scan radius
            Object spawnerBlockPos = "spawnerBlockPos";
            when(mockPlatformHelper.getPlayerPosition(any())).thenReturn(new Position(0, 64, 0));
            when(mockPlatformHelper.createBlockPos(anyInt(), anyInt(), anyInt()))
                .thenReturn(spawnerBlockPos);
            when(mockPlatformHelper.isSpawner(eq(mockWorld), any())).thenReturn(true);
            // Spawner at 0.5, 64.5, 0.5 - within scan radius from player at 0, 64, 0
            when(mockPlatformHelper.getBlockCenter(any())).thenReturn(new Position(0.5, 64.5, 0.5));

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
    }

    @Nested
    @DisplayName("triggerRescan")
    class TriggerRescanTests {

        @Test
        @DisplayName("should not rescan when disabled")
        void shouldNotRescanWhenDisabled() {
            core.triggerRescan(mockPlayer, mockWorld);

            verify(mockPlatformHelper, never()).getPlayerPosition(any());
        }

        @Test
        @DisplayName("should rescan when enabled")
        void shouldRescanWhenEnabled() {
            // Setup and enable
            when(mockPlatformHelper.getPlayerPosition(any())).thenReturn(new Position(0, 64, 0));
            when(mockPlatformHelper.createBlockPos(anyInt(), anyInt(), anyInt())).thenReturn("mockBlockPos");
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
            // Setup and enable
            when(mockPlatformHelper.getPlayerPosition(any())).thenReturn(new Position(0, 64, 0));
            when(mockPlatformHelper.createBlockPos(anyInt(), anyInt(), anyInt())).thenReturn("mockBlockPos");
            core.toggle(mockPlayer, mockWorld); // Enable

            Position pos = new Position(10, 64, 10); // Close to player at 0,64,0

            boolean result = core.isWithinScanRadius(pos, mockPlayer);

            assertTrue(result);
        }

        @Test
        @DisplayName("should return false when position is outside scan radius")
        void shouldReturnFalseWhenOutsideRadius() {
            // Setup and enable
            when(mockPlatformHelper.getPlayerPosition(any())).thenReturn(new Position(0, 64, 0));
            when(mockPlatformHelper.createBlockPos(anyInt(), anyInt(), anyInt())).thenReturn("mockBlockPos");
            core.toggle(mockPlayer, mockWorld); // Enable

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

        @Test
        @DisplayName("should return false initially")
        void shouldReturnFalseInitially() {
            assertFalse(core.isEnabled());
        }

        @Test
        @DisplayName("should return true after enabling")
        void shouldReturnTrueAfterEnabling() {
            when(mockPlatformHelper.getPlayerPosition(any())).thenReturn(new Position(0, 64, 0));
            when(mockPlatformHelper.createBlockPos(anyInt(), anyInt(), anyInt())).thenReturn("mockBlockPos");

            core.toggle(mockPlayer, mockWorld);

            assertTrue(core.isEnabled());
        }

        @Test
        @DisplayName("should return false after disabling")
        void shouldReturnFalseAfterDisabling() {
            when(mockPlatformHelper.getPlayerPosition(any())).thenReturn(new Position(0, 64, 0));
            when(mockPlatformHelper.createBlockPos(anyInt(), anyInt(), anyInt())).thenReturn("mockBlockPos");

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
