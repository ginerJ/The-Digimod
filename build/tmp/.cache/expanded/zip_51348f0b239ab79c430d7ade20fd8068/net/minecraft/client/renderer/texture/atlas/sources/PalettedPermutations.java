package net.minecraft.client.renderer.texture.atlas.sources;

import com.google.common.base.Suppliers;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.IntUnaryOperator;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.client.renderer.texture.atlas.SpriteSourceType;
import net.minecraft.client.renderer.texture.atlas.SpriteSources;
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection;
import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.FastColor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class PalettedPermutations implements SpriteSource {
   static final Logger LOGGER = LogUtils.getLogger();
   public static final Codec<PalettedPermutations> CODEC = RecordCodecBuilder.create((p_266838_) -> {
      return p_266838_.group(Codec.list(ResourceLocation.CODEC).fieldOf("textures").forGetter((p_267300_) -> {
         return p_267300_.textures;
      }), ResourceLocation.CODEC.fieldOf("palette_key").forGetter((p_266732_) -> {
         return p_266732_.paletteKey;
      }), Codec.unboundedMap(Codec.STRING, ResourceLocation.CODEC).fieldOf("permutations").forGetter((p_267234_) -> {
         return p_267234_.permutations;
      })).apply(p_266838_, PalettedPermutations::new);
   });
   private final List<ResourceLocation> textures;
   private final Map<String, ResourceLocation> permutations;
   private final ResourceLocation paletteKey;

   private PalettedPermutations(List<ResourceLocation> p_267282_, ResourceLocation p_266681_, Map<String, ResourceLocation> p_266741_) {
      this.textures = p_267282_;
      this.permutations = p_266741_;
      this.paletteKey = p_266681_;
   }

   public void run(ResourceManager p_267219_, SpriteSource.Output p_267250_) {
      Supplier<int[]> supplier = Suppliers.memoize(() -> {
         return loadPaletteEntryFromImage(p_267219_, this.paletteKey);
      });
      Map<String, Supplier<IntUnaryOperator>> map = new HashMap<>();
      this.permutations.forEach((p_267108_, p_266969_) -> {
         map.put(p_267108_, Suppliers.memoize(() -> {
            return createPaletteMapping(supplier.get(), loadPaletteEntryFromImage(p_267219_, p_266969_));
         }));
      });

      for(ResourceLocation resourcelocation : this.textures) {
         ResourceLocation resourcelocation1 = TEXTURE_ID_CONVERTER.idToFile(resourcelocation);
         Optional<Resource> optional = p_267219_.getResource(resourcelocation1);
         if (optional.isEmpty()) {
            LOGGER.warn("Unable to find texture {}", (Object)resourcelocation1);
         } else {
            LazyLoadedImage lazyloadedimage = new LazyLoadedImage(resourcelocation1, optional.get(), map.size());

            for(Map.Entry<String, Supplier<IntUnaryOperator>> entry : map.entrySet()) {
               ResourceLocation resourcelocation2 = resourcelocation.withSuffix("_" + (String)entry.getKey());
               p_267250_.add(resourcelocation2, new PalettedPermutations.PalettedSpriteSupplier(lazyloadedimage, entry.getValue(), resourcelocation2));
            }
         }
      }

   }

   private static IntUnaryOperator createPaletteMapping(int[] p_266839_, int[] p_266776_) {
      if (p_266776_.length != p_266839_.length) {
         LOGGER.warn("Palette mapping has different sizes: {} and {}", p_266839_.length, p_266776_.length);
         throw new IllegalArgumentException();
      } else {
         Int2IntMap int2intmap = new Int2IntOpenHashMap(p_266776_.length);

         for(int i = 0; i < p_266839_.length; ++i) {
            int j = p_266839_[i];
            if (FastColor.ABGR32.alpha(j) != 0) {
               int2intmap.put(FastColor.ABGR32.transparent(j), p_266776_[i]);
            }
         }

         return (p_267899_) -> {
            int k = FastColor.ABGR32.alpha(p_267899_);
            if (k == 0) {
               return p_267899_;
            } else {
               int l = FastColor.ABGR32.transparent(p_267899_);
               int i1 = int2intmap.getOrDefault(l, FastColor.ABGR32.opaque(l));
               int j1 = FastColor.ABGR32.alpha(i1);
               return FastColor.ABGR32.color(k * j1 / 255, i1);
            }
         };
      }
   }

   public static int[] loadPaletteEntryFromImage(ResourceManager p_267184_, ResourceLocation p_267059_) {
      Optional<Resource> optional = p_267184_.getResource(TEXTURE_ID_CONVERTER.idToFile(p_267059_));
      if (optional.isEmpty()) {
         LOGGER.error("Failed to load palette image {}", (Object)p_267059_);
         throw new IllegalArgumentException();
      } else {
         try (
            InputStream inputstream = optional.get().open();
            NativeImage nativeimage = NativeImage.read(inputstream);
         ) {
            return nativeimage.getPixelsRGBA();
         } catch (Exception exception) {
            LOGGER.error("Couldn't load texture {}", p_267059_, exception);
            throw new IllegalArgumentException();
         }
      }
   }

   public SpriteSourceType type() {
      return SpriteSources.PALETTED_PERMUTATIONS;
   }

   @OnlyIn(Dist.CLIENT)
   static record PalettedSpriteSupplier(LazyLoadedImage baseImage, Supplier<IntUnaryOperator> palette, ResourceLocation permutationLocation) implements SpriteSource.SpriteSupplier {
      @Nullable
      public SpriteContents get() {
         Object object;
         try {
            NativeImage nativeimage = this.baseImage.get().mappedCopy(this.palette.get());
            return new SpriteContents(this.permutationLocation, new FrameSize(nativeimage.getWidth(), nativeimage.getHeight()), nativeimage, AnimationMetadataSection.EMPTY);
         } catch (IllegalArgumentException | IOException ioexception) {
            PalettedPermutations.LOGGER.error("unable to apply palette to {}", this.permutationLocation, ioexception);
            object = null;
         } finally {
            this.baseImage.release();
         }

         return (SpriteContents)object;
      }

      public void discard() {
         this.baseImage.release();
      }
   }
}