package net.minecraft.client.gui.components;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class StringWidget extends AbstractStringWidget {
   private float alignX = 0.5F;

   public StringWidget(Component p_268211_, Font p_267963_) {
      this(0, 0, p_267963_.width(p_268211_.getVisualOrderText()), 9, p_268211_, p_267963_);
   }

   public StringWidget(int p_268183_, int p_268082_, Component p_268069_, Font p_268121_) {
      this(0, 0, p_268183_, p_268082_, p_268069_, p_268121_);
   }

   public StringWidget(int p_268199_, int p_268137_, int p_268178_, int p_268169_, Component p_268285_, Font p_268047_) {
      super(p_268199_, p_268137_, p_268178_, p_268169_, p_268285_, p_268047_);
      this.active = false;
   }

   public StringWidget setColor(int p_270680_) {
      super.setColor(p_270680_);
      return this;
   }

   private StringWidget horizontalAlignment(float p_267947_) {
      this.alignX = p_267947_;
      return this;
   }

   public StringWidget alignLeft() {
      return this.horizontalAlignment(0.0F);
   }

   public StringWidget alignCenter() {
      return this.horizontalAlignment(0.5F);
   }

   public StringWidget alignRight() {
      return this.horizontalAlignment(1.0F);
   }

   public void renderWidget(GuiGraphics p_281367_, int p_268221_, int p_268001_, float p_268214_) {
      Component component = this.getMessage();
      Font font = this.getFont();
      int i = this.getX() + Math.round(this.alignX * (float)(this.getWidth() - font.width(component)));
      int j = this.getY() + (this.getHeight() - 9) / 2;
      p_281367_.drawString(font, component, i, j, this.getColor());
   }
}