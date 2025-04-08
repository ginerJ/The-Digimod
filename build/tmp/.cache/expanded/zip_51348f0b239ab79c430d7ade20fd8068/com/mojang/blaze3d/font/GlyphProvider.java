package com.mojang.blaze3d.font;

import it.unimi.dsi.fastutil.ints.IntSet;
import javax.annotation.Nullable;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface GlyphProvider extends AutoCloseable {
   default void close() {
   }

   @Nullable
   default GlyphInfo getGlyph(int p_231091_) {
      return null;
   }

   IntSet getSupportedGlyphs();
}