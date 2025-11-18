package com.example.spawnersphere.platform;

import com.example.spawnersphere.common.platform.IRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;

/**
 * Forge renderer implementation for MC 1.8.9-1.12.2
 * Uses direct OpenGL and GlStateManager
 */
public class ForgeRenderer implements IRenderer {

    private static final int DEFAULT_SEGMENTS = 32;

    @Override
    public void renderSphere(
        @NotNull Object context,
        double x,
        double y,
        double z,
        float radius,
        @NotNull SphereColor color
    ) {
        // Legacy versions don't use a context object
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        int segments = DEFAULT_SEGMENTS;

        // Setup GL state
        GlStateManager.color(color.red, color.green, color.blue, color.alpha);

        // Draw latitude circles
        for (int lat = -8; lat <= 8; lat++) {
            if (lat == 0) continue;
            float latAngle = (float) (lat * Math.PI / 16);
            float circleRadius = radius * (float) Math.cos(latAngle);
            float circleY = radius * (float) Math.sin(latAngle);

            buffer.begin(GL11.GL_LINE_LOOP, DefaultVertexFormats.POSITION);
            for (int i = 0; i <= segments; i++) {
                float angle = (float) (2 * Math.PI * i / segments);
                float dx = circleRadius * (float) Math.cos(angle);
                float dz = circleRadius * (float) Math.sin(angle);
                buffer.pos(x + dx, y + circleY, z + dz).endVertex();
            }
            tessellator.draw();
        }

        // Draw equator (slightly brighter)
        GlStateManager.color(color.red, color.green, color.blue, color.alpha * 1.5f);
        buffer.begin(GL11.GL_LINE_LOOP, DefaultVertexFormats.POSITION);
        for (int i = 0; i <= segments; i++) {
            float angle = (float) (2 * Math.PI * i / segments);
            float dx = radius * (float) Math.cos(angle);
            float dz = radius * (float) Math.sin(angle);
            buffer.pos(x + dx, y, z + dz).endVertex();
        }
        tessellator.draw();

        // Draw longitude circles
        GlStateManager.color(color.red, color.green, color.blue, color.alpha);
        for (int lon = 0; lon < 8; lon++) {
            float lonAngle = (float) (lon * Math.PI / 8);

            buffer.begin(GL11.GL_LINE_LOOP, DefaultVertexFormats.POSITION);
            for (int i = 0; i <= segments; i++) {
                float angle = (float) (2 * Math.PI * i / segments);
                float dx = radius * (float) (Math.sin(angle) * Math.cos(lonAngle));
                float dy = radius * (float) Math.cos(angle);
                float dz = radius * (float) (Math.sin(angle) * Math.sin(lonAngle));
                buffer.pos(x + dx, y + dy, z + dz).endVertex();
            }
            tessellator.draw();
        }
    }

    /**
     * Wrapper class for legacy rendering context (not needed but included for consistency)
     */
    public static class RenderContext {
        public final float partialTicks;

        public RenderContext(float partialTicks) {
            this.partialTicks = partialTicks;
        }
    }
}
