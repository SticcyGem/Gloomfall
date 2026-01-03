package Gloomfall.gloomfall.handler;

import Gloomfall.gloomfall.Gloomfall;
import Gloomfall.gloomfall.config.GloomfallDeepBuff;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;

import java.util.UUID;

public class MobBuffHandler {

    private static final UUID GLOOMFALL_UUID = UUID.fromString("93c72b10-06f9-462a-8656-749104050215");

    public static void register() {
        ServerEntityEvents.ENTITY_LOAD.register(MobBuffHandler::onEntityLoad);
    }

    private static void onEntityLoad(Entity entity, ServerWorld world) {
        GloomfallDeepBuff config = Gloomfall.getDeepBuffConfig();

        if (config == null || !config.buffsEnabled) return;
        if (!(entity instanceof LivingEntity living)) return;
        if (world.isClient) return;

        if (living.getCommandTags().contains("gloomfall:buffed")) {
            return;
        }

        int y = (int) entity.getY();
        String id = Registries.ENTITY_TYPE.getId(entity.getType()).toString();

        for (GloomfallDeepBuff.BuffZone zone : config.zones) {
            if (y >= zone.minY && y <= zone.maxY) {
                for (GloomfallDeepBuff.MobStats stats : zone.mobs) {
                    if (stats.entityId.equals(id)) {
                        applyBuffs(living, stats);
                        living.addCommandTag("gloomfall:buffed");
                        return;
                    }
                }
            }
        }
    }

    private static void applyBuffs(LivingEntity entity, GloomfallDeepBuff.MobStats stats) {
        // 1. Multiplicative Stats
        applyPercent(entity, EntityAttributes.GENERIC_ATTACK_DAMAGE, stats.attackDamage);
        applyPercent(entity, EntityAttributes.GENERIC_MOVEMENT_SPEED, stats.movementSpeed);
        applyPercent(entity, EntityAttributes.GENERIC_FOLLOW_RANGE, stats.followRange);
        applyPercent(entity, EntityAttributes.ZOMBIE_SPAWN_REINFORCEMENTS, stats.spawnReinforcements);

        // 2. Additive Stats
        applyFlat(entity, EntityAttributes.GENERIC_ARMOR, stats.armor);
        applyFlat(entity, EntityAttributes.GENERIC_ATTACK_KNOCKBACK, stats.attackKnockback);
        applyFlat(entity, EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, stats.knockbackResistance);
        applyFlat(entity, EntityAttributes.GENERIC_ARMOR_TOUGHNESS, stats.armorToughness);

        if (stats.stepHeight > 0) {
            entity.setStepHeight(entity.getStepHeight() + (float) stats.stepHeight);
        }

        if (stats.grantFireResistance) {
            entity.addStatusEffect(new StatusEffectInstance(
                    StatusEffects.FIRE_RESISTANCE,
                    -1,
                    0,
                    false,
                    false
            ));
        }

        if (stats.explosionResistance > 0) {
            entity.addCommandTag("gloomfall:expl_res_" + stats.explosionResistance);
        }
    }

    private static void applyPercent(LivingEntity entity, EntityAttribute attribute, double percent) {
        if (Math.abs(percent) < 0.001) return;

        EntityAttributeInstance instance = entity.getAttributeInstance(attribute);
        if (instance != null) {
            instance.removeModifier(GLOOMFALL_UUID);
            EntityAttributeModifier modifier = new EntityAttributeModifier(
                    GLOOMFALL_UUID,
                    "Gloomfall Buff %",
                    percent,
                    EntityAttributeModifier.Operation.MULTIPLY_BASE
            );
            instance.addPersistentModifier(modifier);
        }
    }

    private static void applyFlat(LivingEntity entity, EntityAttribute attribute, double value) {
        EntityAttributeInstance instance = entity.getAttributeInstance(attribute);
        if (instance != null) {
            instance.removeModifier(GLOOMFALL_UUID);

            if (Math.abs(value) >= 0.001) {
                EntityAttributeModifier modifier = new EntityAttributeModifier(
                        GLOOMFALL_UUID,
                        "Gloomfall Buff Flat",
                        value,
                        EntityAttributeModifier.Operation.ADDITION
                );
                instance.addPersistentModifier(modifier);
            }
        }
    }
}