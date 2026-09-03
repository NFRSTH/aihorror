package com.aihorror.client.renderer;

import com.aihorror.AiHorror;
import com.aihorror.entity.GlitchEntity;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.resources.Identifier;

public class GlitchEntityRenderer extends HumanoidMobRenderer<GlitchEntity, HumanoidRenderState, HumanoidModel<HumanoidRenderState>> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(AiHorror.MOD_ID, "textures/entity/glitch.png");
    private static final Identifier CREEPY = Identifier.fromNamespaceAndPath(AiHorror.MOD_ID, "textures/entity/glitch_creepy.png");

    public GlitchEntityRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new HumanoidModel<>(ctx.bakeLayer(ModelLayers.ZOMBIE)), 0.5f);
        // reuse zombie model but creepy texture
    }

    @Override
    public Identifier getTextureLocation(HumanoidRenderState state) {
        // flicker between textures for glitch effect
        return (System.currentTimeMillis()/200 % 2 == 0) ? TEXTURE : CREEPY;
    }

    @Override
    public HumanoidRenderState createRenderState() {
        return new HumanoidRenderState();
    }
}
