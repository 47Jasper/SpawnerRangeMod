package me.jasper.spawnersphere.mixin;

import me.jasper.spawnersphere.SpawnerSphereMod;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.util.profiler.Profiler;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin to hook into WorldRenderer for custom rendering.
 * This replaces WorldRenderEvents.AFTER_TRANSLUCENT which was removed in Fabric API for 1.21.9+.
 */
@Mixin(WorldRenderer.class)
public class WorldRendererMixin {

    /**
     * Inject at the end of the render method to render our spheres after translucent rendering.
     */
    @Inject(method = "render", at = @At("TAIL"))
    private void onRenderWorld(
            RenderTickCounter tickCounter,
            boolean renderBlockOutline,
            Camera camera,
            GameRenderer gameRenderer,
            Matrix4f positionMatrix,
            Matrix4f projectionMatrix,
            CallbackInfo ci
    ) {
        SpawnerSphereMod.onWorldRender(camera, positionMatrix, projectionMatrix, tickCounter);
    }
}
