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

public class DigimonPegasmon extends CustomDigimon {

    public DigimonPegasmon(EntityType<? extends TamableAnimal> p_21803_, Level p_21804_) {
        super(p_21803_, p_21804_);
        this.switchNavigation(0);
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 5.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                .add(Attributes.ATTACK_DAMAGE, 1D)
                .add(Attributes.FLYING_SPEED, 0.15D);
    }

    @Override
    public String evoStage() {
        return "champion";
    }

    @Override
    public String getSpecies() {
        return "Pegasmon";
    }
    @Override
    protected RegistryObject<?>[] reincarnateTo(){
        return new RegistryObject[]{DigiItems.POYOMON};
    }
    @Override
    protected RegistryObject<?> xpDrop() {
        return DigiItems.HOLY_DATA;
    }
    @Override
    protected String idleAnim() {
        return "idle5";
    }
    @Override
    protected String walkAnim() {
        return "walk7";
    }
    @Override
    protected String sitAnim() {
        return "sit2";
    }
    @Override
    protected String flyAnim() {
        return "fly4";
    }
    @Override
    protected String attackAnim() {
        return "attack3";
    }

    @Override
    protected Boolean isFlyingDigimon() {
        return true;
    }
}
