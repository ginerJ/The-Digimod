package net.minecraft.world.entity.vehicle;

import javax.annotation.Nullable;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.HasCustomInventoryScreen;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;

public class ChestBoat extends Boat implements HasCustomInventoryScreen, ContainerEntity {
   private static final int CONTAINER_SIZE = 27;
   private NonNullList<ItemStack> itemStacks = NonNullList.withSize(27, ItemStack.EMPTY);
   @Nullable
   private ResourceLocation lootTable;
   private long lootTableSeed;

   public ChestBoat(EntityType<? extends Boat> p_219869_, Level p_219870_) {
      super(p_219869_, p_219870_);
   }

   public ChestBoat(Level p_219872_, double p_219873_, double p_219874_, double p_219875_) {
      this(EntityType.CHEST_BOAT, p_219872_);
      this.setPos(p_219873_, p_219874_, p_219875_);
      this.xo = p_219873_;
      this.yo = p_219874_;
      this.zo = p_219875_;
   }

   protected float getSinglePassengerXOffset() {
      return 0.15F;
   }

   protected int getMaxPassengers() {
      return 1;
   }

   protected void addAdditionalSaveData(CompoundTag p_219908_) {
      super.addAdditionalSaveData(p_219908_);
      this.addChestVehicleSaveData(p_219908_);
   }

   protected void readAdditionalSaveData(CompoundTag p_219901_) {
      super.readAdditionalSaveData(p_219901_);
      this.readChestVehicleSaveData(p_219901_);
   }

   public void destroy(DamageSource p_219892_) {
      super.destroy(p_219892_);
      this.chestVehicleDestroyed(p_219892_, this.level(), this);
   }

   public void remove(Entity.RemovalReason p_219894_) {
      if (!this.level().isClientSide && p_219894_.shouldDestroy()) {
         Containers.dropContents(this.level(), this, this);
      }

      super.remove(p_219894_);
   }

   public InteractionResult interact(Player p_219898_, InteractionHand p_219899_) {
      if (this.canAddPassenger(p_219898_) && !p_219898_.isSecondaryUseActive()) {
         return super.interact(p_219898_, p_219899_);
      } else {
         InteractionResult interactionresult = this.interactWithContainerVehicle(p_219898_);
         if (interactionresult.consumesAction()) {
            this.gameEvent(GameEvent.CONTAINER_OPEN, p_219898_);
            PiglinAi.angerNearbyPiglins(p_219898_, true);
         }

         return interactionresult;
      }
   }

   public void openCustomInventoryScreen(Player p_219906_) {
      p_219906_.openMenu(this);
      if (!p_219906_.level().isClientSide) {
         this.gameEvent(GameEvent.CONTAINER_OPEN, p_219906_);
         PiglinAi.angerNearbyPiglins(p_219906_, true);
      }

   }

   public Item getDropItem() {
      Item item;
      switch (this.getVariant()) {
         case SPRUCE:
            item = Items.SPRUCE_CHEST_BOAT;
            break;
         case BIRCH:
            item = Items.BIRCH_CHEST_BOAT;
            break;
         case JUNGLE:
            item = Items.JUNGLE_CHEST_BOAT;
            break;
         case ACACIA:
            item = Items.ACACIA_CHEST_BOAT;
            break;
         case CHERRY:
            item = Items.CHERRY_CHEST_BOAT;
            break;
         case DARK_OAK:
            item = Items.DARK_OAK_CHEST_BOAT;
            break;
         case MANGROVE:
            item = Items.MANGROVE_CHEST_BOAT;
            break;
         case BAMBOO:
            item = Items.BAMBOO_CHEST_RAFT;
            break;
         default:
            item = Items.OAK_CHEST_BOAT;
      }

      return item;
   }

   public void clearContent() {
      this.clearChestVehicleContent();
   }

   public int getContainerSize() {
      return 27;
   }

   public ItemStack getItem(int p_219880_) {
      return this.getChestVehicleItem(p_219880_);
   }

   public ItemStack removeItem(int p_219882_, int p_219883_) {
      return this.removeChestVehicleItem(p_219882_, p_219883_);
   }

   public ItemStack removeItemNoUpdate(int p_219904_) {
      return this.removeChestVehicleItemNoUpdate(p_219904_);
   }

   public void setItem(int p_219885_, ItemStack p_219886_) {
      this.setChestVehicleItem(p_219885_, p_219886_);
   }

   public SlotAccess getSlot(int p_219918_) {
      return this.getChestVehicleSlot(p_219918_);
   }

   public void setChanged() {
   }

   public boolean stillValid(Player p_219896_) {
      return this.isChestVehicleStillValid(p_219896_);
   }

   @Nullable
   public AbstractContainerMenu createMenu(int p_219910_, Inventory p_219911_, Player p_219912_) {
      if (this.lootTable != null && p_219912_.isSpectator()) {
         return null;
      } else {
         this.unpackLootTable(p_219911_.player);
         return ChestMenu.threeRows(p_219910_, p_219911_, this);
      }
   }

   public void unpackLootTable(@Nullable Player p_219914_) {
      this.unpackChestVehicleLootTable(p_219914_);
   }

   @Nullable
   public ResourceLocation getLootTable() {
      return this.lootTable;
   }

   public void setLootTable(@Nullable ResourceLocation p_219890_) {
      this.lootTable = p_219890_;
   }

   public long getLootTableSeed() {
      return this.lootTableSeed;
   }

   public void setLootTableSeed(long p_219888_) {
      this.lootTableSeed = p_219888_;
   }

   public NonNullList<ItemStack> getItemStacks() {
      return this.itemStacks;
   }

   public void clearItemStacks() {
      this.itemStacks = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
   }

   // Forge Start
   private net.minecraftforge.common.util.LazyOptional<?> itemHandler = net.minecraftforge.common.util.LazyOptional.of(() -> new net.minecraftforge.items.wrapper.InvWrapper(this));

   @Override
   public <T> net.minecraftforge.common.util.LazyOptional<T> getCapability(net.minecraftforge.common.capabilities.Capability<T> capability, @Nullable net.minecraft.core.Direction facing) {
      if (this.isAlive() && capability == net.minecraftforge.common.capabilities.ForgeCapabilities.ITEM_HANDLER)
         return itemHandler.cast();
      return super.getCapability(capability, facing);
   }

   @Override
   public void invalidateCaps() {
      super.invalidateCaps();
      itemHandler.invalidate();
   }

   @Override
   public void reviveCaps() {
      super.reviveCaps();
      itemHandler = net.minecraftforge.common.util.LazyOptional.of(() -> new net.minecraftforge.items.wrapper.InvWrapper(this));
   }

   public void stopOpen(Player p_270286_) {
      this.level().gameEvent(GameEvent.CONTAINER_CLOSE, this.position(), GameEvent.Context.of(p_270286_));
   }
}
