package net.modderg.thedigimod.server.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.world.*;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.modderg.thedigimod.TheDigiMod;
import net.modderg.thedigimod.server.ModCommonConfigs;
import net.modderg.thedigimod.client.gui.inventory.DigimonMenu;
import net.modderg.thedigimod.client.gui.inventory.DigimonInventory;
import net.modderg.thedigimod.server.entity.goals.*;
import net.modderg.thedigimod.server.entity.managers.DigimonJsonDataManager;
import net.modderg.thedigimod.server.entity.managers.EvolutionCondition;
import net.modderg.thedigimod.server.entity.managers.MoodManager;
import net.modderg.thedigimod.server.entity.managers.ParticleManager;
import net.modderg.thedigimod.server.goods.AbstractTrainingGood;
import net.modderg.thedigimod.client.gui.DigiviceScreenStats;
import net.modderg.thedigimod.server.item.TDItemsAdmin;
import net.modderg.thedigimod.server.item.TDItemsBabyDigimon;
import net.modderg.thedigimod.server.item.TDItems;
import net.modderg.thedigimod.server.item.diets.DietInit;
import net.modderg.thedigimod.server.item.diets.DigimonDiet;
import net.modderg.thedigimod.server.packet.PacketInit;
import net.modderg.thedigimod.server.packet.SToCSGainXpPacket;
import net.modderg.thedigimod.client.particles.DigitalParticles;
import net.modderg.thedigimod.server.projectiles.ProjectileDefault;
import net.modderg.thedigimod.server.projectiles.InitProjectiles;
import net.modderg.thedigimod.server.sound.DigiSounds;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import oshi.util.tuples.Pair;
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
import static net.modderg.thedigimod.server.ModCommonConfigs.*;

public class DigimonEntity extends TamableAnimal implements GeoEntity, ItemSteerable, RangedAttackMob, MenuProvider {

