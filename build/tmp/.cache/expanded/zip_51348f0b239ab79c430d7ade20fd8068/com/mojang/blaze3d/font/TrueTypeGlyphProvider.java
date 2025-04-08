package com.mojang.blaze3d.font;

import com.mojang.blaze3d.platform.NativeImage;
import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.function.Function;
import java.util.stream.IntStream;
import javax.annotation.Nullable;
import net.minecraft.client.gui.font.glyphs.BakedGlyph;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.stb.STBTruetype;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

@OnlyIn(Dist.CLIENT)
public class TrueTypeGlyphProvider implements GlyphProvider {
   @Nullable
   private ByteBuffer fontMemory;
   @Nullable
   private STBTTFontinfo font;
   final float oversample;
   private final IntSet skip = new IntArraySet();
   final float shiftX;
   final float shiftY;
   final float pointScale;
   final float ascent;

   public TrueTypeGlyphProvider(ByteBuffer p_83846_, STBTTFontinfo p_83847_, float p_83848_, float p_83849_, float p_83850_, float p_83851_, String p_83852_) {
      this.fontMemory = p_83846_;
      this.font = p_83847_;
      this.oversample = p_83849_;
      p_83852_.codePoints().forEach(this.skip::add);
      this.shiftX = p_83850_ * p_83849_;
      this.shiftY = p_83851_ * p_83849_;
      this.pointScale = STBTruetype.stbtt_ScaleForPixelHeight(p_83847_, p_83848_ * p_83849_);

      try (MemoryStack memorystack = MemoryStack.stackPush()) {
         IntBuffer intbuffer = memorystack.mallocInt(1);
         IntBuffer intbuffer1 = memorystack.mallocInt(1);
         IntBuffer intbuffer2 = memorystack.mallocInt(1);
         STBTruetype.stbtt_GetFontVMetrics(p_83847_, intbuffer, intbuffer1, intbuffer2);
         this.ascent = (float)intbuffer.get(0) * this.pointScale;
      }

   }

   @Nullable
   public GlyphInfo getGlyph(int p_231116_) {
      STBTTFontinfo stbttfontinfo = this.validateFontOpen();
      if (this.skip.contains(p_231116_)) {
         return null;
      } else {
         try (MemoryStack memorystack = MemoryStack.stackPush()) {
            int i = STBTruetype.stbtt_FindGlyphIndex(stbttfontinfo, p_231116_);
            if (i == 0) {
               return null;
            } else {
               IntBuffer intbuffer = memorystack.mallocInt(1);
               IntBuffer intbuffer1 = memorystack.mallocInt(1);
               IntBuffer intbuffer2 = memorystack.mallocInt(1);
               IntBuffer intbuffer3 = memorystack.mallocInt(1);
               IntBuffer intbuffer4 = memorystack.mallocInt(1);
               IntBuffer intbuffer5 = memorystack.mallocInt(1);
               STBTruetype.stbtt_GetGlyphHMetrics(stbttfontinfo, i, intbuffer4, intbuffer5);
               STBTruetype.stbtt_GetGlyphBitmapBoxSubpixel(stbttfontinfo, i, this.pointScale, this.pointScale, this.shiftX, this.shiftY, intbuffer, intbuffer1, intbuffer2, intbuffer3);
               float f = (float)intbuffer4.get(0) * this.pointScale;
               int j = intbuffer2.get(0) - intbuffer.get(0);
               int k = intbuffer3.get(0) - intbuffer1.get(0);
               return (GlyphInfo)(j > 0 && k > 0 ? new TrueTypeGlyphProvider.Glyph(intbuffer.get(0), intbuffer2.get(0), -intbuffer1.get(0), -intbuffer3.get(0), f, (float)intbuffer5.get(0) * this.pointScale, i) : (GlyphInfo.SpaceGlyphInfo)() -> {
                  return f / this.oversample;
               });
            }
         }
      }
   }

   STBTTFontinfo validateFontOpen() {
      if (this.fontMemory != null && this.font != null) {
         return this.font;
      } else {
         throw new IllegalArgumentException("Provider already closed");
      }
   }

   public void close() {
      if (this.font != null) {
         this.font.free();
         this.font = null;
      }

      MemoryUtil.memFree(this.fontMemory);
      this.fontMemory = null;
   }

   public IntSet getSupportedGlyphs() {
      return IntStream.range(0, 65535).filter((p_231118_) -> {
         return !this.skip.contains(p_231118_);
      }).collect(IntOpenHashSet::new, IntCollection::add, IntCollection::addAll);
   }

   @OnlyIn(Dist.CLIENT)
   class Glyph implements GlyphInfo {
      final int width;
      final int height;
      final float bearingX;
      final float bearingY;
      private final float advance;
      final int index;

      Glyph(int p_83882_, int p_83883_, int p_83884_, int p_83885_, float p_83886_, float p_83887_, int p_83888_) {
         this.width = p_83883_ - p_83882_;
         this.height = p_83884_ - p_83885_;
         this.advance = p_83886_ / TrueTypeGlyphProvider.this.oversample;
         this.bearingX = (p_83887_ + (float)p_83882_ + TrueTypeGlyphProvider.this.shiftX) / TrueTypeGlyphProvider.this.oversample;
         this.bearingY = (TrueTypeGlyphProvider.this.ascent - (float)p_83884_ + TrueTypeGlyphProvider.this.shiftY) / TrueTypeGlyphProvider.this.oversample;
         this.index = p_83888_;
      }

      public float getAdvance() {
         return this.advance;
      }

      public BakedGlyph bake(Function<SheetGlyphInfo, BakedGlyph> p_231120_) {
         return p_231120_.apply(new SheetGlyphInfo() {
            public int getPixelWidth() {
               return Glyph.this.width;
            }

            public int getPixelHeight() {
               return Glyph.this.height;
            }

            public float getOversample() {
               return TrueTypeGlyphProvider.this.oversample;
            }

            public float getBearingX() {
               return Glyph.this.bearingX;
            }

            public float getBearingY() {
               return Glyph.this.bearingY;
            }

            public void upload(int p_231126_, int p_231127_) {
               STBTTFontinfo stbttfontinfo = TrueTypeGlyphProvider.this.validateFontOpen();
               NativeImage nativeimage = new NativeImage(NativeImage.Format.LUMINANCE, Glyph.this.width, Glyph.this.height, false);
               nativeimage.copyFromFont(stbttfontinfo, Glyph.this.index, Glyph.this.width, Glyph.this.height, TrueTypeGlyphProvider.this.pointScale, TrueTypeGlyphProvider.this.pointScale, TrueTypeGlyphProvider.this.shiftX, TrueTypeGlyphProvider.this.shiftY, 0, 0);
               nativeimage.upload(0, p_231126_, p_231127_, 0, 0, Glyph.this.width, Glyph.this.height, false, true);
            }

            public boolean isColored() {
               return false;
            }
         });
      }
   }
}