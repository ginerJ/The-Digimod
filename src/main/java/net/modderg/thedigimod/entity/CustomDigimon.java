package net.modderg.thedigimod.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
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
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.registries.RegistryObject;
import net.modderg.thedigimod.entity.goals.*;
import net.modderg.thedigimod.entity.managers.EvolutionCondition;
import net.modderg.thedigimod.entity.managers.MoodManager;
import net.modderg.thedigimod.entity.managers.ParticleManager;
import net.modderg.thedigimod.goods.AbstractTrainingGood;
import net.modderg.thedigimod.item.*;
import net.modderg.thedigimod.item.custom.DigiviceItem;
import net.modderg.thedigimod.particles.DigitalParticles;
import net.modderg.thedigimod.projectiles.CustomProjectile;
import net.modderg.thedigimod.projectiles.DigitalProjectiles;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.IntStream;

import static net.minecraft.world.entity.ai.attributes.Attributes.FLYING_SPEED;
import static net.minecraft.world.entity.ai.attributes.Attributes.MOVEMENT_SPEED;

public class CustomDigimon extends TamableAnimal implements GeoEntity, ItemSteerable, PlayerRideableJumping, RangedAttackMob {

    protected String rank = "zero";
    public String getRank(){return rank;}
    public CustomDigimon setRank(String rank){
        this.rank = rank;
        return this;
    }

    public int maxStatGain(){
        return rank.equals("zero")?3:(rank.equals("super")?5:2);
    }
    public int minStatGain(){
        return rank.equals("zero")?1:(rank.equals("super")?2:0);
    }

    protected String species = "";
    public String getSpecies(){return species;}
    public String getLowerCaseSpecies(){return getSpecies().toLowerCase().replace("(", "").replace(")","");}
    public CustomDigimon setSpecies(String species){
        this.species = species;
        return this;
    }

    protected double riderOffSet = 0;
    protected boolean isMountDigimon = false;
    protected CustomDigimon setMountDigimon(double riderOff) {
        isMountDigimon = true;
        this.riderOffSet = riderOff;
        return this;
    }

    protected RegistryObject<?>[] reincarnateTo = new RegistryObject[]{DigiItems.BOTAMON};
    public RegistryObject<?>[] getReincarnateTo(){
        return reincarnateTo;
    }

    public CustomDigimon setBabyAndXpDrop (RegistryObject<?> xp, RegistryObject<?>... babies){
        reincarnateTo = babies;
        xpDrop = xp;
        return this;
    }

    protected RegistryObject<?> xpDrop = DigiItems.DRAGON_DATA;
    public Item getXpDrop(){
        return (Item) xpDrop.get();
    }

    protected static final int MAXLEVEL = 30;
    protected static final int MAXADULT = 250;
    protected static final int MAXULTIMATE = 500;
    public static final int MAXMEGASTAT = 999;

    protected int evoStage;
    public int getEvoStage(){return evoStage;}
    public CustomDigimon setEvoStage(int evoStage){
        this.evoStage = evoStage;
        return this;
    }

    public Boolean isBaby2(){return getEvoStage() == 0;}
    public Boolean isRookie(){return getEvoStage() == 1;}
    public Boolean isChampion(){return getEvoStage() == 2;}
    public Boolean isUltimate(){return getEvoStage() == 3;}

    //public Boolean isMega(){return evoStage().equals("mega");}

    public int getMaxStat(){
        return this.isBaby2() ? 25 : (this.isRookie() ? 100: (this.isChampion() ? MAXADULT: (this.isUltimate() ? MAXULTIMATE : MAXMEGASTAT)));
    }

    protected boolean canFlyDigimon = false;
    public CustomDigimon setFlyingDigimon(){
        canFlyDigimon = true;
        return this;
    }

    protected boolean canSwimDigimon = false;

    public boolean isSwimmerDigimon() {
        return canSwimDigimon;
    }

    public CustomDigimon setSwimmerDigimon(){
        canSwimDigimon = true;
        return this;
    }

