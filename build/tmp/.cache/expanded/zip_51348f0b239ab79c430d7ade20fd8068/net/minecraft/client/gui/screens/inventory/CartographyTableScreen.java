package net.minecraft.client.gui.screens.inventory;

import javax.annotation.Nullable;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.CartographyTableMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CartographyTableScreen extends AbstractContainerScreen<CartographyTableMenu> {
   private static final ResourceLocation BG_LOCATION = new ResourceLocation("textures/gui/container/cartography_table.png");

   public CartographyTableScreen(CartographyTableMenu p_98349_, Inventory p_98350_, Component p_98351_) {
      super(p_98349_, p_98350_, p_98351_);
      this.titleLabelY -= 2;
   }

   public void render(GuiGraphics p_281331_, int p_281706_, int p_282996_, float p_283037_) {
      super.render(p_281331_, p_281706_, p_282996_, p_283037_);
      this.renderTooltip(p_281331_, p_281706_, p_282996_);
   }

   protected void renderBg(GuiGraphics p_282101_, float p_282697_, int p_282380_, int p_282327_) {
      this.renderBackground(p_282101_);
      int i = this.leftPos;
      int j = this.topPos;
      p_282101_.blit(BG_LOCATION, i, j, 0, 0, this.imageWidth, this.imageHeight);
      ItemStack itemstack = this.menu.getSlot(1).getItem();
      boolean flag = itemstack.is(Items.MAP);
      boolean flag1 = itemstack.is(Items.PAPER);
      boolean flag2 = itemstack.is(Items.GLASS_PANE);
      ItemStack itemstack1 = this.menu.getSlot(0).getItem();
      boolean flag3 = false;
      Integer integer;
      MapItemSavedData mapitemsaveddata;
      if (itemstack1.is(Items.FILLED_MAP)) {
         integer = MapItem.getMapId(itemstack1);
         mapitemsaveddata = MapItem.getSavedData(integer, this.minecraft.level);
         if (mapitemsaveddata != null) {
            if (mapitemsaveddata.locked) {
               flag3 = true;
               if (flag1 || flag2) {
                  p_282101_.blit(BG_LOCATION, i + 35, j + 31, this.imageWidth + 50, 132, 28, 21);
               }
            }

            if (flag1 && mapitemsaveddata.scale >= 4) {
               flag3 = true;
               p_282101_.blit(BG_LOCATION, i + 35, j + 31, this.imageWidth + 50, 132, 28, 21);
            }
         }
      } else {
         integer = null;
         mapitemsaveddata = null;
      }

      this.renderResultingMap(p_282101_, integer, mapitemsaveddata, flag, flag1, flag2, flag3);
   }

   private void renderResultingMap(GuiGraphics p_282167_, @Nullable Integer p_282064_, @Nullable MapItemSavedData p_282045_, boolean p_282086_, boolean p_283531_, boolean p_282645_, boolean p_281646_) {
      int i = this.leftPos;
      int j = this.topPos;
      if (p_283531_ && !p_281646_) {
         p_282167_.blit(BG_LOCATION, i + 67, j + 13, this.imageWidth, 66, 66, 66);
         this.renderMap(p_282167_, p_282064_, p_282045_, i + 85, j + 31, 0.226F);
      } else if (p_282086_) {
         p_282167_.blit(BG_LOCATION, i + 67 + 16, j + 13, this.imageWidth, 132, 50, 66);
         this.renderMap(p_282167_, p_282064_, p_282045_, i + 86, j + 16, 0.34F);
         p_282167_.pose().pushPose();
         p_282167_.pose().translate(0.0F, 0.0F, 1.0F);
         p_282167_.blit(BG_LOCATION, i + 67, j + 13 + 16, this.imageWidth, 132, 50, 66);
         this.renderMap(p_282167_, p_282064_, p_282045_, i + 70, j + 32, 0.34F);
         p_282167_.pose().popPose();
      } else if (p_282645_) {
         p_282167_.blit(BG_LOCATION, i + 67, j + 13, this.imageWidth, 0, 66, 66);
         this.renderMap(p_282167_, p_282064_, p_282045_, i + 71, j + 17, 0.45F);
         p_282167_.pose().pushPose();
         p_282167_.pose().translate(0.0F, 0.0F, 1.0F);
         p_282167_.blit(BG_LOCATION, i + 66, j + 12, 0, this.imageHeight, 66, 66);
         p_282167_.pose().popPose();
      } else {
         p_282167_.blit(BG_LOCATION, i + 67, j + 13, this.imageWidth, 0, 66, 66);
         this.renderMap(p_282167_, p_282064_, p_282045_, i + 71, j + 17, 0.45F);
      }

   }

   private void renderMap(GuiGraphics p_282298_, @Nullable Integer p_281648_, @Nullable MapItemSavedData p_282897_, int p_281632_, int p_282115_, float p_283388_) {
      if (p_281648_ != null && p_282897_ != null) {
         p_282298_.pose().pushPose();
         p_282298_.pose().translate((float)p_281632_, (float)p_282115_, 1.0F);
         p_282298_.pose().scale(p_283388_, p_283388_, 1.0F);
         this.minecraft.gameRenderer.getMapRenderer().render(p_282298_.pose(), p_282298_.bufferSource(), p_281648_, p_282897_, true, 15728880);
         p_282298_.flush();
         p_282298_.pose().popPose();
      }

   }
}