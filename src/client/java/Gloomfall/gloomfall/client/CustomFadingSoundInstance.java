package Gloomfall.gloomfall.client;

import Gloomfall.gloomfall.Gloomfall;
import net.minecraft.client.sound.AbstractSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.TickableSoundInstance;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;

public class CustomFadingSoundInstance extends AbstractSoundInstance implements TickableSoundInstance {
    public float fadeVolume = 0.0f;
    private boolean finished = false;

    public CustomFadingSoundInstance(SoundEvent soundEvent, SoundCategory category) {
        super(soundEvent, category, SoundInstance.createRandom());
        this.relative = true;
        this.repeat = false;
        this.repeatDelay = 0;
        this.pitch = 1.0f;
        this.volume = 1.0f;
        this.x = 0.0f;
        this.y = 0.0f;
        this.z = 0.0f;
    }

    @Override
    public void tick() {
        if (Gloomfall.getConfig() == null) return;

        float configVolume = Gloomfall.getConfig().musicVolume / 100.0f;
        float fade = Math.max(0f, Math.min(1f, fadeVolume));
        this.volume = configVolume * fade;
    }

    @Override
    public boolean isDone() {
        return this.finished;
    }

    public void setDone() {
        this.finished = true;
    }
}