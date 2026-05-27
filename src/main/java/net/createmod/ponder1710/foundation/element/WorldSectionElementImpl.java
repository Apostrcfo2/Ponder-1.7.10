package net.createmod.ponder1710.foundation.element;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Nullable;

import net.createmod.metanip.animation.AnimationTickHolder;
import net.createmod.metanip.data.Pair;
import net.createmod.metanip.math.VecHelper;
import net.createmod.metanip.outliner.AABBOutline;
import net.createmod.metanip.registry.RegisteredObjectsHelper;

import net.createmod.ponder1710.Ponder;
import net.createmod.ponder1710.api.element.WorldSectionElement;
import net.createmod.ponder1710.api.level.PonderLevel;
import net.createmod.ponder1710.api.scene.Selection;
import net.createmod.ponder1710.foundation.PonderScene;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

public class WorldSectionElementImpl extends AnimatedSceneElementBase implements WorldSectionElement {

    @Nullable
    List<TileEntity> renderedTileEntities;
    @Nullable
    List<Pair<TileEntity, Runnable>> tickableTileEntities;

    @Nullable
    Selection section;
    boolean redraw;

    Vec3 prevAnimatedOffset;
    Vec3 animatedOffset;
    Vec3 prevAnimatedRotation;
    Vec3 animatedRotation;
    Vec3 centerOfRotation;
    @Nullable
    Vec3 stabilizationAnchor = null;

    @Nullable
    int[] selectedBlock = null; // {x, y, z}

    public WorldSectionElementImpl() {
        prevAnimatedOffset    = Vec3.createVectorHelper(0, 0, 0);
        animatedOffset        = Vec3.createVectorHelper(0, 0, 0);
        prevAnimatedRotation  = Vec3.createVectorHelper(0, 0, 0);
        animatedRotation      = Vec3.createVectorHelper(0, 0, 0);
        centerOfRotation      = Vec3.createVectorHelper(0, 0, 0);
    }

    public WorldSectionElementImpl(Selection section) {
        this();
        this.section = section.copy();
        centerOfRotation = section.getCenter();
    }

    @Override public void mergeOnto(WorldSectionElement other) {
        setVisible(false);
        if (other.isEmpty()) other.set(section);
        else other.add(section);
    }

    @Override public void set(Selection selection)  { applyNewSelection(selection.copy()); }
    @Override public void add(Selection toAdd)       { applyNewSelection(this.section.add(toAdd)); }
    @Override public void erase(Selection toErase)   { applyNewSelection(this.section.substract(toErase)); }

    private void applyNewSelection(Selection selection) {
        this.section = selection;
        queueRedraw();
    }

    @Override public void setCenterOfRotation(Vec3 center)  { centerOfRotation = center; }
    @Override public void stabilizeRotation(Vec3 anchor)    { stabilizationAnchor = anchor; }

    @Override
    public void reset(PonderScene scene) {
        super.reset(scene);
        resetAnimatedTransform();
        resetSelectedBlock();
    }

    @Override public void selectBlock(int x, int y, int z) { selectedBlock = new int[]{x, y, z}; }
    @Override public void resetSelectedBlock()             { selectedBlock = null; }

    public void resetAnimatedTransform() {
        prevAnimatedOffset   = Vec3.createVectorHelper(0, 0, 0);
        animatedOffset       = Vec3.createVectorHelper(0, 0, 0);
        prevAnimatedRotation = Vec3.createVectorHelper(0, 0, 0);
        animatedRotation     = Vec3.createVectorHelper(0, 0, 0);
    }

    @Override public void queueRedraw() { redraw = true; }
    @Override public boolean isEmpty()  { return section == null; }
    @Override public void setEmpty()    { section = null; }

    @Override public void setAnimatedRotation(Vec3 eulerAngles, boolean force) {
        this.animatedRotation = eulerAngles;
        if (force) prevAnimatedRotation = animatedRotation;
    }
    @Override public Vec3 getAnimatedRotation() { return animatedRotation; }

