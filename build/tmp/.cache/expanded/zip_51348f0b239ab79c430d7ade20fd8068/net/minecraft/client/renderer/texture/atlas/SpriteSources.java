package net.minecraft.client.renderer.texture.atlas;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.List;
import net.minecraft.client.renderer.texture.atlas.sources.DirectoryLister;
import net.minecraft.client.renderer.texture.atlas.sources.PalettedPermutations;
import net.minecraft.client.renderer.texture.atlas.sources.SingleFile;
import net.minecraft.client.renderer.texture.atlas.sources.SourceFilter;
import net.minecraft.client.renderer.texture.atlas.sources.Unstitcher;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SpriteSources {
   private static final BiMap<ResourceLocation, SpriteSourceType> TYPES = HashBiMap.create();
   public static final SpriteSourceType SINGLE_FILE = register("single", SingleFile.CODEC);
   public static final SpriteSourceType DIRECTORY = register("directory", DirectoryLister.CODEC);
   public static final SpriteSourceType FILTER = register("filter", SourceFilter.CODEC);
   public static final SpriteSourceType UNSTITCHER = register("unstitch", Unstitcher.CODEC);
   public static final SpriteSourceType PALETTED_PERMUTATIONS = register("paletted_permutations", PalettedPermutations.CODEC);
   public static Codec<SpriteSourceType> TYPE_CODEC = ResourceLocation.CODEC.flatXmap((p_274717_) -> {
      SpriteSourceType spritesourcetype = TYPES.get(p_274717_);
      return spritesourcetype != null ? DataResult.success(spritesourcetype) : DataResult.error(() -> {
         return "Unknown type " + p_274717_;
      });
   }, (p_274716_) -> {
      ResourceLocation resourcelocation = TYPES.inverse().get(p_274716_);
      return p_274716_ != null ? DataResult.success(resourcelocation) : DataResult.error(() -> {
         return "Unknown type " + resourcelocation;
      });
   });
   public static Codec<SpriteSource> CODEC = TYPE_CODEC.dispatch(SpriteSource::type, SpriteSourceType::codec);
   public static Codec<List<SpriteSource>> FILE_CODEC = CODEC.listOf().fieldOf("sources").codec();

   private static SpriteSourceType register(String p_262175_, Codec<? extends SpriteSource> p_261464_) {
      SpriteSourceType spritesourcetype = new SpriteSourceType(p_261464_);
      ResourceLocation resourcelocation = new ResourceLocation(p_262175_);
      SpriteSourceType spritesourcetype1 = TYPES.putIfAbsent(resourcelocation, spritesourcetype);
      if (spritesourcetype1 != null) {
         throw new IllegalStateException("Duplicate registration " + resourcelocation);
      } else {
         return spritesourcetype;
      }
   }
}