    private static final TagKey<EntityType<?>> riderEntityType = TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(TheDigiMod.MOD_ID, "riding/rider"));
    private static final TagKey<EntityType<?>> bbyRiderEntityType = TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(TheDigiMod.MOD_ID, "riding/baby_rider"));
    private static final TagKey<EntityType<?>> bbyVehicleEntityType = TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(TheDigiMod.MOD_ID, "riding/baby_vehicle"));

    private final DigimonInventory inventory = new DigimonInventory(this, 5);
    private final ServerBossEvent bossEvent = new ServerBossEvent(this.getDisplayName(), BossEvent.BossBarColor.BLUE, BossEvent.BossBarOverlay.PROGRESS);

    private int repeatMove = 0;
    private int poopTicks = -1;

    public boolean isEmissive = false;

    public MoodManager moodManager = new MoodManager(this);

    public ParticleManager particleManager = new ParticleManager();

    public DigimonJsonDataManager jsonManager = new DigimonJsonDataManager();

    protected static final EntityDataAccessor<String> RANK = SynchedEntityData.defineId(DigimonEntity.class, EntityDataSerializers.STRING);

    public String getRank() {
        return this.getEntityData().get(RANK);
    }

    public DigimonEntity setRank(String rank) {
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

    public double riderOffSet = 0;
    protected boolean isMountDigimon = false;
    public boolean isMountDigimon() {return isMountDigimon;}

    public DigimonEntity setMountDigimon(double riderOff) {
        isMountDigimon = true;
        this.riderOffSet = riderOff;
        return this;
    }

    protected RegistryObject<?>[] reincarnateTo = new RegistryObject[]{TDItemsBabyDigimon.BOTAMON};

    public RegistryObject<?>[] getReincarnateTo() {
        return reincarnateTo;
    }

    public DigimonEntity setBabyDrops(RegistryObject<?>... babies) {
        reincarnateTo = babies;
        return this;
    }

    protected int[] xpDrop = null;

    public int[] getXpDrop() {
        return xpDrop;
    }

    public void setXpDrop(int... xp) {
        xpDrop = xp;
    }

    protected static final int MAX_LEVEL = 65;
    protected static final int MAX_ADULT = 250;
    protected static final int MAX_ULTIMATE = 500;
    public static final int MAX_MEGA = 999;

    protected int evoStage = 1;

    public int getEvoStage() {
        return evoStage;
    }

    public void setEvoStage(int evoStage) {this.evoStage = evoStage;}

    public boolean isBaby() {return isBaby1() || isBaby2();}

    public Boolean isBaby1() {return getEvoStage() == 0;}

    public Boolean isBaby2() {return getEvoStage() == 1;}

    public Boolean isRookie() {
        return getEvoStage() == 2;
    }

    public Boolean isChampion() {
        return getEvoStage() == 3;
    }

    public Boolean isUltimate() {
        return getEvoStage() == 4;
    }

    public int getMaxStat() {
        return this.isBaby1() ? 2 : (this.isBaby2() ? 25 : (this.isRookie() ? 100 : (this.isChampion() ? MAX_ADULT : (this.isUltimate() ? MAX_ULTIMATE : MAX_MEGA))));
    }

    public int getMaxLevel() {
        return this.isBaby1() ? 2 : (this.isBaby2() ? ROOKIE_EVOLUTION_LEVEL.get()-1 : (this.isRookie() ? CHAMPION_EVOLUTION_LEVEL.get()-1 : (this.isChampion() ? ULTIMATE_EVOLUTION_LEVEL.get()-1 : (this.isUltimate() ? 70 : 100))));
    }

    public int getMinLevel() {
        return this.isBaby1() ? 1 : (this.isBaby2() ? 2 : (this.isRookie() ? ROOKIE_EVOLUTION_LEVEL.get() : (this.isChampion() ? CHAMPION_EVOLUTION_LEVEL.get() : (this.isUltimate() ? ULTIMATE_EVOLUTION_LEVEL.get() : 71))));
    }

    public Boolean evolutionLevelAchieved() {
        return this.getCurrentLevel() > this.getMaxLevel();
    }

    public Boolean isEvolving() {
        return evoCount > 0;
    }

    public EvolutionCondition[] evolutionConditions = new EvolutionCondition[0];

    public String digitronEvo;

    public DigimonEntity setDigitronEvo(String evo) {
        digitronEvo = evo;
        return this;
    }

    protected static final EntityDataAccessor<String> PREEVO = SynchedEntityData.defineId(DigimonEntity.class, EntityDataSerializers.STRING);

    public void setPreEvo(String i) {
        this.getEntityData().set(PREEVO, i);
    }

    public String getPreEvo() {
        return this.getEntityData().get(PREEVO);
    }

    public String[] getPreEvos() {
        return Arrays.stream(getPreEvo().split("-")).filter(s -> !s.equals("a")).toArray(String[]::new);
    }

    protected static final EntityDataAccessor<String> NICKNAME = SynchedEntityData.defineId(DigimonEntity.class, EntityDataSerializers.STRING);

    public void setNickName(String i) {
        this.getEntityData().set(NICKNAME, i);
    }

    public String getNickName() {
        return this.getEntityData().get(NICKNAME);
    }

    protected static final EntityDataAccessor<String> SPMOVENAME = SynchedEntityData.defineId(DigimonEntity.class, EntityDataSerializers.STRING);

    public void setSpMoveName(String i) {
        this.getEntityData().set(SPMOVENAME, i);
    }

    public String getSpMoveName() {
        return this.getEntityData().get(SPMOVENAME);
    }

    protected static final EntityDataAccessor<Boolean> SPAWN_RIDER_FLAG = SynchedEntityData.defineId(DigimonEntity.class, EntityDataSerializers.BOOLEAN);

    public void setSpawnRiderFlag(boolean i) {
        this.getEntityData().set(SPAWN_RIDER_FLAG, i);
    }

    public boolean getSpawnRiderFlag() {
        return this.getEntityData().get(SPAWN_RIDER_FLAG);
    }

    protected static final EntityDataAccessor<Integer> MOVEMENTID = SynchedEntityData.defineId(DigimonEntity.class, EntityDataSerializers.INT);

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

    //dragon-0 beast-1 insectplant-2 aquan-3 wind-4 machine-5 earth-6 nightmare-7 holy-8 poop-9
    protected static final EntityDataAccessor<String> SPECIFICXPS = SynchedEntityData.defineId(DigimonEntity.class, EntityDataSerializers.STRING);

    public void saveGainedXp(int s) {

        String[] ss = this.getGainedXps().split("-");

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < ss.length; i++)
            if (i == s)
                sb.append(Integer.parseInt(ss[i]) + 1).append("-");
            else
                sb.append(ss[i]).append("-");

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

        if(ss.length <= 9){
            setGainedXps(getGainedXps() + "-0");
            ss = this.getGainedXps().split("-");
        }
        if (ss[i].equals("")){
            ss[i] = "0";
            setGainedXps(String.join("-", ss));
        }

        return Integer.parseInt(ss[i]);
    }

    protected static final EntityDataAccessor<Integer> LIVES = SynchedEntityData.defineId(DigimonEntity.class, EntityDataSerializers.INT);

    public void setLives(int i) {
        this.getEntityData().set(LIVES, Math.min(Math.min(3, ModCommonConfigs.MAX_DIGIMON_LIVES.get()),i));
    }

    public int getLives() {
        return this.getEntityData().get(LIVES);
    }

    public void addLife() {
        this.getEntityData().set(LIVES, Math.min(Math.min(3, ModCommonConfigs.MAX_DIGIMON_LIVES.get()), this.getLives() + 1));
        this.setHealth(MAX_MEGA);
    }

    public void restLife() {
        this.setCareMistakesStat(this.getCareMistakesStat() + 1);
        this.getEntityData().set(LIVES, Math.max(0, this.getLives() - 1));
        this.setHealth(MAX_MEGA);
    }

    protected static final EntityDataAccessor<Integer> ATTACK_STAT = SynchedEntityData.defineId(DigimonEntity.class, EntityDataSerializers.INT),
            DEFENCE_STAT = SynchedEntityData.defineId(DigimonEntity.class, EntityDataSerializers.INT),
            SPATTACK_STAT = SynchedEntityData.defineId(DigimonEntity.class, EntityDataSerializers.INT),
            SPDEFENCE_STAT = SynchedEntityData.defineId(DigimonEntity.class, EntityDataSerializers.INT),
            BATTLES_STAT = SynchedEntityData.defineId(DigimonEntity.class, EntityDataSerializers.INT),
            CARE_MISTAKES_STAT = SynchedEntityData.defineId(DigimonEntity.class, EntityDataSerializers.INT);

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

    public void setHealthStat(int i) {Objects.requireNonNull(getAttribute(Attributes.MAX_HEALTH)).setBaseValue(Math.min(i, getMaxStat()));}

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

    protected static final EntityDataAccessor<Integer> EXPERIENCETOTAL = SynchedEntityData.defineId(DigimonEntity.class, EntityDataSerializers.INT);

    public void addExperienceTotal() {
        this.getEntityData().set(EXPERIENCETOTAL, getExperienceTotal() + 1);
    }

    public void setExperienceTotal(int i) {
        this.getEntityData().set(EXPERIENCETOTAL, i);
    }

    public int getExperienceTotal() {
        return this.getEntityData().get(EXPERIENCETOTAL);
    }

    protected static final EntityDataAccessor<Integer> LEVELXP = SynchedEntityData.defineId(DigimonEntity.class, EntityDataSerializers.INT);

    public void addLevelXp() {
        this.getEntityData().set(LEVELXP, getLevelXp() + 1);
    }

    public void setLevelXp(int i) {
        this.getEntityData().set(LEVELXP, i);
    }

    public int getLevelXp() {
        return this.getEntityData().get(LEVELXP);
    }

    protected static final EntityDataAccessor<Integer> CURRENTLEVEL = SynchedEntityData.defineId(DigimonEntity.class, EntityDataSerializers.INT);

    public void levelUp() {
        this.playSound(DigiSounds.LEVEL_UP_SOUND.get(), 0.05F, 1.0F);
        this.setCurrentLevel(Math.min(getCurrentLevel() + 1, MAX_LEVEL));
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

    DigitalRangedAttackGoal<DigimonEntity> rangedGoal = new DigitalRangedAttackGoal<>(this, 1.3D, 65, 10f);
    DigitalMeleeAttackGoal meleeGoal = new DigitalMeleeAttackGoal(this, 1.0D, true);

    DigimonDiet diet;
    public DigimonDiet getDiet() {return diet;}
    public void setDiet(DigimonDiet diet) {this.diet = diet;}

    protected DigimonEntity(EntityType<? extends TamableAnimal> p_21803_, Level p_21804_) {
        super(p_21803_, p_21804_);

        resetAttackGoals();
        this.setCustomName(Component.empty());
        this.diet = DietInit.REGULAR_DIET;
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
        if (component != null && !component.getString().isEmpty())
            this.setNickName(component.getString());
        else
            component = Component.literal("Digimon");
        super.setCustomName(Component.literal(component.getString() + " (" + this.getCurrentLevel() + "Lv)"));
    }

    public void evolveDigimon() {
        DigimonEntity evoD = getNextEvolution();

        evoD.copyOtherDigi(this);

        String[] prevos = this.getPreEvo().split("-");
        prevos[this.getEvoStage()] = this.getLowerCaseSpecies();
        evoD.setPreEvo(String.join("-", prevos));

        this.level().addFreshEntity(evoD);
        this.remove(RemovalReason.UNLOADED_TO_CHUNK);
    }

    public DigimonEntity getNextEvolution() {

        EvolutionCondition condition = Arrays.stream(evolutionConditions)
                .filter(EvolutionCondition::checkConditions)
                .findFirst()
                .orElse(null);

        if(this.getRollFirstEvo() != -1 && evolutionConditions[this.getRollFirstEvo()].checkConditions())
            condition = evolutionConditions[this.getRollFirstEvo()];

        if(condition == null)
            return null;

        EntityType<?> evo = ForgeRegistries.ENTITY_TYPES.getValue(new ResourceLocation(TheDigiMod.MOD_ID, condition.getEvolution()));
        return ((DigimonEntity) Objects.requireNonNull(evo.create(this.level()))).setRank(condition.getRank());
    }

    public void deEvolveDigimon() {

        String[] preEvos = getPreEvos();
        if (preEvos.length == 0)
            return;

        EntityType<?> pEvo = ForgeRegistries.ENTITY_TYPES.getValue(new ResourceLocation(TheDigiMod.MOD_ID, preEvos[preEvos.length-1]));
        DigimonEntity preEvo = (DigimonEntity) pEvo.create(this.level());

        assert preEvo != null;
        preEvo.copyOtherDigi(this);

        preEvo.setCurrentLevel(preEvo.getMinLevel() + (preEvo.getMaxLevel() - preEvo.getMinLevel())/3);
        preEvo.setAttackStat(preEvo.getMaxStat() / 4);
        preEvo.setDefenceStat(preEvo.getMaxStat() / 4);
        preEvo.setSpAttackStat(preEvo.getMaxStat() / 4);
        preEvo.setSpDefenceStat(preEvo.getMaxStat() / 4);
        preEvo.setHealthStat(preEvo.getMaxStat() / 4);

        this.level().addFreshEntity(preEvo);
        this.remove(RemovalReason.UNLOADED_TO_CHUNK);
    }

    public void copyOtherDigiAndData(DigimonEntity d) {
        this.setGainedXps(d.getGainedXps());
        this.copyOtherDigi(d);
    }

    public void copyOtherDigi(DigimonEntity d) {
        if (d.getOwner() != null)
            this.tame((Player) d.getOwner());

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
        this.setLives(d.getLives());
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
                        new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(TDItemsAdmin.adminmMap.get(DigiviceScreenStats.getXpItem(id)).get())), this);
            else
                PacketInit.sendToAll(new SToCSGainXpPacket(this.getId(), id));
        }
    }

    public int getNeededXp() {
        int level = getCurrentLevel();
        float levelFactor = (level / 24f);
        float levelEffect = (levelFactor * levelFactor * levelFactor * (150f - level)) / 150f;
        float baseXp = levelEffect + level / 10f;
        int multiplier = 10;

        float totalXp = baseXp * multiplier;

        return (int) Math.max(1, totalXp);
    }


    public int calculateDamage(int attack, int defense) {
        return Math.max(1, attack / Math.max(1, defense/10));
    }

    public void eatItem(ItemStack itemStack, int moodAdd, int heal) {

        poopTicks = 1000;

        this.playSound(itemStack.getItem().getEatingSound(), 0.15F, 1.0F);
        this.moodManager.addMoodPoints(moodAdd);

        if(canHeal())
            this.heal(heal);

        itemStack.shrink(1);

        eatItemAnim(itemStack);
    }

    public void eatItemAnim(ItemStack itemStack){
        particleManager.spawnItemParticles(itemStack, 16, this);

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

    public boolean canHeal() {return !this.isAggressive() || this.getTarget() instanceof AbstractTrainingGood;}

    protected static final EntityDataAccessor<Boolean> BOSS = SynchedEntityData.defineId(DigimonEntity.class, EntityDataSerializers.BOOLEAN);

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
        if (this.isTame())
            return super.finalizeSpawn(p_146746_, p_146747_, p_146748_, p_146749_, p_146750_);

        this.setHealthStat(Math.min(getMaxStat(),
                random.nextInt(this.isBaby() ? 1 : (this.isRookie() ? 25 : (this.isChampion() ? 100 : (this.isUltimate() ? MAX_ADULT : MAX_ULTIMATE))), getMaxStat())));

        this.setHealth((float) this.getHealthStat());
        this.setCurrentLevel((int) Math.max(getMinLevel(), getMaxLevel() * getHealth() / getMaxStat()));
        this.setAttackStat((int) getHealth());
        this.setDefenceStat((int) getHealth());
        this.setSpAttackStat((int) getHealth());
        this.setSpDefenceStat((int) getHealth());

        if(chanceOverHundred(1) && chanceOverHundred(50)){
            this.setBoss(true);
            this.setCurrentLevel(this.getMaxLevel());
            this.setAttackStat(MAX_MEGA);
            this.setDefenceStat(MAX_MEGA);
            this.setSpDefenceStat(MAX_MEGA);
            this.setSpAttackStat(MAX_MEGA);
            this.setHealthStat(MAX_MEGA);

            this.setHealth(this.getMaxHealth());
            this.setSpMoveName(InitProjectiles.projectileMap.keySet().toArray()[random.nextInt(InitProjectiles.projectileMap.size())].toString());
        }

        setSpawnRiderFlag(true);

        return super.finalizeSpawn(p_146746_, p_146747_, p_146748_, p_146749_, p_146750_);
    }

    public void trySpawnDigimonRider(){

        this.setSpawnRiderFlag(false);

        if(chanceOverHundred(50)) return;

        EntityType<?> riderType = null;

        if (this.getType().is(bbyVehicleEntityType))
            riderType = getRandomDigimonFromTag(bbyRiderEntityType);
        else if (this.isMountDigimon)
            riderType = getRandomDigimonFromTag(riderEntityType);

        if (riderType != null){
            DigimonEntity rider = (DigimonEntity) riderType.create(this.level());
            level().addFreshEntity(rider);
            rider.setPos(this.position());
            rider.startRiding(this);
            rider.trySpawnDigimonRider();
        }
    }

    public EntityType<?> getRandomDigimonFromTag(TagKey<EntityType<?>> tagKey) {
        Object[] matchingDigis = TDEntities.DIGIMONS.getEntries().stream()
                .filter(e -> e.get().is(tagKey)).toArray();

        if(matchingDigis.length == 0)
            return null;

        return ((RegistryObject<EntityType<?>>) matchingDigis[random.nextInt(matchingDigis.length)]).get();
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

    protected static final EntityDataAccessor<Integer> ROLL_EVO_FIRST = SynchedEntityData.defineId(DigimonEntity.class, EntityDataSerializers.INT);
    public void setRollFirstEvo(int evoIdx) {this.getEntityData().set(ROLL_EVO_FIRST, evoIdx);}
    public int getRollFirstEvo() {return this.getEntityData().get(ROLL_EVO_FIRST);}

    protected static final EntityDataAccessor<Integer> DIRTY_COUNTER = SynchedEntityData.defineId(DigimonEntity.class, EntityDataSerializers.INT);
    public void setDirtyCounter(int count) {this.getEntityData().set(DIRTY_COUNTER, count);}
    public int getDirtyCounter() {return this.getEntityData().get(DIRTY_COUNTER);}

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(NICKNAME, "");
        this.entityData.define(MOVEMENTID, 1);
        this.entityData.define(CURRENTLEVEL, 1);
        this.entityData.define(LEVELXP, 0);
        this.entityData.define(EXPERIENCETOTAL, 0);
        this.entityData.define(SPECIFICXPS, "0-0-0-0-0-0-0-0-0-0");
        this.entityData.define(MoodManager.MOODPOINTS, 249);
        this.entityData.define(ATTACK_STAT, 1);
        this.entityData.define(DEFENCE_STAT, 1);
        this.entityData.define(SPATTACK_STAT, 1);
        this.entityData.define(SPDEFENCE_STAT, 1);
        this.entityData.define(BATTLES_STAT, 0);
        this.entityData.define(CARE_MISTAKES_STAT, 0);
        this.entityData.define(LIVES, 1);
        this.entityData.define(PREEVO, "a-a-a-a-a");
        this.entityData.define(SPMOVENAME, "unnamed");
        this.entityData.define(BOSS, false);
        this.entityData.define(RANK, "zero");
        this.entityData.define(ROLL_EVO_FIRST, -1);
        this.entityData.define(DIRTY_COUNTER, 0);
        this.entityData.define(SPAWN_RIDER_FLAG, false);
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
            this.setLives(compound.getInt("LIFES"));

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

        if (compound.contains("ROLL_EVO_FIRST"))
            this.setRollFirstEvo(compound.getInt("ROLL_EVO_FIRST"));

        if (compound.contains("DIRTY_COUNTER"))
            this.setDirtyCounter(compound.getInt("DIRTY_COUNTER"));

        if (compound.contains("SPAWN_RIDER_FLAG"))
            this.setSpawnRiderFlag(compound.getBoolean("SPAWN_RIDER_FLAG"));

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
        compound.putInt("LIFES", this.getLives());
        compound.putString("PREEVO", this.getPreEvo());
        compound.putString("SPMOVENAME", this.getSpMoveName());
        compound.putBoolean("BOSS", this.isBoss());
        compound.putString("RANK", this.getRank());
        compound.put("Inventory", inventory.serializeNBT());
        compound.putInt("ROLL_EVO_FIRST", this.getRollFirstEvo());
        compound.putInt("DIRTY_COUNTER", this.getDirtyCounter());
        compound.putBoolean("SPAWN_RIDER_FLAG", this.getSpawnRiderFlag());
    }

    public void initInventory(){
        inventory.genSlots(0);
    }

    public DigimonInventory getInventory() {
        return inventory;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int p_39954_, @NotNull Inventory p_39955_, @NotNull Player p_39956_) {
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

        if (itemStack.is(Items.WATER_BUCKET) || itemStack.is(Items.PAPER)){

            this.setDirtyCounter(0);

            if(itemStack.getItem() instanceof BucketItem){
                this.playSound(SoundEvents.BUCKET_EMPTY);
                itemStack.shrink(1);
                player.getInventory().add(new ItemStack(Items.BUCKET));
            } else{
                this.playSound(SoundEvents.BOOK_PAGE_TURN);
                itemStack.shrink(1);
            }

            return InteractionResult.SUCCESS;
        }

        if (this.isFood(itemStack))
            return InteractionResult.CONSUME;

        if (this.isTame() && Objects.equals(this.getOwnerUUID(), player.getUUID())) {

            if (player.isShiftKeyDown()) {
                this.changeMovementID();
                return InteractionResult.SUCCESS;
            }

            if (itemStack.isEmpty() && otherItemStack.isEmpty()) {

                if (this.isMountDigimon && ((RIDE_ALLOWED.get() && !(this instanceof FlyingDigimonEntity)) || (FLIGHT_ALLOWED.get() && this instanceof FlyingDigimonEntity))    ) {

                    player.startRiding(this);
                    this.setMovementID(1);

                } else if (this.isBaby())
                    getInPlayerHead(player);
                return InteractionResult.SUCCESS;
            }
        }

        return super.mobInteract(player, hand);
    }

    @Override
    public boolean isFood(@NotNull ItemStack item) {
        boolean isDietItem = diet.isPartOfDiet(item);
        boolean isEdible = item.isEdible();

        if(isDietItem){
            Pair<Integer, Integer> props = diet.getCaloriesAndHeal(item);
            eatItem(item, props.getA(), props.getB());
        } else if (isEdible)
            eatItem(item, 1, 1);

        return isDietItem || isEdible;
    }

    public void getInPlayerHead(Entity player){
        if (player.getPassengers().isEmpty()) this.startRiding(player);
        else getInPlayerHead(player.getPassengers().get(0));
    }

    public void getOffPlayerHead(){

        if(this.getVehicle() != null)
            this.getVehicle().ejectPassengers();

        if(!this.getPassengers().isEmpty() && this.getPassengers().get(0) instanceof DigimonEntity cd)
            cd.getOffPlayerHead();
    }

    int attack = this.getAttackStat(), defence = this.getDefenceStat(), spattack = this.getSpAttackStat(), spdefence = this.getSpDefenceStat(),
            battles = this.getBattlesStat(), health = this.getHealthStat(), mistakes = this.getCareMistakesStat(), lifes = this.getLives();

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
        if (lifes != this.getLives()) {
            particleManager.spawnStatUpParticles(DigitalParticles.LIFE_PARTICLE, 7, this);
            lifes = this.getLives();
        }
    }

    @Override
    public boolean hasCustomName() {
        return true;
    }

    int baby1Evolve = 4800;
    @Override
    public void tick() {
        checkChangeStats();

        if(getSpawnRiderFlag())
            trySpawnDigimonRider();

        if(poopTicks-- == 0){
            this.spawnAtLocation(TDItems.POOP.get());
            this.playSound(SoundEvents.BEEHIVE_EXIT);
            this.setDirtyCounter(this.getDirtyCounter() + 1);

            if(this.getDirtyCounter() > 2)
                this.setCareMistakesStat(this.getCareMistakesStat() + 1);
            if (this.level() instanceof ServerLevel)
                triggerAnim("movement", "feed");
        }

        if(this.isVehicle())
            moving = updateMovingState();

        if(this.getMovementID() == -2 && !this.level().getGameRules().getRule(GameRules.RULE_MOBGRIEFING).get())
            this.changeMovementID();

        if(this.isBaby() && this.getVehicle() instanceof Player player && player.isShiftKeyDown())
            getOffPlayerHead();

        if (evoCount == 1)
            this.evolveDigimon();

        if (evoCount-- > 0) {
            if(evoCount < 5)
                particleManager.finishEvoParticles(this);
            particleManager.spawnEvoParticles(this);
        }

        if (this.moodManager.getMoodPoints() < 100 && this.isTame() && random.nextInt(0, 150) == 1)
            particleManager.spawnBubbleParticle(DigitalParticles.MEAT_BUBBLE, this);

        if(this.isAggressive() && !(this.getTarget() instanceof AbstractTrainingGood) && ticksToShootAnim-- < 0) resetAttackGoals();

        if (this.isTame()){
            if(this.random.nextInt(200) == 2)
                moodManager.spawnMoodParticle();
            if(this.isBaby1() && ((level().isClientSide() && !this.isInSittingPose()) || (!level().isClientSide() && !this.isOrderedToSit())) && baby1Evolve-- <= 0)
                this.gainSpecificXp(0);
        }
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
        dropItem(new ItemStack(item), chance);
    }

    public void dropItem(ItemStack item, int chance) {
        if (chanceOverHundred(chance))
            this.spawnAtLocation(item);
    }

    @Override
    public void die(@NotNull DamageSource source) {
        if(!level().isClientSide()){
            if (this.isTame()) {
                IntStream.range(0,this.getInventory().getStackHandler().getSlots()).forEach(i -> {
                    ItemStack stack = this.getInventory().getStackHandler().getStackInSlot(i);
                    if (!stack.isEmpty())
                        this.dropItem(stack, 100);
                });

                dropItem((Item) getReincarnateTo()[random.nextInt(getReincarnateTo().length)].get(), 101);

            } else if (this.getLastHurtByMob() instanceof DigimonEntity killer) {

                    int bossMultiplier = (this.isBoss() && killer.getEvoStage() == this.getEvoStage()) ? 3 : 1;

                    dropDigiLoot(killer, bossMultiplier, bossMultiplier);
                }
        }
        super.die(source);
    }

    public void dropDigiLoot(DigimonEntity killer, int repeat, int multiplier){

        if (this.isBaby1())
            return;

        for (int i = 0; i < repeat; i++) {

            if (killer.getEvoStage() <= this.getEvoStage())
                killer.setBattlesStat(killer.getBattlesStat() + 1);


            dropItem((Item) getReincarnateTo()[random.nextInt(getReincarnateTo().length)].get(), (CHANCE_DROP_BABY.get() * multiplier));

            dropItem(TDItems.itemMap.get("chip_" + this.getSpMoveName()).get(), (CHANCE_DROP_MOVE_CHIP.get() * multiplier));

            dropItem(TDItems.DIGI_MEAT.get(), (CHANCE_DROP_FOOD.get() * multiplier));
            dropItem(TDItems.GUILMON_BREAD.get(), (CHANCE_DROP_FOOD.get() * multiplier));

            dropItem(TDItems.DIGIMON_CARD.get(), (CHANCE_DROP_CARD.get() * multiplier));

            if (!this.isBaby2()) {
                dropItem(TDItems.ATTACK_BYTE.get(), (CHANCE_DROP_BYTES.get() * multiplier));
                dropItem(TDItems.DEFENSE_BYTE.get(), (CHANCE_DROP_BYTES.get() * multiplier));
                dropItem(TDItems.SPATTACK_BYTE.get(), (CHANCE_DROP_BYTES.get() * multiplier));
                dropItem(TDItems.SPDEFENSE_BYTE.get(), (CHANCE_DROP_BYTES.get() * multiplier));
                dropItem(TDItems.HEALTH_BYTE.get(), (CHANCE_DROP_BYTES.get() * multiplier));
            }

            for (int j = 0; j <= (this.isBaby2() ? 0 :this.isBaby2() ? 2 : this.isRookie() ? 7 : this.isChampion() ? 20 : this.isUltimate() ? 40 : 60); j++) {
                if ((chanceOverHundred(80 * multiplier))) {
                    killer.gainSpecificXp(this.getXpDrop()[random.nextInt(0, this.getXpDrop().length)]);
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

        if(target instanceof DigimonEntity cd)
            cd.hurt(source, calculateDamage(
                    this.getAttackStat() + this.getCurrentLevel(),
                    cd.getDefenceStat() + cd.getCurrentLevel()));
        else
            target.hurt(source, this.getCurrentLevel()/2F);

        return super.doHurtTarget(target);
    }


    @Override
    public void setHealth(float value) {
        if (value <= 0 && this.getLives() > 1)
            this.restLife();
        else
            super.setHealth(value);
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(@NotNull ServerLevel p_146743_, @NotNull AgeableMob p_146744_) {
        return null;
    }

    @Override
    public void performRangedAttack(@NotNull LivingEntity mob, float p_33318_) {
        ProjectileDefault bullet = (ProjectileDefault) InitProjectiles.projectileMap.get(this.getSpMoveName()).get().create(this.level());
        if (bullet != null) {
            if (repeatMove <= 0) repeatMove = bullet.getRepeatTimes();

            if (mob.level() instanceof ServerLevel)
                triggerAnim("movement", "shoot");
            bullet.performRangedAttack(this, mob);

            if (repeatMove > 1 && !this.wasMelee) this.rangedGoal.setAttackTime(5);
            --repeatMove;
        }
    }

    public List<Float> scalesList = List.of(
            BABY_SCALE.get() * 0.01F * 0.85F,
            BABY_SCALE.get() * 0.01F * 0.85F,
            ROOKIE_SCALE.get() * 0.01F,
            CHAMPION_SCALE.get() * 0.01F * 1.15F,
            ULTIMATE_SCALE.get() * 0.01F,
            BOSS_SCALE.get() * 0.01F * 1.5F
    );

    @Override
    public float getScale() {
        int index = this.isBoss() ? 5 : this.getEvoStage();

        return scalesList.get(index);
    }

    //riding stuff

    @Override
    public void setOrderedToSit(boolean p_21840_) {
        this.moveControl.strafe(0f, 0f);
        super.setOrderedToSit(p_21840_);
    }

    @Override
    public double getPassengersRidingOffset() {
        return super.getPassengersRidingOffset() + riderOffSet
                + (!this.getPassengers().isEmpty() && this.getPassengers().get(0) instanceof DigimonEntity ? -0.55 : 0);
    }

    @Override
    public double getMyRidingOffset() {
        return super.getMyRidingOffset() + 0.3d;
    }


    //Sounds

    final static TagKey<EntityType<?>> key = TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(TheDigiMod.MOD_ID,"sounds/rookie_steps"));

    protected void playStepSound(@NotNull BlockPos p_28254_, @NotNull BlockState p_28255_) {

        if(this.isBoss())
            this.playSound(DigiSounds.ULTIMATE_STEPS.get(), 0.30F, 1.0F);
        else if (this.isBaby())
            this.playSound(DigiSounds.BABY_STEPS.get(), 0.25F, 1.0F);
        else if (this.isRookie() || this.getType().is(key))
            this.playSound(DigiSounds.ROOKIE_STEPS.get(), 0.20F, 1.0F);
        else if (this.isUltimate())
            this.playSound(DigiSounds.ULTIMATE_STEPS.get(), 0.20F, 1.0F);
        else
            this.playSound(DigiSounds.CHAMPION_STEPS.get(), 0.20F, 1.0F);
    }

    //Animations

    public String animFileName = this.getLowerCaseSpecies() + "_anims";
    boolean sleeps = true;

    public void setAnimations(String ia, String sa, String wa, String fa, String ata, String sha) {
        if (ia != null) idleAnim = ia;
        if (sa != null) sitAnim = sa;
        if (wa != null) walkAnim = wa;
        if (fa != null) flyAnim = fa;
        if (ata != null) attackAnim = ata;
        if (sha != null) shootAnim = sha;

        sleeps = false;
        animFileName = "digimons_anims";

    }

    public String idleAnim = "idle";
    public String sitAnim = "sit";
    public String walkAnim = "walk";
    public String flyAnim = "fly";
    public String attackAnim = "attack";
    public String shootAnim = "shoot";

    protected AnimationController<DigimonEntity> getMovementController(){
        return animController(this);
    }


    public static <T extends DigimonEntity & GeoEntity> AnimationController<T> animController(T digimon) {
        return new AnimationController<>(digimon, "movement", 7, event -> {

            long time = digimon.level().getDayTime() % 24000;
            boolean night = digimon.sleeps && time > 13000 && time < 23000;

            if (digimon.isEvolving()) {
                event.getController().setAnimation(RawAnimation.begin().then("show", Animation.LoopType.LOOP));
                return PlayState.CONTINUE;
            }

            if (digimon.isMoving(event))
                event.getController().setAnimation(RawAnimation.begin().then(digimon.walkAnim, Animation.LoopType.LOOP));
            else
                if (digimon.isInSittingPose() || digimon.getVehicle() != null) {
                    event.getController().setAnimation(RawAnimation.begin().then(night ? "sleep" : digimon.sitAnim, Animation.LoopType.HOLD_ON_LAST_FRAME));
                    return PlayState.CONTINUE;
                } else {
                    event.getController().setAnimation(RawAnimation.begin().then(digimon.idleAnim, Animation.LoopType.LOOP));
                    return PlayState.CONTINUE;
                }
            return PlayState.CONTINUE;
        });
    }

    <T extends DigimonEntity & GeoEntity> boolean  isMoving(AnimationState<T> event){
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
