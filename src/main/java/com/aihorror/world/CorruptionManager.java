package com.aihorror.world;

import com.aihorror.AiHorror;
import com.aihorror.block.ModBlocks;
import com.aihorror.config.AiHorrorConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.Random;

public class CorruptionManager {
    private static final Random RANDOM = new Random();
    private static int tickCounter = 0;

    public static void initialize() {
        AiHorror.LOGGER.info("[AiHorror] Corruption manager ready (glitch blocks purple/black)");
    }

    public static void tick(MinecraftServer server) {
        if (!AiHorrorConfig.get().enabled || !AiHorrorConfig.get().allowWorldCorruption) return;
        tickCounter++;
        if (tickCounter % AiHorrorConfig.get().corruptionIntervalTicks != 0) return;
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            if (RANDOM.nextFloat() < 0.4f * AiHorrorConfig.get().intensityFactor()) {
                corruptAround(player, Math.min(10, AiHorrorConfig.get().maxCorruptionPerTick+2));
                maybeGenerateFoundFootage(player);
                if (AiHorrorConfig.get().allowBuildDestruction && RANDOM.nextFloat() < 0.2f * AiHorrorConfig.get().intensityFactor()) {
                    destroyBuildPart(player);
                }
            }
        }
    }

    public static void corruptAround(ServerPlayer player, int radius) {
        ServerLevel level = (ServerLevel) player.level();
        BlockPos center = player.blockPosition();
        int count = Math.min(AiHorrorConfig.get().maxCorruptionPerTick, 3 + RANDOM.nextInt(5));
        for (int i=0; i<count; i++) {
            int dx = RANDOM.nextInt(radius*2)-radius;
            int dz = RANDOM.nextInt(radius*2)-radius;
            int dy = RANDOM.nextInt(6)-3;
            BlockPos pos = center.offset(dx, dy, dz);
            if (!level.isLoaded(pos)) continue;
            var state = level.getBlockState(pos);
            if (state.isAir() || state.is(Blocks.BEDROCK) || state.is(Blocks.BARRIER)) continue;
            if (state.is(Blocks.GRASS_BLOCK) || state.is(Blocks.DIRT) || state.is(Blocks.STONE) || state.is(Blocks.COBBLESTONE) || state.is(Blocks.SAND)) {
                double r = RANDOM.nextDouble();

                if (r < 0.5) level.setBlock(pos, ModBlocks.CORRUPTED_BLOCK.defaultBlockState(), 3);
                else if (r < 0.75) level.setBlock(pos, Blocks.CRYING_OBSIDIAN.defaultBlockState(), 3);
                else level.setBlock(pos, Blocks.SOUL_SOIL.defaultBlockState(), 3);
            } else if (state.is(Blocks.TORCH) || state.is(Blocks.WALL_TORCH)) {
                level.destroyBlock(pos, false);
                level.playSound(null, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.6f, 0.8f);
            }
        }
        level.playSound(null, center, SoundEvents.AMBIENT_CAVE.value(), SoundSource.HOSTILE, 0.7f, 0.3f);
        if (RANDOM.nextFloat() < 0.3f) {
            player.sendSystemMessage(Component.literal("\u00A78[AI] \u00A77The world glitches..."));
        }
    }

    private static void maybeGenerateFoundFootage(ServerPlayer player) {
        if (!AiHorrorConfig.get().foundFootageEnabled) return;
        if (RANDOM.nextFloat() > 0.35f) return;
        ServerLevel level = (ServerLevel) player.level();
        BlockPos pos = findNearbyGround(player, 12);
        if (pos == null) return;

        level.setBlock(pos, Blocks.CHEST.defaultBlockState(), 3);
        var be = level.getBlockEntity(pos);
        if (be instanceof net.minecraft.world.level.block.entity.ChestBlockEntity chest) {
            String[][] stories = getFullStory();
            String[] story = stories[RANDOM.nextInt(stories.length)];
            for (int i=0; i<Math.min(story.length, chest.getContainerSize()); i++) {
                var paper = new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.PAPER);
                paper.set(net.minecraft.core.component.DataComponents.CUSTOM_NAME, Component.literal("\u00A78" + story[i]));
                chest.setItem(i, paper);
            }

            chest.setItem(4, new net.minecraft.world.item.ItemStack(com.aihorror.item.ModItems.CORRUPTED_TAPE));
        }
        player.sendSystemMessage(Component.literal("\u00A77\u00A7oYou found something... \u00A78[Found Footage: Full Story fragment at " + pos.toShortString() + "]"));
        AiHorror.LOGGER.info("[AiHorror] Full story footage at {}", pos);
    }

    private static String[][] getFullStory() {
        return new String[][]{
            {"LOG 01: Project AiHorror started", "We taught it to watch. It learned to hunt.", "Subject: Player - always goes left at cave"},
            {"LOG 02: It doesn''t sleep", "We haven''t slept in 3 days. Fear is rising.", "Time snapped day->night without command"},
            {"LOG 03: The smile", "It stood behind me, smiling with black eyes", "I only saw it when I checked the screenshot"},
            {"LOG 04: Ritual notes", "Gather 8 crying obsidian, 4 soul soil, 1 corrupted tape", "Place at midnight to weaken it, then kill"},
            {"LOG 05: The end", "If you read this, you are already being watched", "Don''t hide. It learns your hiding spots."},
            {"LOG 06: EMF 5 - PARANORMAL", "Scanner beeped INSIDE my house", "It was already inside"},
            {"LOG 07: Death feeds it", "When I died, the world corrupted more", "But that setting is off by default - use /aihorror config deathCorruption true to enable"}
        };
    }

    private static void destroyBuildPart(ServerPlayer player) {
        ServerLevel level = (ServerLevel) player.level();
        BlockPos center = player.blockPosition();
        int limit = AiHorrorConfig.get().allowBuildDestruction ? 1 : 0;
        if (limit==0) return;
        for (int i=0; i<6; i++) {
            int dx = RANDOM.nextInt(20)-10;
            int dz = RANDOM.nextInt(20)-10;
            int dy = RANDOM.nextInt(8)-2;
            BlockPos pos = center.offset(dx, dy, dz);
            if (!level.isLoaded(pos)) continue;
            var state = level.getBlockState(pos);
            if (state.is(Blocks.COBBLESTONE) || state.is(Blocks.OAK_PLANKS) || state.is(Blocks.STONE_BRICKS) || state.is(Blocks.GLASS) || state.is(Blocks.CHEST) || state.is(Blocks.FURNACE)) {
                level.destroyBlock(pos, false);
                level.playSound(null, pos, SoundEvents.ZOMBIE_ATTACK_WOODEN_DOOR, SoundSource.BLOCKS, 0.7f, 0.5f);
                if (RANDOM.nextFloat() < 0.4f) level.setBlock(pos, ModBlocks.CORRUPTED_BLOCK.defaultBlockState(), 3);
                player.sendSystemMessage(Component.literal("\u00A7c[AI] \u00A77Your build is being unmade at " + pos.toShortString() + " (moderate, tunable via /aihorror config)"));
                break;
            }
        }
    }

    private static BlockPos findNearbyGround(ServerPlayer player, int radius) {
        ServerLevel level = (ServerLevel) player.level();
        BlockPos origin = player.blockPosition();
        for (int i=0;i<15;i++) {
            int dx = RANDOM.nextInt(radius*2)-radius;
            int dz = RANDOM.nextInt(radius*2)-radius;
            BlockPos p = origin.offset(dx, 0, dz);
            p = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, p);
            if (level.getBlockState(p).isAir() && level.getBlockState(p.below()).isSolidRender()) return p;
        }
        return null;
    }
}
