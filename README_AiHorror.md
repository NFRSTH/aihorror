# AiHorror - Fabric 26.2 Surveillance AI Horror Mod

## Installed
- Jar: `C:\Users\HP\AppData\Roaming\.tlauncher\legacy\Minecraft\game\mods\aihorror-1.0.0.jar`
- Project: `C:\Users\HP\AiHorror`
- MC 26.2 / Fabric Loader 0.19.3 / Fabric API 0.158.0+26.2 / Java 25

## Concept (from your answers)
- **Surveillance Horror + Adaptive Stalker**: AI watches your position, remembers hiding spots, learns your playstyle
- **Sometimes visible creepy skin**: Glitch entity (humanoid, glitched texture flicker, teleports)
- **Full Horror**: jumpscares, titles, screen effects, loud stingers, darkness/blindness
- **Action-Based escalation**: fear increases on mining, breaking, hiding, sleeping
- **World corruption**: replaces grass/dirt/stone with Corrupted Block / Soul Soil, breaks torches, destroys parts of builds
- **Found Footage**: randomly generates chests with lore papers near you
- **Counter Items**: Scanner & EMF Reader
- **Singleplayer** optimized
- **Full Config** via `config/aihorror.json` + commands

## Features

### 1. SurveillanceAI (`com.aihorror.ai.SurveillanceAI`)
- Tracks `fearLevel` 0-100 per player, increases on actions, decays slowly
- Detects: alone (no other player within 50), hiding (standing still in dark), staring
- Triggers: whisper, cave sound, time glitch, door fake, jumpscare, glitch spawn, corruption, blindness, inventory shuffle

### 2. Glitch Entity (`com.aihorror.entity.GlitchEntity`)
- Monster, 40HP, 0.35 speed, invisibleTicks phase then reveals
- Teleports near player every 5-8s, flickers invisible, attacks when close, leaves corruption on despawn
- Renderer: `GlitchEntityRenderer` uses zombie model with `glitch.png` / `glitch_creepy.png` flicker

### 3. World Corruption (`CorruptionManager`)
- Every `corruptionIntervalTicks` (6000 ticks = 5min, config) corrupt 3-5 blocks around player
- Found Footage: chest with papers at nearby ground
- DestroyBuildPart: removes random cobble/planks/bricks/glass/chest within 20 radius

### 4. Counter Items
- **Scanner** (`aihorror:scanner`): Recipe IRI/RCR/IRI (iron/redstone/compass). Right-click scans 64 radius for Glitch, shows distance & threat, gives Glowing to entity
- **EMF Reader** (`aihorror:emf_reader`): Recipe GGG/RIR/IRI (gold/redstone/iron). Measures corrupted blocks + nearby glitch, gives 1-5 reading

### 5. Block
- **Corrupted Block** (`aihorror:corrupted_block`): black, soul soil sound, light 2, requires tool

### 6. Sounds
- `jumpscare`, `whisper`, `glitch_ambient`, `static_loop` in `sounds.json`

### 7. Commands
- `/aihorror start` / `stop` / `status`
- `/aihorror intensity <0-100>`
- `/aihorror trigger glitch` / `corrupt`
- `/aihorror give scanner` / `emf`

### 8. Config
File: `config/aihorror.json` (auto-generated)
```json
{
  "enabled": true,
  "intensity": 50,
  "allowWorldCorruption": true,
  "allowTimeManipulation": true,
  "allowBuildDestruction": true,
  "jumpscaresEnabled": true,
  "foundFootageEnabled": true,
  "glitchEntitySpawnChance": 30,
  "counterItemsEnabled": true
}
```
Edit or use commands. Requires restart or `/aihorror intensity` to hot-reload.

## Recipe Files
- `data/aihorror/recipe/scanner.json`
- `data/aihorror/recipe/emf_reader.json`

## How to Test
1. Launch **Fabric 26.2** via TLauncher
2. Create new Singleplayer world (Survival, Difficulty Normal)
3. `/aihorror status` -> should show enabled
4. `/aihorror give scanner` -> test scanning
5. Mine 10 blocks -> fear rises, expect whisper / corruption
6. Hide in dark 30s -> "I see you hiding..."
7. Sleep -> "You can''t sleep while I watch" + glitch spawn
8. `/aihorror trigger glitch` -> force spawn
9. Check `logs/latest.log` for `[AiHorror] ...`

## Build
```bat
cd C:\Users\HP\AiHorror
set JAVA_HOME=C:\Users\HP\jdk25\jdk-25.0.4.1+1
.\gradlew.bat build
# jar in build\libs\modid-1.0.0.jar -> rename to aihorror-1.0.0.jar
```

## Next Improvements
- Add real creepy 64x64 texture (currently placeholder black 16x16)
- Add custom sounds ogg for jumpscare/whisper
- Add Mixins for screen shake / vignette
- Add config screen via ModMenu/YACL
