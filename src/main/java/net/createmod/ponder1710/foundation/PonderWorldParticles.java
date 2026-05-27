package net.createmod.ponder1710.foundation;

import java.util.Iterator;
import java.util.Queue;

import com.google.common.collect.Queues;

import net.createmod.ponder1710.api.level.PonderLevel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;

import org.lwjgl.opengl.GL11;

// In 1.7.10 particles are EntityFX instances rendered via EffectRenderer
public class PonderWorldParticles {

    private final Queue<EntityFX> queue = Queues.newArrayDeque();
    PonderLevel world;

    public PonderWorldParticles(PonderLevel world) {
        this.world = world;
    }

    public void addParticle(EntityFX p) {
        queue.add(p);
    }

    public void tick() {
        Iterator<EntityFX> iterator = queue.iterator();
        while (iterator.hasNext()) {
            EntityFX particle = iterator.next();
            particle.onUpdate();
            if (!particle.isEntityAlive())
                iterator.remove();
        }
    }

    public void renderParticles(float pt) {
        if (queue.isEmpty()) return;

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDepthMask(false);

        Minecraft mc = Minecraft.getMinecraft();
        mc.getTextureManager().bindTexture(
            net.minecraft.client.renderer.texture.TextureMap.locationParticlesTexture
        );

        for (EntityFX particle : queue) {
            // In 1.7.10 particle rendering uses Tessellator
            net.minecraft.client.renderer.Tessellator tess = net.minecraft.client.renderer.Tessellator.instance;
            tess.startDrawingQuads();
            particle.renderParticle(tess,
                mc.renderViewEntity,
                pt,
                (float) Math.cos(Math.toRadians(mc.thePlayer.rotationYaw)),
                (float) Math.sin(Math.toRadians(mc.thePlayer.rotationPitch)),
                (float) -Math.sin(Math.toRadians(mc.thePlayer.rotationYaw)),
                (float) (Math.sin(Math.toRadians(mc.thePlayer.rotationPitch)) * Math.cos(Math.toRadians(mc.thePlayer.rotationYaw))),
                (float) (Math.sin(Math.toRadians(mc.thePlayer.rotationPitch)) * Math.sin(Math.toRadians(mc.thePlayer.rotationYaw)))
            );
            tess.draw();
        }

        GL11.glDepthMask(true);
        GL11.glDisable(GL11.GL_BLEND);
    }

    public void clearEffects() {
        queue.clear();
    }
}
