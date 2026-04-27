package net.createmod.ponder1710.registry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.createmod.ponder1710.scene.PonderScene;

public class PonderRegistry {

    private static final Map<String, List<PonderScene>> scenes = new HashMap<>();

    public static void init() {
        // Scenes registered here by mods using the API
    }

    public static void addScene(Block block, PonderScene scene) {
        String key = Block.blockRegistry.getNameForObject(block);
        scenes.computeIfAbsent(key, k -> new ArrayList<>()).add(scene);
    }

    public static void addScene(Item item, PonderScene scene) {
        String key = Item.itemRegistry.getNameForObject(item);
        scenes.computeIfAbsent(key, k -> new ArrayList<>()).add(scene);
    }

    public static List<PonderScene> getScenes(ItemStack stack) {
        if (stack == null) return null;

        String key = null;
        if (stack.getItem() instanceof ItemBlock) {
            Block block = ((ItemBlock) stack.getItem()).field_150939_a;
            key = Block.blockRegistry.getNameForObject(block);
        } else {
            key = Item.itemRegistry.getNameForObject(stack.getItem());
        }

        return scenes.get(key);
    }

    public static boolean hasScene(ItemStack stack) {
        List<PonderScene> result = getScenes(stack);
        return result != null && !result.isEmpty();
    }
}
