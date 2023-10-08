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

public class DigimonGuilmon extends CustomDigimon {

    public DigimonGuilmon(EntityType<? extends TamableAnimal> p_21803_, Level p_21804_) {
        super(p_21803_, p_21804_);
        this.switchNavigation(0);
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 5.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                .add(Attributes.ATTACK_DAMAGE, 1D)
                .add(Attributes.FLYING_SPEED, 0.3D);
    }

    @Override
    public int evoStage() {
        return 1;
    }

    @Override
    public String getSpecies() {
        return "Guilmon";
    }
    @Override
    protected RegistryObject<?>[] reincarnateTo(){
        return new RegistryObject[]{DigiItems.JYARIMON};
    }
    @Override
    protected RegistryObject<?> xpDrop() {
        return DigiItems.DRAGON_DATA;
    }

    @Override
    protected String idleAnim() {
        return "idle6";
    }
    @Override
    protected String walkAnim() {
        return "walk7";
    }
    @Override
    protected String sitAnim() {
        return "sit3";
    }

    @Override
    protected EntityType evoPath() {
        return DigitalEntities.GROWLMONDATA.get();
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
        return DigitalEntities.BLACK_GROWLMON.get();
    }
    @Override
    protected Boolean canEvoToPath3() {
        return this.getSpecificXps(7) >= 50 && this.moodManager.getMood().equals("Sad") && this.getCareMistakesStat() <= 10 && this.getBattlesStat() >= 10;
    }

    @Override
    protected EntityType evoPath4() {
        return DigitalEntities.GROWLMON.get();
    }
    @Override
    protected Boolean canEvoToPath4() {
        return this.getSpecificXps(0) >= 50 && this.moodManager.getMood().equals("Joyful") && this.getCareMistakesStat() == 0 && this.getBattlesStat() >= 15;
    }
}
