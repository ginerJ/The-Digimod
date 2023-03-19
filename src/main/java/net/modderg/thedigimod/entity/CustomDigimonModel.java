package net.modderg.thedigimod.entity;

import net.minecraft.resources.ResourceLocation;
import net.modderg.thedigimod.TheDigiMod;
import net.modderg.thedigimod.entity.digimons.DigimonGreymon;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class CustomDigimonModel<D extends CustomDigimon> extends AnimatedGeoModel<CustomDigimon> {

    @Override
    public ResourceLocation getModelResource(CustomDigimon digimon) {
        return new ResourceLocation(TheDigiMod.MOD_ID, "geo/" +
                digimon.getSpecies().toLowerCase().replace("(", "").replace(")","")
                + ".geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(CustomDigimon digimon) {
        return new ResourceLocation(TheDigiMod.MOD_ID, "textures/entities/" +
                digimon.getSpecies().toLowerCase().replace("(", "").replace(")","")
                + ".png");
    }

    @Override
    public ResourceLocation getAnimationResource(CustomDigimon digimon) {
        return new ResourceLocation(TheDigiMod.MOD_ID, "animations/digimons_anims.json");
    }
}