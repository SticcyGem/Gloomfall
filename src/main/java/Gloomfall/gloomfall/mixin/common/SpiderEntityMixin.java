package Gloomfall.gloomfall.mixin.common;

import Gloomfall.gloomfall.Gloomfall;
import Gloomfall.gloomfall.config.GloomfallConfig;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.ThreadLocalRandom;

@Mixin(MobEntity.class)
public class SpiderEntityMixin {

    @Inject(method = "tryAttack", at = @At("RETURN"))
    private void gloomfall$trapPlayerOnAttack(Entity target, CallbackInfoReturnable<Boolean> cir) {
        MobEntity self = (MobEntity) (Object) this;

        GloomfallConfig config = Gloomfall.getConfig();
        if (config == null || !config.globalMobChangeEnabled || !config.spiderTrapPlayerWithCobwebAfterAttackingUnderY0) return;

        String mobId = Registries.ENTITY_TYPE.getId(self.getType()).toString();
        if (!config.spiderApplyingMobs.contains(mobId)) return;

        if (!cir.getReturnValue()) return;

        if (target instanceof PlayerEntity player) {
            if (player.getY() < 0) {
                BlockPos playerPos = player.getBlockPos();
                if (player.getWorld().getBlockState(playerPos).isAir()) {
                    player.getWorld().setBlockState(playerPos, Blocks.COBWEB.getDefaultState());
                }
            }
        }
    }

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