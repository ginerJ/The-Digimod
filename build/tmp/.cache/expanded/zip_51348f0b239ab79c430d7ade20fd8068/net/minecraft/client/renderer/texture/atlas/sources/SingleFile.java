package net.minecraft.client.renderer.texture.atlas.sources;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.client.renderer.texture.atlas.SpriteSourceType;
import net.minecraft.client.renderer.texture.atlas.SpriteSources;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class SingleFile implements SpriteSource {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final Codec<SingleFile> CODEC = RecordCodecBuilder.create((p_261903_) -> {
      return p_261903_.group(ResourceLocation.CODEC.fieldOf("resource").forGetter((p_261913_) -> {
         return p_261913_.resourceId;
      }), ResourceLocation.CODEC.optionalFieldOf("sprite").forGetter((p_261615_) -> {
         return p_261615_.spriteId;
      })).apply(p_261903_, SingleFile::new);
   });
   private final ResourceLocation resourceId;
   private final Optional<ResourceLocation> spriteId;

   public SingleFile(ResourceLocation p_261658_, Optional<ResourceLocation> p_261712_) {
      this.resourceId = p_261658_;
      this.spriteId = p_261712_;
   }

   public void run(ResourceManager p_261920_, SpriteSource.Output p_261578_) {
      ResourceLocation resourcelocation = TEXTURE_ID_CONVERTER.idToFile(this.resourceId);
      Optional<Resource> optional = p_261920_.getResource(resourcelocation);
      if (optional.isPresent()) {
         p_261578_.add(this.spriteId.orElse(this.resourceId), optional.get());
      } else {
         LOGGER.warn("Missing sprite: {}", (Object)resourcelocation);
      }

   }

   public SpriteSourceType type() {
      return SpriteSources.SINGLE_FILE;
   }
}