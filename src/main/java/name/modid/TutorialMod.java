package name.modid;

import name.modid.block.ModBlocks;
import name.modid.entity.ModEntities;
import name.modid.item.ModItemGroups;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import name.modid.item.ModItems;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TutorialMod implements ModInitializer {

	public static final String MOD_ID = "tutorial-mod";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	// 1. Instancia de la partícula
	public static final SimpleParticleType RINKAKU_SWEEP = FabricParticleTypes.simple();

	@Override
	public void onInitialize() {
		ModItemGroups.registerItemGroups();

		ModItems.registerModItems();
		ModBlocks.registerModBlocks();

		ModEntities.registerModEntities();
		Registry.register(Registries.PARTICLE_TYPE, Identifier.of(MOD_ID, "rinkaku_sweep"), RINKAKU_SWEEP);
	}

	public static Identifier id(String path) {
		return Identifier.of(MOD_ID, path);
	}
}
