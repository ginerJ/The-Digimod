package net.modderg.thedigimod.entity.goods;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.Level;
import net.modderg.thedigimod.entity.CustomDigimon;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.builder.ILoopType;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import static software.bernie.geckolib3.util.GeckoLibUtil.createFactory;

public abstract class CustomTrainingGood extends Animal implements IAnimatable {

    public CustomTrainingGood(EntityType<? extends Animal> p_27557_, Level p_27558_) {
        super(p_27557_, p_27558_);
    }

    public String goodName(){
        return null;
    }

    @Override
    public boolean hurt(DamageSource source, float p_27568_) {
        if(source.getDirectEntity() instanceof CustomDigimon digimon && this.random.nextInt(0,5) == 4){
            if(goodName().equals("attack")){
                digimon.setAttackStat(digimon.getAttackStat() + 1);
            }else if(goodName().equals("defence")){
                digimon.setAttackStat(digimon.getAttackStat() + 1);
            }else if(goodName().equals("spattack")){
                digimon.setAttackStat(digimon.getAttackStat() + 1);
            }else if(goodName().equals("spdefence")){
                digimon.setAttackStat(digimon.getAttackStat() + 1);
            }
        }
        return super.hurt(source, p_27568_);
    }

    @Override
    protected void actuallyHurt(DamageSource p_21240_, float p_21241_) {

    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel p_146743_, AgeableMob p_146744_) {
        return null;
    }

    protected AnimationFactory factory = createFactory(this);

    private <E extends IAnimatable> PlayState animController(AnimationEvent<E> event) {
        event.getController().setAnimation(new AnimationBuilder().addAnimation("idle", ILoopType.EDefaultLoopTypes.LOOP));
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController<CustomTrainingGood>(this, "controller", 0, this::animController));
    }

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }
}
