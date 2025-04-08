package net.minecraft.world.entity.animal.frog;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Dynamic;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Unit;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.VariantHolder;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.control.SmoothSwimmingMoveControl;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.navigation.AmphibiousPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.FrogVariant;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.AmphibiousNodeEvaluator;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.phys.Vec3;

public class Frog extends Animal implements VariantHolder<FrogVariant> {
   public static final Ingredient TEMPTATION_ITEM = Ingredient.of(Items.SLIME_BALL);
   protected static final ImmutableList<SensorType<? extends Sensor<? super Frog>>> SENSOR_TYPES = ImmutableList.of(SensorType.NEAREST_LIVING_ENTITIES, SensorType.HURT_BY, SensorType.FROG_ATTACKABLES, SensorType.FROG_TEMPTATIONS, SensorType.IS_IN_WATER);
   protected static final ImmutableList<MemoryModuleType<?>> MEMORY_TYPES = ImmutableList.of(MemoryModuleType.LOOK_TARGET, MemoryModuleType.NEAREST_LIVING_ENTITIES, MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, MemoryModuleType.WALK_TARGET, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryModuleType.PATH, MemoryModuleType.BREED_TARGET, MemoryModuleType.LONG_JUMP_COOLDOWN_TICKS, MemoryModuleType.LONG_JUMP_MID_JUMP, MemoryModuleType.ATTACK_TARGET, MemoryModuleType.TEMPTING_PLAYER, MemoryModuleType.TEMPTATION_COOLDOWN_TICKS, MemoryModuleType.IS_TEMPTED, MemoryModuleType.HURT_BY, MemoryModuleType.HURT_BY_ENTITY, MemoryModuleType.NEAREST_ATTACKABLE, MemoryModuleType.IS_IN_WATER, MemoryModuleType.IS_PREGNANT, MemoryModuleType.IS_PANICKING, MemoryModuleType.UNREACHABLE_TONGUE_TARGETS);
   private static final EntityDataAccessor<FrogVariant> DATA_VARIANT_ID = SynchedEntityData.defineId(Frog.class, EntityDataSerializers.FROG_VARIANT);
   private static final EntityDataAccessor<OptionalInt> DATA_TONGUE_TARGET_ID = SynchedEntityData.defineId(Frog.class, EntityDataSerializers.OPTIONAL_UNSIGNED_INT);
   private static final int FROG_FALL_DAMAGE_REDUCTION = 5;
   public static final String VARIANT_KEY = "variant";
   public final AnimationState jumpAnimationState = new AnimationState();
   public final AnimationState croakAnimationState = new AnimationState();
   public final AnimationState tongueAnimationState = new AnimationState();
   public final AnimationState swimIdleAnimationState = new AnimationState();

   public Frog(EntityType<? extends Animal> p_218470_, Level p_218471_) {
      super(p_218470_, p_218471_);
      this.lookControl = new Frog.FrogLookControl(this);
      this.setPathfindingMalus(BlockPathTypes.WATER, 4.0F);
      this.setPathfindingMalus(BlockPathTypes.TRAPDOOR, -1.0F);
      this.moveControl = new SmoothSwimmingMoveControl(this, 85, 10, 0.02F, 0.1F, true);
      this.setMaxUpStep(1.0F);
   }

   protected Brain.Provider<Frog> brainProvider() {
      return Brain.provider(MEMORY_TYPES, SENSOR_TYPES);
   }

   protected Brain<?> makeBrain(Dynamic<?> p_218494_) {
      return FrogAi.makeBrain(this.brainProvider().makeBrain(p_218494_));
   }

