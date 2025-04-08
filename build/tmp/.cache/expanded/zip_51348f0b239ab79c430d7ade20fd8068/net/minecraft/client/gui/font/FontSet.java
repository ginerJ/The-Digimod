package net.minecraft.client.gui.font;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.font.GlyphInfo;
import com.mojang.blaze3d.font.GlyphProvider;
import com.mojang.blaze3d.font.SheetGlyphInfo;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.util.List;
import java.util.Set;
import net.minecraft.client.gui.font.glyphs.BakedGlyph;
import net.minecraft.client.gui.font.glyphs.SpecialGlyphs;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FontSet implements AutoCloseable {
   private static final RandomSource RANDOM = RandomSource.create();
   private static final float LARGE_FORWARD_ADVANCE = 32.0F;
   private final TextureManager textureManager;
   private final ResourceLocation name;
   private BakedGlyph missingGlyph;
   private BakedGlyph whiteGlyph;
   private final List<GlyphProvider> providers = Lists.newArrayList();
   private final CodepointMap<BakedGlyph> glyphs = new CodepointMap<>((p_284630_) -> {
      return new BakedGlyph[p_284630_];
   }, (p_284629_) -> {
      return new BakedGlyph[p_284629_][];
   });
   private final CodepointMap<FontSet.GlyphInfoFilter> glyphInfos = new CodepointMap<>((p_284631_) -> {
      return new FontSet.GlyphInfoFilter[p_284631_];
   }, (p_284632_) -> {
      return new FontSet.GlyphInfoFilter[p_284632_][];
   });
   private final Int2ObjectMap<IntList> glyphsByWidth = new Int2ObjectOpenHashMap<>();
   private final List<FontTexture> textures = Lists.newArrayList();

   public FontSet(TextureManager p_95062_, ResourceLocation p_95063_) {
      this.textureManager = p_95062_;
      this.name = p_95063_;
   }

   public void reload(List<GlyphProvider> p_95072_) {
      this.closeProviders();
      this.closeTextures();
      this.glyphs.clear();
      this.glyphInfos.clear();
      this.glyphsByWidth.clear();
      this.missingGlyph = SpecialGlyphs.MISSING.bake(this::stitch);
      this.whiteGlyph = SpecialGlyphs.WHITE.bake(this::stitch);
      IntSet intset = new IntOpenHashSet();

      for(GlyphProvider glyphprovider : p_95072_) {
         intset.addAll(glyphprovider.getSupportedGlyphs());
      }

      Set<GlyphProvider> set = Sets.newHashSet();
      intset.forEach((int p_232561_) -> {
         for(GlyphProvider glyphprovider1 : p_95072_) {
            GlyphInfo glyphinfo = glyphprovider1.getGlyph(p_232561_);
            if (glyphinfo != null) {
               set.add(glyphprovider1);
               if (glyphinfo != SpecialGlyphs.MISSING) {
                  this.glyphsByWidth.computeIfAbsent(Mth.ceil(glyphinfo.getAdvance(false)), (p_232567_) -> {
                     return new IntArrayList();
                  }).add(p_232561_);
               }
               break;
            }
         }

      });
      p_95072_.stream().filter(set::contains).forEach(this.providers::add);
   }

   public void close() {
      this.closeProviders();
      this.closeTextures();
   }

   private void closeProviders() {
      for(GlyphProvider glyphprovider : this.providers) {
         glyphprovider.close();
      }

      this.providers.clear();
   }

   private void closeTextures() {
      for(FontTexture fonttexture : this.textures) {
         fonttexture.close();
      }

      this.textures.clear();
   }

   private static boolean hasFishyAdvance(GlyphInfo p_243323_) {
      float f = p_243323_.getAdvance(false);
      if (!(f < 0.0F) && !(f > 32.0F)) {
         float f1 = p_243323_.getAdvance(true);
         return f1 < 0.0F || f1 > 32.0F;
      } else {
         return true;
      }
   }

   private FontSet.GlyphInfoFilter computeGlyphInfo(int p_243321_) {
      GlyphInfo glyphinfo = null;

      for(GlyphProvider glyphprovider : this.providers) {
         GlyphInfo glyphinfo1 = glyphprovider.getGlyph(p_243321_);
         if (glyphinfo1 != null) {
            if (glyphinfo == null) {
               glyphinfo = glyphinfo1;
            }

            if (!hasFishyAdvance(glyphinfo1)) {
               return new FontSet.GlyphInfoFilter(glyphinfo, glyphinfo1);
            }
         }
      }

      return glyphinfo != null ? new FontSet.GlyphInfoFilter(glyphinfo, SpecialGlyphs.MISSING) : FontSet.GlyphInfoFilter.MISSING;
   }

   public GlyphInfo getGlyphInfo(int p_243235_, boolean p_243251_) {
      return this.glyphInfos.computeIfAbsent(p_243235_, this::computeGlyphInfo).select(p_243251_);
   }

   private BakedGlyph computeBakedGlyph(int p_232565_) {
      for(GlyphProvider glyphprovider : this.providers) {
         GlyphInfo glyphinfo = glyphprovider.getGlyph(p_232565_);
         if (glyphinfo != null) {
            return glyphinfo.bake(this::stitch);
         }
      }

      return this.missingGlyph;
   }

   public BakedGlyph getGlyph(int p_95079_) {
      return this.glyphs.computeIfAbsent(p_95079_, this::computeBakedGlyph);
   }

   private BakedGlyph stitch(SheetGlyphInfo p_232557_) {
      for(FontTexture fonttexture : this.textures) {
         BakedGlyph bakedglyph = fonttexture.add(p_232557_);
         if (bakedglyph != null) {
            return bakedglyph;
         }
      }

      ResourceLocation resourcelocation = this.name.withSuffix("/" + this.textures.size());
      boolean flag = p_232557_.isColored();
      GlyphRenderTypes glyphrendertypes = flag ? GlyphRenderTypes.createForColorTexture(resourcelocation) : GlyphRenderTypes.createForIntensityTexture(resourcelocation);
      FontTexture fonttexture1 = new FontTexture(glyphrendertypes, flag);
      this.textures.add(fonttexture1);
      this.textureManager.register(resourcelocation, fonttexture1);
      BakedGlyph bakedglyph1 = fonttexture1.add(p_232557_);
      return bakedglyph1 == null ? this.missingGlyph : bakedglyph1;
   }

   public BakedGlyph getRandomGlyph(GlyphInfo p_95068_) {
      IntList intlist = this.glyphsByWidth.get(Mth.ceil(p_95068_.getAdvance(false)));
      return intlist != null && !intlist.isEmpty() ? this.getGlyph(intlist.getInt(RANDOM.nextInt(intlist.size()))) : this.missingGlyph;
   }

   public BakedGlyph whiteGlyph() {
      return this.whiteGlyph;
   }

   @OnlyIn(Dist.CLIENT)
   static record GlyphInfoFilter(GlyphInfo glyphInfo, GlyphInfo glyphInfoNotFishy) {
      static final FontSet.GlyphInfoFilter MISSING = new FontSet.GlyphInfoFilter(SpecialGlyphs.MISSING, SpecialGlyphs.MISSING);

      GlyphInfo select(boolean p_243218_) {
         return p_243218_ ? this.glyphInfoNotFishy : this.glyphInfo;
      }
   }
}