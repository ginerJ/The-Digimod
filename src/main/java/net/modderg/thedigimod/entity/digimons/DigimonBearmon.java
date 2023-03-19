package net.modderg.thedigimod.entity.digimons;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.modderg.thedigimod.entity.CustomDigimon;
import net.modderg.thedigimod.entity.DigitalEntities;

public class DigimonBearmon extends CustomDigimon {

    public DigimonBearmon(EntityType<? extends TamableAnimal> p_21803_, Level p_21804_) {
        super(p_21803_, p_21804_);
        this.switchNavigation(0);
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 15.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                .add(Attributes.ATTACK_DAMAGE, 1D)
                .add(Attributes.FLYING_SPEED, 0.3D);
    }

    @Override
    public Boolean isRookie() {
        return true;
    }

    @Override
    public String getSpecies() {
        return "Bearmon";
    }
    @Override
    protected String IDLEANIM() {
        return "idle";
    }
    @Override
    protected String WALKANIM() {
        return "walk";
    }
    @Override
    protected String SITANIM() {
        return "sit";
    }

    @Override
    protected EntityType evoPath() {
        return DigitalEntities.GRIZZLYMON.get();
    }
    @Override
    protected Boolean canEvoToPath() {
        return true;
    }

    @Override
    protected EntityType evoPath4() {
        return DigitalEntities.BLACKGAOGAMON.get();
    }
    @Override
    protected Boolean canEvoToPath4() {
        return this.getMood().equals("Joyful") && this.getSpecificXps(7) >= 50;
    }

    @Override
    protected EntityType evoPath5() {
        return DigitalEntities.CHAKMON.get();
    }
    @Override
    protected Boolean canEvoToPath5() {
        return this.getMood().equals("Joyful") && this.getSpecificXps(3) >= 50 && this.getSpecificXps(6) >= 50;
    }
}
