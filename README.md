# AiHorror -- Surveillance AI Horror for Fabric 26.2

> **An adaptive stalker AI that watches, learns, and corrupts your world.** Singleplayer psychological horror for Minecraft 26.2 (Fabric Loader 0.19.3 / Fabric API 0.158.0+26.2 / Java 25).

[![Fabric 26.2](https://img.shields.io/badge/Minecraft-26.2-green)](https://fabricmc.net) [![Loader 0.19.3](https://img.shields.io/badge/Loader-0.19.3-blue)](https://fabricmc.net) [![Java 25](https://img.shields.io/badge/Java-25-orange)](https://adoptium.net) [![License MIT](https://img.shields.io/badge/License-MIT-yellow)](LICENSE)

**Mod jar:** `mods/aihorror-1.0.0.jar` (159KB, contains 3 user textures, valid Ogg Vorbis, working refmaps, fixed icon)  
**Project: `.\AiHorror` (clone via `git clone https://github.com/NFRSTH/aihorror.git` -- do NOT nest)

---

## Concept (from 30 Q&A)

- **Surveillance Horror + Adaptive Stalker** -- AI remembers where you hide, how you play, and counters it
- **Smiling glitch entity** -- humanoid with wide unnatural smile / black eyes, 3 textures from your Downloads flickering every 150ms
- **Glitched English** -- chat inserts `####` and random caps
- **Dark-only hunt (except 5/5)** -- AI only hunts in dark (`light <=5`) unless intensity `5/5` (no light rule)
- **Whisper + intense music** -- vanilla sounds pitch-edited (`0.8-1.2x`) + custom Ogg Vorbis
- **Glitch blocks** -- corrupted world is purple/black checker (missing-texture style) + crying obsidian / soul soil
- **Full Story** -- 7 logs (5+ fragments) in chests with corrupted tape
- **Sleep deprivation** -- less you sleep, more fear (`ticksSinceSleep` > 48000 adds fear)
- **Death protection** -- death corruption `off` by default (protect world, toggle via command)
- **Balanced counters, time snap both ways, moderate destruction tunable, occasional jumpscares, shader glitch, punished hiding, standalone, guide book, ritual endgame, random seed per world**

---

## Features

### 1. Surveillance AI `src/main/java/com/aihorror/ai/SurveillanceAI.java:1`
- `fearLevel` 0-100 internal, `intensity` 0-5 -> `intensityFactor() = /5.0` `AiHorrorConfig.java:60`
- Tracks `hideCount`, `ticksAlone`, `ticksSinceSleep`, `jumpscareCooldown`, `lastSeenGlitchTick`
- Detects: alone (<50 blocks), hiding (still in dark >200 ticks), staring, sleep deprivation
- Triggers: `###` whisper, `AMBIENT_CAVE.value()` 1.5f, time snap, door fake, jumpscare (title + `WARDEN_ROAR` + `BLINDNESS`/`SLOWNESS`/`NAUSEA`), glitch spawn (respects `maxGlitchEntities`), corruption (`maxCorruptionPerTick`), inventory shuffle (gated)

### 2. Glitch Entity `src/main/java/com/aihorror/entity/GlitchEntity.java:1`
- `Monster` 40HP `0.35` speed `64` follow, `isInvisible()` phase 60 ticks, teleport every 5-8s, flickers, attacks <2 blocks, leaves soul soil on despawn
- **Renderer** `src/client/java/com/aihorror/client/renderer/GlitchEntityRenderer.java:14` -- `HumanoidModel` (Zombie) cycling 3 user images:
  - `glitch.png` <- `Downloads/glitch-5167543797.png` (278x491 -> 64x64)
  - `glitch_creepy.png` <- `glitch_creepy-3778253060.png` (315x562 -> 64x64)
  - `glitch_alt.png` <- `glitch_alt-3592543168.png` (314x552 -> 64x64)
  - `System.currentTimeMillis()/150 %3`

### 3. World Corruption `src/main/java/com/aihorror/world/CorruptionManager.java:1`
- Every `corruptionIntervalTicks=6000` (5 min) corrupts `maxCorruptionPerTick` blocks (default 5) within radius
- Replaces `GRASS/DIRT/STONE/COBBLE/SAND` -> `CORRUPTED_BLOCK` (purple/black 16x16) / `CRYING_OBSIDIAN` / `SOUL_SOIL`; breaks torches
- **Found Footage**: chest at nearby ground with `Full Story` 7 logs + `CORRUPTED_TAPE`
- `destroyBuildPart` gated by `allowBuildDestruction=false` (protect builds)

### 4. Counter Items `src/main/java/com/aihorror/item/ModItems.java:1`
- **Scanner** `aihorror:scanner` -- `I R I / R C R / I R I` (iron/redstone/compass) scans 64 blocks, shows `CRITICAL/HIGH/MEDIUM` + distance, gives Glowing
- **EMF Reader** `aihorror:emf_reader` -- `G G G / R I R / I R I` (gold/redstone/iron) scores corruption 1-5 (`NONE->PARANORMAL`) + `DARKNESS` at 5
- Cooldown `ItemStack` 40 / 20 ticks, `sendSystemMessage`

### 5. Block `src/main/java/com/aihorror/block/ModBlocks.java:1`
- `corrupted_block` -- 16x16 purple/black checker + white cross `textures/block/corrupted_block.png:1` (127B valid PNG), `MapColor.COLOR_BLACK`, `2.0/6.0` `SOUL_SOIL` sound, light 2

### 6. Sounds `src/main/resources/assets/aihorror/sounds.json:1` + `sounds/*.ogg:1`
- `jumpscare.ogg` 17KB, `whisper.ogg` 20KB, `glitch_ambient.ogg` 18KB (3s stream), `static_loop.ogg` 25KB (2s loop) -- all valid OggS Vorbis `soundfile` 44.1kHz, not 10B header
- Vanilla-edited via pitch `0.8-1.2x` in `playSound`

### 7. Mixins + Refmap
- `ServerLevelMixin:7` `src/main/java/com/aihorror/mixin/ServerLevelMixin.java:7` `@Mixin(ServerLevel) tick@HEAD`
- `CameraShakeMixin:10` `src/client/java/com/aihorror/mixin/client/CameraShakeMixin.java:10` `@Mixin(Camera) tick@TAIL` -- shakes `setPosition(position().add(dx,dy,dz))` when `NAUSEA/DARKNESS` (shader glitch)
- `aihorror.mixins.json:1` `ServerLevelMixin` + `aihorror.client.mixins.json:1` `CameraShakeMixin` both `JAVA_25` with `aihorror.refmap.json` / `aihorror.client.refmap.json` (working, included in jar)

### 8. Config `src/main/java/com/aihorror/config/AiHorrorConfig.java:1` -> `config/aihorror.json`
```json
{
  "enabled": true,
  "intensity": 2, // 0-5 (migrates 0-100 /20)
  "allowWorldCorruption": true,
  "allowTimeManipulation": true,
  "allowBuildDestruction": false, // gated, protects builds/inventory
  "jumpscaresEnabled": true,
  "foundFootageEnabled": true,
  "allowDeathCorruption": false, // off to protect on death
  "maxGlitchEntities": 3,
  "maxCorruptionPerTick": 5,
  "jumpscareCooldownTicks": 18000, // ~15 min occasional
  "shaderGlitchEnabled": true
}
```
`setIntensity(0-5)` clamps, `save()` + reload verify logs `PERSISTENCE TEST PASSED`

### 9. Ritual Endgame `src/main/java/com/aihorror/world/RitualManager.java:1`
- Needs `CORRUPTED_TAPE` in hand + midnight (`13000-19000`) + 4x `CRYING_OBSIDIAN` nearby -> weakens AI, spawns 20HP glitch to kill

---

## Commands

```
/aihorror start | stop | status   # status shows intensity/5 + flags
/aihorror intensity <0-5>         # 5=max no-light-rule, 0=off, persistence test logged
/aihorror guide                   # gives Guide Book
/aihorror ritual                  # try ritual at midnight
/aihorror trigger glitch          # respects maxGlitch limit
/aihorror trigger corrupt
/aihorror give scanner|emf|guidebook
/aihorror config corruption <bool>
/aihorror config buildDestruction <bool>  # also gates inventoryShuffle
/aihorror config deathCorruption <bool>
/aihorror config maxGlitch <1-10>
/aihorror config maxCorruption <1-20>
```

---

## Installation

1. Install **Fabric Loader 0.19.3** for **26.2** via TLauncher
2. Put `fabric-api-0.158.0+26.2.jar` + `aihorror-1.0.0.jar` (159KB) in `%APPDATA%\.tlauncher\legacy\Minecraft\game\mods\`
3. Requires **Java 25** (`jdk-25.0.4.1+1` at `C:\jdk25\`)
4. Launch `Fabric 26.2` -> `creepy world` (already Creative + cheats: `level.dat GameType 1 allowCommands 1`, `players/data/... playerGameType 1`)

World `saves/creepy world` has `datapacks/aihorror/pack.mcmeta` marker + `AIHORROR_ACTIVE.txt`

---

## Building

```bat
set JAVA_HOME=C:\jdk25\jdk-25.0.4+1  (or your JDK 25 install)
cd .\AiHorror
.\gradlew.bat build
# -> build/libs/aihorror-1.0.0.jar (159KB) + aihorror-1.0.0-sources.jar
copy build\libs\aihorror-1.0.0.jar "%appdata%\.tlauncher\legacy\Minecraft\game\mods\aihorror-1.0.0.jar"
```

Mixins refmaps are generated via `src/*/resources/*.refmap.json` (manual minimal, loom also generates).

---

## Assets

- **Icon** `assets/aihorror/icon.png:1` 128x128 PNG `89 50 4E 47` (was ZIP `PK`) -- dark bg purple stripes smiling face
- **Entity** 3x 64x64 from `Downloads/glitch-*.png` (center-cropped Lanczos)
- **Items** `scanner.png:175B` `emf_reader.png:170B` `corrupted_tape.png:135B` all 16x16 (were 76B 1x1)
- **Block** `corrupted_block.png:127B` 16x16 purple/black (was 103B stub)

---

## GitHub

Source published to **https://github.com/NFRSTH/aihorror** (`master`):
- `bb8bddf` perfect: mixins, icon, 0-5, gated, ritual, refmaps
- `a8d3bda` chore ignore logs
- `e6ad42c` 3 images entity
- `0ffe56a` fix ogg/textures

License MIT.


