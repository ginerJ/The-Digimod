package net.minecraft.client.gui.screens;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GenericDirtMessageScreen extends Screen {
   public GenericDirtMessageScreen(Component p_96061_) {
      super(p_96061_);
   }

   public boolean shouldCloseOnEsc() {
      return false;
   }

   public void render(GuiGraphics p_281274_, int p_283012_, int p_282072_, float p_282608_) {
      this.renderDirtBackground(p_281274_);
      p_281274_.drawCenteredString(this.font, this.title, this.width / 2, 70, 16777215);
      super.render(p_281274_, p_283012_, p_282072_, p_282608_);
   }
}