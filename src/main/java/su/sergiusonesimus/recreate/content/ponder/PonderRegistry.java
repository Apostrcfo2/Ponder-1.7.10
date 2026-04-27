package su.sergiusonesimus.recreate.content.ponder;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import su.sergiusonesimus.recreate.AllBlocks;
import su.sergiusonesimus.recreate.content.ponder.scenes.ShaftPonderScene;

@SideOnly(Side.CLIENT)
public class PonderRegistry {

    private static final Map<Block, PonderScene> scenes = new HashMap<>();

    public static void register() {
        scenes.put(AllBlocks.shaft, new ShaftPonderScene());
    }

    public static PonderScene getScene(ItemStack stack) {
        if (stack == null) return null;
        if (!(stack.getItem() instanceof ItemBlock)) return null;
        Block block = ((ItemBlock) stack.getItem()).field_150939_a;
        return scenes.get(block);
    }
}
