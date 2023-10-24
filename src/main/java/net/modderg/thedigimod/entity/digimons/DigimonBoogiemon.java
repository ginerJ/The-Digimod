package net.modderg.thedigimod.entity.digimons;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.RegistryObject;
import net.modderg.thedigimod.entity.CustomDigimon;
import net.modderg.thedigimod.item.DigiItems;

public class DigimonBoogiemon extends CustomDigimon {

    public DigimonBoogiemon(EntityType<? extends TamableAnimal> p_21803_, Level p_21804_) {
        super(p_21803_, p_21804_);
        this.idleAnim = "fly4";
        this.sitAnim = "sit6";
        this.walkAnim = "fly3";
        this.attackAnim = "attack5";
        this.flyAnim = "fly3";
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
        return 2;
    }

    @Override
    public String getSpecies() {
        return "Boogiemon";
    }
    @Override
    public RegistryObject<?>[] getReincarnateTo(){return new RegistryObject[]{DigiItems.KIIMON};}
    @Override
    public RegistryObject<?> getXpDrop() {
        return DigiItems.NIGHTMARE_DATA;
    }

    @Override
    protected Boolean isFlyingDigimon() {
        return true;
    }
}
