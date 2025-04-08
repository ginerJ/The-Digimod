package net.minecraft.client.renderer.texture.atlas.sources;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.client.renderer.texture.atlas.SpriteSourceType;
import net.minecraft.client.renderer.texture.atlas.SpriteSources;
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection;
import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class Unstitcher implements SpriteSource {
   static final Logger LOGGER = LogUtils.getLogger();
   public static final Codec<Unstitcher> CODEC = RecordCodecBuilder.create((p_262047_) -> {
      return p_262047_.group(ResourceLocation.CODEC.fieldOf("resource").forGetter((p_261910_) -> {
         return p_261910_.resource;
      }), ExtraCodecs.nonEmptyList(Unstitcher.Region.CODEC.listOf()).fieldOf("regions").forGetter((p_261944_) -> {
         return p_261944_.regions;
      }), Codec.DOUBLE.optionalFieldOf("divisor_x", Double.valueOf(1.0D)).forGetter((p_261601_) -> {
         return p_261601_.xDivisor;
      }), Codec.DOUBLE.optionalFieldOf("divisor_y", Double.valueOf(1.0D)).forGetter((p_262039_) -> {
         return p_262039_.yDivisor;
      })).apply(p_262047_, Unstitcher::new);
   });
   private final ResourceLocation resource;
   private final List<Unstitcher.Region> regions;
   private final double xDivisor;
   private final double yDivisor;

   public Unstitcher(ResourceLocation p_261679_, List<Unstitcher.Region> p_261974_, double p_262181_, double p_261631_) {
      this.resource = p_261679_;
      this.regions = p_261974_;
      this.xDivisor = p_262181_;
      this.yDivisor = p_261631_;
   }

   public void run(ResourceManager p_261498_, SpriteSource.Output p_261828_) {
      ResourceLocation resourcelocation = TEXTURE_ID_CONVERTER.idToFile(this.resource);
      Optional<Resource> optional = p_261498_.getResource(resourcelocation);
      if (optional.isPresent()) {
         LazyLoadedImage lazyloadedimage = new LazyLoadedImage(resourcelocation, optional.get(), this.regions.size());

         for(Unstitcher.Region unstitcher$region : this.regions) {
            p_261828_.add(unstitcher$region.sprite, new Unstitcher.RegionInstance(lazyloadedimage, unstitcher$region, this.xDivisor, this.yDivisor));
         }
      } else {
         LOGGER.warn("Missing sprite: {}", (Object)resourcelocation);
      }

   }

   public SpriteSourceType type() {
      return SpriteSources.UNSTITCHER;
   }

   @OnlyIn(Dist.CLIENT)
   static record Region(ResourceLocation sprite, double x, double y, double width, double height) {
      public static final Codec<Unstitcher.Region> CODEC = RecordCodecBuilder.create((p_261521_) -> {
         return p_261521_.group(ResourceLocation.CODEC.fieldOf("sprite").forGetter(Unstitcher.Region::sprite), Codec.DOUBLE.fieldOf("x").forGetter(Unstitcher.Region::x), Codec.DOUBLE.fieldOf("y").forGetter(Unstitcher.Region::y), Codec.DOUBLE.fieldOf("width").forGetter(Unstitcher.Region::width), Codec.DOUBLE.fieldOf("height").forGetter(Unstitcher.Region::height)).apply(p_261521_, Unstitcher.Region::new);
      });
   }

   @OnlyIn(Dist.CLIENT)
   static class RegionInstance implements SpriteSource.SpriteSupplier {
      private final LazyLoadedImage image;
      private final Unstitcher.Region region;
      private final double xDivisor;
      private final double yDivisor;

      RegionInstance(LazyLoadedImage p_266678_, Unstitcher.Region p_267197_, double p_266911_, double p_266789_) {
         this.image = p_266678_;
         this.region = p_267197_;
         this.xDivisor = p_266911_;
         this.yDivisor = p_266789_;
      }

      public SpriteContents get() {
         try {
            NativeImage nativeimage = this.image.get();
            double d0 = (double)nativeimage.getWidth() / this.xDivisor;
            double d1 = (double)nativeimage.getHeight() / this.yDivisor;
            int i = Mth.floor(this.region.x * d0);
            int j = Mth.floor(this.region.y * d1);
            int k = Mth.floor(this.region.width * d0);
            int l = Mth.floor(this.region.height * d1);
            NativeImage nativeimage1 = new NativeImage(NativeImage.Format.RGBA, k, l, false);
            nativeimage.copyRect(nativeimage1, i, j, 0, 0, k, l, false, false);
            return new SpriteContents(this.region.sprite, new FrameSize(k, l), nativeimage1, AnimationMetadataSection.EMPTY);
         } catch (Exception exception) {
            Unstitcher.LOGGER.error("Failed to unstitch region {}", this.region.sprite, exception);
         } finally {
            this.image.release();
         }

         return MissingTextureAtlasSprite.create();
      }

      public void discard() {
         this.image.release();
      }
   }
}