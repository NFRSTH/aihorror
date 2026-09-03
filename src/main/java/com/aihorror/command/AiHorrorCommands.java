package com.aihorror.command;

import com.aihorror.config.AiHorrorConfig;
import com.aihorror.entity.ModEntities;
import com.aihorror.world.CorruptionManager;
import com.aihorror.world.RitualManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntitySpawnReason;

public class AiHorrorCommands {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("aihorror")
            .requires(src -> src.permissions().hasPermission(net.minecraft.server.permissions.Permissions.COMMANDS_GAMEMASTER))
            .then(Commands.literal("start").executes(ctx -> {
                AiHorrorConfig.get().enabled = true; AiHorrorConfig.save();
                ctx.getSource().sendSuccess(() -> Component.literal("\u00A7a[AiHorror] AI surveillance ENABLED"), true);
                return 1;
            }))
            .then(Commands.literal("stop").executes(ctx -> {
                AiHorrorConfig.get().enabled = false; AiHorrorConfig.save();
                ctx.getSource().sendSuccess(() -> Component.literal("\u00A7c[AiHorror] AI surveillance DISABLED"), true);
                return 1;
            }))
            .then(Commands.literal("status").executes(ctx -> {
                var c = AiHorrorConfig.get();
                ctx.getSource().sendSuccess(() -> Component.literal("\u00A77[AiHorror] enabled="+c.enabled+" intensity="+c.intensity+"/5 ("+String.format("%.0f%%", c.intensityFactor()*100)+") corruption="+c.allowWorldCorruption+" buildDestruction="+c.allowBuildDestruction+" deathCorruption="+c.allowDeathCorruption+" maxGlitch="+c.maxGlitchEntities), false);
                return 1;
            }))
            .then(Commands.literal("intensity").then(Commands.argument("value", IntegerArgumentType.integer(0,5)).executes(ctx -> {
                int v = IntegerArgumentType.getInteger(ctx, "value");
                AiHorrorConfig.get().setIntensity(v);
                // file persistence test already in setIntensity, also verify here
                try {
                    String json = java.nio.file.Files.readString(AiHorrorConfig.getConfigPath());
                    ctx.getSource().sendSuccess(() -> Component.literal("\u00A7a[AiHorror] Intensity set to " + v + "/5 " + (v==5?"(NO LIGHT RULE)":"(dark-only)") + " | File: " + json.substring(0, Math.min(60, json.length())) + "..."), true);
                } catch (Exception e) {
                    ctx.getSource().sendSuccess(() -> Component.literal("\u00A7a[AiHorror] Intensity set to " + v + "/5 persisted (check logs for persistence test)"), true);
                }
                return 1;
            })))
            .then(Commands.literal("guide").executes(ctx -> {
                if (ctx.getSource().getEntity() instanceof ServerPlayer sp) {
                    com.aihorror.item.GuideBook.giveGuide(sp);
                } else {
                    ctx.getSource().sendSuccess(() -> Component.literal("\u00A77Use in-game as player"), false);
                }
                return 1;
            }))
            .then(Commands.literal("ritual").executes(ctx -> {
                if (ctx.getSource().getEntity() instanceof ServerPlayer sp) {
                    RitualManager.tryRitual(sp);
                }
                return 1;
            }))
            .then(Commands.literal("trigger").then(Commands.literal("glitch").executes(ctx -> {
                if (ctx.getSource().getEntity() instanceof ServerPlayer sp) {
                    var level = (net.minecraft.server.level.ServerLevel) sp.level();
                    long count = level.getEntitiesOfClass(com.aihorror.entity.GlitchEntity.class, sp.getBoundingBox().inflate(80)).size();
                    if (count >= AiHorrorConfig.get().maxGlitchEntities) {
                        ctx.getSource().sendFailure(Component.literal("\u00A7cMax glitch limit reached ("+count+"/"+AiHorrorConfig.get().maxGlitchEntities+")"));
                        return 0;
                    }
                    BlockPos pos = sp.blockPosition().offset(3,0,3);
                    pos = level.getHeightmapPos(net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, pos);
                    var e = ModEntities.GLITCH_ENTITY.create(level, EntitySpawnReason.COMMAND);
                    if (e != null) { e.snapTo(pos.getX()+0.5, pos.getY(), pos.getZ()+0.5, 0,0); e.setTargetPlayer(sp); level.addFreshEntity(e); }
                    ctx.getSource().sendSuccess(() -> Component.literal("\u00A7c[AiHorror] Glitch spawned nearby (smiling, glitched English)"), true);
                }
                return 1;
            })).then(Commands.literal("corrupt").executes(ctx -> {
                if (ctx.getSource().getEntity() instanceof ServerPlayer sp) {
                    CorruptionManager.corruptAround(sp, 12);
                    ctx.getSource().sendSuccess(() -> Component.literal("\u00A78[AiHorror] Corrupted (glitch blocks purple/black)"), true);
                }
                return 1;
            })))
            .then(Commands.literal("give").then(Commands.literal("scanner").executes(ctx -> {
                if (ctx.getSource().getEntity() instanceof ServerPlayer sp) {
                    sp.getInventory().add(new net.minecraft.world.item.ItemStack(com.aihorror.item.ModItems.SCANNER));
                    ctx.getSource().sendSuccess(() -> Component.literal("\u00A7aGiven Scanner (balanced)"), false);
                }
                return 1;
            })).then(Commands.literal("emf").executes(ctx -> {
                if (ctx.getSource().getEntity() instanceof ServerPlayer sp) {
                    sp.getInventory().add(new net.minecraft.world.item.ItemStack(com.aihorror.item.ModItems.EMF_READER));
                    ctx.getSource().sendSuccess(() -> Component.literal("\u00A7aGiven EMF Reader (balanced)"), false);
                }
                return 1;
            })).then(Commands.literal("guidebook").executes(ctx -> {
                if (ctx.getSource().getEntity() instanceof ServerPlayer sp) {
                    com.aihorror.item.GuideBook.giveGuide(sp); ctx.getSource().sendSuccess(() -> Component.literal("\u00A7aGuide given"), false);
                }
                return 1;
            })))
            .then(Commands.literal("config")
                .then(Commands.literal("corruption").then(Commands.argument("enabled", com.mojang.brigadier.arguments.BoolArgumentType.bool()).executes(ctx -> {
                    boolean v = com.mojang.brigadier.arguments.BoolArgumentType.getBool(ctx, "enabled");
                    AiHorrorConfig.get().allowWorldCorruption = v; AiHorrorConfig.save();
                    ctx.getSource().sendSuccess(() -> Component.literal("\u00A77Corruption: " + v + " (moderate tuning)"), true);
                    return 1;
                })))
                .then(Commands.literal("buildDestruction").then(Commands.argument("enabled", com.mojang.brigadier.arguments.BoolArgumentType.bool()).executes(ctx -> {
                    boolean v = com.mojang.brigadier.arguments.BoolArgumentType.getBool(ctx, "enabled");
                    AiHorrorConfig.get().allowBuildDestruction = v; AiHorrorConfig.save();
                    ctx.getSource().sendSuccess(() -> Component.literal("\u00A77Build destruction: " + v + " (gated, default false - protects builds, inventoryShuffle also gated)"), true);
                    return 1;
                })))
                .then(Commands.literal("deathCorruption").then(Commands.argument("enabled", com.mojang.brigadier.arguments.BoolArgumentType.bool()).executes(ctx -> {
                    boolean v = com.mojang.brigadier.arguments.BoolArgumentType.getBool(ctx, "enabled");
                    AiHorrorConfig.get().allowDeathCorruption = v; AiHorrorConfig.save();
                    ctx.getSource().sendSuccess(() -> Component.literal("\u00A77Death corruption: " + v + " (off by default to protect world)"), true);
                    return 1;
                })))
                .then(Commands.literal("maxGlitch").then(Commands.argument("value", IntegerArgumentType.integer(1,10)).executes(ctx -> {
                    int v = IntegerArgumentType.getInteger(ctx, "value");
                    AiHorrorConfig.get().maxGlitchEntities = v; AiHorrorConfig.save();
                    ctx.getSource().sendSuccess(() -> Component.literal("\u00A7aMax glitch entities: " + v + " (custom limit)"), true);
                    return 1;
                })))
                .then(Commands.literal("maxCorruption").then(Commands.argument("value", IntegerArgumentType.integer(1,20)).executes(ctx -> {
                    int v = IntegerArgumentType.getInteger(ctx, "value");
                    AiHorrorConfig.get().maxCorruptionPerTick = v; AiHorrorConfig.save();
                    ctx.getSource().sendSuccess(() -> Component.literal("\u00A7aMax corruption per tick: " + v), true);
                    return 1;
                })))
            )
        );
    }
}