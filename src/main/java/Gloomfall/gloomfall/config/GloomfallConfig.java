package Gloomfall.gloomfall.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

import java.util.ArrayList;
import java.util.List;

@Config(name = "gloomfall")
public class GloomfallConfig implements ConfigData {

    // --- Audio Settings ---
    public boolean modEnabled = true;

    @ConfigEntry.BoundedDiscrete(min = 0, max = 100)
    public int musicVolume = 100;

    @ConfigEntry.BoundedDiscrete(min = -64, max = 320)
    public int yLevelMusic = 0;

    @ConfigEntry.BoundedDiscrete(min = 1, max = 30)
    public int transitionTime = 5;

    @ConfigEntry.BoundedDiscrete(min = 0, max = 60)
    public int entryDelay = 6;

    @ConfigEntry.BoundedDiscrete(min = 0, max = 20)
    public int glitchDelay = 4;

    @ConfigEntry.BoundedDiscrete(min = 0, max = 60)
    public int inBrightLightDurationCheckToStopDeepMusicPlayingWhenPLaying = 0;

    public boolean caveSoundsStillPlayUnderDeepMusic = true;

    public List<ArenaZone> excludedArenas = new ArrayList<>();

    public static class ArenaZone {
        public String name = "Arena Name";
        public int x = 0;
        public int z = 0;

        @ConfigEntry.BoundedDiscrete(min = -64, max = 320)
        public int yLevel = 0;

        public int radius = 100;
    }

    // --- Global Mob Changes ---
    @ConfigEntry.Category("global_mob_change")
    public boolean globalMobChangeEnabled = true;

    @ConfigEntry.Category("global_mob_change")
    public int effectApplicationCooldown = 30;

    // -- Vulnerability Settings --
    @ConfigEntry.Category("global_mob_change")
    public List<String> vulnerabilityApplyingMobs = new ArrayList<>(List.of("minecraft:zombie"));

    @ConfigEntry.Category("global_mob_change")
    @ConfigEntry.BoundedDiscrete(min = 0, max = 100)
    public int mobGiveVulnerabilityEffectChanceAboveY0 = 30;

    @ConfigEntry.Category("global_mob_change")
    public int mobGiveVulnerabilityEffectDurationAboveY0 = 100;

    @ConfigEntry.Category("global_mob_change")
    @ConfigEntry.BoundedDiscrete(min = 0, max = 100)
    public int mobGiveVulnerabilityEffectChanceBelowY0 = 90;

    @ConfigEntry.Category("global_mob_change")
    public int mobGiveVulnerabilityEffectDurationBelowY0 = 200;

    @ConfigEntry.Category("global_mob_change")
    public float vulnerabilityEffectIncreaseDamageTaken = 0.25f;

    @ConfigEntry.Category("global_mob_change")
    public float vulnerabilityOutgoingDamageReduction = 1.0f;

    // -- Splintered Settings --
    @ConfigEntry.Category("global_mob_change")
    public List<String> splinteredApplyingMobs = new ArrayList<>(List.of("minecraft:skeleton"));

    @ConfigEntry.Category("global_mob_change")
    @ConfigEntry.BoundedDiscrete(min = 0, max = 100)
    public int mobGiveSplinteredEffectChanceAboveY0 = 30;

    @ConfigEntry.Category("global_mob_change")
    public int mobGiveSplinteredEffectDurationAboveY0 = 100;

    @ConfigEntry.Category("global_mob_change")
    @ConfigEntry.BoundedDiscrete(min = 0, max = 100)
    public int mobGiveSplinteredEffectChanceBelowY0 = 90;

    @ConfigEntry.Category("global_mob_change")
    public int mobGiveSplinteredEffectDurationBelowY0 = 200;

    @ConfigEntry.Category("global_mob_change")
    public float splinteredEffectHealReduction = 0.4f;

    @ConfigEntry.Category("global_mob_change")
    public boolean splinteredDisableJump = true;

    // -- Concussed Settings --
    @ConfigEntry.Category("global_mob_change")
    public List<String> concussedApplyingMobs = new ArrayList<>(List.of("minecraft:creeper"));

    @ConfigEntry.Category("global_mob_change")
    @ConfigEntry.BoundedDiscrete(min = 0, max = 100)
    public int mobGiveConcussedEffectChanceAboveY0 = 30;

    @ConfigEntry.Category("global_mob_change")
    public int mobGiveConcussedEffectDurationAboveY0 = 100;

    @ConfigEntry.Category("global_mob_change")
    @ConfigEntry.BoundedDiscrete(min = 0, max = 100)
    public int mobGiveConcussedEffectChanceBelowY0 = 90;

    @ConfigEntry.Category("global_mob_change")
    public int mobGiveConcussedEffectDurationBelowY0 = 200;

    @ConfigEntry.Category("global_mob_change")
    public float concussedMovementSpeedReduction = 0.2f;

    // -- Spider Features --
    @ConfigEntry.Category("global_mob_change")
    public List<String> spiderApplyingMobs = new ArrayList<>(List.of("minecraft:spider", "minecraft:cave_spider"));

    @ConfigEntry.Category("global_mob_change")
    public boolean spiderTrapPlayerWithCobwebAfterAttackingUnderY0 = true;

    @ConfigEntry.Category("global_mob_change")
    public int spiderTrapPlayerCooldown = 30;

    @ConfigEntry.Category("global_mob_change")
    public int spiderDarknessUponAttackUnderY0Duration = 100;

    @ConfigEntry.Category("global_mob_change")
    public int spiderRandomlyGenerateCobwebIntervalAboveY0 = 6000;

    @ConfigEntry.Category("global_mob_change")
    public int spiderRandomlyGenerateCobwebIntervalBelowY0 = 6000;

    @ConfigEntry.Category("global_mob_change")
    public float spiderRandomlyGenerateCobwebChanceUnderY0 = 0.4f;

    @ConfigEntry.Category("global_mob_change")
    public float spiderRandomlyGenerateCobwebChanceAboveY0 = 0.1f;

    // --- Attack Effects ---
    @ConfigEntry.Category("custom_mob_effects")
    public List<MobAttackEffectConfig> customMobAttackEffects = new ArrayList<>();

    public static class MobAttackEffectConfig {
        public String mobId = "minecraft:husk";
        public String effectId = "minecraft:hunger";
        public int duration = 200;
        public int amplifier = 0;
        public float chance = 1.0f;
    }
}