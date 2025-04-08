package net.minecraft.world.inventory;

import com.google.common.collect.ImmutableList;
import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BannerPatternTags;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BannerItem;
import net.minecraft.world.item.BannerPatternItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class LoomMenu extends AbstractContainerMenu {
   private static final int PATTERN_NOT_SET = -1;
   private static final int INV_SLOT_START = 4;
   private static final int INV_SLOT_END = 31;
   private static final int USE_ROW_SLOT_START = 31;
   private static final int USE_ROW_SLOT_END = 40;
   private final ContainerLevelAccess access;
   final DataSlot selectedBannerPatternIndex = DataSlot.standalone();
   private List<Holder<BannerPattern>> selectablePatterns = List.of();
   Runnable slotUpdateListener = () -> {
   };
   final Slot bannerSlot;
   final Slot dyeSlot;
   private final Slot patternSlot;
   private final Slot resultSlot;
   long lastSoundTime;
   private final Container inputContainer = new SimpleContainer(3) {
      public void setChanged() {
         super.setChanged();
         LoomMenu.this.slotsChanged(this);
         LoomMenu.this.slotUpdateListener.run();
      }
   };
   private final Container outputContainer = new SimpleContainer(1) {
      public void setChanged() {
         super.setChanged();
         LoomMenu.this.slotUpdateListener.run();
      }
   };

   public LoomMenu(int p_39856_, Inventory p_39857_) {
      this(p_39856_, p_39857_, ContainerLevelAccess.NULL);
   }

   public LoomMenu(int p_39859_, Inventory p_39860_, final ContainerLevelAccess p_39861_) {
      super(MenuType.LOOM, p_39859_);
      this.access = p_39861_;
      this.bannerSlot = this.addSlot(new Slot(this.inputContainer, 0, 13, 26) {
         public boolean mayPlace(ItemStack p_39918_) {
            return p_39918_.getItem() instanceof BannerItem;
         }
      });
      this.dyeSlot = this.addSlot(new Slot(this.inputContainer, 1, 33, 26) {
         public boolean mayPlace(ItemStack p_39927_) {
            return p_39927_.getItem() instanceof DyeItem;
         }
      });
      this.patternSlot = this.addSlot(new Slot(this.inputContainer, 2, 23, 45) {
         public boolean mayPlace(ItemStack p_39936_) {
            return p_39936_.getItem() instanceof BannerPatternItem;
         }
      });
      this.resultSlot = this.addSlot(new Slot(this.outputContainer, 0, 143, 58) {
         public boolean mayPlace(ItemStack p_39950_) {
            return false;
         }

         public void onTake(Player p_150617_, ItemStack p_150618_) {
            LoomMenu.this.bannerSlot.remove(1);
            LoomMenu.this.dyeSlot.remove(1);
            if (!LoomMenu.this.bannerSlot.hasItem() || !LoomMenu.this.dyeSlot.hasItem()) {
               LoomMenu.this.selectedBannerPatternIndex.set(-1);
            }

            p_39861_.execute((p_39952_, p_39953_) -> {
               long l = p_39952_.getGameTime();
               if (LoomMenu.this.lastSoundTime != l) {
                  p_39952_.playSound((Player)null, p_39953_, SoundEvents.UI_LOOM_TAKE_RESULT, SoundSource.BLOCKS, 1.0F, 1.0F);
                  LoomMenu.this.lastSoundTime = l;
               }

            });
            super.onTake(p_150617_, p_150618_);
         }
      });

      for(int i = 0; i < 3; ++i) {
         for(int j = 0; j < 9; ++j) {
            this.addSlot(new Slot(p_39860_, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
         }
      }

      for(int k = 0; k < 9; ++k) {
         this.addSlot(new Slot(p_39860_, k, 8 + k * 18, 142));
      }

      this.addDataSlot(this.selectedBannerPatternIndex);
   }

   public boolean stillValid(Player p_39865_) {
      return stillValid(this.access, p_39865_, Blocks.LOOM);
   }

   public boolean clickMenuButton(Player p_39867_, int p_39868_) {
      if (p_39868_ >= 0 && p_39868_ < this.selectablePatterns.size()) {
         this.selectedBannerPatternIndex.set(p_39868_);
         this.setupResultSlot(this.selectablePatterns.get(p_39868_));
         return true;
      } else {
         return false;
      }
   }

   private List<Holder<BannerPattern>> getSelectablePatterns(ItemStack p_219994_) {
      if (p_219994_.isEmpty()) {
         return BuiltInRegistries.BANNER_PATTERN.getTag(BannerPatternTags.NO_ITEM_REQUIRED).map(ImmutableList::copyOf).orElse(ImmutableList.of());
      } else {
         Item item = p_219994_.getItem();
         if (item instanceof BannerPatternItem) {
            BannerPatternItem bannerpatternitem = (BannerPatternItem)item;
            return BuiltInRegistries.BANNER_PATTERN.getTag(bannerpatternitem.getBannerPattern()).map(ImmutableList::copyOf).orElse(ImmutableList.of());
         } else {
            return List.of();
         }
      }
   }

   private boolean isValidPatternIndex(int p_242850_) {
      return p_242850_ >= 0 && p_242850_ < this.selectablePatterns.size();
   }

   public void slotsChanged(Container p_39863_) {
      ItemStack itemstack = this.bannerSlot.getItem();
      ItemStack itemstack1 = this.dyeSlot.getItem();
      ItemStack itemstack2 = this.patternSlot.getItem();
      if (!itemstack.isEmpty() && !itemstack1.isEmpty()) {
         int i = this.selectedBannerPatternIndex.get();
         boolean flag = this.isValidPatternIndex(i);
         List<Holder<BannerPattern>> list = this.selectablePatterns;
         this.selectablePatterns = this.getSelectablePatterns(itemstack2);
         Holder<BannerPattern> holder;
         if (this.selectablePatterns.size() == 1) {
            this.selectedBannerPatternIndex.set(0);
            holder = this.selectablePatterns.get(0);
         } else if (!flag) {
            this.selectedBannerPatternIndex.set(-1);
            holder = null;
         } else {
            Holder<BannerPattern> holder1 = list.get(i);
            int j = this.selectablePatterns.indexOf(holder1);
            if (j != -1) {
               holder = holder1;
               this.selectedBannerPatternIndex.set(j);
            } else {
               holder = null;
               this.selectedBannerPatternIndex.set(-1);
            }
         }

         if (holder != null) {
            CompoundTag compoundtag = BlockItem.getBlockEntityData(itemstack);
            boolean flag1 = compoundtag != null && compoundtag.contains("Patterns", 9) && !itemstack.isEmpty() && compoundtag.getList("Patterns", 10).size() >= 6;
            if (flag1) {
               this.selectedBannerPatternIndex.set(-1);
               this.resultSlot.set(ItemStack.EMPTY);
            } else {
               this.setupResultSlot(holder);
            }
         } else {
            this.resultSlot.set(ItemStack.EMPTY);
         }

         this.broadcastChanges();
      } else {
         this.resultSlot.set(ItemStack.EMPTY);
         this.selectablePatterns = List.of();
         this.selectedBannerPatternIndex.set(-1);
      }
   }

   public List<Holder<BannerPattern>> getSelectablePatterns() {
      return this.selectablePatterns;
   }

   public int getSelectedBannerPatternIndex() {
      return this.selectedBannerPatternIndex.get();
   }

   public void registerUpdateListener(Runnable p_39879_) {
      this.slotUpdateListener = p_39879_;
   }

   public ItemStack quickMoveStack(Player p_39883_, int p_39884_) {
      ItemStack itemstack = ItemStack.EMPTY;
      Slot slot = this.slots.get(p_39884_);
      if (slot != null && slot.hasItem()) {
         ItemStack itemstack1 = slot.getItem();
         itemstack = itemstack1.copy();
         if (p_39884_ == this.resultSlot.index) {
            if (!this.moveItemStackTo(itemstack1, 4, 40, true)) {
               return ItemStack.EMPTY;
            }

            slot.onQuickCraft(itemstack1, itemstack);
         } else if (p_39884_ != this.dyeSlot.index && p_39884_ != this.bannerSlot.index && p_39884_ != this.patternSlot.index) {
            if (itemstack1.getItem() instanceof BannerItem) {
               if (!this.moveItemStackTo(itemstack1, this.bannerSlot.index, this.bannerSlot.index + 1, false)) {
                  return ItemStack.EMPTY;
               }
            } else if (itemstack1.getItem() instanceof DyeItem) {
               if (!this.moveItemStackTo(itemstack1, this.dyeSlot.index, this.dyeSlot.index + 1, false)) {
                  return ItemStack.EMPTY;
               }
            } else if (itemstack1.getItem() instanceof BannerPatternItem) {
               if (!this.moveItemStackTo(itemstack1, this.patternSlot.index, this.patternSlot.index + 1, false)) {
                  return ItemStack.EMPTY;
               }
            } else if (p_39884_ >= 4 && p_39884_ < 31) {
               if (!this.moveItemStackTo(itemstack1, 31, 40, false)) {
                  return ItemStack.EMPTY;
               }
            } else if (p_39884_ >= 31 && p_39884_ < 40 && !this.moveItemStackTo(itemstack1, 4, 31, false)) {
               return ItemStack.EMPTY;
            }
         } else if (!this.moveItemStackTo(itemstack1, 4, 40, false)) {
            return ItemStack.EMPTY;
         }

         if (itemstack1.isEmpty()) {
            slot.setByPlayer(ItemStack.EMPTY);
         } else {
            slot.setChanged();
         }

         if (itemstack1.getCount() == itemstack.getCount()) {
            return ItemStack.EMPTY;
         }

         slot.onTake(p_39883_, itemstack1);
      }

      return itemstack;
   }

   public void removed(Player p_39881_) {
      super.removed(p_39881_);
      this.access.execute((p_39871_, p_39872_) -> {
         this.clearContainer(p_39881_, this.inputContainer);
      });
   }

   private void setupResultSlot(Holder<BannerPattern> p_219992_) {
      ItemStack itemstack = this.bannerSlot.getItem();
      ItemStack itemstack1 = this.dyeSlot.getItem();
      ItemStack itemstack2 = ItemStack.EMPTY;
      if (!itemstack.isEmpty() && !itemstack1.isEmpty()) {
         itemstack2 = itemstack.copyWithCount(1);
         DyeColor dyecolor = ((DyeItem)itemstack1.getItem()).getDyeColor();
         CompoundTag compoundtag = BlockItem.getBlockEntityData(itemstack2);
         ListTag listtag;
         if (compoundtag != null && compoundtag.contains("Patterns", 9)) {
            listtag = compoundtag.getList("Patterns", 10);
         } else {
            listtag = new ListTag();
            if (compoundtag == null) {
               compoundtag = new CompoundTag();
            }

            compoundtag.put("Patterns", listtag);
         }

         CompoundTag compoundtag1 = new CompoundTag();
         compoundtag1.putString("Pattern", p_219992_.value().getHashname());
         compoundtag1.putInt("Color", dyecolor.getId());
         listtag.add(compoundtag1);
         BlockItem.setBlockEntityData(itemstack2, BlockEntityType.BANNER, compoundtag);
      }

      if (!ItemStack.matches(itemstack2, this.resultSlot.getItem())) {
         this.resultSlot.set(itemstack2);
      }

   }

   public Slot getBannerSlot() {
      return this.bannerSlot;
   }

   public Slot getDyeSlot() {
      return this.dyeSlot;
   }

   public Slot getPatternSlot() {
      return this.patternSlot;
   }

   public Slot getResultSlot() {
      return this.resultSlot;
   }
}