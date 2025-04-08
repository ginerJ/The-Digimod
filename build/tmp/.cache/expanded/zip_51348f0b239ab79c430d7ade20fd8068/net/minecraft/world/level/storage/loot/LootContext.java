package net.minecraft.world.level.storage.loot;

import com.google.common.collect.Sets;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.util.Set;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class LootContext {
   private final LootParams params;
   private final RandomSource random;
   private final LootDataResolver lootDataResolver;
   private final Set<LootContext.VisitedEntry<?>> visitedElements = Sets.newLinkedHashSet();

   LootContext(LootParams p_287722_, RandomSource p_287702_, LootDataResolver p_287619_) {
      this.params = p_287722_;
      this.random = p_287702_;
      this.lootDataResolver = p_287619_;
   }

   public boolean hasParam(LootContextParam<?> p_78937_) {
      return this.params.hasParam(p_78937_);
   }

   public <T> T getParam(LootContextParam<T> p_165125_) {
      return this.params.getParameter(p_165125_);
   }

   public void addDynamicDrops(ResourceLocation p_78943_, Consumer<ItemStack> p_78944_) {
      this.params.addDynamicDrops(p_78943_, p_78944_);
   }

   @Nullable
   public <T> T getParamOrNull(LootContextParam<T> p_78954_) {
      return this.params.getParamOrNull(p_78954_);
   }

   public boolean hasVisitedElement(LootContext.VisitedEntry<?> p_279182_) {
      return this.visitedElements.contains(p_279182_);
   }

   public boolean pushVisitedElement(LootContext.VisitedEntry<?> p_279152_) {
      return this.visitedElements.add(p_279152_);
   }

   public void popVisitedElement(LootContext.VisitedEntry<?> p_279198_) {
      this.visitedElements.remove(p_279198_);
   }

   public LootDataResolver getResolver() {
      return this.lootDataResolver;
   }

   public RandomSource getRandom() {
      return this.random;
   }

   public float getLuck() {
      return this.params.getLuck();
   }

   public ServerLevel getLevel() {
      return this.params.getLevel();
   }

   public static LootContext.VisitedEntry<LootTable> createVisitedEntry(LootTable p_279327_) {
      return new LootContext.VisitedEntry<>(LootDataType.TABLE, p_279327_);
   }

   public static LootContext.VisitedEntry<LootItemCondition> createVisitedEntry(LootItemCondition p_279250_) {
      return new LootContext.VisitedEntry<>(LootDataType.PREDICATE, p_279250_);
   }

   public static LootContext.VisitedEntry<LootItemFunction> createVisitedEntry(LootItemFunction p_279163_) {
      return new LootContext.VisitedEntry<>(LootDataType.MODIFIER, p_279163_);
   }

   // ============================== FORGE START ==============================
   public int getLootingModifier() {
      return net.minecraftforge.common.ForgeHooks.getLootingLevel(getParamOrNull(LootContextParams.THIS_ENTITY), getParamOrNull(LootContextParams.KILLER_ENTITY), getParamOrNull(LootContextParams.DAMAGE_SOURCE));
   }

   private ResourceLocation queriedLootTableId;

   private LootContext(LootParams p_287722_, RandomSource p_287702_, LootDataResolver p_287619_, ResourceLocation queriedLootTableId) {
      this(p_287722_, p_287702_, p_287619_);
      this.queriedLootTableId = queriedLootTableId;
   }

   public void setQueriedLootTableId(ResourceLocation queriedLootTableId) {
      if (this.queriedLootTableId == null && queriedLootTableId != null) this.queriedLootTableId = queriedLootTableId;
   }

   public ResourceLocation getQueriedLootTableId() {
      return this.queriedLootTableId == null ? net.minecraftforge.common.loot.LootTableIdCondition.UNKNOWN_LOOT_TABLE : this.queriedLootTableId;
   }
   // =============================== FORGE END ===============================

   public static class Builder {
      private final LootParams params;
      @Nullable
      private RandomSource random;
      private ResourceLocation queriedLootTableId; // Forge: correctly pass around loot table ID with copy constructor

      public Builder(LootParams p_287628_) {
         this.params = p_287628_;
      }

      public Builder(LootContext context) {
         this.params = context.params;
         this.random = context.random;
         this.queriedLootTableId = context.queriedLootTableId;
      }

      public LootContext.Builder withOptionalRandomSeed(long p_78966_) {
         if (p_78966_ != 0L) {
            this.random = RandomSource.create(p_78966_);
         }

         return this;
      }

      public LootContext.Builder withQueriedLootTableId(ResourceLocation queriedLootTableId) {
         this.queriedLootTableId = queriedLootTableId;
         return this;
      }

      public ServerLevel getLevel() {
         return this.params.getLevel();
      }

      public LootContext create(@Nullable ResourceLocation p_287626_) {
         ServerLevel serverlevel = this.getLevel();
         MinecraftServer minecraftserver = serverlevel.getServer();
         RandomSource randomsource;
         if (this.random != null) {
            randomsource = this.random;
         } else if (p_287626_ != null) {
            randomsource = serverlevel.getRandomSequence(p_287626_);
         } else {
            randomsource = serverlevel.getRandom();
         }

         return new LootContext(this.params, randomsource, minecraftserver.getLootData(), queriedLootTableId);
      }
   }

   public static enum EntityTarget {
      THIS("this", LootContextParams.THIS_ENTITY),
      KILLER("killer", LootContextParams.KILLER_ENTITY),
      DIRECT_KILLER("direct_killer", LootContextParams.DIRECT_KILLER_ENTITY),
      KILLER_PLAYER("killer_player", LootContextParams.LAST_DAMAGE_PLAYER);

      final String name;
      private final LootContextParam<? extends Entity> param;

      private EntityTarget(String p_79001_, LootContextParam<? extends Entity> p_79002_) {
         this.name = p_79001_;
         this.param = p_79002_;
      }

      public LootContextParam<? extends Entity> getParam() {
         return this.param;
      }

      // Forge: This method is patched in to expose the same name used in getByName so that ContextNbtProvider#forEntity serializes it properly
      public String getName() {
         return this.name;
      }

      public static LootContext.EntityTarget getByName(String p_79007_) {
         for(LootContext.EntityTarget lootcontext$entitytarget : values()) {
            if (lootcontext$entitytarget.name.equals(p_79007_)) {
               return lootcontext$entitytarget;
            }
         }

         throw new IllegalArgumentException("Invalid entity target " + p_79007_);
      }

      public static class Serializer extends TypeAdapter<LootContext.EntityTarget> {
         public void write(JsonWriter p_79015_, LootContext.EntityTarget p_79016_) throws IOException {
            p_79015_.value(p_79016_.name);
         }

         public LootContext.EntityTarget read(JsonReader p_79013_) throws IOException {
            return LootContext.EntityTarget.getByName(p_79013_.nextString());
         }
      }
   }

   public static record VisitedEntry<T>(LootDataType<T> type, T value) {
   }
}
