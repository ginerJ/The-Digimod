package net.minecraft.network.chat;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;

public final class TextColor {
   private static final String CUSTOM_COLOR_PREFIX = "#";
   public static final Codec<TextColor> CODEC = Codec.STRING.comapFlatMap((p_274794_) -> {
      TextColor textcolor = parseColor(p_274794_);
      return textcolor != null ? DataResult.success(textcolor) : DataResult.error(() -> {
         return "String is not a valid color name or hex color code";
      });
   }, TextColor::serialize);
   private static final Map<ChatFormatting, TextColor> LEGACY_FORMAT_TO_COLOR = Stream.of(ChatFormatting.values()).filter(ChatFormatting::isColor).collect(ImmutableMap.toImmutableMap(Function.identity(), (p_237301_) -> {
      return new TextColor(p_237301_.getColor(), p_237301_.getName());
   }));
   private static final Map<String, TextColor> NAMED_COLORS = LEGACY_FORMAT_TO_COLOR.values().stream().collect(ImmutableMap.toImmutableMap((p_237297_) -> {
      return p_237297_.name;
   }, Function.identity()));
   private final int value;
   @Nullable
   private final String name;

   private TextColor(int p_131263_, String p_131264_) {
      this.value = p_131263_;
      this.name = p_131264_;
   }

   private TextColor(int p_131261_) {
      this.value = p_131261_;
      this.name = null;
   }

   public int getValue() {
      return this.value;
   }

   public String serialize() {
      return this.name != null ? this.name : this.formatValue();
   }

   private String formatValue() {
      return String.format(Locale.ROOT, "#%06X", this.value);
   }

   public boolean equals(Object p_131279_) {
      if (this == p_131279_) {
         return true;
      } else if (p_131279_ != null && this.getClass() == p_131279_.getClass()) {
         TextColor textcolor = (TextColor)p_131279_;
         return this.value == textcolor.value;
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hash(this.value, this.name);
   }

   public String toString() {
      return this.name != null ? this.name : this.formatValue();
   }

   @Nullable
   public static TextColor fromLegacyFormat(ChatFormatting p_131271_) {
      return LEGACY_FORMAT_TO_COLOR.get(p_131271_);
   }

   public static TextColor fromRgb(int p_131267_) {
      return new TextColor(p_131267_);
   }

   @Nullable
   public static TextColor parseColor(String p_131269_) {
      if (p_131269_.startsWith("#")) {
         try {
            int i = Integer.parseInt(p_131269_.substring(1), 16);
            return fromRgb(i);
         } catch (NumberFormatException numberformatexception) {
            return null;
         }
      } else {
         return NAMED_COLORS.get(p_131269_);
      }
   }
}