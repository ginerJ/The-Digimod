package net.modderg.thedigimod.entity.goods;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.modderg.thedigimod.entity.CustomDigimon;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.AnimationState;
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

    private boolean hitted = false;

    public CustomTrainingGood(EntityType<? extends Animal> p_27557_, Level p_27558_) {
        super(p_27557_, p_27558_);
        this.setCustomName(Component.literal("LOL"));
    }

    public String goodName(){
        return null;
    }

    @Override
    public boolean isCustomNameVisible() {
        return true;
    }

    @Override
    public boolean hurt(DamageSource source, float p_27568_) {
        if(source.getDirectEntity() instanceof CustomDigimon digimon && this.random.nextInt(0,5) == 4){
            digimon.restMoodPoints(10);
            if(goodName().equals("attack")){
                digimon.setAttackStat(digimon.getAttackStat() + random.nextInt(0, 3));
            }else if(goodName().equals("defence")){
                digimon.setDefenceStat(digimon.getDefenceStat() + random.nextInt(0, 3));
            }else if(goodName().equals("spattack")){
                digimon.setSpAttackStat(digimon.getSpAttackStat() + random.nextInt(0, 3));
            }else if(goodName().equals("spdefence")){
                digimon.setSpDefenceStat(digimon.getSpDefenceStat() + random.nextInt(0, 3));
            }
        }
        return super.hurt(source, p_27568_);
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public boolean canBeLeashed(Player p_21418_) {
        return false;
    }

    @Override
    protected void actuallyHurt(DamageSource p_21240_, float p_21241_) {
        hitted = true;
        this.setHealth(getHealth()-1);
    }

    @Nullable
    @Override
    public Component getCustomName() {
        return Component.literal("uses: " + Integer.toString((int)this.getHealth()) + "/500");
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel p_146743_, AgeableMob p_146744_) {
        return null;
    }

    protected AnimationFactory factory = createFactory(this);

    public static <T extends CustomTrainingGood & IAnimatable> AnimationController<T> animController(T good) {
        return new AnimationController<>(good,"movement", 0, event ->{
            event.getController().setAnimation(new AnimationBuilder().addAnimation("idle", ILoopType.EDefaultLoopTypes.LOOP));
            return PlayState.CONTINUE;
        });
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(animController(this));
    }

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }
}
