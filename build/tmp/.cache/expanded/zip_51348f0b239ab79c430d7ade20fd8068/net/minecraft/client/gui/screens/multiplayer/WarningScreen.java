package net.minecraft.client.gui.screens.multiplayer;

import javax.annotation.Nullable;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class WarningScreen extends Screen {
   private final Component content;
   @Nullable
   private final Component check;
   private final Component narration;
   @Nullable
   protected Checkbox stopShowing;
   private MultiLineLabel message = MultiLineLabel.EMPTY;

   protected WarningScreen(Component p_239894_, Component p_239895_, Component p_239896_) {
      this(p_239894_, p_239895_, (Component)null, p_239896_);
   }

   protected WarningScreen(Component p_232852_, Component p_232853_, @Nullable Component p_232854_, Component p_232855_) {
      super(p_232852_);
      this.content = p_232853_;
      this.check = p_232854_;
      this.narration = p_232855_;
   }

   protected abstract void initButtons(int p_210922_);

   protected void init() {
      super.init();
      this.message = MultiLineLabel.create(this.font, this.content, this.width - 100);
      int i = (this.message.getLineCount() + 1) * this.getLineHeight();
      if (this.check != null) {
         int j = this.font.width(this.check);
         this.stopShowing = new Checkbox(this.width / 2 - j / 2 - 8, 76 + i, j + 24, 20, this.check, false);
         this.addRenderableWidget(this.stopShowing);
      }

      this.initButtons(i);
   }

   public Component getNarrationMessage() {
      return this.narration;
   }

   public void render(GuiGraphics p_282073_, int p_283174_, int p_282617_, float p_282654_) {
      this.renderBackground(p_282073_);
      this.renderTitle(p_282073_);
      int i = this.width / 2 - this.message.getWidth() / 2;
      this.message.renderLeftAligned(p_282073_, i, 70, this.getLineHeight(), 16777215);
      super.render(p_282073_, p_283174_, p_282617_, p_282654_);
   }

   protected void renderTitle(GuiGraphics p_281725_) {
      p_281725_.drawString(this.font, this.title, 25, 30, 16777215);
   }

   protected int getLineHeight() {
      return 9 * 2;
   }
}