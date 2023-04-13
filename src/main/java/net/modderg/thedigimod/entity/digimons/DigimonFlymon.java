package net.modderg.thedigimod.entity.digimons;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.modderg.thedigimod.entity.CustomDigimon;

public class DigimonFlymon extends CustomDigimon {

    public DigimonFlymon(EntityType<? extends TamableAnimal> p_21803_, Level p_21804_) {
        super(p_21803_, p_21804_);
        this.switchNavigation(0);
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 1.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                .add(Attributes.ATTACK_DAMAGE, 1D)
                .add(Attributes.FLYING_SPEED, 0.2D);
    }

    @Override
    public Boolean isChampion() {
        return true;
    }

    @Override
    public String getSpecies() {
        return "Flymon";
    }
    @Override
    protected String IDLEANIM() {
        return "idle";
    }
    @Override
    protected String WALKANIM() {
        return "walk2";
    }
    @Override
    protected String SITANIM() {
        return "sit";
    }
    @Override
    protected String FLYANIM() {
        return "bug_fly";
    }

    @Override
    protected Boolean isFlyingDigimon() {
        return true;
    }
}
