package com.mojang.realmsclient.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Date;
import java.util.UUID;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class JsonUtils {
   public static <T> T getRequired(String p_275573_, JsonObject p_275650_, Function<JsonObject, T> p_275655_) {
      JsonElement jsonelement = p_275650_.get(p_275573_);
      if (jsonelement != null && !jsonelement.isJsonNull()) {
         if (!jsonelement.isJsonObject()) {
            throw new IllegalStateException("Required property " + p_275573_ + " was not a JsonObject as espected");
         } else {
            return p_275655_.apply(jsonelement.getAsJsonObject());
         }
      } else {
         throw new IllegalStateException("Missing required property: " + p_275573_);
      }
   }

   public static String getRequiredString(String p_275692_, JsonObject p_275706_) {
      String s = getStringOr(p_275692_, p_275706_, (String)null);
      if (s == null) {
         throw new IllegalStateException("Missing required property: " + p_275692_);
      } else {
         return s;
      }
   }

   @Nullable
   public static String getStringOr(String p_90162_, JsonObject p_90163_, @Nullable String p_90164_) {
      JsonElement jsonelement = p_90163_.get(p_90162_);
      if (jsonelement != null) {
         return jsonelement.isJsonNull() ? p_90164_ : jsonelement.getAsString();
      } else {
         return p_90164_;
      }
   }

   @Nullable
   public static UUID getUuidOr(String p_275342_, JsonObject p_275515_, @Nullable UUID p_275232_) {
      String s = getStringOr(p_275342_, p_275515_, (String)null);
      return s == null ? p_275232_ : UUID.fromString(s);
   }

   public static int getIntOr(String p_90154_, JsonObject p_90155_, int p_90156_) {
      JsonElement jsonelement = p_90155_.get(p_90154_);
      if (jsonelement != null) {
         return jsonelement.isJsonNull() ? p_90156_ : jsonelement.getAsInt();
      } else {
         return p_90156_;
      }
   }

   public static long getLongOr(String p_90158_, JsonObject p_90159_, long p_90160_) {
      JsonElement jsonelement = p_90159_.get(p_90158_);
      if (jsonelement != null) {
         return jsonelement.isJsonNull() ? p_90160_ : jsonelement.getAsLong();
      } else {
         return p_90160_;
      }
   }

   public static boolean getBooleanOr(String p_90166_, JsonObject p_90167_, boolean p_90168_) {
      JsonElement jsonelement = p_90167_.get(p_90166_);
      if (jsonelement != null) {
         return jsonelement.isJsonNull() ? p_90168_ : jsonelement.getAsBoolean();
      } else {
         return p_90168_;
      }
   }

   public static Date getDateOr(String p_90151_, JsonObject p_90152_) {
      JsonElement jsonelement = p_90152_.get(p_90151_);
      return jsonelement != null ? new Date(Long.parseLong(jsonelement.getAsString())) : new Date();
   }
}