package me.jasper.spawnersphere.platform;

import me.jasper.spawnersphere.common.platform.IPlatformHelper;
import me.jasper.spawnersphere.common.platform.IPlatformHelper.LookVector;
import me.jasper.spawnersphere.common.platform.IPlatformHelper.Platform;
import me.jasper.spawnersphere.common.platform.IPlatformHelper.Position;
import net.minecraft.util.math.BlockPos;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive tests for FabricPlatformHelper.
 *
 * Note: Tests requiring Minecraft classes (World, ClientPlayerEntity, BlockState) are disabled
 * because Mockito cannot mock these classes - they trigger static initialization that requires
 * Minecraft's Bootstrap to be initialized, which is not available in unit tests.
 *
 * These behaviors are tested through integration tests instead.
 */
class FabricPlatformHelperTest {

    private FabricPlatformHelper helper;

    @BeforeEach
    void setUp() {
        helper = new FabricPlatformHelper();
    }

    @Nested
    @DisplayName("getPlatform")
    class GetPlatformTests {

        @Test
        @DisplayName("should return FABRIC platform")
        void shouldReturnFabricPlatform() {
            Platform platform = helper.getPlatform();
            assertEquals(Platform.FABRIC, platform);
        }

        @Test
        @DisplayName("should never return null")
        void shouldNeverReturnNull() {
            assertNotNull(helper.getPlatform());
        }
    }

    @Nested
    @DisplayName("isSpawner")
    class IsSpawnerTests {

        @Test
        @Disabled("Cannot mock Minecraft World class - requires Bootstrap initialization")
        @DisplayName("should return true for spawner block")
        void shouldReturnTrueForSpawner() {
            // This test requires mocking World which triggers Minecraft bootstrap
            // Tested via integration tests instead
        }

        @Test
        @Disabled("Cannot mock Minecraft World class - requires Bootstrap initialization")
        @DisplayName("should return false for non-spawner block")
        void shouldReturnFalseForNonSpawner() {
            // This test requires mocking World which triggers Minecraft bootstrap
            // Tested via integration tests instead
        }

        @Test
        @DisplayName("should return false for null world")
        void shouldReturnFalseForNullWorld() {
            BlockPos pos = new BlockPos(0, 64, 0);
            boolean result = helper.isSpawner(null, pos);
            assertFalse(result);
        }

        @Test
        @DisplayName("should return false for null blockPos")
        void shouldReturnFalseForNullBlockPos() {
            boolean result = helper.isSpawner("not a world", null);
            assertFalse(result);
        }

        @Test
        @DisplayName("should return false for invalid world type")
        void shouldReturnFalseForInvalidWorldType() {
            BlockPos pos = new BlockPos(0, 64, 0);
            boolean result = helper.isSpawner("not a world", pos);
            assertFalse(result);
        }

        @Test
        @DisplayName("should return false for invalid blockPos type")
        void shouldReturnFalseForInvalidBlockPosType() {
            boolean result = helper.isSpawner("not a world", "not a blockpos");
            assertFalse(result);
        }

        @Test
        @DisplayName("should return false when both parameters are invalid")
        void shouldReturnFalseForBothInvalid() {
            boolean result = helper.isSpawner("invalid", "invalid");
            assertFalse(result);
        }
    }

    @Nested
    @DisplayName("getPlayerPosition")
    class GetPlayerPositionTests {

        @Test
        @Disabled("Cannot mock Minecraft ClientPlayerEntity class - requires Bootstrap initialization")
        @DisplayName("should return correct player position")
        void shouldReturnCorrectPosition() {
            // This test requires mocking ClientPlayerEntity which triggers Minecraft bootstrap
            // Tested via integration tests instead
        }

        @Test
        @DisplayName("should return origin for null player")
        void shouldReturnOriginForNullPlayer() {
            Position pos = helper.getPlayerPosition(null);

            assertEquals(0, pos.x, 0.001);
            assertEquals(0, pos.y, 0.001);
            assertEquals(0, pos.z, 0.001);
        }

        @Test
        @DisplayName("should return origin for invalid player type")
        void shouldReturnOriginForInvalidPlayerType() {
            Position pos = helper.getPlayerPosition("not a player");

            assertEquals(0, pos.x, 0.001);
            assertEquals(0, pos.y, 0.001);
            assertEquals(0, pos.z, 0.001);
        }

        @Test
        @DisplayName("should never return null")
        void shouldNeverReturnNull() {
            assertNotNull(helper.getPlayerPosition(null));
            assertNotNull(helper.getPlayerPosition("invalid"));
        }

        @Test
        @Disabled("Cannot mock Minecraft ClientPlayerEntity class - requires Bootstrap initialization")
        @DisplayName("should handle negative coordinates")
        void shouldHandleNegativeCoordinates() {
            // This test requires mocking ClientPlayerEntity which triggers Minecraft bootstrap
            // Tested via integration tests instead
        }
    }

    @Nested
    @DisplayName("createBlockPos")
    class CreateBlockPosTests {

