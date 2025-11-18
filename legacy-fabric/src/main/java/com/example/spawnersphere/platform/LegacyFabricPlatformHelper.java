package com.example.spawnersphere.platform;

import com.example.spawnersphere.common.platform.IPlatformHelper;
import net.minecraft.block.Block;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.init.Blocks;
import net.minecraft.text.LiteralText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

/**
 * Legacy Fabric implementation for MC 1.8.9-1.13.2
 */
public class LegacyFabricPlatformHelper implements IPlatformHelper {

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
        Block block = ((World) world).getBlockState((BlockPos) blockPos).getBlock();
        return block == Blocks.MOB_SPAWNER;
    }

    @Override
    @NotNull
    public Position getPlayerPosition(Object player) {
        if (!(player instanceof ClientPlayerEntity)) {
            return new Position(0, 0, 0);
        }
        ClientPlayerEntity p = (ClientPlayerEntity) player;
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
        if (!(player instanceof ClientPlayerEntity)) {
            return;
        }
        // Legacy versions don't have action bar, always use chat
        ((ClientPlayerEntity) player).addChatMessage(new LiteralText(message));
    }

    @Override
    @NotNull
    public LookVector getPlayerLookVector(Object player) {
        if (!(player instanceof EntityPlayer)) {
            return new LookVector(0, 0, 1);
        }
        EntityPlayer p = (EntityPlayer) player;
        Vec3d lookVec = p.getLook(1.0f);
        return new LookVector(lookVec.xCoord, lookVec.yCoord, lookVec.zCoord);
    }
}