   public Brain<Frog> getBrain() {
      return (Brain<Frog>)super.getBrain();
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_VARIANT_ID, FrogVariant.TEMPERATE);
      this.entityData.define(DATA_TONGUE_TARGET_ID, OptionalInt.empty());
   }

   public void eraseTongueTarget() {
      this.entityData.set(DATA_TONGUE_TARGET_ID, OptionalInt.empty());
   }

   public Optional<Entity> getTongueTarget() {
      return this.entityData.get(DATA_TONGUE_TARGET_ID).stream().mapToObj(this.level()::getEntity).filter(Objects::nonNull).findFirst();
   }

   public void setTongueTarget(Entity p_218482_) {
      this.entityData.set(DATA_TONGUE_TARGET_ID, OptionalInt.of(p_218482_.getId()));
   }

   public int getHeadRotSpeed() {
      return 35;
   }

   public int getMaxHeadYRot() {
      return 5;
   }

   public FrogVariant getVariant() {
      return this.entityData.get(DATA_VARIANT_ID);
   }

   public void setVariant(FrogVariant p_218484_) {
      this.entityData.set(DATA_VARIANT_ID, p_218484_);
   }

   public void addAdditionalSaveData(CompoundTag p_218508_) {
      super.addAdditionalSaveData(p_218508_);
      p_218508_.putString("variant", BuiltInRegistries.FROG_VARIANT.getKey(this.getVariant()).toString());
   }

   public void readAdditionalSaveData(CompoundTag p_218496_) {
      super.readAdditionalSaveData(p_218496_);
      FrogVariant frogvariant = BuiltInRegistries.FROG_VARIANT.get(ResourceLocation.tryParse(p_218496_.getString("variant")));
      if (frogvariant != null) {
         this.setVariant(frogvariant);
      }

   }

   public boolean canBreatheUnderwater() {
      return true;
   }

   protected void customServerAiStep() {
      this.level().getProfiler().push("frogBrain");
      this.getBrain().tick((ServerLevel)this.level(), this);
      this.level().getProfiler().pop();
      this.level().getProfiler().push("frogActivityUpdate");
      FrogAi.updateActivity(this);
      this.level().getProfiler().pop();
      super.customServerAiStep();
   }

   public void tick() {
      if (this.level().isClientSide()) {
         this.swimIdleAnimationState.animateWhen(this.isInWaterOrBubble() && !this.walkAnimation.isMoving(), this.tickCount);
      }

      super.tick();
   }

   public void onSyncedDataUpdated(EntityDataAccessor<?> p_218498_) {
      if (DATA_POSE.equals(p_218498_)) {
         Pose pose = this.getPose();
         if (pose == Pose.LONG_JUMPING) {
            this.jumpAnimationState.start(this.tickCount);
         } else {
            this.jumpAnimationState.stop();
         }

         if (pose == Pose.CROAKING) {
            this.croakAnimationState.start(this.tickCount);
         } else {
            this.croakAnimationState.stop();
         }

         if (pose == Pose.USING_TONGUE) {
            this.tongueAnimationState.start(this.tickCount);
         } else {
            this.tongueAnimationState.stop();
         }
      }

      super.onSyncedDataUpdated(p_218498_);
   }

   protected void updateWalkAnimation(float p_268239_) {
      float f;
      if (this.jumpAnimationState.isStarted()) {
         f = 0.0F;
      } else {
         f = Math.min(p_268239_ * 25.0F, 1.0F);
      }

      this.walkAnimation.update(f, 0.4F);
   }

   @Nullable
   public AgeableMob getBreedOffspring(ServerLevel p_218476_, AgeableMob p_218477_) {
      Frog frog = EntityType.FROG.create(p_218476_);
      if (frog != null) {
         FrogAi.initMemories(frog, p_218476_.getRandom());
      }

      return frog;
   }

   public boolean isBaby() {
      return false;
   }

   public void setBaby(boolean p_218500_) {
   }

   public void spawnChildFromBreeding(ServerLevel p_218479_, Animal p_218480_) {
      this.finalizeSpawnChildFromBreeding(p_218479_, p_218480_, (AgeableMob)null);
      this.getBrain().setMemory(MemoryModuleType.IS_PREGNANT, Unit.INSTANCE);
   }

   public SpawnGroupData finalizeSpawn(ServerLevelAccessor p_218488_, DifficultyInstance p_218489_, MobSpawnType p_218490_, @Nullable SpawnGroupData p_218491_, @Nullable CompoundTag p_218492_) {
      Holder<Biome> holder = p_218488_.getBiome(this.blockPosition());
      if (holder.is(BiomeTags.SPAWNS_COLD_VARIANT_FROGS)) {
         this.setVariant(FrogVariant.COLD);
      } else if (holder.is(BiomeTags.SPAWNS_WARM_VARIANT_FROGS)) {
         this.setVariant(FrogVariant.WARM);
      } else {
         this.setVariant(FrogVariant.TEMPERATE);
      }

      FrogAi.initMemories(this, p_218488_.getRandom());
      return super.finalizeSpawn(p_218488_, p_218489_, p_218490_, p_218491_, p_218492_);
   }

   public static AttributeSupplier.Builder createAttributes() {
      return Mob.createMobAttributes().add(Attributes.MOVEMENT_SPEED, 1.0D).add(Attributes.MAX_HEALTH, 10.0D).add(Attributes.ATTACK_DAMAGE, 10.0D);
   }

   @Nullable
   protected SoundEvent getAmbientSound() {
      return SoundEvents.FROG_AMBIENT;
   }

   @Nullable
   protected SoundEvent getHurtSound(DamageSource p_218510_) {
      return SoundEvents.FROG_HURT;
   }

   @Nullable
   protected SoundEvent getDeathSound() {
      return SoundEvents.FROG_DEATH;
   }

   protected void playStepSound(BlockPos p_218505_, BlockState p_218506_) {
      this.playSound(SoundEvents.FROG_STEP, 0.15F, 1.0F);
   }

   public boolean isPushedByFluid() {
      return false;
   }

   protected void sendDebugPackets() {
      super.sendDebugPackets();
      DebugPackets.sendEntityBrain(this);
   }

   protected int calculateFallDamage(float p_218519_, float p_218520_) {
      return super.calculateFallDamage(p_218519_, p_218520_) - 5;
   }

   public void travel(Vec3 p_218530_) {
      if (this.isControlledByLocalInstance() && this.isInWater()) {
         this.moveRelative(this.getSpeed(), p_218530_);
         this.move(MoverType.SELF, this.getDeltaMovement());
         this.setDeltaMovement(this.getDeltaMovement().scale(0.9D));
      } else {
         super.travel(p_218530_);
      }

   }

   public static boolean canEat(LivingEntity p_218533_) {
      if (p_218533_ instanceof Slime slime) {
         if (slime.getSize() != 1) {
            return false;
         }
      }

      return p_218533_.getType().is(EntityTypeTags.FROG_FOOD);
   }

   protected PathNavigation createNavigation(Level p_218486_) {
      return new Frog.FrogPathNavigation(this, p_218486_);
   }

   public boolean isFood(ItemStack p_218535_) {
      return TEMPTATION_ITEM.test(p_218535_);
   }

   public static boolean checkFrogSpawnRules(EntityType<? extends Animal> p_218512_, LevelAccessor p_218513_, MobSpawnType p_218514_, BlockPos p_218515_, RandomSource p_218516_) {
      return p_218513_.getBlockState(p_218515_.below()).is(BlockTags.FROGS_SPAWNABLE_ON) && isBrightEnoughToSpawn(p_218513_, p_218515_);
   }

   class FrogLookControl extends LookControl {
      FrogLookControl(Mob p_218544_) {
         super(p_218544_);
      }

      protected boolean resetXRotOnTick() {
         return Frog.this.getTongueTarget().isEmpty();
      }
   }

   static class FrogNodeEvaluator extends AmphibiousNodeEvaluator {
      private final BlockPos.MutableBlockPos belowPos = new BlockPos.MutableBlockPos();

      public FrogNodeEvaluator(boolean p_218548_) {
         super(p_218548_);
      }

      public Node getStart() {
         return !this.mob.isInWater() ? super.getStart() : this.getStartNode(new BlockPos(Mth.floor(this.mob.getBoundingBox().minX), Mth.floor(this.mob.getBoundingBox().minY), Mth.floor(this.mob.getBoundingBox().minZ)));
      }

      public BlockPathTypes getBlockPathType(BlockGetter p_218551_, int p_218552_, int p_218553_, int p_218554_) {
         this.belowPos.set(p_218552_, p_218553_ - 1, p_218554_);
         BlockState blockstate = p_218551_.getBlockState(this.belowPos);
         return blockstate.is(BlockTags.FROG_PREFER_JUMP_TO) ? BlockPathTypes.OPEN : super.getBlockPathType(p_218551_, p_218552_, p_218553_, p_218554_);
      }
   }

   static class FrogPathNavigation extends AmphibiousPathNavigation {
      FrogPathNavigation(Frog p_218556_, Level p_218557_) {
         super(p_218556_, p_218557_);
      }

      public boolean canCutCorner(BlockPathTypes p_265335_) {
         return p_265335_ != BlockPathTypes.WATER_BORDER && super.canCutCorner(p_265335_);
      }

      protected PathFinder createPathFinder(int p_218559_) {
         this.nodeEvaluator = new Frog.FrogNodeEvaluator(true);
         this.nodeEvaluator.setCanPassDoors(true);
         return new PathFinder(this.nodeEvaluator, p_218559_);
      }
   }
}