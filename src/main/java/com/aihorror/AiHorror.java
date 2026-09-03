package com.aihorror;

import com.aihorror.ai.SurveillanceAI;
import com.aihorror.block.ModBlocks;
import com.aihorror.command.AiHorrorCommands;
import com.aihorror.config.AiHorrorConfig;
import com.aihorror.entity.ModEntities;
import com.aihorror.item.ModItems;
import com.aihorror.sound.ModSounds;
import com.aihorror.world.CorruptionManager;
import com.aihorror.ai.SurveillanceAI;
import com.aihorror.util.HorrorEvents;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AiHorror implements ModInitializer {
    public static final String MOD_ID = "aihorror";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }

    @Override
    public void onInitialize() {
        LOGGER.info("[AiHorror] Initializing surveillance AI horror for 26.2");
        AiHorrorConfig.load();
        ModSounds.initialize();
        ModBlocks.initialize();
        ModItems.initialize();
        ModEntities.initialize();
        CorruptionManager.initialize();
        HorrorEvents.register();

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, env) -> AiHorrorCommands.register(dispatcher));

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            SurveillanceAI.getInstance().tick(server);
            CorruptionManager.tick(server);
        });

        LOGGER.info("[AiHorror] AI is watching...");
    }
}


