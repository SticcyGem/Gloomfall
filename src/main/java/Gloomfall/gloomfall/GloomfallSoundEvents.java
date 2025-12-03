package Gloomfall.gloomfall;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class GloomfallSoundEvents {

    // Deep
    public static final Identifier GLOOMFALL_DEEP_MUSIC_ID = new Identifier(Gloomfall.MOD_ID, "music.gloomfall.deep");
    public static SoundEvent GLOOMFALL_DEEP_MUSIC_EVENT = SoundEvent.of(GLOOMFALL_DEEP_MUSIC_ID);

    // Glitch
    public static final Identifier GLOOMFALL_GLITCH_ID = new Identifier(Gloomfall.MOD_ID, "music.gloomfall.glitch");
    public static SoundEvent GLOOMFALL_GLITCH_EVENT = SoundEvent.of(GLOOMFALL_GLITCH_ID);

    // Splinted
    public static final Identifier GLOOMFALL_SPLINTER_ID = new Identifier(Gloomfall.MOD_ID, "splinter");
    public static SoundEvent GLOOMFALL_SPLINTER_EVENT = SoundEvent.of(GLOOMFALL_SPLINTER_ID);

    // Concussed
    public static final Identifier GLOOMFALL_CONCUSSED_ID = new Identifier(Gloomfall.MOD_ID, "concussed");
    public static SoundEvent GLOOMFALL_CONCUSSED_EVENT = SoundEvent.of(GLOOMFALL_CONCUSSED_ID);

    public static void registerSounds() {
        GLOOMFALL_DEEP_MUSIC_EVENT = Registry.register(Registries.SOUND_EVENT, GLOOMFALL_DEEP_MUSIC_ID, GLOOMFALL_DEEP_MUSIC_EVENT);
        GLOOMFALL_GLITCH_EVENT = Registry.register(Registries.SOUND_EVENT, GLOOMFALL_GLITCH_ID, GLOOMFALL_GLITCH_EVENT);
        GLOOMFALL_SPLINTER_EVENT = Registry.register(Registries.SOUND_EVENT, GLOOMFALL_SPLINTER_ID, GLOOMFALL_SPLINTER_EVENT);
        GLOOMFALL_CONCUSSED_EVENT = Registry.register(Registries.SOUND_EVENT, GLOOMFALL_CONCUSSED_ID, GLOOMFALL_CONCUSSED_EVENT);

        if (GLOOMFALL_GLITCH_EVENT == null) {
            Gloomfall.LOGGER.error("Failed to register GLOOMFALL_GLITCH_EVENT.");
        }
    }
}