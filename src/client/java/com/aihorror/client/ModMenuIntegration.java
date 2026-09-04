package com.aihorror.client;

import com.aihorror.config.AiHorrorConfig;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> new ConfigScreen(parent);
    }

    public static class ConfigScreen extends Screen {
        private final Screen parent;

        public ConfigScreen(Screen parent) {
            super(Component.literal("AiHorror Config"));
            this.parent = parent;
        }

        @Override
        protected void init() {
            AiHorrorConfig cfg = AiHorrorConfig.get();
            int y = 30;
            int w = 200;
            int cx = this.width / 2 - 100;
            addRenderableWidget(Button.builder(Component.literal("Intensity: " + cfg.intensity + "/5"), btn -> {
                int next = (cfg.intensity + 1) % 6;
                cfg.setIntensity(next);
                btn.setMessage(Component.literal("Intensity: " + next + "/5"));
            }).bounds(cx, y, w, 20).build()); y += 24;
            addRenderableWidget(Button.builder(Component.literal("Enabled: " + (cfg.enabled ? "ON" : "OFF")), btn -> {
                cfg.enabled = !cfg.enabled; AiHorrorConfig.save();
                btn.setMessage(Component.literal("Enabled: " + (cfg.enabled ? "ON" : "OFF")));
            }).bounds(cx, y, w, 20).build()); y += 24;
            addRenderableWidget(Button.builder(Component.literal("World Corruption: " + (cfg.allowWorldCorruption ? "ON" : "OFF")), btn -> {
                cfg.allowWorldCorruption = !cfg.allowWorldCorruption; AiHorrorConfig.save();
                btn.setMessage(Component.literal("World Corruption: " + (cfg.allowWorldCorruption ? "ON" : "OFF")));
            }).bounds(cx, y, w, 20).build()); y += 24;
            addRenderableWidget(Button.builder(Component.literal("Build Destruction: " + (cfg.allowBuildDestruction ? "ON" : "OFF")), btn -> {
                cfg.allowBuildDestruction = !cfg.allowBuildDestruction; AiHorrorConfig.save();
                btn.setMessage(Component.literal("Build Destruction: " + (cfg.allowBuildDestruction ? "ON" : "OFF")));
            }).bounds(cx, y, w, 20).build()); y += 24;
            addRenderableWidget(Button.builder(Component.literal("Death Corruption: " + (cfg.allowDeathCorruption ? "ON" : "OFF")), btn -> {
                cfg.allowDeathCorruption = !cfg.allowDeathCorruption; AiHorrorConfig.save();
                btn.setMessage(Component.literal("Death Corruption: " + (cfg.allowDeathCorruption ? "ON" : "OFF")));
            }).bounds(cx, y, w, 20).build()); y += 24;
            addRenderableWidget(Button.builder(Component.literal("Jumpscares: " + (cfg.jumpscaresEnabled ? "ON" : "OFF")), btn -> {
                cfg.jumpscaresEnabled = !cfg.jumpscaresEnabled; AiHorrorConfig.save();
                btn.setMessage(Component.literal("Jumpscares: " + (cfg.jumpscaresEnabled ? "ON" : "OFF")));
            }).bounds(cx, y, w, 20).build()); y += 24;
            addRenderableWidget(Button.builder(Component.literal("Shader Glitch: " + (cfg.shaderGlitchEnabled ? "ON" : "OFF")), btn -> {
                cfg.shaderGlitchEnabled = !cfg.shaderGlitchEnabled; AiHorrorConfig.save();
                btn.setMessage(Component.literal("Shader Glitch: " + (cfg.shaderGlitchEnabled ? "ON" : "OFF")));
            }).bounds(cx, y, w, 20).build()); y += 24;
            addRenderableWidget(Button.builder(Component.literal("Found Footage: " + (cfg.foundFootageEnabled ? "ON" : "OFF")), btn -> {
                cfg.foundFootageEnabled = !cfg.foundFootageEnabled; AiHorrorConfig.save();
                btn.setMessage(Component.literal("Found Footage: " + (cfg.foundFootageEnabled ? "ON" : "OFF")));
            }).bounds(cx, y, w, 20).build()); y += 24;
            addRenderableWidget(Button.builder(Component.literal("Counter Items: " + (cfg.counterItemsEnabled ? "ON" : "OFF")), btn -> {
                cfg.counterItemsEnabled = !cfg.counterItemsEnabled; AiHorrorConfig.save();
                btn.setMessage(Component.literal("Counter Items: " + (cfg.counterItemsEnabled ? "ON" : "OFF")));
            }).bounds(cx, y, w, 20).build()); y += 24;
            addRenderableWidget(Button.builder(Component.literal("Time Manipulation: " + (cfg.allowTimeManipulation ? "ON" : "OFF")), btn -> {
                cfg.allowTimeManipulation = !cfg.allowTimeManipulation; AiHorrorConfig.save();
                btn.setMessage(Component.literal("Time Manipulation: " + (cfg.allowTimeManipulation ? "ON" : "OFF")));
            }).bounds(cx, y, w, 20).build()); y += 24;
            addRenderableWidget(Button.builder(Component.literal("Max Glitches: " + cfg.maxGlitchEntities), btn -> {
                cfg.maxGlitchEntities = cfg.maxGlitchEntities % 10 + 1;
                AiHorrorConfig.save();
                btn.setMessage(Component.literal("Max Glitches: " + cfg.maxGlitchEntities));
            }).bounds(cx, y, w, 20).build()); y += 24;
            addRenderableWidget(Button.builder(Component.literal("Max Corruption: " + cfg.maxCorruptionPerTick), btn -> {
                cfg.maxCorruptionPerTick = cfg.maxCorruptionPerTick % 10 + 1;
                AiHorrorConfig.save();
                btn.setMessage(Component.literal("Max Corruption: " + cfg.maxCorruptionPerTick));
            }).bounds(cx, y, w, 20).build()); y += 24;
            addRenderableWidget(Button.builder(Component.literal("Spawn Chance: " + cfg.glitchEntitySpawnChance + "%"), btn -> {
                cfg.glitchEntitySpawnChance = (cfg.glitchEntitySpawnChance + 10) % 110;
                if (cfg.glitchEntitySpawnChance > 100) cfg.glitchEntitySpawnChance = 10;
                AiHorrorConfig.save();
                btn.setMessage(Component.literal("Spawn Chance: " + cfg.glitchEntitySpawnChance + "%"));
            }).bounds(cx, y, w, 20).build()); y += 24;
            addRenderableWidget(Button.builder(Component.literal("Sleep Factor: " + cfg.sleepDeprivationFactor), btn -> {
                cfg.sleepDeprivationFactor = cfg.sleepDeprivationFactor % 5 + 1;
                AiHorrorConfig.save();
                btn.setMessage(Component.literal("Sleep Factor: " + cfg.sleepDeprivationFactor));
            }).bounds(cx, y, w, 20).build()); y += 24;
            addRenderableWidget(Button.builder(Component.literal("Surveillance Interval: " + cfg.surveillanceTickInterval), btn -> {
                cfg.surveillanceTickInterval = cfg.surveillanceTickInterval == 20 ? 10 : cfg.surveillanceTickInterval == 10 ? 40 : 20;
                AiHorrorConfig.save();
                btn.setMessage(Component.literal("Surveillance Interval: " + cfg.surveillanceTickInterval));
            }).bounds(cx, y, w, 20).build()); y += 24;
            addRenderableWidget(Button.builder(Component.literal("Corruption Interval: " + cfg.corruptionIntervalTicks), btn -> {
                cfg.corruptionIntervalTicks = cfg.corruptionIntervalTicks == 6000 ? 12000 : cfg.corruptionIntervalTicks == 12000 ? 3000 : 6000;
                AiHorrorConfig.save();
                btn.setMessage(Component.literal("Corruption Interval: " + cfg.corruptionIntervalTicks));
            }).bounds(cx, y, w, 20).build()); y += 24;
            addRenderableWidget(Button.builder(Component.literal("Jumpscare Cooldown: " + cfg.jumpscareCooldownTicks), btn -> {
                cfg.jumpscareCooldownTicks = cfg.jumpscareCooldownTicks == 18000 ? 9000 : cfg.jumpscareCooldownTicks == 9000 ? 36000 : 18000;
                AiHorrorConfig.save();
                btn.setMessage(Component.literal("Jumpscare Cooldown: " + cfg.jumpscareCooldownTicks));
            }).bounds(cx, y, w, 20).build()); y += 24;
            addRenderableWidget(Button.builder(Component.literal("Seed Salt: " + cfg.worldSeedSalt), btn -> {
                cfg.worldSeedSalt = cfg.worldSeedSalt == 0 ? 1337 : cfg.worldSeedSalt == 1337 ? 9999 : 0;
                AiHorrorConfig.save();
                btn.setMessage(Component.literal("Seed Salt: " + cfg.worldSeedSalt));
            }).bounds(cx, y, w, 20).build()); y += 24;
            addRenderableWidget(Button.builder(Component.literal("Done"), b -> {
                Minecraft.getInstance().setScreenAndShow(parent);
            }).bounds(cx, y, w, 20).build());
        }

        @Override
        public void onClose() {
            AiHorrorConfig.save();
            Minecraft.getInstance().setScreenAndShow(parent);
        }
    }
}
