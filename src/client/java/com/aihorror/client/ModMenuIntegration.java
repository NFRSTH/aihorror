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
            // Intensity button cycles 0-5
            addRenderableWidget(Button.builder(Component.literal("Intensity: " + cfg.intensity + "/5"), btn -> {
                int next = (cfg.intensity + 1) % 6;
                cfg.setIntensity(next);
                btn.setMessage(Component.literal("Intensity: " + next + "/5"));
            }).bounds(this.width/2 - 100, y, 200, 20).build());
            y += 24;
            addRenderableWidget(Button.builder(Component.literal("Enabled: " + (cfg.enabled ? "ON" : "OFF")), btn -> {
                cfg.enabled = !cfg.enabled; AiHorrorConfig.save();
                btn.setMessage(Component.literal("Enabled: " + (cfg.enabled ? "ON" : "OFF")));
            }).bounds(this.width/2 - 100, y, 200, 20).build());
            y += 24;
            addRenderableWidget(Button.builder(Component.literal("World Corruption: " + (cfg.allowWorldCorruption ? "ON" : "OFF")), btn -> {
                cfg.allowWorldCorruption = !cfg.allowWorldCorruption; AiHorrorConfig.save();
                btn.setMessage(Component.literal("World Corruption: " + (cfg.allowWorldCorruption ? "ON" : "OFF")));
            }).bounds(this.width/2 - 100, y, 200, 20).build());
            y += 24;
            addRenderableWidget(Button.builder(Component.literal("Build Destruction: " + (cfg.allowBuildDestruction ? "ON" : "OFF")), btn -> {
                cfg.allowBuildDestruction = !cfg.allowBuildDestruction; AiHorrorConfig.save();
                btn.setMessage(Component.literal("Build Destruction: " + (cfg.allowBuildDestruction ? "ON" : "OFF")));
            }).bounds(this.width/2 - 100, y, 200, 20).build());
            y += 24;
            addRenderableWidget(Button.builder(Component.literal("Death Corruption: " + (cfg.allowDeathCorruption ? "ON" : "OFF")), btn -> {
                cfg.allowDeathCorruption = !cfg.allowDeathCorruption; AiHorrorConfig.save();
                btn.setMessage(Component.literal("Death Corruption: " + (cfg.allowDeathCorruption ? "ON" : "OFF")));
            }).bounds(this.width/2 - 100, y, 200, 20).build());
            y += 24;
            addRenderableWidget(Button.builder(Component.literal("Jumpscares: " + (cfg.jumpscaresEnabled ? "ON" : "OFF")), btn -> {
                cfg.jumpscaresEnabled = !cfg.jumpscaresEnabled; AiHorrorConfig.save();
                btn.setMessage(Component.literal("Jumpscares: " + (cfg.jumpscaresEnabled ? "ON" : "OFF")));
            }).bounds(this.width/2 - 100, y, 200, 20).build());
            y += 24;
            addRenderableWidget(Button.builder(Component.literal("Shader Glitch: " + (cfg.shaderGlitchEnabled ? "ON" : "OFF")), btn -> {
                cfg.shaderGlitchEnabled = !cfg.shaderGlitchEnabled; AiHorrorConfig.save();
                btn.setMessage(Component.literal("Shader Glitch: " + (cfg.shaderGlitchEnabled ? "ON" : "OFF")));
            }).bounds(this.width/2 - 100, y, 200, 20).build());
            y += 24;
            addRenderableWidget(Button.builder(Component.literal("Done"), b -> {
                Minecraft.getInstance().setScreenAndShow(parent);
            }).bounds(this.width/2 - 100, y, 200, 20).build());
        }

        @Override
        public void onClose() {
            AiHorrorConfig.save();
            Minecraft.getInstance().setScreenAndShow(parent);
        }
    }
}