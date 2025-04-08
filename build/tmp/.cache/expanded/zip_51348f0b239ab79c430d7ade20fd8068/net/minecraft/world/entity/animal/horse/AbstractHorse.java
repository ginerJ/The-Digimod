package net.minecraft.world.entity.animal.horse;

import java.util.UUID;
import java.util.function.DoubleSupplier;
import java.util.function.IntUnaryOperator;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.OldUsersConverter;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerListener;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HasCustomInventoryScreen;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.PlayerRideableJumping;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.Saddleable;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStandGoal;
import net.minecraft.world.entity.ai.goal.RunAroundLikeCrazyGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.DismountHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public abstract class AbstractHorse extends Animal implements ContainerListener, HasCustomInventoryScreen, OwnableEntity, PlayerRideableJumping, Saddleable {
   public static final int EQUIPMENT_SLOT_OFFSET = 400;
   public static final int CHEST_SLOT_OFFSET = 499;
   public static final int INVENTORY_SLOT_OFFSET = 500;
   public static final double BREEDING_CROSS_FACTOR = 0.15D;
   private static final float MIN_MOVEMENT_SPEED = (float)generateSpeed(() -> {
      return 0.0D;
   });
   private static final float MAX_MOVEMENT_SPEED = (float)generateSpeed(() -> {
      return 1.0D;
   });
   private static final float MIN_JUMP_STRENGTH = (float)generateJumpStrength(() -> {
      return 0.0D;
   });
   private static final float MAX_JUMP_STRENGTH = (float)generateJumpStrength(() -> {
      return 1.0D;
   });
   private static final float MIN_HEALTH = generateMaxHealth((p_272505_) -> {
      return 0;
   });
   private static final float MAX_HEALTH = generateMaxHealth((p_272504_) -> {
      return p_272504_ - 1;
   });
   private static final float BACKWARDS_MOVE_SPEED_FACTOR = 0.25F;
   private static final float SIDEWAYS_MOVE_SPEED_FACTOR = 0.5F;
   private static final Predicate<LivingEntity> PARENT_HORSE_SELECTOR = (p_30636_) -> {
      return p_30636_ instanceof AbstractHorse && ((AbstractHorse)p_30636_).isBred();
   };
   private static final TargetingConditions MOMMY_TARGETING = TargetingConditions.forNonCombat().range(16.0D).ignoreLineOfSight().selector(PARENT_HORSE_SELECTOR);
   private static final Ingredient FOOD_ITEMS = Ingredient.of(Items.WHEAT, Items.SUGAR, Blocks.HAY_BLOCK.asItem(), Items.APPLE, Items.GOLDEN_CARROT, Items.GOLDEN_APPLE, Items.ENCHANTED_GOLDEN_APPLE);
   private static final EntityDataAccessor<Byte> DATA_ID_FLAGS = SynchedEntityData.defineId(AbstractHorse.class, EntityDataSerializers.BYTE);
   private static final int FLAG_TAME = 2;
   private static final int FLAG_SADDLE = 4;
   private static final int FLAG_BRED = 8;
   private static final int FLAG_EATING = 16;
   private static final int FLAG_STANDING = 32;
   private static final int FLAG_OPEN_MOUTH = 64;
   public static final int INV_SLOT_SADDLE = 0;
   public static final int INV_SLOT_ARMOR = 1;
   public static final int INV_BASE_COUNT = 2;
   private int eatingCounter;
   private int mouthCounter;
   private int standCounter;
   public int tailCounter;
   public int sprintCounter;
   protected boolean isJumping;
   protected SimpleContainer inventory;
   protected int temper;
   protected float playerJumpPendingScale;
   protected boolean allowStandSliding;
   private float eatAnim;
   private float eatAnimO;
   private float standAnim;
   private float standAnimO;
   private float mouthAnim;
   private float mouthAnimO;
   protected boolean canGallop = true;
   protected int gallopSoundCounter;
   @Nullable
   private UUID owner;

   protected AbstractHorse(EntityType<? extends AbstractHorse> p_30531_, Level p_30532_) {
      super(p_30531_, p_30532_);
      this.setMaxUpStep(1.0F);
      this.createInventory();
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(1, new PanicGoal(this, 1.2D));
      this.goalSelector.addGoal(1, new RunAroundLikeCrazyGoal(this, 1.2D));
      this.goalSelector.addGoal(2, new BreedGoal(this, 1.0D, AbstractHorse.class));
      this.goalSelector.addGoal(4, new FollowParentGoal(this, 1.0D));
      this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 0.7D));
      this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0F));
      this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
      if (this.canPerformRearing()) {
         this.goalSelector.addGoal(9, new RandomStandGoal(this));
      }

      this.addBehaviourGoals();
   }

   protected void addBehaviourGoals() {
      this.goalSelector.addGoal(0, new FloatGoal(this));
      this.goalSelector.addGoal(3, new TemptGoal(this, 1.25D, Ingredient.of(Items.GOLDEN_CARROT, Items.GOLDEN_APPLE, Items.ENCHANTED_GOLDEN_APPLE), false));
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_ID_FLAGS, (byte)0);
   }

   protected boolean getFlag(int p_30648_) {
      return (this.entityData.get(DATA_ID_FLAGS) & p_30648_) != 0;
   }

   protected void setFlag(int p_30598_, boolean p_30599_) {
      byte b0 = this.entityData.get(DATA_ID_FLAGS);
      if (p_30599_) {
         this.entityData.set(DATA_ID_FLAGS, (byte)(b0 | p_30598_));
      } else {
         this.entityData.set(DATA_ID_FLAGS, (byte)(b0 & ~p_30598_));
      }

   }

   public boolean isTamed() {
      return this.getFlag(2);
   }

   @Nullable
   public UUID getOwnerUUID() {
      return this.owner;
   }

   public void setOwnerUUID(@Nullable UUID p_30587_) {
      this.owner = p_30587_;
   }

   public boolean isJumping() {
      return this.isJumping;
   }

   public void setTamed(boolean p_30652_) {
      this.setFlag(2, p_30652_);
   }

   public void setIsJumping(boolean p_30656_) {
      this.isJumping = p_30656_;
   }

   protected void onLeashDistance(float p_30660_) {
      if (p_30660_ > 6.0F && this.isEating()) {
         this.setEating(false);
      }

   }

   public boolean isEating() {
      return this.getFlag(16);
   }

   public boolean isStanding() {
      return this.getFlag(32);
   }

   public boolean isBred() {
      return this.getFlag(8);
   }

   public void setBred(boolean p_30658_) {
      this.setFlag(8, p_30658_);
   }

   public boolean isSaddleable() {
      return this.isAlive() && !this.isBaby() && this.isTamed();
   }

   public void equipSaddle(@Nullable SoundSource p_30546_) {
      this.inventory.setItem(0, new ItemStack(Items.SADDLE));
   }

   public void equipArmor(Player p_251330_, ItemStack p_248855_) {
      if (this.isArmor(p_248855_)) {
         this.inventory.setItem(1, p_248855_.copyWithCount(1));
         if (!p_251330_.getAbilities().instabuild) {
            p_248855_.shrink(1);
         }
      }

   }

   public boolean isSaddled() {
      return this.getFlag(4);
   }

   public int getTemper() {
      return this.temper;
   }

   public void setTemper(int p_30650_) {
      this.temper = p_30650_;
   }

   public int modifyTemper(int p_30654_) {
      int i = Mth.clamp(this.getTemper() + p_30654_, 0, this.getMaxTemper());
      this.setTemper(i);
      return i;
   }

   public boolean isPushable() {
      return !this.isVehicle();
   }

   private void eating() {
      this.openMouth();
      if (!this.isSilent()) {
         SoundEvent soundevent = this.getEatingSound();
         if (soundevent != null) {
            this.level().playSound((Player)null, this.getX(), this.getY(), this.getZ(), soundevent, this.getSoundSource(), 1.0F, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F);
         }
      }

   }

   public boolean causeFallDamage(float p_149499_, float p_149500_, DamageSource p_149501_) {
      if (p_149499_ > 1.0F) {
         this.playSound(SoundEvents.HORSE_LAND, 0.4F, 1.0F);
      }

      int i = this.calculateFallDamage(p_149499_, p_149500_);
      if (i <= 0) {
         return false;
      } else {
         this.hurt(p_149501_, (float)i);
         if (this.isVehicle()) {
            for(Entity entity : this.getIndirectPassengers()) {
               entity.hurt(p_149501_, (float)i);
            }
         }

         this.playBlockFallSound();
         return true;
      }
   }

   protected int calculateFallDamage(float p_30606_, float p_30607_) {
      return Mth.ceil((p_30606_ * 0.5F - 3.0F) * p_30607_);
   }

   protected int getInventorySize() {
      return 2;
   }

   protected void createInventory() {
      SimpleContainer simplecontainer = this.inventory;
      this.inventory = new SimpleContainer(this.getInventorySize());
      if (simplecontainer != null) {
         simplecontainer.removeListener(this);
         int i = Math.min(simplecontainer.getContainerSize(), this.inventory.getContainerSize());

         for(int j = 0; j < i; ++j) {
            ItemStack itemstack = simplecontainer.getItem(j);
            if (!itemstack.isEmpty()) {
               this.inventory.setItem(j, itemstack.copy());
            }
         }
      }

      this.inventory.addListener(this);
      this.updateContainerEquipment();
      this.itemHandler = net.minecraftforge.common.util.LazyOptional.of(() -> new net.minecraftforge.items.wrapper.InvWrapper(this.inventory));
   }

   protected void updateContainerEquipment() {
      if (!this.level().isClientSide) {
         this.setFlag(4, !this.inventory.getItem(0).isEmpty());
      }
   }

   public void containerChanged(Container p_30548_) {
      boolean flag = this.isSaddled();
      this.updateContainerEquipment();
      if (this.tickCount > 20 && !flag && this.isSaddled()) {
         this.playSound(this.getSaddleSoundEvent(), 0.5F, 1.0F);
      }

   }

   public double getCustomJump() {
      return this.getAttributeValue(Attributes.JUMP_STRENGTH);
   }

   public boolean hurt(DamageSource p_252258_, float p_250984_) {
      boolean flag = super.hurt(p_252258_, p_250984_);
      if (flag && this.random.nextInt(3) == 0) {
         this.standIfPossible();
      }

      return flag;
   }

   protected boolean canPerformRearing() {
      return true;
   }

   @Nullable
   protected SoundEvent getEatingSound() {
      return null;
   }

   @Nullable
   protected SoundEvent getAngrySound() {
      return null;
   }

   protected void playStepSound(BlockPos p_30584_, BlockState p_30585_) {
      if (!p_30585_.liquid()) {
         BlockState blockstate = this.level().getBlockState(p_30584_.above());
         SoundType soundtype = p_30585_.getSoundType(level(), p_30584_, this);
         if (blockstate.is(Blocks.SNOW)) {
            soundtype = blockstate.getSoundType(level(), p_30584_, this);
         }

         if (this.isVehicle() && this.canGallop) {
            ++this.gallopSoundCounter;
            if (this.gallopSoundCounter > 5 && this.gallopSoundCounter % 3 == 0) {
               this.playGallopSound(soundtype);
            } else if (this.gallopSoundCounter <= 5) {
               this.playSound(SoundEvents.HORSE_STEP_WOOD, soundtype.getVolume() * 0.15F, soundtype.getPitch());
            }
         } else if (this.isWoodSoundType(soundtype)) {
            this.playSound(SoundEvents.HORSE_STEP_WOOD, soundtype.getVolume() * 0.15F, soundtype.getPitch());
         } else {
            this.playSound(SoundEvents.HORSE_STEP, soundtype.getVolume() * 0.15F, soundtype.getPitch());
         }

      }
   }

   private boolean isWoodSoundType(SoundType p_278280_) {
      return p_278280_ == SoundType.WOOD || p_278280_ == SoundType.NETHER_WOOD || p_278280_ == SoundType.STEM || p_278280_ == SoundType.CHERRY_WOOD || p_278280_ == SoundType.BAMBOO_WOOD;
   }

   protected void playGallopSound(SoundType p_30560_) {
      this.playSound(SoundEvents.HORSE_GALLOP, p_30560_.getVolume() * 0.15F, p_30560_.getPitch());
   }

   public static AttributeSupplier.Builder createBaseHorseAttributes() {
      return Mob.createMobAttributes().add(Attributes.JUMP_STRENGTH).add(Attributes.MAX_HEALTH, 53.0D).add(Attributes.MOVEMENT_SPEED, (double)0.225F);
   }

   public int getMaxSpawnClusterSize() {
      return 6;
   }

   public int getMaxTemper() {
      return 100;
   }

   protected float getSoundVolume() {
      return 0.8F;
   }

   public int getAmbientSoundInterval() {
      return 400;
   }

   public void openCustomInventoryScreen(Player p_218808_) {
      if (!this.level().isClientSide && (!this.isVehicle() || this.hasPassenger(p_218808_)) && this.isTamed()) {
         p_218808_.openHorseInventory(this, this.inventory);
      }

   }

   public InteractionResult fedFood(Player p_30581_, ItemStack p_30582_) {
      boolean flag = this.handleEating(p_30581_, p_30582_);
      if (!p_30581_.getAbilities().instabuild) {
         p_30582_.shrink(1);
      }

      if (this.level().isClientSide) {
         return InteractionResult.CONSUME;
      } else {
         return flag ? InteractionResult.SUCCESS : InteractionResult.PASS;
      }
   }

   protected boolean handleEating(Player p_30593_, ItemStack p_30594_) {
      boolean flag = false;
      float f = 0.0F;
      int i = 0;
      int j = 0;
      if (p_30594_.is(Items.WHEAT)) {
         f = 2.0F;
         i = 20;
         j = 3;
      } else if (p_30594_.is(Items.SUGAR)) {
         f = 1.0F;
         i = 30;
         j = 3;
      } else if (p_30594_.is(Blocks.HAY_BLOCK.asItem())) {
         f = 20.0F;
         i = 180;
      } else if (p_30594_.is(Items.APPLE)) {
         f = 3.0F;
         i = 60;
         j = 3;
      } else if (p_30594_.is(Items.GOLDEN_CARROT)) {
         f = 4.0F;
         i = 60;
         j = 5;
         if (!this.level().isClientSide && this.isTamed() && this.getAge() == 0 && !this.isInLove()) {
            flag = true;
            this.setInLove(p_30593_);
         }
      } else if (p_30594_.is(Items.GOLDEN_APPLE) || p_30594_.is(Items.ENCHANTED_GOLDEN_APPLE)) {
         f = 10.0F;
         i = 240;
         j = 10;
         if (!this.level().isClientSide && this.isTamed() && this.getAge() == 0 && !this.isInLove()) {
            flag = true;
            this.setInLove(p_30593_);
         }
      }

      if (this.getHealth() < this.getMaxHealth() && f > 0.0F) {
         this.heal(f);
         flag = true;
      }

      if (this.isBaby() && i > 0) {
         this.level().addParticle(ParticleTypes.HAPPY_VILLAGER, this.getRandomX(1.0D), this.getRandomY() + 0.5D, this.getRandomZ(1.0D), 0.0D, 0.0D, 0.0D);
         if (!this.level().isClientSide) {
            this.ageUp(i);
         }

         flag = true;
      }

      if (j > 0 && (flag || !this.isTamed()) && this.getTemper() < this.getMaxTemper()) {
         flag = true;
         if (!this.level().isClientSide) {
            this.modifyTemper(j);
         }
      }

      if (flag) {
         this.eating();
         this.gameEvent(GameEvent.EAT);
      }

      return flag;
   }

   protected void doPlayerRide(Player p_30634_) {
      this.setEating(false);
      this.setStanding(false);
      if (!this.level().isClientSide) {
         p_30634_.setYRot(this.getYRot());
         p_30634_.setXRot(this.getXRot());
         p_30634_.startRiding(this);
      }

   }

   public boolean isImmobile() {
      return super.isImmobile() && this.isVehicle() && this.isSaddled() || this.isEating() || this.isStanding();
   }

   public boolean isFood(ItemStack p_30644_) {
      return FOOD_ITEMS.test(p_30644_);
   }

   private void moveTail() {
      this.tailCounter = 1;
   }

   protected void dropEquipment() {
      super.dropEquipment();
      if (this.inventory != null) {
         for(int i = 0; i < this.inventory.getContainerSize(); ++i) {
            ItemStack itemstack = this.inventory.getItem(i);
            if (!itemstack.isEmpty() && !EnchantmentHelper.hasVanishingCurse(itemstack)) {
               this.spawnAtLocation(itemstack);
            }
         }

      }
   }

   public void aiStep() {
      if (this.random.nextInt(200) == 0) {
         this.moveTail();
      }

      super.aiStep();
      if (!this.level().isClientSide && this.isAlive()) {
         if (this.random.nextInt(900) == 0 && this.deathTime == 0) {
            this.heal(1.0F);
         }

         if (this.canEatGrass()) {
            if (!this.isEating() && !this.isVehicle() && this.random.nextInt(300) == 0 && this.level().getBlockState(this.blockPosition().below()).is(Blocks.GRASS_BLOCK)) {
               this.setEating(true);
            }

            if (this.isEating() && ++this.eatingCounter > 50) {
               this.eatingCounter = 0;
               this.setEating(false);
            }
         }

         this.followMommy();
      }
   }

   protected void followMommy() {
      if (this.isBred() && this.isBaby() && !this.isEating()) {
         LivingEntity livingentity = this.level().getNearestEntity(AbstractHorse.class, MOMMY_TARGETING, this, this.getX(), this.getY(), this.getZ(), this.getBoundingBox().inflate(16.0D));
         if (livingentity != null && this.distanceToSqr(livingentity) > 4.0D) {
            this.navigation.createPath(livingentity, 0);
         }
      }

   }

   public boolean canEatGrass() {
      return true;
   }

   public void tick() {
      super.tick();
      if (this.mouthCounter > 0 && ++this.mouthCounter > 30) {
         this.mouthCounter = 0;
         this.setFlag(64, false);
      }

      if (this.isEffectiveAi() && this.standCounter > 0 && ++this.standCounter > 20) {
         this.standCounter = 0;
         this.setStanding(false);
      }

      if (this.tailCounter > 0 && ++this.tailCounter > 8) {
         this.tailCounter = 0;
      }

      if (this.sprintCounter > 0) {
         ++this.sprintCounter;
         if (this.sprintCounter > 300) {
            this.sprintCounter = 0;
         }
      }

      this.eatAnimO = this.eatAnim;
      if (this.isEating()) {
         this.eatAnim += (1.0F - this.eatAnim) * 0.4F + 0.05F;
         if (this.eatAnim > 1.0F) {
            this.eatAnim = 1.0F;
         }
      } else {
         this.eatAnim += (0.0F - this.eatAnim) * 0.4F - 0.05F;
         if (this.eatAnim < 0.0F) {
            this.eatAnim = 0.0F;
         }
      }

      this.standAnimO = this.standAnim;
      if (this.isStanding()) {
         this.eatAnim = 0.0F;
         this.eatAnimO = this.eatAnim;
         this.standAnim += (1.0F - this.standAnim) * 0.4F + 0.05F;
         if (this.standAnim > 1.0F) {
            this.standAnim = 1.0F;
         }
      } else {
         this.allowStandSliding = false;
         this.standAnim += (0.8F * this.standAnim * this.standAnim * this.standAnim - this.standAnim) * 0.6F - 0.05F;
         if (this.standAnim < 0.0F) {
            this.standAnim = 0.0F;
         }
      }

      this.mouthAnimO = this.mouthAnim;
      if (this.getFlag(64)) {
         this.mouthAnim += (1.0F - this.mouthAnim) * 0.7F + 0.05F;
         if (this.mouthAnim > 1.0F) {
            this.mouthAnim = 1.0F;
         }
      } else {
         this.mouthAnim += (0.0F - this.mouthAnim) * 0.7F - 0.05F;
         if (this.mouthAnim < 0.0F) {
            this.mouthAnim = 0.0F;
         }
      }

   }

   public InteractionResult mobInteract(Player p_252289_, InteractionHand p_248927_) {
      if (!this.isVehicle() && !this.isBaby()) {
         if (this.isTamed() && p_252289_.isSecondaryUseActive()) {
            this.openCustomInventoryScreen(p_252289_);
            return InteractionResult.sidedSuccess(this.level().isClientSide);
         } else {
            ItemStack itemstack = p_252289_.getItemInHand(p_248927_);
            if (!itemstack.isEmpty()) {
               InteractionResult interactionresult = itemstack.interactLivingEntity(p_252289_, this, p_248927_);
               if (interactionresult.consumesAction()) {
                  return interactionresult;
               }

               if (this.canWearArmor() && this.isArmor(itemstack) && !this.isWearingArmor()) {
                  this.equipArmor(p_252289_, itemstack);
                  return InteractionResult.sidedSuccess(this.level().isClientSide);
               }
            }

            this.doPlayerRide(p_252289_);
            return InteractionResult.sidedSuccess(this.level().isClientSide);
         }
      } else {
         return super.mobInteract(p_252289_, p_248927_);
      }
   }

   private void openMouth() {
      if (!this.level().isClientSide) {
         this.mouthCounter = 1;
         this.setFlag(64, true);
      }

   }

   public void setEating(boolean p_30662_) {
      this.setFlag(16, p_30662_);
   }

   public void setStanding(boolean p_30666_) {
      if (p_30666_) {
         this.setEating(false);
      }

      this.setFlag(32, p_30666_);
   }

   @Nullable
   public SoundEvent getAmbientStandSound() {
      return this.getAmbientSound();
   }

   public void standIfPossible() {
      if (this.canPerformRearing() && this.isEffectiveAi()) {
         this.standCounter = 1;
         this.setStanding(true);
      }

   }

   public void makeMad() {
      if (!this.isStanding()) {
         this.standIfPossible();
         SoundEvent soundevent = this.getAngrySound();
         if (soundevent != null) {
            this.playSound(soundevent, this.getSoundVolume(), this.getVoicePitch());
         }
      }

   }

   public boolean tameWithName(Player p_30638_) {
      this.setOwnerUUID(p_30638_.getUUID());
      this.setTamed(true);
      if (p_30638_ instanceof ServerPlayer) {
         CriteriaTriggers.TAME_ANIMAL.trigger((ServerPlayer)p_30638_, this);
      }

      this.level().broadcastEntityEvent(this, (byte)7);
      return true;
   }

   protected void tickRidden(Player p_278233_, Vec3 p_275693_) {
      super.tickRidden(p_278233_, p_275693_);
      Vec2 vec2 = this.getRiddenRotation(p_278233_);
      this.setRot(vec2.y, vec2.x);
      this.yRotO = this.yBodyRot = this.yHeadRot = this.getYRot();
      if (this.isControlledByLocalInstance()) {
         if (p_275693_.z <= 0.0D) {
            this.gallopSoundCounter = 0;
         }

         if (this.onGround()) {
            this.setIsJumping(false);
            if (this.playerJumpPendingScale > 0.0F && !this.isJumping()) {
               this.executeRidersJump(this.playerJumpPendingScale, p_275693_);
            }

            this.playerJumpPendingScale = 0.0F;
         }
      }

   }

   protected Vec2 getRiddenRotation(LivingEntity p_275502_) {
      return new Vec2(p_275502_.getXRot() * 0.5F, p_275502_.getYRot());
   }

   protected Vec3 getRiddenInput(Player p_278278_, Vec3 p_275506_) {
      if (this.onGround() && this.playerJumpPendingScale == 0.0F && this.isStanding() && !this.allowStandSliding) {
         return Vec3.ZERO;
      } else {
         float f = p_278278_.xxa * 0.5F;
         float f1 = p_278278_.zza;
         if (f1 <= 0.0F) {
            f1 *= 0.25F;
         }

         return new Vec3((double)f, 0.0D, (double)f1);
      }
   }

   protected float getRiddenSpeed(Player p_278336_) {
      return (float)this.getAttributeValue(Attributes.MOVEMENT_SPEED);
   }

   protected void executeRidersJump(float p_248808_, Vec3 p_275435_) {
      double d0 = this.getCustomJump() * (double)p_248808_ * (double)this.getBlockJumpFactor();
      double d1 = d0 + (double)this.getJumpBoostPower();
      Vec3 vec3 = this.getDeltaMovement();
      this.setDeltaMovement(vec3.x, d1, vec3.z);
      this.setIsJumping(true);
      this.hasImpulse = true;
               net.minecraftforge.common.ForgeHooks.onLivingJump(this);
      if (p_275435_.z > 0.0D) {
         float f = Mth.sin(this.getYRot() * ((float)Math.PI / 180F));
         float f1 = Mth.cos(this.getYRot() * ((float)Math.PI / 180F));
         this.setDeltaMovement(this.getDeltaMovement().add((double)(-0.4F * f * p_248808_), 0.0D, (double)(0.4F * f1 * p_248808_)));
      }

   }

   protected void playJumpSound() {
      this.playSound(SoundEvents.HORSE_JUMP, 0.4F, 1.0F);
   }

   public void addAdditionalSaveData(CompoundTag p_30589_) {
      super.addAdditionalSaveData(p_30589_);
      p_30589_.putBoolean("EatingHaystack", this.isEating());
      p_30589_.putBoolean("Bred", this.isBred());
      p_30589_.putInt("Temper", this.getTemper());
      p_30589_.putBoolean("Tame", this.isTamed());
      if (this.getOwnerUUID() != null) {
         p_30589_.putUUID("Owner", this.getOwnerUUID());
      }

      if (!this.inventory.getItem(0).isEmpty()) {
         p_30589_.put("SaddleItem", this.inventory.getItem(0).save(new CompoundTag()));
      }

   }

   public void readAdditionalSaveData(CompoundTag p_30565_) {
      super.readAdditionalSaveData(p_30565_);
      this.setEating(p_30565_.getBoolean("EatingHaystack"));
      this.setBred(p_30565_.getBoolean("Bred"));
      this.setTemper(p_30565_.getInt("Temper"));
      this.setTamed(p_30565_.getBoolean("Tame"));
      UUID uuid;
      if (p_30565_.hasUUID("Owner")) {
         uuid = p_30565_.getUUID("Owner");
      } else {
         String s = p_30565_.getString("Owner");
         uuid = OldUsersConverter.convertMobOwnerIfNecessary(this.getServer(), s);
      }

      if (uuid != null) {
         this.setOwnerUUID(uuid);
      }

      if (p_30565_.contains("SaddleItem", 10)) {
         ItemStack itemstack = ItemStack.of(p_30565_.getCompound("SaddleItem"));
         if (itemstack.is(Items.SADDLE)) {
            this.inventory.setItem(0, itemstack);
         }
      }

      this.updateContainerEquipment();
   }

   public boolean canMate(Animal p_30553_) {
      return false;
   }

   protected boolean canParent() {
      return !this.isVehicle() && !this.isPassenger() && this.isTamed() && !this.isBaby() && this.getHealth() >= this.getMaxHealth() && this.isInLove();
   }

   @Nullable
   public AgeableMob getBreedOffspring(ServerLevel p_149506_, AgeableMob p_149507_) {
      return null;
   }

   protected void setOffspringAttributes(AgeableMob p_149509_, AbstractHorse p_149510_) {
      this.setOffspringAttribute(p_149509_, p_149510_, Attributes.MAX_HEALTH, (double)MIN_HEALTH, (double)MAX_HEALTH);
      this.setOffspringAttribute(p_149509_, p_149510_, Attributes.JUMP_STRENGTH, (double)MIN_JUMP_STRENGTH, (double)MAX_JUMP_STRENGTH);
      this.setOffspringAttribute(p_149509_, p_149510_, Attributes.MOVEMENT_SPEED, (double)MIN_MOVEMENT_SPEED, (double)MAX_MOVEMENT_SPEED);
   }

   private void setOffspringAttribute(AgeableMob p_273163_, AbstractHorse p_273784_, Attribute p_273681_, double p_272663_, double p_273405_) {
      double d0 = createOffspringAttribute(this.getAttributeBaseValue(p_273681_), p_273163_.getAttributeBaseValue(p_273681_), p_272663_, p_273405_, this.random);
      p_273784_.getAttribute(p_273681_).setBaseValue(d0);
   }

   static double createOffspringAttribute(double p_272685_, double p_273709_, double p_273376_, double p_273030_, RandomSource p_272743_) {
      if (p_273030_ <= p_273376_) {
         throw new IllegalArgumentException("Incorrect range for an attribute");
      } else {
         p_272685_ = Mth.clamp(p_272685_, p_273376_, p_273030_);
         p_273709_ = Mth.clamp(p_273709_, p_273376_, p_273030_);
         double d0 = 0.15D * (p_273030_ - p_273376_);
         double d1 = Math.abs(p_272685_ - p_273709_) + d0 * 2.0D;
         double d2 = (p_272685_ + p_273709_) / 2.0D;
         double d3 = (p_272743_.nextDouble() + p_272743_.nextDouble() + p_272743_.nextDouble()) / 3.0D - 0.5D;
         double d4 = d2 + d1 * d3;
         if (d4 > p_273030_) {
            double d6 = d4 - p_273030_;
            return p_273030_ - d6;
         } else if (d4 < p_273376_) {
            double d5 = p_273376_ - d4;
            return p_273376_ + d5;
         } else {
            return d4;
         }
      }
   }

   public float getEatAnim(float p_30664_) {
      return Mth.lerp(p_30664_, this.eatAnimO, this.eatAnim);
   }

   public float getStandAnim(float p_30668_) {
      return Mth.lerp(p_30668_, this.standAnimO, this.standAnim);
   }

   public float getMouthAnim(float p_30534_) {
      return Mth.lerp(p_30534_, this.mouthAnimO, this.mouthAnim);
   }

   public void onPlayerJump(int p_30591_) {
      if (this.isSaddled()) {
         if (p_30591_ < 0) {
            p_30591_ = 0;
         } else {
            this.allowStandSliding = true;
            this.standIfPossible();
         }

         if (p_30591_ >= 90) {
            this.playerJumpPendingScale = 1.0F;
         } else {
            this.playerJumpPendingScale = 0.4F + 0.4F * (float)p_30591_ / 90.0F;
         }

      }
   }

   public boolean canJump() {
      return this.isSaddled();
   }

   public void handleStartJump(int p_30574_) {
      this.allowStandSliding = true;
      this.standIfPossible();
      this.playJumpSound();
   }

   public void handleStopJump() {
   }

   protected void spawnTamingParticles(boolean p_30670_) {
      ParticleOptions particleoptions = p_30670_ ? ParticleTypes.HEART : ParticleTypes.SMOKE;

      for(int i = 0; i < 7; ++i) {
         double d0 = this.random.nextGaussian() * 0.02D;
         double d1 = this.random.nextGaussian() * 0.02D;
         double d2 = this.random.nextGaussian() * 0.02D;
         this.level().addParticle(particleoptions, this.getRandomX(1.0D), this.getRandomY() + 0.5D, this.getRandomZ(1.0D), d0, d1, d2);
      }

   }

   public void handleEntityEvent(byte p_30541_) {
      if (p_30541_ == 7) {
         this.spawnTamingParticles(true);
      } else if (p_30541_ == 6) {
         this.spawnTamingParticles(false);
      } else {
         super.handleEntityEvent(p_30541_);
      }

   }

   protected void positionRider(Entity p_289569_, Entity.MoveFunction p_289558_) {
      super.positionRider(p_289569_, p_289558_);
      if (this.standAnimO > 0.0F) {
         float f = Mth.sin(this.yBodyRot * ((float)Math.PI / 180F));
         float f1 = Mth.cos(this.yBodyRot * ((float)Math.PI / 180F));
         float f2 = 0.7F * this.standAnimO;
         float f3 = 0.15F * this.standAnimO;
         p_289558_.accept(p_289569_, this.getX() + (double)(f2 * f), this.getY() + this.getPassengersRidingOffset() + p_289569_.getMyRidingOffset() + (double)f3, this.getZ() - (double)(f2 * f1));
         if (p_289569_ instanceof LivingEntity) {
            ((LivingEntity)p_289569_).yBodyRot = this.yBodyRot;
         }
      }

   }

   protected static float generateMaxHealth(IntUnaryOperator p_272695_) {
      return 15.0F + (float)p_272695_.applyAsInt(8) + (float)p_272695_.applyAsInt(9);
   }

   protected static double generateJumpStrength(DoubleSupplier p_272718_) {
      return (double)0.4F + p_272718_.getAsDouble() * 0.2D + p_272718_.getAsDouble() * 0.2D + p_272718_.getAsDouble() * 0.2D;
   }

   protected static double generateSpeed(DoubleSupplier p_273691_) {
      return ((double)0.45F + p_273691_.getAsDouble() * 0.3D + p_273691_.getAsDouble() * 0.3D + p_273691_.getAsDouble() * 0.3D) * 0.25D;
   }

   public boolean onClimbable() {
      return false;
   }

   protected float getStandingEyeHeight(Pose p_30578_, EntityDimensions p_30579_) {
      return p_30579_.height * 0.95F;
   }

   public boolean canWearArmor() {
      return false;
   }

   public boolean isWearingArmor() {
      return !this.getItemBySlot(EquipmentSlot.CHEST).isEmpty();
   }

   public boolean isArmor(ItemStack p_30645_) {
      return false;
   }

   private SlotAccess createEquipmentSlotAccess(final int p_149503_, final Predicate<ItemStack> p_149504_) {
      return new SlotAccess() {
         public ItemStack get() {
            return AbstractHorse.this.inventory.getItem(p_149503_);
         }

         public boolean set(ItemStack p_149528_) {
            if (!p_149504_.test(p_149528_)) {
               return false;
            } else {
               AbstractHorse.this.inventory.setItem(p_149503_, p_149528_);
               AbstractHorse.this.updateContainerEquipment();
               return true;
            }
         }
      };
   }

   public SlotAccess getSlot(int p_149514_) {
      int i = p_149514_ - 400;
      if (i >= 0 && i < 2 && i < this.inventory.getContainerSize()) {
         if (i == 0) {
            return this.createEquipmentSlotAccess(i, (p_149518_) -> {
               return p_149518_.isEmpty() || p_149518_.is(Items.SADDLE);
            });
         }

         if (i == 1) {
            if (!this.canWearArmor()) {
               return SlotAccess.NULL;
            }

            return this.createEquipmentSlotAccess(i, (p_149516_) -> {
               return p_149516_.isEmpty() || this.isArmor(p_149516_);
            });
         }
      }

      int j = p_149514_ - 500 + 2;
      return j >= 2 && j < this.inventory.getContainerSize() ? SlotAccess.forContainer(this.inventory, j) : super.getSlot(p_149514_);
   }

   @Nullable
   public LivingEntity getControllingPassenger() {
      Entity entity = this.getFirstPassenger();
      if (entity instanceof Mob) {
         return (Mob)entity;
      } else {
         if (this.isSaddled()) {
            entity = this.getFirstPassenger();
            if (entity instanceof Player) {
               return (Player)entity;
            }
         }

         return null;
      }
   }

   @Nullable
   private Vec3 getDismountLocationInDirection(Vec3 p_30562_, LivingEntity p_30563_) {
      double d0 = this.getX() + p_30562_.x;
      double d1 = this.getBoundingBox().minY;
      double d2 = this.getZ() + p_30562_.z;
      BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

      for(Pose pose : p_30563_.getDismountPoses()) {
         blockpos$mutableblockpos.set(d0, d1, d2);
         double d3 = this.getBoundingBox().maxY + 0.75D;

         while(true) {
            double d4 = this.level().getBlockFloorHeight(blockpos$mutableblockpos);
            if ((double)blockpos$mutableblockpos.getY() + d4 > d3) {
               break;
            }

            if (DismountHelper.isBlockFloorValid(d4)) {
               AABB aabb = p_30563_.getLocalBoundsForPose(pose);
               Vec3 vec3 = new Vec3(d0, (double)blockpos$mutableblockpos.getY() + d4, d2);
               if (DismountHelper.canDismountTo(this.level(), p_30563_, aabb.move(vec3))) {
                  p_30563_.setPose(pose);
                  return vec3;
               }
            }

            blockpos$mutableblockpos.move(Direction.UP);
            if (!((double)blockpos$mutableblockpos.getY() < d3)) {
               break;
            }
         }
      }

      return null;
   }

   public Vec3 getDismountLocationForPassenger(LivingEntity p_30576_) {
      Vec3 vec3 = getCollisionHorizontalEscapeVector((double)this.getBbWidth(), (double)p_30576_.getBbWidth(), this.getYRot() + (p_30576_.getMainArm() == HumanoidArm.RIGHT ? 90.0F : -90.0F));
      Vec3 vec31 = this.getDismountLocationInDirection(vec3, p_30576_);
      if (vec31 != null) {
         return vec31;
      } else {
         Vec3 vec32 = getCollisionHorizontalEscapeVector((double)this.getBbWidth(), (double)p_30576_.getBbWidth(), this.getYRot() + (p_30576_.getMainArm() == HumanoidArm.LEFT ? 90.0F : -90.0F));
         Vec3 vec33 = this.getDismountLocationInDirection(vec32, p_30576_);
         return vec33 != null ? vec33 : this.position();
      }
   }

   protected void randomizeAttributes(RandomSource p_218804_) {
   }

   @Nullable
   public SpawnGroupData finalizeSpawn(ServerLevelAccessor p_30555_, DifficultyInstance p_30556_, MobSpawnType p_30557_, @Nullable SpawnGroupData p_30558_, @Nullable CompoundTag p_30559_) {
      if (p_30558_ == null) {
         p_30558_ = new AgeableMob.AgeableMobGroupData(0.2F);
      }

      this.randomizeAttributes(p_30555_.getRandom());
      return super.finalizeSpawn(p_30555_, p_30556_, p_30557_, p_30558_, p_30559_);
   }

   private net.minecraftforge.common.util.LazyOptional<?> itemHandler = null;

   @Override
   public <T> net.minecraftforge.common.util.LazyOptional<T> getCapability(net.minecraftforge.common.capabilities.Capability<T> capability, @Nullable net.minecraft.core.Direction facing) {
      if (this.isAlive() && capability == net.minecraftforge.common.capabilities.ForgeCapabilities.ITEM_HANDLER && itemHandler != null)
         return itemHandler.cast();
      return super.getCapability(capability, facing);
   }

   @Override
   public void invalidateCaps() {
      super.invalidateCaps();
      if (itemHandler != null) {
         net.minecraftforge.common.util.LazyOptional<?> oldHandler = itemHandler;
         itemHandler = null;
         oldHandler.invalidate();
      }
   }

   public boolean hasInventoryChanged(Container p_149512_) {
      return this.inventory != p_149512_;
   }

   public int getAmbientStandInterval() {
      return this.getAmbientSoundInterval();
   }
}
