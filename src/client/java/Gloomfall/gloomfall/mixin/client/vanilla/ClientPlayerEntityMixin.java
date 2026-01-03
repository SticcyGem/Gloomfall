package Gloomfall.gloomfall.mixin.client.vanilla;

import Gloomfall.gloomfall.Gloomfall;
import Gloomfall.gloomfall.config.GloomfallConfig;
import Gloomfall.gloomfall.effect.GloomfallEffects;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin {

    @Inject(method = "canStartSprinting", at = @At("HEAD"), cancellable = true)
    private void gloomfall$preventSprintStart(CallbackInfoReturnable<Boolean> cir) {
        ClientPlayerEntity player = (ClientPlayerEntity) (Object) this;
        GloomfallConfig config = Gloomfall.getConfig();

        if (config == null || !config.globalMobChangeEnabled) return;

        if (config.splinteredDisableSprint && GloomfallEffects.SPLINTERED != null && player.hasStatusEffect(GloomfallEffects.SPLINTERED)) {
            cir.setReturnValue(false);
        }
    }
}