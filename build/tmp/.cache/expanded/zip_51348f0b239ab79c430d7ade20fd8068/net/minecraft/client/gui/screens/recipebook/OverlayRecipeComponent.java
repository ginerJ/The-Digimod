package net.minecraft.client.gui.screens.recipebook;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.recipebook.PlaceRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.AbstractFurnaceMenu;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class OverlayRecipeComponent implements Renderable, GuiEventListener {
   static final ResourceLocation RECIPE_BOOK_LOCATION = new ResourceLocation("textures/gui/recipe_book.png");
   private static final int MAX_ROW = 4;
   private static final int MAX_ROW_LARGE = 5;
   private static final float ITEM_RENDER_SCALE = 0.375F;
   public static final int BUTTON_SIZE = 25;
   private final List<OverlayRecipeComponent.OverlayRecipeButton> recipeButtons = Lists.newArrayList();
   private boolean isVisible;
   private int x;
   private int y;
   private Minecraft minecraft;
   private RecipeCollection collection;
   @Nullable
   private Recipe<?> lastRecipeClicked;
   float time;
   boolean isFurnaceMenu;

   public void init(Minecraft p_100195_, RecipeCollection p_100196_, int p_100197_, int p_100198_, int p_100199_, int p_100200_, float p_100201_) {
      this.minecraft = p_100195_;
      this.collection = p_100196_;
      if (p_100195_.player.containerMenu instanceof AbstractFurnaceMenu) {
         this.isFurnaceMenu = true;
      }

      boolean flag = p_100195_.player.getRecipeBook().isFiltering((RecipeBookMenu)p_100195_.player.containerMenu);
      List<Recipe<?>> list = p_100196_.getDisplayRecipes(true);
      List<Recipe<?>> list1 = flag ? Collections.emptyList() : p_100196_.getDisplayRecipes(false);
      int i = list.size();
      int j = i + list1.size();
      int k = j <= 16 ? 4 : 5;
      int l = (int)Math.ceil((double)((float)j / (float)k));
      this.x = p_100197_;
      this.y = p_100198_;
      float f = (float)(this.x + Math.min(j, k) * 25);
      float f1 = (float)(p_100199_ + 50);
      if (f > f1) {
         this.x = (int)((float)this.x - p_100201_ * (float)((int)((f - f1) / p_100201_)));
      }

      float f2 = (float)(this.y + l * 25);
      float f3 = (float)(p_100200_ + 50);
      if (f2 > f3) {
         this.y = (int)((float)this.y - p_100201_ * (float)Mth.ceil((f2 - f3) / p_100201_));
      }

      float f4 = (float)this.y;
      float f5 = (float)(p_100200_ - 100);
      if (f4 < f5) {
         this.y = (int)((float)this.y - p_100201_ * (float)Mth.ceil((f4 - f5) / p_100201_));
      }

      this.isVisible = true;
      this.recipeButtons.clear();

      for(int i1 = 0; i1 < j; ++i1) {
         boolean flag1 = i1 < i;
         Recipe<?> recipe = flag1 ? list.get(i1) : list1.get(i1 - i);
         int j1 = this.x + 4 + 25 * (i1 % k);
         int k1 = this.y + 5 + 25 * (i1 / k);
         if (this.isFurnaceMenu) {
            this.recipeButtons.add(new OverlayRecipeComponent.OverlaySmeltingRecipeButton(j1, k1, recipe, flag1));
         } else {
            this.recipeButtons.add(new OverlayRecipeComponent.OverlayRecipeButton(j1, k1, recipe, flag1));
         }
      }

      this.lastRecipeClicked = null;
   }

   public RecipeCollection getRecipeCollection() {
      return this.collection;
   }

   @Nullable
   public Recipe<?> getLastRecipeClicked() {
      return this.lastRecipeClicked;
   }

   public boolean mouseClicked(double p_100186_, double p_100187_, int p_100188_) {
      if (p_100188_ != 0) {
         return false;
      } else {
         for(OverlayRecipeComponent.OverlayRecipeButton overlayrecipecomponent$overlayrecipebutton : this.recipeButtons) {
            if (overlayrecipecomponent$overlayrecipebutton.mouseClicked(p_100186_, p_100187_, p_100188_)) {
               this.lastRecipeClicked = overlayrecipecomponent$overlayrecipebutton.recipe;
               return true;
            }
         }

         return false;
      }
   }

   public boolean isMouseOver(double p_100208_, double p_100209_) {
      return false;
   }

   public void render(GuiGraphics p_281618_, int p_282646_, int p_283687_, float p_283147_) {
      if (this.isVisible) {
         this.time += p_283147_;
         RenderSystem.enableBlend();
         p_281618_.pose().pushPose();
         p_281618_.pose().translate(0.0F, 0.0F, 1000.0F);
         int i = this.recipeButtons.size() <= 16 ? 4 : 5;
         int j = Math.min(this.recipeButtons.size(), i);
         int k = Mth.ceil((float)this.recipeButtons.size() / (float)i);
         int l = 4;
         p_281618_.blitNineSliced(RECIPE_BOOK_LOCATION, this.x, this.y, j * 25 + 8, k * 25 + 8, 4, 32, 32, 82, 208);
         RenderSystem.disableBlend();

         for(OverlayRecipeComponent.OverlayRecipeButton overlayrecipecomponent$overlayrecipebutton : this.recipeButtons) {
            overlayrecipecomponent$overlayrecipebutton.render(p_281618_, p_282646_, p_283687_, p_283147_);
         }

         p_281618_.pose().popPose();
      }
   }

   public void setVisible(boolean p_100205_) {
      this.isVisible = p_100205_;
   }

   public boolean isVisible() {
      return this.isVisible;
   }

   public void setFocused(boolean p_265597_) {
   }

   public boolean isFocused() {
      return false;
   }

   @OnlyIn(Dist.CLIENT)
   class OverlayRecipeButton extends AbstractWidget implements PlaceRecipe<Ingredient> {
      final Recipe<?> recipe;
      private final boolean isCraftable;
      protected final List<OverlayRecipeComponent.OverlayRecipeButton.Pos> ingredientPos = Lists.newArrayList();

      public OverlayRecipeButton(int p_100232_, int p_100233_, Recipe<?> p_100234_, boolean p_100235_) {
         super(p_100232_, p_100233_, 200, 20, CommonComponents.EMPTY);
         this.width = 24;
         this.height = 24;
         this.recipe = p_100234_;
         this.isCraftable = p_100235_;
         this.calculateIngredientsPositions(p_100234_);
      }

      protected void calculateIngredientsPositions(Recipe<?> p_100236_) {
         this.placeRecipe(3, 3, -1, p_100236_, p_100236_.getIngredients().iterator(), 0);
      }

      public void updateWidgetNarration(NarrationElementOutput p_259646_) {
         this.defaultButtonNarrationText(p_259646_);
      }

      public void addItemToSlot(Iterator<Ingredient> p_100240_, int p_100241_, int p_100242_, int p_100243_, int p_100244_) {
         ItemStack[] aitemstack = p_100240_.next().getItems();
         if (aitemstack.length != 0) {
            this.ingredientPos.add(new OverlayRecipeComponent.OverlayRecipeButton.Pos(3 + p_100244_ * 7, 3 + p_100243_ * 7, aitemstack));
         }

      }

      public void renderWidget(GuiGraphics p_283557_, int p_283483_, int p_282919_, float p_282165_) {
         int i = 152;
         if (!this.isCraftable) {
            i += 26;
         }

         int j = OverlayRecipeComponent.this.isFurnaceMenu ? 130 : 78;
         if (this.isHoveredOrFocused()) {
            j += 26;
         }

         p_283557_.blit(OverlayRecipeComponent.RECIPE_BOOK_LOCATION, this.getX(), this.getY(), i, j, this.width, this.height);
         p_283557_.pose().pushPose();
         p_283557_.pose().translate((double)(this.getX() + 2), (double)(this.getY() + 2), 150.0D);

         for(OverlayRecipeComponent.OverlayRecipeButton.Pos overlayrecipecomponent$overlayrecipebutton$pos : this.ingredientPos) {
            p_283557_.pose().pushPose();
            p_283557_.pose().translate((double)overlayrecipecomponent$overlayrecipebutton$pos.x, (double)overlayrecipecomponent$overlayrecipebutton$pos.y, 0.0D);
            p_283557_.pose().scale(0.375F, 0.375F, 1.0F);
            p_283557_.pose().translate(-8.0D, -8.0D, 0.0D);
            if (overlayrecipecomponent$overlayrecipebutton$pos.ingredients.length > 0) {
               p_283557_.renderItem(overlayrecipecomponent$overlayrecipebutton$pos.ingredients[Mth.floor(OverlayRecipeComponent.this.time / 30.0F) % overlayrecipecomponent$overlayrecipebutton$pos.ingredients.length], 0, 0);
            }

            p_283557_.pose().popPose();
         }

         p_283557_.pose().popPose();
      }

      @OnlyIn(Dist.CLIENT)
      protected class Pos {
         public final ItemStack[] ingredients;
         public final int x;
         public final int y;

         public Pos(int p_100256_, int p_100257_, ItemStack[] p_100258_) {
            this.x = p_100256_;
            this.y = p_100257_;
            this.ingredients = p_100258_;
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   class OverlaySmeltingRecipeButton extends OverlayRecipeComponent.OverlayRecipeButton {
      public OverlaySmeltingRecipeButton(int p_100262_, int p_100263_, Recipe<?> p_100264_, boolean p_100265_) {
         super(p_100262_, p_100263_, p_100264_, p_100265_);
      }

      protected void calculateIngredientsPositions(Recipe<?> p_100267_) {
         ItemStack[] aitemstack = p_100267_.getIngredients().get(0).getItems();
         this.ingredientPos.add(new OverlayRecipeComponent.OverlayRecipeButton.Pos(10, 10, aitemstack));
      }
   }
}