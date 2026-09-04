package com.aihorror.client;

import com.aihorror.client.renderer.GlitchEntityRenderer;
import com.aihorror.entity.ModEntities;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.renderer.entity.EntityRenderers;

public class AiHorrorClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityRenderers.register(ModEntities.GLITCH_ENTITY, GlitchEntityRenderer::new);
    }
}
