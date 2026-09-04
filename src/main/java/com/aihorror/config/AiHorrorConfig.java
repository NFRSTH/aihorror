package com.aihorror.config;

import com.aihorror.AiHorror;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import net.fabricmc.loader.api.FabricLoader;

public class AiHorrorConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("aihorror.json");

    public boolean enabled = true;
    public int intensity = 2;
    public boolean allowWorldCorruption = true;
    public boolean allowTimeManipulation = true;
    public boolean allowBuildDestruction = false;
    public boolean jumpscaresEnabled = true;
    public boolean foundFootageEnabled = true;
    public int glitchEntitySpawnChance = 30;
    public boolean counterItemsEnabled = true;
    public int surveillanceTickInterval = 20;
    public int corruptionIntervalTicks = 6000;
    public boolean allowDeathCorruption = false;
    public int maxGlitchEntities = 3;
    public int maxCorruptionPerTick = 5;
    public int jumpscareCooldownTicks = 18000;
    public boolean shaderGlitchEnabled = true;
    public long worldSeedSalt = 0;
    public int sleepDeprivationFactor = 1;

    private static AiHorrorConfig INSTANCE = new AiHorrorConfig();

    public static AiHorrorConfig get() { return INSTANCE; }

    public static void load() {
        if (Files.exists(CONFIG_PATH)) {
            try {
                String json = Files.readString(CONFIG_PATH);
                INSTANCE = GSON.fromJson(json, AiHorrorConfig.class);
                if (INSTANCE == null) INSTANCE = new AiHorrorConfig();

                if (INSTANCE.intensity > 5) {
                    INSTANCE.intensity = Math.round(INSTANCE.intensity / 20.0f);
                    INSTANCE.intensity = Math.max(0, Math.min(5, INSTANCE.intensity));
                    save();
                }
                AiHorror.LOGGER.info("[AiHorror] Config loaded: intensity={}/5 enabled={} seedSalt={} allowBuildDestruction={}", INSTANCE.intensity, INSTANCE.enabled, INSTANCE.worldSeedSalt, INSTANCE.allowBuildDestruction);
            } catch (IOException e) {
                AiHorror.LOGGER.error("[AiHorror] Failed to load config", e);
                INSTANCE = new AiHorrorConfig();
            }
        } else {
            save();
        }
    }

    public static void save() {
        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            Files.writeString(CONFIG_PATH, GSON.toJson(INSTANCE));
            AiHorror.LOGGER.info("[AiHorror] Config saved to {}", CONFIG_PATH);
        } catch (IOException e) {
            AiHorror.LOGGER.error("[AiHorror] Failed to save config", e);
        }
    }

    public void setIntensity(int v) {
        intensity = Math.max(0, Math.min(5, v));
        save();

        try {
            String json = Files.readString(CONFIG_PATH);
            AiHorrorConfig reloaded = GSON.fromJson(json, AiHorrorConfig.class);
            if (reloaded != null && reloaded.intensity != intensity) {
                AiHorror.LOGGER.error("[AiHorror] Persistence test FAILED: expected {} got {}", intensity, reloaded.intensity);
            } else {
                AiHorror.LOGGER.info("[AiHorror] Persistence test PASSED: intensity {}/5 saved and reloaded correctly", intensity);
            }
        } catch (Exception e) {
            AiHorror.LOGGER.error("[AiHorror] Persistence test error", e);
        }
    }
    public float intensityFactor() { return intensity / 5.0f; }
    public boolean isMaxIntensity() { return intensity >= 5; }
    public static Path getConfigPath() { return CONFIG_PATH; }
}
