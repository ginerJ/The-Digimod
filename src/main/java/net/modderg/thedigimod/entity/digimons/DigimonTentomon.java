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

public class DigimonTentomon extends CustomDigimon {

    public DigimonTentomon(EntityType<? extends TamableAnimal> p_21803_, Level p_21804_) {
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
        return "Tentomon";
    }
    @Override
    protected RegistryObject<?>[] reincarnateTo(){
        return new RegistryObject[]{DigiItems.BUBBMON};
    }
    @Override
    protected RegistryObject<?> xpDrop() {return DigiItems.PLANTINSECT_DATA;}

    @Override
    protected String idleAnim() {
        return "idle";
    }
    @Override
    protected String walkAnim() {
        return "walk";
    }
    @Override
    protected String sitAnim() {
        return "sit3";
    }

    @Override
    protected EntityType evoPath() {
        return DigitalEntities.KUWAGAMON.get();
    }
    @Override
    protected Boolean canEvoToPath() {
        return true;
    }

    @Override
    protected EntityType evoPath2() {
        return DigitalEntities.KABUTERIMON.get();
    }
    @Override
    protected Boolean canEvoToPath2() {
        return this.moodManager.getMood().equals("Joyful") && this.getSpecificXps(2) >= 50 && this.getCareMistakesStat() == 0 && this.getBattlesStat() >= 15;
    }

    @Override
    protected EntityType evoPath3() {
        return DigitalEntities.ROACHMON.get();
    }
    @Override
    protected Boolean canEvoToPath3() {
        return this.moodManager.getMood().equals("Sad");
    }
}
