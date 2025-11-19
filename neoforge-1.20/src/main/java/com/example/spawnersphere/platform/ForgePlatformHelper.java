package com.example.spawnersphere.platform;

import com.example.spawnersphere.common.platform.IPlatformHelper;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

/**
 * Forge implementation of platform helper for MC 1.20+
 */
public class ForgePlatformHelper implements IPlatformHelper {

    @Override
    @NotNull
    public Platform getPlatform() {
        return Platform.FORGE;
    }

    @Override
    public boolean isSpawner(Object world, Object blockPos) {
        if (!(world instanceof Level) || !(blockPos instanceof BlockPos)) {
            return false;
        }
        return ((Level) world).getBlockState((BlockPos) blockPos).is(Blocks.SPAWNER);
    }

    @Override
    @NotNull
    public Position getPlayerPosition(Object player) {
        if (!(player instanceof LocalPlayer)) {
            return new Position(0, 0, 0);
        }
        Vec3 pos = ((LocalPlayer) player).position();
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
        BlockPos pos = (BlockPos) blockPos;
        return new Position(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
    }

    @Override
    public double calculateDistance(Position pos1, Position pos2) {
        return pos1.distanceTo(pos2);
    }

    @Override
    public void sendMessage(Object player, String message, boolean actionBar) {
        if (!(player instanceof LocalPlayer)) {
            return;
        }
        ((LocalPlayer) player).displayClientMessage(Component.literal(message), actionBar);
    }

    @Override
    @NotNull
    public LookVector getPlayerLookVector(Object player) {
        if (!(player instanceof LocalPlayer)) {
            return new LookVector(0, 0, 1);
        }
        Vec3 lookVec = ((LocalPlayer) player).getViewVector(1.0f);
        return new LookVector(lookVec.x, lookVec.y, lookVec.z);
    }
}
