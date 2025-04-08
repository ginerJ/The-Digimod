package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class ItemDurabilityTrigger extends SimpleCriterionTrigger<ItemDurabilityTrigger.TriggerInstance> {
   static final ResourceLocation ID = new ResourceLocation("item_durability_changed");

   public ResourceLocation getId() {
      return ID;
   }

   public ItemDurabilityTrigger.TriggerInstance createInstance(JsonObject p_286693_, ContextAwarePredicate p_286383_, DeserializationContext p_286352_) {
      ItemPredicate itempredicate = ItemPredicate.fromJson(p_286693_.get("item"));
      MinMaxBounds.Ints minmaxbounds$ints = MinMaxBounds.Ints.fromJson(p_286693_.get("durability"));
      MinMaxBounds.Ints minmaxbounds$ints1 = MinMaxBounds.Ints.fromJson(p_286693_.get("delta"));
      return new ItemDurabilityTrigger.TriggerInstance(p_286383_, itempredicate, minmaxbounds$ints, minmaxbounds$ints1);
   }

   public void trigger(ServerPlayer p_43670_, ItemStack p_43671_, int p_43672_) {
      this.trigger(p_43670_, (p_43676_) -> {
         return p_43676_.matches(p_43671_, p_43672_);
      });
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final ItemPredicate item;
      private final MinMaxBounds.Ints durability;
      private final MinMaxBounds.Ints delta;

      public TriggerInstance(ContextAwarePredicate p_286731_, ItemPredicate p_286447_, MinMaxBounds.Ints p_286431_, MinMaxBounds.Ints p_286460_) {
         super(ItemDurabilityTrigger.ID, p_286731_);
         this.item = p_286447_;
         this.durability = p_286431_;
         this.delta = p_286460_;
      }

      public static ItemDurabilityTrigger.TriggerInstance changedDurability(ItemPredicate p_151287_, MinMaxBounds.Ints p_151288_) {
         return changedDurability(ContextAwarePredicate.ANY, p_151287_, p_151288_);
      }

      public static ItemDurabilityTrigger.TriggerInstance changedDurability(ContextAwarePredicate p_286720_, ItemPredicate p_286288_, MinMaxBounds.Ints p_286730_) {
         return new ItemDurabilityTrigger.TriggerInstance(p_286720_, p_286288_, p_286730_, MinMaxBounds.Ints.ANY);
      }

      public boolean matches(ItemStack p_43699_, int p_43700_) {
         if (!this.item.matches(p_43699_)) {
            return false;
         } else if (!this.durability.matches(p_43699_.getMaxDamage() - p_43700_)) {
            return false;
         } else {
            return this.delta.matches(p_43699_.getDamageValue() - p_43700_);
         }
      }

      public JsonObject serializeToJson(SerializationContext p_43702_) {
         JsonObject jsonobject = super.serializeToJson(p_43702_);
         jsonobject.add("item", this.item.serializeToJson());
         jsonobject.add("durability", this.durability.serializeToJson());
         jsonobject.add("delta", this.delta.serializeToJson());
         return jsonobject;
      }
   }
}