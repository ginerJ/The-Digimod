package net.minecraft.client.gui.screens.inventory;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.DispenserMenu;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DispenserScreen extends AbstractContainerScreen<DispenserMenu> {
   private static final ResourceLocation CONTAINER_LOCATION = new ResourceLocation("textures/gui/container/dispenser.png");

   public DispenserScreen(DispenserMenu p_98685_, Inventory p_98686_, Component p_98687_) {
      super(p_98685_, p_98686_, p_98687_);
   }

   protected void init() {
      super.init();
      this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
   }

   public void render(GuiGraphics p_283282_, int p_282467_, int p_282129_, float p_281965_) {
      this.renderBackground(p_283282_);
      super.render(p_283282_, p_282467_, p_282129_, p_281965_);
      this.renderTooltip(p_283282_, p_282467_, p_282129_);
   }

   protected void renderBg(GuiGraphics p_283137_, float p_282476_, int p_281600_, int p_283194_) {
      int i = (this.width - this.imageWidth) / 2;
      int j = (this.height - this.imageHeight) / 2;
      p_283137_.blit(CONTAINER_LOCATION, i, j, 0, 0, this.imageWidth, this.imageHeight);
   }
}