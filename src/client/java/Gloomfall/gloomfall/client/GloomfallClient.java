package Gloomfall.gloomfall.client;

import Gloomfall.gloomfall.Gloomfall;
import Gloomfall.gloomfall.GloomfallSoundEvents;
import Gloomfall.gloomfall.config.GloomfallConfig;
import Gloomfall.gloomfall.mixin.client.vanilla.SoundManagerAccessor;
import Gloomfall.gloomfall.mixin.client.vanilla.SoundSystemAccessor;
import com.google.common.collect.Multimap;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.sound.SoundSystem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class GloomfallClient implements ClientModInitializer {

    public static GloomfallClient INSTANCE;

    private final CustomMusicManager musicManager = new CustomMusicManager();

    private CustomFadingSoundInstance currentMusicInstance = null;
    private CustomFadingSoundInstance fadingOutMusicInstance = null;

    private SoundInstance glitchSoundInstance = null;

    private int conditionsMetTimer = 0;
    private int postGlitchDelayTimer = 0;

    private int fadeDurationTicks = 100;
    private int currentFadeTicks = 0;
    private int fadeOutTicks = 0;

    public static boolean isGloomActive = false;

    @Override
    public void onInitializeClient() {
        INSTANCE = this;
        ClientTickEvents.END_CLIENT_TICK.register(this::onClientTick);

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(ClientCommandManager.literal("gloomfall")
                    .then(ClientCommandManager.literal("skip")
                            .executes(context -> {
                                if (isGloomActive) {
                                    skipCurrentTrack(context.getSource().getClient());
                                    context.getSource().sendFeedback(Text.of("§7[§cGloomfall§7] §fSkipping current track..."));
                                } else {
                                    context.getSource().sendFeedback(Text.of("§7[§cGloomfall§7] §cDeep music is not currently active."));
                                }
                                return 1;
                            })
                    )
            );
        });
    }

    private void onClientTick(MinecraftClient client) {
        if (client.player == null || client.world == null) {
            forceReset(client.getSoundManager());
            return;
        }

        handleMusicFading(client);

        GloomfallConfig config = Gloomfall.getConfig();
        boolean conditionsMet = areConditionsMet(client.player, client.world, config);

        int entryDelayTicks = config.entryDelay * 20;

        if (conditionsMet) {
            if (!isGloomActive) {
                conditionsMetTimer++;
                if (conditionsMetTimer >= entryDelayTicks) {
                    activateGloom(client, config);
                }
            } else {
                conditionsMetTimer = entryDelayTicks;

                if (postGlitchDelayTimer > 0) {
                    postGlitchDelayTimer--;
                    if (postGlitchDelayTimer == 0) {
                        Gloomfall.LOGGER.info("[Gloomfall] Post Glitch Delay finished. Starting Deep Music fade in.");
                    }
                } else {
                    ensureDeepMusicPlaying(client);
                }
            }
        } else {
            conditionsMetTimer = 0;

            if (isGloomActive) {
                startDeepFadeOut(client.getSoundManager());

                if (currentMusicInstance == null && fadingOutMusicInstance == null && glitchSoundInstance == null) {
                    isGloomActive = false;
                    Gloomfall.LOGGER.info("[Gloomfall] Conditions lost. Gloom sequence ended.");
                }
            }
        }
    }

    public static boolean areConditionsMet(PlayerEntity player, World world, GloomfallConfig config) {
        if (!config.modEnabled) return false;

        for (GloomfallConfig.ArenaZone arena : config.excludedArenas) {
            if (arena.x == 0 && arena.yLevel == 0 && arena.z == 0) {
                continue;
            }

            double dx = player.getX() - arena.x;
            double dy = player.getY() - arena.yLevel;
            double dz = player.getZ() - arena.z;
            double distSq = dx * dx + dy * dy + dz * dz;

            double radiusSq = (double) arena.radius * arena.radius;

            if (distSq <= radiusSq) {
                return false;
            }
        }

        int playerY = (int)Math.floor(player.getY());
        return playerY <= config.yLevelMusic;
    }

    private void activateGloom(MinecraftClient client, GloomfallConfig config) {
        Gloomfall.LOGGER.info("[Gloomfall] Entry Delay Met. Triggering Glitch.");
        isGloomActive = true;

        stopVanillaMusicInstant(client.getSoundManager(), config);

        if (GloomfallSoundEvents.GLOOMFALL_GLITCH_EVENT != null) {
            float randomPitch = (float)ThreadLocalRandom.current().nextDouble(0.95, 1.05);

            this.glitchSoundInstance = PositionedSoundInstance.master(
                    GloomfallSoundEvents.GLOOMFALL_GLITCH_EVENT,
                    randomPitch
            );

            client.getSoundManager().play(this.glitchSoundInstance);
        } else {
            Gloomfall.LOGGER.error("[Gloomfall] Glitch event is null! Check registration.");
        }

        postGlitchDelayTimer = config.glitchDelay * 20;
    }

    private void stopVanillaMusicInstant(SoundManager soundManager, GloomfallConfig config) {
        try {
            SoundSystem soundSystem = ((SoundManagerAccessor) soundManager).getSoundSystem();
            Multimap<SoundCategory, SoundInstance> allSounds = ((SoundSystemAccessor) soundSystem).getSounds();
            for (SoundInstance sound : new ArrayList<>(allSounds.get(SoundCategory.MUSIC))) soundManager.stop(sound);
            for (SoundInstance sound : new ArrayList<>(allSounds.get(SoundCategory.RECORDS))) soundManager.stop(sound);


            if (!config.caveSoundsStillPlayUnderDeepMusic) {
                for (SoundInstance sound : new ArrayList<>(allSounds.get(SoundCategory.AMBIENT))) {
                    if (sound.isRepeatable()) soundManager.stop(sound);
                }
            }
        } catch (Exception e) { }
    }

    private void handleMusicFading(MinecraftClient client) {
        GloomfallConfig config = Gloomfall.getConfig();
        SoundManager soundManager = client.getSoundManager();
        this.fadeDurationTicks = Math.max(20, config.transitionTime * 20);

        if (currentMusicInstance != null) {
            if (currentFadeTicks < fadeDurationTicks) currentFadeTicks++;
            float progress = (float) currentFadeTicks / (float) fadeDurationTicks;
            currentMusicInstance.fadeVolume = MathHelper.clamp(progress, 0.0f, 1.0f);

            if (!soundManager.isPlaying(currentMusicInstance) && currentFadeTicks >= fadeDurationTicks) {
                playNewTrack(client);
            }
        }

        if (fadingOutMusicInstance != null) {
            if (fadeOutTicks > 0) fadeOutTicks--;
            float progress = (float) fadeOutTicks / (float) fadeDurationTicks;
            fadingOutMusicInstance.fadeVolume = MathHelper.clamp(progress, 0.0f, 1.0f);

            if (fadeOutTicks <= 0) {
                fadingOutMusicInstance.setDone();
                fadingOutMusicInstance = null;
            }
        }
    }

    private void ensureDeepMusicPlaying(MinecraftClient client) {
        if (currentMusicInstance == null) {
            playNewTrack(client);
        } else if (!client.getSoundManager().isPlaying(currentMusicInstance)) {
            playNewTrack(client);
        }
    }

    private void playNewTrack(MinecraftClient client) {
        currentMusicInstance = musicManager.getNextTrackInstance();
        if (currentMusicInstance != null) {
            client.getSoundManager().play(currentMusicInstance);
            currentFadeTicks = 0;
        }
    }

    public void skipCurrentTrack(MinecraftClient client) {
        if (this.currentMusicInstance != null) {
            // Stop current track immediately
            client.getSoundManager().stop(this.currentMusicInstance);
            this.currentMusicInstance.setDone();
            this.currentMusicInstance = null;
        }
    }

    private void startDeepFadeOut(SoundManager soundManager) {
        if (this.glitchSoundInstance != null) {
            soundManager.stop(this.glitchSoundInstance);
            this.glitchSoundInstance = null;
        }

        // Start fading out the deep music
        if (currentMusicInstance != null) {
            if (fadingOutMusicInstance != null) {
                fadingOutMusicInstance.setDone();
                soundManager.stop(fadingOutMusicInstance);
            }
            fadingOutMusicInstance = currentMusicInstance;
            currentMusicInstance = null;
            fadeOutTicks = currentFadeTicks;
            currentFadeTicks = 0;
        }
    }

    private void forceReset(SoundManager soundManager) {
        if (currentMusicInstance != null) {
            currentMusicInstance.setDone();
            soundManager.stop(currentMusicInstance);
            currentMusicInstance = null;
        }
        if (fadingOutMusicInstance != null) {
            fadingOutMusicInstance.setDone();
            soundManager.stop(fadingOutMusicInstance);
            fadingOutMusicInstance = null;
        }
        if (glitchSoundInstance != null) {
            soundManager.stop(glitchSoundInstance);
            glitchSoundInstance = null;
        }
        isGloomActive = false;
        conditionsMetTimer = 0;
        postGlitchDelayTimer = 0;
    }
}