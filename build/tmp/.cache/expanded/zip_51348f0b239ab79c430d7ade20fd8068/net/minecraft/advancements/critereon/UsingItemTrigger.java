package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class UsingItemTrigger extends SimpleCriterionTrigger<UsingItemTrigger.TriggerInstance> {
   static final ResourceLocation ID = new ResourceLocation("using_item");

   public ResourceLocation getId() {
      return ID;
   }

   public UsingItemTrigger.TriggerInstance createInstance(JsonObject p_286642_, ContextAwarePredicate p_286670_, DeserializationContext p_286897_) {
      ItemPredicate itempredicate = ItemPredicate.fromJson(p_286642_.get("item"));
      return new UsingItemTrigger.TriggerInstance(p_286670_, itempredicate);
   }

   public void trigger(ServerPlayer p_163866_, ItemStack p_163867_) {
      this.trigger(p_163866_, (p_163870_) -> {
         return p_163870_.matches(p_163867_);
      });
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final ItemPredicate item;

      public TriggerInstance(ContextAwarePredicate p_286652_, ItemPredicate p_286296_) {
         super(UsingItemTrigger.ID, p_286652_);
         this.item = p_286296_;
      }

      public static UsingItemTrigger.TriggerInstance lookingAt(EntityPredicate.Builder p_163884_, ItemPredicate.Builder p_163885_) {
         return new UsingItemTrigger.TriggerInstance(EntityPredicate.wrap(p_163884_.build()), p_163885_.build());
      }

      public boolean matches(ItemStack p_163887_) {
         return this.item.matches(p_163887_);
      }

      public JsonObject serializeToJson(SerializationContext p_163889_) {
         JsonObject jsonobject = super.serializeToJson(p_163889_);
         jsonobject.add("item", this.item.serializeToJson());
         return jsonobject;
      }
   }
}