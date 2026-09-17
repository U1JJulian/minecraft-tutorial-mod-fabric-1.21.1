package name.modid.util;

import name.modid.TutorialMod;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class ModTags {

    public static class Blocks{
        public static final TagKey<Block> NEEDS_KAKU_TOOL = createdTag("needs_kaku_tool");
        public static final TagKey<Block> INCORRECT_FOR_KAKU_TOOL = createdTag("incorrect_for_kaku_tool");

        private static TagKey<Block> createdTag(String name){
            return TagKey.of(RegistryKeys.BLOCK, Identifier.of(TutorialMod.MOD_ID, name));
        }
    }

    public static class Items {
        private static TagKey<Item> createdTag(String name){
            return TagKey.of(RegistryKeys.ITEM, Identifier.of(TutorialMod.MOD_ID, name));
        }
    }
}
