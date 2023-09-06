package net.modderg.thedigimod.goods;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.modderg.thedigimod.entity.CustomDigimon;
import net.modderg.thedigimod.item.CustomXpItem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.example.entity.DynamicExampleEntity;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;

public abstract class AbstractTrainingGood extends Animal implements GeoEntity {

    public AbstractTrainingGood(EntityType<? extends Animal> p_27557_, Level p_27558_) {
        super(p_27557_, p_27558_);
        this.setCustomName(Component.literal("LOL"));
    }

    public int getXpId(){
        return -1;
    }

    public ItemStack goodItem(){
        return null;
    }
    public String statName(){
        return null;
    }
    public String goodName(){
        return null;
    }

    public int min(){
        return 0;
    }
    public int max(){
        return 3;
    }

    @Override
    public boolean isCustomNameVisible() {
        return true;
    }

    @Override
    public boolean hurt(DamageSource source, float p_27568_) {
        if(source.getDirectEntity() instanceof CustomDigimon digimon && this.random.nextInt(4) == 2){
            int add = random.nextInt(min(), max());
            digimon.restMoodPoints(10);
            if(statName().equals("attack")){
                digimon.setAttackStat(digimon.getAttackStat() + add);
            }else if(statName().equals("defence")){
                digimon.setDefenceStat(digimon.getDefenceStat() + add);
            }else if(statName().equals("spattack")){
                digimon.setSpAttackStat(digimon.getSpAttackStat() + add);
            }else if(statName().equals("spdefence")){
                digimon.setSpDefenceStat(digimon.getSpDefenceStat() + add);
            } else if(statName().equals("health")){
                digimon.setHealthStat(digimon.getHealthStat() + add);
            }
            if(getXpId() >= 0 && random.nextInt(0,4) == 1){
                digimon.useXpItem(getXpId());
            }
            hurtTime = 100;
        }
        return super.hurt(source, p_27568_);
    }

    @Override
    public @NotNull InteractionResult mobInteract(Player player, @NotNull InteractionHand p_27585_) {
        if(player.isShiftKeyDown()){
            ItemStack itemstack = this.goodItem();
            itemstack.getOrCreateTag().putInt("USES", (int)this.getHealth());
            this.level().addFreshEntity(new ItemEntity(level(),
                    this.getX(),this.getY(),this.getZ(), itemstack));
            this.remove(RemovalReason.UNLOADED_TO_CHUNK);
            return InteractionResult.CONSUME;
        }
        return super.mobInteract(player, p_27585_);
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public boolean canBeLeashed(@NotNull Player p_21418_) {
        return false;
    }

    @Override
    protected void actuallyHurt(@NotNull DamageSource p_21240_, float p_21241_) {
        this.setHealth(getHealth()-1);
    }

    @Nullable
    @Override
    public Component getCustomName() {
        return Component.literal("uses: " + Integer.toString((int)this.getHealth()) + "/500");
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(@NotNull ServerLevel p_146743_, @NotNull AgeableMob p_146744_) {
        return null;
    }

    protected AnimatableInstanceCache factory = new SingletonAnimatableInstanceCache(this);

    private PlayState animController(AnimationState<AbstractTrainingGood> event) {
        if(this.hurtTime > 0){
            event.getController().setAnimation(RawAnimation.begin().then("hit", Animation.LoopType.LOOP));
        } else {
            event.getController().setAnimation(RawAnimation.begin().then("idle", Animation.LoopType.LOOP));

        }
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<AbstractTrainingGood>(this, "controller", 0, this::animController));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.factory;
    }
}
