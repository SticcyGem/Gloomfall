package Gloomfall.gloomfall.mixin.client.vanilla;

import Gloomfall.gloomfall.Gloomfall;
import Gloomfall.gloomfall.client.GloomfallClient;
import Gloomfall.gloomfall.config.GloomfallConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.AmbientSoundPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AmbientSoundPlayer.class)
public class DisableCaveSoundsMixin {

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void gloomfall$preventCaveSounds(CallbackInfo ci) {
        GloomfallConfig config = Gloomfall.getConfig();
        if (config == null || !config.modEnabled) {
            return;
        }

        if (config.caveSoundsStillPlayUnderDeepMusic) {
            return;
        }

        MinecraftClient client = MinecraftClient.getInstance();
        if (client != null && client.player != null && client.world != null) {
            if (GloomfallClient.isGloomActive) {
                ci.cancel();
                return;
            }

            if (GloomfallClient.areConditionsMet(client.player, client.world, config)) {
                ci.cancel();
            }
        }
    }
}