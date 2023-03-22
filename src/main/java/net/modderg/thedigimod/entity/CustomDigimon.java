package net.modderg.thedigimod.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.modderg.thedigimod.goals.DigitalFollowOwnerGoal;
import net.modderg.thedigimod.goals.DigitalMeleeAttackGoal;
import net.modderg.thedigimod.item.DigiItems;
import net.modderg.thedigimod.projectiles.CustomProjectile;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.builder.ILoopType;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import java.util.Objects;

import static software.bernie.geckolib3.util.GeckoLibUtil.createFactory;

public class CustomDigimon extends TamableAnimal implements IAnimatable {

    public String getSpecies(){return "";}
    public Boolean isBaby2(){return false;}
    public Boolean isRookie(){return false;}
    public Boolean isChampion(){return false;}
    public Boolean isUltimate(){return false;}
    public Boolean isMega(){return false;}

    protected Boolean ISFLYINGDIGIMON(){return false;}
    protected Boolean ISSWIMMERDIGIMON(){return false;}

    public Boolean evolutionLevelAchieved(){return (isRookie() && this.getCurrentLevel() > 15) || (isBaby2() && this.getCurrentLevel() > 5);}
    public Boolean isEvolving() {
        return getEvoCount() > 0;
    }
    protected boolean isStill() {
        return !(this.getDeltaMovement().horizontalDistanceSqr() > 1.0E-3D);
    }

    @Override
    public boolean doHurtTarget(Entity p_21372_) {
        this.ticksToAttackAnim = 10;
        return super.doHurtTarget(p_21372_);
    }
    @Override
    public boolean canFallInLove() {
        return false;
    }
    @Override
    public boolean isFood(ItemStack item) {
        if(item.is(DigiItems.DIGI_MEAT.get())){
            this.addMoodPoints(4800);
            return true;
        }
        return false;
    }
    @Override
    public boolean causeFallDamage(float p_147187_, float p_147188_, DamageSource p_147189_) {
        if(this.getMovementID() == 2){
            return false;
        }
        return super.causeFallDamage(p_147187_, p_147188_, p_147189_);
    }
    @Override
    public boolean isOrderedToSit() {
        return (super.isOrderedToSit()) || this.isEvolving();
    }

    protected EntityType evoPath(){return null;}
    protected Boolean canEvoToPath(){return false;}
    protected EntityType evoPath2(){return null;}
    protected Boolean canEvoToPath2(){return false;}
    protected EntityType evoPath3(){return null;}
    protected Boolean canEvoToPath3(){return false;}
    protected EntityType evoPath4(){return null;}
    protected Boolean canEvoToPath4(){return false;}
    protected EntityType evoPath5(){return null;}
    protected Boolean canEvoToPath5(){return false;}

    protected static final EntityDataAccessor<String> NICKNAME = SynchedEntityData.defineId(CustomDigimon.class, EntityDataSerializers.STRING);
    public void setNickName(String i){
        this.getEntityData().set(NICKNAME, i);
    }
    public String getNickName(){
        return this.getEntityData().get(NICKNAME);
    }
    private boolean activateName = false;

    protected String IDLEANIM(){return "idle";}
    protected String SITANIM(){return "sit";}
    protected String WALKANIM(){return "walk";}
    protected String FLYANIM(){return "fly";}
    protected String SWIMANIM(){return "swim";}
    protected String ATTACKANIM(){return "attack";}
    protected String SHOOTANIM(){return "shoot";}

    protected static final EntityDataAccessor<Integer> MOODPOINTS = SynchedEntityData.defineId(CustomDigimon.class, EntityDataSerializers.INT);
    public void setMoodPoints(int i){
        this.getEntityData().set(MOODPOINTS, i);
    }
    public int getMoodPoints(){
        return this.getEntityData().get(MOODPOINTS);
    }
    public void restMoodPoints(int i){
        this.getEntityData().set(MOODPOINTS, Math.max(this.getMoodPoints() - i,0));
    }
    public void addMoodPoints(int i){
        this.getEntityData().set(MOODPOINTS, Math.min(this.getMoodPoints() + i,24000));
    }
    public String getMood(){
        if(getMoodPoints() > 19200){
            return "Joyful";
        } else if (getMoodPoints() > 14400){
            return "Happy";
        } else if (getMoodPoints() > 9600){
            return "Meh";
        } else if (getMoodPoints() > 0){
            return "Sad";
        } else if (getMoodPoints() == 0){
            return "Depressed";
        }
        return "??";
    }
    public int getMoodColor(){
        if(getMoodPoints() > 19200){
            return 16761177;
        } else if (getMoodPoints() > 14400){
            return 16777088;
        } else if (getMoodPoints() > 9600){
            return 16646143;
        } else if (getMoodPoints() > 0){
            return 10262007;
        } else if (getMoodPoints() == 0){
            return 6579711;
        }
        return 16646143;
    }

