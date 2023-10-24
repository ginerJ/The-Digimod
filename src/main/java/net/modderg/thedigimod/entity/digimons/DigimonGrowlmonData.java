package net.modderg.thedigimod.entity.digimons;

import net.minecraft.world.entity.Entity;
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

public class DigimonGrowlmonData extends CustomDigimon {

    public DigimonGrowlmonData(EntityType<? extends TamableAnimal> p_21803_, Level p_21804_) {
        super(p_21803_, p_21804_);
        this.sitAnim = "sit6";
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 5.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                .add(Attributes.ATTACK_DAMAGE, 1D)
                .add(Attributes.FLYING_SPEED, 0.3D);
    }

    

    protected boolean isDigimonMountable(){return true;}

    @Override
    protected void positionRider(Entity entity, MoveFunction moveF) {
        if (this.hasPassenger(entity)) {moveF.accept(entity, this.getX(), this.getY() + this.getPassengersRidingOffset()/2, this.getZ());}
    }


    @Override
    public int getEvoStage() {
        return 2;
    }

    @Override
    public String getSpecies() {
        return "Growlmon(Data)";
    }
    @Override
    public RegistryObject<?>[] getReincarnateTo(){
        return new RegistryObject[]{DigiItems.JYARIMON};
    }
    @Override
    public RegistryObject<?> getXpDrop() {
        return DigiItems.DRAGON_DATA;
    }
}
