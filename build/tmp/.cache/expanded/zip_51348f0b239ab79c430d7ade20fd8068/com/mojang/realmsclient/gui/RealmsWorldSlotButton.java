package com.mojang.realmsclient.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.RealmsWorldOptions;
import com.mojang.realmsclient.util.RealmsTextureManager;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RealmsWorldSlotButton extends Button {
   public static final ResourceLocation SLOT_FRAME_LOCATION = new ResourceLocation("realms", "textures/gui/realms/slot_frame.png");
   public static final ResourceLocation EMPTY_SLOT_LOCATION = new ResourceLocation("realms", "textures/gui/realms/empty_frame.png");
   public static final ResourceLocation CHECK_MARK_LOCATION = new ResourceLocation("minecraft", "textures/gui/checkmark.png");
   public static final ResourceLocation DEFAULT_WORLD_SLOT_1 = new ResourceLocation("minecraft", "textures/gui/title/background/panorama_0.png");
   public static final ResourceLocation DEFAULT_WORLD_SLOT_2 = new ResourceLocation("minecraft", "textures/gui/title/background/panorama_2.png");
   public static final ResourceLocation DEFAULT_WORLD_SLOT_3 = new ResourceLocation("minecraft", "textures/gui/title/background/panorama_3.png");
   private static final Component SLOT_ACTIVE_TOOLTIP = Component.translatable("mco.configure.world.slot.tooltip.active");
   private static final Component SWITCH_TO_MINIGAME_SLOT_TOOLTIP = Component.translatable("mco.configure.world.slot.tooltip.minigame");
   private static final Component SWITCH_TO_WORLD_SLOT_TOOLTIP = Component.translatable("mco.configure.world.slot.tooltip");
   private static final Component MINIGAME = Component.translatable("mco.worldSlot.minigame");
   private final Supplier<RealmsServer> serverDataProvider;
   private final Consumer<Component> toolTipSetter;
   private final int slotIndex;
   @Nullable
   private RealmsWorldSlotButton.State state;

   public RealmsWorldSlotButton(int p_87929_, int p_87930_, int p_87931_, int p_87932_, Supplier<RealmsServer> p_87933_, Consumer<Component> p_87934_, int p_87935_, Button.OnPress p_87936_) {
      super(p_87929_, p_87930_, p_87931_, p_87932_, CommonComponents.EMPTY, p_87936_, DEFAULT_NARRATION);
      this.serverDataProvider = p_87933_;
      this.slotIndex = p_87935_;
      this.toolTipSetter = p_87934_;
   }

   @Nullable
   public RealmsWorldSlotButton.State getState() {
      return this.state;
   }

   public void tick() {
      RealmsServer realmsserver = this.serverDataProvider.get();
      if (realmsserver != null) {
         RealmsWorldOptions realmsworldoptions = realmsserver.slots.get(this.slotIndex);
         boolean flag2 = this.slotIndex == 4;
         boolean flag;
         String s;
         long i;
         String s1;
         boolean flag1;
         if (flag2) {
            flag = realmsserver.worldType == RealmsServer.WorldType.MINIGAME;
            s = MINIGAME.getString();
            i = (long)realmsserver.minigameId;
            s1 = realmsserver.minigameImage;
            flag1 = realmsserver.minigameId == -1;
         } else {
            flag = realmsserver.activeSlot == this.slotIndex && realmsserver.worldType != RealmsServer.WorldType.MINIGAME;
            s = realmsworldoptions.getSlotName(this.slotIndex);
            i = realmsworldoptions.templateId;
            s1 = realmsworldoptions.templateImage;
            flag1 = realmsworldoptions.empty;
         }

         RealmsWorldSlotButton.Action realmsworldslotbutton$action = getAction(realmsserver, flag, flag2);
         Pair<Component, Component> pair = this.getTooltipAndNarration(realmsserver, s, flag1, flag2, realmsworldslotbutton$action);
         this.state = new RealmsWorldSlotButton.State(flag, s, i, s1, flag1, flag2, realmsworldslotbutton$action, pair.getFirst());
         this.setMessage(pair.getSecond());
      }
   }

   private static RealmsWorldSlotButton.Action getAction(RealmsServer p_87960_, boolean p_87961_, boolean p_87962_) {
      if (p_87961_) {
         if (!p_87960_.expired && p_87960_.state != RealmsServer.State.UNINITIALIZED) {
            return RealmsWorldSlotButton.Action.JOIN;
         }
      } else {
         if (!p_87962_) {
            return RealmsWorldSlotButton.Action.SWITCH_SLOT;
         }

         if (!p_87960_.expired) {
            return RealmsWorldSlotButton.Action.SWITCH_SLOT;
         }
      }

      return RealmsWorldSlotButton.Action.NOTHING;
   }

   private Pair<Component, Component> getTooltipAndNarration(RealmsServer p_87954_, String p_87955_, boolean p_87956_, boolean p_87957_, RealmsWorldSlotButton.Action p_87958_) {
      if (p_87958_ == RealmsWorldSlotButton.Action.NOTHING) {
         return Pair.of((Component)null, Component.literal(p_87955_));
      } else {
         Component component;
         if (p_87957_) {
            if (p_87956_) {
               component = CommonComponents.EMPTY;
            } else {
               component = CommonComponents.space().append(p_87955_).append(CommonComponents.SPACE).append(p_87954_.minigameName);
            }
         } else {
            component = CommonComponents.space().append(p_87955_);
         }

         Component component1;
         if (p_87958_ == RealmsWorldSlotButton.Action.JOIN) {
            component1 = SLOT_ACTIVE_TOOLTIP;
         } else {
            component1 = p_87957_ ? SWITCH_TO_MINIGAME_SLOT_TOOLTIP : SWITCH_TO_WORLD_SLOT_TOOLTIP;
         }

         Component component2 = component1.copy().append(component);
         return Pair.of(component1, component2);
      }
   }

   public void renderWidget(GuiGraphics p_282947_, int p_87965_, int p_87966_, float p_87967_) {
      if (this.state != null) {
         this.drawSlotFrame(p_282947_, this.getX(), this.getY(), p_87965_, p_87966_, this.state.isCurrentlyActiveSlot, this.state.slotName, this.slotIndex, this.state.imageId, this.state.image, this.state.empty, this.state.minigame, this.state.action, this.state.actionPrompt);
      }
   }

   private void drawSlotFrame(GuiGraphics p_282493_, int p_282407_, int p_283212_, int p_283646_, int p_283633_, boolean p_282019_, String p_283553_, int p_283521_, long p_281546_, @Nullable String p_283361_, boolean p_283516_, boolean p_281611_, RealmsWorldSlotButton.Action p_281804_, @Nullable Component p_282910_) {
      boolean flag = this.isHoveredOrFocused();
      if (this.isMouseOver((double)p_283646_, (double)p_283633_) && p_282910_ != null) {
         this.toolTipSetter.accept(p_282910_);
      }

      Minecraft minecraft = Minecraft.getInstance();
      ResourceLocation resourcelocation;
      if (p_281611_) {
         resourcelocation = RealmsTextureManager.worldTemplate(String.valueOf(p_281546_), p_283361_);
      } else if (p_283516_) {
         resourcelocation = EMPTY_SLOT_LOCATION;
      } else if (p_283361_ != null && p_281546_ != -1L) {
         resourcelocation = RealmsTextureManager.worldTemplate(String.valueOf(p_281546_), p_283361_);
      } else if (p_283521_ == 1) {
         resourcelocation = DEFAULT_WORLD_SLOT_1;
      } else if (p_283521_ == 2) {
         resourcelocation = DEFAULT_WORLD_SLOT_2;
      } else if (p_283521_ == 3) {
         resourcelocation = DEFAULT_WORLD_SLOT_3;
      } else {
         resourcelocation = EMPTY_SLOT_LOCATION;
      }

      if (p_282019_) {
         p_282493_.setColor(0.56F, 0.56F, 0.56F, 1.0F);
      }

      p_282493_.blit(resourcelocation, p_282407_ + 3, p_283212_ + 3, 0.0F, 0.0F, 74, 74, 74, 74);
      boolean flag1 = flag && p_281804_ != RealmsWorldSlotButton.Action.NOTHING;
      if (flag1) {
         p_282493_.setColor(1.0F, 1.0F, 1.0F, 1.0F);
      } else if (p_282019_) {
         p_282493_.setColor(0.8F, 0.8F, 0.8F, 1.0F);
      } else {
         p_282493_.setColor(0.56F, 0.56F, 0.56F, 1.0F);
      }

      p_282493_.blit(SLOT_FRAME_LOCATION, p_282407_, p_283212_, 0.0F, 0.0F, 80, 80, 80, 80);
      p_282493_.setColor(1.0F, 1.0F, 1.0F, 1.0F);
      if (p_282019_) {
         this.renderCheckMark(p_282493_, p_282407_, p_283212_);
      }

      p_282493_.drawCenteredString(minecraft.font, p_283553_, p_282407_ + 40, p_283212_ + 66, 16777215);
   }

   private void renderCheckMark(GuiGraphics p_281366_, int p_281849_, int p_283407_) {
      RenderSystem.enableBlend();
      p_281366_.blit(CHECK_MARK_LOCATION, p_281849_ + 67, p_283407_ + 4, 0.0F, 0.0F, 9, 8, 9, 8);
      RenderSystem.disableBlend();
   }

   @OnlyIn(Dist.CLIENT)
   public static enum Action {
      NOTHING,
      SWITCH_SLOT,
      JOIN;
   }

   @OnlyIn(Dist.CLIENT)
   public static class State {
      final boolean isCurrentlyActiveSlot;
      final String slotName;
      final long imageId;
      @Nullable
      final String image;
      public final boolean empty;
      public final boolean minigame;
      public final RealmsWorldSlotButton.Action action;
      @Nullable
      final Component actionPrompt;

      State(boolean p_87989_, String p_87990_, long p_87991_, @Nullable String p_87992_, boolean p_87993_, boolean p_87994_, RealmsWorldSlotButton.Action p_87995_, @Nullable Component p_87996_) {
         this.isCurrentlyActiveSlot = p_87989_;
         this.slotName = p_87990_;
         this.imageId = p_87991_;
         this.image = p_87992_;
         this.empty = p_87993_;
         this.minigame = p_87994_;
         this.action = p_87995_;
         this.actionPrompt = p_87996_;
      }
   }
}