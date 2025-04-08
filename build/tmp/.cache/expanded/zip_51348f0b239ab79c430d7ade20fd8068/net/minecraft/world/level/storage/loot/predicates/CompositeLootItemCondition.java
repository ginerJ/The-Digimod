package net.minecraft.world.level.storage.loot.predicates;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.ValidationContext;

public abstract class CompositeLootItemCondition implements LootItemCondition {
   final LootItemCondition[] terms;
   private final Predicate<LootContext> composedPredicate;

   protected CompositeLootItemCondition(LootItemCondition[] p_286437_, Predicate<LootContext> p_286771_) {
      this.terms = p_286437_;
      this.composedPredicate = p_286771_;
   }

   public final boolean test(LootContext p_286298_) {
      return this.composedPredicate.test(p_286298_);
   }

   public void validate(ValidationContext p_286819_) {
      LootItemCondition.super.validate(p_286819_);

      for(int i = 0; i < this.terms.length; ++i) {
         this.terms[i].validate(p_286819_.forChild(".term[" + i + "]"));
      }

   }

   public abstract static class Builder implements LootItemCondition.Builder {
      private final List<LootItemCondition> terms = new ArrayList<>();

      public Builder(LootItemCondition.Builder... p_286619_) {
         for(LootItemCondition.Builder lootitemcondition$builder : p_286619_) {
            this.terms.add(lootitemcondition$builder.build());
         }

      }

      public void addTerm(LootItemCondition.Builder p_286677_) {
         this.terms.add(p_286677_.build());
      }

      public LootItemCondition build() {
         LootItemCondition[] alootitemcondition = this.terms.toArray((p_286455_) -> {
            return new LootItemCondition[p_286455_];
         });
         return this.create(alootitemcondition);
      }

      protected abstract LootItemCondition create(LootItemCondition[] p_286469_);
   }

   public abstract static class Serializer<T extends CompositeLootItemCondition> implements net.minecraft.world.level.storage.loot.Serializer<T> {
      public void serialize(JsonObject p_286342_, CompositeLootItemCondition p_286412_, JsonSerializationContext p_286331_) {
         p_286342_.add("terms", p_286331_.serialize(p_286412_.terms));
      }

      public T deserialize(JsonObject p_286509_, JsonDeserializationContext p_286321_) {
         LootItemCondition[] alootitemcondition = GsonHelper.getAsObject(p_286509_, "terms", p_286321_, LootItemCondition[].class);
         return this.create(alootitemcondition);
      }

      protected abstract T create(LootItemCondition[] p_286604_);
   }
}