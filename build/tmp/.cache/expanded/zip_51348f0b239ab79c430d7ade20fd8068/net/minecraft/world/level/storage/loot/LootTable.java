package net.minecraft.world.level.storage.loot;

import com.google.common.collect.Lists;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.functions.FunctionUserBuilder;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;

public class LootTable {
   static final Logger LOGGER = LogUtils.getLogger();
   public static final LootTable EMPTY = new LootTable(LootContextParamSets.EMPTY, (ResourceLocation)null, new LootPool[0], new LootItemFunction[0]);
   public static final LootContextParamSet DEFAULT_PARAM_SET = LootContextParamSets.ALL_PARAMS;
   final LootContextParamSet paramSet;
   @Nullable
   final ResourceLocation randomSequence;
   private final List<LootPool> pools;
   final LootItemFunction[] functions;
   private final BiFunction<ItemStack, LootContext, ItemStack> compositeFunction;

   LootTable(LootContextParamSet p_287716_, @Nullable ResourceLocation p_287737_, LootPool[] p_287700_, LootItemFunction[] p_287663_) {
      this.paramSet = p_287716_;
      this.randomSequence = p_287737_;
      this.pools = Lists.newArrayList(p_287700_);
      this.functions = p_287663_;
      this.compositeFunction = LootItemFunctions.compose(p_287663_);
   }

   public static Consumer<ItemStack> createStackSplitter(ServerLevel p_287765_, Consumer<ItemStack> p_251308_) {
      return (p_287570_) -> {
         if (p_287570_.isItemEnabled(p_287765_.enabledFeatures())) {
            if (p_287570_.getCount() < p_287570_.getMaxStackSize()) {
               p_251308_.accept(p_287570_);
            } else {
               int i = p_287570_.getCount();

               while(i > 0) {
                  ItemStack itemstack = p_287570_.copyWithCount(Math.min(p_287570_.getMaxStackSize(), i));
                  i -= itemstack.getCount();
                  p_251308_.accept(itemstack);
               }
            }

         }
      };
   }

   @Deprecated // Use a non-'Raw' version of 'getRandomItems', so that the Forge Global Loot Modifiers will be applied
   public void getRandomItemsRaw(LootParams p_287669_, Consumer<ItemStack> p_287781_) {
      this.getRandomItemsRaw((new LootContext.Builder(p_287669_)).create(this.randomSequence), p_287781_);
   }

   @Deprecated // Use a non-'Raw' version of 'getRandomItems', so that the Forge Global Loot Modifiers will be applied
   public void getRandomItemsRaw(LootContext p_79132_, Consumer<ItemStack> p_79133_) {
      LootContext.VisitedEntry<?> visitedentry = LootContext.createVisitedEntry(this);
      if (p_79132_.pushVisitedElement(visitedentry)) {
         Consumer<ItemStack> consumer = LootItemFunction.decorate(this.compositeFunction, p_79133_, p_79132_);

         for(LootPool lootpool : this.pools) {
            lootpool.addRandomItems(consumer, p_79132_);
         }

         p_79132_.popVisitedElement(visitedentry);
      } else {
         LOGGER.warn("Detected infinite loop in loot tables");
      }

   }

   public void getRandomItems(LootParams p_287748_, long p_287729_, Consumer<ItemStack> p_287583_) {
      this.getRandomItems((new LootContext.Builder(p_287748_)).withOptionalRandomSeed(p_287729_).create(this.randomSequence)).forEach(p_287583_);
   }

   public void getRandomItems(LootParams p_287704_, Consumer<ItemStack> p_287617_) {
      this.getRandomItems(p_287704_).forEach(p_287617_);
   }

   public void getRandomItems(LootContext p_79149_, Consumer<ItemStack> p_79150_) {
      this.getRandomItems(p_79149_).forEach(p_79150_);
   }

   public ObjectArrayList<ItemStack> getRandomItems(LootParams p_287574_, long p_287773_) {
      return this.getRandomItems((new LootContext.Builder(p_287574_)).withOptionalRandomSeed(p_287773_).create(this.randomSequence));
   }

   public ObjectArrayList<ItemStack> getRandomItems(LootParams p_287616_) {
      return this.getRandomItems((new LootContext.Builder(p_287616_)).create(this.randomSequence));
   }

