package net.minecraft.client.gui.screens;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DatapackLoadFailureScreen extends Screen {
   private MultiLineLabel message = MultiLineLabel.EMPTY;
   private final Runnable callback;

   public DatapackLoadFailureScreen(Runnable p_95894_) {
      super(Component.translatable("datapackFailure.title"));
      this.callback = p_95894_;
   }

   protected void init() {
      super.init();
      this.message = MultiLineLabel.create(this.font, this.getTitle(), this.width - 50);
      this.addRenderableWidget(Button.builder(Component.translatable("datapackFailure.safeMode"), (p_95905_) -> {
         this.callback.run();
      }).bounds(this.width / 2 - 155, this.height / 6 + 96, 150, 20).build());
      this.addRenderableWidget(Button.builder(CommonComponents.GUI_TO_TITLE, (p_280793_) -> {
         this.minecraft.setScreen((Screen)null);
      }).bounds(this.width / 2 - 155 + 160, this.height / 6 + 96, 150, 20).build());
   }

   public void render(GuiGraphics p_283519_, int p_282196_, int p_283357_, float p_283026_) {
      this.renderBackground(p_283519_);
      this.message.renderCentered(p_283519_, this.width / 2, 70);
      super.render(p_283519_, p_282196_, p_283357_, p_283026_);
   }

   public boolean shouldCloseOnEsc() {
      return false;
   }
}