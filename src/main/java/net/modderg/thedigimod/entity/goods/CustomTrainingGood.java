package net.modderg.thedigimod.entity.goods;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.modderg.thedigimod.entity.CustomDigimon;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;

public abstract class CustomTrainingGood extends Animal implements GeoEntity {

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
            } else if(goodName().equals("health")){
                digimon.getAttribute(Attributes.MAX_HEALTH).setBaseValue(digimon.getAttribute(Attributes.MAX_HEALTH).getBaseValue()
                        + random.nextInt(0, 3));
                digimon.setHealth(digimon.getHealth() + 1);
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

    protected AnimatableInstanceCache factory = new SingletonAnimatableInstanceCache(this);

    private PlayState animController(AnimationState event){
            event.getController().setAnimation(RawAnimation.begin().then("idle", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<CustomTrainingGood>(this, "controller", 0, this::animController));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.factory;
    }
}
