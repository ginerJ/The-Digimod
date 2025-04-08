package net.minecraft.client.gui.screens;

import net.minecraft.Util;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DemoIntroScreen extends Screen {
   private static final ResourceLocation DEMO_BACKGROUND_LOCATION = new ResourceLocation("textures/gui/demo_background.png");
   private MultiLineLabel movementMessage = MultiLineLabel.EMPTY;
   private MultiLineLabel durationMessage = MultiLineLabel.EMPTY;

   public DemoIntroScreen() {
      super(Component.translatable("demo.help.title"));
   }

   protected void init() {
      int i = -16;
      this.addRenderableWidget(Button.builder(Component.translatable("demo.help.buy"), (p_280797_) -> {
         p_280797_.active = false;
         Util.getPlatform().openUri("https://aka.ms/BuyMinecraftJava");
      }).bounds(this.width / 2 - 116, this.height / 2 + 62 + -16, 114, 20).build());
      this.addRenderableWidget(Button.builder(Component.translatable("demo.help.later"), (p_280798_) -> {
         this.minecraft.setScreen((Screen)null);
         this.minecraft.mouseHandler.grabMouse();
      }).bounds(this.width / 2 + 2, this.height / 2 + 62 + -16, 114, 20).build());
      Options options = this.minecraft.options;
      this.movementMessage = MultiLineLabel.create(this.font, Component.translatable("demo.help.movementShort", options.keyUp.getTranslatedKeyMessage(), options.keyLeft.getTranslatedKeyMessage(), options.keyDown.getTranslatedKeyMessage(), options.keyRight.getTranslatedKeyMessage()), Component.translatable("demo.help.movementMouse"), Component.translatable("demo.help.jump", options.keyJump.getTranslatedKeyMessage()), Component.translatable("demo.help.inventory", options.keyInventory.getTranslatedKeyMessage()));
      this.durationMessage = MultiLineLabel.create(this.font, Component.translatable("demo.help.fullWrapped"), 218);
   }

   public void renderBackground(GuiGraphics p_283391_) {
      super.renderBackground(p_283391_);
      int i = (this.width - 248) / 2;
      int j = (this.height - 166) / 2;
      p_283391_.blit(DEMO_BACKGROUND_LOCATION, i, j, 0, 0, 248, 166);
   }

   public void render(GuiGraphics p_281247_, int p_281844_, int p_283693_, float p_281842_) {
      this.renderBackground(p_281247_);
      int i = (this.width - 248) / 2 + 10;
      int j = (this.height - 166) / 2 + 8;
      p_281247_.drawString(this.font, this.title, i, j, 2039583, false);
      j = this.movementMessage.renderLeftAlignedNoShadow(p_281247_, i, j + 12, 12, 5197647);
      this.durationMessage.renderLeftAlignedNoShadow(p_281247_, i, j + 20, 9, 2039583);
      super.render(p_281247_, p_281844_, p_283693_, p_281842_);
   }
}