package com.mojang.blaze3d.font;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.ints.IntSets;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.client.gui.font.providers.GlyphProviderDefinition;
import net.minecraft.client.gui.font.providers.GlyphProviderType;
import net.minecraft.util.ExtraCodecs;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SpaceProvider implements GlyphProvider {
   private final Int2ObjectMap<GlyphInfo.SpaceGlyphInfo> glyphs;

   public SpaceProvider(Map<Integer, Float> p_286456_) {
      this.glyphs = new Int2ObjectOpenHashMap<>(p_286456_.size());
      p_286456_.forEach((p_286113_, p_286114_) -> {
         this.glyphs.put(p_286113_.intValue(), () -> {
            return p_286114_;
         });
      });
   }

   @Nullable
   public GlyphInfo getGlyph(int p_231105_) {
      return this.glyphs.get(p_231105_);
   }

   public IntSet getSupportedGlyphs() {
      return IntSets.unmodifiable(this.glyphs.keySet());
   }

   @OnlyIn(Dist.CLIENT)
   public static record Definition(Map<Integer, Float> advances) implements GlyphProviderDefinition {
      public static final MapCodec<SpaceProvider.Definition> CODEC = RecordCodecBuilder.mapCodec((p_286766_) -> {
         return p_286766_.group(Codec.unboundedMap(ExtraCodecs.CODEPOINT, Codec.FLOAT).fieldOf("advances").forGetter(SpaceProvider.Definition::advances)).apply(p_286766_, SpaceProvider.Definition::new);
      });

      public GlyphProviderType type() {
         return GlyphProviderType.SPACE;
      }

      public Either<GlyphProviderDefinition.Loader, GlyphProviderDefinition.Reference> unpack() {
         GlyphProviderDefinition.Loader glyphproviderdefinition$loader = (p_286243_) -> {
            return new SpaceProvider(this.advances);
         };
         return Either.left(glyphproviderdefinition$loader);
      }
   }
}