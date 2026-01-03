package Gloomfall.gloomfall.mixin.common;

import Gloomfall.gloomfall.Gloomfall;
import Gloomfall.gloomfall.config.GloomfallConfig;
import net.minecraft.block.Blocks;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.ThreadLocalRandom;

@Mixin(MobEntity.class)
public class SpiderEntityMixin {

    @Inject(method = "tick", at = @At("HEAD"))
    private void gloomfall$randomCobwebGeneration(CallbackInfo ci) {
        MobEntity self = (MobEntity) (Object) this;

        if (self.getWorld().isClient) return;

        GloomfallConfig config = Gloomfall.getConfig();
        if (config == null || !config.globalMobChangeEnabled) return;

        String mobId = Registries.ENTITY_TYPE.getId(self.getType()).toString();
        if (!config.spiderApplyingMobs.contains(mobId)) return;


        int interval;
        float chance;

        if (self.getY() < 0) {
            interval = config.spiderRandomlyGenerateCobwebIntervalBelowY0;
            chance = config.spiderRandomlyGenerateCobwebChanceUnderY0;
        } else {
            interval = config.spiderRandomlyGenerateCobwebIntervalAboveY0;
            chance = config.spiderRandomlyGenerateCobwebChanceAboveY0;
        }

        if (interval <= 0) return;

        if (self.age % interval == 0) {
            if (ThreadLocalRandom.current().nextFloat() < chance) {
                BlockPos spiderPos = self.getBlockPos();
                if (self.getWorld().getBlockState(spiderPos).isAir()) {
                    self.getWorld().setBlockState(spiderPos, Blocks.COBWEB.getDefaultState());
                }
            }
        }
    }
}