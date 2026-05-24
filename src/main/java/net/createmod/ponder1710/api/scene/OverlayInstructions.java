package net.createmod.ponder1710.api.scene;

// import net.createmod.metanip.math.Pointing; // TODO: catnip not available
import net.createmod.ponder1710.api.PonderPalette;
import net.createmod.ponder1710.api.element.InputElementBuilder;
import net.createmod.ponder1710.api.element.TextElementBuilder;

// import net.minecraft.core.BlockPos; // 1.7.10 uses x,y,z
// import net.minecraft.core.Direction; // 1.7.10 uses ForgeDirection
// import net.minecraft.world.phys.AABB; // AxisAlignedBB in 1.7.10
// import net.minecraft.world.phys.Vec3; // net.minecraft.util.Vec3 in 1.7.10
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;
import net.minecraftforge.common.util.ForgeDirection;

public interface OverlayInstructions {

    TextElementBuilder showText(int duration);

    TextElementBuilder showOutlineWithText(Selection selection, int duration);

    // TODO: Pointing from catnip not available - replaced with ForgeDirection
    InputElementBuilder showControls(Vec3 sceneSpace, ForgeDirection direction, int duration);

    // AABB -> AxisAlignedBB in 1.7.10
    void chaseBoundingBoxOutline(PonderPalette color, Object slot, AxisAlignedBB boundingBox, int duration);

    // BlockPos -> x,y,z, Direction -> ForgeDirection in 1.7.10
    void showCenteredScrollInput(int x, int y, int z, ForgeDirection side, int duration);

    void showScrollInput(Vec3 location, ForgeDirection side, int duration);

    void showRepeaterScrollInput(int x, int y, int z, int duration);

    void showFilterSlotInput(Vec3 location, int duration);

    void showFilterSlotInput(Vec3 location, ForgeDirection side, int duration);

    void showLine(PonderPalette color, Vec3 start, Vec3 end, int duration);

    void showBigLine(PonderPalette color, Vec3 start, Vec3 end, int duration);

    void showOutline(PonderPalette color, Object slot, Selection selection, int duration);
}
