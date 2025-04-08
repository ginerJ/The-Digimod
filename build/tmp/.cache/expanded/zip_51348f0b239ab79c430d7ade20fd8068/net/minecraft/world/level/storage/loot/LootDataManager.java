package net.minecraft.world.level.storage.loot;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.JsonElement;
import com.mojang.logging.LogUtils;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditions;
import org.slf4j.Logger;

public class LootDataManager implements PreparableReloadListener, LootDataResolver {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final LootDataId<LootTable> EMPTY_LOOT_TABLE_KEY = new LootDataId<>(LootDataType.TABLE, BuiltInLootTables.EMPTY);
   private Map<LootDataId<?>, ?> elements = Map.of();
   private Multimap<LootDataType<?>, ResourceLocation> typeKeys = ImmutableMultimap.of();

   public final CompletableFuture<Void> reload(PreparableReloadListener.PreparationBarrier p_279240_, ResourceManager p_279377_, ProfilerFiller p_279135_, ProfilerFiller p_279088_, Executor p_279148_, Executor p_279169_) {
      Map<LootDataType<?>, Map<ResourceLocation, ?>> map = new HashMap<>();
      CompletableFuture<?>[] completablefuture = LootDataType.values().map((p_279242_) -> {
         return scheduleElementParse(p_279242_, p_279377_, p_279148_, map);
      }).toArray((p_279126_) -> {
         return new CompletableFuture[p_279126_];
      });
      return CompletableFuture.allOf(completablefuture).thenCompose(p_279240_::wait).thenAcceptAsync((p_279096_) -> {
         this.apply(map);
      }, p_279169_);
   }

   private static <T> CompletableFuture<?> scheduleElementParse(LootDataType<T> p_279205_, ResourceManager p_279441_, Executor p_279233_, Map<LootDataType<?>, Map<ResourceLocation, ?>> p_279241_) {
      Map<ResourceLocation, T> map = new HashMap<>();
      p_279241_.put(p_279205_, map);
      return CompletableFuture.runAsync(() -> {
         Map<ResourceLocation, JsonElement> map1 = new HashMap<>();
         SimpleJsonResourceReloadListener.scanDirectory(p_279441_, p_279205_.directory(), p_279205_.parser(), map1);
         map1.forEach((p_279416_, p_279151_) -> {
            p_279205_.deserialize(p_279416_, p_279151_, p_279441_).ifPresent((p_279295_) -> {
               map.put(p_279416_, p_279295_);
            });
         });
      }, p_279233_);
   }

   private void apply(Map<LootDataType<?>, Map<ResourceLocation, ?>> p_279426_) {
      Object object = p_279426_.get(LootDataType.TABLE).remove(BuiltInLootTables.EMPTY);
      if (object != null) {
         LOGGER.warn("Datapack tried to redefine {} loot table, ignoring", (Object)BuiltInLootTables.EMPTY);
      }

      ImmutableMap.Builder<LootDataId<?>, Object> builder = ImmutableMap.builder();
      ImmutableMultimap.Builder<LootDataType<?>, ResourceLocation> builder1 = ImmutableMultimap.builder();
      p_279426_.forEach((p_279449_, p_279262_) -> {
         p_279262_.forEach((p_279130_, p_279313_) -> {
            builder.put(new LootDataId(p_279449_, p_279130_), p_279313_);
            builder1.put(p_279449_, p_279130_);
         });
      });
      builder.put(EMPTY_LOOT_TABLE_KEY, LootTable.EMPTY);
      final Map<LootDataId<?>, ?> map = builder.build();
      ValidationContext validationcontext = new ValidationContext(LootContextParamSets.ALL_PARAMS, new LootDataResolver() {
         @Nullable
         public <T> T getElement(LootDataId<T> p_279194_) {
            return (T)map.get(p_279194_);
         }
      });
      map.forEach((p_279387_, p_279087_) -> {
         castAndValidate(validationcontext, p_279387_, p_279087_);
      });
      validationcontext.getProblems().forEach((p_279487_, p_279312_) -> {
         LOGGER.warn("Found loot table element validation problem in {}: {}", p_279487_, p_279312_);
      });
      this.elements = map;
      this.typeKeys = builder1.build();
   }

   private static <T> void castAndValidate(ValidationContext p_279270_, LootDataId<T> p_279249_, Object p_279342_) {
      p_279249_.type().runValidation(p_279270_, p_279249_, (T)p_279342_);
   }

   @Nullable
   public <T> T getElement(LootDataId<T> p_279467_) {
      return (T)this.elements.get(p_279467_);
   }

   public Collection<ResourceLocation> getKeys(LootDataType<?> p_279455_) {
      return this.typeKeys.get(p_279455_);
   }

   public static LootItemCondition createComposite(LootItemCondition[] p_279415_) {
      return new LootDataManager.CompositePredicate(p_279415_);
   }

   public static LootItemFunction createComposite(LootItemFunction[] p_279450_) {
      return new LootDataManager.FunctionSequence(p_279450_);
   }

   static class CompositePredicate implements LootItemCondition {
      private final LootItemCondition[] terms;
      private final Predicate<LootContext> composedPredicate;

      CompositePredicate(LootItemCondition[] p_279376_) {
         this.terms = p_279376_;
         this.composedPredicate = LootItemConditions.andConditions(p_279376_);
      }

      public final boolean test(LootContext p_279232_) {
         return this.composedPredicate.test(p_279232_);
      }

      public void validate(ValidationContext p_279208_) {
         LootItemCondition.super.validate(p_279208_);

         for(int i = 0; i < this.terms.length; ++i) {
            this.terms[i].validate(p_279208_.forChild(".term[" + i + "]"));
         }

      }

      public LootItemConditionType getType() {
         throw new UnsupportedOperationException();
      }
   }

   static class FunctionSequence implements LootItemFunction {
      protected final LootItemFunction[] functions;
      private final BiFunction<ItemStack, LootContext, ItemStack> compositeFunction;

      public FunctionSequence(LootItemFunction[] p_279339_) {
         this.functions = p_279339_;
         this.compositeFunction = LootItemFunctions.compose(p_279339_);
      }

      public ItemStack apply(ItemStack p_279166_, LootContext p_279343_) {
         return this.compositeFunction.apply(p_279166_, p_279343_);
      }

      public void validate(ValidationContext p_279400_) {
         LootItemFunction.super.validate(p_279400_);

         for(int i = 0; i < this.functions.length; ++i) {
            this.functions[i].validate(p_279400_.forChild(".function[" + i + "]"));
         }

      }

      public LootItemFunctionType getType() {
         throw new UnsupportedOperationException();
      }
   }
}
