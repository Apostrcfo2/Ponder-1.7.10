package su.sergiusonesimus.recreate;

import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraftforge.common.util.EnumHelper;

import cpw.mods.fml.common.registry.GameRegistry;
import su.sergiusonesimus.recreate.content.contraptions.components.structureMovement.glue.SuperGlueItem;
import su.sergiusonesimus.recreate.content.contraptions.goggles.GogglesItem;
import su.sergiusonesimus.recreate.content.contraptions.wrench.WrenchItem;
import su.sergiusonesimus.recreate.foundation.item.CreateCreativeTab;
import su.sergiusonesimus.recreate.content.book.ReCreateBookItem;

public class AllItems {

    public static ItemArmor.ArmorMaterial ARMOR_GOGGLES = EnumHelper
        .addArmorMaterial("GOGGLES", 0, new int[] { 0, 0, 0, 0 }, 0);

    public static Item goggles;
    public static Item wrench;
    public static Item super_glue;
    public static Item recreate_book;

    public static final CreateCreativeTab BASE_CREATIVE_TAB = new CreateCreativeTab("base");

    public static void registerItems() {
        int gogglesRenderID = ReCreate.proxy.registerArmorRenderID(ReCreate.ID + "/goggles_");

        goggles = new GogglesItem(ARMOR_GOGGLES, gogglesRenderID).setUnlocalizedName("goggles")
            .setMaxStackSize(1)
            .setCreativeTab(AllItems.BASE_CREATIVE_TAB);
        wrench = new WrenchItem().setUnlocalizedName("wrench")
            .setMaxStackSize(1)
            .setCreativeTab(AllItems.BASE_CREATIVE_TAB);
        super_glue = new SuperGlueItem().setUnlocalizedName("super_glue")
            .setTextureName(ReCreate.ID + ":super_glue")
            .setMaxStackSize(1)
            .setCreativeTab(AllItems.BASE_CREATIVE_TAB);

        registerItem(goggles);
        registerItem(wrench);
        registerItem(super_glue);
        recreate_book = new ReCreateBookItem();
        registerItem(recreate_book);
    }

    private static void registerItem(Item item) {
        GameRegistry.registerItem(item, item.getUnlocalizedName(), ReCreate.ID);
    }

}
