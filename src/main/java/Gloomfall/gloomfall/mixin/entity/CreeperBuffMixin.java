package Gloomfall.gloomfall.mixin.entity;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.CreeperEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CreeperEntity.class)
public class CreeperBuffMixin {


    @Inject(method = "spawnEffectsCloud", at = @At("HEAD"))
    private void gloomfall$removeFireResBeforeCloud(CallbackInfo ci) {
        CreeperEntity creeper = (CreeperEntity) (Object) this;

        if (creeper.getCommandTags().contains("gloomfall:buffed")) {

            StatusEffectInstance fireRes = creeper.getStatusEffect(StatusEffects.FIRE_RESISTANCE);


            if (fireRes != null && fireRes.getDuration() > 1000000) {
                creeper.removeStatusEffect(StatusEffects.FIRE_RESISTANCE);
            }
        }
    }
}