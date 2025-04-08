package net.minecraft.client.gui.screens.inventory;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.HopperMenu;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class HopperScreen extends AbstractContainerScreen<HopperMenu> {
   private static final ResourceLocation HOPPER_LOCATION = new ResourceLocation("textures/gui/container/hopper.png");

   public HopperScreen(HopperMenu p_98798_, Inventory p_98799_, Component p_98800_) {
      super(p_98798_, p_98799_, p_98800_);
      this.imageHeight = 133;
      this.inventoryLabelY = this.imageHeight - 94;
   }

   public void render(GuiGraphics p_282918_, int p_282102_, int p_282423_, float p_282621_) {
      this.renderBackground(p_282918_);
      super.render(p_282918_, p_282102_, p_282423_, p_282621_);
      this.renderTooltip(p_282918_, p_282102_, p_282423_);
   }

   protected void renderBg(GuiGraphics p_281616_, float p_282737_, int p_281678_, int p_281465_) {
      int i = (this.width - this.imageWidth) / 2;
      int j = (this.height - this.imageHeight) / 2;
      p_281616_.blit(HOPPER_LOCATION, i, j, 0, 0, this.imageWidth, this.imageHeight);
   }
}