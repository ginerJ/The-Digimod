package net.minecraft.client.gui.screens.recipebook;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import net.minecraft.client.ClientRecipeBook;
import net.minecraft.client.Minecraft;
import net.minecraft.client.RecipeBookCategories;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.StateSwitchingButton;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RecipeBookTabButton extends StateSwitchingButton {
   private final RecipeBookCategories category;
   private static final float ANIMATION_TIME = 15.0F;
   private float animationTime;

   public RecipeBookTabButton(RecipeBookCategories p_100448_) {
      super(0, 0, 35, 27, false);
      this.category = p_100448_;
      this.initTextureValues(153, 2, 35, 0, RecipeBookComponent.RECIPE_BOOK_LOCATION);
   }

   public void startAnimation(Minecraft p_100452_) {
      ClientRecipeBook clientrecipebook = p_100452_.player.getRecipeBook();
      List<RecipeCollection> list = clientrecipebook.getCollection(this.category);
      if (p_100452_.player.containerMenu instanceof RecipeBookMenu) {
         for(RecipeCollection recipecollection : list) {
            for(Recipe<?> recipe : recipecollection.getRecipes(clientrecipebook.isFiltering((RecipeBookMenu)p_100452_.player.containerMenu))) {
               if (clientrecipebook.willHighlight(recipe)) {
                  this.animationTime = 15.0F;
                  return;
               }
            }
         }

      }
   }

   public void renderWidget(GuiGraphics p_283195_, int p_283508_, int p_281788_, float p_283269_) {
      if (this.animationTime > 0.0F) {
         float f = 1.0F + 0.1F * (float)Math.sin((double)(this.animationTime / 15.0F * (float)Math.PI));
         p_283195_.pose().pushPose();
         p_283195_.pose().translate((float)(this.getX() + 8), (float)(this.getY() + 12), 0.0F);
         p_283195_.pose().scale(1.0F, f, 1.0F);
         p_283195_.pose().translate((float)(-(this.getX() + 8)), (float)(-(this.getY() + 12)), 0.0F);
      }

      Minecraft minecraft = Minecraft.getInstance();
      RenderSystem.disableDepthTest();
      int i = this.xTexStart;
      int j = this.yTexStart;
      if (this.isStateTriggered) {
         i += this.xDiffTex;
      }

      if (this.isHoveredOrFocused()) {
         j += this.yDiffTex;
      }

      int k = this.getX();
      if (this.isStateTriggered) {
         k -= 2;
      }

      p_283195_.blit(this.resourceLocation, k, this.getY(), i, j, this.width, this.height);
      RenderSystem.enableDepthTest();
      this.renderIcon(p_283195_, minecraft.getItemRenderer());
      if (this.animationTime > 0.0F) {
         p_283195_.pose().popPose();
         this.animationTime -= p_283269_;
      }

   }

   private void renderIcon(GuiGraphics p_281802_, ItemRenderer p_282499_) {
      List<ItemStack> list = this.category.getIconItems();
      int i = this.isStateTriggered ? -2 : 0;
      if (list.size() == 1) {
         p_281802_.renderFakeItem(list.get(0), this.getX() + 9 + i, this.getY() + 5);
      } else if (list.size() == 2) {
         p_281802_.renderFakeItem(list.get(0), this.getX() + 3 + i, this.getY() + 5);
         p_281802_.renderFakeItem(list.get(1), this.getX() + 14 + i, this.getY() + 5);
      }

   }

   public RecipeBookCategories getCategory() {
      return this.category;
   }

   public boolean updateVisibility(ClientRecipeBook p_100450_) {
      List<RecipeCollection> list = p_100450_.getCollection(this.category);
      this.visible = false;
      if (list != null) {
         for(RecipeCollection recipecollection : list) {
            if (recipecollection.hasKnownRecipes() && recipecollection.hasFitting()) {
               this.visible = true;
               break;
            }
         }
      }

      return this.visible;
   }
}