        @Test
        @DisplayName("should create BlockPos with correct coordinates")
        void shouldCreateBlockPosWithCorrectCoordinates() {
            Object result = helper.createBlockPos(10, 64, -20);

            assertInstanceOf(BlockPos.class, result);
            BlockPos pos = (BlockPos) result;
            assertEquals(10, pos.getX());
            assertEquals(64, pos.getY());
            assertEquals(-20, pos.getZ());
        }

        @Test
        @DisplayName("should handle zero coordinates")
        void shouldHandleZeroCoordinates() {
            Object result = helper.createBlockPos(0, 0, 0);

            assertInstanceOf(BlockPos.class, result);
            BlockPos pos = (BlockPos) result;
            assertEquals(0, pos.getX());
            assertEquals(0, pos.getY());
            assertEquals(0, pos.getZ());
        }

        @Test
        @DisplayName("should handle negative coordinates")
        void shouldHandleNegativeCoordinates() {
            Object result = helper.createBlockPos(-100, -64, -200);

            assertInstanceOf(BlockPos.class, result);
            BlockPos pos = (BlockPos) result;
            assertEquals(-100, pos.getX());
            assertEquals(-64, pos.getY());
            assertEquals(-200, pos.getZ());
        }

        @Test
        @DisplayName("should handle large coordinates")
        void shouldHandleLargeCoordinates() {
            Object result = helper.createBlockPos(Integer.MAX_VALUE, 0, Integer.MIN_VALUE);

            assertInstanceOf(BlockPos.class, result);
            BlockPos pos = (BlockPos) result;
            assertEquals(Integer.MAX_VALUE, pos.getX());
            assertEquals(0, pos.getY());
            assertEquals(Integer.MIN_VALUE, pos.getZ());
        }

        @Test
        @DisplayName("should never return null")
        void shouldNeverReturnNull() {
            assertNotNull(helper.createBlockPos(0, 0, 0));
        }
    }

    @Nested
    @DisplayName("getBlockCenter")
    class GetBlockCenterTests {

        @Test
        @DisplayName("should return center of block at origin")
        void shouldReturnCenterAtOrigin() {
            BlockPos pos = new BlockPos(0, 0, 0);

            Position center = helper.getBlockCenter(pos);

            assertEquals(0.5, center.x, 0.001);
            assertEquals(0.5, center.y, 0.001);
            assertEquals(0.5, center.z, 0.001);
        }

        @Test
        @DisplayName("should return center of block at positive coordinates")
        void shouldReturnCenterAtPositiveCoordinates() {
            BlockPos pos = new BlockPos(10, 64, 20);

            Position center = helper.getBlockCenter(pos);

            assertEquals(10.5, center.x, 0.001);
            assertEquals(64.5, center.y, 0.001);
            assertEquals(20.5, center.z, 0.001);
        }

        @Test
        @DisplayName("should return center of block at negative coordinates")
        void shouldReturnCenterAtNegativeCoordinates() {
            BlockPos pos = new BlockPos(-10, -64, -20);

            Position center = helper.getBlockCenter(pos);

            assertEquals(-9.5, center.x, 0.001);
            assertEquals(-63.5, center.y, 0.001);
            assertEquals(-19.5, center.z, 0.001);
        }

        @Test
        @DisplayName("should return origin for null blockPos")
        void shouldReturnOriginForNull() {
            Position center = helper.getBlockCenter(null);

            assertEquals(0, center.x, 0.001);
            assertEquals(0, center.y, 0.001);
            assertEquals(0, center.z, 0.001);
        }

        @Test
        @DisplayName("should return origin for invalid type")
        void shouldReturnOriginForInvalidType() {
            Position center = helper.getBlockCenter("not a blockpos");

            assertEquals(0, center.x, 0.001);
            assertEquals(0, center.y, 0.001);
            assertEquals(0, center.z, 0.001);
        }

        @Test
        @DisplayName("should never return null")
        void shouldNeverReturnNull() {
            assertNotNull(helper.getBlockCenter(null));
            assertNotNull(helper.getBlockCenter("invalid"));
            assertNotNull(helper.getBlockCenter(new BlockPos(0, 0, 0)));
        }
    }

    @Nested
    @DisplayName("calculateDistance")
    class CalculateDistanceTests {

        @Test
        @DisplayName("should calculate zero distance for same position")
        void shouldCalculateZeroDistanceForSamePosition() {
            Position pos = new Position(10, 20, 30);

            double distance = helper.calculateDistance(pos, pos);

            assertEquals(0.0, distance, 0.001);
        }

        @Test
        @DisplayName("should calculate correct distance along X axis")
        void shouldCalculateDistanceAlongX() {
            Position pos1 = new Position(0, 0, 0);
            Position pos2 = new Position(10, 0, 0);

            double distance = helper.calculateDistance(pos1, pos2);

            assertEquals(10.0, distance, 0.001);
        }

