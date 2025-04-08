package net.minecraft.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import net.minecraft.resources.ResourceLocation;

public class ResourceLocationPattern {
   public static final Codec<ResourceLocationPattern> CODEC = RecordCodecBuilder.create((p_261684_) -> {
      return p_261684_.group(ExtraCodecs.PATTERN.optionalFieldOf("namespace").forGetter((p_261529_) -> {
         return p_261529_.namespacePattern;
      }), ExtraCodecs.PATTERN.optionalFieldOf("path").forGetter((p_261660_) -> {
         return p_261660_.pathPattern;
      })).apply(p_261684_, ResourceLocationPattern::new);
   });
   private final Optional<Pattern> namespacePattern;
   private final Predicate<String> namespacePredicate;
   private final Optional<Pattern> pathPattern;
   private final Predicate<String> pathPredicate;
   private final Predicate<ResourceLocation> locationPredicate;

   private ResourceLocationPattern(Optional<Pattern> p_261800_, Optional<Pattern> p_262131_) {
      this.namespacePattern = p_261800_;
      this.namespacePredicate = p_261800_.map(Pattern::asPredicate).orElse((p_261999_) -> {
         return true;
      });
      this.pathPattern = p_262131_;
      this.pathPredicate = p_262131_.map(Pattern::asPredicate).orElse((p_261815_) -> {
         return true;
      });
      this.locationPredicate = (p_261854_) -> {
         return this.namespacePredicate.test(p_261854_.getNamespace()) && this.pathPredicate.test(p_261854_.getPath());
      };
   }

   public Predicate<String> namespacePredicate() {
      return this.namespacePredicate;
   }

   public Predicate<String> pathPredicate() {
      return this.pathPredicate;
   }

   public Predicate<ResourceLocation> locationPredicate() {
      return this.locationPredicate;
   }
}