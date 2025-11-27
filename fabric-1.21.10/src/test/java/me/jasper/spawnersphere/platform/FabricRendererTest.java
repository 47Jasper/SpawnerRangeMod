package me.jasper.spawnersphere.platform;

import me.jasper.spawnersphere.common.platform.IRenderer.SphereColor;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Comprehensive tests for FabricRenderer
 */
@ExtendWith(MockitoExtension.class)
class FabricRendererTest {

    private FabricRenderer renderer;

    @Mock
    private VertexConsumerProvider mockVertexConsumerProvider;

    @Mock
    private VertexConsumer mockVertexConsumer;

    @Mock
    private MatrixStack mockMatrixStack;

    @Mock
    private MatrixStack.Entry mockEntry;

    @BeforeEach
    void setUp() {
        renderer = new FabricRenderer();
    }

    @Nested
    @DisplayName("RenderContext")
    class RenderContextTests {

        @Test
        @DisplayName("should store matrices and vertex consumers")
        void shouldStoreMatricesAndVertexConsumers() {
            FabricRenderer.RenderContext context = new FabricRenderer.RenderContext(
                mockMatrixStack, mockVertexConsumerProvider
            );

            assertSame(mockMatrixStack, context.matrices);
            assertSame(mockVertexConsumerProvider, context.vertexConsumers);
        }

        @Test
        @DisplayName("should allow null values")
        void shouldAllowNullValues() {
            FabricRenderer.RenderContext context = new FabricRenderer.RenderContext(null, null);

            assertNull(context.matrices);
            assertNull(context.vertexConsumers);
        }
    }

    @Nested
    @DisplayName("renderSphere")
    @MockitoSettings(strictness = Strictness.LENIENT)
    class RenderSphereTests {

        private FabricRenderer.RenderContext validContext;
        private SphereColor testColor;
        private Matrix4f identityMatrix;

        @BeforeEach
        void setUp() {
            identityMatrix = new Matrix4f().identity();
            when(mockMatrixStack.peek()).thenReturn(mockEntry);
            when(mockEntry.getPositionMatrix()).thenReturn(identityMatrix);
            when(mockVertexConsumerProvider.getBuffer(any(RenderLayer.class))).thenReturn(mockVertexConsumer);
            when(mockVertexConsumer.vertex(any(Matrix4f.class), anyFloat(), anyFloat(), anyFloat()))
                .thenReturn(mockVertexConsumer);
            when(mockVertexConsumer.color(anyFloat(), anyFloat(), anyFloat(), anyFloat()))
                .thenReturn(mockVertexConsumer);
            when(mockVertexConsumer.normal(anyFloat(), anyFloat(), anyFloat()))
                .thenReturn(mockVertexConsumer);

            validContext = new FabricRenderer.RenderContext(mockMatrixStack, mockVertexConsumerProvider);
            testColor = new SphereColor(1.0f, 0.5f, 0.0f, 0.8f);
        }

        @Test
        @DisplayName("should handle invalid context type gracefully")
        void shouldHandleInvalidContextType() {
            assertDoesNotThrow(() ->
                renderer.renderSphere("invalid context", 0, 0, 0, 16.0f, testColor, 24)
            );
        }

        @Test
        @DisplayName("should handle null vertex consumers gracefully")
        void shouldHandleNullVertexConsumers() {
            FabricRenderer.RenderContext contextWithNullConsumers =
                new FabricRenderer.RenderContext(mockMatrixStack, null);

            assertDoesNotThrow(() ->
                renderer.renderSphere(contextWithNullConsumers, 0, 0, 0, 16.0f, testColor, 24)
            );
        }

        @Test
        @DisplayName("should render sphere at specified position")
        void shouldRenderSphereAtPosition() {
            renderer.renderSphere(validContext, 10.0, 64.0, -20.0, 16.0f, testColor, 24);

            verify(mockVertexConsumerProvider).getBuffer(any(RenderLayer.class));
            verify(mockVertexConsumer, atLeastOnce()).vertex(any(Matrix4f.class), anyFloat(), anyFloat(), anyFloat());
        }

        @Test
        @DisplayName("should use specified color")
        void shouldUseSpecifiedColor() {
            renderer.renderSphere(validContext, 0, 0, 0, 16.0f, testColor, 24);

            verify(mockVertexConsumer, atLeastOnce())
                .color(eq(testColor.red), eq(testColor.green), eq(testColor.blue), eq(testColor.alpha));
        }

