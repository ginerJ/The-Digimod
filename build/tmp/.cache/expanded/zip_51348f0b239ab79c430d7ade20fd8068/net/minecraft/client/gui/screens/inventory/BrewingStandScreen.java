package net.minecraft.client.gui.screens.inventory;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.BrewingStandMenu;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BrewingStandScreen extends AbstractContainerScreen<BrewingStandMenu> {
   private static final ResourceLocation BREWING_STAND_LOCATION = new ResourceLocation("textures/gui/container/brewing_stand.png");
   private static final int[] BUBBLELENGTHS = new int[]{29, 24, 20, 16, 11, 6, 0};

   public BrewingStandScreen(BrewingStandMenu p_98332_, Inventory p_98333_, Component p_98334_) {
      super(p_98332_, p_98333_, p_98334_);
   }

   protected void init() {
      super.init();
      this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
   }

   public void render(GuiGraphics p_283297_, int p_283600_, int p_282033_, float p_283410_) {
      this.renderBackground(p_283297_);
      super.render(p_283297_, p_283600_, p_282033_, p_283410_);
      this.renderTooltip(p_283297_, p_283600_, p_282033_);
   }

   protected void renderBg(GuiGraphics p_282963_, float p_282080_, int p_283365_, int p_283150_) {
      int i = (this.width - this.imageWidth) / 2;
      int j = (this.height - this.imageHeight) / 2;
      p_282963_.blit(BREWING_STAND_LOCATION, i, j, 0, 0, this.imageWidth, this.imageHeight);
      int k = this.menu.getFuel();
      int l = Mth.clamp((18 * k + 20 - 1) / 20, 0, 18);
      if (l > 0) {
         p_282963_.blit(BREWING_STAND_LOCATION, i + 60, j + 44, 176, 29, l, 4);
      }

      int i1 = this.menu.getBrewingTicks();
      if (i1 > 0) {
         int j1 = (int)(28.0F * (1.0F - (float)i1 / 400.0F));
         if (j1 > 0) {
            p_282963_.blit(BREWING_STAND_LOCATION, i + 97, j + 16, 176, 0, 9, j1);
         }

         j1 = BUBBLELENGTHS[i1 / 2 % 7];
         if (j1 > 0) {
            p_282963_.blit(BREWING_STAND_LOCATION, i + 63, j + 14 + 29 - j1, 185, 29 - j1, 12, j1);
         }
      }

   }
}