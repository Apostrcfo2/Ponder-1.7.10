package net.createmod.ponder1710.api.scene;

import net.createmod.metanip.math.Pointing;
import net.createmod.ponder1710.api.PonderPalette;
import net.createmod.ponder1710.api.element.InputElementBuilder;
import net.createmod.ponder1710.api.element.TextElementBuilder;

import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;
import net.minecraftforge.common.util.ForgeDirection;

// Pointing -> net.createmod.metanip.math.Pointing (available in metanip)
// BlockPos -> x,y,z ints in 1.7.10
// Direction -> ForgeDirection in 1.7.10
// AABB -> AxisAlignedBB in 1.7.10
public interface OverlayInstructions {

    TextElementBuilder showText(int duration);

    TextElementBuilder showOutlineWithText(Selection selection, int duration);

    InputElementBuilder showControls(Vec3 sceneSpace, Pointing direction, int duration);

    void chaseBoundingBoxOutline(PonderPalette color, Object slot, AxisAlignedBB boundingBox, int duration);

    void showCenteredScrollInput(int x, int y, int z, ForgeDirection side, int duration);

    void showScrollInput(Vec3 location, ForgeDirection side, int duration);

    void showRepeaterScrollInput(int x, int y, int z, int duration);

    void showFilterSlotInput(Vec3 location, int duration);

    void showFilterSlotInput(Vec3 location, ForgeDirection side, int duration);

    void showLine(PonderPalette color, Vec3 start, Vec3 end, int duration);

    void showBigLine(PonderPalette color, Vec3 start, Vec3 end, int duration);

    void showOutline(PonderPalette color, Object slot, Selection selection, int duration);
}
