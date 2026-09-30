package name.modid;

import name.modid.entity.ModEntities;
import name.modid.entity.client.TestMobRenderer;
import name.modid.particle.RinkakuSweepParticle;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

public class TutorialModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // Le indica a Minecraft qué renderer usar para el Test Mob
        EntityRendererRegistry.register(ModEntities.MOBENTITY, TestMobRenderer::new);
        ParticleFactoryRegistry.getInstance().register(TutorialMod.RINKAKU_SWEEP, RinkakuSweepParticle.Factory::new);
    }
}
