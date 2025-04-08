package net.minecraft.world.entity.animal.allay;

import com.google.common.collect.ImmutableList;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiConsumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.GameEventTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.npc.InventoryCarrier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.DynamicGameEventListener;
import net.minecraft.world.level.gameevent.EntityPositionSource;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEventListener;
import net.minecraft.world.level.gameevent.PositionSource;
import net.minecraft.world.level.gameevent.vibrations.VibrationSystem;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class Allay extends PathfinderMob implements InventoryCarrier, VibrationSystem {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Vec3i ITEM_PICKUP_REACH = new Vec3i(1, 1, 1);
   private static final int LIFTING_ITEM_ANIMATION_DURATION = 5;
   private static final float DANCING_LOOP_DURATION = 55.0F;
   private static final float SPINNING_ANIMATION_DURATION = 15.0F;
   private static final Ingredient DUPLICATION_ITEM = Ingredient.of(Items.AMETHYST_SHARD);
   private static final int DUPLICATION_COOLDOWN_TICKS = 6000;
   private static final int NUM_OF_DUPLICATION_HEARTS = 3;
   private static final double RIDING_OFFSET = 0.4D;
   private static final EntityDataAccessor<Boolean> DATA_DANCING = SynchedEntityData.defineId(Allay.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> DATA_CAN_DUPLICATE = SynchedEntityData.defineId(Allay.class, EntityDataSerializers.BOOLEAN);
   protected static final ImmutableList<SensorType<? extends Sensor<? super Allay>>> SENSOR_TYPES = ImmutableList.of(SensorType.NEAREST_LIVING_ENTITIES, SensorType.NEAREST_PLAYERS, SensorType.HURT_BY, SensorType.NEAREST_ITEMS);
   protected static final ImmutableList<MemoryModuleType<?>> MEMORY_TYPES = ImmutableList.of(MemoryModuleType.PATH, MemoryModuleType.LOOK_TARGET, MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, MemoryModuleType.WALK_TARGET, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryModuleType.HURT_BY, MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM, MemoryModuleType.LIKED_PLAYER, MemoryModuleType.LIKED_NOTEBLOCK_POSITION, MemoryModuleType.LIKED_NOTEBLOCK_COOLDOWN_TICKS, MemoryModuleType.ITEM_PICKUP_COOLDOWN_TICKS, MemoryModuleType.IS_PANICKING);
   public static final ImmutableList<Float> THROW_SOUND_PITCHES = ImmutableList.of(0.5625F, 0.625F, 0.75F, 0.9375F, 1.0F, 1.0F, 1.125F, 1.25F, 1.5F, 1.875F, 2.0F, 2.25F, 2.5F, 3.0F, 3.75F, 4.0F);
   private final DynamicGameEventListener<VibrationSystem.Listener> dynamicVibrationListener;
   private VibrationSystem.Data vibrationData;
   private final VibrationSystem.User vibrationUser;
   private final DynamicGameEventListener<Allay.JukeboxListener> dynamicJukeboxListener;
   private final SimpleContainer inventory = new SimpleContainer(1);
   private @Nullable BlockPos jukeboxPos;
   private long duplicationCooldown;
   private float holdingItemAnimationTicks;
   private float holdingItemAnimationTicks0;
   private float dancingAnimationTicks;
   private float spinningAnimationTicks;
   private float spinningAnimationTicks0;

   public Allay(EntityType<? extends Allay> p_218310_, Level p_218311_) {
      super(p_218310_, p_218311_);
      this.moveControl = new FlyingMoveControl(this, 20, true);
      this.setCanPickUpLoot(this.canPickUpLoot());
      this.vibrationUser = new Allay.VibrationUser();
      this.vibrationData = new VibrationSystem.Data();
      this.dynamicVibrationListener = new DynamicGameEventListener<>(new VibrationSystem.Listener(this));
      this.dynamicJukeboxListener = new DynamicGameEventListener<>(new Allay.JukeboxListener(this.vibrationUser.getPositionSource(), GameEvent.JUKEBOX_PLAY.getNotificationRadius()));
   }

   protected Brain.Provider<Allay> brainProvider() {
      return Brain.provider(MEMORY_TYPES, SENSOR_TYPES);
   }

   protected Brain<?> makeBrain(Dynamic<?> p_218344_) {
      return AllayAi.makeBrain(this.brainProvider().makeBrain(p_218344_));
   }

   public Brain<Allay> getBrain() {
      return (Brain<Allay>)super.getBrain();
   }

   public static AttributeSupplier.Builder createAttributes() {
      return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 20.0D).add(Attributes.FLYING_SPEED, (double)0.1F).add(Attributes.MOVEMENT_SPEED, (double)0.1F).add(Attributes.ATTACK_DAMAGE, 2.0D).add(Attributes.FOLLOW_RANGE, 48.0D);
   }

   protected PathNavigation createNavigation(Level p_218342_) {
      FlyingPathNavigation flyingpathnavigation = new FlyingPathNavigation(this, p_218342_);
      flyingpathnavigation.setCanOpenDoors(false);
      flyingpathnavigation.setCanFloat(true);
      flyingpathnavigation.setCanPassDoors(true);
      return flyingpathnavigation;
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_DANCING, false);
      this.entityData.define(DATA_CAN_DUPLICATE, true);
   }

   public void travel(Vec3 p_218382_) {
      if (this.isControlledByLocalInstance()) {
         if (this.isInWater()) {
            this.moveRelative(0.02F, p_218382_);
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale((double)0.8F));
         } else if (this.isInLava()) {
            this.moveRelative(0.02F, p_218382_);
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(0.5D));
         } else {
            this.moveRelative(this.getSpeed(), p_218382_);
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale((double)0.91F));
         }
      }

      this.calculateEntityAnimation(false);
   }

   protected float getStandingEyeHeight(Pose p_218356_, EntityDimensions p_218357_) {
      return p_218357_.height * 0.6F;
   }

   public boolean hurt(DamageSource p_218339_, float p_218340_) {
      Entity $$3 = p_218339_.getEntity();
      if ($$3 instanceof Player player) {
         Optional<UUID> optional = this.getBrain().getMemory(MemoryModuleType.LIKED_PLAYER);
         if (optional.isPresent() && player.getUUID().equals(optional.get())) {
            return false;
         }
      }

      return super.hurt(p_218339_, p_218340_);
   }

   protected void playStepSound(BlockPos p_218364_, BlockState p_218365_) {
   }

   protected void checkFallDamage(double p_218316_, boolean p_218317_, BlockState p_218318_, BlockPos p_218319_) {
   }

   protected SoundEvent getAmbientSound() {
      return this.hasItemInSlot(EquipmentSlot.MAINHAND) ? SoundEvents.ALLAY_AMBIENT_WITH_ITEM : SoundEvents.ALLAY_AMBIENT_WITHOUT_ITEM;
   }

   protected SoundEvent getHurtSound(DamageSource p_218369_) {
      return SoundEvents.ALLAY_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ALLAY_DEATH;
   }

   protected float getSoundVolume() {
      return 0.4F;
   }

   protected void customServerAiStep() {
      this.level().getProfiler().push("allayBrain");
      this.getBrain().tick((ServerLevel)this.level(), this);
      this.level().getProfiler().pop();
      this.level().getProfiler().push("allayActivityUpdate");
      AllayAi.updateActivity(this);
      this.level().getProfiler().pop();
      super.customServerAiStep();
   }

   public void aiStep() {
      super.aiStep();
      if (!this.level().isClientSide && this.isAlive() && this.tickCount % 10 == 0) {
         this.heal(1.0F);
      }

      if (this.isDancing() && this.shouldStopDancing() && this.tickCount % 20 == 0) {
         this.setDancing(false);
         this.jukeboxPos = null;
      }

      this.updateDuplicationCooldown();
   }

   public void tick() {
      super.tick();
      if (this.level().isClientSide) {
         this.holdingItemAnimationTicks0 = this.holdingItemAnimationTicks;
         if (this.hasItemInHand()) {
            this.holdingItemAnimationTicks = Mth.clamp(this.holdingItemAnimationTicks + 1.0F, 0.0F, 5.0F);
         } else {
            this.holdingItemAnimationTicks = Mth.clamp(this.holdingItemAnimationTicks - 1.0F, 0.0F, 5.0F);
         }

         if (this.isDancing()) {
            ++this.dancingAnimationTicks;
            this.spinningAnimationTicks0 = this.spinningAnimationTicks;
            if (this.isSpinning()) {
               ++this.spinningAnimationTicks;
            } else {
               --this.spinningAnimationTicks;
            }

            this.spinningAnimationTicks = Mth.clamp(this.spinningAnimationTicks, 0.0F, 15.0F);
         } else {
            this.dancingAnimationTicks = 0.0F;
            this.spinningAnimationTicks = 0.0F;
            this.spinningAnimationTicks0 = 0.0F;
         }
      } else {
         VibrationSystem.Ticker.tick(this.level(), this.vibrationData, this.vibrationUser);
         if (this.isPanicking()) {
            this.setDancing(false);
         }
      }

   }

   public boolean canPickUpLoot() {
      return !this.isOnPickupCooldown() && this.hasItemInHand();
   }

   public boolean hasItemInHand() {
      return !this.getItemInHand(InteractionHand.MAIN_HAND).isEmpty();
   }

   public boolean canTakeItem(ItemStack p_218380_) {
      return false;
   }

   private boolean isOnPickupCooldown() {
      return this.getBrain().checkMemory(MemoryModuleType.ITEM_PICKUP_COOLDOWN_TICKS, MemoryStatus.VALUE_PRESENT);
   }

   protected InteractionResult mobInteract(Player p_218361_, InteractionHand p_218362_) {
      ItemStack itemstack = p_218361_.getItemInHand(p_218362_);
      ItemStack itemstack1 = this.getItemInHand(InteractionHand.MAIN_HAND);
      if (this.isDancing() && this.isDuplicationItem(itemstack) && this.canDuplicate()) {
         this.duplicateAllay();
         this.level().broadcastEntityEvent(this, (byte)18);
         this.level().playSound(p_218361_, this, SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.NEUTRAL, 2.0F, 1.0F);
         this.removeInteractionItem(p_218361_, itemstack);
         return InteractionResult.SUCCESS;
      } else if (itemstack1.isEmpty() && !itemstack.isEmpty()) {
         ItemStack itemstack3 = itemstack.copyWithCount(1);
         this.setItemInHand(InteractionHand.MAIN_HAND, itemstack3);
         this.removeInteractionItem(p_218361_, itemstack);
         this.level().playSound(p_218361_, this, SoundEvents.ALLAY_ITEM_GIVEN, SoundSource.NEUTRAL, 2.0F, 1.0F);
         this.getBrain().setMemory(MemoryModuleType.LIKED_PLAYER, p_218361_.getUUID());
         return InteractionResult.SUCCESS;
      } else if (!itemstack1.isEmpty() && p_218362_ == InteractionHand.MAIN_HAND && itemstack.isEmpty()) {
         this.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
         this.level().playSound(p_218361_, this, SoundEvents.ALLAY_ITEM_TAKEN, SoundSource.NEUTRAL, 2.0F, 1.0F);
         this.swing(InteractionHand.MAIN_HAND);

         for(ItemStack itemstack2 : this.getInventory().removeAllItems()) {
            BehaviorUtils.throwItem(this, itemstack2, this.position());
         }

         this.getBrain().eraseMemory(MemoryModuleType.LIKED_PLAYER);
         p_218361_.addItem(itemstack1);
         return InteractionResult.SUCCESS;
      } else {
         return super.mobInteract(p_218361_, p_218362_);
      }
   }

   public void setJukeboxPlaying(BlockPos p_240102_, boolean p_240103_) {
      if (p_240103_) {
         if (!this.isDancing()) {
            this.jukeboxPos = p_240102_;
            this.setDancing(true);
         }
      } else if (p_240102_.equals(this.jukeboxPos) || this.jukeboxPos == null) {
         this.jukeboxPos = null;
         this.setDancing(false);
      }

   }

   public SimpleContainer getInventory() {
      return this.inventory;
   }

   protected Vec3i getPickupReach() {
      return ITEM_PICKUP_REACH;
   }

   public boolean wantsToPickUp(ItemStack p_218387_) {
      ItemStack itemstack = this.getItemInHand(InteractionHand.MAIN_HAND);
      return !itemstack.isEmpty() && this.inventory.canAddItem(p_218387_) && this.allayConsidersItemEqual(itemstack, p_218387_) && net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.level(), this);
   }

   private boolean allayConsidersItemEqual(ItemStack p_252278_, ItemStack p_250405_) {
      return ItemStack.isSameItem(p_252278_, p_250405_) && !this.hasNonMatchingPotion(p_252278_, p_250405_);
   }

   private boolean hasNonMatchingPotion(ItemStack p_248762_, ItemStack p_250839_) {
      CompoundTag compoundtag = p_248762_.getTag();
      boolean flag = compoundtag != null && compoundtag.contains("Potion");
      if (!flag) {
         return false;
      } else {
         CompoundTag compoundtag1 = p_250839_.getTag();
         boolean flag1 = compoundtag1 != null && compoundtag1.contains("Potion");
         if (!flag1) {
            return true;
         } else {
            Tag tag = compoundtag.get("Potion");
            Tag tag1 = compoundtag1.get("Potion");
            return tag != null && tag1 != null && !tag.equals(tag1);
         }
      }
   }

   protected void pickUpItem(ItemEntity p_218359_) {
      InventoryCarrier.pickUpItem(this, this, p_218359_);
   }

   protected void sendDebugPackets() {
      super.sendDebugPackets();
      DebugPackets.sendEntityBrain(this);
   }

   public boolean isFlapping() {
      return !this.onGround();
   }

   public void updateDynamicGameEventListener(BiConsumer<DynamicGameEventListener<?>, ServerLevel> p_218348_) {
      Level level = this.level();
      if (level instanceof ServerLevel serverlevel) {
         p_218348_.accept(this.dynamicVibrationListener, serverlevel);
         p_218348_.accept(this.dynamicJukeboxListener, serverlevel);
      }

   }

   public boolean isDancing() {
      return this.entityData.get(DATA_DANCING);
   }

   public boolean isPanicking() {
      return this.brain.getMemory(MemoryModuleType.IS_PANICKING).isPresent();
   }

   public void setDancing(boolean p_240178_) {
      if (!this.level().isClientSide && this.isEffectiveAi() && (!p_240178_ || !this.isPanicking())) {
         this.entityData.set(DATA_DANCING, p_240178_);
      }
   }

   private boolean shouldStopDancing() {
      return this.jukeboxPos == null || !this.jukeboxPos.closerToCenterThan(this.position(), (double)GameEvent.JUKEBOX_PLAY.getNotificationRadius()) || !this.level().getBlockState(this.jukeboxPos).is(Blocks.JUKEBOX);
   }

   public float getHoldingItemAnimationProgress(float p_218395_) {
      return Mth.lerp(p_218395_, this.holdingItemAnimationTicks0, this.holdingItemAnimationTicks) / 5.0F;
   }

   public boolean isSpinning() {
      float f = this.dancingAnimationTicks % 55.0F;
      return f < 15.0F;
   }

   public float getSpinningProgress(float p_240057_) {
      return Mth.lerp(p_240057_, this.spinningAnimationTicks0, this.spinningAnimationTicks) / 15.0F;
   }

   public boolean equipmentHasChanged(ItemStack p_249825_, ItemStack p_251595_) {
      return !this.allayConsidersItemEqual(p_249825_, p_251595_);
   }

   protected void dropEquipment() {
      super.dropEquipment();
      this.inventory.removeAllItems().forEach(this::spawnAtLocation);
      ItemStack itemstack = this.getItemBySlot(EquipmentSlot.MAINHAND);
      if (!itemstack.isEmpty() && !EnchantmentHelper.hasVanishingCurse(itemstack)) {
         this.spawnAtLocation(itemstack);
         this.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
      }

   }

   public boolean removeWhenFarAway(double p_218384_) {
      return false;
   }

   public void addAdditionalSaveData(CompoundTag p_218367_) {
      super.addAdditionalSaveData(p_218367_);
      this.writeInventoryToTag(p_218367_);
      VibrationSystem.Data.CODEC.encodeStart(NbtOps.INSTANCE, this.vibrationData).resultOrPartial(LOGGER::error).ifPresent((p_218353_) -> {
         p_218367_.put("listener", p_218353_);
      });
      p_218367_.putLong("DuplicationCooldown", this.duplicationCooldown);
      p_218367_.putBoolean("CanDuplicate", this.canDuplicate());
   }

   public void readAdditionalSaveData(CompoundTag p_218350_) {
      super.readAdditionalSaveData(p_218350_);
      this.readInventoryFromTag(p_218350_);
      if (p_218350_.contains("listener", 10)) {
         VibrationSystem.Data.CODEC.parse(new Dynamic<>(NbtOps.INSTANCE, p_218350_.getCompound("listener"))).resultOrPartial(LOGGER::error).ifPresent((p_281082_) -> {
            this.vibrationData = p_281082_;
         });
      }

      this.duplicationCooldown = (long)p_218350_.getInt("DuplicationCooldown");
      this.entityData.set(DATA_CAN_DUPLICATE, p_218350_.getBoolean("CanDuplicate"));
   }

   protected boolean shouldStayCloseToLeashHolder() {
      return false;
   }

   private void updateDuplicationCooldown() {
      if (this.duplicationCooldown > 0L) {
         --this.duplicationCooldown;
      }

      if (!this.level().isClientSide() && this.duplicationCooldown == 0L && !this.canDuplicate()) {
         this.entityData.set(DATA_CAN_DUPLICATE, true);
      }

   }

   private boolean isDuplicationItem(ItemStack p_239736_) {
      return DUPLICATION_ITEM.test(p_239736_);
   }

   private void duplicateAllay() {
      Allay allay = EntityType.ALLAY.create(this.level());
      if (allay != null) {
         allay.moveTo(this.position());
         allay.setPersistenceRequired();
         allay.resetDuplicationCooldown();
         this.resetDuplicationCooldown();
         this.level().addFreshEntity(allay);
      }

   }

   private void resetDuplicationCooldown() {
      this.duplicationCooldown = 6000L;
      this.entityData.set(DATA_CAN_DUPLICATE, false);
   }

   private boolean canDuplicate() {
      return this.entityData.get(DATA_CAN_DUPLICATE);
   }

   private void removeInteractionItem(Player p_239359_, ItemStack p_239360_) {
      if (!p_239359_.getAbilities().instabuild) {
         p_239360_.shrink(1);
      }

   }

   public Vec3 getLeashOffset() {
      return new Vec3(0.0D, (double)this.getEyeHeight() * 0.6D, (double)this.getBbWidth() * 0.1D);
   }

   public double getMyRidingOffset() {
      return 0.4D;
   }

   public void handleEntityEvent(byte p_239347_) {
      if (p_239347_ == 18) {
         for(int i = 0; i < 3; ++i) {
            this.spawnHeartParticle();
         }
      } else {
         super.handleEntityEvent(p_239347_);
      }

   }

   private void spawnHeartParticle() {
      double d0 = this.random.nextGaussian() * 0.02D;
      double d1 = this.random.nextGaussian() * 0.02D;
      double d2 = this.random.nextGaussian() * 0.02D;
      this.level().addParticle(ParticleTypes.HEART, this.getRandomX(1.0D), this.getRandomY() + 0.5D, this.getRandomZ(1.0D), d0, d1, d2);
   }

   public VibrationSystem.Data getVibrationData() {
      return this.vibrationData;
   }

   public VibrationSystem.User getVibrationUser() {
      return this.vibrationUser;
   }

   class JukeboxListener implements GameEventListener {
      private final PositionSource listenerSource;
      private final int listenerRadius;

      public JukeboxListener(PositionSource p_239448_, int p_239449_) {
         this.listenerSource = p_239448_;
         this.listenerRadius = p_239449_;
      }

      public PositionSource getListenerSource() {
         return this.listenerSource;
      }

      public int getListenerRadius() {
         return this.listenerRadius;
      }

      public boolean handleGameEvent(ServerLevel p_250009_, GameEvent p_251513_, GameEvent.Context p_249478_, Vec3 p_250852_) {
         if (p_251513_ == GameEvent.JUKEBOX_PLAY) {
            Allay.this.setJukeboxPlaying(BlockPos.containing(p_250852_), true);
            return true;
         } else if (p_251513_ == GameEvent.JUKEBOX_STOP_PLAY) {
            Allay.this.setJukeboxPlaying(BlockPos.containing(p_250852_), false);
            return true;
         } else {
            return false;
         }
      }
   }

   class VibrationUser implements VibrationSystem.User {
      private static final int VIBRATION_EVENT_LISTENER_RANGE = 16;
      private final PositionSource positionSource = new EntityPositionSource(Allay.this, Allay.this.getEyeHeight());

      public int getListenerRadius() {
         return 16;
      }

      public PositionSource getPositionSource() {
         return this.positionSource;
      }

      public boolean canReceiveVibration(ServerLevel p_282038_, BlockPos p_283385_, GameEvent p_283669_, GameEvent.Context p_282208_) {
         if (Allay.this.isNoAi()) {
            return false;
         } else {
            Optional<GlobalPos> optional = Allay.this.getBrain().getMemory(MemoryModuleType.LIKED_NOTEBLOCK_POSITION);
            if (optional.isEmpty()) {
               return true;
            } else {
               GlobalPos globalpos = optional.get();
               return globalpos.dimension().equals(p_282038_.dimension()) && globalpos.pos().equals(p_283385_);
            }
         }
      }

      public void onReceiveVibration(ServerLevel p_281422_, BlockPos p_281449_, GameEvent p_281488_, @Nullable Entity p_281794_, @Nullable Entity p_281864_, float p_281642_) {
         if (p_281488_ == GameEvent.NOTE_BLOCK_PLAY) {
            AllayAi.hearNoteblock(Allay.this, new BlockPos(p_281449_));
         }

      }

      public TagKey<GameEvent> getListenableEvents() {
         return GameEventTags.ALLAY_CAN_LISTEN;
      }
   }
}
