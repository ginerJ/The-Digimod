package net.modderg.thedigimod.server.advancements;

import net.minecraft.advancements.Advancement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.modderg.thedigimod.TheDigiMod;

public class TDAdvancements {
    public final static ResourceLocation GET_A_DIGIMON = new ResourceLocation(TheDigiMod.MOD_ID, "get_a_digimon");
    public final static ResourceLocation DIGITRON_EVO = new ResourceLocation(TheDigiMod.MOD_ID, "digitron");
    public final static ResourceLocation MISTAKE = new ResourceLocation(TheDigiMod.MOD_ID, "mistake");
    public final static ResourceLocation EVOLUTION = new ResourceLocation(TheDigiMod.MOD_ID, "evolution");
    public final static ResourceLocation NUMEMON_SCUMON = new ResourceLocation(TheDigiMod.MOD_ID, "numemon_scumon");
    public final static ResourceLocation DEVOLUTION = new ResourceLocation(TheDigiMod.MOD_ID, "deevolution");
    public final static ResourceLocation TRAINING = new ResourceLocation(TheDigiMod.MOD_ID, "training");
    public final static ResourceLocation HIGH_TRAINING = new ResourceLocation(TheDigiMod.MOD_ID, "high_training");
    public final static ResourceLocation MINE_DIGICARD = new ResourceLocation(TheDigiMod.MOD_ID, "mine_digicard");
    public final static ResourceLocation DELIGHTED_FARMER = new ResourceLocation(TheDigiMod.MOD_ID, "delighted_farmer");
    public final static ResourceLocation DIGI_JANITOR = new ResourceLocation(TheDigiMod.MOD_ID, "janitor");
    public final static ResourceLocation CRAVINGS = new ResourceLocation(TheDigiMod.MOD_ID, "cravings");
    public final static ResourceLocation SHORTCUTS = new ResourceLocation(TheDigiMod.MOD_ID, "shortcuts");

    public final static ResourceLocation BRONZE_TAMER = new ResourceLocation(TheDigiMod.MOD_ID, "tamer_bronze");
    public final static ResourceLocation SILVER_TAMER = new ResourceLocation(TheDigiMod.MOD_ID, "tamer_silver");
    public final static ResourceLocation GOLD_TAMER = new ResourceLocation(TheDigiMod.MOD_ID, "tamer_gold");

    public final static ResourceLocation GYM_BRO_BRONZE = new ResourceLocation(TheDigiMod.MOD_ID, "gym_bro_bronze");
    public final static ResourceLocation GYM_BRO_SILVER = new ResourceLocation(TheDigiMod.MOD_ID, "gym_bro_silver");
    public final static ResourceLocation GYM_BRO_GOLD = new ResourceLocation(TheDigiMod.MOD_ID, "gym_bro_gold");

    public final static ResourceLocation PARTY = new ResourceLocation(TheDigiMod.MOD_ID, "party");
    public final static ResourceLocation COLLECTOR_VTAMER = new ResourceLocation(TheDigiMod.MOD_ID, "collection_vtamer");
    public final static ResourceLocation COLLECTOR_VTAMER2 = new ResourceLocation(TheDigiMod.MOD_ID, "collection_vtamer2");
    public final static ResourceLocation COLLECTOR_VTAMER3 = new ResourceLocation(TheDigiMod.MOD_ID, "collection_vtamer3");

    public static void grantAdvancement(ServerPlayer player, ResourceLocation advancement){
        Advancement adv = player.getServer().getAdvancements().getAdvancement(advancement);
        if(adv != null)
            for (String criteria : player.getAdvancements().getOrStartProgress(adv).getRemainingCriteria())
                player.getAdvancements().award(adv, criteria);
    }
}
