package net.createmod.ponder1710.foundation;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;

public class PonderForgeEvents {

    @SubscribeEvent
    public void onItemTooltip(ItemTooltipEvent event) {
        if (event.itemStack == null) return;
        PonderTooltipHandler.addToTooltip(event.toolTip, event.itemStack);
    }
}
