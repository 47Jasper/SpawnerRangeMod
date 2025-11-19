package com.example.spawnersphere.platform;

import com.example.spawnersphere.common.platform.IPlatformHelper;
import net.minecraft.block.Blocks;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

/**
 * Forge implementation of platform helper for MC 1.13-1.16.5
 */
public class ForgePlatformHelper implements IPlatformHelper {

    @Override
    @NotNull
    public Platform getPlatform() {
        return Platform.FORGE;
    }

    @Override
    public boolean isSpawner(Object world, Object blockPos) {
        if (!(world instanceof World) || !(blockPos instanceof BlockPos)) {
            return false;
        }
        return ((World) world).getBlockState((BlockPos) blockPos).isIn(Blocks.SPAWNER);
    }

    @Override
    @NotNull
    public Position getPlayerPosition(Object player) {
        if (!(player instanceof ClientPlayerEntity)) {
            return new Position(0, 0, 0);
        }
        ClientPlayerEntity p = (ClientPlayerEntity) player;
        return new Position(p.getPosX(), p.getPosY(), p.getPosZ());
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
        BlockPos pos = (BlockPos) blockPos;
        return new Position(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
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
        ((ClientPlayerEntity) player).sendStatusMessage(new StringTextComponent(message), actionBar);
    }

    @Override
    @NotNull
    public LookVector getPlayerLookVector(Object player) {
        if (!(player instanceof ClientPlayerEntity)) {
            return new LookVector(0, 0, 1);
        }
        net.minecraft.util.math.vector.Vector3d lookVec = ((ClientPlayerEntity) player).getLookVec();
        return new LookVector(lookVec.x, lookVec.y, lookVec.z);
    }
}
