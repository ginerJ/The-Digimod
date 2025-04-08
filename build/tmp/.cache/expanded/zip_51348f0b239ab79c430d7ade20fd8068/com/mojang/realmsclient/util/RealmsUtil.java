package com.mojang.realmsclient.util;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.util.UUIDTypeAdapter;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.PlayerFaceRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RealmsUtil {
   static final MinecraftSessionService SESSION_SERVICE = Minecraft.getInstance().getMinecraftSessionService();
   private static final Component RIGHT_NOW = Component.translatable("mco.util.time.now");
   private static final LoadingCache<String, GameProfile> GAME_PROFILE_CACHE = CacheBuilder.newBuilder().expireAfterWrite(60L, TimeUnit.MINUTES).build(new CacheLoader<String, GameProfile>() {
      public GameProfile load(String p_90229_) {
         return RealmsUtil.SESSION_SERVICE.fillProfileProperties(new GameProfile(UUIDTypeAdapter.fromString(p_90229_), (String)null), false);
      }
   });
   private static final int MINUTES = 60;
   private static final int HOURS = 3600;
   private static final int DAYS = 86400;

   public static String uuidToName(String p_90222_) {
      return GAME_PROFILE_CACHE.getUnchecked(p_90222_).getName();
   }

   public static GameProfile getGameProfile(String p_270932_) {
      return GAME_PROFILE_CACHE.getUnchecked(p_270932_);
   }

   public static Component convertToAgePresentation(long p_287679_) {
      if (p_287679_ < 0L) {
         return RIGHT_NOW;
      } else {
         long i = p_287679_ / 1000L;
         if (i < 60L) {
            return Component.translatable("mco.time.secondsAgo", i);
         } else if (i < 3600L) {
            long l = i / 60L;
            return Component.translatable("mco.time.minutesAgo", l);
         } else if (i < 86400L) {
            long k = i / 3600L;
            return Component.translatable("mco.time.hoursAgo", k);
         } else {
            long j = i / 86400L;
            return Component.translatable("mco.time.daysAgo", j);
         }
      }
   }

   public static Component convertToAgePresentationFromInstant(Date p_287698_) {
      return convertToAgePresentation(System.currentTimeMillis() - p_287698_.getTime());
   }

   public static void renderPlayerFace(GuiGraphics p_281255_, int p_281818_, int p_281791_, int p_282088_, String p_282512_) {
      GameProfile gameprofile = getGameProfile(p_282512_);
      ResourceLocation resourcelocation = Minecraft.getInstance().getSkinManager().getInsecureSkinLocation(gameprofile);
      PlayerFaceRenderer.draw(p_281255_, resourcelocation, p_281818_, p_281791_, p_282088_);
   }
}