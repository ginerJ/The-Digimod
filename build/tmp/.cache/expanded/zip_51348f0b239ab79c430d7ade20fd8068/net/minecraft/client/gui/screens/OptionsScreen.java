package net.minecraft.client.gui.screens;

import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.LockIconButton;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.layouts.SpacerElement;
import net.minecraft.client.gui.screens.controls.ControlsScreen;
import net.minecraft.client.gui.screens.packs.PackSelectionScreen;
import net.minecraft.client.gui.screens.telemetry.TelemetryInfoScreen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundChangeDifficultyPacket;
import net.minecraft.network.protocol.game.ServerboundLockDifficultyPacket;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.world.Difficulty;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class OptionsScreen extends Screen {
   private static final Component SKIN_CUSTOMIZATION = Component.translatable("options.skinCustomisation");
   private static final Component SOUNDS = Component.translatable("options.sounds");
   private static final Component VIDEO = Component.translatable("options.video");
   private static final Component CONTROLS = Component.translatable("options.controls");
   private static final Component LANGUAGE = Component.translatable("options.language");
   private static final Component CHAT = Component.translatable("options.chat.title");
   private static final Component RESOURCEPACK = Component.translatable("options.resourcepack");
   private static final Component ACCESSIBILITY = Component.translatable("options.accessibility.title");
   private static final Component TELEMETRY = Component.translatable("options.telemetry");
   private static final Component CREDITS_AND_ATTRIBUTION = Component.translatable("options.credits_and_attribution");
   private static final int COLUMNS = 2;
   private final Screen lastScreen;
   private final Options options;
   private CycleButton<Difficulty> difficultyButton;
   private LockIconButton lockButton;

   public OptionsScreen(Screen p_96242_, Options p_96243_) {
      super(Component.translatable("options.title"));
      this.lastScreen = p_96242_;
      this.options = p_96243_;
   }

   protected void init() {
      GridLayout gridlayout = new GridLayout();
      gridlayout.defaultCellSetting().paddingHorizontal(5).paddingBottom(4).alignHorizontallyCenter();
      GridLayout.RowHelper gridlayout$rowhelper = gridlayout.createRowHelper(2);
      gridlayout$rowhelper.addChild(this.options.fov().createButton(this.minecraft.options, 0, 0, 150));
      gridlayout$rowhelper.addChild(this.createOnlineButton());
      gridlayout$rowhelper.addChild(SpacerElement.height(26), 2);
      gridlayout$rowhelper.addChild(this.openScreenButton(SKIN_CUSTOMIZATION, () -> {
         return new SkinCustomizationScreen(this, this.options);
      }));
      gridlayout$rowhelper.addChild(this.openScreenButton(SOUNDS, () -> {
         return new SoundOptionsScreen(this, this.options);
      }));
      gridlayout$rowhelper.addChild(this.openScreenButton(VIDEO, () -> {
         return new VideoSettingsScreen(this, this.options);
      }));
      gridlayout$rowhelper.addChild(this.openScreenButton(CONTROLS, () -> {
         return new ControlsScreen(this, this.options);
      }));
      gridlayout$rowhelper.addChild(this.openScreenButton(LANGUAGE, () -> {
         return new LanguageSelectScreen(this, this.options, this.minecraft.getLanguageManager());
      }));
      gridlayout$rowhelper.addChild(this.openScreenButton(CHAT, () -> {
         return new ChatOptionsScreen(this, this.options);
      }));
      gridlayout$rowhelper.addChild(this.openScreenButton(RESOURCEPACK, () -> {
         return new PackSelectionScreen(this.minecraft.getResourcePackRepository(), this::applyPacks, this.minecraft.getResourcePackDirectory(), Component.translatable("resourcePack.title"));
      }));
      gridlayout$rowhelper.addChild(this.openScreenButton(ACCESSIBILITY, () -> {
         return new AccessibilityOptionsScreen(this, this.options);
      }));
      gridlayout$rowhelper.addChild(this.openScreenButton(TELEMETRY, () -> {
         return new TelemetryInfoScreen(this, this.options);
      }));
      gridlayout$rowhelper.addChild(this.openScreenButton(CREDITS_AND_ATTRIBUTION, () -> {
         return new CreditsAndAttributionScreen(this);
      }));
      gridlayout$rowhelper.addChild(Button.builder(CommonComponents.GUI_DONE, (p_280809_) -> {
         this.minecraft.setScreen(this.lastScreen);
      }).width(200).build(), 2, gridlayout$rowhelper.newCellSettings().paddingTop(6));
      gridlayout.arrangeElements();
      FrameLayout.alignInRectangle(gridlayout, 0, this.height / 6 - 12, this.width, this.height, 0.5F, 0.0F);
      gridlayout.visitWidgets(this::addRenderableWidget);
   }

   private void applyPacks(PackRepository p_275714_) {
      this.options.updateResourcePacks(p_275714_);
      this.minecraft.setScreen(this);
   }

   private LayoutElement createOnlineButton() {
      if (this.minecraft.level != null && this.minecraft.hasSingleplayerServer()) {
         this.difficultyButton = createDifficultyButton(0, 0, "options.difficulty", this.minecraft);
         if (!this.minecraft.level.getLevelData().isHardcore()) {
            this.lockButton = new LockIconButton(0, 0, (p_280806_) -> {
               this.minecraft.setScreen(new ConfirmScreen(this::lockCallback, Component.translatable("difficulty.lock.title"), Component.translatable("difficulty.lock.question", this.minecraft.level.getLevelData().getDifficulty().getDisplayName())));
            });
            this.difficultyButton.setWidth(this.difficultyButton.getWidth() - this.lockButton.getWidth());
            this.lockButton.setLocked(this.minecraft.level.getLevelData().isDifficultyLocked());
            this.lockButton.active = !this.lockButton.isLocked();
            this.difficultyButton.active = !this.lockButton.isLocked();
            LinearLayout linearlayout = new LinearLayout(150, 0, LinearLayout.Orientation.HORIZONTAL);
            linearlayout.addChild(this.difficultyButton);
            linearlayout.addChild(this.lockButton);
            return linearlayout;
         } else {
            this.difficultyButton.active = false;
            return this.difficultyButton;
         }
      } else {
         return Button.builder(Component.translatable("options.online"), (p_280805_) -> {
            this.minecraft.setScreen(OnlineOptionsScreen.createOnlineOptionsScreen(this.minecraft, this, this.options));
         }).bounds(this.width / 2 + 5, this.height / 6 - 12 + 24, 150, 20).build();
      }
   }

   public static CycleButton<Difficulty> createDifficultyButton(int p_262051_, int p_261805_, String p_261598_, Minecraft p_261922_) {
      return CycleButton.builder(Difficulty::getDisplayName).withValues(Difficulty.values()).withInitialValue(p_261922_.level.getDifficulty()).create(p_262051_, p_261805_, 150, 20, Component.translatable(p_261598_), (p_193854_, p_193855_) -> {
         p_261922_.getConnection().send(new ServerboundChangeDifficultyPacket(p_193855_));
      });
   }

   private void lockCallback(boolean p_96261_) {
      this.minecraft.setScreen(this);
      if (p_96261_ && this.minecraft.level != null) {
         this.minecraft.getConnection().send(new ServerboundLockDifficultyPacket(true));
         this.lockButton.setLocked(true);
         this.lockButton.active = false;
         this.difficultyButton.active = false;
      }

   }

   public void removed() {
      this.options.save();
   }

   public void render(GuiGraphics p_283520_, int p_281826_, int p_283378_, float p_281975_) {
      this.renderBackground(p_283520_);
      p_283520_.drawCenteredString(this.font, this.title, this.width / 2, 15, 16777215);
      super.render(p_283520_, p_281826_, p_283378_, p_281975_);
   }

    @Override
    public void onClose() {
        // We need to consider 2 potential parent screens here:
        // 1. From the main menu, in which case display the main menu
        // 2. From the pause menu, in which case exit back to game
        this.minecraft.setScreen(this.lastScreen instanceof PauseScreen ? null : this.lastScreen);
    }

   private Button openScreenButton(Component p_261565_, Supplier<Screen> p_262119_) {
      return Button.builder(p_261565_, (p_280808_) -> {
         this.minecraft.setScreen(p_262119_.get());
      }).build();
   }
}
