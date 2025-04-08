package net.minecraft.client.renderer.texture;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.Util;
import net.minecraft.client.renderer.texture.atlas.SpriteResourceLoader;
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection;
import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class SpriteLoader {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final ResourceLocation location;
   private final int maxSupportedTextureSize;
   private final int minWidth;
   private final int minHeight;

   public SpriteLoader(ResourceLocation p_276126_, int p_276121_, int p_276110_, int p_276114_) {
      this.location = p_276126_;
      this.maxSupportedTextureSize = p_276121_;
      this.minWidth = p_276110_;
      this.minHeight = p_276114_;
   }

   public static SpriteLoader create(TextureAtlas p_249085_) {
      return new SpriteLoader(p_249085_.location(), p_249085_.maxSupportedTextureSize(), p_249085_.getWidth(), p_249085_.getHeight());
   }

   public SpriteLoader.Preparations stitch(List<SpriteContents> p_262029_, int p_261919_, Executor p_261665_) {
      int i = this.maxSupportedTextureSize;
      Stitcher<SpriteContents> stitcher = new Stitcher<>(i, i, p_261919_);
      int j = Integer.MAX_VALUE;
      int k = 1 << p_261919_;

      for(SpriteContents spritecontents : p_262029_) {
         j = Math.min(j, Math.min(spritecontents.width(), spritecontents.height()));
         int l = Math.min(Integer.lowestOneBit(spritecontents.width()), Integer.lowestOneBit(spritecontents.height()));
         if (l < k) {
            LOGGER.warn("Texture {} with size {}x{} limits mip level from {} to {}", spritecontents.name(), spritecontents.width(), spritecontents.height(), Mth.log2(k), Mth.log2(l));
            k = l;
         }

         stitcher.registerSprite(spritecontents);
      }

      int j1 = Math.min(j, k);
      int k1 = Mth.log2(j1);
      int l1;
      if (false) { // Forge: Do not lower the mipmap level
         LOGGER.warn("{}: dropping miplevel from {} to {}, because of minimum power of two: {}", this.location, p_261919_, k1, j1);
         l1 = k1;
      } else {
         l1 = p_261919_;
      }

      try {
         stitcher.stitch();
      } catch (StitcherException stitcherexception) {
         CrashReport crashreport = CrashReport.forThrowable(stitcherexception, "Stitching");
         CrashReportCategory crashreportcategory = crashreport.addCategory("Stitcher");
         crashreportcategory.setDetail("Sprites", stitcherexception.getAllSprites().stream().map((p_249576_) -> {
            return String.format(Locale.ROOT, "%s[%dx%d]", p_249576_.name(), p_249576_.width(), p_249576_.height());
         }).collect(Collectors.joining(",")));
         crashreportcategory.setDetail("Max Texture Size", i);
         throw new ReportedException(crashreport);
      }

      int i1 = Math.max(stitcher.getWidth(), this.minWidth);
      int i2 = Math.max(stitcher.getHeight(), this.minHeight);
      Map<ResourceLocation, TextureAtlasSprite> map = this.getStitchedSprites(stitcher, i1, i2);
      TextureAtlasSprite textureatlassprite = map.get(MissingTextureAtlasSprite.getLocation());
      CompletableFuture<Void> completablefuture;
      if (l1 > 0) {
         completablefuture = CompletableFuture.runAsync(() -> {
            map.values().forEach((p_251202_) -> {
               p_251202_.contents().increaseMipLevel(l1);
            });
         }, p_261665_);
      } else {
         completablefuture = CompletableFuture.completedFuture((Void)null);
      }

      return new SpriteLoader.Preparations(i1, i2, l1, textureatlassprite, map, completablefuture);
   }

   public static CompletableFuture<List<SpriteContents>> runSpriteSuppliers(List<Supplier<SpriteContents>> p_261516_, Executor p_261791_) {
      List<CompletableFuture<SpriteContents>> list = p_261516_.stream().map((p_261395_) -> {
         return CompletableFuture.supplyAsync(p_261395_, p_261791_);
      }).toList();
      return Util.sequence(list).thenApply((p_252234_) -> {
         return p_252234_.stream().filter(Objects::nonNull).toList();
      });
   }

   public CompletableFuture<SpriteLoader.Preparations> loadAndStitch(ResourceManager p_262108_, ResourceLocation p_261754_, int p_262104_, Executor p_261687_) {
      return CompletableFuture.supplyAsync(() -> {
         return SpriteResourceLoader.load(p_262108_, p_261754_).list(p_262108_);
      }, p_261687_).thenCompose((p_261390_) -> {
         return runSpriteSuppliers(p_261390_, p_261687_);
      }).thenApply((p_261393_) -> {
         return this.stitch(p_261393_, p_262104_, p_261687_);
      });
   }

   @Nullable
   public static SpriteContents loadSprite(ResourceLocation p_251630_, Resource p_250558_) {
      AnimationMetadataSection animationmetadatasection;
      try {
         animationmetadatasection = p_250558_.metadata().getSection(AnimationMetadataSection.SERIALIZER).orElse(AnimationMetadataSection.EMPTY);
      } catch (Exception exception) {
         LOGGER.error("Unable to parse metadata from {}", p_251630_, exception);
         return null;
      }

      NativeImage nativeimage;
      try (InputStream inputstream = p_250558_.open()) {
         nativeimage = NativeImage.read(inputstream);
      } catch (IOException ioexception) {
         LOGGER.error("Using missing texture, unable to load {}", p_251630_, ioexception);
         return null;
      }

      FrameSize framesize = animationmetadatasection.calculateFrameSize(nativeimage.getWidth(), nativeimage.getHeight());
      if (Mth.isMultipleOf(nativeimage.getWidth(), framesize.width()) && Mth.isMultipleOf(nativeimage.getHeight(), framesize.height())) {
         SpriteContents contents = net.minecraftforge.client.ForgeHooksClient.loadSpriteContents(p_251630_, p_250558_, framesize, nativeimage, animationmetadatasection);
         if (contents != null)
            return contents;
         return new SpriteContents(p_251630_, framesize, nativeimage, animationmetadatasection);
      } else {
         LOGGER.error("Image {} size {},{} is not multiple of frame size {},{}", p_251630_, nativeimage.getWidth(), nativeimage.getHeight(), framesize.width(), framesize.height());
         nativeimage.close();
         return null;
      }
   }

   private Map<ResourceLocation, TextureAtlasSprite> getStitchedSprites(Stitcher<SpriteContents> p_276117_, int p_276111_, int p_276112_) {
      Map<ResourceLocation, TextureAtlasSprite> map = new HashMap<>();
      p_276117_.gatherSprites((p_251421_, p_250533_, p_251913_) -> {
         TextureAtlasSprite sprite = net.minecraftforge.client.ForgeHooksClient.loadTextureAtlasSprite(this.location, p_251421_, p_276111_, p_276112_, p_250533_, p_251913_, p_251421_.byMipLevel.length - 1);
         if (sprite != null) {
            map.put(p_251421_.name(), sprite);
            return;
         }
         map.put(p_251421_.name(), new TextureAtlasSprite(this.location, p_251421_, p_276111_, p_276112_, p_250533_, p_251913_));
      });
      return map;
   }

   @OnlyIn(Dist.CLIENT)
   public static record Preparations(int width, int height, int mipLevel, TextureAtlasSprite missing, Map<ResourceLocation, TextureAtlasSprite> regions, CompletableFuture<Void> readyForUpload) {
      public CompletableFuture<SpriteLoader.Preparations> waitForUpload() {
         return this.readyForUpload.thenApply((p_249056_) -> {
            return this;
         });
      }
   }
}
