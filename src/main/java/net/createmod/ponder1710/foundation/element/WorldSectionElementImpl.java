package net.createmod.ponder1710.foundation.element;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

import javax.annotation.Nullable;

// import com.mojang.blaze3d.vertex.MeshData; // not available in 1.7.10
// import com.mojang.blaze3d.vertex.PoseStack; // not available in 1.7.10 - use GL11
// import com.mojang.blaze3d.vertex.SheetedDecalTextureGenerator; // not available in 1.7.10
// import com.mojang.blaze3d.vertex.VertexConsumer; // not available in 1.7.10
// import dev.engine_room.flywheel.lib.transform.TransformStack; // Flywheel not in 1.7.10
// import net.createmod.metanip.animation.AnimationTickHolder; // TODO: catnip not available
// import net.createmod.metanip.client.render.model.BakedModelBufferer; // TODO: catnip not available
// import net.createmod.metanip.client.render.model.ShadeSeparatedResultConsumer; // TODO: catnip not available
// import net.createmod.metanip.data.Pair; // TODO: catnip not available
// import net.createmod.metanip.math.VecHelper; // TODO: catnip not available
// import net.createmod.metanip.outliner.AABBOutline; // TODO: catnip not available
// import net.createmod.metanip.registry.RegisteredObjectsHelper; // TODO: catnip not available
// import net.createmod.metanip.render.SuperByteBuffer; // TODO: catnip not available
// import net.createmod.metanip.render.SuperByteBufferBuilder; // TODO: catnip not available
// import net.createmod.metanip.render.SuperByteBufferCache; // TODO: catnip not available
// import net.createmod.metanip.render.SuperByteBufferCache.Compartment; // TODO: catnip not available
// import net.createmod.metanip.render.SuperRenderTypeBuffer; // TODO: catnip not available
// import net.minecraft.client.gui.GuiGraphics; // not available in 1.7.10
// import net.minecraft.client.renderer.LevelRenderer; // different in 1.7.10
// import net.minecraft.client.renderer.MultiBufferSource; // not available in 1.7.10
// import net.minecraft.client.renderer.RenderType; // not available in 1.7.10
// import net.minecraft.client.renderer.blockentity.BlockEntityRenderer; // different in 1.7.10
// import net.minecraft.client.renderer.texture.OverlayTexture; // not available in 1.7.10
// import net.minecraft.client.resources.model.ModelBakery; // different in 1.7.10
// import net.minecraft.core.BlockPos; // 1.7.10 uses x,y,z
// import net.minecraft.core.Direction.Axis; // ForgeDirection in 1.7.10
// import net.minecraft.util.Mth; // MathHelper in 1.7.10
// import net.minecraft.world.level.ClipContext; // not available in 1.7.10
// import net.minecraft.world.level.Level; // World in 1.7.10
// import net.minecraft.world.level.block.Block; // different package in 1.7.10
// import net.minecraft.world.level.block.EntityBlock; // different in 1.7.10
// import net.minecraft.world.level.block.entity.BlockEntity; // TileEntity in 1.7.10
// import net.minecraft.world.level.block.entity.BlockEntityTicker; // not available in 1.7.10
// import net.minecraft.world.level.block.state.BlockState; // not available in 1.7.10
// import net.minecraft.world.phys.BlockHitResult; // MovingObjectPosition in 1.7.10
// import net.minecraft.world.phys.Vec3; // net.minecraft.util.Vec3 in 1.7.10
// import net.minecraft.world.phys.shapes.CollisionContext; // not available in 1.7.10
// import net.minecraft.world.phys.shapes.VoxelShape; // not available in 1.7.10

import net.createmod.ponder1710.Ponder;
import net.createmod.ponder1710.api.element.WorldSectionElement;
import net.createmod.ponder1710.api.level.PonderLevel;
import net.createmod.ponder1710.api.scene.Selection;
import net.createmod.ponder1710.foundation.PonderScene;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

import org.lwjgl.opengl.GL11;

public class WorldSectionElementImpl extends AnimatedSceneElementBase implements WorldSectionElement {

    // TODO: Compartment from catnip not available
    // public static final Compartment<Pair<Integer, Integer>> PONDER_WORLD_SECTION = new Compartment<>();

    @Nullable
    List<TileEntity> renderedTileEntities;
    // TODO: Pair and BlockEntityTicker from catnip/modern not available
    // @Nullable List<Pair<TileEntity, Consumer<World>>> tickableTileEntities;
    @Nullable
    Selection section;
    boolean redraw;

    // Vec3 from net.minecraft.util in 1.7.10
    Vec3 prevAnimatedOffset;
    Vec3 animatedOffset;
    Vec3 prevAnimatedRotation;
    Vec3 animatedRotation;
    Vec3 centerOfRotation;
    @Nullable
    Vec3 stabilizationAnchor = null;

    // BlockPos -> int[] {x, y, z} in 1.7.10
    @Nullable
    int[] selectedBlock = null;

    public WorldSectionElementImpl() {
        prevAnimatedOffset = Vec3.createVectorHelper(0, 0, 0);
        animatedOffset = Vec3.createVectorHelper(0, 0, 0);
        prevAnimatedRotation = Vec3.createVectorHelper(0, 0, 0);
        animatedRotation = Vec3.createVectorHelper(0, 0, 0);
        centerOfRotation = Vec3.createVectorHelper(0, 0, 0);
    }

