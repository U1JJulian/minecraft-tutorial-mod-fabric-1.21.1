package name.modid.item;

import name.modid.TutorialMod;
import name.modid.entity.ModEntities;
import name.modid.item.custom.RinkakuSword;
import name.modid.item.custom.UpgradableKakuSword;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.*;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;

public class ModItems {

    //----Creacion de items--------
    public static final Item KAKU_GEM = registerItem("kaku_gem", new Item(new Item.Settings()));
    public static final Item RC_CELLS = registerItem("rc_cells", new Item(new Item.Settings()));
    public static final Item INVESTIGATORS_BLOOD = registerItem("investigators_blood", new Item(new Item.Settings()));
    public static final Item INVESTIGATORS_FLESH = registerItem("investigators_flesh", new Item(new Item.Settings()));
    public static final Item KAKUHOU_ORGAN = registerItem("kakuhou_organ", new Item(new Item.Settings()));

    //----Creacion de los tools------

    //Creacion de la espada
    // Espada Base (Evolucionable)
    public static final Item KAKUHOU_SWORD = registerItem("kakuhou_sword",
            new UpgradableKakuSword(ModToolMaterials.KAKU_GEM, new Item.Settings()
                    .attributeModifiers(SwordItem.createAttributeModifiers(ModToolMaterials.KAKU_GEM, 3, -2.4f)))
    );

    // Ejemplo de registro de las espadas
    public static final Item RINKAKU_SWORD = registerItem("rinkaku_sword",
            new RinkakuSword(ModToolMaterials.KAKU_GEM, new Item.Settings().attributeModifiers(SwordItem.createAttributeModifiers(ModToolMaterials.KAKU_GEM, 3, -2.4f))));

    public static final Item UKAKU_SWORD = registerItem("ukaku_sword",
            new SwordItem(ModToolMaterials.KAKU_GEM, new Item.Settings()
                    .attributeModifiers(SwordItem.createAttributeModifiers(ModToolMaterials.KAKU_GEM, 7, -2.0f)))
    );

    public static final Item BIKAKU_SWORD = registerItem("bikaku_sword",
            new SwordItem(ModToolMaterials.KAKU_GEM, new Item.Settings()
                    .attributeModifiers(SwordItem.createAttributeModifiers(ModToolMaterials.KAKU_GEM, 7, -2.0f)))
    );

    public static final Item KOUKAKU_SWORD = registerItem("koukaku_sword",
            new SwordItem(ModToolMaterials.KAKU_GEM, new Item.Settings()
                    .attributeModifiers(SwordItem.createAttributeModifiers(ModToolMaterials.KAKU_GEM, 7, -2.0f)))
    );

    //----Creacion de spawn(mobs)----
    public static final Item TUTORIAL_ENTITY_MOB_EGG = registerItem("tutorial_entity_mob_spawn_egg",
            new SpawnEggItem(ModEntities.MOBENTITY, 0x1A1A1A, 0X8B0000, new Item.Settings()));

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
                    entries.add(KAKUHOU_ORGAN);
                });

    }
}