package com.aihorror.world;

import com.aihorror.AiHorror;
import com.aihorror.block.ModBlocks;
import com.aihorror.config.AiHorrorConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.WrittenBookContent;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.server.network.Filterable;

public class CorruptionManager {
    private static final Random RANDOM = new Random();
    private static int tickCounter = 0;

    public static void initialize() {
        AiHorror.LOGGER.info("[AiHorror] Corruption manager ready (glitch blocks purple/black)");
    }

    public static void tick(MinecraftServer server) {
        if (!AiHorrorConfig.get().enabled || !AiHorrorConfig.get().allowWorldCorruption) return;
        if (AiHorrorConfig.get().isBanished()) return;
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
        if (AiHorrorConfig.get().isBanished()) return;
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
        BlockPos chestPos = findNearbyGround(player, 12);
        if (chestPos == null) return;
        level.setBlock(chestPos, Blocks.CHEST.defaultBlockState(), 3);
        var be = level.getBlockEntity(chestPos);
        if (be instanceof net.minecraft.world.level.block.entity.ChestBlockEntity chest) {
            String[][] stories = getFullStory();
            String[] story = stories[RANDOM.nextInt(stories.length)];
            BlockPos glitchPos = findGlitchHideout(player, 40);
            List<net.minecraft.network.chat.Component> pages = new ArrayList<>();
            StringBuilder page1 = new StringBuilder();
            page1.append("FOUND FOOTAGE - FRAGMENT\n\n");
            for (String line : story) page1.append(line).append("\n");
            page1.append("\n---\nCoords of next glitch:\n").append(glitchPos.toShortString()).append("\nSeed trace: ").append(level.getSeed() ^ AiHorrorConfig.get().worldSeedSalt);
            pages.add(Component.literal(page1.toString()));
            if (glitchPos != null) {
                pages.add(Component.literal("Next glitch hides at:\n" + glitchPos.getX() + " " + glitchPos.getY() + " " + glitchPos.getZ() + "\n\nFollow the corrupted blocks...\nEMF will spike near it."));
            }
            ItemStack book = new ItemStack(Items.WRITTEN_BOOK);
            WrittenBookContent content = new WrittenBookContent(Filterable.passThrough("Found Footage"), "AiHorror", 0, pages.stream().map(c -> Filterable.passThrough((Component)c)).toList(), true);
            book.set(DataComponents.WRITTEN_BOOK_CONTENT, content);
            chest.setItem(0, book);
            for (int i=1; i<Math.min(story.length+1, chest.getContainerSize()-1); i++) {
                var paper = new ItemStack(Items.PAPER);
                paper.set(DataComponents.CUSTOM_NAME, Component.literal("\u00A78" + story[i-1]));
                chest.setItem(i, paper);
            }
            chest.setItem(chest.getContainerSize()-1, new ItemStack(com.aihorror.item.ModItems.CORRUPTED_TAPE));
        }
        player.sendSystemMessage(Component.literal("\u00A77\u00A7oYou found something... \u00A78[Found Footage at " + chestPos.toShortString() + " - Written Book with glitch coords]"));
        AiHorror.LOGGER.info("[AiHorror] Full story footage at {} glitch at {}", chestPos, findGlitchHideout(player, 40));
    }

    private static BlockPos findGlitchHideout(ServerPlayer player, int radius) {
        ServerLevel level = (ServerLevel) player.level();
        for (int i=0;i<10;i++) {
            int dx = RANDOM.nextInt(radius*2)-radius;
            int dz = RANDOM.nextInt(radius*2)-radius;
            BlockPos p = player.blockPosition().offset(dx, 0, dz);
            p = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, p);
            if (level.getBlockState(p.below()).isSolidRender()) return p;
        }
        return player.blockPosition().offset(5,0,5);
    }

    private static String[][] getFullStory() {
        return new String[][]{
            {"LOG 01: Project AiHorror started", "We taught it to watch. It learned to hunt.", "Subject: Player - always goes left at cave"},
            {"LOG 02: It doesn''t sleep", "We haven''t slept in 3 days. Fear is rising.", "Time snapped day->night without command"},
            {"LOG 03: The smile", "It stood behind me, smiling with black eyes", "I only saw it when I checked the screenshot"},
            {"LOG 04: Ritual notes", "Gather 8 corrupted blocks around you, 1 corrupted tape in hand", "Place at midnight to banish AI 10 minutes, then kill"},
            {"LOG 05: The end", "If you read this, you are already being watched", "Don''t hide. It learns your hiding spots."},
            {"LOG 06: EMF 5 - PARANORMAL", "Scanner beeped INSIDE my house", "It was already inside"},
            {"LOG 07: Death feeds it", "When I died, the world corrupted more", "But that setting is off by default - use /aihorror config deathCorruption true to enable"},
            {"LOG 08: The veins", "Corruption spreads like veins through stone", "Follow the purple to find it"},
            {"LOG 09: Redstone lies", "Lights flicker when it is near", "If torches die, it is close"},
            {"LOG 10: Fake friends", "Chat is not safe. It mimics your friends", "Fake join messages are its favorite trick"}
        };
    }

    private static void destroyBuildPart(ServerPlayer player) {
        ServerLevel level = (ServerLevel) player.level();
        BlockPos center = player.blockPosition();
        int limit = AiHorrorConfig.get().allowBuildDestruction ? 1 : 0;
        if (limit==0) return;
        if (AiHorrorConfig.get().isBanished()) return;
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