        @Test
        @DisplayName("should use default segments when segments is zero or negative")
        void shouldUseDefaultSegmentsWhenInvalid() {
            renderer.renderSphere(validContext, 0, 0, 0, 16.0f, testColor, 0);
            verify(mockVertexConsumer, atLeastOnce()).vertex(any(Matrix4f.class), anyFloat(), anyFloat(), anyFloat());

            reset(mockVertexConsumer);
            when(mockVertexConsumer.vertex(any(Matrix4f.class), anyFloat(), anyFloat(), anyFloat()))
                .thenReturn(mockVertexConsumer);
            when(mockVertexConsumer.color(anyFloat(), anyFloat(), anyFloat(), anyFloat()))
                .thenReturn(mockVertexConsumer);
            when(mockVertexConsumer.normal(anyFloat(), anyFloat(), anyFloat()))
                .thenReturn(mockVertexConsumer);

            renderer.renderSphere(validContext, 0, 0, 0, 16.0f, testColor, -5);
            verify(mockVertexConsumer, atLeastOnce()).vertex(any(Matrix4f.class), anyFloat(), anyFloat(), anyFloat());
        }

        @ParameterizedTest
        @ValueSource(ints = {8, 16, 24, 32, 48, 64})
        @DisplayName("should render with different segment counts")
        void shouldRenderWithDifferentSegmentCounts(int segments) {
            renderer.renderSphere(validContext, 0, 0, 0, 16.0f, testColor, segments);

            verify(mockVertexConsumer, atLeastOnce()).vertex(any(Matrix4f.class), anyFloat(), anyFloat(), anyFloat());
        }

        @ParameterizedTest
        @ValueSource(floats = {1.0f, 8.0f, 16.0f, 32.0f, 64.0f})
        @DisplayName("should render with different radii")
        void shouldRenderWithDifferentRadii(float radius) {
            renderer.renderSphere(validContext, 0, 0, 0, radius, testColor, 24);

            verify(mockVertexConsumer, atLeastOnce()).vertex(any(Matrix4f.class), anyFloat(), anyFloat(), anyFloat());
        }

        @Test
        @DisplayName("should render latitude circles")
        void shouldRenderLatitudeCircles() {
            renderer.renderSphere(validContext, 0, 0, 0, 16.0f, testColor, 24);

            // Latitude circles: 7 levels (-6 to 6 in steps of 2) * segments * 2 vertices per line
            // Plus longitude circles
            verify(mockVertexConsumer, atLeast(1)).vertex(any(Matrix4f.class), anyFloat(), anyFloat(), anyFloat());
        }

        @Test
        @DisplayName("should render longitude circles")
        void shouldRenderLongitudeCircles() {
            renderer.renderSphere(validContext, 0, 0, 0, 16.0f, testColor, 24);

            // 6 longitude circles
            verify(mockVertexConsumer, atLeast(1)).vertex(any(Matrix4f.class), anyFloat(), anyFloat(), anyFloat());
        }

        @Test
        @DisplayName("should set normal vectors")
        void shouldSetNormalVectors() {
            renderer.renderSphere(validContext, 0, 0, 0, 16.0f, testColor, 24);

            verify(mockVertexConsumer, atLeastOnce()).normal(anyFloat(), anyFloat(), anyFloat());
        }

        @Test
        @DisplayName("should handle zero radius")
        void shouldHandleZeroRadius() {
            assertDoesNotThrow(() ->
                renderer.renderSphere(validContext, 0, 0, 0, 0.0f, testColor, 24)
            );
        }

        @Test
        @DisplayName("should handle negative coordinates")
        void shouldHandleNegativeCoordinates() {
            assertDoesNotThrow(() ->
                renderer.renderSphere(validContext, -100.0, -64.0, -200.0, 16.0f, testColor, 24)
            );
        }
    }

    @Nested
    @DisplayName("SphereColor")
    class SphereColorTests {

        @Test
        @DisplayName("should store color components")
        void shouldStoreColorComponents() {
            SphereColor color = new SphereColor(0.5f, 0.6f, 0.7f, 0.8f);

            assertEquals(0.5f, color.red, 0.001f);
            assertEquals(0.6f, color.green, 0.001f);
            assertEquals(0.7f, color.blue, 0.001f);
            assertEquals(0.8f, color.alpha, 0.001f);
        }

        @Test
        @DisplayName("should handle edge values")
        void shouldHandleEdgeValues() {
            SphereColor colorMin = new SphereColor(0.0f, 0.0f, 0.0f, 0.0f);
            SphereColor colorMax = new SphereColor(1.0f, 1.0f, 1.0f, 1.0f);

            assertEquals(0.0f, colorMin.red, 0.001f);
            assertEquals(1.0f, colorMax.red, 0.001f);
        }
    }
}
