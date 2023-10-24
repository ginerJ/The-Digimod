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

public class DigimonLopmon extends CustomDigimon {

    public DigimonLopmon(EntityType<? extends TamableAnimal> p_21803_, Level p_21804_) {
        super(p_21803_, p_21804_);
        this.idleAnim = "idle3";
        this.walkAnim = "walk7";
        this.sitAnim = "sit8";
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 5.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                .add(Attributes.ATTACK_DAMAGE, 1.D)
                .add(Attributes.FLYING_SPEED, 0.3D);
    }

    @Override
    public int getEvoStage() {
        return 1;
    }

    @Override
    public String getSpecies() {
        return "Lopmon";
    }
    @Override
    public RegistryObject<?>[] getReincarnateTo(){
        return new RegistryObject[]{DigiItems.CONOMON};
    }
    @Override
    public RegistryObject<?> getXpDrop() {
        return DigiItems.HOLY_DATA;
    }

    @Override
    protected EntityType evoPath() {return DigitalEntities.BLACKGALGOMON.get();}
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
        return DigitalEntities.WENDIMON.get();
    }
    @Override
    protected Boolean canEvoToPath3() {
        return this.moodManager.getMood().equals("Sad") && this.getSpecificXps(7) >= 50 && this.getCareMistakesStat() <= 10 && this.getBattlesStat() >= 10;
    }

    @Override
    protected EntityType evoPath4() {
        return DigitalEntities.TURUIEMON.get();
    }
    @Override
    protected Boolean canEvoToPath4() {
        return this.moodManager.getMood().equals("Joyful") && this.getSpecificXps(1) >= 25 && this.getSpecificXps(8) >= 25 && this.getCareMistakesStat() == 0 && this.getBattlesStat() >= 15;
    }
}
