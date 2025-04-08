package net.minecraft.world.level.storage.loot.predicates;

public class AllOfCondition extends CompositeLootItemCondition {
   AllOfCondition(LootItemCondition[] p_286723_) {
      super(p_286723_, LootItemConditions.andConditions(p_286723_));
   }

   public LootItemConditionType getType() {
      return LootItemConditions.ALL_OF;
   }

   public static AllOfCondition.Builder allOf(LootItemCondition.Builder... p_286873_) {
      return new AllOfCondition.Builder(p_286873_);
   }

   public static class Builder extends CompositeLootItemCondition.Builder {
      public Builder(LootItemCondition.Builder... p_286842_) {
         super(p_286842_);
      }

      public AllOfCondition.Builder and(LootItemCondition.Builder p_286760_) {
         this.addTerm(p_286760_);
         return this;
      }

      protected LootItemCondition create(LootItemCondition[] p_286816_) {
         return new AllOfCondition(p_286816_);
      }
   }

   public static class Serializer extends CompositeLootItemCondition.Serializer<AllOfCondition> {
      protected AllOfCondition create(LootItemCondition[] p_286223_) {
         return new AllOfCondition(p_286223_);
      }
   }
}