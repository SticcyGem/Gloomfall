package Gloomfall.gloomfall.client;

import Gloomfall.gloomfall.Gloomfall;
import Gloomfall.gloomfall.GloomfallSoundEvents;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;

import java.util.ArrayList;
import java.util.List;

public class CustomMusicManager {

    private final List<SoundEvent> musicTracks = new ArrayList<>();

    public CustomMusicManager() {
        if (GloomfallSoundEvents.GLOOMFALL_DEEP_MUSIC_EVENT != null) {
            musicTracks.add(GloomfallSoundEvents.GLOOMFALL_DEEP_MUSIC_EVENT);
        } else {
            Gloomfall.LOGGER.error("Custom music event is null!");
        }
    }

    public boolean hasTracks() {
        return !musicTracks.isEmpty();
    }

    public CustomFadingSoundInstance getNextTrackInstance() {
        if (!hasTracks()) return null;
        SoundEvent trackEvent = musicTracks.get(0);
        return new CustomFadingSoundInstance(trackEvent, SoundCategory.MUSIC);
    }
}