    public Boolean evolutionLevelAchieved(){return (isRookie() && this.getCurrentLevel() > 15) || (isBaby2() && this.getCurrentLevel() > 5);}

    public Boolean isEvolving() {
        return getEvoCount() > 0;
    }

    public CustomDigimon setEvos(String... evos){
        getEvoPaths = Arrays.stream(evos)
                        .map(evo -> evo == null ? null :DigitalEntities.digimonMap.get(evo).get().create(this.level()))
                        .toArray(CustomDigimon[]::new);
        return this;
    }

    public EvolutionCondition[] evolutionConditions = {null,null,null,null,null,null};

    public CustomDigimon setEvoConditions(EvolutionCondition... evoConditions) {
        Arrays.stream(evoConditions).forEach(evoCondition -> evoCondition.setDigimon(this));
        evolutionConditions = evoConditions;
        return this;
    }

    public CustomDigimon[] getEvoPaths = {null,null,null,null,null,null};

    protected Boolean canEvoToPath(int i){return evolutionConditions[i] != null && evolutionConditions[i].checkConditions();}

    public String digitronEvo;
    public CustomDigimon setDigitronEvo(String evo){
        digitronEvo = evo;
        return this;
    }

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

    public CustomDigimon setAnimations(String ia, String sa, String wa, String fa, String ata, String sha){
        if(ia != null){ idleAnim = ia;}
        if(sa != null){ sitAnim = sa;}
        if(wa != null){ walkAnim = wa;}
        if(fa != null){ flyAnim = fa;}
        if(ata != null){ attackAnim = ata;}
        if(sha != null){ shootAnim = sha;}
        return this;
    }

    protected String idleAnim= "idle";
    protected String sitAnim= "sit";
    protected String walkAnim="walk";
    protected String flyAnim= "fly";
    protected String attackAnim= "attack";
    protected String shootAnim= "shoot";

    protected static final EntityDataAccessor<String> SPMOVENAME = SynchedEntityData.defineId(CustomDigimon.class, EntityDataSerializers.STRING);
    public void setSpMoveName(String i){
        this.getEntityData().set(SPMOVENAME, i);
    }
    public String getSpMoveName(){
        return this.getEntityData().get(SPMOVENAME);
    }

