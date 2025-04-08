package net.minecraft.network.chat.contents;

import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.Entity;

public class TranslatableContents implements ComponentContents {
   public static final Object[] NO_ARGS = new Object[0];
   private static final FormattedText TEXT_PERCENT = FormattedText.of("%");
   private static final FormattedText TEXT_NULL = FormattedText.of("null");
   private final String key;
   @Nullable
   private final String fallback;
   private final Object[] args;
   @Nullable
   private Language decomposedWith;
   private List<FormattedText> decomposedParts = ImmutableList.of();
   private static final Pattern FORMAT_PATTERN = Pattern.compile("%(?:(\\d+)\\$)?([A-Za-z%]|$)");

   public TranslatableContents(String p_265775_, @Nullable String p_265204_, Object[] p_265752_) {
      this.key = p_265775_;
      this.fallback = p_265204_;
      this.args = p_265752_;
   }

   private void decompose() {
      Language language = Language.getInstance();
      if (language != this.decomposedWith) {
         this.decomposedWith = language;
         String s = this.fallback != null ? language.getOrDefault(this.key, this.fallback) : language.getOrDefault(this.key);

         try {
            ImmutableList.Builder<FormattedText> builder = ImmutableList.builder();
            this.decomposeTemplate(s, builder::add);
            this.decomposedParts = builder.build();
         } catch (TranslatableFormatException translatableformatexception) {
            this.decomposedParts = ImmutableList.of(FormattedText.of(s));
         }

      }
   }

   private void decomposeTemplate(String p_237516_, Consumer<FormattedText> p_237517_) {
      Matcher matcher = FORMAT_PATTERN.matcher(p_237516_);

      try {
         int i = 0;

         int j;
         int l;
         for(j = 0; matcher.find(j); j = l) {
            int k = matcher.start();
            l = matcher.end();
            if (k > j) {
               String s = p_237516_.substring(j, k);
               if (s.indexOf(37) != -1) {
                  throw new IllegalArgumentException();
               }

               p_237517_.accept(FormattedText.of(s));
            }

            String s4 = matcher.group(2);
            String s1 = p_237516_.substring(k, l);
            if ("%".equals(s4) && "%%".equals(s1)) {
               p_237517_.accept(TEXT_PERCENT);
            } else {
               if (!"s".equals(s4)) {
                  throw new TranslatableFormatException(this, "Unsupported format: '" + s1 + "'");
               }

               String s2 = matcher.group(1);
               int i1 = s2 != null ? Integer.parseInt(s2) - 1 : i++;
               p_237517_.accept(this.getArgument(i1));
            }
         }

         if (j == 0) {
            // Forge has some special formatting handlers defined in ForgeI18n, use those if no %s replacements present.
            j = net.minecraftforge.internal.TextComponentMessageFormatHandler.handle(this, p_237517_, this.args, p_237516_);
         }

         if (j < p_237516_.length()) {
            String s3 = p_237516_.substring(j);
            if (s3.indexOf(37) != -1) {
               throw new IllegalArgumentException();
            }

            p_237517_.accept(FormattedText.of(s3));
         }

      } catch (IllegalArgumentException illegalargumentexception) {
         throw new TranslatableFormatException(this, illegalargumentexception);
      }
   }

   private FormattedText getArgument(int p_237510_) {
      if (p_237510_ >= 0 && p_237510_ < this.args.length) {
         Object object = this.args[p_237510_];
         if (object instanceof Component) {
            return (Component)object;
         } else {
            return object == null ? TEXT_NULL : FormattedText.of(object.toString());
         }
      } else {
         throw new TranslatableFormatException(this, p_237510_);
      }
   }

   public <T> Optional<T> visit(FormattedText.StyledContentConsumer<T> p_237521_, Style p_237522_) {
      this.decompose();

      for(FormattedText formattedtext : this.decomposedParts) {
         Optional<T> optional = formattedtext.visit(p_237521_, p_237522_);
         if (optional.isPresent()) {
            return optional;
         }
      }

      return Optional.empty();
   }

   public <T> Optional<T> visit(FormattedText.ContentConsumer<T> p_237519_) {
      this.decompose();

      for(FormattedText formattedtext : this.decomposedParts) {
         Optional<T> optional = formattedtext.visit(p_237519_);
         if (optional.isPresent()) {
            return optional;
         }
      }

      return Optional.empty();
   }

   public MutableComponent resolve(@Nullable CommandSourceStack p_237512_, @Nullable Entity p_237513_, int p_237514_) throws CommandSyntaxException {
      Object[] aobject = new Object[this.args.length];

      for(int i = 0; i < aobject.length; ++i) {
         Object object = this.args[i];
         if (object instanceof Component) {
            aobject[i] = ComponentUtils.updateForEntity(p_237512_, (Component)object, p_237513_, p_237514_);
         } else {
            aobject[i] = object;
         }
      }

      return MutableComponent.create(new TranslatableContents(this.key, this.fallback, aobject));
   }

   public boolean equals(Object p_237526_) {
      if (this == p_237526_) {
         return true;
      } else {
         if (p_237526_ instanceof TranslatableContents) {
            TranslatableContents translatablecontents = (TranslatableContents)p_237526_;
            if (Objects.equals(this.key, translatablecontents.key) && Objects.equals(this.fallback, translatablecontents.fallback) && Arrays.equals(this.args, translatablecontents.args)) {
               return true;
            }
         }

         return false;
      }
   }

   public int hashCode() {
      int i = Objects.hashCode(this.key);
      i = 31 * i + Objects.hashCode(this.fallback);
      return 31 * i + Arrays.hashCode(this.args);
   }

   public String toString() {
      return "translation{key='" + this.key + "'" + (this.fallback != null ? ", fallback='" + this.fallback + "'" : "") + ", args=" + Arrays.toString(this.args) + "}";
   }

   public String getKey() {
      return this.key;
   }

   @Nullable
   public String getFallback() {
      return this.fallback;
   }

   public Object[] getArgs() {
      return this.args;
   }
}
