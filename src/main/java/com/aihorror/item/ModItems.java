package com.aihorror.item;

import com.aihorror.AiHorror;
import com.aihorror.config.AiHorrorConfig;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

public class ModItems {
    public static Item SCANNER;
    public static Item EMF_READER;
    public static Item CORRUPTED_TAPE;

    private static ResourceKey<Item> key(String name) {
        return ResourceKey.create(Registries.ITEM, AiHorror.id(name));
    }

    private static Item register(String name, java.util.function.Function<Item.Properties, Item> factory, Item.Properties props) {
        ResourceKey<Item> k = key(name);
        Item item = factory.apply(props.setId(k));
        Registry.register(BuiltInRegistries.ITEM, k, item);
        return item;
    }

    public static void initialize() {
        SCANNER = register("scanner", ScannerItem::new, new Item.Properties().stacksTo(1));
        EMF_READER = register("emf_reader", EmfReaderItem::new, new Item.Properties().stacksTo(1));
        CORRUPTED_TAPE = register("corrupted_tape", Item::new, new Item.Properties().stacksTo(16));
        AiHorror.LOGGER.info("[AiHorror] Items registered");
    }

    public static class ScannerItem extends Item {
        public ScannerItem(Properties props) { super(props); }
        @Override
        public InteractionResult use(Level level, Player player, InteractionHand hand) {
            if (level.isClientSide()) return InteractionResult.PASS;
            if (!AiHorrorConfig.get().counterItemsEnabled) {
                player.sendSystemMessage(Component.literal("\u00A7c[AI] \u00A77Scanners disabled by config"));
                return InteractionResult.FAIL;
            }
            var box = new AABB(player.blockPosition()).inflate(64);
            var entities = level.getEntitiesOfClass(com.aihorror.entity.GlitchEntity.class, box);
            if (entities.isEmpty()) {
                player.sendSystemMessage(Component.literal("\u00A7a[Scanner] \u00A77No AI signal detected. \u00A78(You are safe... for now)"));
                level.playSound(null, player.blockPosition(), SoundEvents.NOTE_BLOCK_CHIME.value(), SoundSource.PLAYERS, 0.7f, 1.2f);
            } else {
                var e = entities.get(0);
                double dist = player.distanceTo(e);
                String threat = dist < 10 ? "\u00A7c\u00A7lCRITICAL" : dist < 30 ? "\u00A7eHIGH" : "\u00A76MEDIUM";
                player.sendSystemMessage(Component.literal("\u00A7c[Scanner] \u00A77Signal: " + threat + " \u00A77(" + String.format("%.1f", dist) + "m) \u00A78Glitch at " + e.blockPosition().toShortString()));
                level.playSound(null, player.blockPosition(), SoundEvents.NOTE_BLOCK_DIDGERIDOO.value(), SoundSource.HOSTILE, 1.0f, 0.5f);
                e.addEffect(new net.minecraft.world.effect.MobEffectInstance(net.minecraft.world.effect.MobEffects.GLOWING, 200, 0));
            }
            player.getCooldowns().addCooldown(new net.minecraft.world.item.ItemStack(this), 40);
            return InteractionResult.SUCCESS;
        }
    }

    public static class EmfReaderItem extends Item {
        public EmfReaderItem(Properties props) { super(props); }
        @Override
        public InteractionResult use(Level level, Player player, InteractionHand hand) {
            if (level.isClientSide()) return InteractionResult.PASS;
            if (!AiHorrorConfig.get().counterItemsEnabled) return InteractionResult.FAIL;
            int emf = 0;
            var pos = player.blockPosition();
            for (int dx=-8; dx<=8; dx++) for (int dy=-4; dy<=4; dy++) for (int dz=-8; dz<=8; dz++) {
                var p = pos.offset(dx,dy,dz);
                var state = level.getBlockState(p);
                if (state.is(net.minecraft.world.level.block.Blocks.SOUL_SOIL) || state.is(com.aihorror.block.ModBlocks.CORRUPTED_BLOCK)) {
                    emf++;
                }
                if (state.is(net.minecraft.world.level.block.Blocks.SOUL_SOIL)) emf++;
            }
            var nearGlitch = !level.getEntitiesOfClass(com.aihorror.entity.GlitchEntity.class, new AABB(pos).inflate(20)).isEmpty();
            if (nearGlitch) emf += 10;
            String levelStr;
            float pitch;
            if (emf > 15) { levelStr = "\u00A74\u00A7l5 - PARANORMAL"; pitch = 0.4f; player.addEffect(new net.minecraft.world.effect.MobEffectInstance(net.minecraft.world.effect.MobEffects.DARKNESS, 60, 0)); }
            else if (emf > 8) { levelStr = "\u00A7c4 - HIGH"; pitch = 0.6f; }
            else if (emf > 4) { levelStr = "\u00A7e3 - MEDIUM"; pitch = 0.9f; }
            else if (emf > 1) { levelStr = "\u00A7a2 - LOW"; pitch = 1.2f; }
            else { levelStr = "\u00A771 - NONE"; pitch = 1.6f; }
            player.sendSystemMessage(Component.literal("\u00A7b[EMF] \u00A77Reading: " + levelStr + " \u00A78(" + emf + ")"));
            level.playSound(null, player.blockPosition(), SoundEvents.NOTE_BLOCK_BIT.value(), SoundSource.PLAYERS, 1.0f, pitch);
            if (emf > 8) {
                level.playSound(null, player.blockPosition(), SoundEvents.AMBIENT_CAVE.value(), SoundSource.HOSTILE, 0.8f, 0.5f);
            }
            player.getCooldowns().addCooldown(new net.minecraft.world.item.ItemStack(this), 20);
            return InteractionResult.SUCCESS;
        }
    }
}