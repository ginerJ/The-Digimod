package net.minecraft.core;

import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.Lifecycle;
import com.mojang.util.UUIDTypeAdapter;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.UUID;
import net.minecraft.Util;

public final class UUIDUtil {
   public static final Codec<UUID> CODEC = Codec.INT_STREAM.comapFlatMap((p_235884_) -> {
      return Util.fixedSize(p_235884_, 4).map(UUIDUtil::uuidFromIntArray);
   }, (p_235888_) -> {
      return Arrays.stream(uuidToIntArray(p_235888_));
   });
   public static final Codec<UUID> STRING_CODEC = Codec.STRING.comapFlatMap((p_274732_) -> {
      try {
         return DataResult.success(UUID.fromString(p_274732_), Lifecycle.stable());
      } catch (IllegalArgumentException illegalargumentexception) {
         return DataResult.error(() -> {
            return "Invalid UUID " + p_274732_ + ": " + illegalargumentexception.getMessage();
         });
      }
   }, UUID::toString);
   public static Codec<UUID> AUTHLIB_CODEC = Codec.either(CODEC, Codec.STRING.comapFlatMap((p_274735_) -> {
      try {
         return DataResult.success(UUIDTypeAdapter.fromString(p_274735_), Lifecycle.stable());
      } catch (IllegalArgumentException illegalargumentexception) {
         return DataResult.error(() -> {
            return "Invalid UUID " + p_274735_ + ": " + illegalargumentexception.getMessage();
         });
      }
   }, UUIDTypeAdapter::fromUUID)).xmap((p_253364_) -> {
      return p_253364_.map((p_253362_) -> {
         return p_253362_;
      }, (p_253361_) -> {
         return p_253361_;
      });
   }, Either::right);
   public static final int UUID_BYTES = 16;
   private static final String UUID_PREFIX_OFFLINE_PLAYER = "OfflinePlayer:";

   private UUIDUtil() {
   }

   public static UUID uuidFromIntArray(int[] p_235886_) {
      return new UUID((long)p_235886_[0] << 32 | (long)p_235886_[1] & 4294967295L, (long)p_235886_[2] << 32 | (long)p_235886_[3] & 4294967295L);
   }

   public static int[] uuidToIntArray(UUID p_235882_) {
      long i = p_235882_.getMostSignificantBits();
      long j = p_235882_.getLeastSignificantBits();
      return leastMostToIntArray(i, j);
   }

   private static int[] leastMostToIntArray(long p_235873_, long p_235874_) {
      return new int[]{(int)(p_235873_ >> 32), (int)p_235873_, (int)(p_235874_ >> 32), (int)p_235874_};
   }

   public static byte[] uuidToByteArray(UUID p_241285_) {
      byte[] abyte = new byte[16];
      ByteBuffer.wrap(abyte).order(ByteOrder.BIG_ENDIAN).putLong(p_241285_.getMostSignificantBits()).putLong(p_241285_.getLeastSignificantBits());
      return abyte;
   }

   public static UUID readUUID(Dynamic<?> p_235878_) {
      int[] aint = p_235878_.asIntStream().toArray();
      if (aint.length != 4) {
         throw new IllegalArgumentException("Could not read UUID. Expected int-array of length 4, got " + aint.length + ".");
      } else {
         return uuidFromIntArray(aint);
      }
   }

   public static UUID getOrCreatePlayerUUID(GameProfile p_235876_) {
      UUID uuid = p_235876_.getId();
      if (uuid == null) {
         uuid = createOfflinePlayerUUID(p_235876_.getName());
      }

      return uuid;
   }

   public static UUID createOfflinePlayerUUID(String p_235880_) {
      return UUID.nameUUIDFromBytes(("OfflinePlayer:" + p_235880_).getBytes(StandardCharsets.UTF_8));
   }
}