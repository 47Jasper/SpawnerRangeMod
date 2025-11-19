package com.example.spawnersphere.platform;

import com.example.spawnersphere.common.platform.IPlatformHelper;
import net.minecraft.block.Block;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

/**
 * Forge implementation of platform helper for MC 1.8.9-1.12.2
 * Pre-1.13 flattening APIs
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
        Block block = ((World) world).getBlockState((BlockPos) blockPos).getBlock();
        return block == Blocks.MOB_SPAWNER;
    }

    @Override
    @NotNull
    public Position getPlayerPosition(Object player) {
        if (!(player instanceof EntityPlayerSP)) {
            return new Position(0, 0, 0);
        }
        EntityPlayerSP p = (EntityPlayerSP) player;
        return new Position(p.posX, p.posY, p.posZ);
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
        if (!(player instanceof EntityPlayerSP)) {
            return;
        }
        // MC 1.12 supports action bar
        ((EntityPlayerSP) player).sendStatusMessage(new TextComponentString(message), actionBar);
    }

    @Override
    @NotNull
    public LookVector getPlayerLookVector(Object player) {
        if (!(player instanceof EntityPlayerSP)) {
            return new LookVector(0, 0, 1);
        }
        net.minecraft.util.math.Vec3d lookVec = ((EntityPlayerSP) player).getLook(1.0f);
        return new LookVector(lookVec.x, lookVec.y, lookVec.z);
    }
}
