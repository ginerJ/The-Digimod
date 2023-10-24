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

public class DigimonExermon extends CustomDigimon {

    public DigimonExermon(EntityType<? extends TamableAnimal> p_21803_, Level p_21804_) {
        super(p_21803_, p_21804_);
        this.idleAnim = "idle4";
        this.sitAnim = "sit2";
        this.shootAnim = "shoot6";
        this.attackAnim = "attack5";
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
        return 2;
    }

    @Override
    public String getSpecies() {
        return "Exermon";
    }
    @Override
    public RegistryObject<?>[] getReincarnateTo(){
        return new RegistryObject[]{DigiItems.DOKIMON};
    }
    @Override
    public RegistryObject<?> getXpDrop() {
        return DigiItems.PLANTINSECT_DATA;
    }
}
