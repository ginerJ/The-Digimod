package net.minecraft.world.level.block.entity;

import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;

public abstract class RandomizableContainerBlockEntity extends BaseContainerBlockEntity {
   public static final String LOOT_TABLE_TAG = "LootTable";
   public static final String LOOT_TABLE_SEED_TAG = "LootTableSeed";
   @Nullable
   protected ResourceLocation lootTable;
   protected long lootTableSeed;

   protected RandomizableContainerBlockEntity(BlockEntityType<?> p_155629_, BlockPos p_155630_, BlockState p_155631_) {
      super(p_155629_, p_155630_, p_155631_);
   }

   public static void setLootTable(BlockGetter p_222767_, RandomSource p_222768_, BlockPos p_222769_, ResourceLocation p_222770_) {
      BlockEntity blockentity = p_222767_.getBlockEntity(p_222769_);
      if (blockentity instanceof RandomizableContainerBlockEntity) {
         ((RandomizableContainerBlockEntity)blockentity).setLootTable(p_222770_, p_222768_.nextLong());
      }

   }

   protected boolean tryLoadLootTable(CompoundTag p_59632_) {
      if (p_59632_.contains("LootTable", 8)) {
         this.lootTable = new ResourceLocation(p_59632_.getString("LootTable"));
         this.lootTableSeed = p_59632_.getLong("LootTableSeed");
         return true;
      } else {
         return false;
      }
   }

   protected boolean trySaveLootTable(CompoundTag p_59635_) {
      if (this.lootTable == null) {
         return false;
      } else {
         p_59635_.putString("LootTable", this.lootTable.toString());
         if (this.lootTableSeed != 0L) {
            p_59635_.putLong("LootTableSeed", this.lootTableSeed);
         }

         return true;
      }
   }

   public void unpackLootTable(@Nullable Player p_59641_) {
      if (this.lootTable != null && this.level.getServer() != null) {
         LootTable loottable = this.level.getServer().getLootData().getLootTable(this.lootTable);
         if (p_59641_ instanceof ServerPlayer) {
            CriteriaTriggers.GENERATE_LOOT.trigger((ServerPlayer)p_59641_, this.lootTable);
         }

         this.lootTable = null;
         LootParams.Builder lootparams$builder = (new LootParams.Builder((ServerLevel)this.level)).withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(this.worldPosition));
         if (p_59641_ != null) {
            lootparams$builder.withLuck(p_59641_.getLuck()).withParameter(LootContextParams.THIS_ENTITY, p_59641_);
         }

         loottable.fill(this, lootparams$builder.create(LootContextParamSets.CHEST), this.lootTableSeed);
      }

   }

   public void setLootTable(ResourceLocation p_59627_, long p_59628_) {
      this.lootTable = p_59627_;
      this.lootTableSeed = p_59628_;
   }

   public boolean isEmpty() {
      this.unpackLootTable((Player)null);
      return this.getItems().stream().allMatch(ItemStack::isEmpty);
   }

   public ItemStack getItem(int p_59611_) {
      this.unpackLootTable((Player)null);
      return this.getItems().get(p_59611_);
   }

   public ItemStack removeItem(int p_59613_, int p_59614_) {
      this.unpackLootTable((Player)null);
      ItemStack itemstack = ContainerHelper.removeItem(this.getItems(), p_59613_, p_59614_);
      if (!itemstack.isEmpty()) {
         this.setChanged();
      }

      return itemstack;
   }

   public ItemStack removeItemNoUpdate(int p_59630_) {
      this.unpackLootTable((Player)null);
      return ContainerHelper.takeItem(this.getItems(), p_59630_);
   }

   public void setItem(int p_59616_, ItemStack p_59617_) {
      this.unpackLootTable((Player)null);
      this.getItems().set(p_59616_, p_59617_);
      if (p_59617_.getCount() > this.getMaxStackSize()) {
         p_59617_.setCount(this.getMaxStackSize());
      }

      this.setChanged();
   }

   public boolean stillValid(Player p_59619_) {
      return Container.stillValidBlockEntity(this, p_59619_);
   }

   public void clearContent() {
      this.getItems().clear();
   }

   protected abstract NonNullList<ItemStack> getItems();

   protected abstract void setItems(NonNullList<ItemStack> p_59625_);

   public boolean canOpen(Player p_59643_) {
      return super.canOpen(p_59643_) && (this.lootTable == null || !p_59643_.isSpectator());
   }

   @Nullable
   public AbstractContainerMenu createMenu(int p_59637_, Inventory p_59638_, Player p_59639_) {
      if (this.canOpen(p_59639_)) {
         this.unpackLootTable(p_59638_.player);
         return this.createMenu(p_59637_, p_59638_);
      } else {
         return null;
      }
   }
}