package com.aihorror.util;

import com.aihorror.ai.SurveillanceAI;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.level.ServerPlayer;

public class HorrorEvents {
    public static void register() {
        PlayerBlockBreakEvents.AFTER.register((level, player, pos, state, blockEntity) -> {
            if (player instanceof ServerPlayer sp) {
                SurveillanceAI.getInstance().handleBlockBreak(sp);
            }
        });
        net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayer p : server.getPlayerList().getPlayers()) {
                if (p.isSleeping()) {
                    SurveillanceAI.getInstance().handlePlayerSleep(p);
                }
            }

            if (server.overworld() != null) {
                SurveillanceAI.getInstance().setWorldSeed(server.overworld().getSeed());
            }
        });
        ServerLivingEntityEvents.AFTER_DEATH.register((entity, source) -> {
            if (entity instanceof ServerPlayer sp) {
                SurveillanceAI.getInstance().handlePlayerDeath(sp);
            }
        });

        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            SurveillanceAI.getInstance().removeProfile(handler.getPlayer().getUUID());
        });
    }
}
