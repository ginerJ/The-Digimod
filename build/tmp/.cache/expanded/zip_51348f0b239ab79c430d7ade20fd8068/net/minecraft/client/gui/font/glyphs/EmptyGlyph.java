package net.minecraft.client.gui.font.glyphs;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.gui.font.GlyphRenderTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix4f;

@OnlyIn(Dist.CLIENT)
public class EmptyGlyph extends BakedGlyph {
   public static final EmptyGlyph INSTANCE = new EmptyGlyph();

   public EmptyGlyph() {
      super(GlyphRenderTypes.createForColorTexture(new ResourceLocation("")), 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
   }

   public void render(boolean p_95278_, float p_95279_, float p_95280_, Matrix4f p_253794_, VertexConsumer p_95282_, float p_95283_, float p_95284_, float p_95285_, float p_95286_, int p_95287_) {
   }
}