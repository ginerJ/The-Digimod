package net.minecraft.client.gui.screens.recipebook;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.StateSwitchingButton;
import net.minecraft.stats.RecipeBook;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RecipeBookPage {
   public static final int ITEMS_PER_PAGE = 20;
   private final List<RecipeButton> buttons = Lists.newArrayListWithCapacity(20);
   @Nullable
   private RecipeButton hoveredButton;
   private final OverlayRecipeComponent overlay = new OverlayRecipeComponent();
   private Minecraft minecraft;
   private final List<RecipeShownListener> showListeners = Lists.newArrayList();
   private List<RecipeCollection> recipeCollections = ImmutableList.of();
   private StateSwitchingButton forwardButton;
   private StateSwitchingButton backButton;
   private int totalPages;
   private int currentPage;
   private RecipeBook recipeBook;
   @Nullable
   private Recipe<?> lastClickedRecipe;
   @Nullable
   private RecipeCollection lastClickedRecipeCollection;

   public RecipeBookPage() {
      for(int i = 0; i < 20; ++i) {
         this.buttons.add(new RecipeButton());
      }

   }

   public void init(Minecraft p_100429_, int p_100430_, int p_100431_) {
      this.minecraft = p_100429_;
      this.recipeBook = p_100429_.player.getRecipeBook();

      for(int i = 0; i < this.buttons.size(); ++i) {
         this.buttons.get(i).setPosition(p_100430_ + 11 + 25 * (i % 5), p_100431_ + 31 + 25 * (i / 5));
      }

      this.forwardButton = new StateSwitchingButton(p_100430_ + 93, p_100431_ + 137, 12, 17, false);
      this.forwardButton.initTextureValues(1, 208, 13, 18, RecipeBookComponent.RECIPE_BOOK_LOCATION);
      this.backButton = new StateSwitchingButton(p_100430_ + 38, p_100431_ + 137, 12, 17, true);
      this.backButton.initTextureValues(1, 208, 13, 18, RecipeBookComponent.RECIPE_BOOK_LOCATION);
   }

   public void addListener(RecipeBookComponent p_100433_) {
      this.showListeners.remove(p_100433_);
      this.showListeners.add(p_100433_);
   }

   public void updateCollections(List<RecipeCollection> p_100437_, boolean p_100438_) {
      this.recipeCollections = p_100437_;
      this.totalPages = (int)Math.ceil((double)p_100437_.size() / 20.0D);
      if (this.totalPages <= this.currentPage || p_100438_) {
         this.currentPage = 0;
      }

      this.updateButtonsForPage();
   }

   private void updateButtonsForPage() {
      int i = 20 * this.currentPage;

      for(int j = 0; j < this.buttons.size(); ++j) {
         RecipeButton recipebutton = this.buttons.get(j);
         if (i + j < this.recipeCollections.size()) {
            RecipeCollection recipecollection = this.recipeCollections.get(i + j);
            recipebutton.init(recipecollection, this);
            recipebutton.visible = true;
         } else {
            recipebutton.visible = false;
         }
      }

      this.updateArrowButtons();
   }

   private void updateArrowButtons() {
      this.forwardButton.visible = this.totalPages > 1 && this.currentPage < this.totalPages - 1;
      this.backButton.visible = this.totalPages > 1 && this.currentPage > 0;
   }

   public void render(GuiGraphics p_281416_, int p_281888_, int p_281904_, int p_282278_, int p_282424_, float p_281712_) {
      if (this.totalPages > 1) {
         String s = this.currentPage + 1 + "/" + this.totalPages;
         int i = this.minecraft.font.width(s);
         p_281416_.drawString(this.minecraft.font, s, p_281888_ - i / 2 + 73, p_281904_ + 141, -1, false);
      }

      this.hoveredButton = null;

      for(RecipeButton recipebutton : this.buttons) {
         recipebutton.render(p_281416_, p_282278_, p_282424_, p_281712_);
         if (recipebutton.visible && recipebutton.isHoveredOrFocused()) {
            this.hoveredButton = recipebutton;
         }
      }

      this.backButton.render(p_281416_, p_282278_, p_282424_, p_281712_);
      this.forwardButton.render(p_281416_, p_282278_, p_282424_, p_281712_);
      this.overlay.render(p_281416_, p_282278_, p_282424_, p_281712_);
   }

   public void renderTooltip(GuiGraphics p_283690_, int p_282626_, int p_282490_) {
      if (this.minecraft.screen != null && this.hoveredButton != null && !this.overlay.isVisible()) {
         p_283690_.renderComponentTooltip(this.minecraft.font, this.hoveredButton.getTooltipText(), p_282626_, p_282490_);
      }

   }

   @Nullable
   public Recipe<?> getLastClickedRecipe() {
      return this.lastClickedRecipe;
   }

   @Nullable
   public RecipeCollection getLastClickedRecipeCollection() {
      return this.lastClickedRecipeCollection;
   }

   public void setInvisible() {
      this.overlay.setVisible(false);
   }

   public boolean mouseClicked(double p_100410_, double p_100411_, int p_100412_, int p_100413_, int p_100414_, int p_100415_, int p_100416_) {
      this.lastClickedRecipe = null;
      this.lastClickedRecipeCollection = null;
      if (this.overlay.isVisible()) {
         if (this.overlay.mouseClicked(p_100410_, p_100411_, p_100412_)) {
            this.lastClickedRecipe = this.overlay.getLastRecipeClicked();
            this.lastClickedRecipeCollection = this.overlay.getRecipeCollection();
         } else {
            this.overlay.setVisible(false);
         }

         return true;
      } else if (this.forwardButton.mouseClicked(p_100410_, p_100411_, p_100412_)) {
         ++this.currentPage;
         this.updateButtonsForPage();
         return true;
      } else if (this.backButton.mouseClicked(p_100410_, p_100411_, p_100412_)) {
         --this.currentPage;
         this.updateButtonsForPage();
         return true;
      } else {
         for(RecipeButton recipebutton : this.buttons) {
            if (recipebutton.mouseClicked(p_100410_, p_100411_, p_100412_)) {
               if (p_100412_ == 0) {
                  this.lastClickedRecipe = recipebutton.getRecipe();
                  this.lastClickedRecipeCollection = recipebutton.getCollection();
               } else if (p_100412_ == 1 && !this.overlay.isVisible() && !recipebutton.isOnlyOption()) {
                  this.overlay.init(this.minecraft, recipebutton.getCollection(), recipebutton.getX(), recipebutton.getY(), p_100413_ + p_100415_ / 2, p_100414_ + 13 + p_100416_ / 2, (float)recipebutton.getWidth());
               }

               return true;
            }
         }

         return false;
      }
   }

   public void recipesShown(List<Recipe<?>> p_100435_) {
      for(RecipeShownListener recipeshownlistener : this.showListeners) {
         recipeshownlistener.recipesShown(p_100435_);
      }

   }

   public Minecraft getMinecraft() {
      return this.minecraft;
   }

   public RecipeBook getRecipeBook() {
      return this.recipeBook;
   }

   protected void listButtons(Consumer<AbstractWidget> p_170054_) {
      p_170054_.accept(this.forwardButton);
      p_170054_.accept(this.backButton);
      this.buttons.forEach(p_170054_);
   }
}