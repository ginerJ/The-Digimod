package net.modderg.thedigimod.entity.digimons;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.RegistryObject;
import net.modderg.thedigimod.entity.CustomDigimon;
import net.modderg.thedigimod.entity.DigitalEntities;
import net.modderg.thedigimod.entity.managers.EvolutionCondition;
import net.modderg.thedigimod.item.DigiItems;

public class DigimonKoromon extends CustomDigimon {

    public DigimonKoromon(EntityType<? extends TamableAnimal> p_21803_, Level p_21804_) {
        super(p_21803_, p_21804_);
        this.reincarnateTo = new RegistryObject[]{DigiItems.BOTAMON};
        this.xpDrop = DigiItems.DRAGON_DATA;
        this.setSpMoveName("bullet");

        this.idleAnim = "idle3";
        this.walkAnim = "walk4";
        this.sitAnim = "sit7";
        this.attackAnim = "attack2";
        this.shootAnim = "shoot4";

        this.digiEvoPath =  DigitalEntities.digimonMap.get("agumon").get();
        this.digiEvoPath2 =  DigitalEntities.digimonMap.get("agumonblack").get();

        evoCondition = new EvolutionCondition(this).alwaysTrue();
        evoCondition2 = new EvolutionCondition(this).moodCheck("Sad");
    }

    @Override
    public String getSpecies() {
        return "Koromon";
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 5.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                .add(Attributes.ATTACK_DAMAGE, 1D)
                .add(Attributes.FLYING_SPEED, 0.3D);
    }
}
