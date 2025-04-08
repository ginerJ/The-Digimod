package net.minecraft.client.gui.screens.debug;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GameModeSwitcherScreen extends Screen {
   static final ResourceLocation GAMEMODE_SWITCHER_LOCATION = new ResourceLocation("textures/gui/container/gamemode_switcher.png");
   private static final int SPRITE_SHEET_WIDTH = 128;
   private static final int SPRITE_SHEET_HEIGHT = 128;
   private static final int SLOT_AREA = 26;
   private static final int SLOT_PADDING = 5;
   private static final int SLOT_AREA_PADDED = 31;
   private static final int HELP_TIPS_OFFSET_Y = 5;
   private static final int ALL_SLOTS_WIDTH = GameModeSwitcherScreen.GameModeIcon.values().length * 31 - 5;
   private static final Component SELECT_KEY = Component.translatable("debug.gamemodes.select_next", Component.translatable("debug.gamemodes.press_f4").withStyle(ChatFormatting.AQUA));
   private final GameModeSwitcherScreen.GameModeIcon previousHovered;
   private GameModeSwitcherScreen.GameModeIcon currentlyHovered;
   private int firstMouseX;
   private int firstMouseY;
   private boolean setFirstMousePos;
   private final List<GameModeSwitcherScreen.GameModeSlot> slots = Lists.newArrayList();

   public GameModeSwitcherScreen() {
      super(GameNarrator.NO_TITLE);
      this.previousHovered = GameModeSwitcherScreen.GameModeIcon.getFromGameType(this.getDefaultSelected());
      this.currentlyHovered = this.previousHovered;
   }

   private GameType getDefaultSelected() {
      MultiPlayerGameMode multiplayergamemode = Minecraft.getInstance().gameMode;
      GameType gametype = multiplayergamemode.getPreviousPlayerMode();
      if (gametype != null) {
         return gametype;
      } else {
         return multiplayergamemode.getPlayerMode() == GameType.CREATIVE ? GameType.SURVIVAL : GameType.CREATIVE;
      }
   }

   protected void init() {
      super.init();
      this.currentlyHovered = this.previousHovered;

      for(int i = 0; i < GameModeSwitcherScreen.GameModeIcon.VALUES.length; ++i) {
         GameModeSwitcherScreen.GameModeIcon gamemodeswitcherscreen$gamemodeicon = GameModeSwitcherScreen.GameModeIcon.VALUES[i];
         this.slots.add(new GameModeSwitcherScreen.GameModeSlot(gamemodeswitcherscreen$gamemodeicon, this.width / 2 - ALL_SLOTS_WIDTH / 2 + i * 31, this.height / 2 - 31));
      }

   }

   public void render(GuiGraphics p_281834_, int p_283223_, int p_282178_, float p_281339_) {
      if (!this.checkToClose()) {
         p_281834_.pose().pushPose();
         RenderSystem.enableBlend();
         int i = this.width / 2 - 62;
         int j = this.height / 2 - 31 - 27;
         p_281834_.blit(GAMEMODE_SWITCHER_LOCATION, i, j, 0.0F, 0.0F, 125, 75, 128, 128);
         p_281834_.pose().popPose();
         super.render(p_281834_, p_283223_, p_282178_, p_281339_);
         p_281834_.drawCenteredString(this.font, this.currentlyHovered.getName(), this.width / 2, this.height / 2 - 31 - 20, -1);
         p_281834_.drawCenteredString(this.font, SELECT_KEY, this.width / 2, this.height / 2 + 5, 16777215);
         if (!this.setFirstMousePos) {
            this.firstMouseX = p_283223_;
            this.firstMouseY = p_282178_;
            this.setFirstMousePos = true;
         }

         boolean flag = this.firstMouseX == p_283223_ && this.firstMouseY == p_282178_;

         for(GameModeSwitcherScreen.GameModeSlot gamemodeswitcherscreen$gamemodeslot : this.slots) {
            gamemodeswitcherscreen$gamemodeslot.render(p_281834_, p_283223_, p_282178_, p_281339_);
            gamemodeswitcherscreen$gamemodeslot.setSelected(this.currentlyHovered == gamemodeswitcherscreen$gamemodeslot.icon);
            if (!flag && gamemodeswitcherscreen$gamemodeslot.isHoveredOrFocused()) {
               this.currentlyHovered = gamemodeswitcherscreen$gamemodeslot.icon;
            }
         }

      }
   }

   private void switchToHoveredGameMode() {
      switchToHoveredGameMode(this.minecraft, this.currentlyHovered);
   }

   private static void switchToHoveredGameMode(Minecraft p_281340_, GameModeSwitcherScreen.GameModeIcon p_281358_) {
      if (p_281340_.gameMode != null && p_281340_.player != null) {
         GameModeSwitcherScreen.GameModeIcon gamemodeswitcherscreen$gamemodeicon = GameModeSwitcherScreen.GameModeIcon.getFromGameType(p_281340_.gameMode.getPlayerMode());
         if (p_281340_.player.hasPermissions(2) && p_281358_ != gamemodeswitcherscreen$gamemodeicon) {
            p_281340_.player.connection.sendUnsignedCommand(p_281358_.getCommand());
         }

      }
   }

   private boolean checkToClose() {
      if (!InputConstants.isKeyDown(this.minecraft.getWindow().getWindow(), 292)) {
         this.switchToHoveredGameMode();
         this.minecraft.setScreen((Screen)null);
         return true;
      } else {
         return false;
      }
   }

   public boolean keyPressed(int p_97553_, int p_97554_, int p_97555_) {
      if (p_97553_ == 293) {
         this.setFirstMousePos = false;
         this.currentlyHovered = this.currentlyHovered.getNext();
         return true;
      } else {
         return super.keyPressed(p_97553_, p_97554_, p_97555_);
      }
   }

   public boolean isPauseScreen() {
      return false;
   }

   @OnlyIn(Dist.CLIENT)
   static enum GameModeIcon {
      CREATIVE(Component.translatable("gameMode.creative"), "gamemode creative", new ItemStack(Blocks.GRASS_BLOCK)),
      SURVIVAL(Component.translatable("gameMode.survival"), "gamemode survival", new ItemStack(Items.IRON_SWORD)),
      ADVENTURE(Component.translatable("gameMode.adventure"), "gamemode adventure", new ItemStack(Items.MAP)),
      SPECTATOR(Component.translatable("gameMode.spectator"), "gamemode spectator", new ItemStack(Items.ENDER_EYE));

      protected static final GameModeSwitcherScreen.GameModeIcon[] VALUES = values();
      private static final int ICON_AREA = 16;
      protected static final int ICON_TOP_LEFT = 5;
      final Component name;
      final String command;
      final ItemStack renderStack;

      private GameModeIcon(Component p_97594_, String p_97595_, ItemStack p_97596_) {
         this.name = p_97594_;
         this.command = p_97595_;
         this.renderStack = p_97596_;
      }

      void drawIcon(GuiGraphics p_282609_, int p_283301_, int p_281692_) {
         p_282609_.renderItem(this.renderStack, p_283301_, p_281692_);
      }

      Component getName() {
         return this.name;
      }

      String getCommand() {
         return this.command;
      }

      GameModeSwitcherScreen.GameModeIcon getNext() {
         GameModeSwitcherScreen.GameModeIcon gamemodeswitcherscreen$gamemodeicon;
         switch (this) {
            case CREATIVE:
               gamemodeswitcherscreen$gamemodeicon = SURVIVAL;
               break;
            case SURVIVAL:
               gamemodeswitcherscreen$gamemodeicon = ADVENTURE;
               break;
            case ADVENTURE:
               gamemodeswitcherscreen$gamemodeicon = SPECTATOR;
               break;
            case SPECTATOR:
               gamemodeswitcherscreen$gamemodeicon = CREATIVE;
               break;
            default:
               throw new IncompatibleClassChangeError();
         }

         return gamemodeswitcherscreen$gamemodeicon;
      }

      static GameModeSwitcherScreen.GameModeIcon getFromGameType(GameType p_283307_) {
         GameModeSwitcherScreen.GameModeIcon gamemodeswitcherscreen$gamemodeicon;
         switch (p_283307_) {
            case SPECTATOR:
               gamemodeswitcherscreen$gamemodeicon = SPECTATOR;
               break;
            case SURVIVAL:
               gamemodeswitcherscreen$gamemodeicon = SURVIVAL;
               break;
            case CREATIVE:
               gamemodeswitcherscreen$gamemodeicon = CREATIVE;
               break;
            case ADVENTURE:
               gamemodeswitcherscreen$gamemodeicon = ADVENTURE;
               break;
            default:
               throw new IncompatibleClassChangeError();
         }

         return gamemodeswitcherscreen$gamemodeicon;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public class GameModeSlot extends AbstractWidget {
      final GameModeSwitcherScreen.GameModeIcon icon;
      private boolean isSelected;

      public GameModeSlot(GameModeSwitcherScreen.GameModeIcon p_97627_, int p_97628_, int p_97629_) {
         super(p_97628_, p_97629_, 26, 26, p_97627_.getName());
         this.icon = p_97627_;
      }

      public void renderWidget(GuiGraphics p_281380_, int p_283094_, int p_283558_, float p_282631_) {
         this.drawSlot(p_281380_);
         this.icon.drawIcon(p_281380_, this.getX() + 5, this.getY() + 5);
         if (this.isSelected) {
            this.drawSelection(p_281380_);
         }

      }

      public void updateWidgetNarration(NarrationElementOutput p_259120_) {
         this.defaultButtonNarrationText(p_259120_);
      }

      public boolean isHoveredOrFocused() {
         return super.isHoveredOrFocused() || this.isSelected;
      }

      public void setSelected(boolean p_97644_) {
         this.isSelected = p_97644_;
      }

      private void drawSlot(GuiGraphics p_281786_) {
         p_281786_.blit(GameModeSwitcherScreen.GAMEMODE_SWITCHER_LOCATION, this.getX(), this.getY(), 0.0F, 75.0F, 26, 26, 128, 128);
      }

      private void drawSelection(GuiGraphics p_281820_) {
         p_281820_.blit(GameModeSwitcherScreen.GAMEMODE_SWITCHER_LOCATION, this.getX(), this.getY(), 26.0F, 75.0F, 26, 26, 128, 128);
      }
   }
}