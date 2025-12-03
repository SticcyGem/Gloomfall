package Gloomfall.gloomfall.client;

import Gloomfall.gloomfall.config.GloomfallConfig;
import Gloomfall.gloomfall.config.GloomfallDeepBuff;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class GloomfallModMenuIntegration implements ModMenuApi {

    private static String lastCategory = "General";

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> {
            GloomfallConfig config = AutoConfig.getConfigHolder(GloomfallConfig.class).getConfig();
            GloomfallDeepBuff deepBuffConfig = AutoConfig.getConfigHolder(GloomfallDeepBuff.class).getConfig();

            ConfigBuilder builder = ConfigBuilder.create()
                    .setParentScreen(parent)
                    .setTitle(Text.of("Gloomfall Configuration"));

            builder.setSavingRunnable(() -> {
                AutoConfig.getConfigHolder(GloomfallConfig.class).save();
                AutoConfig.getConfigHolder(GloomfallDeepBuff.class).save();
            });

            ConfigEntryBuilder entryBuilder = builder.entryBuilder();

            ConfigCategory general = builder.getOrCreateCategory(Text.of("General"));
            buildGeneralCategory(general, entryBuilder, config);

            ConfigCategory arenas = builder.getOrCreateCategory(Text.of("Excluded Arenas"));
            buildArenasCategory(arenas, entryBuilder, config, parent);

            ConfigCategory mobBuffs = builder.getOrCreateCategory(Text.of("Deep Mob Buffs"));
            buildDeepBuffsCategory(mobBuffs, entryBuilder, deepBuffConfig, parent);

            ConfigCategory globalMobs = builder.getOrCreateCategory(Text.of("Global Mob Changes"));
            buildGlobalMobChangesCategory(globalMobs, entryBuilder, config);

            ConfigCategory customEffects = builder.getOrCreateCategory(Text.of("Mob Attack Effects"));
            buildCustomEffectsCategory(customEffects, entryBuilder, config, parent);

            if (lastCategory.equals("Excluded Arenas")) builder.setFallbackCategory(arenas);
            else if (lastCategory.equals("Deep Mob Buffs")) builder.setFallbackCategory(mobBuffs);
            else if (lastCategory.equals("Global Mob Changes")) builder.setFallbackCategory(globalMobs);
            else if (lastCategory.equals("Mob Attack Effects")) builder.setFallbackCategory(customEffects);
            else builder.setFallbackCategory(general);

            return builder.build();
        };
    }

    private void buildGeneralCategory(ConfigCategory category, ConfigEntryBuilder entryBuilder, GloomfallConfig config) {
        category.addEntry(entryBuilder.startBooleanToggle(Text.of("Mod Enabled"), config.modEnabled)
                .setDefaultValue(true).setSaveConsumer(v -> config.modEnabled = v).build());
        category.addEntry(entryBuilder.startBooleanToggle(Text.of("Cave Sounds Play Under Deep Music"), config.caveSoundsStillPlayUnderDeepMusic)
                .setDefaultValue(true).setSaveConsumer(v -> config.caveSoundsStillPlayUnderDeepMusic = v).build());
        category.addEntry(entryBuilder.startIntSlider(Text.of("Music Volume"), config.musicVolume, 0, 100)
                .setDefaultValue(100).setSaveConsumer(v -> config.musicVolume = v).build());
        category.addEntry(entryBuilder.startIntSlider(Text.of("Max Y-Level for Music"), config.yLevelMusic, -64, 320)
                .setDefaultValue(0).setSaveConsumer(v -> config.yLevelMusic = v).build());
        category.addEntry(entryBuilder.startIntSlider(Text.of("Transition Time (s)"), config.transitionTime, 1, 30)
                .setDefaultValue(5).setSaveConsumer(v -> config.transitionTime = v).build());
        category.addEntry(entryBuilder.startIntSlider(Text.of("Entry Delay (s)"), config.entryDelay, 0, 60)
                .setDefaultValue(6).setSaveConsumer(v -> config.entryDelay = v).build());
        category.addEntry(entryBuilder.startIntSlider(Text.of("Glitch Delay (s)"), config.glitchDelay, 0, 20)
                .setDefaultValue(4).setSaveConsumer(v -> config.glitchDelay = v).build());
        category.addEntry(entryBuilder.startIntSlider(Text.of("Bright Light Stop Delay (s)"), config.inBrightLightDurationCheckToStopDeepMusicPlayingWhenPLaying, 0, 60)
                .setDefaultValue(0).setSaveConsumer(v -> config.inBrightLightDurationCheckToStopDeepMusicPlayingWhenPLaying = v).build());
    }

    private void buildGlobalMobChangesCategory(ConfigCategory category, ConfigEntryBuilder entryBuilder, GloomfallConfig config) {
        category.addEntry(entryBuilder.startBooleanToggle(Text.of("Enable Global Mob Changes"), config.globalMobChangeEnabled)
                .setDefaultValue(true).setSaveConsumer(v -> config.globalMobChangeEnabled = v).build());
        category.addEntry(entryBuilder.startIntField(Text.of("Effect Application Cooldown"), config.effectApplicationCooldown)
                .setDefaultValue(30).setSaveConsumer(v -> config.effectApplicationCooldown = v).build());

        // --- Vulnerability ---
        category.addEntry(entryBuilder.startTextDescription(Text.of("§6Vulnerability (Zombies)")).build());
        category.addEntry(entryBuilder.startStrList(Text.of("Vuln Mobs List"), config.vulnerabilityApplyingMobs)
                .setDefaultValue(List.of("minecraft:zombie")).setSaveConsumer(v -> config.vulnerabilityApplyingMobs = v).build());
        category.addEntry(entryBuilder.startIntSlider(Text.of("Vuln Chance (> Y0) %"), config.mobGiveVulnerabilityEffectChanceAboveY0, 0, 100)
                .setDefaultValue(30).setSaveConsumer(v -> config.mobGiveVulnerabilityEffectChanceAboveY0 = v).build());
        category.addEntry(entryBuilder.startIntField(Text.of("Vuln Duration (> Y0)"), config.mobGiveVulnerabilityEffectDurationAboveY0)
                .setDefaultValue(100).setSaveConsumer(v -> config.mobGiveVulnerabilityEffectDurationAboveY0 = v).build());
        category.addEntry(entryBuilder.startIntSlider(Text.of("Vuln Chance (< Y0) %"), config.mobGiveVulnerabilityEffectChanceBelowY0, 0, 100)
                .setDefaultValue(90).setSaveConsumer(v -> config.mobGiveVulnerabilityEffectChanceBelowY0 = v).build());
        category.addEntry(entryBuilder.startIntField(Text.of("Vuln Duration (< Y0)"), config.mobGiveVulnerabilityEffectDurationBelowY0)
                .setDefaultValue(200).setSaveConsumer(v -> config.mobGiveVulnerabilityEffectDurationBelowY0 = v).build());
        category.addEntry(entryBuilder.startFloatField(Text.of("Vuln Damage Multiplier"), config.vulnerabilityEffectIncreaseDamageTaken)
                .setDefaultValue(0.25f).setTooltip(Text.of("0.25 = +25% Damage")).setSaveConsumer(v -> config.vulnerabilityEffectIncreaseDamageTaken = v).build());
        category.addEntry(entryBuilder.startFloatField(Text.of("Vuln Outgoing Dmg Reduc."), config.vulnerabilityOutgoingDamageReduction)
                .setDefaultValue(1.0f).setTooltip(Text.of("Flat damage reduction")).setSaveConsumer(v -> config.vulnerabilityOutgoingDamageReduction = v).build());

        // --- Splintered ---
        category.addEntry(entryBuilder.startTextDescription(Text.of("§6Splintered (Skeletons)")).build());
        category.addEntry(entryBuilder.startStrList(Text.of("Splintered Mobs List"), config.splinteredApplyingMobs)
                .setDefaultValue(List.of("minecraft:skeleton")).setSaveConsumer(v -> config.splinteredApplyingMobs = v).build());
        category.addEntry(entryBuilder.startIntSlider(Text.of("Splintered Chance (> Y0) %"), config.mobGiveSplinteredEffectChanceAboveY0, 0, 100)
                .setDefaultValue(30).setSaveConsumer(v -> config.mobGiveSplinteredEffectChanceAboveY0 = v).build());
        category.addEntry(entryBuilder.startIntField(Text.of("Splintered Duration (> Y0)"), config.mobGiveSplinteredEffectDurationAboveY0)
                .setDefaultValue(100).setSaveConsumer(v -> config.mobGiveSplinteredEffectDurationAboveY0 = v).build());
        category.addEntry(entryBuilder.startIntSlider(Text.of("Splintered Chance (< Y0) %"), config.mobGiveSplinteredEffectChanceBelowY0, 0, 100)
                .setDefaultValue(90).setSaveConsumer(v -> config.mobGiveSplinteredEffectChanceBelowY0 = v).build());
        category.addEntry(entryBuilder.startIntField(Text.of("Splintered Duration (< Y0)"), config.mobGiveSplinteredEffectDurationBelowY0)
                .setDefaultValue(200).setSaveConsumer(v -> config.mobGiveSplinteredEffectDurationBelowY0 = v).build());
        category.addEntry(entryBuilder.startFloatField(Text.of("Splintered Heal Reduction"), config.splinteredEffectHealReduction)
                .setDefaultValue(0.4f).setTooltip(Text.of("0.4 = -40% Healing")).setSaveConsumer(v -> config.splinteredEffectHealReduction = v).build());
        category.addEntry(entryBuilder.startBooleanToggle(Text.of("Splintered Disable Jump"), config.splinteredDisableJump)
                .setDefaultValue(true).setSaveConsumer(v -> config.splinteredDisableJump = v).build());

        // --- Concussed ---
        category.addEntry(entryBuilder.startTextDescription(Text.of("§6Concussed (Creepers)")).build());
        category.addEntry(entryBuilder.startStrList(Text.of("Concussed Mobs List"), config.concussedApplyingMobs)
                .setDefaultValue(List.of("minecraft:creeper")).setSaveConsumer(v -> config.concussedApplyingMobs = v).build());
        category.addEntry(entryBuilder.startIntSlider(Text.of("Concussed Chance (> Y0) %"), config.mobGiveConcussedEffectChanceAboveY0, 0, 100)
                .setDefaultValue(30).setSaveConsumer(v -> config.mobGiveConcussedEffectChanceAboveY0 = v).build());
        category.addEntry(entryBuilder.startIntField(Text.of("Concussed Duration (> Y0)"), config.mobGiveConcussedEffectDurationAboveY0)
                .setDefaultValue(100).setSaveConsumer(v -> config.mobGiveConcussedEffectDurationAboveY0 = v).build());
        category.addEntry(entryBuilder.startIntSlider(Text.of("Concussed Chance (< Y0) %"), config.mobGiveConcussedEffectChanceBelowY0, 0, 100)
                .setDefaultValue(90).setSaveConsumer(v -> config.mobGiveConcussedEffectChanceBelowY0 = v).build());
        category.addEntry(entryBuilder.startIntField(Text.of("Concussed Duration (< Y0)"), config.mobGiveConcussedEffectDurationBelowY0)
                .setDefaultValue(200).setSaveConsumer(v -> config.mobGiveConcussedEffectDurationBelowY0 = v).build());
        category.addEntry(entryBuilder.startFloatField(Text.of("Concussed Speed Reduction"), config.concussedMovementSpeedReduction)
                .setDefaultValue(0.1f).setTooltip(Text.of("0.1 = -10% Speed")).setSaveConsumer(v -> config.concussedMovementSpeedReduction = v).build());

        // --- Spider Web Features ---
        category.addEntry(entryBuilder.startTextDescription(Text.of("§6Spiders")).build());
        category.addEntry(entryBuilder.startStrList(Text.of("Spider Mobs List"), config.spiderApplyingMobs)
                .setDefaultValue(List.of("minecraft:spider", "minecraft:cave_spider")).setSaveConsumer(v -> config.spiderApplyingMobs = v).build());
        category.addEntry(entryBuilder.startBooleanToggle(Text.of("Trap Player (Y < 0)"), config.spiderTrapPlayerWithCobwebAfterAttackingUnderY0)
                .setDefaultValue(true).setSaveConsumer(v -> config.spiderTrapPlayerWithCobwebAfterAttackingUnderY0 = v).build());
        category.addEntry(entryBuilder.startIntField(Text.of("Web Attack Cooldown (Ticks)"), config.spiderTrapPlayerCooldown)
                .setDefaultValue(30).setSaveConsumer(v -> config.spiderTrapPlayerCooldown = v).build());
        category.addEntry(entryBuilder.startIntField(Text.of("Darkness Duration (< Y0)"), config.spiderDarknessUponAttackUnderY0Duration)
                .setDefaultValue(100).setSaveConsumer(v -> config.spiderDarknessUponAttackUnderY0Duration = v).build());
        category.addEntry(entryBuilder.startIntField(Text.of("Web Gen Interval (> Y0) Ticks"), config.spiderRandomlyGenerateCobwebIntervalAboveY0)
                .setDefaultValue(6000).setSaveConsumer(v -> config.spiderRandomlyGenerateCobwebIntervalAboveY0 = v).build());
        category.addEntry(entryBuilder.startIntField(Text.of("Web Gen Interval (< Y0) Ticks"), config.spiderRandomlyGenerateCobwebIntervalBelowY0)
                .setDefaultValue(6000).setSaveConsumer(v -> config.spiderRandomlyGenerateCobwebIntervalBelowY0 = v).build());
        category.addEntry(entryBuilder.startFloatField(Text.of("Web Chance (< Y0)"), config.spiderRandomlyGenerateCobwebChanceUnderY0)
                .setDefaultValue(0.4f).setTooltip(Text.of("Probability 0.0 - 1.0")).setSaveConsumer(v -> config.spiderRandomlyGenerateCobwebChanceUnderY0 = v).build());
        category.addEntry(entryBuilder.startFloatField(Text.of("Web Chance (> Y0)"), config.spiderRandomlyGenerateCobwebChanceAboveY0)
                .setDefaultValue(0.1f).setTooltip(Text.of("Probability 0.0 - 1.0")).setSaveConsumer(v -> config.spiderRandomlyGenerateCobwebChanceAboveY0 = v).build());
    }

    private void buildCustomEffectsCategory(ConfigCategory category, ConfigEntryBuilder entryBuilder, GloomfallConfig config, net.minecraft.client.gui.screen.Screen parent) {
        category.addEntry(buildButtonEntry(Text.of("✚ Add New Custom Effect"), () -> {
            GloomfallConfig.MobAttackEffectConfig newEffect = new GloomfallConfig.MobAttackEffectConfig();
            config.customMobAttackEffects.add(newEffect);
            lastCategory = "Mob Attack Effects";
            MinecraftClient.getInstance().setScreen(getModConfigScreenFactory().create(parent));
        }));

        for (int i = 0; i < config.customMobAttackEffects.size(); i++) {
            GloomfallConfig.MobAttackEffectConfig effectConfig = config.customMobAttackEffects.get(i);
            final int index = i;
            List<AbstractConfigListEntry> entries = new ArrayList<>();
            entries.add(entryBuilder.startStrField(Text.of("Mob ID"), effectConfig.mobId).setDefaultValue("minecraft:husk").setSaveConsumer(v -> effectConfig.mobId = v).build());
            entries.add(entryBuilder.startStrField(Text.of("Effect ID"), effectConfig.effectId).setDefaultValue("minecraft:hunger").setSaveConsumer(v -> effectConfig.effectId = v).build());
            entries.add(entryBuilder.startIntField(Text.of("Duration"), effectConfig.duration).setDefaultValue(200).setSaveConsumer(v -> effectConfig.duration = v).build());
            entries.add(entryBuilder.startIntField(Text.of("Amplifier"), effectConfig.amplifier).setDefaultValue(0).setSaveConsumer(v -> effectConfig.amplifier = v).build());
            entries.add(entryBuilder.startFloatField(Text.of("Chance"), effectConfig.chance).setDefaultValue(1.0f).setSaveConsumer(v -> effectConfig.chance = v).build());
            entries.add(buildButtonEntry(Text.of("§cDelete"), () -> {
                config.customMobAttackEffects.remove(index);
                lastCategory = "Mob Attack Effects";
                MinecraftClient.getInstance().setScreen(getModConfigScreenFactory().create(parent));
            }));
            category.addEntry(entryBuilder.startSubCategory(Text.of(effectConfig.mobId + " -> " + effectConfig.effectId), entries).setExpanded(false).build());
        }
    }

    private void buildArenasCategory(ConfigCategory category, ConfigEntryBuilder entryBuilder, GloomfallConfig config, net.minecraft.client.gui.screen.Screen parent) {
        category.addEntry(buildButtonEntry(Text.of("✚ Add Arena at Current Position"), () -> {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player != null && client.world != null && client.world.getRegistryKey() == World.OVERWORLD) {
                GloomfallConfig.ArenaZone newZone = new GloomfallConfig.ArenaZone();
                newZone.name = "Arena " + (config.excludedArenas.size() + 1);
                newZone.x = (int) client.player.getX();
                newZone.yLevel = (int) client.player.getY();
                newZone.z = (int) client.player.getZ();
                config.excludedArenas.add(newZone);
                lastCategory = "Excluded Arenas";
                MinecraftClient.getInstance().setScreen(getModConfigScreenFactory().create(parent));
            }
        }));

        for (int i = 0; i < config.excludedArenas.size(); i++) {
            GloomfallConfig.ArenaZone zone = config.excludedArenas.get(i);
            final int index = i;
            category.addEntry(entryBuilder.startSubCategory(Text.of("Arena: " + zone.name), java.util.List.of(
                    entryBuilder.startStrField(Text.of("Name"), zone.name).setDefaultValue("Arena").setSaveConsumer(v -> zone.name = v).build(),
                    entryBuilder.startIntField(Text.of("X"), zone.x).setDefaultValue(0).setSaveConsumer(v -> zone.x = v).build(),
                    entryBuilder.startIntField(Text.of("Y"), zone.yLevel).setDefaultValue(0).setSaveConsumer(v -> zone.yLevel = v).build(),
                    entryBuilder.startIntField(Text.of("Z"), zone.z).setDefaultValue(0).setSaveConsumer(v -> zone.z = v).build(),
                    entryBuilder.startIntField(Text.of("Radius"), zone.radius).setDefaultValue(100).setSaveConsumer(v -> zone.radius = v).build(),
                    buildButtonEntry(Text.of("§cDelete"), () -> {
                        config.excludedArenas.remove(index);
                        lastCategory = "Excluded Arenas";
                        MinecraftClient.getInstance().setScreen(getModConfigScreenFactory().create(parent));
                    })
            )).build());
        }
    }

    private void buildDeepBuffsCategory(ConfigCategory category, ConfigEntryBuilder entryBuilder, GloomfallDeepBuff config, net.minecraft.client.gui.screen.Screen parent) {
        category.addEntry(entryBuilder.startBooleanToggle(Text.of("Enable Mob Buffs"), config.buffsEnabled)
                .setDefaultValue(true).setSaveConsumer(v -> config.buffsEnabled = v).build());

        category.addEntry(buildButtonEntry(Text.of("✚ Add New Buff Zone"), () -> {
            GloomfallDeepBuff.BuffZone newZone = new GloomfallDeepBuff.BuffZone();
            newZone.name = "Zone " + (config.zones.size() + 1);
            config.zones.add(newZone);
            lastCategory = "Deep Mob Buffs";
            MinecraftClient.getInstance().setScreen(getModConfigScreenFactory().create(parent));
        }));

        for (int z = 0; z < config.zones.size(); z++) {
            GloomfallDeepBuff.BuffZone zone = config.zones.get(z);
            final int zoneIndex = z;
            List<AbstractConfigListEntry> zoneEntries = new ArrayList<>();
            zoneEntries.add(entryBuilder.startStrField(Text.of("Zone Name"), zone.name).setDefaultValue("Zone").setSaveConsumer(v -> zone.name = v).build());
            zoneEntries.add(entryBuilder.startIntSlider(Text.of("Min Y"), zone.minY, -64, 320).setDefaultValue(-64).setSaveConsumer(v -> zone.minY = v).build());
            zoneEntries.add(entryBuilder.startIntSlider(Text.of("Max Y"), zone.maxY, -64, 320).setDefaultValue(0).setSaveConsumer(v -> zone.maxY = v).build());

            zoneEntries.add(buildButtonEntry(Text.of("✚ Add Mob to Zone"), () -> {
                zone.mobs.add(new GloomfallDeepBuff.MobStats("minecraft:zombie", 0,0,0,0,0,0,0,0, false));
                lastCategory = "Deep Mob Buffs";
                MinecraftClient.getInstance().setScreen(getModConfigScreenFactory().create(parent));
            }));

            for (int m = 0; m < zone.mobs.size(); m++) {
                GloomfallDeepBuff.MobStats mob = zone.mobs.get(m);
                final int mobIndex = m;
                List<AbstractConfigListEntry> mobEntries = new ArrayList<>();

                AbstractConfigListEntry entityIdEntry = entryBuilder.startStrField(Text.of("Entity ID"), mob.entityId)
                        .setDefaultValue("minecraft:zombie").setSaveConsumer(v -> mob.entityId = v).build();
                mobEntries.add(entityIdEntry);
                mobEntries.add(buildButtonEntry(Text.of("↻ Refresh ID"), () -> {
                    entityIdEntry.save();
                    lastCategory = "Deep Mob Buffs";
                    MinecraftClient.getInstance().setScreen(getModConfigScreenFactory().create(parent));
                }));

                // Multiplicative Fields (Percent)
                mobEntries.add(createPercentField(entryBuilder, "Attack Damage", mob.attackDamage, v -> mob.attackDamage = v));
                mobEntries.add(createPercentField(entryBuilder, "Move Speed", mob.movementSpeed, v -> mob.movementSpeed = v));
                mobEntries.add(createPercentField(entryBuilder, "Follow Range", mob.followRange, v -> mob.followRange = v));
                mobEntries.add(createPercentField(entryBuilder, "Reinforcements", mob.spawnReinforcements, v -> mob.spawnReinforcements = v));

                // Additive Fields (Flat)
                mobEntries.add(createFlatField(entryBuilder, "Attack Knockback", mob.attackKnockback, v -> mob.attackKnockback = v));
                mobEntries.add(createFlatField(entryBuilder, "Armor", mob.armor, v -> mob.armor = v));
                mobEntries.add(createFlatField(entryBuilder, "Toughness", mob.armorToughness, v -> mob.armorToughness = v));
                mobEntries.add(createFlatField(entryBuilder, "Knockback Res", mob.knockbackResistance, v -> mob.knockbackResistance = v));

                mobEntries.add(createFlatField(entryBuilder, "Step Height Add", mob.stepHeight, v -> mob.stepHeight = v));
                mobEntries.add(entryBuilder.startBooleanToggle(Text.of("Grant Fire Res"), mob.grantFireResistance).setDefaultValue(false).setSaveConsumer(v -> mob.grantFireResistance = v).build());

                mobEntries.add(buildButtonEntry(Text.of("§cRemove Mob"), () -> {
                    zone.mobs.remove(mobIndex);
                    lastCategory = "Deep Mob Buffs";
                    MinecraftClient.getInstance().setScreen(getModConfigScreenFactory().create(parent));
                }));
                zoneEntries.add(entryBuilder.startSubCategory(Text.of("Mob: " + mob.entityId), mobEntries).setExpanded(false).build());
            }

            zoneEntries.add(buildButtonEntry(Text.of("§cDelete Zone"), () -> {
                config.zones.remove(zoneIndex);
                lastCategory = "Deep Mob Buffs";
                MinecraftClient.getInstance().setScreen(getModConfigScreenFactory().create(parent));
            }));
            category.addEntry(entryBuilder.startSubCategory(Text.of("Zone: " + zone.name), zoneEntries).setExpanded(true).build());
        }
    }

    private AbstractConfigListEntry createPercentField(ConfigEntryBuilder builder, String name, double value, Consumer<Double> consumer) {
        return builder.startDoubleField(Text.of(name + " (%)"), value)
                .setDefaultValue(0.0)
                .setTooltip(Text.of("Multiplicative: 0.5 = +50%, 1.0 = +100%"))
                .setSaveConsumer(consumer)
                .build();
    }

    private AbstractConfigListEntry createFlatField(ConfigEntryBuilder builder, String name, double value, Consumer<Double> consumer) {
        return builder.startDoubleField(Text.of(name + " (Flat)"), value)
                .setDefaultValue(0.0)
                .setTooltip(Text.of("Additive: 2.0 = Adds 2.0 to the value directly"))
                .setSaveConsumer(consumer)
                .build();
    }

    private AbstractConfigListEntry<Object> buildButtonEntry(Text text, Runnable onClick) {
        return new AbstractConfigListEntry<Object>(Text.empty(), false) {
            private final ButtonWidget widget = ButtonWidget.builder(text, button -> onClick.run()).dimensions(0, 0, 200, 20).build();
            @Override public void render(DrawContext context, int index, int y, int x, int w, int h, int mx, int my, boolean hov, float tick) {
                widget.setX(x + (w / 2) - (widget.getWidth() / 2)); widget.setY(y); widget.render(context, mx, my, tick);
            }
            @Override public java.util.List<? extends Element> children() { return Collections.singletonList(widget); }
            @Override public java.util.List<? extends Selectable> narratables() { return Collections.singletonList(widget); }
            @Override public Object getValue() { return null; }
            @Override public Optional<Object> getDefaultValue() { return Optional.empty(); }
            @Override public void save() {}
        };
    }
}