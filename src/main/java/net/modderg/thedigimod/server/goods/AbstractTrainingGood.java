package net.modderg.thedigimod.server.goods;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.modderg.thedigimod.server.entity.DigimonEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;

import static net.modderg.thedigimod.server.TDConfig.*;

public class AbstractTrainingGood extends Animal implements GeoEntity {

    protected float statMultiplier = 1.0f;
    public float getStatMultiplier(){return statMultiplier;}

    public AbstractTrainingGood setStatMultiplier(float f){statMultiplier = f; return this;}

    public AbstractTrainingGood(EntityType<? extends Animal> p_27557_, Level p_27558_) {
        super(p_27557_, p_27558_);
        this.setCustomName(Component.literal("LOL"));
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 500.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                .add(Attributes.ATTACK_DAMAGE, 1D)
                .add(Attributes.FLYING_SPEED, 0.3D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 100D);
    }

    private int xpId = -1;
    public int getXpId(){
        return xpId;
    }
    public AbstractTrainingGood setXpId(int id){xpId = id; return this;}

    private Item goodItem;
    public ItemStack getGoodItem(){
        return new ItemStack(goodItem);
    }
    public AbstractTrainingGood setItem(Item item){goodItem = item; return this;}

    private String statName;
    public String getStatName(){return statName;}
    public AbstractTrainingGood setStatTrain(String name){statName = name; return this;}

    private String goodName;
    public String getGoodName(){return goodName;}
    public AbstractTrainingGood setName(String name){goodName = name; return this;}

    @Override
    public boolean isCustomNameVisible() {
        return true;
    }

    protected int minStatGain(DigimonEntity digimon) {
        return digimon.getRank().equals("zero") ? F_RANK_MIN_STAT_GAIN.get() : (digimon.getRank().equals("super") ? S_RANK_MIN_STAT_GAIN.get() : F_RANK_MIN_STAT_GAIN.get());
    }

    protected int maxStatGain(DigimonEntity digimon) {
        return digimon.getRank().equals("zero") ? F_RANK_MAX_STAT_GAIN.get() : (digimon.getRank().equals("super") ? S_RANK_MAX_STAT_GAIN.get() : F_RANK_MAX_STAT_GAIN.get());
    }

    @Override
    public boolean hurt(DamageSource source, float p_27568_) {
        if(source.getDirectEntity() instanceof DigimonEntity digimon && this.random.nextInt(6) == 2){

            int add = random.nextInt(minStatGain(digimon), maxStatGain(digimon));
            add = (int) (add * statMultiplier);

            digimon.moodManager.restMoodPoints(10);

            if(getStatName().equals("attack"))
                digimon.setAttackStat(digimon.getAttackStat() + add);

            else if(getStatName().equals("defence"))
                digimon.setDefenceStat(digimon.getDefenceStat() + add);

            else if(getStatName().equals("spattack"))
                digimon.setSpAttackStat(digimon.getSpAttackStat() + add);

            else if(getStatName().equals("spdefence"))
                digimon.setSpDefenceStat(digimon.getSpDefenceStat() + add);

            else if(getStatName().equals("health"))
                digimon.setHealthStat(digimon.getHealthStat() + add);

            if(getXpId() >= 0 && random.nextInt(0,20) == 1)
                digimon.gainSpecificXp(getXpId());

        }
        return super.hurt(source, p_27568_);
    }

    @Override
    public @NotNull InteractionResult mobInteract(Player player, @NotNull InteractionHand p_27585_) {
        if(player.isShiftKeyDown()){
            ItemStack itemstack = this.getGoodItem();

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
        if(this.hurtTime > 0)
            event.getController().setAnimation(RawAnimation.begin().then("hit", Animation.LoopType.LOOP));
        else
            event.getController().setAnimation(RawAnimation.begin().then("idle", Animation.LoopType.LOOP));
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, this::animController));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.factory;
    }
}
