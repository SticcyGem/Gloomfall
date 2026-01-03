package Gloomfall.gloomfall.mixin.common;

import Gloomfall.gloomfall.Gloomfall;
import Gloomfall.gloomfall.config.GloomfallConfig;
import Gloomfall.gloomfall.effect.GloomfallEffects;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public class EntityMixin {

    @Inject(method = "setSprinting", at = @At("HEAD"), cancellable = true)
    private void gloomfall$preventSprinting(boolean sprinting, CallbackInfo ci) {
        if (!sprinting) return;

        Entity self = (Entity) (Object) this;

        if (self instanceof LivingEntity entity) {
            GloomfallConfig config = Gloomfall.getConfig();
            if (config != null && config.globalMobChangeEnabled) {

                // Splintered
                if (config.splinteredDisableSprint && GloomfallEffects.SPLINTERED != null && entity.hasStatusEffect(GloomfallEffects.SPLINTERED)) {
                    ci.cancel();
                }
            }
        }
    }
}