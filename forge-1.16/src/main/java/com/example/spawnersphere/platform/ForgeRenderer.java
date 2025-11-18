package com.example.spawnersphere.platform;

import com.example.spawnersphere.common.platform.IRenderer;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.vector.Matrix4f;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;

/**
 * Forge renderer implementation for MC 1.13-1.16.5
 * Uses BufferBuilder/Tessellator pattern
 */
public class ForgeRenderer implements IRenderer {

    private static final int DEFAULT_SEGMENTS = 24;

    @Override
    public void renderSphere(
        @NotNull Object context,
        double x,
        double y,
        double z,
        float radius,
        @NotNull SphereColor color
    ) {
        if (!(context instanceof RenderContext)) {
            return;
        }

        RenderContext ctx = (RenderContext) context;
        MatrixStack matrixStack = ctx.matrixStack;

        Matrix4f matrix = matrixStack.getLast().getMatrix();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        // Setup GL state
        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.lineWidth(2.0f);

        int segments = DEFAULT_SEGMENTS;

        // Draw horizontal circles (latitude)
        buffer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);

        for (int lat = -6; lat <= 6; lat += 2) {
            float latAngle = (float) (lat * Math.PI / 12);
            float circleRadius = radius * (float) Math.cos(latAngle);
            float circleY = radius * (float) Math.sin(latAngle);

            for (int i = 0; i < segments; i++) {
                float angle1 = (float) (2 * Math.PI * i / segments);
                float angle2 = (float) (2 * Math.PI * (i + 1) / segments);

                float x1 = circleRadius * (float) Math.cos(angle1);
                float z1 = circleRadius * (float) Math.sin(angle1);
                float x2 = circleRadius * (float) Math.cos(angle2);
                float z2 = circleRadius * (float) Math.sin(angle2);

                buffer.pos(matrix, (float)(x + x1), (float)(y + circleY), (float)(z + z1))
                    .color(color.red, color.green, color.blue, color.alpha)
                    .endVertex();
                buffer.pos(matrix, (float)(x + x2), (float)(y + circleY), (float)(z + z2))
                    .color(color.red, color.green, color.blue, color.alpha)
                    .endVertex();
            }
        }

        // Draw vertical circles (longitude)
        for (int lon = 0; lon < 6; lon++) {
            float lonAngle = (float) (lon * Math.PI / 6);

            for (int i = 0; i < segments; i++) {
                float angle1 = (float) (2 * Math.PI * i / segments);
                float angle2 = (float) (2 * Math.PI * (i + 1) / segments);

                float x1 = radius * (float) (Math.sin(angle1) * Math.cos(lonAngle));
                float y1 = radius * (float) Math.cos(angle1);
                float z1 = radius * (float) (Math.sin(angle1) * Math.sin(lonAngle));

                float x2 = radius * (float) (Math.sin(angle2) * Math.cos(lonAngle));
                float y2 = radius * (float) Math.cos(angle2);
                float z2 = radius * (float) (Math.sin(angle2) * Math.sin(lonAngle));

                buffer.pos(matrix, (float)(x + x1), (float)(y + y1), (float)(z + z1))
                    .color(color.red, color.green, color.blue, color.alpha)
                    .endVertex();
                buffer.pos(matrix, (float)(x + x2), (float)(y + y2), (float)(z + z2))
                    .color(color.red, color.green, color.blue, color.alpha)
                    .endVertex();
            }
        }

        tessellator.draw();

        // Restore GL state
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
    }

    /**
     * Wrapper class to pass rendering context data
     */
    public static class RenderContext {
        public final MatrixStack matrixStack;

        public RenderContext(MatrixStack matrixStack) {
            this.matrixStack = matrixStack;
        }
    }
}
