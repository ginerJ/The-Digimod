package net.minecraft.data.models;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonElement;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.models.blockstates.BlockStateGenerator;
import net.minecraft.data.models.model.DelegatedModel;
import net.minecraft.data.models.model.ModelLocationUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class ModelProvider implements DataProvider {
   private final PackOutput.PathProvider blockStatePathProvider;
   private final PackOutput.PathProvider modelPathProvider;

   public ModelProvider(PackOutput p_252226_) {
      this.blockStatePathProvider = p_252226_.createPathProvider(PackOutput.Target.RESOURCE_PACK, "blockstates");
      this.modelPathProvider = p_252226_.createPathProvider(PackOutput.Target.RESOURCE_PACK, "models");
   }

   public CompletableFuture<?> run(CachedOutput p_253790_) {
      Map<Block, BlockStateGenerator> map = Maps.newHashMap();
      Consumer<BlockStateGenerator> consumer = (p_125120_) -> {
         Block block = p_125120_.getBlock();
         BlockStateGenerator blockstategenerator = map.put(block, p_125120_);
         if (blockstategenerator != null) {
            throw new IllegalStateException("Duplicate blockstate definition for " + block);
         }
      };
      Map<ResourceLocation, Supplier<JsonElement>> map1 = Maps.newHashMap();
      Set<Item> set = Sets.newHashSet();
      BiConsumer<ResourceLocation, Supplier<JsonElement>> biconsumer = (p_125123_, p_125124_) -> {
         Supplier<JsonElement> supplier = map1.put(p_125123_, p_125124_);
         if (supplier != null) {
            throw new IllegalStateException("Duplicate model definition for " + p_125123_);
         }
      };
      Consumer<Item> consumer1 = set::add;
      (new BlockModelGenerators(consumer, biconsumer, consumer1)).run();
      (new ItemModelGenerators(biconsumer)).run();
      List<Block> list = BuiltInRegistries.BLOCK.stream().filter((p_125117_) -> {
         return !map.containsKey(p_125117_);
      }).toList();
      if (!list.isEmpty()) {
         throw new IllegalStateException("Missing blockstate definitions for: " + list);
      } else {
         BuiltInRegistries.BLOCK.forEach((p_125128_) -> {
            Item item = Item.BY_BLOCK.get(p_125128_);
            if (item != null) {
               if (set.contains(item)) {
                  return;
               }

               ResourceLocation resourcelocation = ModelLocationUtils.getModelLocation(item);
               if (!map1.containsKey(resourcelocation)) {
                  map1.put(resourcelocation, new DelegatedModel(ModelLocationUtils.getModelLocation(p_125128_)));
               }
            }

         });
         return CompletableFuture.allOf(this.saveCollection(p_253790_, map, (p_248016_) -> {
            return this.blockStatePathProvider.json(p_248016_.builtInRegistryHolder().key().location());
         }), this.saveCollection(p_253790_, map1, this.modelPathProvider::json));
      }
   }

   private <T> CompletableFuture<?> saveCollection(CachedOutput p_254549_, Map<T, ? extends Supplier<JsonElement>> p_253779_, Function<T, Path> p_254013_) {
      return CompletableFuture.allOf(p_253779_.entrySet().stream().map((p_253408_) -> {
         Path path = p_254013_.apply(p_253408_.getKey());
         JsonElement jsonelement = p_253408_.getValue().get();
         return DataProvider.saveStable(p_254549_, jsonelement, path);
      }).toArray((p_253409_) -> {
         return new CompletableFuture[p_253409_];
      }));
   }

   public final String getName() {
      return "Model Definitions";
   }
}