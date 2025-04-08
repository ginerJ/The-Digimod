package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public class ShotCrossbowTrigger extends SimpleCriterionTrigger<ShotCrossbowTrigger.TriggerInstance> {
   static final ResourceLocation ID = new ResourceLocation("shot_crossbow");

   public ResourceLocation getId() {
      return ID;
   }

   public ShotCrossbowTrigger.TriggerInstance createInstance(JsonObject p_286679_, ContextAwarePredicate p_286410_, DeserializationContext p_286233_) {
      ItemPredicate itempredicate = ItemPredicate.fromJson(p_286679_.get("item"));
      return new ShotCrossbowTrigger.TriggerInstance(p_286410_, itempredicate);
   }

   public void trigger(ServerPlayer p_65463_, ItemStack p_65464_) {
      this.trigger(p_65463_, (p_65467_) -> {
         return p_65467_.matches(p_65464_);
      });
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final ItemPredicate item;

      public TriggerInstance(ContextAwarePredicate p_286262_, ItemPredicate p_286755_) {
         super(ShotCrossbowTrigger.ID, p_286262_);
         this.item = p_286755_;
      }

      public static ShotCrossbowTrigger.TriggerInstance shotCrossbow(ItemPredicate p_159432_) {
         return new ShotCrossbowTrigger.TriggerInstance(ContextAwarePredicate.ANY, p_159432_);
      }

      public static ShotCrossbowTrigger.TriggerInstance shotCrossbow(ItemLike p_65484_) {
         return new ShotCrossbowTrigger.TriggerInstance(ContextAwarePredicate.ANY, ItemPredicate.Builder.item().of(p_65484_).build());
      }

      public boolean matches(ItemStack p_65482_) {
         return this.item.matches(p_65482_);
      }

      public JsonObject serializeToJson(SerializationContext p_65486_) {
         JsonObject jsonobject = super.serializeToJson(p_65486_);
         jsonobject.add("item", this.item.serializeToJson());
         return jsonobject;
      }
   }
}