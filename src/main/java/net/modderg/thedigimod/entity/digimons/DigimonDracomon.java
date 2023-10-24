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

public class DigimonDracomon extends CustomDigimon {

    public DigimonDracomon(EntityType<? extends TamableAnimal> p_21803_, Level p_21804_) {
        super(p_21803_, p_21804_);
        this.sitAnim = "sit3";
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
        return "Dracomon";
    }
    @Override
    public RegistryObject<?>[] getReincarnateTo(){
        return new RegistryObject[]{DigiItems.PETITMON};
    }
    @Override
    public RegistryObject<?> getXpDrop() {
        return DigiItems.DRAGON_DATA;
    }

    @Override
    protected EntityType evoPath() {
        return DigitalEntities.AIRDRAMON.get();
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
    protected EntityType evoPath3() {return DigitalEntities.GROWLMONDATA.get();}
    @Override
    protected Boolean canEvoToPath3() {
        return this.moodManager.getMood().equals("Sad") && this.getCareMistakesStat() <= 10 && this.getBattlesStat() >= 10;
    }

    @Override
    protected EntityType evoPath4() {
        return DigitalEntities.COREDRAMONGREEN.get();
    }
    @Override
    protected Boolean canEvoToPath4() {
        return this.getSpecificXps(6) >= 50 && this.moodManager.getMood().equals("Joyful") && this.getCareMistakesStat() <= 5 && this.getBattlesStat() >= 15;
    }

    @Override
    protected EntityType evoPath5() {
        return DigitalEntities.COREDRAMON.get();
    }
    @Override
    protected Boolean canEvoToPath5() {
        return this.getSpecificXps(0) >= 50 && this.moodManager.getMood().equals("Joyful") && this.getCareMistakesStat() == 0 && this.getBattlesStat() >= 15;
    }
}
