package net.minecraft.client.gui.components;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class AbstractStringWidget extends AbstractWidget {
   private final Font font;
   private int color = 16777215;

   public AbstractStringWidget(int p_270910_, int p_270297_, int p_270088_, int p_270842_, Component p_270063_, Font p_270327_) {
      super(p_270910_, p_270297_, p_270088_, p_270842_, p_270063_);
      this.font = p_270327_;
   }

   protected void updateWidgetNarration(NarrationElementOutput p_270859_) {
   }

   public AbstractStringWidget setColor(int p_270638_) {
      this.color = p_270638_;
      return this;
   }

   protected final Font getFont() {
      return this.font;
   }

   protected final int getColor() {
      return this.color;
   }
}