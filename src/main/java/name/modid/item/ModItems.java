package name.modid.item;

import name.modid.TutorialMod;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;

public class ModItems {

    public static final Item KAKU_GEM = registerItem("kaku_gem", new Item(new Item.Settings()));
    public static final Item RC_CELLS = registerItem("rc_cells", new Item(new Item.Settings()));
    public static final Item INVESTIGATORS_BLOOD = registerItem("investigators_blood", new Item(new Item.Settings()));
    public static final Item INVESTIGATORS_FLESH = registerItem("investigators_flesh", new Item(new Item.Settings()));

    private static Item registerItem(String name, Item item){
        return Registry.register(Registries.ITEM, Identifier.of(TutorialMod.MOD_ID, name), item);
    }

    public static void registerModItems(){
        TutorialMod.LOGGER.info("Registrar mods items for" + TutorialMod.MOD_ID);

        ItemGroupEvents
                .modifyEntriesEvent(ItemGroups.INGREDIENTS)
                .register(entries -> {
                    entries.add(KAKU_GEM);
                    entries.add(RC_CELLS);
                    entries.add(INVESTIGATORS_BLOOD);
                    entries.add(INVESTIGATORS_FLESH);
                });

    }
}