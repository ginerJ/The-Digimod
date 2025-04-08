package net.minecraft.client.gui.screens.inventory;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.datafixers.util.Pair;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.HotbarManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.player.inventory.Hotbar;
import net.minecraft.client.searchtree.SearchRegistry;
import net.minecraft.client.searchtree.SearchTree;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CreativeModeInventoryScreen extends EffectRenderingInventoryScreen<CreativeModeInventoryScreen.ItemPickerMenu> {
   private static final ResourceLocation CREATIVE_TABS_LOCATION = new ResourceLocation("textures/gui/container/creative_inventory/tabs.png");
   private static final String GUI_CREATIVE_TAB_PREFIX = "textures/gui/container/creative_inventory/tab_";
   private static final String CUSTOM_SLOT_LOCK = "CustomCreativeLock";
   private static final int NUM_ROWS = 5;
   private static final int NUM_COLS = 9;
   private static final int TAB_WIDTH = 26;
   private static final int TAB_HEIGHT = 32;
   private static final int SCROLLER_WIDTH = 12;
   private static final int SCROLLER_HEIGHT = 15;
   static final SimpleContainer CONTAINER = new SimpleContainer(45);
   private static final Component TRASH_SLOT_TOOLTIP = Component.translatable("inventory.binSlot");
   private static final int TEXT_COLOR = 16777215;
   private static CreativeModeTab selectedTab = CreativeModeTabs.getDefaultTab();
   private float scrollOffs;
   private boolean scrolling;
   private EditBox searchBox;
   @Nullable
   private List<Slot> originalSlots;
   @Nullable
   private Slot destroyItemSlot;
   private CreativeInventoryListener listener;
   private boolean ignoreTextInput;
   private boolean hasClickedOutside;
   private final Set<TagKey<Item>> visibleTags = new HashSet<>();
   private final boolean displayOperatorCreativeTab;
   private final List<net.minecraftforge.client.gui.CreativeTabsScreenPage> pages = new java.util.ArrayList<>();
   private net.minecraftforge.client.gui.CreativeTabsScreenPage currentPage = new net.minecraftforge.client.gui.CreativeTabsScreenPage(new java.util.ArrayList<>());

   public CreativeModeInventoryScreen(Player p_259788_, FeatureFlagSet p_260074_, boolean p_259569_) {
      super(new CreativeModeInventoryScreen.ItemPickerMenu(p_259788_), p_259788_.getInventory(), CommonComponents.EMPTY);
      p_259788_.containerMenu = this.menu;
      this.imageHeight = 136;
      this.imageWidth = 195;
      this.displayOperatorCreativeTab = p_259569_;
      CreativeModeTabs.tryRebuildTabContents(p_260074_, this.hasPermissions(p_259788_), p_259788_.level().registryAccess());
   }

   private boolean hasPermissions(Player p_259959_) {
      return p_259959_.canUseGameMasterBlocks() && this.displayOperatorCreativeTab;
   }

   private void tryRefreshInvalidatedTabs(FeatureFlagSet p_259501_, boolean p_259713_, HolderLookup.Provider p_270898_) {
      if (CreativeModeTabs.tryRebuildTabContents(p_259501_, p_259713_, p_270898_)) {
         for(CreativeModeTab creativemodetab : CreativeModeTabs.allTabs()) {
            Collection<ItemStack> collection = creativemodetab.getDisplayItems();
            if (creativemodetab == selectedTab) {
               if (creativemodetab.getType() == CreativeModeTab.Type.CATEGORY && collection.isEmpty()) {
                  this.selectTab(CreativeModeTabs.getDefaultTab());
               } else {
                  this.refreshCurrentTabContents(collection);
               }
            }
         }
      }

   }

   private void refreshCurrentTabContents(Collection<ItemStack> p_261591_) {
      int i = this.menu.getRowIndexForScroll(this.scrollOffs);
      (this.menu).items.clear();
      if (selectedTab.hasSearchBar()) {
         this.refreshSearchResults();
      } else {
         (this.menu).items.addAll(p_261591_);
      }

      this.scrollOffs = this.menu.getScrollForRowIndex(i);
      this.menu.scrollTo(this.scrollOffs);
   }

   public void containerTick() {
      super.containerTick();
      if (this.minecraft != null) {
         if (this.minecraft.player != null) {
            this.tryRefreshInvalidatedTabs(this.minecraft.player.connection.enabledFeatures(), this.hasPermissions(this.minecraft.player), this.minecraft.player.level().registryAccess());
         }

         if (!this.minecraft.gameMode.hasInfiniteItems()) {
            this.minecraft.setScreen(new InventoryScreen(this.minecraft.player));
         } else {
            this.searchBox.tick();
         }

      }
   }

   protected void slotClicked(@Nullable Slot p_98556_, int p_98557_, int p_98558_, ClickType p_98559_) {
      if (this.isCreativeSlot(p_98556_)) {
         this.searchBox.moveCursorToEnd();
         this.searchBox.setHighlightPos(0);
      }

      boolean flag = p_98559_ == ClickType.QUICK_MOVE;
      p_98559_ = p_98557_ == -999 && p_98559_ == ClickType.PICKUP ? ClickType.THROW : p_98559_;
      if (p_98556_ == null && selectedTab.getType() != CreativeModeTab.Type.INVENTORY && p_98559_ != ClickType.QUICK_CRAFT) {
         if (!this.menu.getCarried().isEmpty() && this.hasClickedOutside) {
            if (p_98558_ == 0) {
               this.minecraft.player.drop(this.menu.getCarried(), true);
               this.minecraft.gameMode.handleCreativeModeItemDrop(this.menu.getCarried());
               this.menu.setCarried(ItemStack.EMPTY);
            }

            if (p_98558_ == 1) {
               ItemStack itemstack5 = this.menu.getCarried().split(1);
               this.minecraft.player.drop(itemstack5, true);
               this.minecraft.gameMode.handleCreativeModeItemDrop(itemstack5);
            }
         }
      } else {
         if (p_98556_ != null && !p_98556_.mayPickup(this.minecraft.player)) {
            return;
         }

         if (p_98556_ == this.destroyItemSlot && flag) {
            for(int j = 0; j < this.minecraft.player.inventoryMenu.getItems().size(); ++j) {
               this.minecraft.gameMode.handleCreativeModeItemAdd(ItemStack.EMPTY, j);
            }
         } else if (selectedTab.getType() == CreativeModeTab.Type.INVENTORY) {
            if (p_98556_ == this.destroyItemSlot) {
               this.menu.setCarried(ItemStack.EMPTY);
            } else if (p_98559_ == ClickType.THROW && p_98556_ != null && p_98556_.hasItem()) {
               ItemStack itemstack = p_98556_.remove(p_98558_ == 0 ? 1 : p_98556_.getItem().getMaxStackSize());
               ItemStack itemstack1 = p_98556_.getItem();
               this.minecraft.player.drop(itemstack, true);
               this.minecraft.gameMode.handleCreativeModeItemDrop(itemstack);
               this.minecraft.gameMode.handleCreativeModeItemAdd(itemstack1, ((CreativeModeInventoryScreen.SlotWrapper)p_98556_).target.index);
            } else if (p_98559_ == ClickType.THROW && !this.menu.getCarried().isEmpty()) {
               this.minecraft.player.drop(this.menu.getCarried(), true);
               this.minecraft.gameMode.handleCreativeModeItemDrop(this.menu.getCarried());
               this.menu.setCarried(ItemStack.EMPTY);
            } else {
               this.minecraft.player.inventoryMenu.clicked(p_98556_ == null ? p_98557_ : ((CreativeModeInventoryScreen.SlotWrapper)p_98556_).target.index, p_98558_, p_98559_, this.minecraft.player);
               this.minecraft.player.inventoryMenu.broadcastChanges();
            }
         } else if (p_98559_ != ClickType.QUICK_CRAFT && p_98556_.container == CONTAINER) {
            ItemStack itemstack4 = this.menu.getCarried();
            ItemStack itemstack7 = p_98556_.getItem();
            if (p_98559_ == ClickType.SWAP) {
               if (!itemstack7.isEmpty()) {
                  this.minecraft.player.getInventory().setItem(p_98558_, itemstack7.copyWithCount(itemstack7.getMaxStackSize()));
                  this.minecraft.player.inventoryMenu.broadcastChanges();
               }

               return;
            }

            if (p_98559_ == ClickType.CLONE) {
               if (this.menu.getCarried().isEmpty() && p_98556_.hasItem()) {
                  ItemStack itemstack9 = p_98556_.getItem();
                  this.menu.setCarried(itemstack9.copyWithCount(itemstack9.getMaxStackSize()));
               }

               return;
            }

            if (p_98559_ == ClickType.THROW) {
               if (!itemstack7.isEmpty()) {
                  ItemStack itemstack8 = itemstack7.copyWithCount(p_98558_ == 0 ? 1 : itemstack7.getMaxStackSize());
                  this.minecraft.player.drop(itemstack8, true);
                  this.minecraft.gameMode.handleCreativeModeItemDrop(itemstack8);
               }

               return;
            }

            if (!itemstack4.isEmpty() && !itemstack7.isEmpty() && ItemStack.isSameItemSameTags(itemstack4, itemstack7)) {
               if (p_98558_ == 0) {
                  if (flag) {
                     itemstack4.setCount(itemstack4.getMaxStackSize());
                  } else if (itemstack4.getCount() < itemstack4.getMaxStackSize()) {
                     itemstack4.grow(1);
                  }
               } else {
                  itemstack4.shrink(1);
               }
            } else if (!itemstack7.isEmpty() && itemstack4.isEmpty()) {
               int l = flag ? itemstack7.getMaxStackSize() : itemstack7.getCount();
               this.menu.setCarried(itemstack7.copyWithCount(l));
            } else if (p_98558_ == 0) {
               this.menu.setCarried(ItemStack.EMPTY);
            } else if (!this.menu.getCarried().isEmpty()) {
               this.menu.getCarried().shrink(1);
            }
         } else if (this.menu != null) {
            ItemStack itemstack3 = p_98556_ == null ? ItemStack.EMPTY : this.menu.getSlot(p_98556_.index).getItem();
            this.menu.clicked(p_98556_ == null ? p_98557_ : p_98556_.index, p_98558_, p_98559_, this.minecraft.player);
            if (AbstractContainerMenu.getQuickcraftHeader(p_98558_) == 2) {
               for(int k = 0; k < 9; ++k) {
                  this.minecraft.gameMode.handleCreativeModeItemAdd(this.menu.getSlot(45 + k).getItem(), 36 + k);
               }
            } else if (p_98556_ != null) {
               ItemStack itemstack6 = this.menu.getSlot(p_98556_.index).getItem();
               this.minecraft.gameMode.handleCreativeModeItemAdd(itemstack6, p_98556_.index - (this.menu).slots.size() + 9 + 36);
               int i = 45 + p_98558_;
               if (p_98559_ == ClickType.SWAP) {
                  this.minecraft.gameMode.handleCreativeModeItemAdd(itemstack3, i - (this.menu).slots.size() + 9 + 36);
               } else if (p_98559_ == ClickType.THROW && !itemstack3.isEmpty()) {
                  ItemStack itemstack2 = itemstack3.copyWithCount(p_98558_ == 0 ? 1 : itemstack3.getMaxStackSize());
                  this.minecraft.player.drop(itemstack2, true);
                  this.minecraft.gameMode.handleCreativeModeItemDrop(itemstack2);
               }

               this.minecraft.player.inventoryMenu.broadcastChanges();
            }
         }
      }

   }

   private boolean isCreativeSlot(@Nullable Slot p_98554_) {
      return p_98554_ != null && p_98554_.container == CONTAINER;
   }

   protected void init() {
      if (this.minecraft.gameMode.hasInfiniteItems()) {
         super.init();
         this.pages.clear();
         int tabIndex = 0;
         List<CreativeModeTab> currentPage = new java.util.ArrayList<>();
         for (CreativeModeTab sortedCreativeModeTab : net.minecraftforge.common.CreativeModeTabRegistry.getSortedCreativeModeTabs()) {
            currentPage.add(sortedCreativeModeTab);
            tabIndex++;
            if (tabIndex == 10) {
               this.pages.add(new net.minecraftforge.client.gui.CreativeTabsScreenPage(currentPage));
               currentPage = new java.util.ArrayList<>();
               tabIndex = 0;
            }
         }
         if (tabIndex != 0) {
            this.pages.add(new net.minecraftforge.client.gui.CreativeTabsScreenPage(currentPage));
         }
         if (this.pages.isEmpty()) {
            this.currentPage = new net.minecraftforge.client.gui.CreativeTabsScreenPage(new java.util.ArrayList<>());
         } else {
            this.currentPage = this.pages.get(0);
         }
         if (this.pages.size() > 1) {
            addRenderableWidget(net.minecraft.client.gui.components.Button.builder(Component.literal("<"), b -> setCurrentPage(this.pages.get(Math.max(this.pages.indexOf(this.currentPage) - 1, 0)))).pos(leftPos,  topPos - 50).size(20, 20).build());
            addRenderableWidget(net.minecraft.client.gui.components.Button.builder(Component.literal(">"), b -> setCurrentPage(this.pages.get(Math.min(this.pages.indexOf(this.currentPage) + 1, this.pages.size() - 1)))).pos(leftPos + imageWidth - 20, topPos - 50).size(20, 20).build());
         }
         this.currentPage = this.pages.stream().filter(page -> page.getVisibleTabs().contains(selectedTab)).findFirst().orElse(this.currentPage);
         if (!this.currentPage.getVisibleTabs().contains(selectedTab)) {
            selectedTab = this.currentPage.getVisibleTabs().get(0);
         }
         this.searchBox = new EditBox(this.font, this.leftPos + 82, this.topPos + 6, 80, 9, Component.translatable("itemGroup.search"));
         this.searchBox.setMaxLength(50);
         this.searchBox.setBordered(false);
         this.searchBox.setVisible(false);
         this.searchBox.setTextColor(16777215);
         this.addWidget(this.searchBox);
         CreativeModeTab creativemodetab = selectedTab;
         selectedTab = CreativeModeTabs.getDefaultTab();
         this.selectTab(creativemodetab);
         this.minecraft.player.inventoryMenu.removeSlotListener(this.listener);
         this.listener = new CreativeInventoryListener(this.minecraft);
         this.minecraft.player.inventoryMenu.addSlotListener(this.listener);
         if (!selectedTab.shouldDisplay()) {
            this.selectTab(CreativeModeTabs.getDefaultTab());
         }
      } else {
         this.minecraft.setScreen(new InventoryScreen(this.minecraft.player));
      }

   }

   public void resize(Minecraft p_98595_, int p_98596_, int p_98597_) {
      int i = this.menu.getRowIndexForScroll(this.scrollOffs);
      String s = this.searchBox.getValue();
      this.init(p_98595_, p_98596_, p_98597_);
      this.searchBox.setValue(s);
      if (!this.searchBox.getValue().isEmpty()) {
         this.refreshSearchResults();
      }

      this.scrollOffs = this.menu.getScrollForRowIndex(i);
      this.menu.scrollTo(this.scrollOffs);
   }

   public void removed() {
      super.removed();
      if (this.minecraft.player != null && this.minecraft.player.getInventory() != null) {
         this.minecraft.player.inventoryMenu.removeSlotListener(this.listener);
      }

   }

   public boolean charTyped(char p_98521_, int p_98522_) {
      if (this.ignoreTextInput) {
         return false;
      } else if (!selectedTab.hasSearchBar()) {
         return false;
      } else {
         String s = this.searchBox.getValue();
         if (this.searchBox.charTyped(p_98521_, p_98522_)) {
            if (!Objects.equals(s, this.searchBox.getValue())) {
               this.refreshSearchResults();
            }

            return true;
         } else {
            return false;
         }
      }
   }

   public boolean keyPressed(int p_98547_, int p_98548_, int p_98549_) {
      this.ignoreTextInput = false;
      if (!selectedTab.hasSearchBar()) {
         if (this.minecraft.options.keyChat.matches(p_98547_, p_98548_)) {
            this.ignoreTextInput = true;
            this.selectTab(CreativeModeTabs.searchTab());
            return true;
         } else {
            return super.keyPressed(p_98547_, p_98548_, p_98549_);
         }
      } else {
         boolean flag = !this.isCreativeSlot(this.hoveredSlot) || this.hoveredSlot.hasItem();
         boolean flag1 = InputConstants.getKey(p_98547_, p_98548_).getNumericKeyValue().isPresent();
         if (flag && flag1 && this.checkHotbarKeyPressed(p_98547_, p_98548_)) {
            this.ignoreTextInput = true;
            return true;
         } else {
            String s = this.searchBox.getValue();
            if (this.searchBox.keyPressed(p_98547_, p_98548_, p_98549_)) {
               if (!Objects.equals(s, this.searchBox.getValue())) {
                  this.refreshSearchResults();
               }

               return true;
            } else {
               return this.searchBox.isFocused() && this.searchBox.isVisible() && p_98547_ != 256 ? true : super.keyPressed(p_98547_, p_98548_, p_98549_);
            }
         }
      }
   }

   public boolean keyReleased(int p_98612_, int p_98613_, int p_98614_) {
      this.ignoreTextInput = false;
      return super.keyReleased(p_98612_, p_98613_, p_98614_);
   }

   private void refreshSearchResults() {
      if (!selectedTab.hasSearchBar()) return;
      (this.menu).items.clear();
      this.visibleTags.clear();
      String s = this.searchBox.getValue();
      if (s.isEmpty()) {
         (this.menu).items.addAll(selectedTab.getDisplayItems());
      } else {
         SearchTree<ItemStack> searchtree;
         if (s.startsWith("#")) {
            s = s.substring(1);
            searchtree = this.minecraft.getSearchTree(net.minecraftforge.client.CreativeModeTabSearchRegistry.getTagSearchKey(selectedTab));
            this.updateVisibleTags(s);
         } else {
            searchtree = this.minecraft.getSearchTree(net.minecraftforge.client.CreativeModeTabSearchRegistry.getNameSearchKey(selectedTab));
         }

         (this.menu).items.addAll(searchtree.search(s.toLowerCase(Locale.ROOT)));
      }

      this.scrollOffs = 0.0F;
      this.menu.scrollTo(0.0F);
   }

   private void updateVisibleTags(String p_98620_) {
      int i = p_98620_.indexOf(58);
      Predicate<ResourceLocation> predicate;
      if (i == -1) {
         predicate = (p_98609_) -> {
            return p_98609_.getPath().contains(p_98620_);
         };
      } else {
         String s = p_98620_.substring(0, i).trim();
         String s1 = p_98620_.substring(i + 1).trim();
         predicate = (p_98606_) -> {
            return p_98606_.getNamespace().contains(s) && p_98606_.getPath().contains(s1);
         };
      }

      BuiltInRegistries.ITEM.getTagNames().filter((p_205410_) -> {
         return predicate.test(p_205410_.location());
      }).forEach(this.visibleTags::add);
   }

   protected void renderLabels(GuiGraphics p_283168_, int p_281774_, int p_281466_) {
      if (selectedTab.showTitle()) {
         com.mojang.blaze3d.systems.RenderSystem.disableBlend();
         p_283168_.drawString(this.font, selectedTab.getDisplayName(), 8, 6, selectedTab.getLabelColor(), false);
      }

   }

   public boolean mouseClicked(double p_98531_, double p_98532_, int p_98533_) {
      if (p_98533_ == 0) {
         double d0 = p_98531_ - (double)this.leftPos;
         double d1 = p_98532_ - (double)this.topPos;

         for(CreativeModeTab creativemodetab : currentPage.getVisibleTabs()) {
            if (this.checkTabClicked(creativemodetab, d0, d1)) {
               return true;
            }
         }

         if (selectedTab.getType() != CreativeModeTab.Type.INVENTORY && this.insideScrollbar(p_98531_, p_98532_)) {
            this.scrolling = this.canScroll();
            return true;
         }
      }

      return super.mouseClicked(p_98531_, p_98532_, p_98533_);
   }

   public boolean mouseReleased(double p_98622_, double p_98623_, int p_98624_) {
      if (p_98624_ == 0) {
         double d0 = p_98622_ - (double)this.leftPos;
         double d1 = p_98623_ - (double)this.topPos;
         this.scrolling = false;

         for(CreativeModeTab creativemodetab : currentPage.getVisibleTabs()) {
            if (this.checkTabClicked(creativemodetab, d0, d1)) {
               this.selectTab(creativemodetab);
               return true;
            }
         }
      }

      return super.mouseReleased(p_98622_, p_98623_, p_98624_);
   }

   private boolean canScroll() {
      return selectedTab.canScroll() && this.menu.canScroll();
   }

   private void selectTab(CreativeModeTab p_98561_) {
      CreativeModeTab creativemodetab = selectedTab;
      selectedTab = p_98561_;
      slotColor = p_98561_.getSlotColor();
      this.quickCraftSlots.clear();
      (this.menu).items.clear();
      this.clearDraggingState();
      if (selectedTab.getType() == CreativeModeTab.Type.HOTBAR) {
         HotbarManager hotbarmanager = this.minecraft.getHotbarManager();

         for(int i = 0; i < 9; ++i) {
            Hotbar hotbar = hotbarmanager.get(i);
            if (hotbar.isEmpty()) {
               for(int j = 0; j < 9; ++j) {
                  if (j == i) {
                     ItemStack itemstack = new ItemStack(Items.PAPER);
                     itemstack.getOrCreateTagElement("CustomCreativeLock");
                     Component component = this.minecraft.options.keyHotbarSlots[i].getTranslatedKeyMessage();
                     Component component1 = this.minecraft.options.keySaveHotbarActivator.getTranslatedKeyMessage();
                     itemstack.setHoverName(Component.translatable("inventory.hotbarInfo", component1, component));
                     (this.menu).items.add(itemstack);
                  } else {
                     (this.menu).items.add(ItemStack.EMPTY);
                  }
               }
            } else {
               (this.menu).items.addAll(hotbar);
            }
         }
      } else if (selectedTab.getType() == CreativeModeTab.Type.CATEGORY) {
         (this.menu).items.addAll(selectedTab.getDisplayItems());
      }

      if (selectedTab.getType() == CreativeModeTab.Type.INVENTORY) {
         AbstractContainerMenu abstractcontainermenu = this.minecraft.player.inventoryMenu;
         if (this.originalSlots == null) {
            this.originalSlots = ImmutableList.copyOf((this.menu).slots);
         }

         (this.menu).slots.clear();

         for(int k = 0; k < abstractcontainermenu.slots.size(); ++k) {
            int l;
            int i1;
            if (k >= 5 && k < 9) {
               int k1 = k - 5;
               int i2 = k1 / 2;
               int k2 = k1 % 2;
               l = 54 + i2 * 54;
               i1 = 6 + k2 * 27;
            } else if (k >= 0 && k < 5) {
               l = -2000;
               i1 = -2000;
            } else if (k == 45) {
               l = 35;
               i1 = 20;
            } else {
               int j1 = k - 9;
               int l1 = j1 % 9;
               int j2 = j1 / 9;
               l = 9 + l1 * 18;
               if (k >= 36) {
                  i1 = 112;
               } else {
                  i1 = 54 + j2 * 18;
               }
            }

            Slot slot = new CreativeModeInventoryScreen.SlotWrapper(abstractcontainermenu.slots.get(k), k, l, i1);
            (this.menu).slots.add(slot);
         }

         this.destroyItemSlot = new Slot(CONTAINER, 0, 173, 112);
         (this.menu).slots.add(this.destroyItemSlot);
      } else if (creativemodetab.getType() == CreativeModeTab.Type.INVENTORY) {
         (this.menu).slots.clear();
         (this.menu).slots.addAll(this.originalSlots);
         this.originalSlots = null;
      }

      if (selectedTab.hasSearchBar()) {
         this.searchBox.setVisible(true);
         this.searchBox.setCanLoseFocus(false);
         this.searchBox.setFocused(true);
         if (creativemodetab != p_98561_) {
            this.searchBox.setValue("");
         }
         this.searchBox.setWidth(selectedTab.getSearchBarWidth());
         this.searchBox.setX(this.leftPos + (82 /*default left*/ + 89 /*default width*/) - this.searchBox.getWidth());

         this.refreshSearchResults();
      } else {
         this.searchBox.setVisible(false);
         this.searchBox.setCanLoseFocus(true);
         this.searchBox.setFocused(false);
         this.searchBox.setValue("");
      }

      this.scrollOffs = 0.0F;
      this.menu.scrollTo(0.0F);
   }

   public boolean mouseScrolled(double p_98527_, double p_98528_, double p_98529_) {
      if (!this.canScroll()) {
         return false;
      } else {
         this.scrollOffs = this.menu.subtractInputFromScroll(this.scrollOffs, p_98529_);
         this.menu.scrollTo(this.scrollOffs);
         return true;
      }
   }

   protected boolean hasClickedOutside(double p_98541_, double p_98542_, int p_98543_, int p_98544_, int p_98545_) {
      boolean flag = p_98541_ < (double)p_98543_ || p_98542_ < (double)p_98544_ || p_98541_ >= (double)(p_98543_ + this.imageWidth) || p_98542_ >= (double)(p_98544_ + this.imageHeight);
      this.hasClickedOutside = flag && !this.checkTabClicked(selectedTab, p_98541_, p_98542_);
      return this.hasClickedOutside;
   }

   protected boolean insideScrollbar(double p_98524_, double p_98525_) {
      int i = this.leftPos;
      int j = this.topPos;
      int k = i + 175;
      int l = j + 18;
      int i1 = k + 14;
      int j1 = l + 112;
      return p_98524_ >= (double)k && p_98525_ >= (double)l && p_98524_ < (double)i1 && p_98525_ < (double)j1;
   }

   public boolean mouseDragged(double p_98535_, double p_98536_, int p_98537_, double p_98538_, double p_98539_) {
      if (this.scrolling) {
         int i = this.topPos + 18;
         int j = i + 112;
         this.scrollOffs = ((float)p_98536_ - (float)i - 7.5F) / ((float)(j - i) - 15.0F);
         this.scrollOffs = Mth.clamp(this.scrollOffs, 0.0F, 1.0F);
         this.menu.scrollTo(this.scrollOffs);
         return true;
      } else {
         return super.mouseDragged(p_98535_, p_98536_, p_98537_, p_98538_, p_98539_);
      }
   }

   public void render(GuiGraphics p_283000_, int p_281317_, int p_282770_, float p_281295_) {
      this.renderBackground(p_283000_);
      super.render(p_283000_, p_281317_, p_282770_, p_281295_);

      for(CreativeModeTab creativemodetab : currentPage.getVisibleTabs()) {
         if (this.checkTabHovering(p_283000_, creativemodetab, p_281317_, p_282770_)) {
            break;
         }
      }

      if (this.destroyItemSlot != null && selectedTab.getType() == CreativeModeTab.Type.INVENTORY && this.isHovering(this.destroyItemSlot.x, this.destroyItemSlot.y, 16, 16, (double)p_281317_, (double)p_282770_)) {
         p_283000_.renderTooltip(this.font, TRASH_SLOT_TOOLTIP, p_281317_, p_282770_);
      }

      if (this.pages.size() != 1) {
         Component page = Component.literal(String.format("%d / %d", this.pages.indexOf(this.currentPage) + 1, this.pages.size()));
         p_283000_.pose().pushPose();
         p_283000_.pose().translate(0F, 0F, 300F);
         p_283000_.drawString(font, page.getVisualOrderText(), leftPos + (imageWidth / 2) - (font.width(page) / 2), topPos - 44, -1);
         p_283000_.pose().popPose();
      }

      com.mojang.blaze3d.systems.RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      this.renderTooltip(p_283000_, p_281317_, p_282770_);
   }

   public List<Component> getTooltipFromContainerItem(ItemStack p_281769_) {
      boolean flag = this.hoveredSlot != null && this.hoveredSlot instanceof CreativeModeInventoryScreen.CustomCreativeSlot;
      boolean flag1 = selectedTab.getType() == CreativeModeTab.Type.CATEGORY;
      boolean flag2 = selectedTab.hasSearchBar();
      TooltipFlag.Default tooltipflag$default = this.minecraft.options.advancedItemTooltips ? TooltipFlag.Default.ADVANCED : TooltipFlag.Default.NORMAL;
      TooltipFlag tooltipflag = flag ? tooltipflag$default.asCreative() : tooltipflag$default;
      List<Component> list = p_281769_.getTooltipLines(this.minecraft.player, tooltipflag);
      if (flag1 && flag) {
         return list;
      } else {
         List<Component> list1 = Lists.newArrayList(list);
         if (flag2 && flag) {
            this.visibleTags.forEach((p_205407_) -> {
               if (p_281769_.is(p_205407_)) {
                  list1.add(1, Component.literal("#" + p_205407_.location()).withStyle(ChatFormatting.DARK_PURPLE));
               }

            });
         }

         int i = 1;

         for(CreativeModeTab creativemodetab : CreativeModeTabs.tabs()) {
            if (!creativemodetab.hasSearchBar() && creativemodetab.contains(p_281769_)) {
               list1.add(i++, creativemodetab.getDisplayName().copy().withStyle(ChatFormatting.BLUE));
            }
         }

         return list1;
      }
   }

   protected void renderBg(GuiGraphics p_282663_, float p_282504_, int p_282089_, int p_282249_) {
      for(CreativeModeTab creativemodetab : currentPage.getVisibleTabs()) {
         if (creativemodetab != selectedTab) {
            this.renderTabButton(p_282663_, creativemodetab);
         }
      }

      p_282663_.blit(selectedTab.getBackgroundLocation(), this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
      this.searchBox.render(p_282663_, p_282089_, p_282249_, p_282504_);
      int j = this.leftPos + 175;
      int k = this.topPos + 18;
      int i = k + 112;
      if (selectedTab.canScroll()) {
         p_282663_.blit(selectedTab.getTabsImage(), j, k + (int)((float)(i - k - 17) * this.scrollOffs), 232 + (this.canScroll() ? 0 : 12), 0, 12, 15);
      }

      if (currentPage.getVisibleTabs().contains(selectedTab)) //Forge: only display tab selection when the selected tab is on the current page
      this.renderTabButton(p_282663_, selectedTab);
      if (selectedTab.getType() == CreativeModeTab.Type.INVENTORY) {
         InventoryScreen.renderEntityInInventoryFollowsMouse(p_282663_, this.leftPos + 88, this.topPos + 45, 20, (float)(this.leftPos + 88 - p_282089_), (float)(this.topPos + 45 - 30 - p_282249_), this.minecraft.player);
      }

   }

   private int getTabX(CreativeModeTab p_260136_) {
      int i = currentPage.getColumn(p_260136_);
      int j = 27;
      int k = 27 * i;
      if (p_260136_.isAlignedRight()) {
         k = this.imageWidth - 27 * (7 - i) + 1;
      }

      return k;
   }

   private int getTabY(CreativeModeTab p_260181_) {
      int i = 0;
      if (currentPage.isTop(p_260181_)) {
         i -= 32;
      } else {
         i += this.imageHeight;
      }

      return i;
   }

   protected boolean checkTabClicked(CreativeModeTab p_98563_, double p_98564_, double p_98565_) {
      int i = this.getTabX(p_98563_);
      int j = this.getTabY(p_98563_);
      return p_98564_ >= (double)i && p_98564_ <= (double)(i + 26) && p_98565_ >= (double)j && p_98565_ <= (double)(j + 32);
   }

   protected boolean checkTabHovering(GuiGraphics p_282317_, CreativeModeTab p_282244_, int p_283469_, int p_283411_) {
      int i = this.getTabX(p_282244_);
      int j = this.getTabY(p_282244_);
      if (this.isHovering(i + 3, j + 3, 21, 27, (double)p_283469_, (double)p_283411_)) {
         p_282317_.renderTooltip(this.font, p_282244_.getDisplayName(), p_283469_, p_283411_);
         return true;
      } else {
         return false;
      }
   }

   protected void renderTabButton(GuiGraphics p_283590_, CreativeModeTab p_283489_) {
      boolean flag = p_283489_ == selectedTab;
      boolean flag1 = currentPage.isTop(p_283489_);
      int i = currentPage.getColumn(p_283489_);
      int j = i * 26;
      int k = 0;
      int l = this.leftPos + this.getTabX(p_283489_);
      int i1 = this.topPos;
      int j1 = 32;
      if (flag) {
         k += 32;
      }

      if (flag1) {
         i1 -= 28;
      } else {
         k += 64;
         i1 += this.imageHeight - 4;
      }

      com.mojang.blaze3d.systems.RenderSystem.enableBlend(); //Forge: Make sure blend is enabled else tabs show a white border.
      p_283590_.blit(p_283489_.getTabsImage(), l, i1, j, k, 26, 32);
      p_283590_.pose().pushPose();
      p_283590_.pose().translate(0.0F, 0.0F, 100.0F);
      l += 5;
      i1 += 8 + (flag1 ? 1 : -1);
      ItemStack itemstack = p_283489_.getIconItem();
      p_283590_.renderItem(itemstack, l, i1);
      p_283590_.renderItemDecorations(this.font, itemstack, l, i1);
      p_283590_.pose().popPose();
   }

   public boolean isInventoryOpen() {
      return selectedTab.getType() == CreativeModeTab.Type.INVENTORY;
   }

   public static void handleHotbarLoadOrSave(Minecraft p_98599_, int p_98600_, boolean p_98601_, boolean p_98602_) {
      LocalPlayer localplayer = p_98599_.player;
      HotbarManager hotbarmanager = p_98599_.getHotbarManager();
      Hotbar hotbar = hotbarmanager.get(p_98600_);
      if (p_98601_) {
         for(int i = 0; i < Inventory.getSelectionSize(); ++i) {
            ItemStack itemstack = hotbar.get(i);
            ItemStack itemstack1 = itemstack.isItemEnabled(localplayer.level().enabledFeatures()) ? itemstack.copy() : ItemStack.EMPTY;
            localplayer.getInventory().setItem(i, itemstack1);
            p_98599_.gameMode.handleCreativeModeItemAdd(itemstack1, 36 + i);
         }

         localplayer.inventoryMenu.broadcastChanges();
      } else if (p_98602_) {
         for(int j = 0; j < Inventory.getSelectionSize(); ++j) {
            hotbar.set(j, localplayer.getInventory().getItem(j).copy());
         }

         Component component = p_98599_.options.keyHotbarSlots[p_98600_].getTranslatedKeyMessage();
         Component component1 = p_98599_.options.keyLoadHotbarActivator.getTranslatedKeyMessage();
         Component component2 = Component.translatable("inventory.hotbarSaved", component1, component);
         p_98599_.gui.setOverlayMessage(component2, false);
         p_98599_.getNarrator().sayNow(component2);
         hotbarmanager.save();
      }

   }

   public net.minecraftforge.client.gui.CreativeTabsScreenPage getCurrentPage() {
      return currentPage;
   }

   public void setCurrentPage(net.minecraftforge.client.gui.CreativeTabsScreenPage currentPage) {
      this.currentPage = currentPage;
   }

   @OnlyIn(Dist.CLIENT)
   static class CustomCreativeSlot extends Slot {
      public CustomCreativeSlot(Container p_98633_, int p_98634_, int p_98635_, int p_98636_) {
         super(p_98633_, p_98634_, p_98635_, p_98636_);
      }

      public boolean mayPickup(Player p_98638_) {
         ItemStack itemstack = this.getItem();
         if (super.mayPickup(p_98638_) && !itemstack.isEmpty()) {
            return itemstack.isItemEnabled(p_98638_.level().enabledFeatures()) && itemstack.getTagElement("CustomCreativeLock") == null;
         } else {
            return itemstack.isEmpty();
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class ItemPickerMenu extends AbstractContainerMenu {
      public final NonNullList<ItemStack> items = NonNullList.create();
      private final AbstractContainerMenu inventoryMenu;

      public ItemPickerMenu(Player p_98641_) {
         super((MenuType<?>)null, 0);
         this.inventoryMenu = p_98641_.inventoryMenu;
         Inventory inventory = p_98641_.getInventory();

         for(int i = 0; i < 5; ++i) {
            for(int j = 0; j < 9; ++j) {
               this.addSlot(new CreativeModeInventoryScreen.CustomCreativeSlot(CreativeModeInventoryScreen.CONTAINER, i * 9 + j, 9 + j * 18, 18 + i * 18));
            }
         }

         for(int k = 0; k < 9; ++k) {
            this.addSlot(new Slot(inventory, k, 9 + k * 18, 112));
         }

         this.scrollTo(0.0F);
      }

      public boolean stillValid(Player p_98645_) {
         return true;
      }

      protected int calculateRowCount() {
         return Mth.positiveCeilDiv(this.items.size(), 9) - 5;
      }

      protected int getRowIndexForScroll(float p_259664_) {
         return Math.max((int)((double)(p_259664_ * (float)this.calculateRowCount()) + 0.5D), 0);
      }

      protected float getScrollForRowIndex(int p_259315_) {
         return Mth.clamp((float)p_259315_ / (float)this.calculateRowCount(), 0.0F, 1.0F);
      }

      protected float subtractInputFromScroll(float p_259841_, double p_260358_) {
         return Mth.clamp(p_259841_ - (float)(p_260358_ / (double)this.calculateRowCount()), 0.0F, 1.0F);
      }

      public void scrollTo(float p_98643_) {
         int i = this.getRowIndexForScroll(p_98643_);

         for(int j = 0; j < 5; ++j) {
            for(int k = 0; k < 9; ++k) {
               int l = k + (j + i) * 9;
               if (l >= 0 && l < this.items.size()) {
                  CreativeModeInventoryScreen.CONTAINER.setItem(k + j * 9, this.items.get(l));
               } else {
                  CreativeModeInventoryScreen.CONTAINER.setItem(k + j * 9, ItemStack.EMPTY);
               }
            }
         }

      }

      public boolean canScroll() {
         return this.items.size() > 45;
      }

      public ItemStack quickMoveStack(Player p_98650_, int p_98651_) {
         if (p_98651_ >= this.slots.size() - 9 && p_98651_ < this.slots.size()) {
            Slot slot = this.slots.get(p_98651_);
            if (slot != null && slot.hasItem()) {
               slot.setByPlayer(ItemStack.EMPTY);
            }
         }

         return ItemStack.EMPTY;
      }

      public boolean canTakeItemForPickAll(ItemStack p_98647_, Slot p_98648_) {
         return p_98648_.container != CreativeModeInventoryScreen.CONTAINER;
      }

      public boolean canDragTo(Slot p_98653_) {
         return p_98653_.container != CreativeModeInventoryScreen.CONTAINER;
      }

      public ItemStack getCarried() {
         return this.inventoryMenu.getCarried();
      }

      public void setCarried(ItemStack p_169751_) {
         this.inventoryMenu.setCarried(p_169751_);
      }
   }

   @OnlyIn(Dist.CLIENT)
   static class SlotWrapper extends Slot {
      final Slot target;

      public SlotWrapper(Slot p_98657_, int p_98658_, int p_98659_, int p_98660_) {
         super(p_98657_.container, p_98658_, p_98659_, p_98660_);
         this.target = p_98657_;
      }

      public void onTake(Player p_169754_, ItemStack p_169755_) {
         this.target.onTake(p_169754_, p_169755_);
      }

      public boolean mayPlace(ItemStack p_98670_) {
         return this.target.mayPlace(p_98670_);
      }

      public ItemStack getItem() {
         return this.target.getItem();
      }

      public boolean hasItem() {
         return this.target.hasItem();
      }

      public void setByPlayer(ItemStack p_271008_) {
         this.target.setByPlayer(p_271008_);
      }

      public void set(ItemStack p_98679_) {
         this.target.set(p_98679_);
      }

      public void setChanged() {
         this.target.setChanged();
      }

      public int getMaxStackSize() {
         return this.target.getMaxStackSize();
      }

      public int getMaxStackSize(ItemStack p_98675_) {
         return this.target.getMaxStackSize(p_98675_);
      }

      @Nullable
      public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
         return this.target.getNoItemIcon();
      }

      public ItemStack remove(int p_98663_) {
         return this.target.remove(p_98663_);
      }

      public boolean isActive() {
         return this.target.isActive();
      }

      public boolean mayPickup(Player p_98665_) {
         return this.target.mayPickup(p_98665_);
      }

      @Override
      public int getSlotIndex() {
         return this.target.getSlotIndex();
      }

      @Override
      public boolean isSameInventory(Slot other) {
         return this.target.isSameInventory(other);
      }

      @Override
      public Slot setBackground(ResourceLocation atlas, ResourceLocation sprite) {
         this.target.setBackground(atlas, sprite);
         return this;
      }
   }
}