    public MoodManager moodManager = new MoodManager(this);
    public ParticleManager particleManager = new ParticleManager();

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
        } else if(i == 1 && this.canFlyDigimon){
            messageState("flying");
            setMovementID(2);
        } else if(i == 2 || (i == 1 && !this.canFlyDigimon)){
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
        Objects.requireNonNull(this.getAttribute(Attributes.ATTACK_DAMAGE)).setBaseValue((double) this.getCurrentLevel() /2);
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
        Objects.requireNonNull(getAttribute(Attributes.MAX_HEALTH)).setBaseValue(Math.min(i, getMaxStat()));
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
        return (int) Objects.requireNonNull(getAttribute(Attributes.MAX_HEALTH)).getValue();
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
        this.setCurrentLevel(Math.min(getCurrentLevel() + 1, MAXLEVEL));
        if(this.evolutionLevelAchieved()){this.setEvoCount(200);}
    }
    public void setCurrentLevel(int i){
        this.getEntityData().set(CURRENTLEVEL, i);
        this.setCustomName(Component.literal(this.getNickName()));
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
        resetAttackGoals();
    }

    @Override
    public void setCustomName(@Nullable Component component) {
        if(component != null && !component.getString().isEmpty()){
            this.setNickName(component.getString());
        } else {
            component = Component.literal(this.getSpecies());
        }
        super.setCustomName(Component.literal(component.getString() +  " (" + Integer.toString(this.getCurrentLevel()) + "Lv)"));
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 5.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                .add(Attributes.ATTACK_DAMAGE, 1D)
                .add(Attributes.FLYING_SPEED, 0.15D);
    }


    public static boolean checkDigimonSpawnRules(EntityType<? extends Mob> p_217058_, LevelAccessor p_217059_, MobSpawnType p_217060_, BlockPos p_217061_, RandomSource p_217062_) {
        return true;
    }

    public static boolean checkWaterDigimonSpawnRules(EntityType<? extends CustomDigimon> p_218283_, LevelAccessor p_218284_, MobSpawnType p_218285_, BlockPos p_218286_, RandomSource p_218287_) {
        int i = p_218284_.getSeaLevel();
        int j = i - 13;
        return p_218286_.getY() >= j && p_218286_.getY() <= i && p_218284_.getFluidState(p_218286_.below()).is(FluidTags.WATER) && p_218284_.getBlockState(p_218286_.above()).is(Blocks.WATER);
    }

    public void evolveDigimon(){
        CustomDigimon evoD = IntStream.range(0, (int) Arrays.stream(getEvoPaths).filter(Objects::nonNull).count())
                .filter(this::canEvoToPath)
                .mapToObj(i -> getEvoPaths[i])
                .reduce((first, second) -> second)
                .get();

        evoD.copyOtherDigi(this);

        String[] prevos = this.getPreEvo().split("-");
        prevos[this.getEvoStage()] = this.getLowerCaseSpecies();
        evoD.setPreEvo(String.join("-", prevos));

        this.level().addFreshEntity(evoD);
        this.remove(RemovalReason.UNLOADED_TO_CHUNK);
    }

    public void deEvolveDigimon(){
        if(this.getEvoStage()-1 >= 0 && !this.getPreEvo().split("-")[this.getEvoStage()-1].equals("a")){

            CustomDigimon prevo = (CustomDigimon) DigitalEntities.digimonMap.get(this.getPreEvo().split("-")[this.getEvoStage()-1]).get().create(this.level());

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
        if(d.getNickName().equals(d.getDisplayName().toString())){this.setNickName(this.getDisplayName().toString());}
        this.setMovementID(1);
        this.setNickName(d.getNickName());
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
        } else if ((b == 3 && !(moveControl instanceof WaterMoveControl))){
            this.moveControl = new WaterMoveControl(this);
            this.navigation = new DigitalWaterPathNavigation(this, this.level());
            this.setNoGravity(false);
        } else if ((b != 2 && b != 3 && ((moveControl instanceof FlyingMoveControl)||(moveControl instanceof WaterMoveControl)))){
            this.moveControl = new MoveControl(this);
            this.navigation = new GroundPathNavigation(this, this.level());
            this.setNoGravity(false);
        }
    }


    public int calculateDamage(int attack, int defense){return Math.max(1, attack/((defense+100)/100)/2);}

    public void eatItem(ItemStack itemStack,int moodAdd){
        this.moodManager.addMoodPoints(moodAdd);
        particleManager.spawnItemParticles(itemStack, 16, this);
        itemStack.shrink(1);
        if (this.level() instanceof ServerLevel)
            triggerAnim("feedController", "feed");
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

    @Override
    public boolean canFallInLove() {
        return false;
    }

    @Override
    public boolean isFood(ItemStack item) {
        if(isFoodBool(item)){
            eatItem(item, 20);
            this.playSound(item.getItem().getEatingSound(), 0.15F, 1.0F);
            if(!this.isAggressive() || this.getTarget() instanceof AbstractTrainingGood){
                this.heal(20);
            }
            return true;
        }
        return false;
    }

    public boolean isFoodBool(ItemStack item){
        return item.is(DigiItems.DIGI_MEAT.get())||item.is(DigiItems.GUILMON_BREAD.get());
    }

    @ParametersAreNonnullByDefault
    @Override
    public boolean causeFallDamage(float p_147187_, float p_147188_, DamageSource p_147189_) {
        return !this.canFlyDigimon && super.causeFallDamage(p_147187_, p_147188_, p_147189_);
    }

    @Override
    public SpawnGroupData finalizeSpawn(@NotNull ServerLevelAccessor p_146746_, @NotNull DifficultyInstance p_146747_, @NotNull MobSpawnType p_146748_, @Nullable SpawnGroupData p_146749_, @Nullable CompoundTag p_146750_) {
        if(canFlyDigimon && this.getOwner() == null) {
            setMovementID(2);
            switchNavigation(2);
        }
        if(!this.isTame()){

            this.setHealthStat(Math.min(getMaxStat(),
                    random.nextInt(this.isBaby2() ? 1 : (this.isRookie() ? 25: (this.isChampion() ? 100: (this.isUltimate() ? MAXADULT : MAXULTIMATE))),getMaxStat())));

            this.setHealth((float)this.getHealthStat());
            this.setCurrentLevel((int) Math.max(1, MAXLEVEL/100f * getHealth()/250f*100f));
            this.setAttackStat((int)getHealth());
            this.setDefenceStat((int)getHealth());
            this.setSpAttackStat((int)getHealth());
            this.setSpDefenceStat((int)getHealth());
        }
        return super.finalizeSpawn(p_146746_, p_146747_, p_146748_, p_146749_, p_146750_);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.goalSelector.addGoal(2, new OwnerHurtTargetGoal(this));
        this.goalSelector.addGoal(3, new DigitalHurtByTargetGoal(this));
        this.goalSelector.addGoal(4, new DigitalFollowOwnerGoal(this, 1.0D, 10.0F, 2.0F, true));
        this.goalSelector.addGoal(5, new DigimonFloatGoal(this));
        this.goalSelector.addGoal(6, new WaterAvoidingRandomFlyingGoal(this, 1.0D));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0F));
    }

    String getDefaultSpMove(){return null;}

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(NICKNAME, "");
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
        this.entityData.define(LIFES, 1);
        this.entityData.define(PREEVO, "a-a-a-a-a");
        this.entityData.define(SPMOVENAME, this.getDefaultSpMove());
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
        if (compound.contains("SPMOVENAME")) {
            this.setSpMoveName(compound.getString("SPMOVENAME"));
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
        compound.putInt("LIFES", this.getLifes());
        compound.putString("PREEVO", this.getPreEvo());
        compound.putString("SPMOVENAME", this.getSpMoveName());
    }

    @Override
    public @NotNull InteractionResult mobInteract(Player player, @NotNull InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        Item item = itemStack.getItem();

        if (item instanceof DigiviceItem && this.getOwner() != null && getOwner().level().isClientSide){
            return InteractionResult.CONSUME;
        }

        if(item.isEdible() && !isFoodBool(itemStack)){
            eatItem(itemStack, 5);
            this.playSound(itemStack.getItem().getEatingSound(), 0.15F, 1.0F);
            return InteractionResult.CONSUME;
        }

        if (this.isTame() && Objects.equals(this.getOwnerUUID(), player.getUUID())) {
            if(player.isShiftKeyDown()){
                this.changeMovementID();
                return InteractionResult.CONSUME;
            }
            if(!this.isInSittingPose()){
                if(!itemStack.isEmpty()) return super.mobInteract(player, hand);
                if(this.isMountDigimon){
                    player.startRiding(this);
                    if(this.getMovementID() == -1) this.setMovementID(1);

                } else if(this.isBaby2()){
                    if(player.getPassengers().isEmpty())this.startRiding(player);
                    else if(player.getPassengers().stream().anyMatch(p -> p instanceof CustomDigimon))
                        player.getPassengers().stream()
                            .filter(p -> p instanceof CustomDigimon)
                                .forEach(e -> e.startRiding(this));

                }
                return InteractionResult.SUCCESS;
            }
        }
        return super.mobInteract(player, hand);
    }

    int attack = this.getAttackStat(), defence = this.getDefenceStat(), spattack = this.getSpAttackStat(), spdefence = this.getSpDefenceStat(),
            battles = this.getBattlesStat(), health = this.getHealthStat(), mistakes = this.getCareMistakesStat(), lifes = this.getLifes();

    public void checkChangeStats(){
        if(attack != this.getAttackStat()){particleManager.spawnStatUpParticles(DigitalParticles.ATTACK_UP,1, this);
            attack = this.getAttackStat();}
        if(defence != this.getDefenceStat()){particleManager.spawnStatUpParticles(DigitalParticles.DEFENCE_UP,1, this);
            defence = this.getDefenceStat();}
        if(spattack != this.getSpAttackStat()){particleManager.spawnStatUpParticles(DigitalParticles.SPATTACK_UP,1, this);
            spattack = this.getSpAttackStat();}
        if(spdefence != this.getSpDefenceStat()){particleManager.spawnStatUpParticles(DigitalParticles.SPDEFENCE_UP,1, this);
            spdefence = this.getSpDefenceStat();}
        if(battles != this.getBattlesStat()){particleManager.spawnStatUpParticles(DigitalParticles.BATTLES_UP,1, this);
            battles = this.getBattlesStat();}
        if(health != this.getHealthStat()){particleManager.spawnStatUpParticles(DigitalParticles.HEALTH_UP,1, this);
            health = this.getHealthStat();}
        if(mistakes != this.getCareMistakesStat()){particleManager.spawnBubbleParticle(DigitalParticles.MISTAKE_BUBBLE, this);
            mistakes = this.getCareMistakesStat();}
        if(lifes != this.getLifes()){particleManager.spawnStatUpParticles(DigitalParticles.LIFE_PARTICLE,7, this);
            lifes = this.getLifes();}
    }

    private boolean setName = false;

    @Override
    public void tick() {
        checkChangeStats();

        if(getEvoCount() == 1){this.evolveDigimon();}

        if(evoCount > 0){
            particleManager.spawnEvoParticles(DigitalParticles.EVO_PARTICLES, this);
            evoCount--;
        }

        if(this.moodManager.getMoodPoints() < 100 && this.isTame() && random.nextInt(0,150) == 1){
            particleManager.spawnBubbleParticle(DigitalParticles.MEAT_BUBBLE, this);
        }

        if(this.getTarget()instanceof AbstractTrainingGood){
            if(ticksToShootAnim < 0){
                ticksToShootAnim = -100;
                resetAttackGoals();
            }
        } else {
            if(ticksToShootAnim == 1 || ticksToShootAnim == -1) resetAttackGoals();

            if(ticksToShootAnim > 0) ticksToShootAnim--;
            else ticksToShootAnim++;
        }

        if(ticksToShootAnim == 1 || ticksToShootAnim == -1) resetAttackGoals();

        if(ticksToShootAnim > 0) ticksToShootAnim--;
        else ticksToShootAnim++;

        if(this.canSwimDigimon){
            if(this.isInWater()){
                if(!(this.moveControl instanceof WaterMoveControl)) this.switchNavigation(3);
            } else {
                if(this.moveControl instanceof WaterMoveControl) this.switchNavigation(1);
            }
        }

        if(!setName){
            this.setCustomName(Component.literal(this.getSpecies()));
            setName = true;
        }

        super.tick();
    }

    @Override
    protected boolean canRide(Entity entity) {
        if(this.isBaby2()){
            return entity instanceof Player;
        }
        return super.canRide(entity);
    }

    @Override
    public void travel(@NotNull Vec3 pos) {
        if(!this.isEvolving()){
            if (this.isAlive()) {
                if (this.canBeControlledByRider()) {
                    LivingEntity passenger = getControllingPassenger();
                    this.yRotO = getYRot();
                    this.xRotO = getXRot();

                    setYRot(passenger.getYRot());
                    setXRot(passenger.getXRot() * 0.5f);
                    setRot(getYRot(), getXRot());

                    this.yBodyRot = this.getYRot();
                    this.yHeadRot = this.yBodyRot;
                    float x = passenger.xxa * 0.25F;
                    float z = passenger.zza/2;

                    if (z <= 0) z *= 0.25f;

                    this.setSpeed((float)this.getAttribute(MOVEMENT_SPEED).getValue()*(float)this.getAttribute(MOVEMENT_SPEED).getValue()/0.2f);

                    if (getMovementID() == 2)
                    {
                        float speed = (float) getAttributeValue(FLYING_SPEED);
                        moveDist = moveDist > 0? moveDist : 0;
                        float movY = (float) (-this.getControllingPassenger().getXRot() * (Math.PI / 180));
                        pos = new Vec3(x, movY, z);
                        moveRelative(speed, pos);
                        move(MoverType.SELF, getDeltaMovement());
                        if (getDeltaMovement().lengthSqr() < 0.1)
                            setDeltaMovement(getDeltaMovement().add(0, Math.sin(tickCount / 4f) * 0.03, 0));
                        setDeltaMovement(getDeltaMovement().scale(0.9f));

                    } else {
                        super.travel(new Vec3(x, pos.y,  z));
                    }

                } else {
                    if(getMovementID() == 2){
                        if (this.isEffectiveAi() || this.isControlledByLocalInstance()) {
                            if (this.isInWater()) {
                                this.moveRelative(0.02F, pos);
                                this.move(MoverType.SELF, this.getDeltaMovement());
                                this.setDeltaMovement(this.getDeltaMovement().scale( 0.8F));
                            } else if (this.isInLava()) {
                                this.moveRelative(0.02F, pos);
                                this.move(MoverType.SELF, this.getDeltaMovement());
                                this.setDeltaMovement(this.getDeltaMovement().scale(0.5D));
                            } else {
                                this.moveRelative(this.getSpeed(), pos);
                                this.move(MoverType.SELF, this.getDeltaMovement());
                                this.setDeltaMovement(this.getDeltaMovement().scale( 0.91F));
                            }
                        }
                    } else {
                        super.travel(pos);
                    }
                }
            }
        }
    }

    @Override
    public boolean isPushedByFluid() {
        if(this.canSwimDigimon){
            return false;
        }
        return super.isPushedByFluid();
    }

    @Override
    public boolean isNoGravity() {
        if(canSwimDigimon) return this.isInWater();
        return super.isNoGravity();
    }

    @Override
    public boolean canDrownInFluidType(FluidType type) {
        if (type == ForgeMod.WATER_TYPE.get()) return !this.canSwimDigimon;
        return super.canDrownInFluidType(type);
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
        else this.changeMovementID();
    }

    @Override
    public boolean canJump() {
        return this.canFlyDigimon;
    }

    @Override
    public void handleStartJump(int p_21695_) {

    }

    @Override
    public void handleStopJump() {

    }

    @Override
    public boolean canBeLeashed(@NotNull Player player) {
        return super.canBeLeashed(player)&&this.isOwnedBy(player);
    }

    public boolean chanceOverHundred(int chance){
        return random.nextInt(100) <= chance;
    }

    public void dropItem(Item item, int chance){
        if(chanceOverHundred(chance))
            this.level().addFreshEntity(new ItemEntity(level(), this.getX(), this.getY(), this.getZ(), new ItemStack(item)));
    }

    @Override
    public void die(@NotNull DamageSource source) {

        if(this.isTame()){
            dropItem((Item) getReincarnateTo()[random.nextInt(getReincarnateTo().length)].get(), 101);
        } else{
            if(source.getEntity() instanceof CustomDigimon digimon) {
                if (digimon.getEvoStage() == this.getEvoStage()) {
                    digimon.setBattlesStat(digimon.getBattlesStat() + 1);
                }

                dropItem((Item) getReincarnateTo()[random.nextInt(getReincarnateTo().length)].get(), 3);
                dropItem(DigiItems.itemMap.get("chip_"+this.getSpMoveName()).get(), 5);

                dropItem(DigiItems.DIGI_MEAT.get(), 10);

                if(!this.isBaby2()) {
                    dropItem(DigiItems.ATTACK_BYTE.get(), 7);
                    dropItem(DigiItems.DEFENSE_BYTE.get(), 7);
                    dropItem(DigiItems.SPATTACK_BYTE.get(), 7);
                    dropItem(DigiItems.SPDEFENSE_BYTE.get(), 7);
                    dropItem(DigiItems.HEALTH_BYTE.get(), 7);
                }

                for (int i = 0; i < (this.isBaby2() ? 1 : this.isRookie() ? 5 : 15); i++) {
                    dropItem(this.getXpDrop(), 85);
                }
            }
        }
        super.die(source);
    }

    @Override
    public void setTarget(@Nullable LivingEntity entity) {
        if(entity instanceof Player || (entity instanceof CustomDigimon cd && cd.getOwner() != null && this.getOwner() != null && cd.isOwnedBy(this.getOwner()))){
            return;
        }

        super.setTarget(entity);
    }

    @Override
    public boolean doHurtTarget(@NotNull Entity target) {
        if (this.level() instanceof ServerLevel)
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
    public AgeableMob getBreedOffspring(@NotNull ServerLevel p_146743_, @NotNull AgeableMob p_146744_) {
        return null;
    }

    private int repeat = 0;
    @Override
    public void performRangedAttack(@NotNull LivingEntity mob, float p_33318_){
        if(mob != null){
            CustomProjectile bullet = (CustomProjectile) DigitalProjectiles.projectileMap.get(this.getSpMoveName()).get().create(this.level());
            if (repeat == 0) repeat = bullet.getRepeatTimes();
            if (mob.level() instanceof ServerLevel)
                triggerAnim("shootController", "shoot");
            bullet.performRangedAttack(this, mob);
            if (repeat > 1 && this.ticksToShootAnim < 0) this.rangedGoal.setAttackTime(5);
            --repeat;
            for(MobEffectInstance effect : bullet.oEffects) {
                    this.addEffect(effect, this);
            }
        }
    }

    @Override
    public void setOrderedToSit(boolean p_21840_) {
        this.moveControl.strafe(0f,0f);
        super.setOrderedToSit(p_21840_);
    }

    @Override
    public double getPassengersRidingOffset() {
        return super.getPassengersRidingOffset() + riderOffSet
                + (!this.getPassengers().isEmpty()&&this.getPassengers().get(0)instanceof CustomDigimon ?-0.55:0);
    }

    @Override
    public double getMyRidingOffset() {
        return super.getMyRidingOffset() + 0.3d;
    }

    //Animations
    protected AnimatableInstanceCache factory = new SingletonAnimatableInstanceCache(this);

    public static <T extends CustomDigimon & GeoEntity> AnimationController<T> animController(T digimon) {
        return new AnimationController<>(digimon,"movement", 10, event ->{
            if(digimon.isEvolving()){
                event.getController().setAnimation(RawAnimation.begin().then("show", Animation.LoopType.LOOP));
                return PlayState.CONTINUE;
            }
            if(digimon.isInSittingPose()){
                event.getController().setAnimation(RawAnimation.begin().then(digimon.sitAnim, Animation.LoopType.HOLD_ON_LAST_FRAME));
                return PlayState.CONTINUE;
            }

            if (digimon.getMovementID() == 2){
                event.getController().setAnimation(RawAnimation.begin().then(digimon.flyAnim, Animation.LoopType.LOOP));
                return PlayState.CONTINUE;
            } else {
                if (event.isMoving()) {
                    if(digimon.isInWater() && digimon.canSwimDigimon) event.getController().setAnimation(RawAnimation.begin().then(digimon.flyAnim, Animation.LoopType.LOOP));
                    else event.getController().setAnimation(RawAnimation.begin().then(digimon.walkAnim, Animation.LoopType.LOOP));
                    return PlayState.CONTINUE;
                } else {
                    event.getController().setAnimation(RawAnimation.begin().then(digimon.idleAnim, Animation.LoopType.LOOP));
                    return PlayState.CONTINUE;
                }
            }
        });
    }


    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(animController(this));

        controllers.add(new AnimationController<>(this, "shootController", state -> PlayState.STOP)
                .triggerableAnim("shoot", RawAnimation.begin().then(this.shootAnim, Animation.LoopType.PLAY_ONCE)));
        controllers.add(new AnimationController<>(this, "attackController", state -> PlayState.STOP)
                .triggerableAnim("attack", RawAnimation.begin().then(this.attackAnim, Animation.LoopType.PLAY_ONCE)));
        controllers.add(new AnimationController<>(this, "feedController", state -> PlayState.STOP)
                .triggerableAnim("feed", RawAnimation.begin().then("xp", Animation.LoopType.PLAY_ONCE)));
    }
    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.factory;
    }
}