   private ObjectArrayList<ItemStack> getRandomItems(LootContext p_230923_) {
      ObjectArrayList<ItemStack> objectarraylist = new ObjectArrayList<>();
      this.getRandomItemsRaw(p_230923_, createStackSplitter(p_230923_.getLevel(), objectarraylist::add));
      objectarraylist = net.minecraftforge.common.ForgeHooks.modifyLoot(this.getLootTableId(), objectarraylist, p_230923_);
      return objectarraylist;
   }

   public LootContextParamSet getParamSet() {
      return this.paramSet;
   }

   public void validate(ValidationContext p_79137_) {
      for(int i = 0; i < this.pools.size(); ++i) {
         this.pools.get(i).validate(p_79137_.forChild(".pools[" + i + "]"));
      }

      for(int j = 0; j < this.functions.length; ++j) {
         this.functions[j].validate(p_79137_.forChild(".functions[" + j + "]"));
      }

   }

   public void fill(Container p_287662_, LootParams p_287743_, long p_287585_) {
      LootContext lootcontext = (new LootContext.Builder(p_287743_)).withOptionalRandomSeed(p_287585_).create(this.randomSequence);
      ObjectArrayList<ItemStack> objectarraylist = this.getRandomItems(lootcontext);
      RandomSource randomsource = lootcontext.getRandom();
      List<Integer> list = this.getAvailableSlots(p_287662_, randomsource);
      this.shuffleAndSplitItems(objectarraylist, list.size(), randomsource);

      for(ItemStack itemstack : objectarraylist) {
         if (list.isEmpty()) {
            LOGGER.warn("Tried to over-fill a container");
            return;
         }

         if (itemstack.isEmpty()) {
            p_287662_.setItem(list.remove(list.size() - 1), ItemStack.EMPTY);
         } else {
            p_287662_.setItem(list.remove(list.size() - 1), itemstack);
         }
      }

   }

   private void shuffleAndSplitItems(ObjectArrayList<ItemStack> p_230925_, int p_230926_, RandomSource p_230927_) {
      List<ItemStack> list = Lists.newArrayList();
      Iterator<ItemStack> iterator = p_230925_.iterator();

      while(iterator.hasNext()) {
         ItemStack itemstack = iterator.next();
         if (itemstack.isEmpty()) {
            iterator.remove();
         } else if (itemstack.getCount() > 1) {
            list.add(itemstack);
            iterator.remove();
         }
      }

      while(p_230926_ - p_230925_.size() - list.size() > 0 && !list.isEmpty()) {
         ItemStack itemstack2 = list.remove(Mth.nextInt(p_230927_, 0, list.size() - 1));
         int i = Mth.nextInt(p_230927_, 1, itemstack2.getCount() / 2);
         ItemStack itemstack1 = itemstack2.split(i);
         if (itemstack2.getCount() > 1 && p_230927_.nextBoolean()) {
            list.add(itemstack2);
         } else {
            p_230925_.add(itemstack2);
         }

         if (itemstack1.getCount() > 1 && p_230927_.nextBoolean()) {
            list.add(itemstack1);
         } else {
            p_230925_.add(itemstack1);
         }
      }

      p_230925_.addAll(list);
      Util.shuffle(p_230925_, p_230927_);
   }

   private List<Integer> getAvailableSlots(Container p_230920_, RandomSource p_230921_) {
      ObjectArrayList<Integer> objectarraylist = new ObjectArrayList<>();

      for(int i = 0; i < p_230920_.getContainerSize(); ++i) {
         if (p_230920_.getItem(i).isEmpty()) {
            objectarraylist.add(i);
         }
      }

      Util.shuffle(objectarraylist, p_230921_);
      return objectarraylist;
   }

   public static LootTable.Builder lootTable() {
      return new LootTable.Builder();
   }

   //======================== FORGE START =============================================
   private boolean isFrozen = false;
   public void freeze() {
      this.isFrozen = true;
      this.pools.forEach(LootPool::freeze);
   }
   public boolean isFrozen(){ return this.isFrozen; }
   private void checkFrozen() {
      if (this.isFrozen())
         throw new RuntimeException("Attempted to modify LootTable after being finalized!");
   }

   private ResourceLocation lootTableId;
   public void setLootTableId(final ResourceLocation id) {
      if (this.lootTableId != null) throw new IllegalStateException("Attempted to rename loot table from '" + this.lootTableId + "' to '" + id + "': this is not supported");
      this.lootTableId = java.util.Objects.requireNonNull(id);
   }
   public ResourceLocation getLootTableId() { return this.lootTableId; }