    protected static final EntityDataAccessor<Integer> MOVEMENTID = SynchedEntityData.defineId(CustomDigimon.class, EntityDataSerializers.INT);
    public void setMovementID(int i){
        this.getEntityData().set(MOVEMENTID, i);
        this.switchNavigation(getMovementID());
        this.setOrderedToSit(i == 0);
    }
    public int getMovementID(){
        return this.getEntityData().get(MOVEMENTID);
    }
    public void changeMovementID(){
      int i = this.getMovementID();
      if(i == 0){
          setMovementID(1);
      } else if(i == 1 && this.ISFLYINGDIGIMON()){
          setMovementID(2);
      } else if(i == 1 && !this.ISFLYINGDIGIMON()){
          setMovementID(0);
      } else if(i == 2){
          setMovementID(0);
      }
    }

    //dragon-0 beast-1 insectplant-2 aquan-3 wind-4 machine-5 earth-6 nightmare-7 holy-8
    protected static final EntityDataAccessor<String> SPECIFICXPS = SynchedEntityData.defineId(CustomDigimon.class, EntityDataSerializers.STRING);
    public void addSpecificXps(int s){
        String[]ss = this.getSpecificXps().split("-");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ss.length; i++){
            if(i == s){
                sb.append(Integer.toString(Integer.parseInt(ss[i]) + 1)+"-");
            } else {
                sb.append(ss[i]+"-");
            }
        }
        this.getEntityData().set(SPECIFICXPS, sb.toString());
    }
    public void setSpecificXps(String s){
        this.getEntityData().set(SPECIFICXPS, s);
    }
    public String getSpecificXps(){
        return this.getEntityData().get(SPECIFICXPS);
    }
    public int getSpecificXps(int i){
        String[]ss = this.getSpecificXps().split("-");
        return Integer.parseInt(ss[i]);
    }

    protected static final EntityDataAccessor<Integer> EXPERIENCETOTAL = SynchedEntityData.defineId(CustomDigimon.class, EntityDataSerializers.INT);
    public void addExperienceTotal(){
        this.getEntityData().set(EXPERIENCETOTAL, getExperienceTotal() + 1);
    }
    public void setExperienceTotal(int i){
        this.getEntityData().set(EXPERIENCETOTAL, i);
    }
    public int getExperienceTotal(){
        return this.getEntityData().get(EXPERIENCETOTAL);
    }

    protected static final EntityDataAccessor<Integer> LEVELXP = SynchedEntityData.defineId(CustomDigimon.class, EntityDataSerializers.INT);
    public void addLevelXp(){
        this.getEntityData().set(LEVELXP, getLevelXp() + 1);
    }
    public void setLevelXp(int i){
        this.getEntityData().set(LEVELXP, i);
    }
    public int getLevelXp(){
        return this.getEntityData().get(LEVELXP);
    }

    protected final int MAXLEVEL = 35;
    protected static final EntityDataAccessor<Integer> CURRENTLEVEL = SynchedEntityData.defineId(CustomDigimon.class, EntityDataSerializers.INT);
    public void addCurrentLevel(){
        this.getEntityData().set(CURRENTLEVEL, Math.min(getCurrentLevel() + 1, MAXLEVEL));
        setCustomName(Component.literal(this.getNickName()));
        if(this.evolutionLevelAchieved()){this.setEvoCount(200);}
    }
    public void setCurrentLevel(int i){
        this.getEntityData().set(CURRENTLEVEL, i);
    }
    public int getCurrentLevel(){
        return this.getEntityData().get(CURRENTLEVEL);
    }

    protected int evoCount = 0;
    public int getEvoCount() {
        return evoCount;
    }
    public void setEvoCount(int e) {
        evoCount = e;
    }
    protected int ticksToAttackAnim = 0;

    protected int ticksToShootAnim = this.random.nextInt(150, 250);

    protected CustomDigimon(EntityType<? extends TamableAnimal> p_21803_, Level p_21804_) {
        super(p_21803_, p_21804_);
        this.switchNavigation(getMovementID());
        if(!this.hasCustomName()){this.setCustomName(Component.literal(getNickName()));
        } else {setCustomName(Component.literal(this.getNickName()));
        }
    }

    @Override
    public void setCustomName(@Nullable Component component) {
        this.setNickName(component.getString());
        super.setCustomName(Component.literal(component.getString() +  " (" + Integer.toString(this.getCurrentLevel()) + "Lv)"));
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.goalSelector.addGoal(1, new OwnerHurtTargetGoal(this));
        this.goalSelector.addGoal(1, new HurtByTargetGoal(this));
        this.goalSelector.addGoal(1, new DigitalMeleeAttackGoal(this,1.0D, true));
        this.goalSelector.addGoal(2, new DigitalFollowOwnerGoal(this, 1.0D, 10.0F, 2.0F, true));
        this.goalSelector.addGoal(3, new FloatGoal(this));
        this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(5, new RandomSwimmingGoal(this, 1.0D, 10));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 6.0F));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(NICKNAME, this.getSpecies());
        this.entityData.define(MOVEMENTID, 1);
        this.entityData.define(CURRENTLEVEL, 1);
        this.entityData.define(LEVELXP, 0);
        this.entityData.define(EXPERIENCETOTAL, 0);
        this.entityData.define(SPECIFICXPS, "0-0-0-0-0-0-0-0-0");
        this.entityData.define(MOODPOINTS, 24000);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("NAME")) {
            this.setNickName(compound.getString("NAME"));
        }
        if (compound.contains("MOVEMENTID")) {
            this.setMovementID(compound.getInt("MOVEMENTID"));
        }
        if (compound.contains("CURRENTLEVEL")) {
            this.setCurrentLevel(compound.getInt("CURRENTLEVEL"));
        }
        if (compound.contains("LEVELXP")) {
            this.setLevelXp(compound.getInt("LEVELXP"));
        }
        if (compound.contains("EXPERIENCETOTAL")) {
            this.setExperienceTotal(compound.getInt("EXPERIENCETOTAL"));
        }
        if (compound.contains("SPECIFICXPS")) {
            this.setSpecificXps(compound.getString("SPECIFICXPS"));
        }
        if (compound.contains("MOODPOINTS")) {
            this.setMoodPoints(compound.getInt("MOODPOINTS"));
        }
    }
    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putString("NAME", this.getNickName());
        compound.putInt("MOVEMENTID", this.getMovementID());
        compound.putInt("CURRENTLEVEL", this.getCurrentLevel());
        compound.putInt("LEVELXP", this.getLevelXp());
        compound.putInt("EXPERIENCETOTAL", this.getExperienceTotal());
        compound.putString("SPECIFICXPS", this.getSpecificXps());
        compound.putInt("MOODPOINTS", this.getMoodPoints());
    }

    public void evolveDigimon(){
        CustomDigimon evoD = null;
        if(canEvoToPath5()){
            evoD = (CustomDigimon) evoPath5().create(this.getLevel());
        } else if (canEvoToPath4()){
            evoD = (CustomDigimon) evoPath4().create(this.getLevel());
        } else if (canEvoToPath3()){
            evoD = (CustomDigimon) evoPath3().create(this.getLevel());
        } else if (canEvoToPath2()){
            evoD = (CustomDigimon) evoPath2().create(this.getLevel());
        } else if (canEvoToPath()){
            evoD = (CustomDigimon) evoPath().create(this.getLevel());
        }
        evoD.copyOtherDigi(this);
        this.getLevel().addFreshEntity(evoD);
        this.remove(RemovalReason.UNLOADED_TO_CHUNK);
    }

    public void copyOtherDigi(CustomDigimon d){
        this.tame((Player) Objects.requireNonNull(d.getOwner()));
        if(d.getNickName().equals(d.getSpecies())){
            this.setNickName(this.getSpecies());
            this.setCustomName(Component.literal(this.getNickName()));
        }
        else {
            this.setNickName(d.getNickName());
            this.setCustomName(Component.literal(this.getNickName()));
        }
        this.setMoodPoints(d.getMoodPoints());
        this.setPos(d.position());
        this.setExperienceTotal(d.getExperienceTotal());
        this.setLevelXp(d.getLevelXp());
        this.setCurrentLevel(d.getCurrentLevel());
        this.setSpecificXps(d.getSpecificXps());
    }

    public void useXpItem(int id){
        addExperienceTotal();
        addLevelXp();
        if(getLevelXp() >= getNeededXp()){
            setLevelXp(0);
            addCurrentLevel();
        }
        addSpecificXps(id);
    }

    public int getNeededXp(){
        int[] xpRequirements = {0, 5, 10, 15, 25, 50, 100};
        int i = getCurrentLevel();
        return (i >= 1 && i < xpRequirements.length) ? xpRequirements[i] : 1;
    }


    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (this.isTame() && this.getOwnerUUID().equals(player.getUUID()) && player.isShiftKeyDown()) {
            this.changeMovementID();
            this.switchNavigation(getMovementID());
            return InteractionResult.CONSUME;
        }
        return super.mobInteract(player, hand);
    }

    protected void switchNavigation(int b){
        if(b == 2  && !(moveControl instanceof FlyingMoveControl)){
            this.moveControl = new FlyingMoveControl(this, 20, true);
            this.navigation = new FlyingPathNavigation(this, this.level);
        } else if ((b != 2 && (moveControl instanceof FlyingMoveControl))){
            this.moveControl = new MoveControl(this);
            this.navigation = new GroundPathNavigation(this, this.level);
            this.setNoGravity(false);
        }
    }

    @Override
    public void tick() {
        restMoodPoints(1);
        if(getEvoCount() == 1){this.evolveDigimon();}
        evoCount = Math.max(evoCount - 1, 0);
        ticksToAttackAnim--;
        if(!this.isEvolving()){
            if(this.isAggressive()){
                ticksToShootAnim--;
                if (ticksToShootAnim == 20) {
                    doShoot(this);
                }
            }
            if (ticksToShootAnim == 0) {
                ticksToShootAnim = this.random.nextInt(150, 250);
            }
        }
        if(!activateName){
            activateName = true;
            setCustomName(Component.literal(this.getNickName()));
        }
        super.tick();
    }

    @Override
    public void travel(Vec3 p_218382_) {
        if(getMovementID() == 2){
            if (this.isEffectiveAi() || this.isControlledByLocalInstance()) {
                if (this.isInWater()) {
                    this.moveRelative(0.02F, p_218382_);
                    this.move(MoverType.SELF, this.getDeltaMovement());
                    this.setDeltaMovement(this.getDeltaMovement().scale((double) 0.8F));
                } else if (this.isInLava()) {
                    this.moveRelative(0.02F, p_218382_);
                    this.move(MoverType.SELF, this.getDeltaMovement());
                    this.setDeltaMovement(this.getDeltaMovement().scale(0.5D));
                } else {
                    this.moveRelative(this.getSpeed(), p_218382_);
                    this.move(MoverType.SELF, this.getDeltaMovement());
                    this.setDeltaMovement(this.getDeltaMovement().scale((double) 0.91F));
                }
            }
        } else {
            super.travel(p_218382_);
        }
    }

    public void doShoot(CustomDigimon entity){
        if(!this.getLevel().isClientSide()){
            CustomProjectile bullet = new CustomProjectile(DigitalEntities.BULLET.get(), entity.level);
                bullet.setOwner(this);
                bullet.setPos(new Vec3(this.position().x, this.position().y + this.getDimensions(Pose.STANDING).height - 1f, this.position().z));
                bullet.shootFromRotation(entity, entity.getXRot(), entity.yHeadRot, 0.0F, 0.75F, 0.75F);
                bullet.setYBodyRot(entity.yHeadRot);
                if(getTarget() != null){this.lookControl.setLookAt(this.getTarget());}
                this.level.addFreshEntity(bullet);
            }
    }

    //Animations
    protected AnimationFactory factory = createFactory(this);
    public static <T extends CustomDigimon & IAnimatable> AnimationController<T> animController(T digimon) {
        return new AnimationController<>(digimon,"movement", 3, event ->{
            if(!digimon.isEvolving()){
                if(digimon.getMovementID() == 0){
                    event.getController().setAnimation(new AnimationBuilder().addAnimation(digimon.SITANIM(), ILoopType.EDefaultLoopTypes.LOOP));
                } else {
                    if(digimon.getMovementID() == 2){
                        event.getController().setAnimation(new AnimationBuilder().addAnimation(digimon.FLYANIM(), ILoopType.EDefaultLoopTypes.LOOP));
                    }else {
                        if(digimon.getMovementID() == 1 && event.isMoving()){
                            event.getController().setAnimation(new AnimationBuilder().addAnimation(digimon.WALKANIM(), ILoopType.EDefaultLoopTypes.LOOP));
                        } else if (digimon.getMovementID() == 1){
                            event.getController().setAnimation(new AnimationBuilder().addAnimation(digimon.IDLEANIM(), ILoopType.EDefaultLoopTypes.LOOP));
                        }
                    }

                }
            } else {
                event.getController().setAnimation(new AnimationBuilder().addAnimation("show", ILoopType.EDefaultLoopTypes.LOOP));
            }
            return PlayState.CONTINUE;
        });
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel p_146743_, AgeableMob p_146744_) {
        return null;
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
