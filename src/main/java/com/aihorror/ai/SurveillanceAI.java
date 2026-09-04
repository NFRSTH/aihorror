package com.aihorror.ai;

import com.aihorror.AiHorror;
import com.aihorror.config.AiHorrorConfig;
import com.aihorror.entity.ModEntities;
import com.aihorror.sound.ModSounds;
import com.aihorror.world.CorruptionManager;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.LightLayer;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class SurveillanceAI {
    private static final SurveillanceAI INSTANCE = new SurveillanceAI();
    public static SurveillanceAI getInstance() { return INSTANCE; }

    private final Map<UUID, PlayerProfile> profiles = new HashMap<>();
    private int globalTick = 0;
    private long worldSeed = 0;
    private Random seedRandom = new Random();

    private static class PlayerProfile {
        int actions = 0;
        int hideCount = 0;
        BlockPos lastPos = BlockPos.ZERO;
        int ticksAlone = 0;
        int ticksStared = 0;
        int fearLevel = 0;
        long lastSeenGlitchTick = -10000;
        int ticksSinceSleep = 0;
        int jumpscareCooldown = 0;
        int deathCount = 0;
    }

    private String glitchText(String s) {

        StringBuilder sb = new StringBuilder();
        Random r = new Random();
        for (char c : s.toCharArray()) {
            if (r.nextFloat() < 0.12f) sb.append("ÔûêÔûôÔûÆÔûæ".charAt(r.nextInt(4)));
            else if (r.nextFloat() < 0.08f) sb.append(Character.toString(c).toUpperCase());
            else sb.append(c);
            if (r.nextFloat() < 0.04f) sb.append(" ");
        }
        return sb.toString();
    }

    public void onPlayerAction(ServerPlayer player, String action) {
        if (!AiHorrorConfig.get().enabled) return;
        PlayerProfile p = profiles.computeIfAbsent(player.getUUID(), k -> new PlayerProfile());
        p.actions++;
        p.fearLevel = Math.min(100, p.fearLevel + 2);
        float factor = AiHorrorConfig.get().intensityFactor();

        if (p.ticksSinceSleep > 24000) {
            p.fearLevel = Math.min(100, p.fearLevel + AiHorrorConfig.get().sleepDeprivationFactor);
        }

        ServerLevel lvl = (ServerLevel) player.level();
        int light = lvl.getBrightness(LightLayer.BLOCK, player.blockPosition());
        boolean isDark = light <= 5;
        boolean canHunt = isDark || AiHorrorConfig.get().isMaxIntensity();
        if (!canHunt) return;
        if (p.fearLevel > 30 * factor && randomChance(0.15f * factor)) {
            triggerSurveillanceEvent(player, p);
        }
    }

    public void tick(MinecraftServer server) {
        if (!AiHorrorConfig.get().enabled) return;

        if (worldSeed == 0) {
            try {
                worldSeed = server.overworld().getSeed() ^ AiHorrorConfig.get().worldSeedSalt;
                seedRandom = new Random(worldSeed);
                AiHorror.LOGGER.info("[AiHorror] Seed personality: {}", worldSeed);
            } catch (Exception e) { worldSeed = System.currentTimeMillis(); }
        }
        globalTick++;
        if (globalTick % AiHorrorConfig.get().surveillanceTickInterval != 0) return;
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            PlayerProfile p = profiles.computeIfAbsent(player.getUUID(), k -> new PlayerProfile());
            ServerLevel level = (ServerLevel) player.level();
            p.ticksSinceSleep++;
            if (p.jumpscareCooldown > 0) p.jumpscareCooldown--;


            int light = level.getBrightness(LightLayer.BLOCK, player.blockPosition());
            boolean isDark = light <= 5;
            boolean canHunt = isDark || AiHorrorConfig.get().isMaxIntensity();
            if (!canHunt) continue;

            boolean alone = server.getPlayerList().getPlayers().stream()
                    .filter(other -> other != player)
                    .noneMatch(other -> other.level() == level && other.distanceTo(player) < 50);
            if (alone) p.ticksAlone++; else p.ticksAlone = 0;

            if (player.blockPosition().equals(p.lastPos)) {
                int blockLight = level.getBrightness(LightLayer.BLOCK, player.blockPosition());
                if (blockLight <= 3) p.hideCount++;
                else p.hideCount = 0;
            }
            p.lastPos = player.blockPosition().immutable();


            if (p.hideCount > 200 && globalTick % 100 == 0) {
                player.sendSystemMessage(Component.literal(glitchText("[AI] I see you hiding...")));
                p.fearLevel = Math.min(100, p.fearLevel + 10);
                p.hideCount = 0;
                if (randomChance(0.7f * AiHorrorConfig.get().intensityFactor())) {

                    long glitchCount = level.getEntitiesOfClass(com.aihorror.entity.GlitchEntity.class, player.getBoundingBox().inflate(80)).size();
                    if (glitchCount < AiHorrorConfig.get().maxGlitchEntities) spawnGlitchNear(player);
                }
            }

            if (p.ticksAlone > 600) {
                triggerAloneEvent(player, p);
                p.ticksAlone = 0;
            }


            if (p.ticksSinceSleep > 48000) {
                p.fearLevel = Math.min(100, p.fearLevel + 1);
                if (globalTick % 400 == 0) whisper(player, glitchText("You need to sleep... but I won''t let you"));
            }

            float horrorChance = (p.fearLevel / 100f) * AiHorrorConfig.get().intensityFactor() * 0.06f;

            horrorChance *= (0.8f + seedRandom.nextFloat()*0.4f);
            if (randomChance(horrorChance) && p.jumpscareCooldown==0) {
                triggerSurveillanceEvent(player, p);
            }

            if (globalTick % 200 == 0 && p.fearLevel > 0) p.fearLevel--;

            if (player.getRandom().nextFloat() < 0.005f * AiHorrorConfig.get().intensityFactor()) {
                handleStare(player, p);
            }
        }
    }

    private void triggerSurveillanceEvent(ServerPlayer player, PlayerProfile p) {

        ServerLevel lvl = (ServerLevel) player.level();
        long glitchCount = lvl.getEntitiesOfClass(com.aihorror.entity.GlitchEntity.class, player.getBoundingBox().inflate(80)).size();
        int r = player.getRandom().nextInt(10);

        if (r == 4 && p.jumpscareCooldown > 0) r = (r+1)%10;
        switch (r) {
            case 0 -> whisper(player, glitchText("I am watching you, " + player.getName().getString() + "..."));
            case 1 -> playSound(player, SoundEvents.AMBIENT_CAVE.value(), 1.5f, 0.5f + seedRandom.nextFloat()*0.2f);
            case 2 -> timeGlitch(player);
            case 3 -> fakeDoorSound(player);
            case 4 -> { if (AiHorrorConfig.get().jumpscaresEnabled) { jumpscare(player); p.jumpscareCooldown = AiHorrorConfig.get().jumpscareCooldownTicks; } }
            case 5 -> { if (glitchCount < AiHorrorConfig.get().maxGlitchEntities) spawnGlitchNear(player); else whisper(player, glitchText("Too many eyes...")); }
            case 6 -> corruptNearby(player);
            case 7 -> blindnessFlicker(player);
            case 8 -> whisper(player, glitchText("Don''t look behind you."));
            case 9 -> inventoryShuffle(player);
        }
        p.fearLevel = Math.min(100, p.fearLevel + 3);
    }

    private void triggerAloneEvent(ServerPlayer player, PlayerProfile p) {
        whisper(player, glitchText("You shouldn''t be alone..."));
        playSound(player, SoundEvents.WARDEN_HEARTBEAT, 1.0f, 0.6f);
    }

    private void handleStare(ServerPlayer player, PlayerProfile p) {
        p.ticksStared++;
        if (p.ticksStared > 3) {
            whisper(player, glitchText("Stop staring at me."));
            spawnGlitchNear(player);
            p.ticksStared = 0;
        }
    }

    private void whisper(ServerPlayer player, String msg) {

        if (AiHorrorConfig.get().shaderGlitchEnabled && player.getRandom().nextFloat() < 0.4f) {
            player.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 80, 0));
            player.addEffect(new MobEffectInstance(MobEffects.NAUSEA, 100, 0));
        }
        player.sendSystemMessage(Component.literal(msg));
        if (AiHorrorConfig.get().jumpscaresEnabled && player.getRandom().nextFloat() < 0.25f) {
            player.connection.send(new net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket(Component.literal("\u00A74\u00A7l" + glitchText("HE SEES YOU"))));
            player.connection.send(new net.minecraft.network.protocol.game.ClientboundSetTitlesAnimationPacket(5, 40, 10));
        }
    }

    private void playSound(ServerPlayer player, net.minecraft.sounds.SoundEvent event, float vol, float pitch) {
        ServerLevel lvl = (ServerLevel) player.level();

        float editedPitch = pitch * (0.8f + player.getRandom().nextFloat()*0.4f);
        lvl.playSound(null, player.blockPosition(), event, SoundSource.HOSTILE, vol, editedPitch);
    }

    private void timeGlitch(ServerPlayer player) {

        player.sendSystemMessage(Component.literal(glitchText("*Time snaps*")));

        ServerLevel level = (ServerLevel) player.level();
        level.playSound(null, player.blockPosition(), SoundEvents.PORTAL_AMBIENT, SoundSource.HOSTILE, 0.8f, 0.3f);
        addEffect(player, MobEffects.DARKNESS, 100, 0);
        if (AiHorrorConfig.get().allowTimeManipulation) {

            if (player.getRandom().nextBoolean()) {

                level.playSound(null, player.blockPosition(), SoundEvents.AMBIENT_CAVE.value(), SoundSource.HOSTILE, 1.0f, 0.2f);
            } else {

                level.playSound(null, player.blockPosition(), SoundEvents.BEACON_ACTIVATE, SoundSource.HOSTILE, 1.0f, 1.5f);
            }
        }
    }

    private void fakeDoorSound(ServerPlayer player) {
        ServerLevel lvl = (ServerLevel) player.level();
        BlockPos pos = player.blockPosition().offset(player.getRandom().nextInt(10)-5, 0, player.getRandom().nextInt(10)-5);
        lvl.playSound(null, pos, SoundEvents.WOODEN_DOOR_OPEN, SoundSource.BLOCKS, 1.0f, 0.7f + player.getRandom().nextFloat()*0.5f);
        lvl.playSound(null, pos, SoundEvents.WOODEN_DOOR_CLOSE, SoundSource.BLOCKS, 1.0f, 0.7f);
    }

    private void jumpscare(ServerPlayer player) {
        if (!AiHorrorConfig.get().jumpscaresEnabled) return;
        player.connection.send(new net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket(Component.literal("\u00A74\u2588\u2593\u2592\u2591 " + glitchText("JUMPSCARE") + " \u2591\u2592\u2593\u2588")));
        player.connection.send(new net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket(Component.literal("\u00A7cThe AI is behind you")));
        player.connection.send(new net.minecraft.network.protocol.game.ClientboundSetTitlesAnimationPacket(2, 20, 5));
        playSound(player, SoundEvents.WARDEN_ROAR, 3.0f, 0.4f);
        playSound(player, SoundEvents.ELDER_GUARDIAN_CURSE, 2.0f, 0.5f);
        addEffect(player, MobEffects.BLINDNESS, 40, 0);
        addEffect(player, MobEffects.SLOWNESS, 40, 2);
        addEffect(player, MobEffects.NAUSEA, 80, 0);
        ServerLevel lvl = (ServerLevel) player.level();
        lvl.playSound(null, player.blockPosition(), ModSounds.JUMPSCARE_EVENT, SoundSource.HOSTILE, 2.0f, 1.0f);
    }

    private void blindnessFlicker(ServerPlayer player) {
        addEffect(player, MobEffects.DARKNESS, 60, 0);
        if (AiHorrorConfig.get().shaderGlitchEnabled) addEffect(player, MobEffects.NAUSEA, 60, 0);
        playSound(player, SoundEvents.AMBIENT_CAVE.value(), 1.0f, 0.2f);
    }

    private void corruptNearby(ServerPlayer player) {
        if (!AiHorrorConfig.get().allowWorldCorruption) return;

        CorruptionManager.corruptAround(player, Math.min(6, AiHorrorConfig.get().maxCorruptionPerTick));
    }

    private void inventoryShuffle(ServerPlayer player) {
        if (!AiHorrorConfig.get().allowBuildDestruction) return;
        var inv = player.getInventory();
        int a = player.getRandom().nextInt(36);
        int b = player.getRandom().nextInt(36);
        var stackA = inv.getItem(a).copy();
        inv.setItem(a, inv.getItem(b).copy());
        inv.setItem(b, stackA);
        whisper(player, glitchText("Your inventory feels... wrong."));
    }

    private void spawnGlitchNear(ServerPlayer player) {
        PlayerProfile prof = profiles.get(player.getUUID());
        if (prof != null && globalTick - prof.lastSeenGlitchTick < 200) return;
        BlockPos pos = findSpawnPosNear(player, 8, 15);
        if (pos == null) return;
        ServerLevel lvl = (ServerLevel) player.level();
        long count = lvl.getEntitiesOfClass(com.aihorror.entity.GlitchEntity.class, player.getBoundingBox().inflate(80)).size();
        if (count >= AiHorrorConfig.get().maxGlitchEntities) return;
        var entity = ModEntities.GLITCH_ENTITY.create(lvl, net.minecraft.world.entity.EntitySpawnReason.EVENT);
        if (entity != null) {
            entity.snapTo(pos.getX()+0.5, pos.getY(), pos.getZ()+0.5, player.getYRot(), 0);
            entity.setTargetPlayer(player);
            entity.setInvisibleTicks(60);
            lvl.addFreshEntity(entity);
            if (prof != null) prof.lastSeenGlitchTick = globalTick;
            AiHorror.LOGGER.info("[AiHorror] Spawned glitch near {} at {} seed {}", player.getName().getString(), pos, worldSeed);
        }
    }

    private BlockPos findSpawnPosNear(ServerPlayer player, int minDist, int maxDist) {
        BlockPos origin = player.blockPosition();
        ServerLevel lvl = (ServerLevel) player.level();
        for (int i=0;i<10;i++) {
            int dx = player.getRandom().nextInt(maxDist*2)-maxDist;
            int dz = player.getRandom().nextInt(maxDist*2)-maxDist;
            if (Math.abs(dx) < minDist && Math.abs(dz) < minDist) continue;
            BlockPos p = origin.offset(dx, 0, dz);
            p = lvl.getHeightmapPos(net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, p);
            if (lvl.getBlockState(p.below()).isSolidRender()) return p;
        }
        return null;
    }

    private void addEffect(ServerPlayer player, net.minecraft.core.Holder<net.minecraft.world.effect.MobEffect> effect, int duration, int amp) {
        player.addEffect(new MobEffectInstance(effect, duration, amp, false, false));
    }

    private boolean randomChance(float chance) {
        return Math.random() < chance;
    }

    public void handlePlayerSleep(ServerPlayer player) {
        PlayerProfile p = profiles.computeIfAbsent(player.getUUID(), k -> new PlayerProfile());
        p.ticksSinceSleep = 0;
        onPlayerAction(player, "sleep");
        if (p.fearLevel > 20) {
            whisper(player, glitchText("You can''t sleep while I watch."));
            ServerLevel lvl = (ServerLevel) player.level();
            lvl.playSound(null, player.blockPosition(), SoundEvents.WARDEN_SONIC_BOOM, SoundSource.HOSTILE, 1.0f, 0.7f);
            spawnGlitchNear(player);
            player.stopSleeping();
        }
    }

    public void handleBlockBreak(ServerPlayer player) {
        onPlayerAction(player, "break");
        if (AiHorrorConfig.get().allowWorldCorruption && randomChance(0.03f * AiHorrorConfig.get().intensityFactor())) {
            corruptNearby(player);
        }
    }

    public void handleLookAtGlitch(ServerPlayer player) {
        PlayerProfile p = profiles.computeIfAbsent(player.getUUID(), k -> new PlayerProfile());
        p.ticksStared++;
        if (p.ticksStared > 2) {
            jumpscare(player);
            p.ticksStared = 0;
        }
    }

    public void handlePlayerDeath(ServerPlayer player) {
        PlayerProfile p = profiles.computeIfAbsent(player.getUUID(), k -> new PlayerProfile());
        p.deathCount++;
        p.fearLevel = Math.min(100, p.fearLevel + 5);
        if (AiHorrorConfig.get().allowDeathCorruption) {

            CorruptionManager.corruptAround(player, 12);
            player.sendSystemMessage(Component.literal(glitchText("[AI] Your death feeds me... world corrupts")));
        } else {
            player.sendSystemMessage(Component.literal(glitchText("[AI] Death is not escape")));
        }
    }

    public void setWorldSeed(long seed) { this.worldSeed = seed; this.seedRandom = new Random(seed); }

    public void removeProfile(UUID uuid) { profiles.remove(uuid); }
}
