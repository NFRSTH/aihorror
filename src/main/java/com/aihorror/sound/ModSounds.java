package com.aihorror.sound;

import com.aihorror.AiHorror;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;

public class ModSounds {
    public static SoundEvent JUMPSCARE_EVENT;
    public static SoundEvent WHISPER_EVENT;
    public static SoundEvent GLITCH_AMBIENT;
    public static SoundEvent STATIC_LOOP;
    public static SoundEvent HEARTBEAT_LOOP;
    public static SoundEvent STEPS_EVENT;
    public static SoundEvent DOOR_CREAK_EVENT;

    public static void initialize() {
        JUMPSCARE_EVENT = register("jumpscare");
        WHISPER_EVENT = register("whisper");
        GLITCH_AMBIENT = register("glitch_ambient");
        STATIC_LOOP = register("static_loop");
        HEARTBEAT_LOOP = register("heartbeat_loop");
        STEPS_EVENT = register("steps");
        DOOR_CREAK_EVENT = register("door_creak");
        AiHorror.LOGGER.info("[AiHorror] Sounds registered (7 events)");
    }

    private static SoundEvent register(String name) {
        var id = AiHorror.id(name);
        SoundEvent ev = SoundEvent.createVariableRangeEvent(id);
        Registry.register(BuiltInRegistries.SOUND_EVENT, id, ev);
        return ev;
    }
}
