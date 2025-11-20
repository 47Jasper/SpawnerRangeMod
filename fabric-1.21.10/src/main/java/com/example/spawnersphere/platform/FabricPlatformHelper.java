package com.example.spawnersphere.platform;

import com.example.spawnersphere.common.platform.IPlatformHelper;
import net.minecraft.block.Blocks;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Fabric implementation of platform helper for MC 1.19+
 */
public class FabricPlatformHelper implements IPlatformHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger("SpawnerSphere");

    @Override
    @NotNull
    public Platform getPlatform() {
        return Platform.FABRIC;
    }

    @Override
    public boolean isSpawner(Object world, Object blockPos) {
        if (!(world instanceof World) || !(blockPos instanceof BlockPos)) {
            LOGGER.warn("isSpawner called with invalid types: world={}, blockPos={}",
                world != null ? world.getClass().getName() : "null",
                blockPos != null ? blockPos.getClass().getName() : "null");
            return false;
        }
        return ((World) world).getBlockState((BlockPos) blockPos).isOf(Blocks.SPAWNER);
    }

    @Override
    @NotNull
    public Position getPlayerPosition(Object player) {
        if (!(player instanceof ClientPlayerEntity)) {
            LOGGER.warn("getPlayerPosition called with invalid type: player={}",
                player != null ? player.getClass().getName() : "null");
            return new Position(0, 0, 0);
        }
        Vec3d pos = ((ClientPlayerEntity) player).getPos();
        return new Position(pos.x, pos.y, pos.z);
    }

    @Override
    @NotNull
    public Object createBlockPos(int x, int y, int z) {
        return new BlockPos(x, y, z);
    }

    @Override
    @NotNull
    public Position getBlockCenter(Object blockPos) {
        if (!(blockPos instanceof BlockPos)) {
            LOGGER.warn("getBlockCenter called with invalid type: blockPos={}",
                blockPos != null ? blockPos.getClass().getName() : "null");
            return new Position(0, 0, 0);
        }
        Vec3d center = Vec3d.ofCenter((BlockPos) blockPos);
        return new Position(center.x, center.y, center.z);
    }

    @Override
    public double calculateDistance(Position pos1, Position pos2) {
        return pos1.distanceTo(pos2);
    }

    @Override
    public void sendMessage(Object player, String message, boolean actionBar) {
        if (!(player instanceof ClientPlayerEntity)) {
            LOGGER.warn("sendMessage called with invalid type: player={}",
                player != null ? player.getClass().getName() : "null");
            return;
        }
        ((ClientPlayerEntity) player).sendMessage(Text.literal(message), actionBar);
    }

    @Override
    @NotNull
    public LookVector getPlayerLookVector(Object player) {
        if (!(player instanceof ClientPlayerEntity)) {
            LOGGER.warn("getPlayerLookVector called with invalid type: player={}",
                player != null ? player.getClass().getName() : "null");
            return new LookVector(0, 0, 1);
        }
        Vec3d lookVec = ((ClientPlayerEntity) player).getRotationVec(1.0f);
        return new LookVector(lookVec.x, lookVec.y, lookVec.z);
    }
}
