package me.jasper.spawnersphere.mixin;

import me.jasper.spawnersphere.SpawnerSphereMod;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.WorldRenderer;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin to hook into WorldRenderer for custom rendering.
 * This replaces WorldRenderEvents.AFTER_TRANSLUCENT which was removed in Fabric API for 1.21.9+.
 *
 * Updated for Minecraft 1.21.10 render method signature.
 */
@Mixin(WorldRenderer.class)
public class WorldRendererMixin {

    /**
     * Inject at the end of the render method to render our spheres after translucent rendering.
     *
     * The render method signature in 1.21.10 is:
     * render(FrameGraphBuilder, RenderTickCounter, boolean, Camera, Matrix4f, Matrix4f, Matrix4f, GpuBufferSlice, Vector4f, boolean)
     */
    @Inject(method = "render", at = @At("TAIL"))
    private void onRenderWorld(
            Object frameGraphBuilder,  // net.minecraft.client.render.FrameGraphBuilder (class_9922)
            RenderTickCounter tickCounter,
            boolean renderBlockOutline,
            Camera camera,
            Matrix4f positionMatrix,
            Matrix4f projectionMatrix,
            Matrix4f inverseViewRotationMatrix,
            Object gpuBufferSlice,  // com.mojang.blaze3d.buffers.GpuBufferSlice
            Vector4f fogColor,
            boolean bl,
            CallbackInfo ci
    ) {
        SpawnerSphereMod.onWorldRender(camera, positionMatrix, projectionMatrix, tickCounter);
    }
}
