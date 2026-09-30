package name.modid.entity;

import name.modid.TutorialMod;
import name.modid.entity.custom.TestMobEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class ModEntities {

    public static final EntityType<TestMobEntity> MOBENTITY =
            Registry.register(Registries.ENTITY_TYPE,
                    TutorialMod.id("entity_mob"),
                    EntityType.Builder.create(TestMobEntity::new, SpawnGroup.MONSTER)
                            .dimensions(0.6f, 1.9f)
                            .build()
            );


    public static void registerModEntities(){
        TutorialMod.LOGGER.info("Registering Mod Entities for "+ TutorialMod.MOD_ID);

        //Registra los atributos de vida, daño, etc.
        FabricDefaultAttributeRegistry.register(MOBENTITY, TestMobEntity.createTestMobEntitiAttributes());
    }
}
