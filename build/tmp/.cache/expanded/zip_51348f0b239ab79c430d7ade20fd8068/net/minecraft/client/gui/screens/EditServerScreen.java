package net.minecraft.client.gui.screens;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class EditServerScreen extends Screen {
   private static final Component NAME_LABEL = Component.translatable("addServer.enterName");
   private static final Component IP_LABEL = Component.translatable("addServer.enterIp");
   private Button addButton;
   private final BooleanConsumer callback;
   private final ServerData serverData;
   private EditBox ipEdit;
   private EditBox nameEdit;
   private final Screen lastScreen;

   public EditServerScreen(Screen p_96017_, BooleanConsumer p_96018_, ServerData p_96019_) {
      super(Component.translatable("addServer.title"));
      this.lastScreen = p_96017_;
      this.callback = p_96018_;
      this.serverData = p_96019_;
   }

   public void tick() {
      this.nameEdit.tick();
      this.ipEdit.tick();
   }

   protected void init() {
      this.nameEdit = new EditBox(this.font, this.width / 2 - 100, 66, 200, 20, Component.translatable("addServer.enterName"));
      this.nameEdit.setValue(this.serverData.name);
      this.nameEdit.setResponder((p_169304_) -> {
         this.updateAddButtonStatus();
      });
      this.addWidget(this.nameEdit);
      this.ipEdit = new EditBox(this.font, this.width / 2 - 100, 106, 200, 20, Component.translatable("addServer.enterIp"));
      this.ipEdit.setMaxLength(128);
      this.ipEdit.setValue(this.serverData.ip);
      this.ipEdit.setResponder((p_169302_) -> {
         this.updateAddButtonStatus();
      });
      this.addWidget(this.ipEdit);
      this.addRenderableWidget(CycleButton.builder(ServerData.ServerPackStatus::getName).withValues(ServerData.ServerPackStatus.values()).withInitialValue(this.serverData.getResourcePackStatus()).create(this.width / 2 - 100, this.height / 4 + 72, 200, 20, Component.translatable("addServer.resourcePack"), (p_169299_, p_169300_) -> {
         this.serverData.setResourcePackStatus(p_169300_);
      }));
      this.addButton = this.addRenderableWidget(Button.builder(Component.translatable("addServer.add"), (p_96030_) -> {
         this.onAdd();
      }).bounds(this.width / 2 - 100, this.height / 4 + 96 + 18, 200, 20).build());
      this.addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, (p_169297_) -> {
         this.callback.accept(false);
      }).bounds(this.width / 2 - 100, this.height / 4 + 120 + 18, 200, 20).build());
      this.setInitialFocus(this.nameEdit);
      this.updateAddButtonStatus();
   }

   public void resize(Minecraft p_96026_, int p_96027_, int p_96028_) {
      String s = this.ipEdit.getValue();
      String s1 = this.nameEdit.getValue();
      this.init(p_96026_, p_96027_, p_96028_);
      this.ipEdit.setValue(s);
      this.nameEdit.setValue(s1);
   }

   private void onAdd() {
      this.serverData.name = this.nameEdit.getValue();
      this.serverData.ip = this.ipEdit.getValue();
      this.callback.accept(true);
   }

   public void onClose() {
      this.minecraft.setScreen(this.lastScreen);
   }

   private void updateAddButtonStatus() {
      this.addButton.active = ServerAddress.isValidAddress(this.ipEdit.getValue()) && !this.nameEdit.getValue().isEmpty();
   }

   public void render(GuiGraphics p_282351_, int p_96022_, int p_96023_, float p_96024_) {
      this.renderBackground(p_282351_);
      p_282351_.drawCenteredString(this.font, this.title, this.width / 2, 17, 16777215);
      p_282351_.drawString(this.font, NAME_LABEL, this.width / 2 - 100, 53, 10526880);
      p_282351_.drawString(this.font, IP_LABEL, this.width / 2 - 100, 94, 10526880);
      this.nameEdit.render(p_282351_, p_96022_, p_96023_, p_96024_);
      this.ipEdit.render(p_282351_, p_96022_, p_96023_, p_96024_);
      super.render(p_282351_, p_96022_, p_96023_, p_96024_);
   }
}