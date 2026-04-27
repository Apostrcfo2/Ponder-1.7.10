package su.sergiusonesimus.recreate.content.ponder;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.item.ItemStack;

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraftforge.client.event.GuiScreenEvent;

@SideOnly(Side.CLIENT)
public class PonderHandler {

    private static final int HOLD_TICKS = 60;
    private int holdTicks = 0;
    private boolean wasHolding = false;

    @SubscribeEvent
    public void onGuiKeyboard(GuiScreenEvent.KeyboardInputEvent.Pre event) {
        if (!(event.gui instanceof GuiInventory)) {
            holdTicks = 0;
            wasHolding = false;
            return;
        }

        boolean wDown = Keyboard.isKeyDown(Keyboard.KEY_W);

        if (wDown) {
            holdTicks++;
            if (holdTicks >= HOLD_TICKS && !wasHolding) {
                wasHolding = true;
                Minecraft mc = Minecraft.getMinecraft();
                ItemStack hovered = getHoveredItem(mc);
                if (hovered != null) {
                    PonderScreen.open(hovered);
                }
            }
        } else {
            holdTicks = 0;
            wasHolding = false;
        }
    }

    private ItemStack getHoveredItem(Minecraft mc) {
        if (mc.thePlayer == null) return null;
        return mc.thePlayer.inventory.getItemStack();
    }
}