    @Override public void setAnimatedOffset(Vec3 offset, boolean force) {
        this.animatedOffset = offset;
        if (force) prevAnimatedOffset = animatedOffset;
    }
    @Override public Vec3 getAnimatedOffset() { return animatedOffset; }

    @Override public boolean isVisible() { return super.isVisible() && !isEmpty(); }

    // Ray trace using 1.7.10 world.rayTraceBlocks()
    @Override
    public Pair<Vec3, MovingObjectPosition> rayTrace(PonderLevel world, Vec3 source, Vec3 target) {
        world.setMask(section);
        Vec3 transformedSource = reverseTransformVec(source);
        Vec3 transformedTarget = reverseTransformVec(target);

        MovingObjectPosition hit = world.rayTraceBlocks(transformedSource, transformedTarget);
        world.clearMask();

        if (hit == null || hit.typeOfHit == MovingObjectPosition.MovingObjectType.MISS)
            return Pair.of(target, hit);

        Vec3 hitVec = hit.hitVec;
        double t = hitVec.distanceTo(transformedTarget) / source.distanceTo(target);
        Vec3 actualHit = VecHelper.lerp((float) t, target, source);
        return Pair.of(actualHit, hit);
    }

    private Vec3 reverseTransformVec(Vec3 in) {
        float pt = AnimationTickHolder.getPartialTicks();
        Vec3 offset = VecHelper.lerp(pt, prevAnimatedOffset, animatedOffset);
        in = in.addVector(-offset.xCoord, -offset.yCoord, -offset.zCoord);

        boolean hasRotation = animatedRotation.xCoord != 0 || animatedRotation.yCoord != 0
            || animatedRotation.zCoord != 0 || prevAnimatedRotation.xCoord != 0
            || prevAnimatedRotation.yCoord != 0 || prevAnimatedRotation.zCoord != 0;

        if (hasRotation) {
            double rotX = MathHelper.lerp(pt, prevAnimatedRotation.xCoord, animatedRotation.xCoord);
            double rotY = MathHelper.lerp(pt, prevAnimatedRotation.yCoord, animatedRotation.yCoord);
            double rotZ = MathHelper.lerp(pt, prevAnimatedRotation.zCoord, animatedRotation.zCoord);

            in = in.addVector(-centerOfRotation.xCoord, -centerOfRotation.yCoord, -centerOfRotation.zCoord);
            in = VecHelper.rotate(in, -rotX, VecHelper.AXIS_X);
            in = VecHelper.rotate(in, -rotZ, VecHelper.AXIS_Z);
            in = VecHelper.rotate(in, -rotY, VecHelper.AXIS_Y);
            in = in.addVector(centerOfRotation.xCoord, centerOfRotation.yCoord, centerOfRotation.zCoord);

            if (stabilizationAnchor != null) {
                in = in.addVector(-stabilizationAnchor.xCoord, -stabilizationAnchor.yCoord, -stabilizationAnchor.zCoord);
                in = VecHelper.rotate(in, rotX, VecHelper.AXIS_X);
                in = VecHelper.rotate(in, rotZ, VecHelper.AXIS_Z);
                in = VecHelper.rotate(in, rotY, VecHelper.AXIS_Y);
                in = in.addVector(stabilizationAnchor.xCoord, stabilizationAnchor.yCoord, stabilizationAnchor.zCoord);
            }
        }
        return in;
    }

