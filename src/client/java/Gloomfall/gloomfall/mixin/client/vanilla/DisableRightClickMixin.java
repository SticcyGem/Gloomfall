package Gloomfall.gloomfall.mixin.client.vanilla;

import Gloomfall.gloomfall.effect.GloomfallEffects;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class DisableRightClickMixin {

    @Shadow
    public ClientPlayerEntity player;

    @Inject(method = "doItemUse", at = @At("HEAD"), cancellable = true)
    private void gloomfall$preventRightClickIfConcussed(CallbackInfo ci) {
        if (this.player != null && GloomfallEffects.CONCUSSED != null) {
            if (this.player.hasStatusEffect(GloomfallEffects.CONCUSSED)) {
                ci.cancel();
            }
        }
    }
}