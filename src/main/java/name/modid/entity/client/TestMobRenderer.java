package name.modid.entity.client;

import name.modid.TutorialMod;
import name.modid.entity.custom.TestMobEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.util.Identifier;

public class TestMobRenderer extends MobEntityRenderer<TestMobEntity, PlayerEntityModel<TestMobEntity>> {
    public TestMobRenderer(EntityRendererFactory.Context context) {
        // Usa el modelo de humanoide estándar de Minecraft (útil para pruebas)
        super(context, new PlayerEntityModel<>(context.getPart(EntityModelLayers.PLAYER), false), 0.5f);
    }

    @Override
    public Identifier getTexture(TestMobEntity entity) {
        // Ruta a tu archivo de textura .png
        return TutorialMod.id("textures/entity/test_mob.png");
    }
}
