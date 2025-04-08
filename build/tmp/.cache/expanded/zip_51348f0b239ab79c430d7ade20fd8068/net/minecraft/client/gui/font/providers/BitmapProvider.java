package net.minecraft.client.gui.font.providers;

import com.mojang.blaze3d.font.GlyphInfo;
import com.mojang.blaze3d.font.GlyphProvider;
import com.mojang.blaze3d.font.SheetGlyphInfo;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.datafixers.util.Either;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.ints.IntSets;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.client.gui.font.CodepointMap;
import net.minecraft.client.gui.font.glyphs.BakedGlyph;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.ExtraCodecs;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class BitmapProvider implements GlyphProvider {
   static final Logger LOGGER = LogUtils.getLogger();
   private final NativeImage image;
   private final CodepointMap<BitmapProvider.Glyph> glyphs;

   BitmapProvider(NativeImage p_285380_, CodepointMap<BitmapProvider.Glyph> p_285445_) {
      this.image = p_285380_;
      this.glyphs = p_285445_;
   }

   public void close() {
      this.image.close();
   }

   @Nullable
   public GlyphInfo getGlyph(int p_232638_) {
      return this.glyphs.get(p_232638_);
   }

   public IntSet getSupportedGlyphs() {
      return IntSets.unmodifiable(this.glyphs.keySet());
   }

   @OnlyIn(Dist.CLIENT)
   public static record Definition(ResourceLocation file, int height, int ascent, int[][] codepointGrid) implements GlyphProviderDefinition {
      private static final Codec<int[][]> CODEPOINT_GRID_CODEC = ExtraCodecs.validate(Codec.STRING.listOf().xmap((p_286900_) -> {
         int i = p_286900_.size();
         int[][] aint = new int[i][];

         for(int j = 0; j < i; ++j) {
            aint[j] = p_286900_.get(j).codePoints().toArray();
         }

         return aint;
      }, (p_286828_) -> {
         List<String> list = new ArrayList<>(p_286828_.length);

         for(int[] aint : p_286828_) {
            list.add(new String(aint, 0, aint.length));
         }

         return list;
      }), BitmapProvider.Definition::validateDimensions);
      public static final MapCodec<BitmapProvider.Definition> CODEC = ExtraCodecs.validate(RecordCodecBuilder.mapCodec((p_286905_) -> {
         return p_286905_.group(ResourceLocation.CODEC.fieldOf("file").forGetter(BitmapProvider.Definition::file), Codec.INT.optionalFieldOf("height", Integer.valueOf(8)).forGetter(BitmapProvider.Definition::height), Codec.INT.fieldOf("ascent").forGetter(BitmapProvider.Definition::ascent), CODEPOINT_GRID_CODEC.fieldOf("chars").forGetter(BitmapProvider.Definition::codepointGrid)).apply(p_286905_, BitmapProvider.Definition::new);
      }), BitmapProvider.Definition::validate);

      private static DataResult<int[][]> validateDimensions(int[][] p_286348_) {
         int i = p_286348_.length;
         if (i == 0) {
            return DataResult.error(() -> {
               return "Expected to find data in codepoint grid";
            });
         } else {
            int[] aint = p_286348_[0];
            int j = aint.length;
            if (j == 0) {
               return DataResult.error(() -> {
                  return "Expected to find data in codepoint grid";
               });
            } else {
               for(int k = 1; k < i; ++k) {
                  int[] aint1 = p_286348_[k];
                  if (aint1.length != j) {
                     return DataResult.error(() -> {
                        return "Lines in codepoint grid have to be the same length (found: " + aint1.length + " codepoints, expected: " + j + "), pad with \\u0000";
                     });
                  }
               }

               return DataResult.success(p_286348_);
            }
         }
      }

      private static DataResult<BitmapProvider.Definition> validate(BitmapProvider.Definition p_286662_) {
         return p_286662_.ascent > p_286662_.height ? DataResult.error(() -> {
            return "Ascent " + p_286662_.ascent + " higher than height " + p_286662_.height;
         }) : DataResult.success(p_286662_);
      }

      public GlyphProviderType type() {
         return GlyphProviderType.BITMAP;
      }

      public Either<GlyphProviderDefinition.Loader, GlyphProviderDefinition.Reference> unpack() {
         return Either.left(this::load);
      }

      private GlyphProvider load(ResourceManager p_286694_) throws IOException {
         ResourceLocation resourcelocation = this.file.withPrefix("textures/");

         try (InputStream inputstream = p_286694_.open(resourcelocation)) {
            NativeImage nativeimage = NativeImage.read(NativeImage.Format.RGBA, inputstream);
            int i = nativeimage.getWidth();
            int j = nativeimage.getHeight();
            int k = i / this.codepointGrid[0].length;
            int l = j / this.codepointGrid.length;
            float f = (float)this.height / (float)l;
            CodepointMap<BitmapProvider.Glyph> codepointmap = new CodepointMap<>((p_286343_) -> {
               return new BitmapProvider.Glyph[p_286343_];
            }, (p_286759_) -> {
               return new BitmapProvider.Glyph[p_286759_][];
            });

            for(int i1 = 0; i1 < this.codepointGrid.length; ++i1) {
               int j1 = 0;

               for(int k1 : this.codepointGrid[i1]) {
                  int l1 = j1++;
                  if (k1 != 0) {
                     int i2 = this.getActualGlyphWidth(nativeimage, k, l, l1, i1);
                     BitmapProvider.Glyph bitmapprovider$glyph = codepointmap.put(k1, new BitmapProvider.Glyph(f, nativeimage, l1 * k, i1 * l, k, l, (int)(0.5D + (double)((float)i2 * f)) + 1, this.ascent));
                     if (bitmapprovider$glyph != null) {
                        BitmapProvider.LOGGER.warn("Codepoint '{}' declared multiple times in {}", Integer.toHexString(k1), resourcelocation);
                     }
                  }
               }
            }

            return new BitmapProvider(nativeimage, codepointmap);
         }
      }

      private int getActualGlyphWidth(NativeImage p_286449_, int p_286656_, int p_286554_, int p_286657_, int p_286307_) {
         int i;
         for(i = p_286656_ - 1; i >= 0; --i) {
            int j = p_286657_ * p_286656_ + i;

            for(int k = 0; k < p_286554_; ++k) {
               int l = p_286307_ * p_286554_ + k;
               if (p_286449_.getLuminanceOrAlpha(j, l) != 0) {
                  return i + 1;
               }
            }
         }

         return i + 1;
      }
   }

   @OnlyIn(Dist.CLIENT)
   static record Glyph(float scale, NativeImage image, int offsetX, int offsetY, int width, int height, int advance, int ascent) implements GlyphInfo {
      public float getAdvance() {
         return (float)this.advance;
      }

      public BakedGlyph bake(Function<SheetGlyphInfo, BakedGlyph> p_232640_) {
         return p_232640_.apply(new SheetGlyphInfo() {
            public float getOversample() {
               return 1.0F / Glyph.this.scale;
            }

            public int getPixelWidth() {
               return Glyph.this.width;
            }

            public int getPixelHeight() {
               return Glyph.this.height;
            }

            public float getBearingY() {
               return SheetGlyphInfo.super.getBearingY() + 7.0F - (float)Glyph.this.ascent;
            }

            public void upload(int p_232658_, int p_232659_) {
               Glyph.this.image.upload(0, p_232658_, p_232659_, Glyph.this.offsetX, Glyph.this.offsetY, Glyph.this.width, Glyph.this.height, false, false);
            }

            public boolean isColored() {
               return Glyph.this.image.format().components() > 1;
            }
         });
      }
   }
}