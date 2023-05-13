package net.modderg.thedigimod.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.protocol.game.ClientboundAnimatePacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.RegistryObject;
import net.modderg.thedigimod.entity.goals.ScapeDigimonGoal;
import net.modderg.thedigimod.entity.goods.CustomTrainingGood;
import net.modderg.thedigimod.goals.DigitalFollowOwnerGoal;
import net.modderg.thedigimod.goals.DigitalMeleeAttackGoal;
import net.modderg.thedigimod.item.DigiItems;
import net.modderg.thedigimod.item.DigiviceItem;
import net.modderg.thedigimod.particles.DigitalParticles;
import net.modderg.thedigimod.projectiles.CustomProjectile;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.Animation;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

import javax.annotation.ParametersAreNonnullByDefault;

public class CustomDigimon extends TamableAnimal implements GeoEntity {

    public String getSpecies(){return "";}

    protected RegistryObject<?>[] reincarnateTo(){
        return null;
    }

    protected final int MAXLEVEL = 30;
    public final int MAXBBYSTAT = 25;
    public final int MAXROOKIESTAT = 100;
    public final int MAXCHMPSTAT = 250;
    public final int MAXULTSTAT = 500;
    public final int MAXMEGASTAT = 999;

    public Boolean isBaby2(){return false;}
    public Boolean isRookie(){return false;}
    public Boolean isChampion(){return false;}
    public Boolean isUltimate(){return false;}
    //public Boolean isMega(){return false;}

    public int getMaxStat(){
        return this.isBaby2() ? MAXBBYSTAT : (this.isRookie() ? MAXROOKIESTAT: (this.isChampion() ? MAXCHMPSTAT: (this.isUltimate() ? MAXULTSTAT : MAXMEGASTAT)));
    }

    protected Boolean isFlyingDigimon(){return false;}
    //protected Boolean isSwimmerDigimon(){return false;}

    public Boolean evolutionLevelAchieved(){return (isRookie() && this.getCurrentLevel() > 15) || (isBaby2() && this.getCurrentLevel() > 5);}
    public Boolean isEvolving() {
        return getEvoCount() > 0;
    }
    //protected boolean isStill() {return this.getDeltaMovement().horizontalDistanceSqr() <= 1.0E-3D;}

    protected static final EntityDataAccessor<Boolean> FIRSTSPAWN = SynchedEntityData.defineId(CustomDigimon.class, EntityDataSerializers.BOOLEAN);
    public void setSpawn(Boolean i){
        this.getEntityData().set(FIRSTSPAWN, i);
    }
    public Boolean getSpawn(){
        return this.getEntityData().get(FIRSTSPAWN);
    }

    @Override
    public boolean canFallInLove() {
        return false;
    }
    @Override
    public boolean isFood(ItemStack item) {
        if(item.is(DigiItems.DIGI_MEAT.get())){
            this.addMoodPoints(10);
            return true;
        }
        return false;
    }

    @ParametersAreNonnullByDefault
    @Override
    public boolean causeFallDamage(float p_147187_, float p_147188_, DamageSource p_147189_) {
        return this.getMovementID() != 2 && super.causeFallDamage(p_147187_, p_147188_, p_147189_);
    }

    protected EntityType<CustomDigimon> evoPath(){return null;}
    protected Boolean canEvoToPath(){return false;}
    protected EntityType<CustomDigimon> evoPath2(){return null;}
    protected Boolean canEvoToPath2(){return false;}
    protected EntityType<CustomDigimon> evoPath3(){return null;}
    protected Boolean canEvoToPath3(){return false;}
    protected EntityType<CustomDigimon> evoPath4(){return null;}
    protected Boolean canEvoToPath4(){return false;}
    protected EntityType<CustomDigimon> evoPath5(){return null;}
    protected Boolean canEvoToPath5(){return false;}

    public EntityType<CustomDigimon> digitronEvo(){return null;}

