package net.modderg.thedigimod.entity.digimons;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.RegistryObject;
import net.modderg.thedigimod.entity.CustomDigimon;
import net.modderg.thedigimod.entity.DigitalEntities;
import net.modderg.thedigimod.item.DigiItems;

public class DigimonAgumon extends CustomDigimon {

    public DigimonAgumon(EntityType<? extends TamableAnimal> p_21803_, Level p_21804_) {
        super(p_21803_, p_21804_);
        this.switchNavigation(0);
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 5.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                .add(Attributes.ATTACK_DAMAGE, 1.D)
                .add(Attributes.FLYING_SPEED, 0.3D);
    }

    @Override
    public String evoStage() {
        return "rookie";
    }

    @Override
    public String getSpecies() {
        return "Agumon";
    }
    @Override
    protected RegistryObject<?>[] reincarnateTo(){
        return new RegistryObject[]{DigiItems.BOTAMON};
    }
    @Override
    protected RegistryObject<?> xpDrop() {
        return DigiItems.DRAGON_DATA;
    }

    @Override
    protected String idleAnim() {
        return "idle";
    }
    @Override
    protected String walkAnim() {
        return "walk5";
    }
    @Override
    protected String sitAnim() {
        return "sit";
    }
    @Override
    protected String attackAnim() {return "attack";}
    @Override
    protected String shootAnim() {
        return "shoot";
    }

    @Override
    protected EntityType evoPath() {
        return DigitalEntities.TYRANNOMON.get();
    }
    @Override
    protected Boolean canEvoToPath() {
        return true;
    }

    @Override
    protected EntityType evoPath2() {return DigitalEntities.NUMEMON.get();}
    @Override
    protected Boolean canEvoToPath2() {
        return this.getMood().equals("Sad");
    }

    @Override
    protected EntityType evoPath4() {
        return DigitalEntities.VEEDRAMON.get();
    }
    @Override
    protected Boolean canEvoToPath4() {
        return this.getMood().equals("Joyful") && this.getSpecificXps(6) >= 50 && this.getCareMistakesStat() <= 5 && this.getBattlesStat() >= 10;
    }

    @Override
    protected EntityType evoPath5() {
        return DigitalEntities.GREYMON.get();
    }
    @Override
    protected Boolean canEvoToPath5() {
        return this.getMood().equals("Joyful") && this.getSpecificXps(0) >= 50 && this.getCareMistakesStat() == 0 && this.getBattlesStat() >= 15;
    }

    @Override
    public EntityType digitronEvo() {
        return DigitalEntities.AGUMONBLACK.get();
    }
}
