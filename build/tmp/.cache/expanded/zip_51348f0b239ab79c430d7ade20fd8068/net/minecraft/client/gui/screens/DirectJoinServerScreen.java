package net.minecraft.client.gui.screens;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DirectJoinServerScreen extends Screen {
   private static final Component ENTER_IP_LABEL = Component.translatable("addServer.enterIp");
   private Button selectButton;
   private final ServerData serverData;
   private EditBox ipEdit;
   private final BooleanConsumer callback;
   private final Screen lastScreen;

   public DirectJoinServerScreen(Screen p_95960_, BooleanConsumer p_95961_, ServerData p_95962_) {
      super(Component.translatable("selectServer.direct"));
      this.lastScreen = p_95960_;
      this.serverData = p_95962_;
      this.callback = p_95961_;
   }

   public void tick() {
      this.ipEdit.tick();
   }

   public boolean keyPressed(int p_95964_, int p_95965_, int p_95966_) {
      if (!this.selectButton.active || this.getFocused() != this.ipEdit || p_95964_ != 257 && p_95964_ != 335) {
         return super.keyPressed(p_95964_, p_95965_, p_95966_);
      } else {
         this.onSelect();
         return true;
      }
   }

   protected void init() {
      this.ipEdit = new EditBox(this.font, this.width / 2 - 100, 116, 200, 20, Component.translatable("addServer.enterIp"));
      this.ipEdit.setMaxLength(128);
      this.ipEdit.setValue(this.minecraft.options.lastMpIp);
      this.ipEdit.setResponder((p_95983_) -> {
         this.updateSelectButtonStatus();
      });
      this.addWidget(this.ipEdit);
      this.selectButton = this.addRenderableWidget(Button.builder(Component.translatable("selectServer.select"), (p_95981_) -> {
         this.onSelect();
      }).bounds(this.width / 2 - 100, this.height / 4 + 96 + 12, 200, 20).build());
      this.addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, (p_95977_) -> {
         this.callback.accept(false);
      }).bounds(this.width / 2 - 100, this.height / 4 + 120 + 12, 200, 20).build());
      this.setInitialFocus(this.ipEdit);
      this.updateSelectButtonStatus();
   }

   public void resize(Minecraft p_95973_, int p_95974_, int p_95975_) {
      String s = this.ipEdit.getValue();
      this.init(p_95973_, p_95974_, p_95975_);
      this.ipEdit.setValue(s);
   }

   private void onSelect() {
      this.serverData.ip = this.ipEdit.getValue();
      this.callback.accept(true);
   }

   public void onClose() {
      this.minecraft.setScreen(this.lastScreen);
   }

   public void removed() {
      this.minecraft.options.lastMpIp = this.ipEdit.getValue();
      this.minecraft.options.save();
   }

   private void updateSelectButtonStatus() {
      this.selectButton.active = ServerAddress.isValidAddress(this.ipEdit.getValue());
   }

   public void render(GuiGraphics p_282464_, int p_95969_, int p_95970_, float p_95971_) {
      this.renderBackground(p_282464_);
      p_282464_.drawCenteredString(this.font, this.title, this.width / 2, 20, 16777215);
      p_282464_.drawString(this.font, ENTER_IP_LABEL, this.width / 2 - 100, 100, 10526880);
      this.ipEdit.render(p_282464_, p_95969_, p_95970_, p_95971_);
      super.render(p_282464_, p_95969_, p_95970_, p_95971_);
   }
}