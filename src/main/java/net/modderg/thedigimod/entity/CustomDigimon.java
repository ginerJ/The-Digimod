package net.modderg.thedigimod.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.RegistryObject;
import net.modderg.thedigimod.entity.goals.DigitalFollowOwnerGoal;
import net.modderg.thedigimod.entity.goals.DigitalHurtByTargetGoal;
import net.modderg.thedigimod.entity.goals.DigitalMeleeAttackGoal;
import net.modderg.thedigimod.entity.goals.DigitalRangedAttackGoal;
import net.modderg.thedigimod.entity.managers.MoodManager;
import net.modderg.thedigimod.entity.managers.ParticleManager;
import net.modderg.thedigimod.goods.AbstractTrainingGood;
import net.modderg.thedigimod.item.*;
import net.modderg.thedigimod.item.custom.DigiviceItem;
import net.modderg.thedigimod.particles.DigitalParticles;
import net.modderg.thedigimod.projectiles.CustomProjectile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

import static net.minecraft.world.entity.ai.attributes.Attributes.FLYING_SPEED;

public class CustomDigimon extends TamableAnimal implements GeoEntity, ItemSteerable, PlayerRideableJumping, RangedAttackMob {

    public String getSpecies(){return "";}
    public String getLowerCaseSpecies(){return getSpecies().toLowerCase().replace("(", "").replace(")","");}

    protected boolean isDigimonMountable(){return false;}

    protected RegistryObject<?>[] reincarnateTo(){
        return null;
    }
    protected RegistryObject<?> xpDrop(){
        return null;
    }

    protected final int MAXLEVEL = 30;
    protected final int MAXADULT = 250;
    protected final int MAXULTIMATE = 500;
    public final int MAXMEGASTAT = 999;

    public int evoStage(){return 0;}

    public Boolean isBaby2(){return evoStage() == 0;}
    public Boolean isRookie(){return evoStage() == 1;}
    public Boolean isChampion(){return evoStage() == 2;}
    public Boolean isUltimate(){return evoStage() == 3;}

    //public Boolean isMega(){return evoStage().equals("mega");}

