package com.example.spawnersphere.platform;

import com.example.spawnersphere.common.platform.IPlatformHelper;
import net.minecraft.block.Blocks;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

/**
 * Fabric implementation of platform helper for MC 1.14-1.16.5
 */
public class FabricPlatformHelper implements IPlatformHelper {

    @Override
    @NotNull
    public Platform getPlatform() {
        return Platform.FABRIC;
    }

    @Override
    public boolean isSpawner(Object world, Object blockPos) {
        if (!(world instanceof World) || !(blockPos instanceof BlockPos)) {
            return false;
        }
        return ((World) world).getBlockState((BlockPos) blockPos).isOf(Blocks.SPAWNER);
    }

    @Override
    @NotNull
    public Position getPlayerPosition(Object player) {
        if (!(player instanceof ClientPlayerEntity)) {
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
            return;
        }
        ((ClientPlayerEntity) player).sendMessage(new LiteralText(message), actionBar);
    }
}
