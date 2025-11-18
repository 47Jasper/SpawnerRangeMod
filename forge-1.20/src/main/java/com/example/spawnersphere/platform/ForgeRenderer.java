package com.example.spawnersphere.platform;

import com.example.spawnersphere.common.platform.IRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

/**
 * Forge renderer implementation for MC 1.20+
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
        @NotNull SphereColor color,
        int segments
    ) {
        if (!(context instanceof RenderContext)) {
            return;
        }

        RenderContext ctx = (RenderContext) context;
        PoseStack poseStack = ctx.poseStack;
        MultiBufferSource bufferSource = ctx.bufferSource;

        VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.lines());
        Matrix4f matrix = poseStack.last().pose();

        // Draw horizontal circles (latitude)
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

                vertexConsumer.vertex(matrix, (float)(x + x1), (float)(y + circleY), (float)(z + z1))
                    .color(color.red, color.green, color.blue, color.alpha)
                    .normal(0, 1, 0)
                    .endVertex();
                vertexConsumer.vertex(matrix, (float)(x + x2), (float)(y + circleY), (float)(z + z2))
                    .color(color.red, color.green, color.blue, color.alpha)
                    .normal(0, 1, 0)
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

                vertexConsumer.vertex(matrix, (float)(x + x1), (float)(y + y1), (float)(z + z1))
                    .color(color.red, color.green, color.blue, color.alpha)
                    .normal(0, 1, 0)
                    .endVertex();
                vertexConsumer.vertex(matrix, (float)(x + x2), (float)(y + y2), (float)(z + z2))
                    .color(color.red, color.green, color.blue, color.alpha)
                    .normal(0, 1, 0)
                    .endVertex();
            }
        }
    }

    /**
     * Wrapper class to pass rendering context data
     */
    public static class RenderContext {
        public final PoseStack poseStack;
        public final MultiBufferSource bufferSource;

        public RenderContext(PoseStack poseStack, MultiBufferSource bufferSource) {
            this.poseStack = poseStack;
            this.bufferSource = bufferSource;
        }
    }
}