    public int getMaxStat(){
        return this.isBaby2() ? 25 : (this.isRookie() ? 100: (this.isChampion() ? MAXADULT: (this.isUltimate() ? MAXULTIMATE : MAXMEGASTAT)));
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

    protected EntityType<CustomDigimon> evoPath(){return null;}
    public CustomDigimon getEvoPath = evoPath() != null ? evoPath().create(this.level()): null;
    protected Boolean canEvoToPath(){return false;}

    protected EntityType<CustomDigimon> evoPath2(){return null;}
    public CustomDigimon getEvoPath2 = evoPath2() != null ? evoPath2().create(this.level()): null;
    protected Boolean canEvoToPath2(){return false;}

    protected EntityType<CustomDigimon> evoPath3(){return null;}
    public CustomDigimon getEvoPath3 = evoPath3() != null ? evoPath3().create(this.level()): null;
    protected Boolean canEvoToPath3(){return false;}

    protected EntityType<CustomDigimon> evoPath4(){return null;}
    public CustomDigimon getEvoPath4 = evoPath4() != null ? evoPath4().create(this.level()): null;
    protected Boolean canEvoToPath4(){return false;}

    protected EntityType<CustomDigimon> evoPath5(){return null;}
    public CustomDigimon getEvoPath5 = evoPath5() != null ? evoPath5().create(this.level()): null;
    protected Boolean canEvoToPath5(){return false;}

    protected EntityType<CustomDigimon> evoPath6(){return null;}
    public CustomDigimon getEvoPath6 = evoPath6() != null ? evoPath6().create(this.level()): null;
    protected Boolean canEvoToPath6(){return false;}

    public EntityType<CustomDigimon> digitronEvo(){return null;}

    protected static final EntityDataAccessor<String> PREEVO = SynchedEntityData.defineId(CustomDigimon.class, EntityDataSerializers.STRING);
    public void setPreEvo(String i){this.getEntityData().set(PREEVO, i);}
    public String getPreEvo(){return this.getEntityData().get(PREEVO);}

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
    //protected String swimAnim(){return "swim";}
    protected String attackAnim(){return "attack";}
    protected String shootAnim(){return "shoot";}
    protected String hurtAnim(){return "hurt";}


    public MoodManager moodManager = new MoodManager(this);
    public ParticleManager particleManager = new ParticleManager(this);
    protected static final EntityDataAccessor<Integer> MOVEMENTID = SynchedEntityData.defineId(CustomDigimon.class, EntityDataSerializers.INT);
    public void setMovementID(int i){
        this.getEntityData().set(MOVEMENTID, i);
        this.switchNavigation(i);
        this.setOrderedToSit(i == 0);
    }
    public int getMovementID(){return this.getEntityData().get(MOVEMENTID);}
    public void changeMovementID(){
        int i = this.getMovementID();
        if(i == 0){
            messageState("following");
            setMovementID(1);
        } else if(i == 1 && this.isFlyingDigimon()){
            messageState("flying");
            setMovementID(2);
        } else if(i == 2 || (i == 1 && !this.isFlyingDigimon())){
            messageState("wandering");
            setMovementID(1);
            setMovementID(-1);
        } else if (i == -1){
            messageState("sitting");
            this.setTarget(null);
            this.navigation.stop();
            setMovementID(0);
        }
    }
    public void messageState(String txt){
        if (Objects.requireNonNull(getOwner()).level().isClientSide && getOwner().level() instanceof ClientLevel) {
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

    protected static final EntityDataAccessor<Integer> LIFES = SynchedEntityData.defineId(CustomDigimon.class, EntityDataSerializers.INT);
    public void setLifes(int i){
        this.getEntityData().set(LIFES,i);
    }
    public int getLifes(){
        return this.getEntityData().get(LIFES);
    }
    public void addLife(){
        this.getEntityData().set(LIFES,Math.min(3,this.getLifes()+1));
        this.setHealth(MAXMEGASTAT);
    }
    public void restLifes(){
        if(this.getLastAttacker() instanceof CustomDigimon cd) {
            cd.setTarget(null);
            cd.setAggressive(false);
            cd.resetAttackGoals();
        }
        this.setCareMistakesStat(this.getCareMistakesStat() + 1);
        this.setMovementID(0);
        this.getEntityData().set(LIFES,Math.max(0,this.getLifes()-1));
        this.setHealth(MAXMEGASTAT);
    }

    protected static final EntityDataAccessor<Integer> ATTACK_STAT = SynchedEntityData.defineId(CustomDigimon.class, EntityDataSerializers.INT),
    DEFENCE_STAT = SynchedEntityData.defineId(CustomDigimon.class, EntityDataSerializers.INT),
    SPATTACK_STAT = SynchedEntityData.defineId(CustomDigimon.class, EntityDataSerializers.INT),
    SPDEFENCE_STAT = SynchedEntityData.defineId(CustomDigimon.class, EntityDataSerializers.INT),
    BATTLES_STAT = SynchedEntityData.defineId(CustomDigimon.class, EntityDataSerializers.INT),
    CARE_MISTAKES_STAT = SynchedEntityData.defineId(CustomDigimon.class, EntityDataSerializers.INT);

    public void setAttackStat(int i){
        this.getEntityData().set(ATTACK_STAT, Math.min(i, getMaxStat()));
        this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(this.getCurrentLevel()/2);
    }
    public void setDefenceStat(int i){this.getEntityData().set(DEFENCE_STAT, Math.min(i, getMaxStat()));}
    public void setSpAttackStat(int i){
        this.getEntityData().set(SPATTACK_STAT, Math.min(i, getMaxStat()));
    }
    public void setSpDefenceStat(int i){this.getEntityData().set(SPDEFENCE_STAT, Math.min(i, getMaxStat()));}
    public void setBattlesStat(int i){
        this.getEntityData().set(BATTLES_STAT, i);
    }
    public void setCareMistakesStat(int i){
        this.getEntityData().set(CARE_MISTAKES_STAT, i);
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
    public int getCareMistakesStat(){return this.getEntityData().get(CARE_MISTAKES_STAT);}
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

    public int ticksToShootAnim = this.random.nextInt(150, 250);

    DigitalRangedAttackGoal<CustomDigimon> rangedGoal = new DigitalRangedAttackGoal<>(this, 1.0D, 65, 10f);
    DigitalMeleeAttackGoal meleeGoal = new DigitalMeleeAttackGoal(this,1.0D, true);

    protected CustomDigimon(EntityType<? extends TamableAnimal> p_21803_, Level p_21804_) {
        super(p_21803_, p_21804_);
        this.switchNavigation(getMovementID());
        if(!this.hasCustomName()){this.setCustomName(Component.literal(getNickName()));
        } else {setCustomName(Component.literal(this.getNickName()));}
        resetAttackGoals();
    }

    @Override
    public boolean canFallInLove() {
        return false;
    }

    @Override
    public boolean isFood(ItemStack item) {
        if(item.is(DigiItems.DIGI_MEAT.get())){
            eatItem(item, 20);
            this.playSound(item.getItem().getEatingSound(), 0.15F, 1.0F);
            if(!this.isAggressive() || this.getTarget() instanceof AbstractTrainingGood){
                this.heal(20);
            }
            return true;
        }
        return false;
    }

    @ParametersAreNonnullByDefault
    @Override
    public boolean causeFallDamage(float p_147187_, float p_147188_, DamageSource p_147189_) {
        return !this.isFlyingDigimon() && super.causeFallDamage(p_147187_, p_147188_, p_147189_);
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor p_146746_, DifficultyInstance p_146747_, MobSpawnType p_146748_, @Nullable SpawnGroupData p_146749_, @Nullable CompoundTag p_146750_) {
        if(isFlyingDigimon() && this.getOwner() == null) {
            setMovementID(2);
            switchNavigation(2);
        }
        if(getSpawn() && !this.isTame()){
            setSpawn(false);
            this.setHealthStat(Math.min(getMaxStat(),
                    random.nextInt(this.isBaby2() ? 1 : (this.isRookie() ? 25: (this.isChampion() ? 100: (this.isUltimate() ? MAXADULT : MAXULTIMATE))),getMaxStat())));
            this.setHealth((float)this.getHealthStat());
            this.setCurrentLevel(Math.max(1,(int) (MAXLEVEL * (getHealth()/getMaxStat()*1.0))));
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
        this.goalSelector.addGoal(0, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.goalSelector.addGoal(1, new OwnerHurtTargetGoal(this));
        this.goalSelector.addGoal(1, new DigitalHurtByTargetGoal(this));
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
        this.entityData.define(MoodManager.getMoodAccessor(), 249);
        this.entityData.define(ATTACK_STAT, 1);
        this.entityData.define(DEFENCE_STAT, 1);
        this.entityData.define(SPATTACK_STAT, 1);
        this.entityData.define(SPDEFENCE_STAT, 1);
        this.entityData.define(BATTLES_STAT, 0);
        this.entityData.define(CARE_MISTAKES_STAT, 0);
        this.entityData.define(FIRSTSPAWN, true);
        this.entityData.define(LIFES, 1);
        this.entityData.define(PREEVO, "a-a-a-a-a");
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag compound) {
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
            this.moodManager.setMoodPoints(compound.getInt("MOODPOINTS"));
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
        if (compound.contains("CARE_MISTAKES_STAT")) {
            this.setCareMistakesStat(compound.getInt("CARE_MISTAKES_STAT"));
        }
        if (compound.contains("LIFES")) {
            this.setLifes(compound.getInt("LIFES"));
        }
        if (compound.contains("PREEVO")) {
            this.setPreEvo(compound.getString("PREEVO"));
        }
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putString("NAME", this.getNickName());
        compound.putInt("MOVEMENTID", this.getMovementID());
        compound.putInt("CURRENTLEVEL", this.getCurrentLevel());
        compound.putInt("LEVELXP", this.getLevelXp());
        compound.putInt("EXPERIENCETOTAL", this.getExperienceTotal());
        compound.putString("SPECIFICXPS", this.getSpecificXps());
        compound.putInt("MOODPOINTS", this.moodManager.getMoodPoints());
        compound.putInt("ATTACK_STAT", this.getAttackStat());
        compound.putInt("DEFENCE_STAT", this.getDefenceStat());
        compound.putInt("SPATTACK_STAT", this.getSpAttackStat());
        compound.putInt("SPDEFENCE_STAT", this.getSpDefenceStat());
        compound.putInt("BATTLES_STAT", this.getBattlesStat());
        compound.putInt("CARE_MISTAKES_STAT", this.getCareMistakesStat());
        compound.putBoolean("FIRSTSPAWN", this.getSpawn());
        compound.putInt("LIFES", this.getLifes());
        compound.putString("PREEVO", this.getPreEvo());
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        if (itemStack.getItem() instanceof DigiviceItem && this.getOwner() != null && getOwner().level().isClientSide){
            return InteractionResult.CONSUME;
        }
        if(itemStack.getItem().isEdible()){
            eatItem(itemStack, 1);
            this.playSound(itemStack.getItem().getEatingSound(), 0.15F, 1.0F);
            return InteractionResult.CONSUME;
        }
        if (this.isTame() && this.getOwnerUUID().equals(player.getUUID())) {
            if(player.isShiftKeyDown()){
                this.changeMovementID();
                this.switchNavigation(getMovementID());
                return InteractionResult.CONSUME;
            }
            if(this.isDigimonMountable() && !this.isInSittingPose()){
                if(!itemStack.isEmpty()) return super.mobInteract(player, hand);
                player.startRiding(this);
                return InteractionResult.SUCCESS;
            }
        }
        return super.mobInteract(player, hand);
    }

    int attack = this.getAttackStat(), defence = this.getDefenceStat(), spattack = this.getSpAttackStat(), spdefence = this.getSpDefenceStat(),
            battles = this.getBattlesStat(), health = this.getHealthStat(), mistakes = this.getCareMistakesStat(), lifes = this.getLifes();
    String xps = this.getSpecificXps();

    public void checkChangeStats(){
        if(attack != this.getAttackStat()){particleManager.spawnStatUpParticles(DigitalParticles.ATTACK_UP,1);
            attack = this.getAttackStat();}
        if(defence != this.getDefenceStat()){particleManager.spawnStatUpParticles(DigitalParticles.DEFENCE_UP,1);
            defence = this.getDefenceStat();}
        if(spattack != this.getSpAttackStat()){particleManager.spawnStatUpParticles(DigitalParticles.SPATTACK_UP,1);
            spattack = this.getSpAttackStat();}
        if(spdefence != this.getSpDefenceStat()){particleManager.spawnStatUpParticles(DigitalParticles.SPDEFENCE_UP,1);
            spdefence = this.getSpDefenceStat();}
        if(battles != this.getBattlesStat()){particleManager.spawnStatUpParticles(DigitalParticles.BATTLES_UP,1);
            battles = this.getBattlesStat();}
        if(health != this.getHealthStat()){particleManager.spawnStatUpParticles(DigitalParticles.HEALTH_UP,1);
            health = this.getHealthStat();}
        if(mistakes != this.getCareMistakesStat()){particleManager.spawnBubbleParticle(DigitalParticles.MISTAKE_BUBBLE);
            mistakes = this.getCareMistakesStat();}
        if(lifes != this.getLifes()){particleManager.spawnStatUpParticles(DigitalParticles.LIFE_PARTICLE,7);
            lifes = this.getLifes();}
    }

    @Override
    public void tick() {
        checkChangeStats();

        if(getEvoCount() == 1){this.evolveDigimon();}

        if(evoCount > 0){
            particleManager.spawnEvoParticles(DigitalParticles.EVO_PARTICLES);
            evoCount--;
        }

        if(this.moodManager.getMoodPoints() < 100 && this.isTame() && random.nextInt(0,150) == 1){
            particleManager.spawnBubbleParticle(DigitalParticles.MEAT_BUBBLE);
        }

        /*
        if(this.getTarget()instanceof AbstractTrainingGood){
            if(ticksToShootAnim < 0){
                ticksToShootAnim = -100;
                resetAttackGoals();
            }
        } else {
            if(ticksToShootAnim == 1 || ticksToShootAnim == -1) resetAttackGoals();

            if(ticksToShootAnim > 0) ticksToShootAnim--;
            else ticksToShootAnim++;
        }*/

        if(ticksToShootAnim == 1 || ticksToShootAnim == -1) resetAttackGoals();

        if(ticksToShootAnim > 0) ticksToShootAnim--;
        else ticksToShootAnim++;

        if(!activateName){
            activateName = true;
            setCustomName(Component.literal(this.getNickName()));
        }
        super.tick();
    }

    @Override
    public void travel(Vec3 pos) {
        if(!this.isEvolving()){
            if (this.isAlive()) {
                if (this.canBeControlledByRider()) {
                    LivingEntity passenger = (LivingEntity)getControllingPassenger();
                    this.yRotO = getYRot();
                    this.xRotO = getXRot();

                    this.setMaxUpStep(1.0f);

                    setYRot(passenger.getYRot());
                    setXRot(passenger.getXRot() * 0.5f);
                    setRot(getYRot(), getXRot());

                    this.yBodyRot = this.getYRot();
                    this.yHeadRot = this.yBodyRot;
                    float x = passenger.xxa * 0.25F;
                    float z = passenger.zza/2;

                    if (z <= 0) z *= 0.25f;

                    this.setSpeed(0.3f);

                    if (getMovementID() == 2)
                    {
                        float speed = (float) getAttributeValue(FLYING_SPEED) * 0.6f;
                        moveDist = moveDist > 0? moveDist : 0;
                        float movY = (float) (-this.getControllingPassenger().getXRot() * (Math.PI / 180));
                        pos = new Vec3(x, movY, z);
                        moveRelative(speed, pos);
                        move(MoverType.SELF, getDeltaMovement());
                        if (getDeltaMovement().lengthSqr() < 0.1)
                            setDeltaMovement(getDeltaMovement().add(0, Math.sin(tickCount / 4f) * 0.03, 0));
                        setDeltaMovement(getDeltaMovement().scale(0.9f));
                    } else {
                        super.travel(new Vec3((double) x, pos.y,  z));
                    }

                } else {
                    if(getMovementID() == 2){
                        if (this.isEffectiveAi() || this.isControlledByLocalInstance()) {
                            if (this.isInWater()) {
                                this.moveRelative(0.02F, pos);
                                this.move(MoverType.SELF, this.getDeltaMovement());
                                this.setDeltaMovement(this.getDeltaMovement().scale((double) 0.8F));
                            } else if (this.isInLava()) {
                                this.moveRelative(0.02F, pos);
                                this.move(MoverType.SELF, this.getDeltaMovement());
                                this.setDeltaMovement(this.getDeltaMovement().scale(0.5D));
                            } else {
                                this.moveRelative(this.getSpeed(), pos);
                                this.move(MoverType.SELF, this.getDeltaMovement());
                                this.setDeltaMovement(this.getDeltaMovement().scale((double) 0.91F));
                            }
                        }
                    } else {
                        super.travel(pos);
                    }
                }
            }
        }
    }

    @javax.annotation.Nullable
    public LivingEntity getControllingPassenger() {
        return this.getPassengers().isEmpty() ? null : (LivingEntity) this.getPassengers().get(0);
    }

    @Override
    public boolean boost() {
        return false;
    }


    @Override
    public void onPlayerJump(int p_21696_) {
        if (this.getMovementID() == 2) this.setMovementID(1);
        else this.setMovementID(2);
    }

    @Override
    public boolean canJump() {
        return this.isFlyingDigimon();
    }

    @Override
    public void handleStartJump(int p_21695_) {

    }

    @Override
    public void handleStopJump() {

    }

    @Override
    public void ejectPassengers() {
        this.setMovementID(1);
        super.ejectPassengers();
    }

    @Override
    public void die(DamageSource source) {
        if(source.getEntity() instanceof CustomDigimon digimon) {
            if (digimon.evoStage() == this.evoStage()) {
                digimon.setBattlesStat(digimon.getBattlesStat() + 1);
            }
            if (!this.isTame() && random.nextInt(0, 50) < 2) {
                this.level().addFreshEntity(new ItemEntity(level(),
                        this.getX(), this.getY(), this.getZ(), new ItemStack((ItemLike) reincarnateTo()[random.nextInt(reincarnateTo().length)].get())));
            } else if (!this.isTame()) {
                for (int i = 0; i < (this.isBaby2() ? 1 : this.isRookie() ? 5 : 15); i++) {
                    this.level().addFreshEntity(new ItemEntity(level(), this.getX(), this.getY(), this.getZ(), new ItemStack((ItemLike) xpDrop().get())));
                }
            }
        }
        if(this.isTame()){
            this.level().addFreshEntity(new ItemEntity(level(),
                    this.getX(), this.getY(), this.getZ(), new ItemStack((ItemLike) reincarnateTo()[random.nextInt(reincarnateTo().length)].get())));
        }
        super.die(source);
    }

    @Override
    public void setTarget(@Nullable LivingEntity entity) {
        if(entity instanceof CustomDigimon cd && cd.getOwner() != null && this.getOwner() != null && cd.isOwnedBy(this.getOwner())){
            return;
        }
        super.setTarget(entity);
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        if (this.level() instanceof ServerLevel serverLevel)
            triggerAnim("attackController", "attack");
        return target instanceof CustomDigimon cd ? cd.hurt(this.damageSources().mobAttack(this), calculateDamage(
                this.getAttackStat() + this.getCurrentLevel(),
                cd.getDefenceStat() + cd.getCurrentLevel()))
        : super.doHurtTarget(target);
    }

    @Override
    public void setHealth(float value) {
        if(value <= 0 && this.getLifes() > 1){
            this.restLifes();
        } else {
            super.setHealth(value);
        }
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel p_146743_, AgeableMob p_146744_) {
        return null;
    }

    public static boolean checkDigimonSpawnRules(EntityType<? extends Mob> p_217058_, LevelAccessor p_217059_, MobSpawnType p_217060_, BlockPos p_217061_, RandomSource p_217062_) {
        return true;
    }

    public void evolveDigimon(){
        CustomDigimon evoD = null;
        if(canEvoToPath6()){
            evoD = evoPath6().create(this.level());
        } else if(canEvoToPath5()){
            evoD = evoPath5().create(this.level());
        } else if (canEvoToPath4()){
            evoD = evoPath4().create(this.level());
        } else if (canEvoToPath3()){
            evoD = evoPath3().create(this.level());
        } else if (canEvoToPath2()){
            evoD = evoPath2().create(this.level());
        } else if (canEvoToPath()){
            evoD = evoPath().create(this.level());
        }
        assert evoD != null;
        evoD.copyOtherDigi(this);

        String[] prevos = this.getPreEvo().split("-");
        prevos[this.evoStage()] = this.getLowerCaseSpecies();
        evoD.setPreEvo(String.join("-", prevos));

        this.level().addFreshEntity(evoD);
        this.remove(RemovalReason.UNLOADED_TO_CHUNK);
    }

    public void deEvolveDigimon(){
        if(this.evoStage()-1 >= 0 && !this.getPreEvo().split("-")[this.evoStage()-1].equals("a")){
            CustomDigimon prevo = (CustomDigimon) DigitalEntities.digimonMap.get(this.getPreEvo().split("-")[this.evoStage()-1]).get().create(this.level());
            prevo.copyOtherDigi(this);

            prevo.setAttackStat(prevo.getMaxStat()/4);
            prevo.setDefenceStat(prevo.getMaxStat()/4);
            prevo.setSpAttackStat(prevo.getMaxStat()/4);
            prevo.setSpDefenceStat(prevo.getMaxStat()/4);
            prevo.setHealthStat(prevo.getMaxStat()/4);

            this.level().addFreshEntity(prevo);
            this.remove(RemovalReason.UNLOADED_TO_CHUNK);
        }
    }

    public void copyOtherDigi(CustomDigimon d){
        if(d.getOwner() != null){this.tame((Player) d.getOwner());}
        if(d.getNickName().equals(d.getSpecies())){this.setNickName(this.getSpecies());}
        else {this.setNickName(d.getNickName());}
        this.setMovementID(1);
        this.setCustomName(Component.literal(this.getNickName()));
        this.moodManager.setMoodPoints(d.moodManager.getMoodPoints());
        this.setPos(d.position());
        this.setExperienceTotal(d.getExperienceTotal());
        this.setLevelXp(d.getLevelXp());
        this.setCurrentLevel(d.getCurrentLevel());
        this.setSpecificXps(d.getSpecificXps());
        this.setPreEvo(d.getPreEvo());

        int evoAdd = d.isBaby2() ? 5 : d.isRookie() ? 15 :
                d.isChampion() ? 35 : 50;

        this.setAttackStat(d.getAttackStat() + evoAdd);
        this.setDefenceStat(d.getDefenceStat() + evoAdd);
        this.setSpAttackStat(d.getSpAttackStat() + evoAdd);
        this.setSpDefenceStat(d.getSpDefenceStat() + evoAdd);
        this.setHealthStat(d.getHealthStat() + evoAdd);

        this.setHealth(d.getHealth());
        this.setLifes(d.getLifes());
    }

    public boolean canBeControlledByRider() {
        return this.getControllingPassenger() instanceof Player p && this.isOwnedBy(p);
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
            this.navigation = new FlyingPathNavigation(this, this.level());
        } else if ((b != 2 && (moveControl instanceof FlyingMoveControl))){
            this.moveControl = new MoveControl(this);
            this.navigation = new GroundPathNavigation(this, this.level());
            this.setNoGravity(false);
        }
    }

    public int calculateDamage(int attack, int defense){return Math.max(1, attack/((defense+100)/100)/2);}

    @Override
    public void performRangedAttack(@NotNull LivingEntity mob, float p_33318_){
        if(getTarget() != null){
            if (this.level() instanceof ServerLevel serverLevel)
                triggerAnim("shootController", "shoot");
            this.lookControl.setLookAt(this.getTarget());
            CustomProjectile bullet = new CustomProjectile(DigitalEntities.BULLET.get(), this.level());
            bullet.setPos(new Vec3(this.position().x, this.position().y + 1, this.position().z));
            this.lookControl.setLookAt(this.getTarget());
            double d0 = mob.getX() - this.getX();
            double d1 = mob.getY(0.3333333333333333D) - bullet.getY();
            double d2 = mob.getZ() - this.getZ();
            double d3 = Math.sqrt(d0 * d0 + d2 * d2) * (double)0.2F;
            bullet.shoot(d0, d1 + d3 * (double)0.2F, d2, 1.6F, (float)(14 - this.level().getDifficulty().getId() * 4));
            bullet.setOwner(this);
            this.level().addFreshEntity(bullet);
            bullet.setOwner(this);
            this.level().addFreshEntity(bullet);
        }
    }

    public void eatItem(ItemStack itemStack,int moodAdd){
        if (this.level() instanceof ServerLevel serverLevel)
            triggerAnim("feedController", "feed");
        this.moodManager.addMoodPoints(moodAdd);
        particleManager.spawnItemParticles(itemStack, 16);
        itemStack.shrink(1);
    }

    public void resetAttackGoals() {
        if (!this.level().isClientSide) {
            this.goalSelector.removeGoal(this.meleeGoal);
            this.goalSelector.removeGoal(this.rangedGoal);

            if(ticksToShootAnim > 0){
                this.ticksToShootAnim = -this.random.nextInt(50,this.getAttackStat()<this.getSpAttackStat()?500:250);
                this.goalSelector.addGoal(1, this.rangedGoal);
            }else {
                this.ticksToShootAnim = this.random.nextInt(50,this.getAttackStat()>this.getSpAttackStat()?500:250);
                this.goalSelector.addGoal(1, this.meleeGoal);
            }
        }
    }

    //Animations
    protected AnimatableInstanceCache factory = new SingletonAnimatableInstanceCache(this);

    public static <T extends CustomDigimon & GeoEntity> AnimationController<T> animController(T digimon) {
        return new AnimationController<>(digimon,"movement", 5, event ->{
            if(digimon.isEvolving()){
                event.getController().setAnimation(RawAnimation.begin().then("show", Animation.LoopType.LOOP));
                return PlayState.CONTINUE;
            }
            if(digimon.getMovementID() == 0){
                event.getController().setAnimation(RawAnimation.begin().then(digimon.sitAnim(), Animation.LoopType.HOLD_ON_LAST_FRAME));
                return PlayState.CONTINUE;
            }

            if(digimon.getMovementID() == 1 || digimon.getMovementID() == -1 ){
                if(event.isMoving()){
                    event.getController().setAnimation(RawAnimation.begin().then(digimon.walkAnim(), Animation.LoopType.LOOP));
                } else {
                    event.getController().setAnimation(RawAnimation.begin().then(digimon.idleAnim(), Animation.LoopType.LOOP));
                }
            } else if (digimon.getMovementID() == 2){
                event.getController().setAnimation(RawAnimation.begin().then(digimon.flyAnim(), Animation.LoopType.LOOP));
            }

            return PlayState.CONTINUE;
        });
    }


    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(animController(this));

        controllers.add(new AnimationController<>(this, "attackController", state -> PlayState.STOP)
                .triggerableAnim("attack", RawAnimation.begin().then(this.attackAnim(), Animation.LoopType.PLAY_ONCE)));
        controllers.add(new AnimationController<>(this, "feedController", state -> PlayState.STOP)
                .triggerableAnim("feed", RawAnimation.begin().then("xp", Animation.LoopType.PLAY_ONCE)));
        controllers.add(new AnimationController<>(this, "shootController", state -> PlayState.STOP)
                .triggerableAnim("shoot", RawAnimation.begin().then(this.shootAnim(), Animation.LoopType.PLAY_ONCE)));
    }
    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.factory;
    }
}