    // GL11 equivalent of transformMS
    public void transformGL(float pt) {
        double lerpX = MathHelper.lerp(pt, prevAnimatedOffset.xCoord, animatedOffset.xCoord);
        double lerpY = MathHelper.lerp(pt, prevAnimatedOffset.yCoord, animatedOffset.yCoord);
        double lerpZ = MathHelper.lerp(pt, prevAnimatedOffset.zCoord, animatedOffset.zCoord);
        GL11.glTranslated(lerpX, lerpY, lerpZ);

        boolean hasRotation = animatedRotation.xCoord != 0 || animatedRotation.yCoord != 0
            || animatedRotation.zCoord != 0 || prevAnimatedRotation.xCoord != 0
            || prevAnimatedRotation.yCoord != 0 || prevAnimatedRotation.zCoord != 0;

        if (hasRotation) {
            double rotX = MathHelper.lerp(pt, prevAnimatedRotation.xCoord, animatedRotation.xCoord);
            double rotY = MathHelper.lerp(pt, prevAnimatedRotation.yCoord, animatedRotation.yCoord);
            double rotZ = MathHelper.lerp(pt, prevAnimatedRotation.zCoord, animatedRotation.zCoord);

            GL11.glTranslated(centerOfRotation.xCoord, centerOfRotation.yCoord, centerOfRotation.zCoord);
            GL11.glRotated(rotX, 1, 0, 0);
            GL11.glRotated(rotY, 0, 1, 0);
            GL11.glRotated(rotZ, 0, 0, 1);
            GL11.glTranslated(-centerOfRotation.xCoord, -centerOfRotation.yCoord, -centerOfRotation.zCoord);

            if (stabilizationAnchor != null) {
                GL11.glTranslated(stabilizationAnchor.xCoord, stabilizationAnchor.yCoord, stabilizationAnchor.zCoord);
                GL11.glRotated(-rotX, 1, 0, 0);
                GL11.glRotated(-rotY, 0, 1, 0);
                GL11.glRotated(-rotZ, 0, 0, 1);
                GL11.glTranslated(-stabilizationAnchor.xCoord, -stabilizationAnchor.yCoord, -stabilizationAnchor.zCoord);
            }
        }
    }

    @Override
    public void tick(PonderScene scene) {
        prevAnimatedOffset   = animatedOffset;
        prevAnimatedRotation = animatedRotation;
        if (!isVisible()) return;
        loadTileEntitiesIfMissing(scene.getWorld());

        // Remove stale TileEntities
        renderedTileEntities.removeIf(te ->
            scene.getWorld().getTileEntity(te.xCoord, te.yCoord, te.zCoord) != te);
        tickableTileEntities.removeIf(pair ->
            scene.getWorld().getTileEntity(
                pair.getFirst().xCoord,
                pair.getFirst().yCoord,
                pair.getFirst().zCoord
            ) != pair.getFirst());

        // Tick tickable TileEntities
        tickableTileEntities.forEach(pair -> pair.getSecond().run());
    }

    @Override
    public void whileSkipping(PonderScene scene) {
        if (redraw) {
            renderedTileEntities = null;
            tickableTileEntities = null;
        }
        redraw = false;
    }

    protected void loadTileEntitiesIfMissing(PonderLevel world) {
        if (renderedTileEntities != null) return;
        renderedTileEntities  = new ArrayList<>();
        tickableTileEntities  = new ArrayList<>();

        section.forEach((int x, int y, int z) -> {
            TileEntity te = world.getTileEntity(x, y, z);
            if (te == null) return;
            Block block = world.getBlock(x, y, z);
            int meta = world.getBlockMetadata(x, y, z);
            if (!block.hasTileEntity(meta)) return;

            te.setWorldObj(world);
            // Check if TileEntity can tick (has updateEntity override)
            tickableTileEntities.add(Pair.of(te, te::updateEntity));
            renderedTileEntities.add(te);
        });
    }

