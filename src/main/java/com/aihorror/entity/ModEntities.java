package com.aihorror.entity;

import com.aihorror.AiHorror;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public class ModEntities {
    public static final ResourceKey<EntityType<?>> GLITCH_KEY = ResourceKey.create(Registries.ENTITY_TYPE, AiHorror.id("glitch"));
    public static EntityType<GlitchEntity> GLITCH_ENTITY;

    public static void initialize() {
        GLITCH_ENTITY = Registry.register(BuiltInRegistries.ENTITY_TYPE, GLITCH_KEY,
            EntityType.Builder.<GlitchEntity>of(GlitchEntity::new, MobCategory.MONSTER)
                .sized(0.6f, 1.95f)
                .eyeHeight(1.62f)
                .build(GLITCH_KEY)
        );
        net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry.register(GLITCH_ENTITY, GlitchEntity.createAttributes());
        AiHorror.LOGGER.info("[AiHorror] Entity registered: glitch");
    }
}
