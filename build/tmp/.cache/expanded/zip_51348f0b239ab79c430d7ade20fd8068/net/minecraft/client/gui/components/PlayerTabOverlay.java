package net.minecraft.client.gui.components;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.Optionull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.level.GameType;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PlayerTabOverlay {
   private static final Comparator<PlayerInfo> PLAYER_COMPARATOR = Comparator.<PlayerInfo>comparingInt((p_253306_) -> {
      return p_253306_.getGameMode() == GameType.SPECTATOR ? 1 : 0;
   }).thenComparing((p_269613_) -> {
      return Optionull.mapOrDefault(p_269613_.getTeam(), PlayerTeam::getName, "");
   }).thenComparing((p_253305_) -> {
      return p_253305_.getProfile().getName();
   }, String::compareToIgnoreCase);
   private static final ResourceLocation GUI_ICONS_LOCATION = new ResourceLocation("textures/gui/icons.png");
   public static final int MAX_ROWS_PER_COL = 20;
   public static final int HEART_EMPTY_CONTAINER = 16;
   public static final int HEART_EMPTY_CONTAINER_BLINKING = 25;
   public static final int HEART_FULL = 52;
   public static final int HEART_HALF_FULL = 61;
   public static final int HEART_GOLDEN_FULL = 160;
   public static final int HEART_GOLDEN_HALF_FULL = 169;
   public static final int HEART_GHOST_FULL = 70;
   public static final int HEART_GHOST_HALF_FULL = 79;
   private final Minecraft minecraft;
   private final Gui gui;
   @Nullable
   private Component footer;
   @Nullable
   private Component header;
   private boolean visible;
   private final Map<UUID, PlayerTabOverlay.HealthState> healthStates = new Object2ObjectOpenHashMap<>();

   public PlayerTabOverlay(Minecraft p_94527_, Gui p_94528_) {
      this.minecraft = p_94527_;
      this.gui = p_94528_;
   }

   public Component getNameForDisplay(PlayerInfo p_94550_) {
      return p_94550_.getTabListDisplayName() != null ? this.decorateName(p_94550_, p_94550_.getTabListDisplayName().copy()) : this.decorateName(p_94550_, PlayerTeam.formatNameForTeam(p_94550_.getTeam(), Component.literal(p_94550_.getProfile().getName())));
   }

   private Component decorateName(PlayerInfo p_94552_, MutableComponent p_94553_) {
      return p_94552_.getGameMode() == GameType.SPECTATOR ? p_94553_.withStyle(ChatFormatting.ITALIC) : p_94553_;
   }

   public void setVisible(boolean p_94557_) {
      if (this.visible != p_94557_) {
         this.healthStates.clear();
         this.visible = p_94557_;
         if (p_94557_) {
            Component component = ComponentUtils.formatList(this.getPlayerInfos(), Component.literal(", "), this::getNameForDisplay);
            this.minecraft.getNarrator().sayNow(Component.translatable("multiplayer.player.list.narration", component));
         }
      }

   }

   private List<PlayerInfo> getPlayerInfos() {
      return this.minecraft.player.connection.getListedOnlinePlayers().stream().sorted(PLAYER_COMPARATOR).limit(80L).toList();
   }

   public void render(GuiGraphics p_281484_, int p_283602_, Scoreboard p_282338_, @Nullable Objective p_282369_) {
      List<PlayerInfo> list = this.getPlayerInfos();
      int i = 0;
      int j = 0;

      for(PlayerInfo playerinfo : list) {
         int k = this.minecraft.font.width(this.getNameForDisplay(playerinfo));
         i = Math.max(i, k);
         if (p_282369_ != null && p_282369_.getRenderType() != ObjectiveCriteria.RenderType.HEARTS) {
            k = this.minecraft.font.width(" " + p_282338_.getOrCreatePlayerScore(playerinfo.getProfile().getName(), p_282369_).getScore());
            j = Math.max(j, k);
         }
      }

      if (!this.healthStates.isEmpty()) {
         Set<UUID> set = list.stream().map((p_250472_) -> {
            return p_250472_.getProfile().getId();
         }).collect(Collectors.toSet());
         this.healthStates.keySet().removeIf((p_248583_) -> {
            return !set.contains(p_248583_);
         });
      }

      int i3 = list.size();
      int j3 = i3;

      int k3;
      for(k3 = 1; j3 > 20; j3 = (i3 + k3 - 1) / k3) {
         ++k3;
      }

      boolean flag = this.minecraft.isLocalServer() || this.minecraft.getConnection().getConnection().isEncrypted();
      int l;
      if (p_282369_ != null) {
         if (p_282369_.getRenderType() == ObjectiveCriteria.RenderType.HEARTS) {
            l = 90;
         } else {
            l = j;
         }
      } else {
         l = 0;
      }

      int i1 = Math.min(k3 * ((flag ? 9 : 0) + i + l + 13), p_283602_ - 50) / k3;
      int j1 = p_283602_ / 2 - (i1 * k3 + (k3 - 1) * 5) / 2;
      int k1 = 10;
      int l1 = i1 * k3 + (k3 - 1) * 5;
      List<FormattedCharSequence> list1 = null;
      if (this.header != null) {
         list1 = this.minecraft.font.split(this.header, p_283602_ - 50);

         for(FormattedCharSequence formattedcharsequence : list1) {
            l1 = Math.max(l1, this.minecraft.font.width(formattedcharsequence));
         }
      }

      List<FormattedCharSequence> list2 = null;
      if (this.footer != null) {
         list2 = this.minecraft.font.split(this.footer, p_283602_ - 50);

         for(FormattedCharSequence formattedcharsequence1 : list2) {
            l1 = Math.max(l1, this.minecraft.font.width(formattedcharsequence1));
         }
      }

      if (list1 != null) {
         p_281484_.fill(p_283602_ / 2 - l1 / 2 - 1, k1 - 1, p_283602_ / 2 + l1 / 2 + 1, k1 + list1.size() * 9, Integer.MIN_VALUE);

         for(FormattedCharSequence formattedcharsequence2 : list1) {
            int i2 = this.minecraft.font.width(formattedcharsequence2);
            p_281484_.drawString(this.minecraft.font, formattedcharsequence2, p_283602_ / 2 - i2 / 2, k1, -1);
            k1 += 9;
         }

         ++k1;
      }

      p_281484_.fill(p_283602_ / 2 - l1 / 2 - 1, k1 - 1, p_283602_ / 2 + l1 / 2 + 1, k1 + j3 * 9, Integer.MIN_VALUE);
      int l3 = this.minecraft.options.getBackgroundColor(553648127);

      for(int i4 = 0; i4 < i3; ++i4) {
         int j4 = i4 / j3;
         int j2 = i4 % j3;
         int k2 = j1 + j4 * i1 + j4 * 5;
         int l2 = k1 + j2 * 9;
         p_281484_.fill(k2, l2, k2 + i1, l2 + 8, l3);
         RenderSystem.enableBlend();
         if (i4 < list.size()) {
            PlayerInfo playerinfo1 = list.get(i4);
            GameProfile gameprofile = playerinfo1.getProfile();
            if (flag) {
               Player player = this.minecraft.level.getPlayerByUUID(gameprofile.getId());
               boolean flag1 = player != null && LivingEntityRenderer.isEntityUpsideDown(player);
               boolean flag2 = player != null && player.isModelPartShown(PlayerModelPart.HAT);
               PlayerFaceRenderer.draw(p_281484_, playerinfo1.getSkinLocation(), k2, l2, 8, flag2, flag1);
               k2 += 9;
            }

            p_281484_.drawString(this.minecraft.font, this.getNameForDisplay(playerinfo1), k2, l2, playerinfo1.getGameMode() == GameType.SPECTATOR ? -1862270977 : -1);
            if (p_282369_ != null && playerinfo1.getGameMode() != GameType.SPECTATOR) {
               int l4 = k2 + i + 1;
               int i5 = l4 + l;
               if (i5 - l4 > 5) {
                  this.renderTablistScore(p_282369_, l2, gameprofile.getName(), l4, i5, gameprofile.getId(), p_281484_);
               }
            }

            this.renderPingIcon(p_281484_, i1, k2 - (flag ? 9 : 0), l2, playerinfo1);
         }
      }

      if (list2 != null) {
         k1 += j3 * 9 + 1;
         p_281484_.fill(p_283602_ / 2 - l1 / 2 - 1, k1 - 1, p_283602_ / 2 + l1 / 2 + 1, k1 + list2.size() * 9, Integer.MIN_VALUE);

         for(FormattedCharSequence formattedcharsequence3 : list2) {
            int k4 = this.minecraft.font.width(formattedcharsequence3);
            p_281484_.drawString(this.minecraft.font, formattedcharsequence3, p_283602_ / 2 - k4 / 2, k1, -1);
            k1 += 9;
         }
      }

   }

   protected void renderPingIcon(GuiGraphics p_283286_, int p_281809_, int p_282801_, int p_282223_, PlayerInfo p_282986_) {
      int i = 0;
      int j;
      if (p_282986_.getLatency() < 0) {
         j = 5;
      } else if (p_282986_.getLatency() < 150) {
         j = 0;
      } else if (p_282986_.getLatency() < 300) {
         j = 1;
      } else if (p_282986_.getLatency() < 600) {
         j = 2;
      } else if (p_282986_.getLatency() < 1000) {
         j = 3;
      } else {
         j = 4;
      }

      p_283286_.pose().pushPose();
      p_283286_.pose().translate(0.0F, 0.0F, 100.0F);
      p_283286_.blit(GUI_ICONS_LOCATION, p_282801_ + p_281809_ - 11, p_282223_, 0, 176 + j * 8, 10, 8);
      p_283286_.pose().popPose();
   }

   private void renderTablistScore(Objective p_283381_, int p_282557_, String p_283058_, int p_283533_, int p_281254_, UUID p_283099_, GuiGraphics p_282280_) {
      int i = p_283381_.getScoreboard().getOrCreatePlayerScore(p_283058_, p_283381_).getScore();
      if (p_283381_.getRenderType() == ObjectiveCriteria.RenderType.HEARTS) {
         this.renderTablistHearts(p_282557_, p_283533_, p_281254_, p_283099_, p_282280_, i);
      } else {
         String s = "" + ChatFormatting.YELLOW + i;
         p_282280_.drawString(this.minecraft.font, s, p_281254_ - this.minecraft.font.width(s), p_282557_, 16777215);
      }
   }

   private void renderTablistHearts(int p_282904_, int p_283173_, int p_282149_, UUID p_283348_, GuiGraphics p_281723_, int p_281354_) {
      PlayerTabOverlay.HealthState playertaboverlay$healthstate = this.healthStates.computeIfAbsent(p_283348_, (p_249546_) -> {
         return new PlayerTabOverlay.HealthState(p_281354_);
      });
      playertaboverlay$healthstate.update(p_281354_, (long)this.gui.getGuiTicks());
      int i = Mth.positiveCeilDiv(Math.max(p_281354_, playertaboverlay$healthstate.displayedValue()), 2);
      int j = Math.max(p_281354_, Math.max(playertaboverlay$healthstate.displayedValue(), 20)) / 2;
      boolean flag = playertaboverlay$healthstate.isBlinking((long)this.gui.getGuiTicks());
      if (i > 0) {
         int k = Mth.floor(Math.min((float)(p_282149_ - p_283173_ - 4) / (float)j, 9.0F));
         if (k <= 3) {
            float f = Mth.clamp((float)p_281354_ / 20.0F, 0.0F, 1.0F);
            int i1 = (int)((1.0F - f) * 255.0F) << 16 | (int)(f * 255.0F) << 8;
            String s = "" + (float)p_281354_ / 2.0F;
            if (p_282149_ - this.minecraft.font.width(s + "hp") >= p_283173_) {
               s = s + "hp";
            }

            p_281723_.drawString(this.minecraft.font, s, (p_282149_ + p_283173_ - this.minecraft.font.width(s)) / 2, p_282904_, i1);
         } else {
            for(int l = i; l < j; ++l) {
               p_281723_.blit(GUI_ICONS_LOCATION, p_283173_ + l * k, p_282904_, flag ? 25 : 16, 0, 9, 9);
            }

            for(int j1 = 0; j1 < i; ++j1) {
               p_281723_.blit(GUI_ICONS_LOCATION, p_283173_ + j1 * k, p_282904_, flag ? 25 : 16, 0, 9, 9);
               if (flag) {
                  if (j1 * 2 + 1 < playertaboverlay$healthstate.displayedValue()) {
                     p_281723_.blit(GUI_ICONS_LOCATION, p_283173_ + j1 * k, p_282904_, 70, 0, 9, 9);
                  }

                  if (j1 * 2 + 1 == playertaboverlay$healthstate.displayedValue()) {
                     p_281723_.blit(GUI_ICONS_LOCATION, p_283173_ + j1 * k, p_282904_, 79, 0, 9, 9);
                  }
               }

               if (j1 * 2 + 1 < p_281354_) {
                  p_281723_.blit(GUI_ICONS_LOCATION, p_283173_ + j1 * k, p_282904_, j1 >= 10 ? 160 : 52, 0, 9, 9);
               }

               if (j1 * 2 + 1 == p_281354_) {
                  p_281723_.blit(GUI_ICONS_LOCATION, p_283173_ + j1 * k, p_282904_, j1 >= 10 ? 169 : 61, 0, 9, 9);
               }
            }

         }
      }
   }

   public void setFooter(@Nullable Component p_94555_) {
      this.footer = p_94555_;
   }

   public void setHeader(@Nullable Component p_94559_) {
      this.header = p_94559_;
   }

   public void reset() {
      this.header = null;
      this.footer = null;
   }

   @OnlyIn(Dist.CLIENT)
   static class HealthState {
      private static final long DISPLAY_UPDATE_DELAY = 20L;
      private static final long DECREASE_BLINK_DURATION = 20L;
      private static final long INCREASE_BLINK_DURATION = 10L;
      private int lastValue;
      private int displayedValue;
      private long lastUpdateTick;
      private long blinkUntilTick;

      public HealthState(int p_250562_) {
         this.displayedValue = p_250562_;
         this.lastValue = p_250562_;
      }

      public void update(int p_251066_, long p_251460_) {
         if (p_251066_ != this.lastValue) {
            long i = p_251066_ < this.lastValue ? 20L : 10L;
            this.blinkUntilTick = p_251460_ + i;
            this.lastValue = p_251066_;
            this.lastUpdateTick = p_251460_;
         }

         if (p_251460_ - this.lastUpdateTick > 20L) {
            this.displayedValue = p_251066_;
         }

      }

      public int displayedValue() {
         return this.displayedValue;
      }

      public boolean isBlinking(long p_251847_) {
         return this.blinkUntilTick > p_251847_ && (this.blinkUntilTick - p_251847_) % 6L >= 3L;
      }
   }
}