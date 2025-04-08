package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public class UsedTotemTrigger extends SimpleCriterionTrigger<UsedTotemTrigger.TriggerInstance> {
   static final ResourceLocation ID = new ResourceLocation("used_totem");

   public ResourceLocation getId() {
      return ID;
   }

   public UsedTotemTrigger.TriggerInstance createInstance(JsonObject p_286841_, ContextAwarePredicate p_286597_, DeserializationContext p_286414_) {
      ItemPredicate itempredicate = ItemPredicate.fromJson(p_286841_.get("item"));
      return new UsedTotemTrigger.TriggerInstance(p_286597_, itempredicate);
   }

   public void trigger(ServerPlayer p_74432_, ItemStack p_74433_) {
      this.trigger(p_74432_, (p_74436_) -> {
         return p_74436_.matches(p_74433_);
      });
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final ItemPredicate item;

      public TriggerInstance(ContextAwarePredicate p_286406_, ItemPredicate p_286462_) {
         super(UsedTotemTrigger.ID, p_286406_);
         this.item = p_286462_;
      }

      public static UsedTotemTrigger.TriggerInstance usedTotem(ItemPredicate p_163725_) {
         return new UsedTotemTrigger.TriggerInstance(ContextAwarePredicate.ANY, p_163725_);
      }

      public static UsedTotemTrigger.TriggerInstance usedTotem(ItemLike p_74453_) {
         return new UsedTotemTrigger.TriggerInstance(ContextAwarePredicate.ANY, ItemPredicate.Builder.item().of(p_74453_).build());
      }

      public boolean matches(ItemStack p_74451_) {
         return this.item.matches(p_74451_);
      }

      public JsonObject serializeToJson(SerializationContext p_74455_) {
         JsonObject jsonobject = super.serializeToJson(p_74455_);
         jsonobject.add("item", this.item.serializeToJson());
         return jsonobject;
      }
   }
}