package Gloomfall.gloomfall.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;

import java.util.ArrayList;
import java.util.List;

@Config(name = "gloomfall_deep_buffs")
public class GloomfallDeepBuff implements ConfigData {

    public boolean buffsEnabled = true;


    public List<BuffZone> zones = new ArrayList<>();

    public GloomfallDeepBuff() {
        BuffZone deepZone = new BuffZone();
        deepZone.name = "Deep Caves (Example)";
        deepZone.minY = -64;
        deepZone.maxY = 0;

        // 1. Zombie
        // Attack: 35%, KB: 0.5 (Flat), Speed: 40%, Armor: 2.5, Tough: 1.0, KBRes: 0.15, Follow: 15%, Reinf: 0.15, ExplosionRes: 0.75
        deepZone.mobs.add(new MobStats("minecraft:zombie", 0.35, 0.5, 0.40, 2.5, 1.0, 0.15, 0.15, 0.15, 0.75, true));

        // 2. Skeleton
        // Attack: 30%, KB: 0.4, Speed: 25%, Armor: 2.5, Tough: 1.0, KBRes: 0.25, Follow: 15%, ExplosionRes: 0.75
        deepZone.mobs.add(new MobStats("minecraft:skeleton", 0.30, 0.4, 0.25, 2.5, 1.0, 0.25, 0.15, 0.0, 0.75, true));

        // 3. Stray (Same as Skeleton)
        deepZone.mobs.add(new MobStats("minecraft:stray", 0.30, 0.4, 0.25, 2.5, 1.0, 0.25, 0.15, 0.0, 0.75, true));

        // 4. Spider
        // Attack: 15%, KB: 0, Speed: 50%, Armor: 2.0, Tough: 0.5, KBRes: 0.30, Follow: 20%, ExplosionRes: 0.75
        deepZone.mobs.add(new MobStats("minecraft:spider", 0.15, 0.0, 0.50, 2.0, 0.5, 0.30, 0.20, 0.0, 0.75, true));

        // 5. Creeper
        // Attack: 0, KB: 0, Speed: 40%, Armor: 2.0, Tough: 0.5, KBRes: 0.25, Follow: 10%, ExplosionRes: 1.0
        deepZone.mobs.add(new MobStats("minecraft:creeper", 0.0, 0.0, 0.40, 2.0, 0.5, 0.25, 0.10, 0.0, 1.00, false));

        zones.add(deepZone);
    }

    public static class BuffZone {
        public String name = "Zone";
        public int minY = -64;
        public int maxY = 0;
        public List<MobStats> mobs = new ArrayList<>();
    }

    public static class MobStats {
        public String entityId = "minecraft:zombie";

        // --- Multiplicative Stats (Percentage) ---
        public double attackDamage = 0.0;        // 0.35
        public double movementSpeed = 0.0;       // 0.40
        public double followRange = 0.0;         // 0.15
        public double spawnReinforcements = 0.0; // 0.15
        public double explosionResistance = 0.0; // 0.75

        // --- Additive Stats (Flat Value) ---
        public double attackKnockback = 0.0;
        public double armor = 0.0;
        public double armorToughness = 0.0;
        public double knockbackResistance = 0.0;

        public double stepHeight = 0.0;
        public boolean grantFireResistance = false;

        public MobStats() {}

        public MobStats(String id, double dmg, double kb, double speed, double arm, double tough, double kbRes, double follow, double reinf, double explRes, boolean fire) {
            this.entityId = id;
            this.attackDamage = dmg;
            this.attackKnockback = kb;
            this.movementSpeed = speed;
            this.armor = arm;
            this.armorToughness = tough;
            this.knockbackResistance = kbRes;
            this.followRange = follow;
            this.spawnReinforcements = reinf;
            this.explosionResistance = explRes;
            this.grantFireResistance = fire;
        }
    }
}