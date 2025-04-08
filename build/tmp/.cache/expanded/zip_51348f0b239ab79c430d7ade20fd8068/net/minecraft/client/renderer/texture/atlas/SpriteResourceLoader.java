package net.minecraft.client.renderer.texture.atlas;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Supplier;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class SpriteResourceLoader {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final FileToIdConverter ATLAS_INFO_CONVERTER = new FileToIdConverter("atlases", ".json");
   private final List<SpriteSource> sources;

   private SpriteResourceLoader(List<SpriteSource> p_261613_) {
      this.sources = p_261613_;
   }

   public List<Supplier<SpriteContents>> list(ResourceManager p_261989_) {
      final Map<ResourceLocation, SpriteSource.SpriteSupplier> map = new HashMap<>();
      SpriteSource.Output spritesource$output = new SpriteSource.Output() {
         public void add(ResourceLocation p_262067_, SpriteSource.SpriteSupplier p_261936_) {
            SpriteSource.SpriteSupplier spritesource$spritesupplier = map.put(p_262067_, p_261936_);
            if (spritesource$spritesupplier != null) {
               spritesource$spritesupplier.discard();
            }

         }

         public void removeAll(Predicate<ResourceLocation> p_261939_) {
            Iterator<Map.Entry<ResourceLocation, SpriteSource.SpriteSupplier>> iterator = map.entrySet().iterator();

            while(iterator.hasNext()) {
               Map.Entry<ResourceLocation, SpriteSource.SpriteSupplier> entry = iterator.next();
               if (p_261939_.test(entry.getKey())) {
                  entry.getValue().discard();
                  iterator.remove();
               }
            }

         }
      };
      this.sources.forEach((p_261747_) -> {
         p_261747_.run(p_261989_, spritesource$output);
      });
      ImmutableList.Builder<Supplier<SpriteContents>> builder = ImmutableList.builder();
      builder.add(MissingTextureAtlasSprite::create);
      builder.addAll(map.values());
      return builder.build();
   }

   public static SpriteResourceLoader load(ResourceManager p_261551_, ResourceLocation p_261709_) {
      ResourceLocation resourcelocation = ATLAS_INFO_CONVERTER.idToFile(p_261709_);
      List<SpriteSource> list = new ArrayList<>();

      for(Resource resource : p_261551_.getResourceStack(resourcelocation)) {
         try (BufferedReader bufferedreader = resource.openAsReader()) {
            Dynamic<JsonElement> dynamic = new Dynamic<>(JsonOps.INSTANCE, JsonParser.parseReader(bufferedreader));
            list.addAll(SpriteSources.FILE_CODEC.parse(dynamic).getOrThrow(false, LOGGER::error));
         } catch (Exception exception) {
            LOGGER.warn("Failed to parse atlas definition {} in pack {}", resourcelocation, resource.sourcePackId(), exception);
         }
      }

      return new SpriteResourceLoader(list);
   }
}