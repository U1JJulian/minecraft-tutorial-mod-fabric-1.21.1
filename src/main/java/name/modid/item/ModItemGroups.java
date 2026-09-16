package name.modid.item;

import name.modid.TutorialMod;
import name.modid.block.ModBlocks;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModItemGroups {
    public static final ItemGroup PINK_GARNET_ITEMS_GROUP = Registry.register(
            Registries.ITEM_GROUP, Identifier.of(TutorialMod.MOD_ID, "pink_garnet_item"),
            FabricItemGroup.builder().icon(()-> new ItemStack(ModItems.KAKU_GEM))
                    .displayName(Text.translatable("itemgroup.tutorial-mod.kaku_items_group"))
                    .entries(((displayContext, entries) -> {
                        entries.add(ModItems.KAKU_GEM);
                        entries.add(ModItems.RC_CELLS);
                        entries.add(ModItems.INVESTIGATORS_FLESH);
                        entries.add(ModItems.INVESTIGATORS_BLOOD);
                    })).build());

    public static final ItemGroup PINK_GARNET_BLOCKS_GROUP = Registry.register(
            Registries.ITEM_GROUP, Identifier.of(TutorialMod.MOD_ID, "kaku_block"),
            FabricItemGroup.builder().icon(()-> new ItemStack(ModBlocks.KAKU_BLOCK))
                    .displayName(Text.translatable("itemgroup.tutorial-mod.kaku_blocks_group"))
                    .entries(((displayContext, entries) -> {
                        entries.add(ModBlocks.KAKU_BLOCK);
                    })).build());

    public static void registerItemGroups(){
        TutorialMod.LOGGER.info("Registering Item Groups for: " + TutorialMod.MOD_ID);
    }
}
