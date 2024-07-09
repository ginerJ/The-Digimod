package net.modderg.thedigimod.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.*;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.registries.RegistryObject;
import net.modderg.thedigimod.config.ModCommonConfigs;
import net.modderg.thedigimod.entity.goals.*;
import net.modderg.thedigimod.gui.inventory.DigimonMenu;
import net.modderg.thedigimod.gui.inventory.DigimonInventory;
import net.modderg.thedigimod.entity.managers.DigimonJsonDataManager;
import net.modderg.thedigimod.entity.managers.EvolutionCondition;
import net.modderg.thedigimod.entity.managers.MoodManager;
import net.modderg.thedigimod.entity.managers.ParticleManager;
import net.modderg.thedigimod.goods.AbstractTrainingGood;
import net.modderg.thedigimod.gui.StatsGui;
import net.modderg.thedigimod.item.*;
import net.modderg.thedigimod.item.custom.DigiFoodBlockItem;
import net.modderg.thedigimod.item.custom.DigiFoodItem;
import net.modderg.thedigimod.packet.PacketInit;
import net.modderg.thedigimod.packet.SToCSGainXpPacket;
import net.modderg.thedigimod.particles.DigitalParticles;
import net.modderg.thedigimod.projectiles.ProjectileDefault;
import net.modderg.thedigimod.projectiles.InitProjectiles;
import net.modderg.thedigimod.sound.DigiSounds;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

import static net.minecraft.world.entity.ai.attributes.Attributes.*;
import static net.modderg.thedigimod.config.ModCommonConfigs.*;

public class CustomDigimon extends TamableAnimal implements GeoEntity, ItemSteerable, RangedAttackMob, MenuProvider {

    public MoodManager moodManager = new MoodManager(this);

    public ParticleManager particleManager = new ParticleManager();

    public DigimonJsonDataManager jsonManager = new DigimonJsonDataManager();

    protected static final EntityDataAccessor<String> RANK = SynchedEntityData.defineId(CustomDigimon.class, EntityDataSerializers.STRING);

    public String getRank() {
        return this.getEntityData().get(RANK);
    }

    public CustomDigimon setRank(String rank) {
        this.getEntityData().set(RANK, rank);
        return this;
    }

    public String profession;

    public int maxStatGain() {
        return getRank().equals("zero") ? 6 : (getRank().equals("super") ? 10 : 4);
    }

    public int minStatGain() {
        return getRank().equals("zero") ? 2 : (getRank().equals("super") ? 3 : 1);
    }

    public String getLowerCaseSpecies() {
        return this.getType().getDescriptionId().replace("entity.thedigimod.", "");
    }

    protected double riderOffSet = 0;
    protected boolean isMountDigimon = false;
    public boolean isMountDigimon() {return isMountDigimon;}

    protected CustomDigimon setMountDigimon(double riderOff) {
        isMountDigimon = true;
        this.riderOffSet = riderOff;
        return this;
    }

    protected RegistryObject<?>[] reincarnateTo = new RegistryObject[]{BabyDigimonItems.BOTAMON};

    public RegistryObject<?>[] getReincarnateTo() {
        return reincarnateTo;
    }

    public CustomDigimon setBabyAndXpDrop(RegistryObject<?>... babies) {
        reincarnateTo = babies;
        return this;
    }

    protected int xpDrop = 0;

    public int getXpDrop() {
        return xpDrop;
    }

    public CustomDigimon setXpDrop(int xp) {
        xpDrop = xp;
        return this;
    }

    protected static final int MAXLEVEL = 65;
    protected static final int MAXADULT = 250;
    protected static final int MAXULTIMATE = 500;
    public static final int MAXMEGASTAT = 999;

    protected int evoStage;

    public int getEvoStage() {
        return evoStage;
    }

    public CustomDigimon setEvoStage(int evoStage) {
        this.evoStage = evoStage;
        return this;
    }

    public Boolean isBaby2() {
        return getEvoStage() == 0;
    }

    public Boolean isRookie() {
        return getEvoStage() == 1;
    }

    public Boolean isChampion() {
        return getEvoStage() == 2;
    }

    public Boolean isUltimate() {
        return getEvoStage() == 3;
    }

    public int getMaxStat() {
        return this.isBaby2() ? 25 : (this.isRookie() ? 100 : (this.isChampion() ? MAXADULT : (this.isUltimate() ? MAXULTIMATE : MAXMEGASTAT)));
    }

    public int getMaxLevel() {
        return this.isBaby2() ? 5 : (this.isRookie() ? 15 : (this.isChampion() ? 35 : (this.isUltimate() ? 70 : 100)));
    }

    public int getMinLevel() {
        return this.isBaby2() ? 1 : (this.isRookie() ? 6 : (this.isChampion() ? 16 : (this.isUltimate() ? 36 : 71)));
    }

    public Boolean evolutionLevelAchieved() {
        return
                (isBaby2() && this.getCurrentLevel() >= ROOKIE_EVOLUTION_LEVEL.get()) ||
                        (isRookie() && this.getCurrentLevel() >= CHAMPION_EVOLUTION_LEVEL.get()) ||
                        (isChampion() && this.getCurrentLevel() >= ULTIMATE_EVOLUTION_LEVEL.get());
    }

    public Boolean isEvolving() {
        return evoCount > 0;
    }

    public EvolutionCondition[] evolutionConditions = {null, null, null, null, null, null};

    protected Boolean canEvoToPath(int i) {
        return evolutionConditions[i] != null && evolutionConditions[i].checkConditions();
    }

    public String digitronEvo;

    public CustomDigimon setDigitronEvo(String evo) {
        digitronEvo = evo;
        return this;
    }

    protected static final EntityDataAccessor<String> PREEVO = SynchedEntityData.defineId(CustomDigimon.class, EntityDataSerializers.STRING);

    public void setPreEvo(String i) {
        this.getEntityData().set(PREEVO, i);
    }

    public String getPreEvo() {
        return this.getEntityData().get(PREEVO);
    }

