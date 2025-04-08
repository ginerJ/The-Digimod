package net.minecraft.client.gui.screens.controls;

import com.mojang.blaze3d.platform.InputConstants;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.OptionsSubScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class KeyBindsScreen extends OptionsSubScreen {
   @Nullable
   public KeyMapping selectedKey;
   public long lastKeySelection;
   private KeyBindsList keyBindsList;
   private Button resetButton;

   public KeyBindsScreen(Screen p_193980_, Options p_193981_) {
      super(p_193980_, p_193981_, Component.translatable("controls.keybinds.title"));
   }

   protected void init() {
      this.keyBindsList = new KeyBindsList(this, this.minecraft);
      this.addWidget(this.keyBindsList);
      this.resetButton = this.addRenderableWidget(Button.builder(Component.translatable("controls.resetAll"), (p_269619_) -> {
         for(KeyMapping keymapping : this.options.keyMappings) {
            keymapping.setToDefault();
         }

         this.keyBindsList.resetMappingAndUpdateButtons();
      }).bounds(this.width / 2 - 155, this.height - 29, 150, 20).build());
      this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, (p_280847_) -> {
         this.minecraft.setScreen(this.lastScreen);
      }).bounds(this.width / 2 - 155 + 160, this.height - 29, 150, 20).build());
   }

   public boolean mouseClicked(double p_193983_, double p_193984_, int p_193985_) {
      if (this.selectedKey != null) {
         this.options.setKey(this.selectedKey, InputConstants.Type.MOUSE.getOrCreate(p_193985_));
         this.selectedKey = null;
         this.keyBindsList.resetMappingAndUpdateButtons();
         return true;
      } else {
         return super.mouseClicked(p_193983_, p_193984_, p_193985_);
      }
   }

   public boolean keyPressed(int p_193987_, int p_193988_, int p_193989_) {
      if (this.selectedKey != null) {
         if (p_193987_ == 256) {
            this.selectedKey.setKeyModifierAndCode(net.minecraftforge.client.settings.KeyModifier.getActiveModifier(), InputConstants.UNKNOWN);
            this.options.setKey(this.selectedKey, InputConstants.UNKNOWN);
         } else {
            this.selectedKey.setKeyModifierAndCode(net.minecraftforge.client.settings.KeyModifier.getActiveModifier(), InputConstants.getKey(p_193987_, p_193988_));
            this.options.setKey(this.selectedKey, InputConstants.getKey(p_193987_, p_193988_));
         }

         if(!net.minecraftforge.client.settings.KeyModifier.isKeyCodeModifier(this.selectedKey.getKey()))
         this.selectedKey = null;
         this.lastKeySelection = Util.getMillis();
         this.keyBindsList.resetMappingAndUpdateButtons();
         return true;
      } else {
         return super.keyPressed(p_193987_, p_193988_, p_193989_);
      }
   }

   public void render(GuiGraphics p_282556_, int p_193992_, int p_193993_, float p_193994_) {
      this.renderBackground(p_282556_);
      this.keyBindsList.render(p_282556_, p_193992_, p_193993_, p_193994_);
      p_282556_.drawCenteredString(this.font, this.title, this.width / 2, 8, 16777215);
      boolean flag = false;

      for(KeyMapping keymapping : this.options.keyMappings) {
         if (!keymapping.isDefault()) {
            flag = true;
            break;
         }
      }

      this.resetButton.active = flag;
      super.render(p_282556_, p_193992_, p_193993_, p_193994_);
   }
}
