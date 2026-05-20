package net.createmod.metanip.render;

// SuperByteBuffer in modern MC buffers vertex data for deferred rendering.
// In 1.7.10 we use GL11 immediate mode - this interface is simplified.
// Flywheel TransformStack not available.

import org.joml.Matrix4f;
import org.joml.Quaternionf;

import net.createmod.metanip.theme.Color;

import org.lwjgl.opengl.GL11;

public interface SuperByteBuffer {

    // Matrix math via JOML
    SuperByteBuffer translate(double x, double y, double z);
    SuperByteBuffer rotate(float radians, int axis); // axis: 0=X, 1=Y, 2=Z
    SuperByteBuffer scale(float x, float y, float z);

    // Color
    SuperByteBuffer color(int r, int g, int b, int a);

    default SuperByteBuffer color(int color) {
        return color((color >> 16) & 0xFF, (color >> 8) & 0xFF, color & 0xFF, (color >> 24) & 0xFF);
    }

    default SuperByteBuffer color(Color color) {
        return color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
    }

    SuperByteBuffer disableDiffuse();

    SuperByteBuffer light(int packedLight);

    // Render the buffered geometry
    void renderInto(Matrix4f ms);

    boolean isEmpty();

    SuperByteBuffer reset();

    static int maxLight(int light1, int light2) {
        int block1 = (light1 >> 4) & 0xF;
        int sky1   = (light1 >> 20) & 0xF;
        int block2 = (light2 >> 4) & 0xF;
        int sky2   = (light2 >> 20) & 0xF;
        return (Math.max(block1, block2) << 4) | (Math.max(sky1, sky2) << 20);
    }
}
