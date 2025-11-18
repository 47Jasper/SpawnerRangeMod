package com.example.spawnersphere.platform;

import com.example.spawnersphere.common.platform.IRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexBuffer;
import net.minecraft.client.render.VertexFormats;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;

/**
 * Legacy Fabric renderer for MC 1.8.9-1.13.2 using OpenGL directly
 */
public class LegacyFabricRenderer implements IRenderer {

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
        VertexBuffer buffer = tessellator.getBuffer();

        int segments = DEFAULT_SEGMENTS;

        // Draw latitude circles
        GL11.glColor4f(color.red, color.green, color.blue, color.alpha);
        for (int lat = -8; lat <= 8; lat++) {
            if (lat == 0) continue;
            float latAngle = (float) (lat * Math.PI / 16);
            float circleRadius = radius * (float) Math.cos(latAngle);
            float circleY = radius * (float) Math.sin(latAngle);

            buffer.begin(GL11.GL_LINE_LOOP, VertexFormats.POSITION);
            for (int i = 0; i <= segments; i++) {
                float angle = (float) (2 * Math.PI * i / segments);
                float dx = circleRadius * (float) Math.cos(angle);
                float dz = circleRadius * (float) Math.sin(angle);
                buffer.vertex(x + dx, y + circleY, z + dz).next();
            }
            tessellator.draw();
        }

        // Draw equator (slightly brighter)
        GL11.glColor4f(color.red, color.green, color.blue, color.alpha * 1.5f);
        buffer.begin(GL11.GL_LINE_LOOP, VertexFormats.POSITION);
        for (int i = 0; i <= segments; i++) {
            float angle = (float) (2 * Math.PI * i / segments);
            float dx = radius * (float) Math.cos(angle);
            float dz = radius * (float) Math.sin(angle);
            buffer.vertex(x + dx, y, z + dz).next();
        }
        tessellator.draw();

        // Draw longitude circles
        GL11.glColor4f(color.red, color.green, color.blue, color.alpha);
        for (int lon = 0; lon < 8; lon++) {
            float lonAngle = (float) (lon * Math.PI / 8);

            buffer.begin(GL11.GL_LINE_LOOP, VertexFormats.POSITION);
            for (int i = 0; i <= segments; i++) {
                float angle = (float) (2 * Math.PI * i / segments);
                float dx = radius * (float) (Math.sin(angle) * Math.cos(lonAngle));
                float dy = radius * (float) Math.cos(angle);
                float dz = radius * (float) (Math.sin(angle) * Math.sin(lonAngle));
                buffer.vertex(x + dx, y + dy, z + dz).next();
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