    protected static final EntityDataAccessor<String> NICKNAME = SynchedEntityData.defineId(CustomDigimon.class, EntityDataSerializers.STRING);
    public void setNickName(String i){
        this.getEntityData().set(NICKNAME, i);
    }
    public String getNickName(){
        return this.getEntityData().get(NICKNAME);
    }
    private boolean activateName = false;

    protected String idleAnim(){return "idle";}
    protected String sitAnim(){return "sit";}
    protected String walkAnim(){return "walk";}
    protected String flyAnim(){return "fly";}
    protected String swimAnim(){return "swim";}
    protected String attackAnim(){return "attack";}
    protected String shootAnim(){return "shoot";}

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
        this.getEntityData().set(MOODPOINTS, Math.min(this.getMoodPoints() + i,250));
    }
    public String getMood(){
        if(getMoodPoints() > 200){
            return "Joyful";
        } else if (getMoodPoints() > 150){
            return "Happy";
        } else if (getMoodPoints() > 100){
            return "Meh";
        } else if (getMoodPoints() > 50){
            return "Sad";
        }
        return "Depressed";
    }
    public int getMoodColor() {
        int moodPoints = getMoodPoints();
        if (moodPoints > 200) return 16761177;
        if (moodPoints > 150) return 16777088;
        if (moodPoints > 100) return 16646143;
        if (moodPoints > 50) return 10262007;
        return 6579711;
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
            messageState("following");
            setMovementID(1);
        } else if(i == 1 && this.isFlyingDigimon()){
            messageState("flying");
            setMovementID(2);
        } else if(i == 1 && !this.isFlyingDigimon()){
            messageState("sitting");
            this.setTarget(null);
            setMovementID(0);
        } else if(i == 2){
            messageState("sitting");
            setMovementID(0);
        }
    }
    public void messageState(String txt){
        if (getOwner().getLevel().isClientSide && getOwner().getLevel() instanceof ClientLevel) {
            Minecraft.getInstance().gui.setOverlayMessage(Component.literal(txt), false);
        }
    }

    //dragon-0 beast-1 insectplant-2 aquan-3 wind-4 machine-5 earth-6 nightmare-7 holy-8
    protected static final EntityDataAccessor<String> SPECIFICXPS = SynchedEntityData.defineId(CustomDigimon.class, EntityDataSerializers.STRING);
    public void addSpecificXps(int s){
        String[]ss = this.getSpecificXps().split("-");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ss.length; i++){
            if(i == s){
                sb.append(Integer.parseInt(ss[i]) + 1 +"-");
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

    protected static final EntityDataAccessor<Integer> ATTACK_STAT = SynchedEntityData.defineId(CustomDigimon.class, EntityDataSerializers.INT),
    DEFENCE_STAT = SynchedEntityData.defineId(CustomDigimon.class, EntityDataSerializers.INT),
    SPATTACK_STAT = SynchedEntityData.defineId(CustomDigimon.class, EntityDataSerializers.INT),
    SPDEFENCE_STAT = SynchedEntityData.defineId(CustomDigimon.class, EntityDataSerializers.INT),
    BATTLES_STAT = SynchedEntityData.defineId(CustomDigimon.class, EntityDataSerializers.INT);

    public void setAttackStat(int i){
        this.getEntityData().set(ATTACK_STAT, Math.min(i, getMaxStat()));}
    public void setDefenceStat(int i){
        this.getEntityData().set(DEFENCE_STAT, Math.min(i, getMaxStat()));}
    public void setSpAttackStat(int i){
        this.getEntityData().set(SPATTACK_STAT, Math.min(i, getMaxStat()));}
    public void setSpDefenceStat(int i){
        this.getEntityData().set(SPDEFENCE_STAT, Math.min(i, getMaxStat()));}
    public void setBattlesStat(int i){
        this.getEntityData().set(BATTLES_STAT, i);
    }
    public void setHealthStat(int i){
        getAttribute(Attributes.MAX_HEALTH).setBaseValue(Math.min(i, getMaxStat()));
    }

    public int getAttackStat(){
        return this.getEntityData().get(ATTACK_STAT);
    }
    public int getDefenceStat(){
        return this.getEntityData().get(DEFENCE_STAT);
    }
    public int getSpAttackStat(){
        return this.getEntityData().get(SPATTACK_STAT);
    }
    public int getSpDefenceStat(){
        return this.getEntityData().get(SPDEFENCE_STAT);
    }
    public int getBattlesStat(){return this.getEntityData().get(BATTLES_STAT);}
    public int getHealthStat(){
        return (int) getAttribute(Attributes.MAX_HEALTH).getValue();
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

    protected int ticksToShootAnim = this.random.nextInt(150, 250);

    protected CustomDigimon(EntityType<? extends TamableAnimal> p_21803_, Level p_21804_) {
        super(p_21803_, p_21804_);
        this.switchNavigation(getMovementID());
        if(!this.hasCustomName()){this.setCustomName(Component.literal(getNickName()));
        } else {setCustomName(Component.literal(this.getNickName()));}
        scape = random.nextInt(100, 600);
    }

    public int scape;

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor p_146746_, DifficultyInstance p_146747_, MobSpawnType p_146748_, @Nullable SpawnGroupData p_146749_, @Nullable CompoundTag p_146750_) {
        if(isFlyingDigimon() && this.getOwner() == null) {
            setMovementID(2);
            switchNavigation(2);
        }
        if(getSpawn() && !this.isTame()){
            setSpawn(false);
            this.setHealthStat(Math.min(getMaxStat(),
                    random.nextInt(this.isBaby2() ? 1 : (this.isRookie() ? 25: (this.isChampion() ? 100: (this.isUltimate() ? 250 : 500))),getMaxStat())));
            this.setHealth((float)this.getHealthStat());
            this.setCurrentLevel((int) (getHealth()/999*100));
            this.setAttackStat((int)getHealth());
            this.setDefenceStat((int)getHealth());
            this.setSpAttackStat((int)getHealth());
            this.setSpDefenceStat((int)getHealth());
        }
        return super.finalizeSpawn(p_146746_, p_146747_, p_146748_, p_146749_, p_146750_);
    }

    @Override
    public void setCustomName(@Nullable Component component) {
        this.setNickName(component.getString());
        super.setCustomName(Component.literal(component.getString() +  " (" + Integer.toString(this.getCurrentLevel()) + "Lv)"));
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new ScapeDigimonGoal(this, CustomDigimon.class, 8.0F, 1.0D, 1.0D));
        this.goalSelector.addGoal(0, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.goalSelector.addGoal(1, new OwnerHurtTargetGoal(this));
        this.goalSelector.addGoal(1, new HurtByTargetGoal(this));
        this.goalSelector.addGoal(1, new DigitalMeleeAttackGoal(this,1.0D, true));
        this.goalSelector.addGoal(2, new DigitalFollowOwnerGoal(this, 1.0D, 10.0F, 2.0F, true));
        this.goalSelector.addGoal(3, new FloatGoal(this));
        this.goalSelector.addGoal(4, new WaterAvoidingRandomFlyingGoal(this, 1.0D));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 6.0F));
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
        this.entityData.define(MOODPOINTS, 249);
        this.entityData.define(ATTACK_STAT, 1);
        this.entityData.define(DEFENCE_STAT, 1);
        this.entityData.define(SPATTACK_STAT, 1);
        this.entityData.define(SPDEFENCE_STAT, 1);
        this.entityData.define(BATTLES_STAT, 0);
        this.entityData.define(FIRSTSPAWN, true);
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
        if (compound.contains("FIRSTSPAWN")) {
            this.setSpawn(compound.getBoolean("FIRSTSPAWN"));
        }
        if (compound.contains("ATTACK_STAT")) {
            this.setAttackStat(compound.getInt("ATTACK_STAT"));
        }
        if (compound.contains("DEFENCE_STAT")) {
            this.setDefenceStat(compound.getInt("DEFENCE_STAT"));
        }
        if (compound.contains("SPATTACK_STAT")) {
            this.setSpAttackStat(compound.getInt("SPATTACK_STAT"));
        }
        if (compound.contains("SPDEFENCE_STAT")) {
            this.setSpDefenceStat(compound.getInt("SPDEFENCE_STAT"));
        }
        if (compound.contains("BATTLES_STAT")) {
            this.setBattlesStat(compound.getInt("BATTLES_STAT"));
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
        compound.putInt("ATTACK_STAT", this.getAttackStat());
        compound.putInt("DEFENCE_STAT", this.getDefenceStat());
        compound.putInt("SPATTACK_STAT", this.getSpAttackStat());
        compound.putInt("SPDEFENCE_STAT", this.getSpDefenceStat());
        compound.putInt("BATTLES_STAT", this.getBattlesStat());
        compound.putBoolean("FIRSTSPAWN", this.getSpawn());
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (player.getItemInHand(hand).getItem() instanceof DigiviceItem && this.getOwner() != null && getOwner().getLevel().isClientSide)
            this.showStats();
        if (this.isTame() && this.getOwnerUUID().equals(player.getUUID()) && player.isShiftKeyDown()) {
            this.changeMovementID();
            this.switchNavigation(getMovementID());
            return InteractionResult.CONSUME;
        }
        return super.mobInteract(player, hand);
    }

    @Override
    public void tick() {
        checkChangeStats();

        if(getEvoCount() == 1){this.evolveDigimon();}
        if(evoCount > 0){
            spawnEvoParticles(DigitalParticles.EVO_PARTICLES);
            evoCount--;
        }

        if (!this.isEvolving() && this.isAggressive() && !(this.getTarget() instanceof CustomTrainingGood)) {
            if (--ticksToShootAnim == 20) {
                doShoot();
            } else if (ticksToShootAnim == 0) {
                ticksToShootAnim = this.random.nextInt(100, 500);
            }
        }

        if(scape < -60) scape = random.nextInt(100,600);
        scape--;

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
        } else if (!this.isEvolving() || !this.isOrderedToSit()){
            super.travel(p_218382_);
        }
    }

    @Override
    public void die(DamageSource source) {
        if(source.getEntity() instanceof CustomDigimon digimon){
            digimon.setBattlesStat(digimon.getBattlesStat() + 1);
        }
        if(this.isTame() || random.nextInt(0,50) < 5){
            this.getLevel().addFreshEntity(new ItemEntity(getLevel(),
                    this.getX(),this.getY(),this.getZ(), new ItemStack((ItemLike) reincarnateTo()[random.nextInt(reincarnateTo().length)].get())));
        }
        super.die(source);
    }

    @Override
    public void setTarget(@Nullable LivingEntity entity) {
        if(!(entity instanceof CustomDigimon cd && cd.getOwner() != null && this.getOwner() != null && cd.isOwnedBy(this.getOwner()))){
            super.setTarget(entity);
        }
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        return target instanceof CustomDigimon cd ? cd.hurt(DamageSource.mobAttack(this), calculateDamage(this.getAttackStat(), cd.getDefenceStat()))
        : super.doHurtTarget(target);
    }

    public void evolveDigimon(){
        CustomDigimon evoD = null;
        if(canEvoToPath5()){
            evoD = evoPath5().create(this.getLevel());
        } else if (canEvoToPath4()){
            evoD = evoPath4().create(this.getLevel());
        } else if (canEvoToPath3()){
            evoD = evoPath3().create(this.getLevel());
        } else if (canEvoToPath2()){
            evoD = evoPath2().create(this.getLevel());
        } else if (canEvoToPath()){
            evoD = evoPath().create(this.getLevel());
        }
        evoD.copyOtherDigi(this);
        this.getLevel().addFreshEntity(evoD);
        this.remove(RemovalReason.UNLOADED_TO_CHUNK);
    }

    public void copyOtherDigi(CustomDigimon d){
        if(d.getOwner() != null){this.tame((Player) d.getOwner());}
        if(d.getNickName().equals(d.getSpecies())){this.setNickName(this.getSpecies());}
        else {this.setNickName(d.getNickName());}
        this.setMovementID(d.getMovementID());
        this.setCustomName(Component.literal(this.getNickName()));
        this.setMoodPoints(d.getMoodPoints());
        this.setPos(d.position());
        this.setExperienceTotal(d.getExperienceTotal());
        this.setLevelXp(d.getLevelXp());
        this.setCurrentLevel(d.getCurrentLevel());
        this.setSpecificXps(d.getSpecificXps());
        this.setAttackStat(d.getAttackStat());
        this.setDefenceStat(d.getDefenceStat());
        this.setSpAttackStat(d.getSpAttackStat());
        this.setSpDefenceStat(d.getSpDefenceStat());
        this.setHealthStat(d.getHealthStat());
        this.setHealth(d.getHealth());
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
        int i = getCurrentLevel();
        return i <= 3 ? 2: (i <= 5 ? 5 : (i <= 10 ? 10:(i <= 15 ? 20:(i <= 20 ? 30: (i <= 30 ? 40: 50)))));
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

    int attack = this.getAttackStat(), defence = this.getDefenceStat(), spattack = this.getSpAttackStat(),
            spdefence = this.getSpDefenceStat(), battles = this.getBattlesStat(), health = this.getHealthStat();

    private String lastStat = "";

    public void showStats() {
        if (lastStat.equals("battles") || lastStat.equals("")) {
            lastStat = "attack";
            Minecraft.getInstance().gui.setOverlayMessage(Component.literal("⚔ Attack: " + Integer.toString(getAttackStat()) + " ⚔").withStyle(style -> style.withColor(TextColor.fromRgb(0xFF0000))), false);
        } else if (lastStat.equals("attack")) {
            lastStat = "defence";
            Minecraft.getInstance().gui.setOverlayMessage(Component.literal("\uD83D\uDEE1 Defence: " + Integer.toString(getDefenceStat()) + " \uD83D\uDEE1").withStyle(style -> style.withColor(TextColor.fromRgb(0x00FF00))), false);
        } else if (lastStat.equals("defence")) {
            lastStat = "spattack";
            Minecraft.getInstance().gui.setOverlayMessage(Component.literal("\uD83C\uDFF9 Sp.Attack: " + Integer.toString(getSpAttackStat()) + " \uD83C\uDFF9").withStyle(style -> style.withColor(TextColor.fromRgb(0xFF69B4))), false);
        } else if (lastStat.equals("spattack")) {
            lastStat = "spdefence";
            Minecraft.getInstance().gui.setOverlayMessage(Component.literal("⚙ Sp.Defence: " + Integer.toString(getSpDefenceStat()) + " ⚙").withStyle(style -> style.withColor(TextColor.fromRgb(0xADD8E6))), false);
        } else if (lastStat.equals("spdefence")) {
            lastStat = "battles";
            Minecraft.getInstance().gui.setOverlayMessage(Component.literal("\uD83D\uDDE1 Wins: " + Integer.toString(getBattlesStat()) + " \uD83D\uDDE1").withStyle(style -> style.withColor(TextColor.fromRgb(0xFF542D))), false);
        }
    }

    public void checkChangeStats(){
        if(attack != this.getAttackStat()){spawnStatUpParticles(DigitalParticles.ATTACK_UP);
            attack = this.getAttackStat();}
        if(defence != this.getDefenceStat()){spawnStatUpParticles(DigitalParticles.DEFENCE_UP);
            defence = this.getDefenceStat();}
        if(spattack != this.getSpAttackStat()){spawnStatUpParticles(DigitalParticles.SPATTACK_UP);
            spattack = this.getSpAttackStat();}
        if(spdefence != this.getSpDefenceStat()){spawnStatUpParticles(DigitalParticles.SPDEFENCE_UP);
            spdefence = this.getSpDefenceStat();}
        if(battles != this.getBattlesStat()){spawnStatUpParticles(DigitalParticles.BATTLES_UP);
            battles = this.getBattlesStat();}
        if(health != this.getHealthStat()){spawnStatUpParticles(DigitalParticles.HEALTH_UP);
            health = this.getHealthStat();}
    }

    public void spawnStatUpParticles(RegistryObject<SimpleParticleType> particle) {
        for(int i = 0; i < 360; i++) {
            if(i % 20 == 0) {
                this.getLevel().addParticle(particle.get(),
                        blockPosition().getX() + 0.75d, blockPosition().getY(), blockPosition().getZ() + 0.75d,
                        Math.cos(i) * 0.15d, 0.15d, Math.sin(i) * 0.15d);
            }
        }
    }

    public void spawnEvoParticles(RegistryObject<SimpleParticleType> particle) {
        for(int i = 0; i < 360; i++) {
            if(random.nextInt(0,20) == 5) {
                this.getLevel().addParticle(particle.get(),
                        blockPosition().getX() + 0.75d, blockPosition().getY(), blockPosition().getZ() + 0.75d,
                        Math.cos(i) * 0.3d, 0.15d + random.nextDouble()*0.1d, Math.sin(i) * 0.3d);
            }
        }
    }

    public void doShoot(){
        if(getTarget() != null){this.lookControl.setLookAt(this.getTarget());}

        CustomProjectile bullet = new CustomProjectile(DigitalEntities.BULLET.get(), this.level);
        bullet.setPos(new Vec3(this.position().x, this.position().y + 1, this.position().z));
        bullet.shootFromRotation(this, this.getXRot(), this.getYHeadRot(), 0, 1, 1);
        bullet.setYRot(this.getYHeadRot());
        bullet.setOwner(this);
        this.level.addFreshEntity(bullet);
    }

    public int calculateDamage(int attack, int defense){
        return (int) Math.max(1, attack - defense);
    }

    //Animations
    protected AnimatableInstanceCache factory = new SingletonAnimatableInstanceCache(this);

    public static <T extends CustomDigimon & GeoEntity> AnimationController<T> animController(T digimon) {
        return new AnimationController<>(digimon,"movement", 3, event ->{
            if(!digimon.isEvolving()){
                if(digimon.getMovementID() == 0){
                    event.getController().setAnimation(RawAnimation.begin().then(digimon.sitAnim(), Animation.LoopType.LOOP));
                } else {
                    if(digimon.getMovementID() == 2){
                        event.getController().setAnimation(RawAnimation.begin().then(digimon.flyAnim(), Animation.LoopType.LOOP));
                    }else {
                        if(digimon.getMovementID() == 1 && event.isMoving()){
                            event.getController().setAnimation(RawAnimation.begin().then(digimon.walkAnim(), Animation.LoopType.LOOP));
                        } else if (digimon.getMovementID() == 1){
                            event.getController().setAnimation(RawAnimation.begin().then(digimon.idleAnim(), Animation.LoopType.LOOP));
                        }
                    }
                }
            } else {
                event.getController().setAnimation(RawAnimation.begin().then("show", Animation.LoopType.LOOP));
            }
            return PlayState.CONTINUE;
        });
    }

    private PlayState attackPredicate(AnimationState state){
        if(this.swinging && state.getController().getAnimationState().equals(AnimationController.State.STOPPED)){
            state.getController().forceAnimationReset();
            state.getController().setAnimation(RawAnimation.begin().then(this.attackAnim(), Animation.LoopType.PLAY_ONCE));
            this.swinging = false;
        }
        return PlayState.CONTINUE;
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel p_146743_, AgeableMob p_146744_) {
        return null;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(animController(this));
        controllers.add(new AnimationController(this, "attackController", 0,this::attackPredicate));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.factory;
    }
}