        @Test
        @DisplayName("should calculate correct distance along Y axis")
        void shouldCalculateDistanceAlongY() {
            Position pos1 = new Position(0, 0, 0);
            Position pos2 = new Position(0, 10, 0);

            double distance = helper.calculateDistance(pos1, pos2);

            assertEquals(10.0, distance, 0.001);
        }

        @Test
        @DisplayName("should calculate correct distance along Z axis")
        void shouldCalculateDistanceAlongZ() {
            Position pos1 = new Position(0, 0, 0);
            Position pos2 = new Position(0, 0, 10);

            double distance = helper.calculateDistance(pos1, pos2);

            assertEquals(10.0, distance, 0.001);
        }

        @Test
        @DisplayName("should calculate correct 3D diagonal distance")
        void shouldCalculate3DDiagonalDistance() {
            Position pos1 = new Position(0, 0, 0);
            Position pos2 = new Position(3, 4, 0);

            double distance = helper.calculateDistance(pos1, pos2);

            assertEquals(5.0, distance, 0.001);
        }

        @Test
        @DisplayName("should calculate distance with negative coordinates")
        void shouldCalculateDistanceWithNegativeCoordinates() {
            Position pos1 = new Position(-5, -5, -5);
            Position pos2 = new Position(5, 5, 5);

            double distance = helper.calculateDistance(pos1, pos2);

            assertEquals(Math.sqrt(300), distance, 0.001);
        }

        @Test
        @DisplayName("should be commutative")
        void shouldBeCommutative() {
            Position pos1 = new Position(10, 20, 30);
            Position pos2 = new Position(40, 50, 60);

            double distance1 = helper.calculateDistance(pos1, pos2);
            double distance2 = helper.calculateDistance(pos2, pos1);

            assertEquals(distance1, distance2, 0.001);
        }
    }

    @Nested
    @DisplayName("sendMessage")
    class SendMessageTests {

        @Test
        @Disabled("Cannot mock Minecraft ClientPlayerEntity class - requires Bootstrap initialization")
        @DisplayName("should send message to player action bar")
        void shouldSendMessageToActionBar() {
            // This test requires mocking ClientPlayerEntity which triggers Minecraft bootstrap
            // Tested via integration tests instead
        }

        @Test
        @Disabled("Cannot mock Minecraft ClientPlayerEntity class - requires Bootstrap initialization")
        @DisplayName("should send message to player chat")
        void shouldSendMessageToChat() {
            // This test requires mocking ClientPlayerEntity which triggers Minecraft bootstrap
            // Tested via integration tests instead
        }

        @Test
        @DisplayName("should handle null player gracefully")
        void shouldHandleNullPlayer() {
            assertDoesNotThrow(() -> helper.sendMessage(null, "Test", true));
        }

        @Test
        @DisplayName("should handle invalid player type gracefully")
        void shouldHandleInvalidPlayerType() {
            assertDoesNotThrow(() -> helper.sendMessage("not a player", "Test", true));
        }

        @Test
        @Disabled("Cannot mock Minecraft ClientPlayerEntity class - requires Bootstrap initialization")
        @DisplayName("should handle empty message")
        void shouldHandleEmptyMessage() {
            // This test requires mocking ClientPlayerEntity which triggers Minecraft bootstrap
            // Tested via integration tests instead
        }
    }

    @Nested
    @DisplayName("getPlayerLookVector")
    class GetPlayerLookVectorTests {

        @Test
        @Disabled("Cannot mock Minecraft ClientPlayerEntity class - requires Bootstrap initialization")
        @DisplayName("should return correct look vector")
        void shouldReturnCorrectLookVector() {
            // This test requires mocking ClientPlayerEntity which triggers Minecraft bootstrap
            // Tested via integration tests instead
        }

        @Test
        @Disabled("Cannot mock Minecraft ClientPlayerEntity class - requires Bootstrap initialization")
        @DisplayName("should normalize look vector")
        void shouldNormalizeLookVector() {
            // This test requires mocking ClientPlayerEntity which triggers Minecraft bootstrap
            // Tested via integration tests instead
        }

        @Test
        @DisplayName("should return default vector for null player")
        void shouldReturnDefaultForNullPlayer() {
            LookVector result = helper.getPlayerLookVector(null);

            assertEquals(0.0, result.x, 0.001);
            assertEquals(0.0, result.y, 0.001);
            assertEquals(1.0, result.z, 0.001);
        }

        @Test
        @DisplayName("should return default vector for invalid player type")
        void shouldReturnDefaultForInvalidType() {
            LookVector result = helper.getPlayerLookVector("not a player");

            assertEquals(0.0, result.x, 0.001);
            assertEquals(0.0, result.y, 0.001);
            assertEquals(1.0, result.z, 0.001);
        }

        @Test
        @DisplayName("should never return null")
        void shouldNeverReturnNull() {
            assertNotNull(helper.getPlayerLookVector(null));
            assertNotNull(helper.getPlayerLookVector("invalid"));
        }
    }
}
