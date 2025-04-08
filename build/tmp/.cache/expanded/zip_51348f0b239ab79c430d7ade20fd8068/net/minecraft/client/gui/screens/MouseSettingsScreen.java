package net.minecraft.client.gui.screens;

import com.mojang.blaze3d.platform.InputConstants;
import java.util.Arrays;
import java.util.stream.Stream;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.OptionsList;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MouseSettingsScreen extends OptionsSubScreen {
   private OptionsList list;

   private static OptionInstance<?>[] options(Options p_232749_) {
      return new OptionInstance[]{p_232749_.sensitivity(), p_232749_.invertYMouse(), p_232749_.mouseWheelSensitivity(), p_232749_.discreteMouseScroll(), p_232749_.touchscreen()};
   }

   public MouseSettingsScreen(Screen p_96222_, Options p_96223_) {
      super(p_96222_, p_96223_, Component.translatable("options.mouse_settings.title"));
   }

   protected void init() {
      this.list = new OptionsList(this.minecraft, this.width, this.height, 32, this.height - 32, 25);
      if (InputConstants.isRawMouseInputSupported()) {
         this.list.addSmall(Stream.concat(Arrays.stream(options(this.options)), Stream.of(this.options.rawMouseInput())).toArray((p_232747_) -> {
            return new OptionInstance[p_232747_];
         }));
      } else {
         this.list.addSmall(options(this.options));
      }

      this.addWidget(this.list);
      this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, (p_280804_) -> {
         this.options.save();
         this.minecraft.setScreen(this.lastScreen);
      }).bounds(this.width / 2 - 100, this.height - 27, 200, 20).build());
   }

   public void render(GuiGraphics p_281246_, int p_282657_, int p_282507_, float p_282093_) {
      this.renderBackground(p_281246_);
      this.list.render(p_281246_, p_282657_, p_282507_, p_282093_);
      p_281246_.drawCenteredString(this.font, this.title, this.width / 2, 5, 16777215);
      super.render(p_281246_, p_282657_, p_282507_, p_282093_);
   }
}