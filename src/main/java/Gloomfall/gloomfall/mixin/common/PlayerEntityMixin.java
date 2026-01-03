package Gloomfall.gloomfall.mixin.common;

import Gloomfall.gloomfall.Gloomfall;
import Gloomfall.gloomfall.config.GloomfallConfig;
import Gloomfall.gloomfall.effect.GloomfallEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {

    @Inject(method = "tick", at = @At("HEAD"))
    private void gloomfall$tickSprintCheck(CallbackInfo ci) {
        GloomfallConfig config = Gloomfall.getConfig();
        if (config == null || !config.globalMobChangeEnabled) return;

        PlayerEntity player = (PlayerEntity) (Object) this;
        if (!player.isSprinting()) return;

        if (config.splinteredDisableSprint && GloomfallEffects.SPLINTERED != null && player.hasStatusEffect(GloomfallEffects.SPLINTERED)) {
            player.setSprinting(false);
        }
    }

    @Inject(method = "getMovementSpeed", at = @At("RETURN"), cancellable = true)
    private void gloomfall$reduceSpeedIfConcussed(CallbackInfoReturnable<Float> cir) {
        GloomfallConfig config = Gloomfall.getConfig();
        if (config == null || !config.globalMobChangeEnabled) return;

        PlayerEntity player = (PlayerEntity) (Object) this;

        if (GloomfallEffects.CONCUSSED != null && player.hasStatusEffect(GloomfallEffects.CONCUSSED)) {
            float originalSpeed = cir.getReturnValue();
            float reduction = config.concussedMovementSpeedReduction;

            float newSpeed = Math.max(0, originalSpeed * (1.0f - reduction));

            cir.setReturnValue(newSpeed);
        }
    }

    @Inject(method = "jump", at = @At("RETURN"))
    private void gloomfall$removeJumpMomentumIfConcussed(CallbackInfo ci) {
        GloomfallConfig config = Gloomfall.getConfig();
        if (config == null || !config.globalMobChangeEnabled) return;

        PlayerEntity player = (PlayerEntity) (Object) this;

        if (GloomfallEffects.CONCUSSED != null && player.hasStatusEffect(GloomfallEffects.CONCUSSED)) {
            Vec3d velocity = player.getVelocity();

            player.setVelocity(0, velocity.y, 0);
        }
    }
}