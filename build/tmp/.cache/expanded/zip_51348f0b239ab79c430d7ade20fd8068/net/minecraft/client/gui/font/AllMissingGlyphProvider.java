package net.minecraft.client.gui.font;

import com.mojang.blaze3d.font.GlyphInfo;
import com.mojang.blaze3d.font.GlyphProvider;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.ints.IntSets;
import javax.annotation.Nullable;
import net.minecraft.client.gui.font.glyphs.SpecialGlyphs;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class AllMissingGlyphProvider implements GlyphProvider {
   @Nullable
   public GlyphInfo getGlyph(int p_232553_) {
      return SpecialGlyphs.MISSING;
   }

   public IntSet getSupportedGlyphs() {
      return IntSets.EMPTY_SET;
   }
}