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
import net.modderg.thedigimod.item.DigiItems;

public class DigimonJellymon extends CustomDigimon {

    public DigimonJellymon(EntityType<? extends TamableAnimal> p_21803_, Level p_21804_) {
        super(p_21803_, p_21804_);
        this.walkAnim = "walk7";
        this.flyAnim = "float";
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 5.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                .add(Attributes.ATTACK_DAMAGE, 1D)
                .add(Attributes.FLYING_SPEED, 0.3D);
    }

    
    @Override
    public int getEvoStage() {
        return 1;
    }

    @Override
    public String getSpecies() {
        return "Jellymon";
    }
    @Override
    public RegistryObject<?>[] getReincarnateTo(){
        return new RegistryObject[]{DigiItems.PUYOMON};
    }
    @Override
    public RegistryObject<?> getXpDrop() {
        return DigiItems.AQUAN_DATA;
    }

    @Override
    protected Boolean isFlyingDigimon() {
        return true;
    }

    @Override
    protected EntityType evoPath() {
        return DigitalEntities.OCTOMON.get();
    }
    @Override
    protected Boolean canEvoToPath() {
        return true;
    }

    @Override
    protected EntityType evoPath2() {return DigitalEntities.NUMEMON.get();}
    @Override
    protected Boolean canEvoToPath2() {
        return this.moodManager.getMood().equals("Sad");
    }

    @Override
    protected EntityType evoPath3() {
        return DigitalEntities.GESOMON.get();
    }
    @Override
    protected Boolean canEvoToPath3() {
        return this.moodManager.getMood().equals("Sad") && this.getSpecificXps(7) > 50 && this.getCareMistakesStat() <= 5 && this.getBattlesStat() >= 10;
    }

    @Override
    protected EntityType evoPath4() {
        return DigitalEntities.TESLAJELLYMON.get();
    }
    @Override
    protected Boolean canEvoToPath4() {
        return this.moodManager.getMood().equals("Joyful") && this.getSpecificXps(3) > 50 && this.getCareMistakesStat() <= 5 && this.getBattlesStat() >= 15;
    }
}