    @Override
    public void renderFirst(PonderLevel world, float pt) {
        if (redraw) {
            renderedTileEntities = null;
            tickableTileEntities = null;
        }
        GL11.glPushMatrix();
        transformGL(pt);

        int light = -1;
        if (fadeValue != 1)
            light = (int) MathHelper.lerp(fadeValue, 5, 15);
        world.pushFakeLight(light);
        renderTileEntities(world, pt);
        world.popLight();

        // Block breaking overlay
        for (java.util.Map.Entry<Long, Integer> entry : world.getBlockBreakingProgressions().entrySet()) {
            int[] pos = PonderLevel.decodePos(entry.getKey());
            if (!section.test(pos)) continue;
            int stage = entry.getValue();
            // In 1.7.10 break overlay is handled by RenderGlobal
            Minecraft.getMinecraft().renderGlobal.drawBlockBreaking(
                Minecraft.getMinecraft().thePlayer, null,
                pos[0], pos[1], pos[2],
                stage
            );
        }

        GL11.glPopMatrix();
    }

    @Override
    public void renderLayer(PonderLevel world, float pt) {
        GL11.glPushMatrix();
        transformGL(pt);

        // Render blocks using RenderBlocks
        RenderBlocks renderBlocks = new RenderBlocks(world);
        renderBlocks.renderAllFaces = true;

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        section.forEach((int x, int y, int z) -> {
            Block block = world.getBlock(x, y, z);
            int meta = world.getBlockMetadata(x, y, z);
            if (block == null || block.getMaterial() == net.minecraft.block.material.Material.air) return;

            Minecraft.getMinecraft().getTextureManager().bindTexture(
                net.minecraft.client.renderer.texture.TextureMap.locationBlocksTexture
            );

            renderBlocks.renderBlockByRenderType(block, x, y, z);
        });

        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
        redraw = false;
    }

    @Override
    public void renderLast(PonderLevel world, float pt) {
        redraw = false;
        if (selectedBlock == null) return;

        int x = selectedBlock[0], y = selectedBlock[1], z = selectedBlock[2];
        Block block = world.getBlock(x, y, z);
        int meta = world.getBlockMetadata(x, y, z);
        if (block == null || block.getMaterial() == net.minecraft.block.material.Material.air) return;

        // Get block bounds for selection outline
        block.setBlockBoundsBasedOnState(world, x, y, z);
        AxisAlignedBB bb = AxisAlignedBB.getBoundingBox(
            x + block.getBlockBoundsMinX(), y + block.getBlockBoundsMinY(), z + block.getBlockBoundsMinZ(),
            x + block.getBlockBoundsMaxX(), y + block.getBlockBoundsMaxY(), z + block.getBlockBoundsMaxZ()
        );

        GL11.glPushMatrix();
        transformGL(pt);

        // Use AABBOutline from metanip
        AABBOutline outline = new AABBOutline(bb);
        outline.getParams()
            .lineWidth(1 / 64f)
            .colored(0xefefef)
            .disableLineNormals();
        outline.render(new Matrix4f(), Vec3.createVectorHelper(0, 0, 0), pt);

        GL11.glPopMatrix();
    }

    private void renderTileEntities(PonderLevel world, float pt) {
        loadTileEntitiesIfMissing(world);
        Iterator<TileEntity> iterator = renderedTileEntities.iterator();
        while (iterator.hasNext()) {
            TileEntity te = iterator.next();
            if (TileEntityRendererDispatcher.instance.getSpecialRenderer(te) == null) {
                iterator.remove();
                continue;
            }
            GL11.glPushMatrix();
            GL11.glTranslatef(te.xCoord, te.yCoord, te.zCoord);
            try {
                TileEntityRendererDispatcher.instance.renderTileEntityAt(te, te.xCoord, te.yCoord, te.zCoord, pt);
            } catch (Exception e) {
                iterator.remove();
                Ponder.LOGGER.error("TileEntity {} could not be rendered virtually.",
                    RegisteredObjectsHelper.getKeyOrThrow(net.minecraft.block.Block.blockRegistry.getNameForObject(
                        world.getBlock(te.xCoord, te.yCoord, te.zCoord))), e);
            }
            GL11.glPopMatrix();
        }
    }
}
