package net.minecraft.client.gui.screens.controls;

import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.MouseSettingsScreen;
import net.minecraft.client.gui.screens.OptionsSubScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ControlsScreen extends OptionsSubScreen {
   private static final int ROW_SPACING = 24;

   public ControlsScreen(Screen p_97519_, Options p_97520_) {
      super(p_97519_, p_97520_, Component.translatable("controls.title"));
   }

   protected void init() {
      super.init();
      int i = this.width / 2 - 155;
      int j = i + 160;
      int k = this.height / 6 - 12;
      this.addRenderableWidget(Button.builder(Component.translatable("options.mouse_settings"), (p_280846_) -> {
         this.minecraft.setScreen(new MouseSettingsScreen(this, this.options));
      }).bounds(i, k, 150, 20).build());
      this.addRenderableWidget(Button.builder(Component.translatable("controls.keybinds"), (p_280844_) -> {
         this.minecraft.setScreen(new KeyBindsScreen(this, this.options));
      }).bounds(j, k, 150, 20).build());
      k += 24;
      this.addRenderableWidget(this.options.toggleCrouch().createButton(this.options, i, k, 150));
      this.addRenderableWidget(this.options.toggleSprint().createButton(this.options, j, k, 150));
      k += 24;
      this.addRenderableWidget(this.options.autoJump().createButton(this.options, i, k, 150));
      this.addRenderableWidget(this.options.operatorItemsTab().createButton(this.options, j, k, 150));
      k += 24;
      this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, (p_280845_) -> {
         this.minecraft.setScreen(this.lastScreen);
      }).bounds(this.width / 2 - 100, k, 200, 20).build());
   }

   public void render(GuiGraphics p_282220_, int p_281404_, int p_281386_, float p_281394_) {
      this.renderBackground(p_282220_);
      p_282220_.drawCenteredString(this.font, this.title, this.width / 2, 15, 16777215);
      super.render(p_282220_, p_281404_, p_281386_, p_281394_);
   }
}