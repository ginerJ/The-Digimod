package net.modderg.thedigimod.client;

import net.minecraftforge.common.ForgeConfigSpec;

public class TDClientConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.ConfigValue<Boolean> SHOULD_RENDER_DIRTY_EFFECT;
    public static final ForgeConfigSpec.ConfigValue<Boolean> SHOULD_RENDER_GLOWMASKS;

    static {
        BUILDER.push("The Digimod Client Config");

        SHOULD_RENDER_DIRTY_EFFECT = BUILDER.comment("Useful if you have renedering issues do to shader/gpu issues")
                .define("Should dirty digimon effect be rendered: ", true);
        SHOULD_RENDER_GLOWMASKS = BUILDER
                .define("Should glowmasks be rendered for digimon: ", true);

        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}