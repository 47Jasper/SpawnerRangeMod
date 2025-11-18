package com.example.spawnersphere.mixin;

import com.example.spawnersphere.SpawnerSphereMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    
    @Inject(method = "renderWorld", at = @At("RETURN"))
    private void onRenderWorld(float partialTicks, long nanoTime, CallbackInfo ci) {
        SpawnerSphereMod.renderSpheres(partialTicks);
    }
}
