package com.mojang.realmsclient.gui.screens;

import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.realms.RealmsLabel;
import net.minecraft.realms.RealmsObjectSelectionList;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.LevelSummary;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class RealmsSelectFileToUploadScreen extends RealmsScreen {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Component UNABLE_TO_LOAD_WORLD = Component.translatable("selectWorld.unable_to_load");
   static final Component WORLD_TEXT = Component.translatable("selectWorld.world");
   static final Component HARDCORE_TEXT = Component.translatable("mco.upload.hardcore").withStyle((p_264655_) -> {
      return p_264655_.withColor(-65536);
   });
   static final Component CHEATS_TEXT = Component.translatable("selectWorld.cheats");
   private static final DateFormat DATE_FORMAT = new SimpleDateFormat();
   private final RealmsResetWorldScreen lastScreen;
   private final long worldId;
   private final int slotId;
   Button uploadButton;
   List<LevelSummary> levelList = Lists.newArrayList();
   int selectedWorld = -1;
   RealmsSelectFileToUploadScreen.WorldSelectionList worldSelectionList;
   private final Runnable callback;

   public RealmsSelectFileToUploadScreen(long p_89498_, int p_89499_, RealmsResetWorldScreen p_89500_, Runnable p_89501_) {
      super(Component.translatable("mco.upload.select.world.title"));
      this.lastScreen = p_89500_;
      this.worldId = p_89498_;
      this.slotId = p_89499_;
      this.callback = p_89501_;
   }

   private void loadLevelList() throws Exception {
      LevelStorageSource.LevelCandidates levelstoragesource$levelcandidates = this.minecraft.getLevelSource().findLevelCandidates();
      this.levelList = this.minecraft.getLevelSource().loadLevelSummaries(levelstoragesource$levelcandidates).join().stream().filter((p_193517_) -> {
         return !p_193517_.requiresManualConversion() && !p_193517_.isLocked();
      }).collect(Collectors.toList());

      for(LevelSummary levelsummary : this.levelList) {
         this.worldSelectionList.addEntry(levelsummary);
      }

   }

   public void init() {
      this.worldSelectionList = new RealmsSelectFileToUploadScreen.WorldSelectionList();

      try {
         this.loadLevelList();
      } catch (Exception exception) {
         LOGGER.error("Couldn't load level list", (Throwable)exception);
         this.minecraft.setScreen(new RealmsGenericErrorScreen(UNABLE_TO_LOAD_WORLD, Component.nullToEmpty(exception.getMessage()), this.lastScreen));
         return;
      }

      this.addWidget(this.worldSelectionList);
      this.uploadButton = this.addRenderableWidget(Button.builder(Component.translatable("mco.upload.button.name"), (p_231307_) -> {
         this.upload();
      }).bounds(this.width / 2 - 154, this.height - 32, 153, 20).build());
      this.uploadButton.active = this.selectedWorld >= 0 && this.selectedWorld < this.levelList.size();
      this.addRenderableWidget(Button.builder(CommonComponents.GUI_BACK, (p_280747_) -> {
         this.minecraft.setScreen(this.lastScreen);
      }).bounds(this.width / 2 + 6, this.height - 32, 153, 20).build());
      this.addLabel(new RealmsLabel(Component.translatable("mco.upload.select.world.subtitle"), this.width / 2, row(-1), 10526880));
      if (this.levelList.isEmpty()) {
         this.addLabel(new RealmsLabel(Component.translatable("mco.upload.select.world.none"), this.width / 2, this.height / 2 - 20, 16777215));
      }

   }

   public Component getNarrationMessage() {
      return CommonComponents.joinForNarration(this.getTitle(), this.createLabelNarration());
   }

   private void upload() {
      if (this.selectedWorld != -1 && !this.levelList.get(this.selectedWorld).isHardcore()) {
         LevelSummary levelsummary = this.levelList.get(this.selectedWorld);
         this.minecraft.setScreen(new RealmsUploadScreen(this.worldId, this.slotId, this.lastScreen, levelsummary, this.callback));
      }

   }

   public void render(GuiGraphics p_281244_, int p_282772_, int p_281746_, float p_281757_) {
      this.renderBackground(p_281244_);
      this.worldSelectionList.render(p_281244_, p_282772_, p_281746_, p_281757_);
      p_281244_.drawCenteredString(this.font, this.title, this.width / 2, 13, 16777215);
      super.render(p_281244_, p_282772_, p_281746_, p_281757_);
   }

   public boolean keyPressed(int p_89506_, int p_89507_, int p_89508_) {
      if (p_89506_ == 256) {
         this.minecraft.setScreen(this.lastScreen);
         return true;
      } else {
         return super.keyPressed(p_89506_, p_89507_, p_89508_);
      }
   }

   static Component gameModeName(LevelSummary p_89535_) {
      return p_89535_.getGameMode().getLongDisplayName();
   }

   static String formatLastPlayed(LevelSummary p_89539_) {
      return DATE_FORMAT.format(new Date(p_89539_.getLastPlayed()));
   }

   @OnlyIn(Dist.CLIENT)
   class Entry extends ObjectSelectionList.Entry<RealmsSelectFileToUploadScreen.Entry> {
      private final LevelSummary levelSummary;
      private final String name;
      private final Component id;
      private final Component info;

      public Entry(LevelSummary p_89560_) {
         this.levelSummary = p_89560_;
         this.name = p_89560_.getLevelName();
         this.id = Component.translatable("mco.upload.entry.id", p_89560_.getLevelId(), RealmsSelectFileToUploadScreen.formatLastPlayed(p_89560_));
         Component component;
         if (p_89560_.isHardcore()) {
            component = RealmsSelectFileToUploadScreen.HARDCORE_TEXT;
         } else {
            component = RealmsSelectFileToUploadScreen.gameModeName(p_89560_);
         }

         if (p_89560_.hasCheats()) {
            component = Component.translatable("mco.upload.entry.cheats", component.getString(), RealmsSelectFileToUploadScreen.CHEATS_TEXT);
         }

         this.info = component;
      }

      public void render(GuiGraphics p_282307_, int p_281918_, int p_281770_, int p_282954_, int p_281599_, int p_281852_, int p_283452_, int p_282531_, boolean p_283120_, float p_282082_) {
         this.renderItem(p_282307_, p_281918_, p_282954_, p_281770_);
      }

      public boolean mouseClicked(double p_89562_, double p_89563_, int p_89564_) {
         RealmsSelectFileToUploadScreen.this.worldSelectionList.selectItem(RealmsSelectFileToUploadScreen.this.levelList.indexOf(this.levelSummary));
         return true;
      }

      protected void renderItem(GuiGraphics p_282872_, int p_283187_, int p_283611_, int p_282173_) {
         String s;
         if (this.name.isEmpty()) {
            s = RealmsSelectFileToUploadScreen.WORLD_TEXT + " " + (p_283187_ + 1);
         } else {
            s = this.name;
         }

         p_282872_.drawString(RealmsSelectFileToUploadScreen.this.font, s, p_283611_ + 2, p_282173_ + 1, 16777215, false);
         p_282872_.drawString(RealmsSelectFileToUploadScreen.this.font, this.id, p_283611_ + 2, p_282173_ + 12, 8421504, false);
         p_282872_.drawString(RealmsSelectFileToUploadScreen.this.font, this.info, p_283611_ + 2, p_282173_ + 12 + 10, 8421504, false);
      }

      public Component getNarration() {
         Component component = CommonComponents.joinLines(Component.literal(this.levelSummary.getLevelName()), Component.literal(RealmsSelectFileToUploadScreen.formatLastPlayed(this.levelSummary)), RealmsSelectFileToUploadScreen.gameModeName(this.levelSummary));
         return Component.translatable("narrator.select", component);
      }
   }

   @OnlyIn(Dist.CLIENT)
   class WorldSelectionList extends RealmsObjectSelectionList<RealmsSelectFileToUploadScreen.Entry> {
      public WorldSelectionList() {
         super(RealmsSelectFileToUploadScreen.this.width, RealmsSelectFileToUploadScreen.this.height, RealmsSelectFileToUploadScreen.row(0), RealmsSelectFileToUploadScreen.this.height - 40, 36);
      }

      public void addEntry(LevelSummary p_89588_) {
         this.addEntry(RealmsSelectFileToUploadScreen.this.new Entry(p_89588_));
      }

      public int getMaxPosition() {
         return RealmsSelectFileToUploadScreen.this.levelList.size() * 36;
      }

      public void renderBackground(GuiGraphics p_281249_) {
         RealmsSelectFileToUploadScreen.this.renderBackground(p_281249_);
      }

      public void setSelected(@Nullable RealmsSelectFileToUploadScreen.Entry p_89592_) {
         super.setSelected(p_89592_);
         RealmsSelectFileToUploadScreen.this.selectedWorld = this.children().indexOf(p_89592_);
         RealmsSelectFileToUploadScreen.this.uploadButton.active = RealmsSelectFileToUploadScreen.this.selectedWorld >= 0 && RealmsSelectFileToUploadScreen.this.selectedWorld < this.getItemCount() && !RealmsSelectFileToUploadScreen.this.levelList.get(RealmsSelectFileToUploadScreen.this.selectedWorld).isHardcore();
      }
   }
}