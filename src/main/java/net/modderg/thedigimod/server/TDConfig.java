package net.modderg.thedigimod.server;

import net.minecraftforge.common.ForgeConfigSpec;

public class TDConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.ConfigValue<Boolean> CAN_SPAWN_DIGIMON;
    public static final ForgeConfigSpec.ConfigValue<Boolean> GIVE_TRAINING_BAG;

    public static final ForgeConfigSpec.ConfigValue<Integer> MAX_DIGIMON_LIVES;

    public static final ForgeConfigSpec.ConfigValue<Integer> ROOKIE_EVOLUTION_LEVEL;
    public static final ForgeConfigSpec.ConfigValue<Integer> CHAMPION_EVOLUTION_LEVEL;
    public static final ForgeConfigSpec.ConfigValue<Integer> ULTIMATE_EVOLUTION_LEVEL;

    public static final ForgeConfigSpec.ConfigValue<Integer> CHANCE_DROP_BABY;
    public static final ForgeConfigSpec.ConfigValue<Integer> CHANCE_DROP_MOVE_CHIP;
    public static final ForgeConfigSpec.ConfigValue<Integer> CHANCE_DROP_FOOD;
    public static final ForgeConfigSpec.ConfigValue<Integer> CHANCE_DROP_CARD;
    public static final ForgeConfigSpec.ConfigValue<Integer> CHANCE_DROP_BYTES;

    public static final ForgeConfigSpec.ConfigValue<Boolean> RIDE_ALLOWED;
    public static final ForgeConfigSpec.ConfigValue<Boolean> FLIGHT_ALLOWED;

    public static final ForgeConfigSpec.ConfigValue<Boolean> FIRE_ATTACKS;

    public static final ForgeConfigSpec.ConfigValue<Integer> BABY_SCALE;
    public static final ForgeConfigSpec.ConfigValue<Integer> ROOKIE_SCALE;
    public static final ForgeConfigSpec.ConfigValue<Integer> CHAMPION_SCALE;
    public static final ForgeConfigSpec.ConfigValue<Integer> ULTIMATE_SCALE;
    public static final ForgeConfigSpec.ConfigValue<Integer> BOSS_SCALE;

    public static final ForgeConfigSpec.ConfigValue<Integer> F_RANK_MIN_STAT_GAIN;
    public static final ForgeConfigSpec.ConfigValue<Integer> Z_RANK_MIN_STAT_GAIN;
    public static final ForgeConfigSpec.ConfigValue<Integer> S_RANK_MIN_STAT_GAIN;

    public static final ForgeConfigSpec.ConfigValue<Integer> F_RANK_MAX_STAT_GAIN;
    public static final ForgeConfigSpec.ConfigValue<Integer> Z_RANK_MAX_STAT_GAIN;
    public static final ForgeConfigSpec.ConfigValue<Integer> S_RANK_MAX_STAT_GAIN;


    static {
        BUILDER.push("The Digimod Server Configs");

        CAN_SPAWN_DIGIMON = BUILDER.comment("side note: disabling digimon professions is not a config, it can be turned off by setting mob griefing to false \n\nallows digimon to spawn in the overworld")
                .define("digimon spawn enabled:", true);

        GIVE_TRAINING_BAG = BUILDER.comment("\nshould newly joined players spawn with training bag")
                .define("players spawn with training bag enabled:", true);

        MAX_DIGIMON_LIVES = BUILDER.comment("\nmaximum amount of lives a tamed digimon can have (3 max value)")
                .define("tamed digimon lives max:", 3);


        ROOKIE_EVOLUTION_LEVEL = BUILDER.comment("\nlevels at which each stage is achieved (only natural numbers, current max level is 65)")
                .define("rookie evolution level:", 6);
        CHAMPION_EVOLUTION_LEVEL = BUILDER
                .define("champion evolution level:", 20);
        ULTIMATE_EVOLUTION_LEVEL = BUILDER
                .define("ultimate evolution level:", 40);


        CHANCE_DROP_BABY = BUILDER.comment("\nchance over a hundred of each item a digimon drops (only natural numbers until 100)")
                .define("baby drop chance:", 3);
        CHANCE_DROP_MOVE_CHIP = BUILDER
                .define("move chip drop chance:", 5);
        CHANCE_DROP_FOOD = BUILDER
                .define("chance of digi food drop:", 5);
        CHANCE_DROP_CARD = BUILDER
                .define("chance of green card drop:", 2);
        CHANCE_DROP_BYTES = BUILDER
                .define("chance of stat bytes drop:", 5);


        RIDE_ALLOWED = BUILDER.comment("\nallow riding of both ground and flying digimon in servers")
                .define("ground digimon rideable:", true);
        FLIGHT_ALLOWED = BUILDER
                .define("flying digimon rideable:", true);


        FIRE_ATTACKS = BUILDER.comment("\nallow fire attacks to turn hit blocks on fire")
                .define("fire attacks spread fire:", true);


        BABY_SCALE = BUILDER.comment("\nScale the size of digimon to make them bigger or smaller to your preference")
                .define("Baby digimon size %:", 100);
        ROOKIE_SCALE = BUILDER
                .define("Rookie digimon size %:", 100);
        CHAMPION_SCALE = BUILDER
                .define("Champion digimon size %:", 100);
        ULTIMATE_SCALE = BUILDER
                .define("Ultimate digimon size %:", 100);
        BOSS_SCALE = BUILDER
                .define("Boss digimon size %:", 100);


        F_RANK_MIN_STAT_GAIN = BUILDER.comment("\nMin and max amount of stat gain per training on each digimon rank").define("Failed rank digimon min stat gain: ", 1);
        F_RANK_MAX_STAT_GAIN = BUILDER.define("Failed rank digimon max stat gain: ", 4);

        Z_RANK_MIN_STAT_GAIN = BUILDER.define("Zero rank digimon min stat gain: ", 2);
        Z_RANK_MAX_STAT_GAIN = BUILDER.define("Zero rank digimon max stat gain: ", 6);

        S_RANK_MIN_STAT_GAIN = BUILDER.define("Super rank digimon min stat gain: ", 3);
        S_RANK_MAX_STAT_GAIN = BUILDER.define("Super rank digimon max stat gain: ", 8);

        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}