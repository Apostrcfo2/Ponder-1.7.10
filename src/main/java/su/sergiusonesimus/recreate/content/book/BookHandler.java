package su.sergiusonesimus.recreate.content.book;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import su.sergiusonesimus.recreate.AllItems;

public class BookHandler {

    private static final String TAG = "recreate_book_given";

    @SubscribeEvent
    public void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        EntityPlayer player = event.player;

        if (player.worldObj.isRemote) return;

        NBTTagCompound data = player.getEntityData();
        NBTTagCompound persistent = data.getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);

        if (!persistent.getBoolean(TAG)) {
            persistent.setBoolean(TAG, true);
            data.setTag(EntityPlayer.PERSISTED_NBT_TAG, persistent);
            player.inventory.addItemStackToInventory(new ItemStack(AllItems.recreate_book));
        }
    }
}
