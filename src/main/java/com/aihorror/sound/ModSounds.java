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

    public static void initialize() {
        JUMPSCARE_EVENT = register("jumpscare");
        WHISPER_EVENT = register("whisper");
        GLITCH_AMBIENT = register("glitch_ambient");
        STATIC_LOOP = register("static_loop");
        AiHorror.LOGGER.info("[AiHorror] Sounds registered");
    }

    private static SoundEvent register(String name) {
        var id = AiHorror.id(name);
        SoundEvent ev = SoundEvent.createVariableRangeEvent(id);
        Registry.register(BuiltInRegistries.SOUND_EVENT, id, ev);
        return ev;
    }
}
