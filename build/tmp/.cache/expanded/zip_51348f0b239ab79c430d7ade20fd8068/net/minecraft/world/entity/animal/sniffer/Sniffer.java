package net.minecraft.world.entity.animal.sniffer;

import com.mojang.serialization.Dynamic;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class Sniffer extends Animal {
   private static final int DIGGING_PARTICLES_DELAY_TICKS = 1700;
   private static final int DIGGING_PARTICLES_DURATION_TICKS = 6000;
   private static final int DIGGING_PARTICLES_AMOUNT = 30;
   private static final int DIGGING_DROP_SEED_OFFSET_TICKS = 120;
   private static final int SNIFFER_BABY_AGE_TICKS = 48000;
   private static final float DIGGING_BB_HEIGHT_OFFSET = 0.4F;
   private static final EntityDimensions DIGGING_DIMENSIONS = EntityDimensions.scalable(EntityType.SNIFFER.getWidth(), EntityType.SNIFFER.getHeight() - 0.4F);
   private static final EntityDataAccessor<Sniffer.State> DATA_STATE = SynchedEntityData.defineId(Sniffer.class, EntityDataSerializers.SNIFFER_STATE);
   private static final EntityDataAccessor<Integer> DATA_DROP_SEED_AT_TICK = SynchedEntityData.defineId(Sniffer.class, EntityDataSerializers.INT);
   public final AnimationState feelingHappyAnimationState = new AnimationState();
   public final AnimationState scentingAnimationState = new AnimationState();
   public final AnimationState sniffingAnimationState = new AnimationState();
   public final AnimationState diggingAnimationState = new AnimationState();
   public final AnimationState risingAnimationState = new AnimationState();

   public static AttributeSupplier.Builder createAttributes() {
      return Mob.createMobAttributes().add(Attributes.MOVEMENT_SPEED, (double)0.1F).add(Attributes.MAX_HEALTH, 14.0D);
   }

   public Sniffer(EntityType<? extends Animal> p_273717_, Level p_273562_) {
      super(p_273717_, p_273562_);
      this.entityData.define(DATA_STATE, Sniffer.State.IDLING);
      this.entityData.define(DATA_DROP_SEED_AT_TICK, 0);
      this.getNavigation().setCanFloat(true);
      this.setPathfindingMalus(BlockPathTypes.WATER, -1.0F);
      this.setPathfindingMalus(BlockPathTypes.DANGER_POWDER_SNOW, -1.0F);
      this.setPathfindingMalus(BlockPathTypes.DAMAGE_CAUTIOUS, -1.0F);
   }

   protected float getStandingEyeHeight(Pose p_272721_, EntityDimensions p_273353_) {
      return this.getDimensions(p_272721_).height * 0.6F;
   }

   public void onPathfindingStart() {
      super.onPathfindingStart();
      if (this.isOnFire() || this.isInWater()) {
         this.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
      }

   }

   public void onPathfindingDone() {
      this.setPathfindingMalus(BlockPathTypes.WATER, -1.0F);
   }

   public EntityDimensions getDimensions(Pose p_286815_) {
      return this.entityData.hasItem(DATA_STATE) && this.getState() == Sniffer.State.DIGGING ? DIGGING_DIMENSIONS.scale(this.getScale()) : super.getDimensions(p_286815_);
   }

   public boolean isPanicking() {
      return this.brain.getMemory(MemoryModuleType.IS_PANICKING).isPresent();
   }

   public boolean isSearching() {
      return this.getState() == Sniffer.State.SEARCHING;
   }

   public boolean isTempted() {
      return this.brain.getMemory(MemoryModuleType.IS_TEMPTED).orElse(false);
   }

   public boolean canSniff() {
      return !this.isTempted() && !this.isPanicking() && !this.isInWater() && !this.isInLove() && this.onGround() && !this.isPassenger();
   }

   public boolean canPlayDiggingSound() {
      return this.getState() == Sniffer.State.DIGGING || this.getState() == Sniffer.State.SEARCHING;
   }

   private BlockPos getHeadBlock() {
      Vec3 vec3 = this.getHeadPosition();
      return BlockPos.containing(vec3.x(), this.getY() + (double)0.2F, vec3.z());
   }

   private Vec3 getHeadPosition() {
      return this.position().add(this.getForward().scale(2.25D));
   }

   private Sniffer.State getState() {
      return this.entityData.get(DATA_STATE);
   }

   private Sniffer setState(Sniffer.State p_273359_) {
      this.entityData.set(DATA_STATE, p_273359_);
      return this;
   }

   public void onSyncedDataUpdated(EntityDataAccessor<?> p_272936_) {
      if (DATA_STATE.equals(p_272936_)) {
         Sniffer.State sniffer$state = this.getState();
         this.resetAnimations();
         switch (sniffer$state) {
            case SCENTING:
               this.scentingAnimationState.startIfStopped(this.tickCount);
               break;
            case SNIFFING:
               this.sniffingAnimationState.startIfStopped(this.tickCount);
               break;
            case DIGGING:
               this.diggingAnimationState.startIfStopped(this.tickCount);
               break;
            case RISING:
               this.risingAnimationState.startIfStopped(this.tickCount);
               break;
            case FEELING_HAPPY:
               this.feelingHappyAnimationState.startIfStopped(this.tickCount);
         }

         this.refreshDimensions();
      }

      super.onSyncedDataUpdated(p_272936_);
   }

   private void resetAnimations() {
      this.diggingAnimationState.stop();
      this.sniffingAnimationState.stop();
      this.risingAnimationState.stop();
      this.feelingHappyAnimationState.stop();
      this.scentingAnimationState.stop();
   }

   public Sniffer transitionTo(Sniffer.State p_273096_) {
      switch (p_273096_) {
         case SCENTING:
            this.setState(Sniffer.State.SCENTING).onScentingStart();
            break;
         case SNIFFING:
            this.playSound(SoundEvents.SNIFFER_SNIFFING, 1.0F, 1.0F);
            this.setState(Sniffer.State.SNIFFING);
            break;
         case DIGGING:
            this.setState(Sniffer.State.DIGGING).onDiggingStart();
            break;
         case RISING:
            this.playSound(SoundEvents.SNIFFER_DIGGING_STOP, 1.0F, 1.0F);
            this.setState(Sniffer.State.RISING);
            break;
         case FEELING_HAPPY:
            this.playSound(SoundEvents.SNIFFER_HAPPY, 1.0F, 1.0F);
            this.setState(Sniffer.State.FEELING_HAPPY);
            break;
         case IDLING:
            this.setState(Sniffer.State.IDLING);
            break;
         case SEARCHING:
            this.setState(Sniffer.State.SEARCHING);
      }

      return this;
   }

   private Sniffer onScentingStart() {
      this.playSound(SoundEvents.SNIFFER_SCENTING, 1.0F, this.isBaby() ? 1.3F : 1.0F);
      return this;
   }

   private Sniffer onDiggingStart() {
      this.entityData.set(DATA_DROP_SEED_AT_TICK, this.tickCount + 120);
      this.level().broadcastEntityEvent(this, (byte)63);
      return this;
   }

   public Sniffer onDiggingComplete(boolean p_272677_) {
      if (p_272677_) {
         this.storeExploredPosition(this.getOnPos());
      }

      return this;
   }

   Optional<BlockPos> calculateDigPosition() {
      return IntStream.range(0, 5).mapToObj((p_273771_) -> {
         return LandRandomPos.getPos(this, 10 + 2 * p_273771_, 3);
      }).filter(Objects::nonNull).map(BlockPos::containing).filter((p_289451_) -> {
         return this.level().getWorldBorder().isWithinBounds(p_289451_);
      }).map(BlockPos::below).filter(this::canDig).findFirst();
   }

   boolean canDig() {
      return !this.isPanicking() && !this.isTempted() && !this.isBaby() && !this.isInWater() && this.onGround() && !this.isPassenger() && this.canDig(this.getHeadBlock().below());
   }

   private boolean canDig(BlockPos p_272757_) {
      return this.level().getBlockState(p_272757_).is(BlockTags.SNIFFER_DIGGABLE_BLOCK) && this.getExploredPositions().noneMatch((p_289453_) -> {
         return GlobalPos.of(this.level().dimension(), p_272757_).equals(p_289453_);
      }) && Optional.ofNullable(this.getNavigation().createPath(p_272757_, 1)).map(Path::canReach).orElse(false);
   }

   private void dropSeed() {
      if (!this.level().isClientSide() && this.entityData.get(DATA_DROP_SEED_AT_TICK) == this.tickCount) {
         ServerLevel serverlevel = (ServerLevel)this.level();
         LootTable loottable = serverlevel.getServer().getLootData().getLootTable(BuiltInLootTables.SNIFFER_DIGGING);
         LootParams lootparams = (new LootParams.Builder(serverlevel)).withParameter(LootContextParams.ORIGIN, this.getHeadPosition()).withParameter(LootContextParams.THIS_ENTITY, this).create(LootContextParamSets.GIFT);
         List<ItemStack> list = loottable.getRandomItems(lootparams);
         BlockPos blockpos = this.getHeadBlock();

         for(ItemStack itemstack : list) {
            ItemEntity itementity = new ItemEntity(serverlevel, (double)blockpos.getX(), (double)blockpos.getY(), (double)blockpos.getZ(), itemstack);
            itementity.setDefaultPickUpDelay();
            serverlevel.addFreshEntity(itementity);
         }

         this.playSound(SoundEvents.SNIFFER_DROP_SEED, 1.0F, 1.0F);
      }
   }

   private Sniffer emitDiggingParticles(AnimationState p_273528_) {
      boolean flag = p_273528_.getAccumulatedTime() > 1700L && p_273528_.getAccumulatedTime() < 6000L;
      if (flag) {
         BlockPos blockpos = this.getHeadBlock();
         BlockState blockstate = this.level().getBlockState(blockpos.below());
         if (blockstate.getRenderShape() != RenderShape.INVISIBLE) {
            for(int i = 0; i < 30; ++i) {
               Vec3 vec3 = Vec3.atCenterOf(blockpos).add(0.0D, (double)-0.65F, 0.0D);
               this.level().addParticle(new BlockParticleOption(ParticleTypes.BLOCK, blockstate), vec3.x, vec3.y, vec3.z, 0.0D, 0.0D, 0.0D);
            }

            if (this.tickCount % 10 == 0) {
               this.level().playLocalSound(this.getX(), this.getY(), this.getZ(), blockstate.getSoundType(level(), blockpos.below(), this).getHitSound(), this.getSoundSource(), 0.5F, 0.5F, false);
            }
         }
      }

      if (this.tickCount % 10 == 0) {
         this.level().gameEvent(GameEvent.ENTITY_SHAKE, this.getHeadBlock(), GameEvent.Context.of(this));
      }

      return this;
   }

   private Sniffer storeExploredPosition(BlockPos p_273015_) {
      List<GlobalPos> list = this.getExploredPositions().limit(20L).collect(Collectors.toList());
      list.add(0, GlobalPos.of(this.level().dimension(), p_273015_));
      this.getBrain().setMemory(MemoryModuleType.SNIFFER_EXPLORED_POSITIONS, list);
      return this;
   }

   private Stream<GlobalPos> getExploredPositions() {
      return this.getBrain().getMemory(MemoryModuleType.SNIFFER_EXPLORED_POSITIONS).stream().flatMap(Collection::stream);
   }

   protected void jumpFromGround() {
      super.jumpFromGround();
      double d0 = this.moveControl.getSpeedModifier();
      if (d0 > 0.0D) {
         double d1 = this.getDeltaMovement().horizontalDistanceSqr();
         if (d1 < 0.01D) {
            this.moveRelative(0.1F, new Vec3(0.0D, 0.0D, 1.0D));
         }
      }

   }

   public void spawnChildFromBreeding(ServerLevel p_277923_, Animal p_277857_) {
      ItemStack itemstack = new ItemStack(Items.SNIFFER_EGG);
      ItemEntity itementity = new ItemEntity(p_277923_, this.position().x(), this.position().y(), this.position().z(), itemstack);
      itementity.setDefaultPickUpDelay();
      this.finalizeSpawnChildFromBreeding(p_277923_, p_277857_, (AgeableMob)null);
      this.playSound(SoundEvents.SNIFFER_EGG_PLOP, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 0.5F);
      p_277923_.addFreshEntity(itementity);
   }

   public void die(DamageSource p_277689_) {
      this.transitionTo(Sniffer.State.IDLING);
      super.die(p_277689_);
   }

   public void tick() {
      switch (this.getState()) {
         case DIGGING:
            this.emitDiggingParticles(this.diggingAnimationState).dropSeed();
            break;
         case SEARCHING:
            this.playSearchingSound();
      }

      super.tick();
   }

   public InteractionResult mobInteract(Player p_273046_, InteractionHand p_272687_) {
      ItemStack itemstack = p_273046_.getItemInHand(p_272687_);
      boolean flag = this.isFood(itemstack);
      InteractionResult interactionresult = super.mobInteract(p_273046_, p_272687_);
      if (interactionresult.consumesAction() && flag) {
         this.level().playSound((Player)null, this, this.getEatingSound(itemstack), SoundSource.NEUTRAL, 1.0F, Mth.randomBetween(this.level().random, 0.8F, 1.2F));
      }

      return interactionresult;
   }

   public double getPassengersRidingOffset() {
      return 1.8D;
   }

   public float getNameTagOffsetY() {
      return super.getNameTagOffsetY() + 0.3F;
   }

   private void playSearchingSound() {
      if (this.level().isClientSide() && this.tickCount % 20 == 0) {
         this.level().playLocalSound(this.getX(), this.getY(), this.getZ(), SoundEvents.SNIFFER_SEARCHING, this.getSoundSource(), 1.0F, 1.0F, false);
      }

   }

   protected void playStepSound(BlockPos p_272953_, BlockState p_273729_) {
      this.playSound(SoundEvents.SNIFFER_STEP, 0.15F, 1.0F);
   }

   public SoundEvent getEatingSound(ItemStack p_272747_) {
      return SoundEvents.SNIFFER_EAT;
   }

   protected SoundEvent getAmbientSound() {
      return Set.of(Sniffer.State.DIGGING, Sniffer.State.SEARCHING).contains(this.getState()) ? null : SoundEvents.SNIFFER_IDLE;
   }

   protected SoundEvent getHurtSound(DamageSource p_273718_) {
      return SoundEvents.SNIFFER_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.SNIFFER_DEATH;
   }

   public int getMaxHeadYRot() {
      return 50;
   }

   public void setBaby(boolean p_272995_) {
      this.setAge(p_272995_ ? -48000 : 0);
   }

   public AgeableMob getBreedOffspring(ServerLevel p_273401_, AgeableMob p_273310_) {
      return EntityType.SNIFFER.create(p_273401_);
   }

   public boolean canMate(Animal p_272966_) {
      if (!(p_272966_ instanceof Sniffer sniffer)) {
         return false;
      } else {
         Set<Sniffer.State> set = Set.of(Sniffer.State.IDLING, Sniffer.State.SCENTING, Sniffer.State.FEELING_HAPPY);
         return set.contains(this.getState()) && set.contains(sniffer.getState()) && super.canMate(p_272966_);
      }
   }

   public AABB getBoundingBoxForCulling() {
      return super.getBoundingBoxForCulling().inflate((double)0.6F);
   }

   public boolean isFood(ItemStack p_273659_) {
      return p_273659_.is(ItemTags.SNIFFER_FOOD);
   }

   protected Brain<?> makeBrain(Dynamic<?> p_273174_) {
      return SnifferAi.makeBrain(this.brainProvider().makeBrain(p_273174_));
   }

   public Brain<Sniffer> getBrain() {
      return (Brain<Sniffer>)super.getBrain();
   }

   protected Brain.Provider<Sniffer> brainProvider() {
      return Brain.provider(SnifferAi.MEMORY_TYPES, SnifferAi.SENSOR_TYPES);
   }

   protected void customServerAiStep() {
      this.level().getProfiler().push("snifferBrain");
      this.getBrain().tick((ServerLevel)this.level(), this);
      this.level().getProfiler().popPush("snifferActivityUpdate");
      SnifferAi.updateActivity(this);
      this.level().getProfiler().pop();
      super.customServerAiStep();
   }

   protected void sendDebugPackets() {
      super.sendDebugPackets();
      DebugPackets.sendEntityBrain(this);
   }

   public static enum State {
      IDLING,
      FEELING_HAPPY,
      SCENTING,
      SNIFFING,
      SEARCHING,
      DIGGING,
      RISING;
   }
}