    protected static final EntityDataAccessor<String> NICKNAME = SynchedEntityData.defineId(CustomDigimon.class, EntityDataSerializers.STRING);

    public void setNickName(String i) {
        this.getEntityData().set(NICKNAME, i);
    }

    public String getNickName() {
        return this.getEntityData().get(NICKNAME);
    }

    public CustomDigimon setAnimations(String ia, String sa, String wa, String fa, String ata, String sha) {
        if (ia != null) {
            idleAnim = ia;
        }
        if (sa != null) {
            sitAnim = sa;
        }
        if (wa != null) {
            walkAnim = wa;
        }
        if (fa != null) {
            flyAnim = fa;
        }
        if (ata != null) {
            attackAnim = ata;
        }
        if (sha != null) {
            shootAnim = sha;
        }
        return this;
    }

    protected String idleAnim = "idle";
    protected String sitAnim = "sit";
    protected String walkAnim = "walk";
    protected String flyAnim = "fly";
    protected String attackAnim = "attack";
    protected String shootAnim = "shoot";

    protected static final EntityDataAccessor<String> SPMOVENAME = SynchedEntityData.defineId(CustomDigimon.class, EntityDataSerializers.STRING);
    String defaultSpMove;

    public void setSpMoveName(String i) {
        this.getEntityData().set(SPMOVENAME, i);
    }

    public String getSpMoveName() {
        if(this.getEntityData().get(SPMOVENAME).equals("unnamed")){
            setSpMoveName(defaultSpMove);
        }
        return this.getEntityData().get(SPMOVENAME);
    }

    public CustomDigimon setDefaultSpMove(String move) {
        defaultSpMove = move;
        return this;
    }

    protected static final EntityDataAccessor<Integer> MOVEMENTID = SynchedEntityData.defineId(CustomDigimon.class, EntityDataSerializers.INT);

    public void setMovementID(int i) {
        this.getEntityData().set(MOVEMENTID, i);
        this.setOrderedToSit(i == 0);
    }

    public int getMovementID() {
        return this.getEntityData().get(MOVEMENTID);
    }

    public void changeMovementID() {
        int i = this.getMovementID();

        if (i == 0) {
            messageState("following");
            setMovementID(1);
        } else if (i == 1) {
            messageState("wandering");
            setMovementID(1);
            setMovementID(-1);
        } else if (i == -1) {
            messageState("working");
            setMovementID(1);
            setMovementID(-2);
        } else if (i == -2) {
            this.spawnAtLocation(this.getPickedItem());
            this.setPickedItem(ItemStack.EMPTY);
            messageState("sitting");
            this.setTarget(null);
            this.navigation.stop();
            this.playStepSound(this.blockPosition(), this.getBlockStateOn());
            setMovementID(0);
        }
    }

    public void messageState(String txt) {
        if (this.level().isClientSide) {
            Minecraft.getInstance().gui.setOverlayMessage(Component.literal(txt), false);
        }
    }

    //dragon-0 beast-1 insectplant-2 aquan-3 wind-4 machine-5 earth-6 nightmare-7 holy-8
    protected static final EntityDataAccessor<String> SPECIFICXPS = SynchedEntityData.defineId(CustomDigimon.class, EntityDataSerializers.STRING);

    public void saveGainedXp(int s) {

        String[] ss = this.getGainedXps().split("-");

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < ss.length; i++) {
            if (i == s) {
                sb.append(Integer.parseInt(ss[i]) + 1 + "-");
            } else {
                sb.append(ss[i] + "-");
            }
        }

