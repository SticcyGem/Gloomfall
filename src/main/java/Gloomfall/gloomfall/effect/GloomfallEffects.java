package Gloomfall.gloomfall.effect;

import Gloomfall.gloomfall.Gloomfall;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.entity.effect.StatusEffect;

public class GloomfallEffects {

    public static StatusEffect VULNERABILITY;
    public static StatusEffect SPLINTERED;
    public static StatusEffect CONCUSSED;

    public static void registerEffects() {
        VULNERABILITY = Registry.register(
                Registries.STATUS_EFFECT,
                new Identifier(Gloomfall.MOD_ID, "vulnerability"),
                new VulnerabilityEffect()
        );

        SPLINTERED = Registry.register(
                Registries.STATUS_EFFECT,
                new Identifier(Gloomfall.MOD_ID, "splintered"),
                new SplinteredEffect()
        );

        CONCUSSED = Registry.register(
                Registries.STATUS_EFFECT,
                new Identifier(Gloomfall.MOD_ID, "concussed"),
                new ConcussedEffect()
        );

        Gloomfall.LOGGER.info("Gloomfall Effects Registered.");
    }
}