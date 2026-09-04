package com.aihorror.client;

import com.aihorror.config.AiHorrorConfig;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
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
            int y = 20;
            int w = 200;
            int cx = this.width / 2 - 100;
            addRenderableWidget(Button.builder(Component.literal("Intensity: " + cfg.intensity + "/5"), btn -> {
                int next = (cfg.intensity + 1) % 6;
                cfg.setIntensity(next);
                btn.setMessage(Component.literal("Intensity: " + next + "/5"));
            }).bounds(cx, y, w, 20).build()); y += 22;
            addRenderableWidget(CycleButton.onOffBuilder(cfg.enabled).create(cx, y, w, 20, Component.literal("Enabled"), (btn,val) -> { cfg.enabled = val; AiHorrorConfig.save(); })); y += 22;
            addRenderableWidget(CycleButton.onOffBuilder(cfg.allowWorldCorruption).create(cx, y, w, 20, Component.literal("World Corruption"), (btn,val) -> { cfg.allowWorldCorruption = val; AiHorrorConfig.save(); })); y += 22;
            addRenderableWidget(CycleButton.onOffBuilder(cfg.allowBuildDestruction).create(cx, y, w, 20, Component.literal("Build Destruction"), (btn,val) -> { cfg.allowBuildDestruction = val; AiHorrorConfig.save(); })); y += 22;
            addRenderableWidget(CycleButton.onOffBuilder(cfg.allowDeathCorruption).create(cx, y, w, 20, Component.literal("Death Corruption"), (btn,val) -> { cfg.allowDeathCorruption = val; AiHorrorConfig.save(); })); y += 22;
            addRenderableWidget(CycleButton.onOffBuilder(cfg.jumpscaresEnabled).create(cx, y, w, 20, Component.literal("Jumpscares"), (btn,val) -> { cfg.jumpscaresEnabled = val; AiHorrorConfig.save(); })); y += 22;
            addRenderableWidget(CycleButton.onOffBuilder(cfg.shaderGlitchEnabled).create(cx, y, w, 20, Component.literal("Shader Glitch"), (btn,val) -> { cfg.shaderGlitchEnabled = val; AiHorrorConfig.save(); })); y += 22;
            addRenderableWidget(CycleButton.onOffBuilder(cfg.foundFootageEnabled).create(cx, y, w, 20, Component.literal("Found Footage"), (btn,val) -> { cfg.foundFootageEnabled = val; AiHorrorConfig.save(); })); y += 22;
            addRenderableWidget(CycleButton.onOffBuilder(cfg.counterItemsEnabled).create(cx, y, w, 20, Component.literal("Counter Items"), (btn,val) -> { cfg.counterItemsEnabled = val; AiHorrorConfig.save(); })); y += 22;
            addRenderableWidget(CycleButton.onOffBuilder(cfg.allowTimeManipulation).create(cx, y, w, 20, Component.literal("Time Manipulation"), (btn,val) -> { cfg.allowTimeManipulation = val; AiHorrorConfig.save(); })); y += 22;
            addRenderableWidget(CycleButton.onOffBuilder(cfg.fakeChatEnabled).create(cx, y, w, 20, Component.literal("Fake Chat"), (btn,val) -> { cfg.fakeChatEnabled = val; AiHorrorConfig.save(); })); y += 22;
            addRenderableWidget(CycleButton.onOffBuilder(cfg.fakeJoinLeaveEnabled).create(cx, y, w, 20, Component.literal("Fake Join/Leave"), (btn,val) -> { cfg.fakeJoinLeaveEnabled = val; AiHorrorConfig.save(); })); y += 22;
            addRenderableWidget(CycleButton.onOffBuilder(cfg.inventoryGhostEnabled).create(cx, y, w, 20, Component.literal("Ghost Items"), (btn,val) -> { cfg.inventoryGhostEnabled = val; AiHorrorConfig.save(); })); y += 22;
            addRenderableWidget(CycleButton.onOffBuilder(cfg.veinCorruptionEnabled).create(cx, y, w, 20, Component.literal("Vein Corruption"), (btn,val) -> { cfg.veinCorruptionEnabled = val; AiHorrorConfig.save(); })); y += 22;
            addRenderableWidget(CycleButton.onOffBuilder(cfg.redstoneFlickerEnabled).create(cx, y, w, 20, Component.literal("Redstone Flicker"), (btn,val) -> { cfg.redstoneFlickerEnabled = val; AiHorrorConfig.save(); })); y += 22;
            addRenderableWidget(CycleButton.onOffBuilder(cfg.fakeCaveSoundEnabled).create(cx, y, w, 20, Component.literal("Fake Cave Sounds"), (btn,val) -> { cfg.fakeCaveSoundEnabled = val; AiHorrorConfig.save(); })); y += 22;
            addRenderableWidget(Button.builder(Component.literal("Max Glitches: " + cfg.maxGlitchEntities), btn -> {
                cfg.maxGlitchEntities = cfg.maxGlitchEntities % 10 + 1; AiHorrorConfig.save(); btn.setMessage(Component.literal("Max Glitches: " + cfg.maxGlitchEntities));
            }).bounds(cx, y, w, 20).build()); y += 22;
            addRenderableWidget(Button.builder(Component.literal("Max Corruption: " + cfg.maxCorruptionPerTick), btn -> {
                cfg.maxCorruptionPerTick = cfg.maxCorruptionPerTick % 10 + 1; AiHorrorConfig.save(); btn.setMessage(Component.literal("Max Corruption: " + cfg.maxCorruptionPerTick));
            }).bounds(cx, y, w, 20).build()); y += 22;
            addRenderableWidget(Button.builder(Component.literal("Spawn Chance: " + cfg.glitchEntitySpawnChance + "%"), btn -> {
                cfg.glitchEntitySpawnChance = (cfg.glitchEntitySpawnChance + 10) % 110; if (cfg.glitchEntitySpawnChance > 100) cfg.glitchEntitySpawnChance = 10; AiHorrorConfig.save(); btn.setMessage(Component.literal("Spawn Chance: " + cfg.glitchEntitySpawnChance + "%"));
            }).bounds(cx, y, w, 20).build()); y += 22;
            addRenderableWidget(Button.builder(Component.literal("Sleep Factor: " + cfg.sleepDeprivationFactor), btn -> {
                cfg.sleepDeprivationFactor = cfg.sleepDeprivationFactor % 5 + 1; AiHorrorConfig.save(); btn.setMessage(Component.literal("Sleep Factor: " + cfg.sleepDeprivationFactor));
            }).bounds(cx, y, w, 20).build()); y += 22;
            addRenderableWidget(Button.builder(Component.literal("Surveillance Interval: " + cfg.surveillanceTickInterval), btn -> {
                cfg.surveillanceTickInterval = cfg.surveillanceTickInterval == 20 ? 10 : cfg.surveillanceTickInterval == 10 ? 40 : 20; AiHorrorConfig.save(); btn.setMessage(Component.literal("Surveillance Interval: " + cfg.surveillanceTickInterval));
            }).bounds(cx, y, w, 20).build()); y += 22;
            addRenderableWidget(Button.builder(Component.literal("Corruption Interval: " + cfg.corruptionIntervalTicks), btn -> {
                cfg.corruptionIntervalTicks = cfg.corruptionIntervalTicks == 6000 ? 12000 : cfg.corruptionIntervalTicks == 12000 ? 3000 : 6000; AiHorrorConfig.save(); btn.setMessage(Component.literal("Corruption Interval: " + cfg.corruptionIntervalTicks));
            }).bounds(cx, y, w, 20).build()); y += 22;
            addRenderableWidget(Button.builder(Component.literal("Jumpscare Cooldown: " + cfg.jumpscareCooldownTicks), btn -> {
                cfg.jumpscareCooldownTicks = cfg.jumpscareCooldownTicks == 18000 ? 9000 : cfg.jumpscareCooldownTicks == 9000 ? 36000 : 18000; AiHorrorConfig.save(); btn.setMessage(Component.literal("Jumpscare Cooldown: " + cfg.jumpscareCooldownTicks));
            }).bounds(cx, y, w, 20).build()); y += 22;
            addRenderableWidget(Button.builder(Component.literal("Seed Salt: " + cfg.worldSeedSalt), btn -> {
                cfg.worldSeedSalt = cfg.worldSeedSalt == 0 ? 1337 : cfg.worldSeedSalt == 1337 ? 9999 : 0; AiHorrorConfig.save(); btn.setMessage(Component.literal("Seed Salt: " + cfg.worldSeedSalt));
            }).bounds(cx, y, w, 20).build()); y += 22;
            addRenderableWidget(Button.builder(Component.literal("Done"), b -> { Minecraft.getInstance().setScreenAndShow(parent); }).bounds(cx, y, w, 20).build());
        }
        @Override
        public void onClose() {
            AiHorrorConfig.save();
            Minecraft.getInstance().setScreenAndShow(parent);
        }
    }
}
