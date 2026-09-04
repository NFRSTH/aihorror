package com.aihorror.world;

import com.aihorror.AiHorror;
import com.aihorror.block.ModBlocks;
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
        if (AiHorrorConfig.get().isBanished()) {
            player.sendSystemMessage(Component.literal("\u00A7e[Ritual] AI is already banished for " + (AiHorrorConfig.get().banishTicksRemaining/20) + "s"));
            return false;
        }
        ServerLevel level = (ServerLevel) player.level();
        BlockPos pos = player.blockPosition().below();
        boolean hasTape = player.getMainHandItem().is(com.aihorror.item.ModItems.CORRUPTED_TAPE) || player.getOffhandItem().is(com.aihorror.item.ModItems.CORRUPTED_TAPE);
        if (!hasTape) {
            player.sendSystemMessage(Component.literal("\u00A7c[Ritual] Need corrupted tape in hand"));
            return false;
        }
        long time = level.getGameTime() % 24000;
        boolean isMidnight = time > 17000 && time < 19000;
        if (!isMidnight) {
            player.sendSystemMessage(Component.literal("\u00A78[Ritual] Must be midnight (use /time set midnight or wait) Current: " + time));
            return false;
        }
        int corruptedCount = 0;
        int soulCount = 0;
        int obsidianCount = 0;
        for (int dx=-2; dx<=2; dx++) for (int dz=-2; dz<=2; dz++) {
            BlockPos p = pos.offset(dx,0,dz);
            if (level.getBlockState(p).is(ModBlocks.CORRUPTED_BLOCK)) corruptedCount++;
            if (level.getBlockState(p).is(Blocks.SOUL_SOIL)) soulCount++;
            if (level.getBlockState(p).is(Blocks.CRYING_OBSIDIAN)) obsidianCount++;
        }
        if (corruptedCount < 8) {
            if (obsidianCount >= 8) {
                corruptedCount = obsidianCount;
            } else {
                player.sendSystemMessage(Component.literal("\u00A78[Ritual] Need 8 corrupted blocks around you. Found: " + corruptedCount + "/8 (or 8 crying obsidian)"));
                return false;
            }
        }
        AiHorrorConfig.get().setBanished(12000);
        player.sendSystemMessage(Component.literal("\u00A7a[Ritual] AI banished for 10 minutes! All horror paused. Glitches will despawn."));
        level.playSound(null, pos, SoundEvents.BEACON_ACTIVATE, SoundSource.HOSTILE, 2.0f, 0.5f);
        level.playSound(null, pos, SoundEvents.WARDEN_SONIC_BOOM, SoundSource.HOSTILE, 1.0f, 0.3f);
        player.addEffect(new net.minecraft.world.effect.MobEffectInstance(net.minecraft.world.effect.MobEffects.GLOWING, 600, 0));
        player.addEffect(new net.minecraft.world.effect.MobEffectInstance(net.minecraft.world.effect.MobEffects.REGENERATION, 200, 1));
        for (var e : level.getEntitiesOfClass(com.aihorror.entity.GlitchEntity.class, player.getBoundingBox().inflate(80))) {
            e.discard();
        }
        var e = com.aihorror.entity.ModEntities.GLITCH_ENTITY.create(level, net.minecraft.world.entity.EntitySpawnReason.EVENT);
        if (e != null) {
            e.snapTo(pos.getX()+0.5, pos.getY()+1, pos.getZ()+0.5, 0,0);
            e.setHealth(1);
            e.setTargetPlayer(player);
            level.addFreshEntity(e);
            player.sendSystemMessage(Component.literal("\u00A78One last glitch spawns with 1 HP - kill it while banished!"));
        }
        if (player.getMainHandItem().is(com.aihorror.item.ModItems.CORRUPTED_TAPE)) player.getMainHandItem().shrink(1);
        else player.getOffhandItem().shrink(1);
        for (int dx=-2; dx<=2; dx++) for (int dz=-2; dz<=2; dz++) {
            BlockPos p = pos.offset(dx,0,dz);
            if (level.getBlockState(p).is(ModBlocks.CORRUPTED_BLOCK) && Math.random()<0.5) level.destroyBlock(p, false);
        }
        AiHorror.LOGGER.info("[AiHorror] Ritual success for {} banished 12000 ticks", player.getName().getString());
        return true;
    }
}
