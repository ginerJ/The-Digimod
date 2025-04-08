package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class FilledBucketTrigger extends SimpleCriterionTrigger<FilledBucketTrigger.TriggerInstance> {
   static final ResourceLocation ID = new ResourceLocation("filled_bucket");

   public ResourceLocation getId() {
      return ID;
   }

   public FilledBucketTrigger.TriggerInstance createInstance(JsonObject p_286783_, ContextAwarePredicate p_286776_, DeserializationContext p_286812_) {
      ItemPredicate itempredicate = ItemPredicate.fromJson(p_286783_.get("item"));
      return new FilledBucketTrigger.TriggerInstance(p_286776_, itempredicate);
   }

   public void trigger(ServerPlayer p_38773_, ItemStack p_38774_) {
      this.trigger(p_38773_, (p_38777_) -> {
         return p_38777_.matches(p_38774_);
      });
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final ItemPredicate item;

      public TriggerInstance(ContextAwarePredicate p_286231_, ItemPredicate p_286845_) {
         super(FilledBucketTrigger.ID, p_286231_);
         this.item = p_286845_;
      }

      public static FilledBucketTrigger.TriggerInstance filledBucket(ItemPredicate p_38794_) {
         return new FilledBucketTrigger.TriggerInstance(ContextAwarePredicate.ANY, p_38794_);
      }

      public boolean matches(ItemStack p_38792_) {
         return this.item.matches(p_38792_);
      }

      public JsonObject serializeToJson(SerializationContext p_38796_) {
         JsonObject jsonobject = super.serializeToJson(p_38796_);
         jsonobject.add("item", this.item.serializeToJson());
         return jsonobject;
      }
   }
}