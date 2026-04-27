package su.sergiusonesimus.recreate.content.ponder;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.item.ItemStack;

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class PonderHandler {

    private static final int HOLD_TICKS = 60;
    private int holdTicks = 0;
    private boolean wasHolding = false;

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.START) return;

        Minecraft mc = Minecraft.getMinecraft();
        if (!(mc.currentScreen instanceof GuiInventory)) {
            holdTicks = 0;
            wasHolding = false;
            return;
        }

        boolean wDown = Keyboard.isKeyDown(Keyboard.KEY_W);

        if (wDown) {
            holdTicks++;
            if (holdTicks >= HOLD_TICKS && !wasHolding) {
                wasHolding = true;
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
