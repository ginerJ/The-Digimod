package net.minecraft.world.level.storage.loot;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;

public class LootParams {
   private final ServerLevel level;
   private final Map<LootContextParam<?>, Object> params;
   private final Map<ResourceLocation, LootParams.DynamicDrop> dynamicDrops;
   private final float luck;

   public LootParams(ServerLevel p_287766_, Map<LootContextParam<?>, Object> p_287705_, Map<ResourceLocation, LootParams.DynamicDrop> p_287642_, float p_287671_) {
      this.level = p_287766_;
      this.params = p_287705_;
      this.dynamicDrops = p_287642_;
      this.luck = p_287671_;
   }

   public ServerLevel getLevel() {
      return this.level;
   }

   public boolean hasParam(LootContextParam<?> p_287749_) {
      return this.params.containsKey(p_287749_);
   }

   public <T> T getParameter(LootContextParam<T> p_287670_) {
      T t = (T)this.params.get(p_287670_);
      if (t == null) {
         throw new NoSuchElementException(p_287670_.getName().toString());
      } else {
         return t;
      }
   }

   @Nullable
   public <T> T getOptionalParameter(LootContextParam<T> p_287644_) {
      return (T)this.params.get(p_287644_);
   }

   @Nullable
   public <T> T getParamOrNull(LootContextParam<T> p_287769_) {
      return (T)this.params.get(p_287769_);
   }

   public void addDynamicDrops(ResourceLocation p_287768_, Consumer<ItemStack> p_287711_) {
      LootParams.DynamicDrop lootparams$dynamicdrop = this.dynamicDrops.get(p_287768_);
      if (lootparams$dynamicdrop != null) {
         lootparams$dynamicdrop.add(p_287711_);
      }

   }

   public float getLuck() {
      return this.luck;
   }

   public static class Builder {
      private final ServerLevel level;
      private final Map<LootContextParam<?>, Object> params = Maps.newIdentityHashMap();
      private final Map<ResourceLocation, LootParams.DynamicDrop> dynamicDrops = Maps.newHashMap();
      private float luck;

      public Builder(ServerLevel p_287594_) {
         this.level = p_287594_;
      }

      public ServerLevel getLevel() {
         return this.level;
      }

      public <T> LootParams.Builder withParameter(LootContextParam<T> p_287706_, T p_287606_) {
         this.params.put(p_287706_, p_287606_);
         return this;
      }

      public <T> LootParams.Builder withOptionalParameter(LootContextParam<T> p_287680_, @Nullable T p_287630_) {
         if (p_287630_ == null) {
            this.params.remove(p_287680_);
         } else {
            this.params.put(p_287680_, p_287630_);
         }

         return this;
      }

      public <T> T getParameter(LootContextParam<T> p_287646_) {
         T t = (T)this.params.get(p_287646_);
         if (t == null) {
            throw new NoSuchElementException(p_287646_.getName().toString());
         } else {
            return t;
         }
      }

      @Nullable
      public <T> T getOptionalParameter(LootContextParam<T> p_287759_) {
         return (T)this.params.get(p_287759_);
      }

      public LootParams.Builder withDynamicDrop(ResourceLocation p_287734_, LootParams.DynamicDrop p_287724_) {
         LootParams.DynamicDrop lootparams$dynamicdrop = this.dynamicDrops.put(p_287734_, p_287724_);
         if (lootparams$dynamicdrop != null) {
            throw new IllegalStateException("Duplicated dynamic drop '" + this.dynamicDrops + "'");
         } else {
            return this;
         }
      }

      public LootParams.Builder withLuck(float p_287703_) {
         this.luck = p_287703_;
         return this;
      }

      public LootParams create(LootContextParamSet p_287701_) {
         Set<LootContextParam<?>> set = Sets.difference(this.params.keySet(), p_287701_.getAllowed());
         if (false && !set.isEmpty()) { // Forge: Allow mods to pass custom loot parameters (not part of the vanilla loot table) to the loot context.
            throw new IllegalArgumentException("Parameters not allowed in this parameter set: " + set);
         } else {
            Set<LootContextParam<?>> set1 = Sets.difference(p_287701_.getRequired(), this.params.keySet());
            if (!set1.isEmpty()) {
               throw new IllegalArgumentException("Missing required parameters: " + set1);
            } else {
               return new LootParams(this.level, this.params, this.dynamicDrops, this.luck);
            }
         }
      }
   }

   @FunctionalInterface
   public interface DynamicDrop {
      void add(Consumer<ItemStack> p_287584_);
   }
}
