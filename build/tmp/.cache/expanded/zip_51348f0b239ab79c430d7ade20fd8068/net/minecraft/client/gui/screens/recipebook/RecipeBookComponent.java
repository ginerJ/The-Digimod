package net.minecraft.client.gui.screens.recipebook;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.ClientRecipeBook;
import net.minecraft.client.Minecraft;
import net.minecraft.client.RecipeBookCategories;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.StateSwitchingButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.LanguageInfo;
import net.minecraft.client.resources.language.LanguageManager;
import net.minecraft.client.searchtree.SearchRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundRecipeBookChangeSettingsPacket;
import net.minecraft.recipebook.PlaceRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.inventory.RecipeBookType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RecipeBookComponent implements PlaceRecipe<Ingredient>, Renderable, GuiEventListener, NarratableEntry, RecipeShownListener {
   protected static final ResourceLocation RECIPE_BOOK_LOCATION = new ResourceLocation("textures/gui/recipe_book.png");
   private static final Component SEARCH_HINT = Component.translatable("gui.recipebook.search_hint").withStyle(ChatFormatting.ITALIC).withStyle(ChatFormatting.GRAY);
   public static final int IMAGE_WIDTH = 147;
   public static final int IMAGE_HEIGHT = 166;
   private static final int OFFSET_X_POSITION = 86;
   private static final Component ONLY_CRAFTABLES_TOOLTIP = Component.translatable("gui.recipebook.toggleRecipes.craftable");
   private static final Component ALL_RECIPES_TOOLTIP = Component.translatable("gui.recipebook.toggleRecipes.all");
   private int xOffset;
   private int width;
   private int height;
   protected final GhostRecipe ghostRecipe = new GhostRecipe();
   private final List<RecipeBookTabButton> tabButtons = Lists.newArrayList();
   @Nullable
   private RecipeBookTabButton selectedTab;
   protected StateSwitchingButton filterButton;
   protected RecipeBookMenu<?> menu;
   protected Minecraft minecraft;
   @Nullable
   private EditBox searchBox;
   private String lastSearch = "";
   private ClientRecipeBook book;
   private final RecipeBookPage recipeBookPage = new RecipeBookPage();
   private final StackedContents stackedContents = new StackedContents();
   private int timesInventoryChanged;
   private boolean ignoreTextInput;
   private boolean visible;
   private boolean widthTooNarrow;

   public void init(int p_100310_, int p_100311_, Minecraft p_100312_, boolean p_100313_, RecipeBookMenu<?> p_100314_) {
      this.minecraft = p_100312_;
      this.width = p_100310_;
      this.height = p_100311_;
      this.menu = p_100314_;
      this.widthTooNarrow = p_100313_;
      p_100312_.player.containerMenu = p_100314_;
      this.book = p_100312_.player.getRecipeBook();
      this.timesInventoryChanged = p_100312_.player.getInventory().getTimesChanged();
      this.visible = this.isVisibleAccordingToBookData();
      if (this.visible) {
         this.initVisuals();
      }

   }

   public void initVisuals() {
      this.xOffset = this.widthTooNarrow ? 0 : 86;
      int i = (this.width - 147) / 2 - this.xOffset;
      int j = (this.height - 166) / 2;
      this.stackedContents.clear();
      this.minecraft.player.getInventory().fillStackedContents(this.stackedContents);
      this.menu.fillCraftSlotsStackedContents(this.stackedContents);
      String s = this.searchBox != null ? this.searchBox.getValue() : "";
      this.searchBox = new EditBox(this.minecraft.font, i + 26, j + 14, 79, 9 + 3, Component.translatable("itemGroup.search"));
      this.searchBox.setMaxLength(50);
      this.searchBox.setVisible(true);
      this.searchBox.setTextColor(16777215);
      this.searchBox.setValue(s);
      this.searchBox.setHint(SEARCH_HINT);
      this.recipeBookPage.init(this.minecraft, i, j);
      this.recipeBookPage.addListener(this);
      this.filterButton = new StateSwitchingButton(i + 110, j + 12, 26, 16, this.book.isFiltering(this.menu));
      this.updateFilterButtonTooltip();
      this.initFilterButtonTextures();
      this.tabButtons.clear();

      for(RecipeBookCategories recipebookcategories : this.menu.getRecipeBookCategories()) {
         this.tabButtons.add(new RecipeBookTabButton(recipebookcategories));
      }

      if (this.selectedTab != null) {
         this.selectedTab = this.tabButtons.stream().filter((p_100329_) -> {
            return p_100329_.getCategory().equals(this.selectedTab.getCategory());
         }).findFirst().orElse((RecipeBookTabButton)null);
      }

      if (this.selectedTab == null) {
         this.selectedTab = this.tabButtons.get(0);
      }

      this.selectedTab.setStateTriggered(true);
      this.updateCollections(false);
      this.updateTabs();
   }

   private void updateFilterButtonTooltip() {
      this.filterButton.setTooltip(this.filterButton.isStateTriggered() ? Tooltip.create(this.getRecipeFilterName()) : Tooltip.create(ALL_RECIPES_TOOLTIP));
   }

   protected void initFilterButtonTextures() {
      this.filterButton.initTextureValues(152, 41, 28, 18, RECIPE_BOOK_LOCATION);
   }

   public int updateScreenPosition(int p_181402_, int p_181403_) {
      int i;
      if (this.isVisible() && !this.widthTooNarrow) {
         i = 177 + (p_181402_ - p_181403_ - 200) / 2;
      } else {
         i = (p_181402_ - p_181403_) / 2;
      }

      return i;
   }

   public void toggleVisibility() {
      this.setVisible(!this.isVisible());
   }

   public boolean isVisible() {
      return this.visible;
   }

   private boolean isVisibleAccordingToBookData() {
      return this.book.isOpen(this.menu.getRecipeBookType());
   }

   protected void setVisible(boolean p_100370_) {
      if (p_100370_) {
         this.initVisuals();
      }

      this.visible = p_100370_;
      this.book.setOpen(this.menu.getRecipeBookType(), p_100370_);
      if (!p_100370_) {
         this.recipeBookPage.setInvisible();
      }

      this.sendUpdateSettings();
   }

   public void slotClicked(@Nullable Slot p_100315_) {
      if (p_100315_ != null && p_100315_.index < this.menu.getSize()) {
         this.ghostRecipe.clear();
         if (this.isVisible()) {
            this.updateStackedContents();
         }
      }

   }

   private void updateCollections(boolean p_100383_) {
      List<RecipeCollection> list = this.book.getCollection(this.selectedTab.getCategory());
      list.forEach((p_100381_) -> {
         p_100381_.canCraft(this.stackedContents, this.menu.getGridWidth(), this.menu.getGridHeight(), this.book);
      });
      List<RecipeCollection> list1 = Lists.newArrayList(list);
      list1.removeIf((p_100368_) -> {
         return !p_100368_.hasKnownRecipes();
      });
      list1.removeIf((p_100360_) -> {
         return !p_100360_.hasFitting();
      });
      String s = this.searchBox.getValue();
      if (!s.isEmpty()) {
         ObjectSet<RecipeCollection> objectset = new ObjectLinkedOpenHashSet<>(this.minecraft.getSearchTree(SearchRegistry.RECIPE_COLLECTIONS).search(s.toLowerCase(Locale.ROOT)));
         list1.removeIf((p_100334_) -> {
            return !objectset.contains(p_100334_);
         });
      }

      if (this.book.isFiltering(this.menu)) {
         list1.removeIf((p_100331_) -> {
            return !p_100331_.hasCraftable();
         });
      }

      this.recipeBookPage.updateCollections(list1, p_100383_);
   }

   private void updateTabs() {
      int i = (this.width - 147) / 2 - this.xOffset - 30;
      int j = (this.height - 166) / 2 + 3;
      int k = 27;
      int l = 0;

      for(RecipeBookTabButton recipebooktabbutton : this.tabButtons) {
         RecipeBookCategories recipebookcategories = recipebooktabbutton.getCategory();
         if (recipebookcategories != RecipeBookCategories.CRAFTING_SEARCH && recipebookcategories != RecipeBookCategories.FURNACE_SEARCH) {
            if (recipebooktabbutton.updateVisibility(this.book)) {
               recipebooktabbutton.setPosition(i, j + 27 * l++);
               recipebooktabbutton.startAnimation(this.minecraft);
            }
         } else {
            recipebooktabbutton.visible = true;
            recipebooktabbutton.setPosition(i, j + 27 * l++);
         }
      }

   }

   public void tick() {
      boolean flag = this.isVisibleAccordingToBookData();
      if (this.isVisible() != flag) {
         this.setVisible(flag);
      }

      if (this.isVisible()) {
         if (this.timesInventoryChanged != this.minecraft.player.getInventory().getTimesChanged()) {
            this.updateStackedContents();
            this.timesInventoryChanged = this.minecraft.player.getInventory().getTimesChanged();
         }

         this.searchBox.tick();
      }
   }

   private void updateStackedContents() {
      this.stackedContents.clear();
      this.minecraft.player.getInventory().fillStackedContents(this.stackedContents);
      this.menu.fillCraftSlotsStackedContents(this.stackedContents);
      this.updateCollections(false);
   }

   public void render(GuiGraphics p_283597_, int p_282668_, int p_283506_, float p_282813_) {
      if (this.isVisible()) {
         p_283597_.pose().pushPose();
         p_283597_.pose().translate(0.0F, 0.0F, 100.0F);
         int i = (this.width - 147) / 2 - this.xOffset;
         int j = (this.height - 166) / 2;
         p_283597_.blit(RECIPE_BOOK_LOCATION, i, j, 1, 1, 147, 166);
         this.searchBox.render(p_283597_, p_282668_, p_283506_, p_282813_);

         for(RecipeBookTabButton recipebooktabbutton : this.tabButtons) {
            recipebooktabbutton.render(p_283597_, p_282668_, p_283506_, p_282813_);
         }

         this.filterButton.render(p_283597_, p_282668_, p_283506_, p_282813_);
         this.recipeBookPage.render(p_283597_, i, j, p_282668_, p_283506_, p_282813_);
         p_283597_.pose().popPose();
      }
   }

   public void renderTooltip(GuiGraphics p_281740_, int p_281520_, int p_282050_, int p_282836_, int p_282758_) {
      if (this.isVisible()) {
         this.recipeBookPage.renderTooltip(p_281740_, p_282836_, p_282758_);
         this.renderGhostRecipeTooltip(p_281740_, p_281520_, p_282050_, p_282836_, p_282758_);
      }
   }

   protected Component getRecipeFilterName() {
      return ONLY_CRAFTABLES_TOOLTIP;
   }

   private void renderGhostRecipeTooltip(GuiGraphics p_282776_, int p_282886_, int p_281571_, int p_282948_, int p_283050_) {
      ItemStack itemstack = null;

      for(int i = 0; i < this.ghostRecipe.size(); ++i) {
         GhostRecipe.GhostIngredient ghostrecipe$ghostingredient = this.ghostRecipe.get(i);
         int j = ghostrecipe$ghostingredient.getX() + p_282886_;
         int k = ghostrecipe$ghostingredient.getY() + p_281571_;
         if (p_282948_ >= j && p_283050_ >= k && p_282948_ < j + 16 && p_283050_ < k + 16) {
            itemstack = ghostrecipe$ghostingredient.getItem();
         }
      }

      if (itemstack != null && this.minecraft.screen != null) {
         p_282776_.renderComponentTooltip(this.minecraft.font, Screen.getTooltipFromItem(this.minecraft, itemstack), p_282948_, p_283050_, itemstack);
      }

   }

   public void renderGhostRecipe(GuiGraphics p_283634_, int p_283327_, int p_282027_, boolean p_283495_, float p_283514_) {
      this.ghostRecipe.render(p_283634_, this.minecraft, p_283327_, p_282027_, p_283495_, p_283514_);
   }

   public boolean mouseClicked(double p_100294_, double p_100295_, int p_100296_) {
      if (this.isVisible() && !this.minecraft.player.isSpectator()) {
         if (this.recipeBookPage.mouseClicked(p_100294_, p_100295_, p_100296_, (this.width - 147) / 2 - this.xOffset, (this.height - 166) / 2, 147, 166)) {
            Recipe<?> recipe = this.recipeBookPage.getLastClickedRecipe();
            RecipeCollection recipecollection = this.recipeBookPage.getLastClickedRecipeCollection();
            if (recipe != null && recipecollection != null) {
               if (!recipecollection.isCraftable(recipe) && this.ghostRecipe.getRecipe() == recipe) {
                  return false;
               }

               this.ghostRecipe.clear();
               this.minecraft.gameMode.handlePlaceRecipe(this.minecraft.player.containerMenu.containerId, recipe, Screen.hasShiftDown());
               if (!this.isOffsetNextToMainGUI()) {
                  this.setVisible(false);
               }
            }

            return true;
         } else if (this.searchBox.mouseClicked(p_100294_, p_100295_, p_100296_)) {
            this.searchBox.setFocused(true);
            return true;
         } else {
            this.searchBox.setFocused(false);
            if (this.filterButton.mouseClicked(p_100294_, p_100295_, p_100296_)) {
               boolean flag = this.toggleFiltering();
               this.filterButton.setStateTriggered(flag);
               this.updateFilterButtonTooltip();
               this.sendUpdateSettings();
               this.updateCollections(false);
               return true;
            } else {
               for(RecipeBookTabButton recipebooktabbutton : this.tabButtons) {
                  if (recipebooktabbutton.mouseClicked(p_100294_, p_100295_, p_100296_)) {
                     if (this.selectedTab != recipebooktabbutton) {
                        if (this.selectedTab != null) {
                           this.selectedTab.setStateTriggered(false);
                        }

                        this.selectedTab = recipebooktabbutton;
                        this.selectedTab.setStateTriggered(true);
                        this.updateCollections(true);
                     }

                     return true;
                  }
               }

               return false;
            }
         }
      } else {
         return false;
      }
   }

   private boolean toggleFiltering() {
      RecipeBookType recipebooktype = this.menu.getRecipeBookType();
      boolean flag = !this.book.isFiltering(recipebooktype);
      this.book.setFiltering(recipebooktype, flag);
      return flag;
   }

   public boolean hasClickedOutside(double p_100298_, double p_100299_, int p_100300_, int p_100301_, int p_100302_, int p_100303_, int p_100304_) {
      if (!this.isVisible()) {
         return true;
      } else {
         boolean flag = p_100298_ < (double)p_100300_ || p_100299_ < (double)p_100301_ || p_100298_ >= (double)(p_100300_ + p_100302_) || p_100299_ >= (double)(p_100301_ + p_100303_);
         boolean flag1 = (double)(p_100300_ - 147) < p_100298_ && p_100298_ < (double)p_100300_ && (double)p_100301_ < p_100299_ && p_100299_ < (double)(p_100301_ + p_100303_);
         return flag && !flag1 && !this.selectedTab.isHoveredOrFocused();
      }
   }

   public boolean keyPressed(int p_100306_, int p_100307_, int p_100308_) {
      this.ignoreTextInput = false;
      if (this.isVisible() && !this.minecraft.player.isSpectator()) {
         if (p_100306_ == 256 && !this.isOffsetNextToMainGUI()) {
            this.setVisible(false);
            return true;
         } else if (this.searchBox.keyPressed(p_100306_, p_100307_, p_100308_)) {
            this.checkSearchStringUpdate();
            return true;
         } else if (this.searchBox.isFocused() && this.searchBox.isVisible() && p_100306_ != 256) {
            return true;
         } else if (this.minecraft.options.keyChat.matches(p_100306_, p_100307_) && !this.searchBox.isFocused()) {
            this.ignoreTextInput = true;
            this.searchBox.setFocused(true);
            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public boolean keyReleased(int p_100356_, int p_100357_, int p_100358_) {
      this.ignoreTextInput = false;
      return GuiEventListener.super.keyReleased(p_100356_, p_100357_, p_100358_);
   }

   public boolean charTyped(char p_100291_, int p_100292_) {
      if (this.ignoreTextInput) {
         return false;
      } else if (this.isVisible() && !this.minecraft.player.isSpectator()) {
         if (this.searchBox.charTyped(p_100291_, p_100292_)) {
            this.checkSearchStringUpdate();
            return true;
         } else {
            return GuiEventListener.super.charTyped(p_100291_, p_100292_);
         }
      } else {
         return false;
      }
   }

   public boolean isMouseOver(double p_100353_, double p_100354_) {
      return false;
   }

   public void setFocused(boolean p_265089_) {
   }

   public boolean isFocused() {
      return false;
   }

   private void checkSearchStringUpdate() {
      String s = this.searchBox.getValue().toLowerCase(Locale.ROOT);
      this.pirateSpeechForThePeople(s);
      if (!s.equals(this.lastSearch)) {
         this.updateCollections(false);
         this.lastSearch = s;
      }

   }

   private void pirateSpeechForThePeople(String p_100336_) {
      if ("excitedze".equals(p_100336_)) {
         LanguageManager languagemanager = this.minecraft.getLanguageManager();
         String s = "en_pt";
         LanguageInfo languageinfo = languagemanager.getLanguage("en_pt");
         if (languageinfo == null || languagemanager.getSelected().equals("en_pt")) {
            return;
         }

         languagemanager.setSelected("en_pt");
         this.minecraft.options.languageCode = "en_pt";
         this.minecraft.reloadResourcePacks();
         this.minecraft.options.save();
      }

   }

   private boolean isOffsetNextToMainGUI() {
      return this.xOffset == 86;
   }

   public void recipesUpdated() {
      this.updateTabs();
      if (this.isVisible()) {
         this.updateCollections(false);
      }

   }

   public void recipesShown(List<Recipe<?>> p_100344_) {
      for(Recipe<?> recipe : p_100344_) {
         this.minecraft.player.removeRecipeHighlight(recipe);
      }

   }

   public void setupGhostRecipe(Recipe<?> p_100316_, List<Slot> p_100317_) {
      ItemStack itemstack = p_100316_.getResultItem(this.minecraft.level.registryAccess());
      this.ghostRecipe.setRecipe(p_100316_);
      this.ghostRecipe.addIngredient(Ingredient.of(itemstack), (p_100317_.get(0)).x, (p_100317_.get(0)).y);
      this.placeRecipe(this.menu.getGridWidth(), this.menu.getGridHeight(), this.menu.getResultSlotIndex(), p_100316_, p_100316_.getIngredients().iterator(), 0);
   }

   public void addItemToSlot(Iterator<Ingredient> p_100338_, int p_100339_, int p_100340_, int p_100341_, int p_100342_) {
      Ingredient ingredient = p_100338_.next();
      if (!ingredient.isEmpty()) {
         Slot slot = this.menu.slots.get(p_100339_);
         this.ghostRecipe.addIngredient(ingredient, slot.x, slot.y);
      }

   }

   protected void sendUpdateSettings() {
      if (this.minecraft.getConnection() != null) {
         RecipeBookType recipebooktype = this.menu.getRecipeBookType();
         boolean flag = this.book.getBookSettings().isOpen(recipebooktype);
         boolean flag1 = this.book.getBookSettings().isFiltering(recipebooktype);
         this.minecraft.getConnection().send(new ServerboundRecipeBookChangeSettingsPacket(recipebooktype, flag, flag1));
      }

   }

   public NarratableEntry.NarrationPriority narrationPriority() {
      return this.visible ? NarratableEntry.NarrationPriority.HOVERED : NarratableEntry.NarrationPriority.NONE;
   }

   public void updateNarration(NarrationElementOutput p_170046_) {
      List<NarratableEntry> list = Lists.newArrayList();
      this.recipeBookPage.listButtons((p_170049_) -> {
         if (p_170049_.isActive()) {
            list.add(p_170049_);
         }

      });
      list.add(this.searchBox);
      list.add(this.filterButton);
      list.addAll(this.tabButtons);
      Screen.NarratableSearchResult screen$narratablesearchresult = Screen.findNarratableWidget(list, (NarratableEntry)null);
      if (screen$narratablesearchresult != null) {
         screen$narratablesearchresult.entry.updateNarration(p_170046_.nest());
      }

   }
}
