package Gloomfall.gloomfall.mixin.common;

import Gloomfall.gloomfall.Gloomfall;
import Gloomfall.gloomfall.GloomfallSoundEvents;
import Gloomfall.gloomfall.config.GloomfallConfig;
import Gloomfall.gloomfall.effect.GloomfallEffects;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.registry.Registries;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.ThreadLocalRandom;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    @Unique
    private int gloomfall$lastEffectTime = -1000;

    @Unique
    private int gloomfall$lastWebTime = -1000;

    @ModifyVariable(method = "damage", at = @At("HEAD"), argsOnly = true)
    private float gloomfall$modifyIncomingDamage(float amount, DamageSource source) {
        GloomfallConfig config = Gloomfall.getConfig();
        if (config == null || !config.globalMobChangeEnabled) return amount;

        LivingEntity victim = (LivingEntity) (Object) this;
        if (GloomfallEffects.VULNERABILITY != null && victim.hasStatusEffect(GloomfallEffects.VULNERABILITY)) {
            float multiplier = 1.0f + config.vulnerabilityEffectIncreaseDamageTaken;
            amount *= multiplier;
        }

        Entity attacker = source.getAttacker();
        if (attacker instanceof LivingEntity livingAttacker) {
            if (GloomfallEffects.VULNERABILITY != null && livingAttacker.hasStatusEffect(GloomfallEffects.VULNERABILITY)) {
                amount -= config.vulnerabilityOutgoingDamageReduction;
                if (amount < 0) amount = 0;
            }
        }
        return amount;
    }

    @ModifyVariable(method = "heal", at = @At("HEAD"), argsOnly = true)
    private float gloomfall$modifyHealing(float amount) {
        GloomfallConfig config = Gloomfall.getConfig();
        if (config == null || !config.globalMobChangeEnabled) return amount;

        LivingEntity entity = (LivingEntity) (Object) this;
        if (GloomfallEffects.SPLINTERED != null && entity.hasStatusEffect(GloomfallEffects.SPLINTERED)) {
            float multiplier = Math.max(0.0f, 1.0f - config.splinteredEffectHealReduction);
            return amount * multiplier;
        }
        return amount;
    }

    @Inject(method = "getMovementSpeed()F", at = @At("RETURN"), cancellable = true)
    private void gloomfall$reduceSpeedIfConcussed(CallbackInfoReturnable<Float> cir) {
        GloomfallConfig config = Gloomfall.getConfig();
        if (config == null || !config.globalMobChangeEnabled) return;

        LivingEntity entity = (LivingEntity) (Object) this;
        if (GloomfallEffects.CONCUSSED != null && entity.hasStatusEffect(GloomfallEffects.CONCUSSED)) {
            float originalSpeed = cir.getReturnValue();
            float reduction = config.concussedMovementSpeedReduction;
            cir.setReturnValue(originalSpeed * (1.0f - reduction));
        }
    }

    @Inject(method = "jump", at = @At("HEAD"), cancellable = true)
    private void gloomfall$preventJumpEffects(CallbackInfo ci) {
        GloomfallConfig config = Gloomfall.getConfig();
        if (config == null || !config.globalMobChangeEnabled) return;

        LivingEntity entity = (LivingEntity) (Object) this;
        if (config.splinteredDisableJump && GloomfallEffects.SPLINTERED != null && entity.hasStatusEffect(GloomfallEffects.SPLINTERED)) {
            ci.cancel();
        }
    }

    @Inject(method = "damage", at = @At("RETURN"))
    private void gloomfall$applyEffectsOnDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue()) return;

        LivingEntity target = (LivingEntity) (Object) this;
        if (target.getWorld().isClient()) return;

        GloomfallConfig config = Gloomfall.getConfig();
        if (config == null || !config.globalMobChangeEnabled) return;

        Entity attacker = source.getAttacker();
        if (!(attacker instanceof LivingEntity attackerLiving)) return;

        String mobId = Registries.ENTITY_TYPE.getId(attackerLiving.getType()).toString();
        int yLevel = (int) attackerLiving.getY();

        if (config.spiderApplyingMobs.contains(mobId)) {
            if (target.age - gloomfall$lastWebTime >= config.spiderTrapPlayerCooldown) {
                if (config.spiderTrapPlayerWithCobwebAfterAttackingUnderY0 && target.getY() < 0) {
                    BlockPos targetPos = target.getBlockPos();
                    if (target.getWorld().getBlockState(targetPos).isAir()) {
                        target.getWorld().setBlockState(targetPos, Blocks.COBWEB.getDefaultState());
                        gloomfall$lastWebTime = target.age;
                    }
                }
            }
            if (target.getY() < 0 && config.spiderDarknessUponAttackUnderY0Duration > 0) {
                target.addStatusEffect(new StatusEffectInstance(
                        StatusEffects.DARKNESS,
                        config.spiderDarknessUponAttackUnderY0Duration,
                        0,
                        false,
                        false,
                        true
                ));
            }
        }

        // Global Effects
        boolean effectApplied = false;
        if (target.age - gloomfall$lastEffectTime < config.effectApplicationCooldown) return;

        // Vulnerability
        if (!effectApplied && config.vulnerabilityApplyingMobs.contains(mobId)) {
            boolean apply = false;
            int duration = 0;
            if (yLevel > 0) {
                if (ThreadLocalRandom.current().nextInt(100) < config.mobGiveVulnerabilityEffectChanceAboveY0) { apply = true; duration = config.mobGiveVulnerabilityEffectDurationAboveY0; }
            } else {
                if (ThreadLocalRandom.current().nextInt(100) < config.mobGiveVulnerabilityEffectChanceBelowY0) { apply = true; duration = config.mobGiveVulnerabilityEffectDurationBelowY0; }
            }
            if (apply && GloomfallEffects.VULNERABILITY != null) {
                target.addStatusEffect(new StatusEffectInstance(GloomfallEffects.VULNERABILITY, duration, 0));
                target.getWorld().playSound(null, target.getX(), target.getY(), target.getZ(), SoundEvents.ENTITY_SHULKER_AMBIENT, SoundCategory.HOSTILE, 1.0f, 1.0f);
                effectApplied = true;
            }
        }

        // Splintered
        if (!effectApplied && config.splinteredApplyingMobs.contains(mobId)) {
            boolean apply = false;
            int duration = 0;
            if (yLevel > 0) {
                if (ThreadLocalRandom.current().nextInt(100) < config.mobGiveSplinteredEffectChanceAboveY0) { apply = true; duration = config.mobGiveSplinteredEffectDurationAboveY0; }
            } else {
                if (ThreadLocalRandom.current().nextInt(100) < config.mobGiveSplinteredEffectChanceBelowY0) { apply = true; duration = config.mobGiveSplinteredEffectDurationBelowY0; }
            }
            if (apply && GloomfallEffects.SPLINTERED != null) {
                target.addStatusEffect(new StatusEffectInstance(GloomfallEffects.SPLINTERED, duration, 0));
                target.getWorld().playSound(null, target.getX(), target.getY(), target.getZ(), GloomfallSoundEvents.GLOOMFALL_SPLINTER_EVENT, SoundCategory.HOSTILE, 1.0f, 1.0f);
                effectApplied = true;
            }
        }

        // Concussed
        if (!effectApplied && config.concussedApplyingMobs.contains(mobId)) {
            boolean apply = false;
            int duration = 0;
            if (yLevel > 0) {
                if (ThreadLocalRandom.current().nextInt(100) < config.mobGiveConcussedEffectChanceAboveY0) { apply = true; duration = config.mobGiveConcussedEffectDurationAboveY0; }
            } else {
                if (ThreadLocalRandom.current().nextInt(100) < config.mobGiveConcussedEffectChanceBelowY0) { apply = true; duration = config.mobGiveConcussedEffectDurationBelowY0; }
            }
            if (apply && GloomfallEffects.CONCUSSED != null) {
                target.addStatusEffect(new StatusEffectInstance(GloomfallEffects.CONCUSSED, duration, 0));
                target.getWorld().playSound(null, target.getX(), target.getY(), target.getZ(), GloomfallSoundEvents.GLOOMFALL_CONCUSSED_EVENT, SoundCategory.HOSTILE, 1.0f, 1.0f);
                effectApplied = true;
            }
        }

        if (!config.customMobAttackEffects.isEmpty()) {
            for (GloomfallConfig.MobAttackEffectConfig customEffect : config.customMobAttackEffects) {
                if (customEffect.mobId.equals(mobId)) {
                    if (ThreadLocalRandom.current().nextFloat() <= customEffect.chance) {
                        Identifier effectId = new Identifier(customEffect.effectId);
                        if (Registries.STATUS_EFFECT.containsId(effectId)) {
                            StatusEffect effect = Registries.STATUS_EFFECT.get(effectId);
                            if (effect != null) {
                                target.addStatusEffect(new StatusEffectInstance(effect, customEffect.duration, customEffect.amplifier));
                                effectApplied = true;
                            }
                        }
                    }
                }
            }
        }

        if (effectApplied) {
            gloomfall$lastEffectTime = target.age;
        }
    }
}