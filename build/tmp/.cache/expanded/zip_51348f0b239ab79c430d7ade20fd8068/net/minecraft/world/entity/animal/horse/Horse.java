package net.minecraft.world.entity.animal.horse;

import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.VariantHolder;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.HorseArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.SoundType;

public class Horse extends AbstractHorse implements VariantHolder<Variant> {
   private static final UUID ARMOR_MODIFIER_UUID = UUID.fromString("556E1665-8B10-40C8-8F9D-CF9B1667F295");
   private static final EntityDataAccessor<Integer> DATA_ID_TYPE_VARIANT = SynchedEntityData.defineId(Horse.class, EntityDataSerializers.INT);

   public Horse(EntityType<? extends Horse> p_30689_, Level p_30690_) {
      super(p_30689_, p_30690_);
   }

   protected void randomizeAttributes(RandomSource p_218815_) {
      this.getAttribute(Attributes.MAX_HEALTH).setBaseValue((double)generateMaxHealth(p_218815_::nextInt));
      this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(generateSpeed(p_218815_::nextDouble));
      this.getAttribute(Attributes.JUMP_STRENGTH).setBaseValue(generateJumpStrength(p_218815_::nextDouble));
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_ID_TYPE_VARIANT, 0);
   }

   public void addAdditionalSaveData(CompoundTag p_30716_) {
      super.addAdditionalSaveData(p_30716_);
      p_30716_.putInt("Variant", this.getTypeVariant());
      if (!this.inventory.getItem(1).isEmpty()) {
         p_30716_.put("ArmorItem", this.inventory.getItem(1).save(new CompoundTag()));
      }

   }

   public ItemStack getArmor() {
      return this.getItemBySlot(EquipmentSlot.CHEST);
   }

   private void setArmor(ItemStack p_30733_) {
      this.setItemSlot(EquipmentSlot.CHEST, p_30733_);
      this.setDropChance(EquipmentSlot.CHEST, 0.0F);
   }

   public void readAdditionalSaveData(CompoundTag p_30711_) {
      super.readAdditionalSaveData(p_30711_);
      this.setTypeVariant(p_30711_.getInt("Variant"));
      if (p_30711_.contains("ArmorItem", 10)) {
         ItemStack itemstack = ItemStack.of(p_30711_.getCompound("ArmorItem"));
         if (!itemstack.isEmpty() && this.isArmor(itemstack)) {
            this.inventory.setItem(1, itemstack);
         }
      }

      this.updateContainerEquipment();
   }

   private void setTypeVariant(int p_30737_) {
      this.entityData.set(DATA_ID_TYPE_VARIANT, p_30737_);
   }

   private int getTypeVariant() {
      return this.entityData.get(DATA_ID_TYPE_VARIANT);
   }

   private void setVariantAndMarkings(Variant p_30700_, Markings p_30701_) {
      this.setTypeVariant(p_30700_.getId() & 255 | p_30701_.getId() << 8 & '\uff00');
   }

   public Variant getVariant() {
      return Variant.byId(this.getTypeVariant() & 255);
   }

   public void setVariant(Variant p_262684_) {
      this.setTypeVariant(p_262684_.getId() & 255 | this.getTypeVariant() & -256);
   }

   public Markings getMarkings() {
      return Markings.byId((this.getTypeVariant() & '\uff00') >> 8);
   }

   protected void updateContainerEquipment() {
      if (!this.level().isClientSide) {
         super.updateContainerEquipment();
         this.setArmorEquipment(this.inventory.getItem(1));
         this.setDropChance(EquipmentSlot.CHEST, 0.0F);
      }
   }

   private void setArmorEquipment(ItemStack p_30735_) {
      this.setArmor(p_30735_);
      if (!this.level().isClientSide) {
         this.getAttribute(Attributes.ARMOR).removeModifier(ARMOR_MODIFIER_UUID);
         if (this.isArmor(p_30735_)) {
            int i = ((HorseArmorItem)p_30735_.getItem()).getProtection();
            if (i != 0) {
               this.getAttribute(Attributes.ARMOR).addTransientModifier(new AttributeModifier(ARMOR_MODIFIER_UUID, "Horse armor bonus", (double)i, AttributeModifier.Operation.ADDITION));
            }
         }
      }

   }

   public void containerChanged(Container p_30696_) {
      ItemStack itemstack = this.getArmor();
      super.containerChanged(p_30696_);
      ItemStack itemstack1 = this.getArmor();
      if (this.tickCount > 20 && this.isArmor(itemstack1) && itemstack != itemstack1) {
         this.playSound(SoundEvents.HORSE_ARMOR, 0.5F, 1.0F);
      }

   }

   protected void playGallopSound(SoundType p_30709_) {
      super.playGallopSound(p_30709_);
      if (this.random.nextInt(10) == 0) {
         this.playSound(SoundEvents.HORSE_BREATHE, p_30709_.getVolume() * 0.6F, p_30709_.getPitch());
      }

      ItemStack stack = this.inventory.getItem(1);
      if (isArmor(stack)) stack.onHorseArmorTick(level(), this);
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.HORSE_AMBIENT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.HORSE_DEATH;
   }

   @Nullable
   protected SoundEvent getEatingSound() {
      return SoundEvents.HORSE_EAT;
   }

   protected SoundEvent getHurtSound(DamageSource p_30720_) {
      return SoundEvents.HORSE_HURT;
   }

   protected SoundEvent getAngrySound() {
      return SoundEvents.HORSE_ANGRY;
   }

   public InteractionResult mobInteract(Player p_30713_, InteractionHand p_30714_) {
      boolean flag = !this.isBaby() && this.isTamed() && p_30713_.isSecondaryUseActive();
      if (!this.isVehicle() && !flag) {
         ItemStack itemstack = p_30713_.getItemInHand(p_30714_);
         if (!itemstack.isEmpty()) {
            if (this.isFood(itemstack)) {
               return this.fedFood(p_30713_, itemstack);
            }

            if (!this.isTamed()) {
               this.makeMad();
               return InteractionResult.sidedSuccess(this.level().isClientSide);
            }
         }

         return super.mobInteract(p_30713_, p_30714_);
      } else {
         return super.mobInteract(p_30713_, p_30714_);
      }
   }

   public boolean canMate(Animal p_30698_) {
      if (p_30698_ == this) {
         return false;
      } else if (!(p_30698_ instanceof Donkey) && !(p_30698_ instanceof Horse)) {
         return false;
      } else {
         return this.canParent() && ((AbstractHorse)p_30698_).canParent();
      }
   }

   @Nullable
   public AgeableMob getBreedOffspring(ServerLevel p_149533_, AgeableMob p_149534_) {
      if (p_149534_ instanceof Donkey) {
         Mule mule = EntityType.MULE.create(p_149533_);
         if (mule != null) {
            this.setOffspringAttributes(p_149534_, mule);
         }

         return mule;
      } else {
         Horse horse = (Horse)p_149534_;
         Horse horse1 = EntityType.HORSE.create(p_149533_);
         if (horse1 != null) {
            int i = this.random.nextInt(9);
            Variant variant;
            if (i < 4) {
               variant = this.getVariant();
            } else if (i < 8) {
               variant = horse.getVariant();
            } else {
               variant = Util.getRandom(Variant.values(), this.random);
            }

            int j = this.random.nextInt(5);
            Markings markings;
            if (j < 2) {
               markings = this.getMarkings();
            } else if (j < 4) {
               markings = horse.getMarkings();
            } else {
               markings = Util.getRandom(Markings.values(), this.random);
            }

            horse1.setVariantAndMarkings(variant, markings);
            this.setOffspringAttributes(p_149534_, horse1);
         }

         return horse1;
      }
   }

   public boolean canWearArmor() {
      return true;
   }

   public boolean isArmor(ItemStack p_30731_) {
      return p_30731_.getItem() instanceof HorseArmorItem;
   }

   @Nullable
   public SpawnGroupData finalizeSpawn(ServerLevelAccessor p_30703_, DifficultyInstance p_30704_, MobSpawnType p_30705_, @Nullable SpawnGroupData p_30706_, @Nullable CompoundTag p_30707_) {
      RandomSource randomsource = p_30703_.getRandom();
      Variant variant;
      if (p_30706_ instanceof Horse.HorseGroupData) {
         variant = ((Horse.HorseGroupData)p_30706_).variant;
      } else {
         variant = Util.getRandom(Variant.values(), randomsource);
         p_30706_ = new Horse.HorseGroupData(variant);
      }

      this.setVariantAndMarkings(variant, Util.getRandom(Markings.values(), randomsource));
      return super.finalizeSpawn(p_30703_, p_30704_, p_30705_, p_30706_, p_30707_);
   }

   public static class HorseGroupData extends AgeableMob.AgeableMobGroupData {
      public final Variant variant;

      public HorseGroupData(Variant p_30740_) {
         super(true);
         this.variant = p_30740_;
      }
   }
}
