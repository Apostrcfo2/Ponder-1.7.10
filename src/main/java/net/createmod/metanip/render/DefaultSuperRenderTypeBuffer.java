package net.createmod.metanip.render;

// Simplified singleton render buffer for 1.7.10.
// In modern MC this manages RenderType batching.
// In 1.7.10 we use GL11 directly - this class just acts as a hook point.

import org.lwjgl.opengl.GL11;

public class DefaultSuperRenderTypeBuffer implements SuperRenderTypeBuffer {

    private static final DefaultSuperRenderTypeBuffer INSTANCE = new DefaultSuperRenderTypeBuffer();

    public static DefaultSuperRenderTypeBuffer getInstance() {
        return INSTANCE;
    }

    @Override
    public void draw() {
        // In 1.7.10 GL11 calls are immediate - nothing to flush
        // GL11 state is managed per-call, no batching needed
    }
}
