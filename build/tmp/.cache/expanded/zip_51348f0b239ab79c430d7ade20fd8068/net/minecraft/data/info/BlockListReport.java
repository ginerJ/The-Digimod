package net.minecraft.data.info;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import net.minecraft.Util;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;

public class BlockListReport implements DataProvider {
   private final PackOutput output;

   public BlockListReport(PackOutput p_251533_) {
      this.output = p_251533_;
   }

   public CompletableFuture<?> run(CachedOutput p_236197_) {
      JsonObject jsonobject = new JsonObject();

      for(Block block : BuiltInRegistries.BLOCK) {
         ResourceLocation resourcelocation = BuiltInRegistries.BLOCK.getKey(block);
         JsonObject jsonobject1 = new JsonObject();
         StateDefinition<Block, BlockState> statedefinition = block.getStateDefinition();
         if (!statedefinition.getProperties().isEmpty()) {
            JsonObject jsonobject2 = new JsonObject();

            for(Property<?> property : statedefinition.getProperties()) {
               JsonArray jsonarray = new JsonArray();

               for(Comparable<?> comparable : property.getPossibleValues()) {
                  jsonarray.add(Util.getPropertyName(property, comparable));
               }

               jsonobject2.add(property.getName(), jsonarray);
            }

            jsonobject1.add("properties", jsonobject2);
         }

         JsonArray jsonarray1 = new JsonArray();

         for(BlockState blockstate : statedefinition.getPossibleStates()) {
            JsonObject jsonobject3 = new JsonObject();
            JsonObject jsonobject4 = new JsonObject();

            for(Property<?> property1 : statedefinition.getProperties()) {
               jsonobject4.addProperty(property1.getName(), Util.getPropertyName(property1, blockstate.getValue(property1)));
            }

            if (jsonobject4.size() > 0) {
               jsonobject3.add("properties", jsonobject4);
            }

            jsonobject3.addProperty("id", Block.getId(blockstate));
            if (blockstate == block.defaultBlockState()) {
               jsonobject3.addProperty("default", true);
            }

            jsonarray1.add(jsonobject3);
         }

         jsonobject1.add("states", jsonarray1);
         jsonobject.add(resourcelocation.toString(), jsonobject1);
      }

      Path path = this.output.getOutputFolder(PackOutput.Target.REPORTS).resolve("blocks.json");
      return DataProvider.saveStable(p_236197_, jsonobject, path);
   }

   public final String getName() {
      return "Block List";
   }
}