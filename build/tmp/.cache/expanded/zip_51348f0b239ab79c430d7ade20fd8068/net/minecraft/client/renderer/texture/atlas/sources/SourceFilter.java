package net.minecraft.client.renderer.texture.atlas.sources;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.client.renderer.texture.atlas.SpriteSourceType;
import net.minecraft.client.renderer.texture.atlas.SpriteSources;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.ResourceLocationPattern;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SourceFilter implements SpriteSource {
   public static final Codec<SourceFilter> CODEC = RecordCodecBuilder.create((p_261830_) -> {
      return p_261830_.group(ResourceLocationPattern.CODEC.fieldOf("pattern").forGetter((p_262094_) -> {
         return p_262094_.filter;
      })).apply(p_261830_, SourceFilter::new);
   });
   private final ResourceLocationPattern filter;

   public SourceFilter(ResourceLocationPattern p_261654_) {
      this.filter = p_261654_;
   }

   public void run(ResourceManager p_261888_, SpriteSource.Output p_261864_) {
      p_261864_.removeAll(this.filter.locationPredicate());
   }

   public SpriteSourceType type() {
      return SpriteSources.FILTER;
   }
}