        this.getEntityData().set(SPECIFICXPS, sb.toString());
    }

    public void setGainedXps(String s) {
        this.getEntityData().set(SPECIFICXPS, s);
    }

    public String getGainedXps() {
        return this.getEntityData().get(SPECIFICXPS);
    }

    public int getSpecificGainedXps(int i) {
        String[] ss = this.getGainedXps().split("-");
        return Integer.parseInt(ss[i]);
    }

    protected static final EntityDataAccessor<Integer> LIFES = SynchedEntityData.defineId(CustomDigimon.class, EntityDataSerializers.INT);

    public void setLifes(int i) {
        this.getEntityData().set(LIFES, Math.min(Math.min(3, ModCommonConfigs.MAX_DIGIMON_LIVES.get()),i));
    }

    public int getLifes() {
        return this.getEntityData().get(LIFES);
    }

    public void addLife() {
        this.getEntityData().set(LIFES, Math.min(Math.min(3, ModCommonConfigs.MAX_DIGIMON_LIVES.get()), this.getLifes() + 1));
        this.setHealth(MAXMEGASTAT);
    }

    public void restLife() {
        this.setCareMistakesStat(this.getCareMistakesStat() + 1);
        this.getEntityData().set(LIFES, Math.max(0, this.getLifes() - 1));
        this.setHealth(MAXMEGASTAT);
    }

    protected static final EntityDataAccessor<Integer> ATTACK_STAT = SynchedEntityData.defineId(CustomDigimon.class, EntityDataSerializers.INT),
            DEFENCE_STAT = SynchedEntityData.defineId(CustomDigimon.class, EntityDataSerializers.INT),
            SPATTACK_STAT = SynchedEntityData.defineId(CustomDigimon.class, EntityDataSerializers.INT),
            SPDEFENCE_STAT = SynchedEntityData.defineId(CustomDigimon.class, EntityDataSerializers.INT),
            BATTLES_STAT = SynchedEntityData.defineId(CustomDigimon.class, EntityDataSerializers.INT),
            CARE_MISTAKES_STAT = SynchedEntityData.defineId(CustomDigimon.class, EntityDataSerializers.INT);

    public void setAttackStat(int i) {
        this.getEntityData().set(ATTACK_STAT, Math.min(i, getMaxStat()));
    }

    public void setDefenceStat(int i) {
        this.getEntityData().set(DEFENCE_STAT, Math.min(i, getMaxStat()));
    }

    public void setSpAttackStat(int i) {
        this.getEntityData().set(SPATTACK_STAT, Math.min(i, getMaxStat()));
    }

    public void setSpDefenceStat(int i) {
        this.getEntityData().set(SPDEFENCE_STAT, Math.min(i, getMaxStat()));
    }

    public void setBattlesStat(int i) {
        this.getEntityData().set(BATTLES_STAT, i);
    }

    public void setCareMistakesStat(int i) {
        this.getEntityData().set(CARE_MISTAKES_STAT, i);
    }

    public void setHealthStat(int i) {
        Objects.requireNonNull(getAttribute(Attributes.MAX_HEALTH)).setBaseValue(Math.min(i, getMaxStat()));
    }

    public int getAttackStat() {
        return this.getEntityData().get(ATTACK_STAT);
    }

    public int getDefenceStat() {
        return this.getEntityData().get(DEFENCE_STAT);
    }

    public int getSpAttackStat() {
        return this.getEntityData().get(SPATTACK_STAT);
    }

    public int getSpDefenceStat() {
        return this.getEntityData().get(SPDEFENCE_STAT);
    }

    public int getBattlesStat() {
        return this.getEntityData().get(BATTLES_STAT);
    }

    public int getCareMistakesStat() {
        return this.getEntityData().get(CARE_MISTAKES_STAT);
    }

    public int getHealthStat() {
        return (int) Objects.requireNonNull(getAttribute(Attributes.MAX_HEALTH)).getValue();
    }

    protected static final EntityDataAccessor<Integer> EXPERIENCETOTAL = SynchedEntityData.defineId(CustomDigimon.class, EntityDataSerializers.INT);

    public void addExperienceTotal() {
        this.getEntityData().set(EXPERIENCETOTAL, getExperienceTotal() + 1);
    }

    public void setExperienceTotal(int i) {
        this.getEntityData().set(EXPERIENCETOTAL, i);
    }

    public int getExperienceTotal() {
        return this.getEntityData().get(EXPERIENCETOTAL);
    }

    protected static final EntityDataAccessor<Integer> LEVELXP = SynchedEntityData.defineId(CustomDigimon.class, EntityDataSerializers.INT);

    public void addLevelXp() {
        this.getEntityData().set(LEVELXP, getLevelXp() + 1);
    }

    public void setLevelXp(int i) {
        this.getEntityData().set(LEVELXP, i);
    }

    public int getLevelXp() {
        return this.getEntityData().get(LEVELXP);
    }

    protected static final EntityDataAccessor<Integer> CURRENTLEVEL = SynchedEntityData.defineId(CustomDigimon.class, EntityDataSerializers.INT);

    public void levelUp() {
        this.playSound(DigiSounds.LEVEL_UP_SOUND.get(), 0.05F, 1.0F);
        this.setCurrentLevel(Math.min(getCurrentLevel() + 1, MAXLEVEL));
        if (this.evolutionLevelAchieved() && getNextEvolution() != null) {
            this.evoCount = 200;
            this.setPos(this.blockPosition().getCenter());
            this.playSound(DigiSounds.EVOLUTION_SOUND.get(), 0.25F, 1.0F);
        }
    }

    public void setCurrentLevel(int i) {
        this.getEntityData().set(CURRENTLEVEL, i);
        this.setCustomName(Component.literal(this.getNickName()));
    }

    public int getCurrentLevel() {
        return this.getEntityData().get(CURRENTLEVEL);
    }

    protected int evoCount = 0;

    public int ticksToShootAnim = this.random.nextInt(150, 250);

    DigitalRangedAttackGoal<CustomDigimon> rangedGoal = new DigitalRangedAttackGoal<>(this, 1.3D, 65, 10f);
    DigitalMeleeAttackGoal meleeGoal = new DigitalMeleeAttackGoal(this, 1.0D, true);

    protected CustomDigimon(EntityType<? extends TamableAnimal> p_21803_, Level p_21804_) {
        super(p_21803_, p_21804_);
        resetAttackGoals();
        this.setCustomName(Component.empty());
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 5.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                .add(Attributes.ATTACK_DAMAGE, 0.5D)
                .add(Attributes.FLYING_SPEED, 0.1D)
                .add(ATTACK_KNOCKBACK, 4.0D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new SitWhenOrderedToGoal(this));
        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(3, new DigitalHurtByTargetGoal(this));
        this.goalSelector.addGoal(4, new DigitalFollowOwnerGoal(this, 1.1D, 10.0F, 2.0F, true));
        this.goalSelector.addGoal(5, new DigimonFloatGoal(this));
        this.goalSelector.addGoal(6, new WaterAvoidingRandomFlyingGoal(this, 1.0D));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0F));
    }

    @Override
    public void setCustomName(@Nullable Component component) {
        if (component != null && !component.getString().isEmpty()) {
            this.setNickName(component.getString());
        } else {
            component = Component.literal("Digimon");
        }
        super.setCustomName(Component.literal(component.getString() + " (" + this.getCurrentLevel() + "Lv)"));
    }

    public void evolveDigimon() {
        CustomDigimon evoD = getNextEvolution();

        evoD.copyOtherDigi(this);

        String[] prevos = this.getPreEvo().split("-");
        prevos[this.getEvoStage()] = this.getLowerCaseSpecies();
        evoD.setPreEvo(String.join("-", prevos));

        this.level().addFreshEntity(evoD);
        this.remove(RemovalReason.UNLOADED_TO_CHUNK);
    }

    public CustomDigimon getNextEvolution() {
        EvolutionCondition condition = IntStream.range(0, (int) Arrays.stream(evolutionConditions).filter(Objects::nonNull).count())
                .filter(this::canEvoToPath)
                .mapToObj(i -> evolutionConditions[i])
                .reduce((first, second) -> second)
                .orElse(null);

        return ((CustomDigimon) InitDigimons.digimonMap.get(condition.getEvolution()).get().create(this.level())).setRank(condition.getRank());
    }

    public void deEvolveDigimon() {
        if (this.getEvoStage() - 1 >= 0 && !this.getPreEvo().split("-")[this.getEvoStage() - 1].equals("a")) {

            CustomDigimon prevo = (CustomDigimon) InitDigimons.digimonMap.get(this.getPreEvo().split("-")[this.getEvoStage() - 1]).get().create(this.level());

            assert prevo != null;
            prevo.copyOtherDigi(this);

            prevo.setAttackStat(prevo.getMaxStat() / 4);
            prevo.setDefenceStat(prevo.getMaxStat() / 4);
            prevo.setSpAttackStat(prevo.getMaxStat() / 4);
            prevo.setSpDefenceStat(prevo.getMaxStat() / 4);
            prevo.setHealthStat(prevo.getMaxStat() / 4);

            this.level().addFreshEntity(prevo);
            this.remove(RemovalReason.UNLOADED_TO_CHUNK);
        }
    }

    public void copyOtherDigiAndData(CustomDigimon d) {
        this.setGainedXps(d.getGainedXps());
        this.copyOtherDigi(d);
    }

    public void copyOtherDigi(CustomDigimon d) {
        if (d.getOwner() != null) {
            this.tame((Player) d.getOwner());
        }

        this.setMovementID(1);
        this.setNickName(d.getNickName());
        this.moodManager.setMoodPoints(d.moodManager.getMoodPoints());
        this.setPos(d.position());
        this.setExperienceTotal(d.getExperienceTotal());
        this.setLevelXp(d.getLevelXp());
        this.setCurrentLevel(d.getCurrentLevel());
        this.setPreEvo(d.getPreEvo());

        int evoAdd = d.isBaby2() ? 5 : d.isRookie() ? 15 :
                d.isChampion() ? 35 : 50;

        this.setAttackStat(d.getAttackStat() + evoAdd);
        this.setDefenceStat(d.getDefenceStat() + evoAdd);
        this.setSpAttackStat(d.getSpAttackStat() + evoAdd);
        this.setSpDefenceStat(d.getSpDefenceStat() + evoAdd);
        this.setHealthStat(d.getHealthStat() + evoAdd);
        this.inventory.inventoryReplace(d.inventory.getStackHandler(), this.inventory.getStackHandler());

        this.setHealth(d.getHealth());
        this.setLifes(d.getLifes());
    }

    public void gainSpecificXp(int id) {
        if(!this.isEvolving()){
            this.playSound(DigiSounds.XP_GAIN_SOUND.get(), 0.01F, 1.0F);
            addExperienceTotal();
            addLevelXp();
            if (getLevelXp() >= getNeededXp()) {
                setLevelXp(0);
                levelUp();
            }
            saveGainedXp(id);
            if(level().isClientSide())
                particleManager.eatData(
                        new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(AdminItems.adminmMap.get(StatsGui.getXpItem(id)).get())), this);
            else
                PacketInit.sendToAll(new SToCSGainXpPacket(this.getId(), id));
        }
    }

    public int getNeededXp() {
        int i = getCurrentLevel();
        float calc = (((i / 24f) * (i / 24f) * (i / 24f) * (150f - i)) / 150f + i / 10f) * (i < 20 ? 10 : 8);
        return (int) Math.max(1, calc);
    }

    public int calculateDamage(int attack, int defense) {
        return Math.max(1, attack / Math.max(1, defense/10));
    }

    public void eatItem(ItemStack itemStack, int moodAdd, int heal) {

        this.playSound(itemStack.getItem().getEatingSound(), 0.15F, 1.0F);
        this.moodManager.addMoodPoints(moodAdd);

        if(canHeal())
            this.heal(heal);

        eatItemAnim(itemStack);
    }

    public void eatItemAnim(ItemStack itemStack){
        particleManager.spawnItemParticles(itemStack, 16, this);
        itemStack.shrink(1);
        this.playSound(itemStack.getItem().getEatingSound(), 0.20F, 1.0F);

        if (this.level() instanceof ServerLevel)
            triggerAnim("movement", "feed");
    }

    boolean wasMelee = random.nextBoolean();
    public void resetAttackGoals() {

        if (!this.level().isClientSide) {

            this.goalSelector.removeGoal(this.meleeGoal);
            this.goalSelector.removeGoal(this.rangedGoal);

            boolean isMeleeDominant = this.getAttackStat() > this.getSpAttackStat();

            if (wasMelee) {
                this.ticksToShootAnim = this.random.nextInt(50, isMeleeDominant ? 250 : 500);
                this.goalSelector.addGoal(1, this.rangedGoal);
            } else {
                this.ticksToShootAnim = this.random.nextInt(50, isMeleeDominant ? 500 : 250);
                this.goalSelector.addGoal(1, this.meleeGoal);
            }

            wasMelee = !wasMelee;
        }
    }

    @Override
    public boolean canFallInLove() {
        return false;
    }

    @Override
    public boolean isFood(@NotNull ItemStack item) {
        if(item.getItem() instanceof DigiFoodItem food){
            eatItem(item, food.getCalories(), food.getHeal());
            return true;
        } else if(item.getItem() instanceof DigiFoodBlockItem food){
            eatItem(item, food.getCalories(), food.getHeal());
            return true;
        }
        return false;
    }

    public boolean canHeal() {return !this.isAggressive() || this.getTarget() instanceof AbstractTrainingGood;}

    private final ServerBossEvent bossEvent = new ServerBossEvent(this.getDisplayName(), BossEvent.BossBarColor.BLUE, BossEvent.BossBarOverlay.PROGRESS);

    protected static final EntityDataAccessor<Boolean> BOSS = SynchedEntityData.defineId(CustomDigimon.class, EntityDataSerializers.BOOLEAN);

    public boolean isBoss(){
        return entityData.get(BOSS);
    }

    public void setBoss(boolean b){
        entityData.set(BOSS, b);
        if(b)
            this.setBoundingBox(this.getBoundingBox().inflate(1.5f));
    }

    @Override
    public SpawnGroupData finalizeSpawn(@NotNull ServerLevelAccessor p_146746_, @NotNull DifficultyInstance p_146747_, @NotNull MobSpawnType p_146748_, @Nullable SpawnGroupData p_146749_, @Nullable CompoundTag p_146750_) {
        if (!this.isTame()) {

            this.setHealthStat(Math.min(getMaxStat(),
                    random.nextInt(this.isBaby2() ? 1 : (this.isRookie() ? 25 : (this.isChampion() ? 100 : (this.isUltimate() ? MAXADULT : MAXULTIMATE))), getMaxStat())));

            this.setHealth((float) this.getHealthStat());
            this.setCurrentLevel((int) Math.max(getMinLevel(), getMaxLevel() * getHealth() / getMaxStat()));
            this.setAttackStat((int) getHealth());
            this.setDefenceStat((int) getHealth());
            this.setSpAttackStat((int) getHealth());
            this.setSpDefenceStat((int) getHealth());

            if(chanceOverHundred(1) && chanceOverHundred(50)){
                this.setBoss(true);
                this.setCurrentLevel(this.getMaxLevel());
                this.setAttackStat(MAXMEGASTAT);
                this.setDefenceStat(MAXMEGASTAT);
                this.setSpDefenceStat(MAXMEGASTAT);
                this.setSpAttackStat(MAXMEGASTAT);
                this.setHealthStat(MAXMEGASTAT);

                this.setHealth(this.getMaxHealth());
                this.setSpMoveName(InitProjectiles.projectileMap.keySet().toArray()[random.nextInt(InitProjectiles.projectileMap.size())].toString());
            }

            CustomDigimon rider = null;

            if(chanceOverHundred(5))
                if(this.isMountDigimon)
                    rider = GroupsDigimon.riderDigimon.get(random.nextInt(GroupsDigimon.riderDigimon.size())).get()
                            .create(this.level());
                else if(this.isBaby2())
                    rider = GroupsDigimon.baby2Digimon.get(random.nextInt(GroupsDigimon.baby2Digimon.size())).get()
                            .create(this.level());

            if(rider != null){
                rider.setPos(this.position());
                rider.startRiding(this);
            }

        }
        return super.finalizeSpawn(p_146746_, p_146747_, p_146748_, p_146749_, p_146750_);
    }

    public static boolean checkDigimonSpawnRules() {
        return ModCommonConfigs.CAN_SPAWN_DIGIMON.get();
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        this.bossEvent.setProgress(this.getHealth() / this.getMaxHealth());
    }

    @Override
    public void startSeenByPlayer(@NotNull ServerPlayer player) {
        super.startSeenByPlayer(player);
        if(isBoss())
            this.bossEvent.addPlayer(player);
    }

    @Override
    public void stopSeenByPlayer(@NotNull ServerPlayer p_31488_) {
        super.stopSeenByPlayer(p_31488_);
        if(isBoss())
            this.bossEvent.removePlayer(p_31488_);
    }

    public void setPickedItem(ItemStack item) {
        CompoundTag entityTag = this.getPersistentData();

        CompoundTag itemStackTag = new CompoundTag();
        item.save(itemStackTag);

        entityTag.put("PickedItemStack", itemStackTag);
    }

    public ItemStack getPickedItem() {
        ItemStack retrievedItemStack = ItemStack.EMPTY;

        CompoundTag entityTag = this.getPersistentData();

        if (entityTag.contains("PickedItemStack", Tag.TAG_COMPOUND)) {
            CompoundTag itemStackTag = entityTag.getCompound("PickedItemStack");

            retrievedItemStack = ItemStack.of(itemStackTag);
        }
        return retrievedItemStack;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(NICKNAME, "");
        this.entityData.define(MOVEMENTID, 1);
        this.entityData.define(CURRENTLEVEL, 1);
        this.entityData.define(LEVELXP, 0);
        this.entityData.define(EXPERIENCETOTAL, 0);
        this.entityData.define(SPECIFICXPS, "0-0-0-0-0-0-0-0-0");
        this.entityData.define(MoodManager.MOODPOINTS, 249);
        this.entityData.define(ATTACK_STAT, 1);
        this.entityData.define(DEFENCE_STAT, 1);
        this.entityData.define(SPATTACK_STAT, 1);
        this.entityData.define(SPDEFENCE_STAT, 1);
        this.entityData.define(BATTLES_STAT, 0);
        this.entityData.define(CARE_MISTAKES_STAT, 0);
        this.entityData.define(LIFES, 1);
        this.entityData.define(PREEVO, "a-a-a-a-a");
        this.entityData.define(SPMOVENAME, "unnamed");
        this.entityData.define(BOSS, false);
        this.entityData.define(RANK, "zero");
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag compound) {
        super.readAdditionalSaveData(compound);

        if (compound.contains("NAME"))
            this.setNickName(compound.getString("NAME"));

        if (compound.contains("MOVEMENTID"))
            this.setMovementID(compound.getInt("MOVEMENTID"));

        if (compound.contains("CURRENTLEVEL"))
            this.setCurrentLevel(compound.getInt("CURRENTLEVEL"));

        if (compound.contains("LEVELXP"))
            this.setLevelXp(compound.getInt("LEVELXP"));

        if (compound.contains("EXPERIENCETOTAL"))
            this.setExperienceTotal(compound.getInt("EXPERIENCETOTAL"));

        if (compound.contains("SPECIFICXPS"))
            this.setGainedXps(compound.getString("SPECIFICXPS"));

        if (compound.contains("MOODPOINTS"))
            this.moodManager.setMoodPoints(compound.getInt("MOODPOINTS"));

        if (compound.contains("ATTACK_STAT"))
            this.setAttackStat(compound.getInt("ATTACK_STAT"));

        if (compound.contains("DEFENCE_STAT"))
            this.setDefenceStat(compound.getInt("DEFENCE_STAT"));

        if (compound.contains("SPATTACK_STAT"))
            this.setSpAttackStat(compound.getInt("SPATTACK_STAT"));

        if (compound.contains("SPDEFENCE_STAT"))
            this.setSpDefenceStat(compound.getInt("SPDEFENCE_STAT"));

        if (compound.contains("BATTLES_STAT"))
            this.setBattlesStat(compound.getInt("BATTLES_STAT"));

        if (compound.contains("CARE_MISTAKES_STAT"))
            this.setCareMistakesStat(compound.getInt("CARE_MISTAKES_STAT"));

        if (compound.contains("LIFES"))
            this.setLifes(compound.getInt("LIFES"));

        if (compound.contains("PREEVO"))
            this.setPreEvo(compound.getString("PREEVO"));

        if (compound.contains("SPMOVENAME"))
            this.setSpMoveName(compound.getString("SPMOVENAME"));

        if (compound.contains("BOSS"))
            this.setBoss(compound.getBoolean("BOSS"));

        if (compound.contains("RANK"))
            this.setRank(compound.getString("RANK"));

        if (compound.contains("RANK"))
            this.setRank(compound.getString("RANK"));

        inventory.deserializeNBT(compound);
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putString("NAME", this.getNickName());
        compound.putInt("MOVEMENTID", this.getMovementID());
        compound.putInt("CURRENTLEVEL", this.getCurrentLevel());
        compound.putInt("LEVELXP", this.getLevelXp());
        compound.putInt("EXPERIENCETOTAL", this.getExperienceTotal());
        compound.putString("SPECIFICXPS", this.getGainedXps());
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
        compound.putString("SPMOVENAME", this.getSpMoveName());
        compound.putBoolean("BOSS", this.isBoss());
        compound.putString("RANK", this.getRank());
        compound.put("Inventory", inventory.serializeNBT());
    }

    private final DigimonInventory inventory = new DigimonInventory(this, 5);

    public void initInventory(){
        inventory.genSlots(0);
    }

    public DigimonInventory getInventory() {
        return inventory;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int p_39954_, Inventory p_39955_, Player p_39956_) {
        return new DigimonMenu(p_39954_, p_39955_, this);
    }

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction facing) {
        if (capability == ForgeCapabilities.ITEM_HANDLER)
            return inventory.getInventoryCapability().cast();

        return super.getCapability(capability, facing);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        inventory.getInventoryCapability().invalidate();
    }

    public void openMenu(Player player){
        if(player instanceof ServerPlayer sPlayer)
            NetworkHooks.openScreen(sPlayer, new SimpleMenuProvider(
                    (id, playerInventory, playerEntity) -> new DigimonMenu(id, playerInventory, this),
                    Component.literal("Digimon Inventory")
            ), buffer -> buffer.writeInt(this.getId()));

    }

    @Override
    public @NotNull InteractionResult mobInteract(Player player, @NotNull InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        ItemStack otherItemStack = player.getItemInHand(hand.equals(InteractionHand.MAIN_HAND) ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND);
        Item item = itemStack.getItem();

        if (item.isEdible() && !isFood(itemStack)) {
            eatItem(itemStack, 1, 1);
            return InteractionResult.CONSUME;
        }

        if (this.isTame() && Objects.equals(this.getOwnerUUID(), player.getUUID())) {

            if (player.isShiftKeyDown()) {
                this.changeMovementID();
                return InteractionResult.SUCCESS;
            }

            if ((this.getMovementID() != 0) && itemStack.isEmpty() && otherItemStack.isEmpty()) {

                if (this.isMountDigimon && (RIDE_ALLOWED.get() && !(this instanceof CustomFlyingDigimon)) || (FLIGHT_ALLOWED.get() && this instanceof CustomFlyingDigimon)) {

                    player.startRiding(this);
                    if (this.getMovementID() != 2) this.setMovementID(1);

                } else if (this.isBaby2()) {
                    getInPlayerHead(player);
                }
                return InteractionResult.SUCCESS;
            }
        }

        return super.mobInteract(player, hand);
    }

    public void getInPlayerHead(Entity player){
        if (player.getPassengers().isEmpty()) this.startRiding(player);
        else getInPlayerHead(player.getPassengers().get(0));
    }

    public void getOffPlayerHead(){
        if(this.getVehicle() != null)
            this.getVehicle().ejectPassengers();
        if(!this.getPassengers().isEmpty() && this.getPassengers().get(0) instanceof CustomDigimon cd)
            cd.getOffPlayerHead();
    }

    int attack = this.getAttackStat(), defence = this.getDefenceStat(), spattack = this.getSpAttackStat(), spdefence = this.getSpDefenceStat(),
            battles = this.getBattlesStat(), health = this.getHealthStat(), mistakes = this.getCareMistakesStat(), lifes = this.getLifes();

    public void checkChangeStats() {
        if (attack != this.getAttackStat()) {
            particleManager.spawnStatUpParticles(DigitalParticles.ATTACK_UP, 1, this);
            attack = this.getAttackStat();
        }
        if (defence != this.getDefenceStat()) {
            particleManager.spawnStatUpParticles(DigitalParticles.DEFENCE_UP, 1, this);
            defence = this.getDefenceStat();
        }
        if (spattack != this.getSpAttackStat()) {
            particleManager.spawnStatUpParticles(DigitalParticles.SPATTACK_UP, 1, this);
            spattack = this.getSpAttackStat();
        }
        if (spdefence != this.getSpDefenceStat()) {
            particleManager.spawnStatUpParticles(DigitalParticles.SPDEFENCE_UP, 1, this);
            spdefence = this.getSpDefenceStat();
        }
        if (battles != this.getBattlesStat()) {
            particleManager.spawnStatUpParticles(DigitalParticles.BATTLES_UP, 1, this);
            battles = this.getBattlesStat();
        }
        if (health != this.getHealthStat()) {
            particleManager.spawnStatUpParticles(DigitalParticles.HEALTH_UP, 1, this);
            health = this.getHealthStat();
        }
        if (mistakes != this.getCareMistakesStat()) {
            particleManager.spawnBubbleParticle(DigitalParticles.MISTAKE_BUBBLE, this);
            mistakes = this.getCareMistakesStat();
        }
        if (lifes != this.getLifes()) {
            particleManager.spawnStatUpParticles(DigitalParticles.LIFE_PARTICLE, 7, this);
            lifes = this.getLifes();
        }
    }

    @Override
    public boolean hasCustomName() {
        return true;
    }


    @Override
    public void tick() {
        checkChangeStats();

        if(this.isVehicle())
            moving = updateMovingState();

        if(this.getMovementID() == -2 && !this.level().getGameRules().getRule(GameRules.RULE_MOBGRIEFING).get())
            this.changeMovementID();

        if(this.isBaby2() && this.getVehicle() instanceof Player player && player.isShiftKeyDown())
            getOffPlayerHead();

        if (evoCount == 1)
            this.evolveDigimon();

        if (evoCount-- > 0) {
            if(evoCount < 5)
                particleManager.finishEvoParticles(this);
            particleManager.spawnEvoParticles(this);
        }

        if (this.moodManager.getMoodPoints() < 100 && this.isTame() && random.nextInt(0, 150) == 1) {
            particleManager.spawnBubbleParticle(DigitalParticles.MEAT_BUBBLE, this);
        }

        if(this.isAggressive() && !(this.getTarget() instanceof AbstractTrainingGood) && ticksToShootAnim-- < 0) resetAttackGoals();

        if (this.isTame() && this.random.nextInt(200) == 2)
            moodManager.spawnMoodParticle();

        super.tick();
    }

    public boolean canBeControlledByRider() {
        return this.getFirstPassenger() != null;
    }

    @Nullable
    @Override
    public LivingEntity getControllingPassenger() {
        if (this.getFirstPassenger() instanceof LivingEntity lv)
            return lv;
        return null;
    }

    @Override
    public void travel(@NotNull Vec3 pos) {
        if (!this.isEvolving()) {
            if (this.isAlive()) {
                if (this.getFirstPassenger() instanceof Player passenger) {

                    this.yRotO = getYRot();
                    this.xRotO = getXRot();

                    setYRot(passenger.getYRot());
                    setXRot(passenger.getXRot() * 0.5f);

                    this.yBodyRot = this.getYRot();
                    this.yHeadRot = this.yBodyRot;
                    float x = passenger.xxa * 0.25F;
                    float z = passenger.zza / 2;

                    if (z <= 0) z *= 0.25f;

                    this.setSpeed(travelRideSpeed());

                    super.travel(new Vec3(x, pos.y, z));

                } else super.travel(pos);
            }
        }
    }

    protected float travelRideSpeed(){
        return (float) Objects.requireNonNull(this.getAttribute(MOVEMENT_SPEED)).getValue() * (this.isChampion() ? 1.7f: this.isUltimate() ? 2f : 2.2f);
    }

    @Override
    public boolean boost() {
        return false;
    }

    @Override
    public boolean canBeLeashed(@NotNull Player player) {
        return super.canBeLeashed(player) && this.isOwnedBy(player);
    }

    public boolean chanceOverHundred(int chance) {
        return random.nextInt(100) <= chance;
    }

    public void dropItem(Item item, int chance) {
        if (chanceOverHundred(chance))
            this.level().addFreshEntity(new ItemEntity(level(), this.getX(), this.getY(), this.getZ(), new ItemStack(item)));
    }

    @Override
    public void die(@NotNull DamageSource source) {
        if(!level().isClientSide()){
            if (this.isTame()) {
                dropItem((Item) getReincarnateTo()[random.nextInt(getReincarnateTo().length)].get(), 101);
            } else {
                if (source.getEntity() instanceof CustomDigimon killer) {

                    int bossMultiplier = (this.isBoss() && killer.getEvoStage() == this.getEvoStage()) ? 3 : 1;

                    dropDigiLoot(killer, bossMultiplier, bossMultiplier);
                }
            }
        }
        super.die(source);
    }

    public void dropDigiLoot(CustomDigimon killer, int repeat, int multiplier){

        for (int i = 0; i < repeat; i++) {

            if (killer.getEvoStage() <= this.getEvoStage()) {
                killer.setBattlesStat(killer.getBattlesStat() + 1);
            }

            dropItem((Item) getReincarnateTo()[random.nextInt(getReincarnateTo().length)].get(), (CHANCE_DROP_BABY.get() * multiplier));

            dropItem(InitItems.itemMap.get("chip_" + this.getSpMoveName()).get(), (CHANCE_DROP_MOVE_CHIP.get() * multiplier));

            dropItem(InitItems.DIGI_MEAT.get(), (CHANCE_DROP_FOOD.get() * multiplier));
            dropItem(InitItems.GUILMON_BREAD.get(), (CHANCE_DROP_FOOD.get() * multiplier));

            dropItem(InitItems.DIGIMON_CARD.get(), (CHANCE_DROP_CARD.get() * multiplier));

            if (!this.isBaby2()) {
                dropItem(InitItems.ATTACK_BYTE.get(), (CHANCE_DROP_BYTES.get() * multiplier));
                dropItem(InitItems.DEFENSE_BYTE.get(), (CHANCE_DROP_BYTES.get() * multiplier));
                dropItem(InitItems.SPATTACK_BYTE.get(), (CHANCE_DROP_BYTES.get() * multiplier));
                dropItem(InitItems.SPDEFENSE_BYTE.get(), (CHANCE_DROP_BYTES.get() * multiplier));
                dropItem(InitItems.HEALTH_BYTE.get(), (CHANCE_DROP_BYTES.get() * multiplier));
            }

            for (int j = 0; j <= (this.isBaby2() ? 2 : this.isRookie() ? 7 : this.isChampion() ? 20 : this.isUltimate() ? 40 : 60); j++) {
                if ((chanceOverHundred(80 * multiplier))) {
                    killer.gainSpecificXp(this.getXpDrop());
                }
            }
        }
    }

    @Override
    public void setTarget(@Nullable LivingEntity entity) {
        if ((entity instanceof TamableAnimal animal && animal.getOwner() != null && this.getOwner() != null && animal.isOwnedBy(this.getOwner()))) {
            return;
        }
        if(entity instanceof AbstractTrainingGood && !this.wasMelee) resetAttackGoals();
        super.setTarget(entity);
    }

    @Override
    public boolean doHurtTarget(@NotNull Entity target) {
        if (this.level() instanceof ServerLevel)
            triggerAnim("movement", "attack");

        DamageSource source = this.damageSources().mobAttack(this);

        if(target instanceof CustomDigimon cd)
            cd.hurt(source, calculateDamage(
                    this.getAttackStat() + this.getCurrentLevel(),
                    cd.getDefenceStat() + cd.getCurrentLevel()));
        else
            target.hurt(source, this.getCurrentLevel()/2F);

        return super.doHurtTarget(target);
    }


    @Override
    public void setHealth(float value) {
        if (value <= 0 && this.getLifes() > 1) {
            this.restLife();
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
    public void performRangedAttack(@NotNull LivingEntity mob, float p_33318_) {
        ProjectileDefault bullet = (ProjectileDefault) InitProjectiles.projectileMap.get(this.getSpMoveName()).get().create(this.level());
        if (bullet != null) {
            if (repeat <= 0) repeat = bullet.getRepeatTimes() + 1;

            if (mob.level() instanceof ServerLevel)
                triggerAnim("movement", "shoot");
            bullet.performRangedAttack(this, mob);

            if (repeat > 1 && !this.wasMelee) this.rangedGoal.setAttackTime(5);
            --repeat;
        }
    }

    public List<Float> scalesList = List.of(
        BABY_SCALE.get() * 0.01F * 0.85F,
        ROOKIE_SCALE.get() * 0.01F,
        CHAMPION_SCALE.get() * 0.01F * 1.15F,
        ULTIMATE_SCALE.get() * 0.01F,
        BOSS_SCALE.get() * 0.01F * 1.5F
    );

    @Override
    public float getScale() {
        int index = this.isBoss() ? 4 : this.getEvoStage();

        return scalesList.get(index);
    }

    @Override
    public void setOrderedToSit(boolean p_21840_) {
        this.moveControl.strafe(0f, 0f);
        super.setOrderedToSit(p_21840_);
    }

    @Override
    public double getPassengersRidingOffset() {
        return super.getPassengersRidingOffset() + riderOffSet
                + (!this.getPassengers().isEmpty() && this.getPassengers().get(0) instanceof CustomDigimon ? -0.55 : 0);
    }

    @Override
    public double getMyRidingOffset() {
        return super.getMyRidingOffset() + 0.3d;
    }


    //Sounds

    protected void playStepSound(@NotNull BlockPos p_28254_, @NotNull BlockState p_28255_) {
        if(this.isBoss() || this.isUltimate())
            this.playSound(DigiSounds.ULTIMATE_STEPS.get(), 0.30F, 1.0F);
        else if (this.isBaby2())
            this.playSound(DigiSounds.BABY_STEPS.get(), 0.25F, 1.0F);
        else if (this.isRookie() || this.getType().equals(InitDigimons.MAMEMON.get()) || this.getType().equals(InitDigimons.MAMETYRAMON.get()))
            this.playSound(DigiSounds.ROOKIE_STEPS.get(), 0.20F, 1.0F);
        else
            this.playSound(DigiSounds.CHAMPION_STEPS.get(), 0.20F, 1.0F);
    }

    //Animations

    protected AnimationController<CustomDigimon> getMovementController(){
        return animController(this);
    }


    public static <T extends CustomDigimon & GeoEntity> AnimationController<T> animController(T digimon) {
        return new AnimationController<>(digimon, "movement", 7, event -> {

            event.setControllerSpeed(1.25f);

            if (digimon.isEvolving()) {
                event.getController().setAnimation(RawAnimation.begin().then("show", Animation.LoopType.LOOP));
                return PlayState.CONTINUE;
            }

            if (digimon.isMoving(event)) {
                event.getController().setAnimation(RawAnimation.begin().then(digimon.walkAnim, Animation.LoopType.LOOP));
            } else {
                if (digimon.isInSittingPose() || digimon.getVehicle() != null) {
                    event.getController().setAnimation(RawAnimation.begin().then(digimon.sitAnim, Animation.LoopType.HOLD_ON_LAST_FRAME));
                    return PlayState.CONTINUE;
                } else {
                    event.getController().setAnimation(RawAnimation.begin().then(digimon.idleAnim, Animation.LoopType.LOOP));
                    return PlayState.CONTINUE;
                }
            }
            return PlayState.CONTINUE;
        });
    }

    <T extends CustomDigimon & GeoEntity> boolean  isMoving(AnimationState<T> event){
        if(this.isVehicle()){
            event.setControllerSpeed(2.25f);
            return moving;
        }
        event.setControllerSpeed(1.5f);
        return event.isMoving();
    }

    public boolean moving = false;

    Vec3 previousPosition = this.position();

    public boolean updateMovingState() {
        Vec3 currentPosition = this.position();
        boolean flag = !currentPosition.equals(this.previousPosition);
        previousPosition = currentPosition;
        return flag;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(
                getMovementController()
                        .triggerableAnim("shoot", RawAnimation.begin().then(this.shootAnim, Animation.LoopType.PLAY_ONCE))
                        .triggerableAnim("attack", RawAnimation.begin().then(this.attackAnim, Animation.LoopType.PLAY_ONCE))
                        .triggerableAnim("feed", RawAnimation.begin().then("xp", Animation.LoopType.PLAY_ONCE))
        );
    }

    protected AnimatableInstanceCache factory = GeckoLibUtil.createInstanceCache(this);
    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.factory;
    }
}
