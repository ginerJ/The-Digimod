package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class ConstructBeaconTrigger extends SimpleCriterionTrigger<ConstructBeaconTrigger.TriggerInstance> {
   static final ResourceLocation ID = new ResourceLocation("construct_beacon");

   public ResourceLocation getId() {
      return ID;
   }

   public ConstructBeaconTrigger.TriggerInstance createInstance(JsonObject p_286465_, ContextAwarePredicate p_286914_, DeserializationContext p_286803_) {
      MinMaxBounds.Ints minmaxbounds$ints = MinMaxBounds.Ints.fromJson(p_286465_.get("level"));
      return new ConstructBeaconTrigger.TriggerInstance(p_286914_, minmaxbounds$ints);
   }

   public void trigger(ServerPlayer p_148030_, int p_148031_) {
      this.trigger(p_148030_, (p_148028_) -> {
         return p_148028_.matches(p_148031_);
      });
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final MinMaxBounds.Ints level;

      public TriggerInstance(ContextAwarePredicate p_286868_, MinMaxBounds.Ints p_286272_) {
         super(ConstructBeaconTrigger.ID, p_286868_);
         this.level = p_286272_;
      }

      public static ConstructBeaconTrigger.TriggerInstance constructedBeacon() {
         return new ConstructBeaconTrigger.TriggerInstance(ContextAwarePredicate.ANY, MinMaxBounds.Ints.ANY);
      }

      public static ConstructBeaconTrigger.TriggerInstance constructedBeacon(MinMaxBounds.Ints p_22766_) {
         return new ConstructBeaconTrigger.TriggerInstance(ContextAwarePredicate.ANY, p_22766_);
      }

      public boolean matches(int p_148033_) {
         return this.level.matches(p_148033_);
      }

      public JsonObject serializeToJson(SerializationContext p_22770_) {
         JsonObject jsonobject = super.serializeToJson(p_22770_);
         jsonobject.add("level", this.level.serializeToJson());
         return jsonobject;
      }
   }
}