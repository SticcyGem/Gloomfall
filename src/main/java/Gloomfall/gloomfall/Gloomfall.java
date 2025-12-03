package Gloomfall.gloomfall;

import Gloomfall.gloomfall.config.GloomfallConfig;
import Gloomfall.gloomfall.config.GloomfallDeepBuff;
import Gloomfall.gloomfall.handler.MobBuffHandler;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import Gloomfall.gloomfall.effect.GloomfallEffects;

public class Gloomfall implements ModInitializer {
    public static final String MOD_ID = "gloomfall";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        AutoConfig.register(GloomfallConfig.class, GsonConfigSerializer::new);

        AutoConfig.register(GloomfallDeepBuff.class, GsonConfigSerializer::new);

        GloomfallSoundEvents.registerSounds();

        MobBuffHandler.register();
        GloomfallEffects.registerEffects();

        LOGGER.info("Gloomfall initialized. Sounds and Deep Buffs registered.");
    }

    public static GloomfallConfig getConfig() {
        return AutoConfig.getConfigHolder(GloomfallConfig.class).getConfig();
    }

    public static GloomfallDeepBuff getDeepBuffConfig() {
        return AutoConfig.getConfigHolder(GloomfallDeepBuff.class).getConfig();
    }
}