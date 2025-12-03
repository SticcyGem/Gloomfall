package Gloomfall.gloomfall.mixin.client.vanilla;

import Gloomfall.gloomfall.client.vanilla.GloomfallFadeableSound;
import net.minecraft.client.sound.AbstractSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractSoundInstance.class)
public abstract class TickableVanillaSoundInstance implements SoundInstance, GloomfallFadeableSound {

    @Shadow
    protected float volume;

    @Unique
    private float gloomfall$fadeMultiplier = 1.0f;

    @Override
    public void gloomfall$setFadeMultiplier(float fade) {
        this.gloomfall$fadeMultiplier = fade;
    }

    @Inject(method = "getVolume", at = @At("HEAD"), cancellable = true)
    private void gloomfall$applyFadeToVolume(CallbackInfoReturnable<Float> cir) {
        float finalVolume = this.volume * this.gloomfall$fadeMultiplier;
        cir.setReturnValue(finalVolume);
    }
}