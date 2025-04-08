package net.minecraft.client.gui.screens.inventory;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ContainerScreen extends AbstractContainerScreen<ChestMenu> implements MenuAccess<ChestMenu> {
   private static final ResourceLocation CONTAINER_BACKGROUND = new ResourceLocation("textures/gui/container/generic_54.png");
   private final int containerRows;

   public ContainerScreen(ChestMenu p_98409_, Inventory p_98410_, Component p_98411_) {
      super(p_98409_, p_98410_, p_98411_);
      int i = 222;
      int j = 114;
      this.containerRows = p_98409_.getRowCount();
      this.imageHeight = 114 + this.containerRows * 18;
      this.inventoryLabelY = this.imageHeight - 94;
   }

   public void render(GuiGraphics p_282060_, int p_282533_, int p_281661_, float p_281873_) {
      this.renderBackground(p_282060_);
      super.render(p_282060_, p_282533_, p_281661_, p_281873_);
      this.renderTooltip(p_282060_, p_282533_, p_281661_);
   }

   protected void renderBg(GuiGraphics p_283694_, float p_282334_, int p_282603_, int p_282158_) {
      int i = (this.width - this.imageWidth) / 2;
      int j = (this.height - this.imageHeight) / 2;
      p_283694_.blit(CONTAINER_BACKGROUND, i, j, 0, 0, this.imageWidth, this.containerRows * 18 + 17);
      p_283694_.blit(CONTAINER_BACKGROUND, i, j + this.containerRows * 18 + 17, 0, 126, this.imageWidth, 96);
   }
}