package net.modderg.thedigimod.goods;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.modderg.thedigimod.item.DigiItems;

public class ClownBox extends AbstractTrainingGood {
    public ClownBox(EntityType<? extends Animal> p_27557_, Level p_27558_) {
        super(p_27557_, p_27558_);
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 500.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                .add(Attributes.ATTACK_DAMAGE, 1D)
                .add(Attributes.FLYING_SPEED, 0.3D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 100D);
    }

    @Override
    public String statName() {
        return "spattack";
    }
    @Override
    public String goodName() {
        return "clown_box";
    }
    @Override
    public ItemStack goodItem() {
        return new ItemStack(DigiItems.CLOWN_BOX.get());
    }

    @Override
    public int min(){
        return 2;
    }
    @Override
    public int max(){
        return 5;
    }

    @Override
    public int getXpId(){
        return 7;
    }
}
