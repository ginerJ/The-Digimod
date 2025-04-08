package net.minecraft.world.entity.animal.axolotl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.IntFunction;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LerpingModel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.VariantHolder;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.SmoothSwimmingLookControl;
import net.minecraft.world.entity.ai.control.SmoothSwimmingMoveControl;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.navigation.AmphibiousPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Bucketable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public class Axolotl extends Animal implements LerpingModel, VariantHolder<Axolotl.Variant>, Bucketable {
   public static final int TOTAL_PLAYDEAD_TIME = 200;
   protected static final ImmutableList<? extends SensorType<? extends Sensor<? super Axolotl>>> SENSOR_TYPES = ImmutableList.of(SensorType.NEAREST_LIVING_ENTITIES, SensorType.NEAREST_ADULT, SensorType.HURT_BY, SensorType.AXOLOTL_ATTACKABLES, SensorType.AXOLOTL_TEMPTATIONS);
   protected static final ImmutableList<? extends MemoryModuleType<?>> MEMORY_TYPES = ImmutableList.of(MemoryModuleType.BREED_TARGET, MemoryModuleType.NEAREST_LIVING_ENTITIES, MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, MemoryModuleType.NEAREST_VISIBLE_PLAYER, MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYER, MemoryModuleType.LOOK_TARGET, MemoryModuleType.WALK_TARGET, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryModuleType.PATH, MemoryModuleType.ATTACK_TARGET, MemoryModuleType.ATTACK_COOLING_DOWN, MemoryModuleType.NEAREST_VISIBLE_ADULT, MemoryModuleType.HURT_BY_ENTITY, MemoryModuleType.PLAY_DEAD_TICKS, MemoryModuleType.NEAREST_ATTACKABLE, MemoryModuleType.TEMPTING_PLAYER, MemoryModuleType.TEMPTATION_COOLDOWN_TICKS, MemoryModuleType.IS_TEMPTED, MemoryModuleType.HAS_HUNTING_COOLDOWN, MemoryModuleType.IS_PANICKING);
   private static final EntityDataAccessor<Integer> DATA_VARIANT = SynchedEntityData.defineId(Axolotl.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Boolean> DATA_PLAYING_DEAD = SynchedEntityData.defineId(Axolotl.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> FROM_BUCKET = SynchedEntityData.defineId(Axolotl.class, EntityDataSerializers.BOOLEAN);
   public static final double PLAYER_REGEN_DETECTION_RANGE = 20.0D;
   public static final int RARE_VARIANT_CHANCE = 1200;
   private static final int AXOLOTL_TOTAL_AIR_SUPPLY = 6000;
   public static final String VARIANT_TAG = "Variant";
   private static final int REHYDRATE_AIR_SUPPLY = 1800;
   private static final int REGEN_BUFF_MAX_DURATION = 2400;
   private final Map<String, Vector3f> modelRotationValues = Maps.newHashMap();
   private static final int REGEN_BUFF_BASE_DURATION = 100;

   public Axolotl(EntityType<? extends Axolotl> p_149105_, Level p_149106_) {
      super(p_149105_, p_149106_);
      this.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
      this.moveControl = new Axolotl.AxolotlMoveControl(this);
      this.lookControl = new Axolotl.AxolotlLookControl(this, 20);
      this.setMaxUpStep(1.0F);
   }

   public Map<String, Vector3f> getModelRotationValues() {
      return this.modelRotationValues;
   }

   public float getWalkTargetValue(BlockPos p_149140_, LevelReader p_149141_) {
      return 0.0F;
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_VARIANT, 0);
      this.entityData.define(DATA_PLAYING_DEAD, false);
      this.entityData.define(FROM_BUCKET, false);
   }

   public void addAdditionalSaveData(CompoundTag p_149158_) {
      super.addAdditionalSaveData(p_149158_);
      p_149158_.putInt("Variant", this.getVariant().getId());
      p_149158_.putBoolean("FromBucket", this.fromBucket());
   }

   public void readAdditionalSaveData(CompoundTag p_149145_) {
      super.readAdditionalSaveData(p_149145_);
      this.setVariant(Axolotl.Variant.byId(p_149145_.getInt("Variant")));
      this.setFromBucket(p_149145_.getBoolean("FromBucket"));
   }

   public void playAmbientSound() {
      if (!this.isPlayingDead()) {
         super.playAmbientSound();
      }
   }

   public SpawnGroupData finalizeSpawn(ServerLevelAccessor p_149132_, DifficultyInstance p_149133_, MobSpawnType p_149134_, @Nullable SpawnGroupData p_149135_, @Nullable CompoundTag p_149136_) {
      boolean flag = false;
      if (p_149134_ == MobSpawnType.BUCKET) {
         return p_149135_;
      } else {
         RandomSource randomsource = p_149132_.getRandom();
         if (p_149135_ instanceof Axolotl.AxolotlGroupData) {
            if (((Axolotl.AxolotlGroupData)p_149135_).getGroupSize() >= 2) {
               flag = true;
            }
         } else {
            p_149135_ = new Axolotl.AxolotlGroupData(Axolotl.Variant.getCommonSpawnVariant(randomsource), Axolotl.Variant.getCommonSpawnVariant(randomsource));
         }

         this.setVariant(((Axolotl.AxolotlGroupData)p_149135_).getVariant(randomsource));
         if (flag) {
            this.setAge(-24000);
         }

         return super.finalizeSpawn(p_149132_, p_149133_, p_149134_, p_149135_, p_149136_);
      }
   }

   public void baseTick() {
      int i = this.getAirSupply();
      super.baseTick();
      if (!this.isNoAi()) {
         this.handleAirSupply(i);
      }

   }

   protected void handleAirSupply(int p_149194_) {
      if (this.isAlive() && !this.isInWaterRainOrBubble()) {
         this.setAirSupply(p_149194_ - 1);
         if (this.getAirSupply() == -20) {
            this.setAirSupply(0);
            this.hurt(this.damageSources().dryOut(), 2.0F);
         }
      } else {
         this.setAirSupply(this.getMaxAirSupply());
      }

   }

   public void rehydrate() {
      int i = this.getAirSupply() + 1800;
      this.setAirSupply(Math.min(i, this.getMaxAirSupply()));
   }

   public int getMaxAirSupply() {
      return 6000;
   }

   public Axolotl.Variant getVariant() {
      return Axolotl.Variant.byId(this.entityData.get(DATA_VARIANT));
   }

   public void setVariant(Axolotl.Variant p_149118_) {
      this.entityData.set(DATA_VARIANT, p_149118_.getId());
   }

   private static boolean useRareVariant(RandomSource p_218436_) {
      return p_218436_.nextInt(1200) == 0;
   }

   public boolean checkSpawnObstruction(LevelReader p_149130_) {
      return p_149130_.isUnobstructed(this);
   }

   public boolean canBreatheUnderwater() {
      return true;
   }

   public boolean isPushedByFluid() {
      return false;
   }

   public MobType getMobType() {
      return MobType.WATER;
   }

   public void setPlayingDead(boolean p_149199_) {
      this.entityData.set(DATA_PLAYING_DEAD, p_149199_);
   }

   public boolean isPlayingDead() {
      return this.entityData.get(DATA_PLAYING_DEAD);
   }

   public boolean fromBucket() {
      return this.entityData.get(FROM_BUCKET);
   }

   public void setFromBucket(boolean p_149196_) {
      this.entityData.set(FROM_BUCKET, p_149196_);
   }

   @Nullable
   public AgeableMob getBreedOffspring(ServerLevel p_149112_, AgeableMob p_149113_) {
      Axolotl axolotl = EntityType.AXOLOTL.create(p_149112_);
      if (axolotl != null) {
         Axolotl.Variant axolotl$variant;
         if (useRareVariant(this.random)) {
            axolotl$variant = Axolotl.Variant.getRareSpawnVariant(this.random);
         } else {
            axolotl$variant = this.random.nextBoolean() ? this.getVariant() : ((Axolotl)p_149113_).getVariant();
         }

         axolotl.setVariant(axolotl$variant);
         axolotl.setPersistenceRequired();
      }

      return axolotl;
   }

   public double getMeleeAttackRangeSqr(LivingEntity p_149185_) {
      return 1.5D + (double)p_149185_.getBbWidth() * 2.0D;
   }

   public boolean isFood(ItemStack p_149189_) {
      return p_149189_.is(ItemTags.AXOLOTL_TEMPT_ITEMS);
   }

   public boolean canBeLeashed(Player p_149122_) {
      return true;
   }

   protected void customServerAiStep() {
      this.level().getProfiler().push("axolotlBrain");
      this.getBrain().tick((ServerLevel)this.level(), this);
      this.level().getProfiler().pop();
      this.level().getProfiler().push("axolotlActivityUpdate");
      AxolotlAi.updateActivity(this);
      this.level().getProfiler().pop();
      if (!this.isNoAi()) {
         Optional<Integer> optional = this.getBrain().getMemory(MemoryModuleType.PLAY_DEAD_TICKS);
         this.setPlayingDead(optional.isPresent() && optional.get() > 0);
      }

   }

   public static AttributeSupplier.Builder createAttributes() {
      return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 14.0D).add(Attributes.MOVEMENT_SPEED, 1.0D).add(Attributes.ATTACK_DAMAGE, 2.0D);
   }

   protected PathNavigation createNavigation(Level p_149128_) {
      return new AmphibiousPathNavigation(this, p_149128_);
   }

   public boolean doHurtTarget(Entity p_149201_) {
      boolean flag = p_149201_.hurt(this.damageSources().mobAttack(this), (float)((int)this.getAttributeValue(Attributes.ATTACK_DAMAGE)));
      if (flag) {
         this.doEnchantDamageEffects(this, p_149201_);
         this.playSound(SoundEvents.AXOLOTL_ATTACK, 1.0F, 1.0F);
      }

      return flag;
   }

   public boolean hurt(DamageSource p_149115_, float p_149116_) {
      float f = this.getHealth();
      if (!this.level().isClientSide && !this.isNoAi() && this.level().random.nextInt(3) == 0 && ((float)this.level().random.nextInt(3) < p_149116_ || f / this.getMaxHealth() < 0.5F) && p_149116_ < f && this.isInWater() && (p_149115_.getEntity() != null || p_149115_.getDirectEntity() != null) && !this.isPlayingDead()) {
         this.brain.setMemory(MemoryModuleType.PLAY_DEAD_TICKS, 200);
      }

      return super.hurt(p_149115_, p_149116_);
   }

   protected float getStandingEyeHeight(Pose p_149152_, EntityDimensions p_149153_) {
      return p_149153_.height * 0.655F;
   }

   public int getMaxHeadXRot() {
      return 1;
   }

   public int getMaxHeadYRot() {
      return 1;
   }

   public InteractionResult mobInteract(Player p_149155_, InteractionHand p_149156_) {
      return Bucketable.bucketMobPickup(p_149155_, p_149156_, this).orElse(super.mobInteract(p_149155_, p_149156_));
   }

   public void saveToBucketTag(ItemStack p_149187_) {
      Bucketable.saveDefaultDataToBucketTag(this, p_149187_);
      CompoundTag compoundtag = p_149187_.getOrCreateTag();
      compoundtag.putInt("Variant", this.getVariant().getId());
      compoundtag.putInt("Age", this.getAge());
      Brain<?> brain = this.getBrain();
      if (brain.hasMemoryValue(MemoryModuleType.HAS_HUNTING_COOLDOWN)) {
         compoundtag.putLong("HuntingCooldown", brain.getTimeUntilExpiry(MemoryModuleType.HAS_HUNTING_COOLDOWN));
      }

   }

   public void loadFromBucketTag(CompoundTag p_149163_) {
      Bucketable.loadDefaultDataFromBucketTag(this, p_149163_);
      this.setVariant(Axolotl.Variant.byId(p_149163_.getInt("Variant")));
      if (p_149163_.contains("Age")) {
         this.setAge(p_149163_.getInt("Age"));
      }

      if (p_149163_.contains("HuntingCooldown")) {
         this.getBrain().setMemoryWithExpiry(MemoryModuleType.HAS_HUNTING_COOLDOWN, true, p_149163_.getLong("HuntingCooldown"));
      }

   }

   public ItemStack getBucketItemStack() {
      return new ItemStack(Items.AXOLOTL_BUCKET);
   }

   public SoundEvent getPickupSound() {
      return SoundEvents.BUCKET_FILL_AXOLOTL;
   }

   public boolean canBeSeenAsEnemy() {
      return !this.isPlayingDead() && super.canBeSeenAsEnemy();
   }

   public static void onStopAttacking(Axolotl p_218444_, LivingEntity p_218445_) {
      Level level = p_218444_.level();
      if (p_218445_.isDeadOrDying()) {
         DamageSource damagesource = p_218445_.getLastDamageSource();
         if (damagesource != null) {
            Entity entity = damagesource.getEntity();
            if (entity != null && entity.getType() == EntityType.PLAYER) {
               Player player = (Player)entity;
               List<Player> list = level.getEntitiesOfClass(Player.class, p_218444_.getBoundingBox().inflate(20.0D));
               if (list.contains(player)) {
                  p_218444_.applySupportingEffects(player);
               }
            }
         }
      }

   }

   public void applySupportingEffects(Player p_149174_) {
      MobEffectInstance mobeffectinstance = p_149174_.getEffect(MobEffects.REGENERATION);
      if (mobeffectinstance == null || mobeffectinstance.endsWithin(2399)) {
         int i = mobeffectinstance != null ? mobeffectinstance.getDuration() : 0;
         int j = Math.min(2400, 100 + i);
         p_149174_.addEffect(new MobEffectInstance(MobEffects.REGENERATION, j, 0), this);
      }

      p_149174_.removeEffect(MobEffects.DIG_SLOWDOWN);
   }

   public boolean requiresCustomPersistence() {
      return super.requiresCustomPersistence() || this.fromBucket();
   }

   protected SoundEvent getHurtSound(DamageSource p_149161_) {
      return SoundEvents.AXOLOTL_HURT;
   }

   @Nullable
   protected SoundEvent getDeathSound() {
      return SoundEvents.AXOLOTL_DEATH;
   }

   @Nullable
   protected SoundEvent getAmbientSound() {
      return this.isInWater() ? SoundEvents.AXOLOTL_IDLE_WATER : SoundEvents.AXOLOTL_IDLE_AIR;
   }

   protected SoundEvent getSwimSplashSound() {
      return SoundEvents.AXOLOTL_SPLASH;
   }

   protected SoundEvent getSwimSound() {
      return SoundEvents.AXOLOTL_SWIM;
   }

   protected Brain.Provider<Axolotl> brainProvider() {
      return Brain.provider(MEMORY_TYPES, SENSOR_TYPES);
   }

   protected Brain<?> makeBrain(Dynamic<?> p_149138_) {
      return AxolotlAi.makeBrain(this.brainProvider().makeBrain(p_149138_));
   }

   public Brain<Axolotl> getBrain() {
      return (Brain<Axolotl>)super.getBrain();
   }

   protected void sendDebugPackets() {
      super.sendDebugPackets();
      DebugPackets.sendEntityBrain(this);
   }

   public void travel(Vec3 p_149181_) {
      if (this.isControlledByLocalInstance() && this.isInWater()) {
         this.moveRelative(this.getSpeed(), p_149181_);
         this.move(MoverType.SELF, this.getDeltaMovement());
         this.setDeltaMovement(this.getDeltaMovement().scale(0.9D));
      } else {
         super.travel(p_149181_);
      }

   }

   protected void usePlayerItem(Player p_149124_, InteractionHand p_149125_, ItemStack p_149126_) {
      if (p_149126_.is(Items.TROPICAL_FISH_BUCKET)) {
         p_149124_.setItemInHand(p_149125_, new ItemStack(Items.WATER_BUCKET));
      } else {
         super.usePlayerItem(p_149124_, p_149125_, p_149126_);
      }

   }

   public boolean removeWhenFarAway(double p_149183_) {
      return !this.fromBucket() && !this.hasCustomName();
   }

   public static boolean checkAxolotlSpawnRules(EntityType<? extends LivingEntity> p_218438_, ServerLevelAccessor p_218439_, MobSpawnType p_218440_, BlockPos p_218441_, RandomSource p_218442_) {
      return p_218439_.getBlockState(p_218441_.below()).is(BlockTags.AXOLOTLS_SPAWNABLE_ON);
   }

   public static class AxolotlGroupData extends AgeableMob.AgeableMobGroupData {
      public final Axolotl.Variant[] types;

      public AxolotlGroupData(Axolotl.Variant... p_149204_) {
         super(false);
         this.types = p_149204_;
      }

      public Axolotl.Variant getVariant(RandomSource p_218447_) {
         return this.types[p_218447_.nextInt(this.types.length)];
      }
   }

   class AxolotlLookControl extends SmoothSwimmingLookControl {
      public AxolotlLookControl(Axolotl p_149210_, int p_149211_) {
         super(p_149210_, p_149211_);
      }

      public void tick() {
         if (!Axolotl.this.isPlayingDead()) {
            super.tick();
         }

      }
   }

   static class AxolotlMoveControl extends SmoothSwimmingMoveControl {
      private final Axolotl axolotl;

      public AxolotlMoveControl(Axolotl p_149215_) {
         super(p_149215_, 85, 10, 0.1F, 0.5F, false);
         this.axolotl = p_149215_;
      }

      public void tick() {
         if (!this.axolotl.isPlayingDead()) {
            super.tick();
         }

      }
   }

   public static enum Variant implements StringRepresentable {
      LUCY(0, "lucy", true),
      WILD(1, "wild", true),
      GOLD(2, "gold", true),
      CYAN(3, "cyan", true),
      BLUE(4, "blue", false);

      private static final IntFunction<Axolotl.Variant> BY_ID = ByIdMap.continuous(Axolotl.Variant::getId, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
      public static final Codec<Axolotl.Variant> CODEC = StringRepresentable.fromEnum(Axolotl.Variant::values);
      private final int id;
      private final String name;
      private final boolean common;

      private Variant(int p_149239_, String p_149240_, boolean p_149241_) {
         this.id = p_149239_;
         this.name = p_149240_;
         this.common = p_149241_;
      }

      public int getId() {
         return this.id;
      }

      public String getName() {
         return this.name;
      }

      public String getSerializedName() {
         return this.name;
      }

      public static Axolotl.Variant byId(int p_262930_) {
         return BY_ID.apply(p_262930_);
      }

      public static Axolotl.Variant getCommonSpawnVariant(RandomSource p_218449_) {
         return getSpawnVariant(p_218449_, true);
      }

      public static Axolotl.Variant getRareSpawnVariant(RandomSource p_218454_) {
         return getSpawnVariant(p_218454_, false);
      }

      private static Axolotl.Variant getSpawnVariant(RandomSource p_218451_, boolean p_218452_) {
         Axolotl.Variant[] aaxolotl$variant = Arrays.stream(values()).filter((p_149252_) -> {
            return p_149252_.common == p_218452_;
         }).toArray((p_149244_) -> {
            return new Axolotl.Variant[p_149244_];
         });
         return Util.getRandom(aaxolotl$variant, p_218451_);
      }
   }
}