package net.minecraft.client.gui.screens;

import net.minecraft.Util;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class AccessibilityOptionsScreen extends SimpleOptionsSubScreen {
   private static OptionInstance<?>[] options(Options p_232691_) {
      return new OptionInstance[]{p_232691_.narrator(), p_232691_.showSubtitles(), p_232691_.highContrast(), p_232691_.autoJump(), p_232691_.textBackgroundOpacity(), p_232691_.backgroundForChatOnly(), p_232691_.chatOpacity(), p_232691_.chatLineSpacing(), p_232691_.chatDelay(), p_232691_.notificationDisplayTime(), p_232691_.toggleCrouch(), p_232691_.toggleSprint(), p_232691_.screenEffectScale(), p_232691_.fovEffectScale(), p_232691_.darknessEffectScale(), p_232691_.damageTiltStrength(), p_232691_.glintSpeed(), p_232691_.glintStrength(), p_232691_.hideLightningFlash(), p_232691_.darkMojangStudiosBackground(), p_232691_.panoramaSpeed()};
   }

   public AccessibilityOptionsScreen(Screen p_95504_, Options p_95505_) {
      super(p_95504_, p_95505_, Component.translatable("options.accessibility.title"), options(p_95505_));
   }

   protected void init() {
      super.init();
      AbstractWidget abstractwidget = this.list.findOption(this.options.highContrast());
      if (abstractwidget != null && !this.minecraft.getResourcePackRepository().getAvailableIds().contains("high_contrast")) {
         abstractwidget.active = false;
         abstractwidget.setTooltip(Tooltip.create(Component.translatable("options.accessibility.high_contrast.error.tooltip")));
      }

   }

   protected void createFooter() {
      this.addRenderableWidget(Button.builder(Component.translatable("options.accessibility.link"), (p_280784_) -> {
         this.minecraft.setScreen(new ConfirmLinkScreen((p_280783_) -> {
            if (p_280783_) {
               Util.getPlatform().openUri("https://aka.ms/MinecraftJavaAccessibility");
            }

            this.minecraft.setScreen(this);
         }, "https://aka.ms/MinecraftJavaAccessibility", true));
      }).bounds(this.width / 2 - 155, this.height - 27, 150, 20).build());
      this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, (p_280785_) -> {
         this.minecraft.setScreen(this.lastScreen);
      }).bounds(this.width / 2 + 5, this.height - 27, 150, 20).build());
   }
}