    public WorldSectionElementImpl(Selection section) {
        this();
        this.section = section.copy();
        centerOfRotation = section.getCenter();
    }

    @Override
    public void mergeOnto(WorldSectionElement other) {
        setVisible(false);
        if (other.isEmpty())
            other.set(section);
        else
            other.add(section);
    }

    @Override
    public void set(Selection selection) {
        applyNewSelection(selection.copy());
    }

    @Override
    public void add(Selection toAdd) {
        applyNewSelection(this.section.add(toAdd));
    }

    @Override
    public void erase(Selection toErase) {
        applyNewSelection(this.section.substract(toErase));
    }

    private void applyNewSelection(Selection selection) {
        this.section = selection;
        queueRedraw();
    }

    @Override
    public void setCenterOfRotation(Vec3 center) {
        centerOfRotation = center;
    }

    @Override
    public void stabilizeRotation(Vec3 anchor) {
        stabilizationAnchor = anchor;
    }

    @Override
    public void reset(PonderScene scene) {
        super.reset(scene);
        resetAnimatedTransform();
        resetSelectedBlock();
    }

    @Override
    public void selectBlock(int x, int y, int z) {
        selectedBlock = new int[]{x, y, z};
    }

    @Override
    public void resetSelectedBlock() {
        selectedBlock = null;
    }

    public void resetAnimatedTransform() {
        prevAnimatedOffset = Vec3.createVectorHelper(0, 0, 0);
        animatedOffset = Vec3.createVectorHelper(0, 0, 0);
        prevAnimatedRotation = Vec3.createVectorHelper(0, 0, 0);
        animatedRotation = Vec3.createVectorHelper(0, 0, 0);
    }

    @Override
    public void queueRedraw() {
        redraw = true;
    }

    @Override
    public boolean isEmpty() {
        return section == null;
    }

    @Override
    public void setEmpty() {
        section = null;
    }

    @Override
    public void setAnimatedRotation(Vec3 eulerAngles, boolean force) {
        this.animatedRotation = eulerAngles;
        if (force)
            prevAnimatedRotation = animatedRotation;
    }

    @Override
    public Vec3 getAnimatedRotation() {
        return animatedRotation;
    }

    @Override
    public void setAnimatedOffset(Vec3 offset, boolean force) {
        this.animatedOffset = offset;
        if (force)
            prevAnimatedOffset = animatedOffset;
    }

    @Override
    public Vec3 getAnimatedOffset() {
        return animatedOffset;
    }

    @Override
    public boolean isVisible() {
        return super.isVisible() && !isEmpty();
    }

    @Override
    public Object[] rayTrace(PonderLevel world, Vec3 source, Vec3 target) {
        // TODO: Reimplement using 1.7.10 ray tracing
        // Original used ClipContext and VecHelper from catnip
        return new Object[]{null, null};
    }

    // TODO: transformMS - PoseStack not available in 1.7.10
    // Replaced with GL11 matrix operations
    public void transformGL(float pt) {
        double lerpX = MathHelper.lerp(pt, prevAnimatedOffset.xCoord, animatedOffset.xCoord);
        double lerpY = MathHelper.lerp(pt, prevAnimatedOffset.yCoord, animatedOffset.yCoord);
        double lerpZ = MathHelper.lerp(pt, prevAnimatedOffset.zCoord, animatedOffset.zCoord);
        GL11.glTranslated(lerpX, lerpY, lerpZ);

        boolean hasRotation = animatedRotation.xCoord != 0 || animatedRotation.yCoord != 0 || animatedRotation.zCoord != 0
            || prevAnimatedRotation.xCoord != 0 || prevAnimatedRotation.yCoord != 0 || prevAnimatedRotation.zCoord != 0;

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
        prevAnimatedOffset = animatedOffset;
        prevAnimatedRotation = animatedRotation;
        if (!isVisible())
            return;
        // TODO: loadTileEntitiesIfMissing - TileEntity system different in 1.7.10
    }

    @Override
    public void whileSkipping(PonderScene scene) {
        if (redraw) {
            renderedTileEntities = null;
            // tickableTileEntities = null;
        }
        redraw = false;
    }

    @Override
    public void renderFirst(PonderLevel world, float pt) {
        // TODO: Reimplement using 1.7.10 TileEntityRenderer
        // Original used BlockEntityRenderer + PoseStack + MultiBufferSource
        GL11.glPushMatrix();
        transformGL(pt);
        // TODO: render tile entities
        GL11.glPopMatrix();
    }

    @Override
    public void renderLayer(PonderLevel world, float pt) {
        // TODO: Reimplement block rendering using 1.7.10 RenderBlocks
        // Original used SuperByteBufferCache from catnip
        GL11.glPushMatrix();
        transformGL(pt);
        // TODO: render blocks
        GL11.glPopMatrix();
    }

    @Override
    public void renderLast(PonderLevel world, float pt) {
        redraw = false;
        if (selectedBlock == null)
            return;
        // TODO: Render selection outline using 1.7.10 RenderGlobal
        // Original used AABBOutline from catnip and VoxelShape
    }
}
