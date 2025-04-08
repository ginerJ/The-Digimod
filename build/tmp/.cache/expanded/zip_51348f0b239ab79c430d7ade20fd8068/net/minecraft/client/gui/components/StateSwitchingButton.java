package net.minecraft.client.gui.components;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class StateSwitchingButton extends AbstractWidget {
   protected ResourceLocation resourceLocation;
   protected boolean isStateTriggered;
   protected int xTexStart;
   protected int yTexStart;
   protected int xDiffTex;
   protected int yDiffTex;

   public StateSwitchingButton(int p_94615_, int p_94616_, int p_94617_, int p_94618_, boolean p_94619_) {
      super(p_94615_, p_94616_, p_94617_, p_94618_, CommonComponents.EMPTY);
      this.isStateTriggered = p_94619_;
   }

   public void initTextureValues(int p_94625_, int p_94626_, int p_94627_, int p_94628_, ResourceLocation p_94629_) {
      this.xTexStart = p_94625_;
      this.yTexStart = p_94626_;
      this.xDiffTex = p_94627_;
      this.yDiffTex = p_94628_;
      this.resourceLocation = p_94629_;
   }

   public void setStateTriggered(boolean p_94636_) {
      this.isStateTriggered = p_94636_;
   }

   public boolean isStateTriggered() {
      return this.isStateTriggered;
   }

   public void updateWidgetNarration(NarrationElementOutput p_259073_) {
      this.defaultButtonNarrationText(p_259073_);
   }

   public void renderWidget(GuiGraphics p_283051_, int p_283010_, int p_281379_, float p_283453_) {
      RenderSystem.disableDepthTest();
      int i = this.xTexStart;
      int j = this.yTexStart;
      if (this.isStateTriggered) {
         i += this.xDiffTex;
      }

      if (this.isHoveredOrFocused()) {
         j += this.yDiffTex;
      }

      p_283051_.blit(this.resourceLocation, this.getX(), this.getY(), i, j, this.width, this.height);
      RenderSystem.enableDepthTest();
   }
}