   @org.jetbrains.annotations.Nullable
   public LootPool getPool(String name) {
      return pools.stream().filter(e -> name.equals(e.getName())).findFirst().orElse(null);
   }

   @org.jetbrains.annotations.Nullable
   public LootPool removePool(String name) {
      checkFrozen();
      for (LootPool pool : this.pools) {
         if (name.equals(pool.getName())) {
            this.pools.remove(pool);
            return pool;
         }
      }
      return null;
   }

   public void addPool(LootPool pool) {
      checkFrozen();
      if (pools.stream().anyMatch(e -> e == pool || e.getName() != null && e.getName().equals(pool.getName())))
         throw new RuntimeException("Attempted to add a duplicate pool to loot table: " + pool.getName());
      this.pools.add(pool);
   }
   //======================== FORGE END ===============================================

   public static class Builder implements FunctionUserBuilder<LootTable.Builder> {
      private final List<LootPool> pools = Lists.newArrayList();
      private final List<LootItemFunction> functions = Lists.newArrayList();
      private LootContextParamSet paramSet = LootTable.DEFAULT_PARAM_SET;
      @Nullable
      private ResourceLocation randomSequence = null;

      public LootTable.Builder withPool(LootPool.Builder p_79162_) {
         this.pools.add(p_79162_.build());
         return this;
      }

      public LootTable.Builder setParamSet(LootContextParamSet p_79166_) {
         this.paramSet = p_79166_;
         return this;
      }

      public LootTable.Builder setRandomSequence(ResourceLocation p_287667_) {
         this.randomSequence = p_287667_;
         return this;
      }

      public LootTable.Builder apply(LootItemFunction.Builder p_79164_) {
         this.functions.add(p_79164_.build());
         return this;
      }

      public LootTable.Builder unwrap() {
         return this;
      }

      public LootTable build() {
         return new LootTable(this.paramSet, this.randomSequence, this.pools.toArray(new LootPool[0]), this.functions.toArray(new LootItemFunction[0]));
      }
   }

   public static class Serializer implements JsonDeserializer<LootTable>, JsonSerializer<LootTable> {
      public LootTable deserialize(JsonElement p_79173_, Type p_79174_, JsonDeserializationContext p_79175_) throws JsonParseException {
         JsonObject jsonobject = GsonHelper.convertToJsonObject(p_79173_, "loot table");
         LootPool[] alootpool = GsonHelper.getAsObject(jsonobject, "pools", new LootPool[0], p_79175_, LootPool[].class);
         LootContextParamSet lootcontextparamset = null;
         if (jsonobject.has("type")) {
            String s = GsonHelper.getAsString(jsonobject, "type");
            lootcontextparamset = LootContextParamSets.get(new ResourceLocation(s));
         }

         ResourceLocation resourcelocation;
         if (jsonobject.has("random_sequence")) {
            String s1 = GsonHelper.getAsString(jsonobject, "random_sequence");
            resourcelocation = new ResourceLocation(s1);
         } else {
            resourcelocation = null;
         }

         LootItemFunction[] alootitemfunction = GsonHelper.getAsObject(jsonobject, "functions", new LootItemFunction[0], p_79175_, LootItemFunction[].class);
         return new LootTable(lootcontextparamset != null ? lootcontextparamset : LootContextParamSets.ALL_PARAMS, resourcelocation, alootpool, alootitemfunction);
      }

      public JsonElement serialize(LootTable p_79177_, Type p_79178_, JsonSerializationContext p_79179_) {
         JsonObject jsonobject = new JsonObject();
         if (p_79177_.paramSet != LootTable.DEFAULT_PARAM_SET) {
            ResourceLocation resourcelocation = LootContextParamSets.getKey(p_79177_.paramSet);
            if (resourcelocation != null) {
               jsonobject.addProperty("type", resourcelocation.toString());
            } else {
               LootTable.LOGGER.warn("Failed to find id for param set {}", (Object)p_79177_.paramSet);
            }
         }

         if (p_79177_.randomSequence != null) {
            jsonobject.addProperty("random_sequence", p_79177_.randomSequence.toString());
         }

         if (!p_79177_.pools.isEmpty()) {
            jsonobject.add("pools", p_79179_.serialize(p_79177_.pools));
         }

         if (!ArrayUtils.isEmpty((Object[])p_79177_.functions)) {
            jsonobject.add("functions", p_79179_.serialize(p_79177_.functions));
         }

         return jsonobject;
      }
   }
}
