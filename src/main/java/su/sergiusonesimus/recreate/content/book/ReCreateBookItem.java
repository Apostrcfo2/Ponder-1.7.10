package su.sergiusonesimus.recreate.content.book;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import su.sergiusonesimus.recreate.AllItems;
import su.sergiusonesimus.recreate.ReCreate;

public class ReCreateBookItem extends Item {

    public ReCreateBookItem() {
        setUnlocalizedName("recreate_book");
        setTextureName(ReCreate.ID + ":recreate_book");
        setMaxStackSize(1);
        setCreativeTab(AllItems.BASE_CREATIVE_TAB);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (world.isRemote) {
            ReCreateBookGui.open();
        }
        return stack;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean isFull3D() {
        return true;
    }
}
