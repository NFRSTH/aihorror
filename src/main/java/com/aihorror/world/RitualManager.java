package com.aihorror.world;

import com.aihorror.AiHorror;
import com.aihorror.config.AiHorrorConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.Blocks;

public class RitualManager {
    public static boolean tryRitual(ServerPlayer player) {
        ServerLevel level = (ServerLevel) player.level();
        BlockPos pos = player.blockPosition().below();
        // Check ritual: 8 crying obsidian in ring, 4 soul soil corners, player holding corrupted tape
        // Simplified: check if player has tape and is at midnight with 4 crying obsidian nearby
        boolean hasTape = player.getMainHandItem().is(com.aihorror.item.ModItems.CORRUPTED_TAPE) || player.getOffhandItem().is(com.aihorror.item.ModItems.CORRUPTED_TAPE);
        if (!hasTape) {
            player.sendSystemMessage(Component.literal("\u00A7c[Ritual] Need corrupted tape in hand"));
            return false;
        }
        long time = level.getGameTime() % 24000;
        boolean isMidnight = time > 17000 && time < 19000;
        if (!isMidnight) {
            player.sendSystemMessage(Component.literal("\u00A78[Ritual] Must be midnight (use /time set midnight or wait)"));
            return false;
        }
        int obsidianCount = 0;
        for (int dx=-2; dx<=2; dx++) for (int dz=-2; dz<=2; dz++) {
            BlockPos p = pos.offset(dx,0,dz);
            if (level.getBlockState(p).is(Blocks.CRYING_OBSIDIAN)) obsidianCount++;
        }
        if (obsidianCount < 4) {
            player.sendSystemMessage(Component.literal("\u00A78[Ritual] Need 4+ crying obsidian around you. Found: " + obsidianCount));
            return false;
        }
        // Success: weaken AI
        player.sendSystemMessage(Component.literal("\u00A7a[Ritual] AI weakened! Now you can kill the Glitch!"));
        level.playSound(null, pos, SoundEvents.BEACON_ACTIVATE, SoundSource.HOSTILE, 2.0f, 0.5f);
        // Give effect and spawn weakened glitch that can be killed
        player.addEffect(new net.minecraft.world.effect.MobEffectInstance(net.minecraft.world.effect.MobEffects.GLOWING, 600, 0));
        // Spawn ritual glitch
        var e = com.aihorror.entity.ModEntities.GLITCH_ENTITY.create(level, net.minecraft.world.entity.EntitySpawnReason.EVENT);
        if (e != null) {
            e.snapTo(pos.getX(), pos.getY()+1, pos.getZ(), 0,0);
            e.setTargetPlayer(player);
            e.setHealth(20); // weakened
            level.addFreshEntity(e);
        }
        // consume tape
        if (player.getMainHandItem().is(com.aihorror.item.ModItems.CORRUPTED_TAPE)) player.getMainHandItem().shrink(1);
        else player.getOffhandItem().shrink(1);
        AiHorror.LOGGER.info("[AiHorror] Ritual success for {}", player.getName().getString());
        return true;
    }
}