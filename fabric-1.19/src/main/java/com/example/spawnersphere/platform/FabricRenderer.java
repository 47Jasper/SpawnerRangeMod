package com.example.spawnersphere.platform;

import com.example.spawnersphere.common.platform.IRenderer;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

/**
 * Fabric renderer implementation for MC 1.21+
 */
public class FabricRenderer implements IRenderer {

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
        MatrixStack matrices = ctx.matrices;
        VertexConsumerProvider vertexConsumers = ctx.vertexConsumers;

        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getLines());
        Matrix4f matrix = matrices.peek().getPositionMatrix();

        int segments = DEFAULT_SEGMENTS;

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
                    .normal(0, 1, 0);
                vertexConsumer.vertex(matrix, (float)(x + x2), (float)(y + circleY), (float)(z + z2))
                    .color(color.red, color.green, color.blue, color.alpha)
                    .normal(0, 1, 0);
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
                    .normal(0, 1, 0);
                vertexConsumer.vertex(matrix, (float)(x + x2), (float)(y + y2), (float)(z + z2))
                    .color(color.red, color.green, color.blue, color.alpha)
                    .normal(0, 1, 0);
            }
        }
    }

    /**
     * Wrapper class to pass rendering context data
     */
    public static class RenderContext {
        public final MatrixStack matrices;
        public final VertexConsumerProvider vertexConsumers;

        public RenderContext(MatrixStack matrices, VertexConsumerProvider vertexConsumers) {
            this.matrices = matrices;
            this.vertexConsumers = vertexConsumers;
        }

        /**
         * Create from WorldRenderContext
         */
        public static RenderContext from(WorldRenderContext context) {
            return new RenderContext(context.matrixStack(), context.consumers());
        }
    }
}
