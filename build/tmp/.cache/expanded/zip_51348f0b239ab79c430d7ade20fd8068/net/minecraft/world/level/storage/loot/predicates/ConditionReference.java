package net.minecraft.world.level.storage.loot.predicates;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootDataId;
import net.minecraft.world.level.storage.loot.LootDataType;
import net.minecraft.world.level.storage.loot.ValidationContext;
import org.slf4j.Logger;

public class ConditionReference implements LootItemCondition {
   private static final Logger LOGGER = LogUtils.getLogger();
   final ResourceLocation name;

   ConditionReference(ResourceLocation p_81553_) {
      this.name = p_81553_;
   }

   public LootItemConditionType getType() {
      return LootItemConditions.REFERENCE;
   }

   public void validate(ValidationContext p_81560_) {
      LootDataId<LootItemCondition> lootdataid = new LootDataId<>(LootDataType.PREDICATE, this.name);
      if (p_81560_.hasVisitedElement(lootdataid)) {
         p_81560_.reportProblem("Condition " + this.name + " is recursively called");
      } else {
         LootItemCondition.super.validate(p_81560_);
         p_81560_.resolver().getElementOptional(lootdataid).ifPresentOrElse((p_279085_) -> {
            p_279085_.validate(p_81560_.enterElement(".{" + this.name + "}", lootdataid));
         }, () -> {
            p_81560_.reportProblem("Unknown condition table called " + this.name);
         });
      }
   }

   public boolean test(LootContext p_81558_) {
      LootItemCondition lootitemcondition = p_81558_.getResolver().getElement(LootDataType.PREDICATE, this.name);
      if (lootitemcondition == null) {
         LOGGER.warn("Tried using unknown condition table called {}", (Object)this.name);
         return false;
      } else {
         LootContext.VisitedEntry<?> visitedentry = LootContext.createVisitedEntry(lootitemcondition);
         if (p_81558_.pushVisitedElement(visitedentry)) {
            boolean flag;
            try {
               flag = lootitemcondition.test(p_81558_);
            } finally {
               p_81558_.popVisitedElement(visitedentry);
            }

            return flag;
         } else {
            LOGGER.warn("Detected infinite loop in loot tables");
            return false;
         }
      }
   }

   public static LootItemCondition.Builder conditionReference(ResourceLocation p_165481_) {
      return () -> {
         return new ConditionReference(p_165481_);
      };
   }

   public static class Serializer implements net.minecraft.world.level.storage.loot.Serializer<ConditionReference> {
      public void serialize(JsonObject p_81571_, ConditionReference p_81572_, JsonSerializationContext p_81573_) {
         p_81571_.addProperty("name", p_81572_.name.toString());
      }

      public ConditionReference deserialize(JsonObject p_81579_, JsonDeserializationContext p_81580_) {
         ResourceLocation resourcelocation = new ResourceLocation(GsonHelper.getAsString(p_81579_, "name"));
         return new ConditionReference(resourcelocation);
      }
   }
}