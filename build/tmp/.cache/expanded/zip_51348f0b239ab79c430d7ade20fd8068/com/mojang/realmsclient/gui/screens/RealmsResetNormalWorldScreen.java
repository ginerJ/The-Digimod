package com.mojang.realmsclient.gui.screens;

import com.mojang.realmsclient.util.LevelType;
import com.mojang.realmsclient.util.WorldGenerationInfo;
import java.util.function.Consumer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.realms.RealmsScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RealmsResetNormalWorldScreen extends RealmsScreen {
   private static final Component SEED_LABEL = Component.translatable("mco.reset.world.seed");
   private final Consumer<WorldGenerationInfo> callback;
   private EditBox seedEdit;
   private LevelType levelType = LevelType.DEFAULT;
   private boolean generateStructures = true;
   private final Component buttonTitle;

   public RealmsResetNormalWorldScreen(Consumer<WorldGenerationInfo> p_167438_, Component p_167439_) {
      super(Component.translatable("mco.reset.world.generate"));
      this.callback = p_167438_;
      this.buttonTitle = p_167439_;
   }

   public void tick() {
      this.seedEdit.tick();
      super.tick();
   }

   public void init() {
      this.seedEdit = new EditBox(this.minecraft.font, this.width / 2 - 100, row(2), 200, 20, (EditBox)null, Component.translatable("mco.reset.world.seed"));
      this.seedEdit.setMaxLength(32);
      this.addWidget(this.seedEdit);
      this.setInitialFocus(this.seedEdit);
      this.addRenderableWidget(CycleButton.builder(LevelType::getName).withValues(LevelType.values()).withInitialValue(this.levelType).create(this.width / 2 - 102, row(4), 205, 20, Component.translatable("selectWorld.mapType"), (p_167441_, p_167442_) -> {
         this.levelType = p_167442_;
      }));
      this.addRenderableWidget(CycleButton.onOffBuilder(this.generateStructures).create(this.width / 2 - 102, row(6) - 2, 205, 20, Component.translatable("selectWorld.mapFeatures"), (p_167444_, p_167445_) -> {
         this.generateStructures = p_167445_;
      }));
      this.addRenderableWidget(Button.builder(this.buttonTitle, (p_89291_) -> {
         this.callback.accept(new WorldGenerationInfo(this.seedEdit.getValue(), this.levelType, this.generateStructures));
      }).bounds(this.width / 2 - 102, row(12), 97, 20).build());
      this.addRenderableWidget(Button.builder(CommonComponents.GUI_BACK, (p_89288_) -> {
         this.onClose();
      }).bounds(this.width / 2 + 8, row(12), 97, 20).build());
   }

   public void onClose() {
      this.callback.accept((WorldGenerationInfo)null);
   }

   public void render(GuiGraphics p_281862_, int p_282455_, int p_281572_, float p_282211_) {
      this.renderBackground(p_281862_);
      p_281862_.drawCenteredString(this.font, this.title, this.width / 2, 17, 16777215);
      p_281862_.drawString(this.font, SEED_LABEL, this.width / 2 - 100, row(1), 10526880, false);
      this.seedEdit.render(p_281862_, p_282455_, p_281572_, p_282211_);
      super.render(p_281862_, p_282455_, p_281572_, p_282211_);
   }
}