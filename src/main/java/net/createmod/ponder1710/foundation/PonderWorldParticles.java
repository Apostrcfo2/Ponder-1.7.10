package net.createmod.ponder1710.foundation;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;

// import org.joml.Matrix4fStack; // not available in 1.7.10
// import com.google.common.collect.EvictingQueue; // not available in 1.7.10
// import com.mojang.blaze3d.systems.RenderSystem; // not available in 1.7.10
// import com.mojang.blaze3d.vertex.*; // not available in 1.7.10
// import net.minecraft.client.Camera; // not available in 1.7.10
// import net.minecraft.client.particle.Particle; // different in 1.7.10
// import net.minecraft.client.particle.ParticleRenderType; // not available in 1.7.10
// import net.minecraft.client.renderer.GameRenderer; // different in 1.7.10
// import net.minecraft.client.renderer.LightTexture; // not available in 1.7.10
// import net.minecraft.client.renderer.MultiBufferSource; // not available in 1.7.10

import net.createmod.ponder1710.api.level.PonderLevel;

import com.google.common.collect.Maps;
import com.google.common.collect.Queues;

public class PonderWorldParticles {

    // TODO: Particle system completely different in 1.7.10
    // Using Object as placeholder for Particle
    private final Queue<Object> queue = Queues.newArrayDeque();

    PonderLevel world;

    public PonderWorldParticles(PonderLevel world) {
        this.world = world;
    }

    public void addParticle(Object p) {
        this.queue.add(p);
    }

    public void tick() {
        // TODO: Reimplement using 1.7.10 particle system
        queue.clear();
    }

    // TODO: renderParticles - Camera/PoseStack/MultiBufferSource not available in 1.7.10
    // public void renderParticles(PoseStack ms, MultiBufferSource buffer, Camera renderInfo, float pt) { ... }
    public void renderParticles(float pt) {
        // TODO: Reimplement using 1.7.10 EffectRenderer
    }

    public void clearEffects() {
        this.queue.clear();
    }
}
