package Gloomfall.gloomfall.mixin.client.vanilla;

import Gloomfall.gloomfall.Gloomfall;
import Gloomfall.gloomfall.client.GloomfallClient;
import Gloomfall.gloomfall.config.GloomfallConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.MusicTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MusicTracker.class)
public class MusicStopMixin {

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void gloomfall$disableVanillaMusicLogic(CallbackInfo ci) {
        GloomfallConfig config = Gloomfall.getConfig();
        if (config == null || !config.modEnabled) {
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