package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;

public class LevitationTrigger extends SimpleCriterionTrigger<LevitationTrigger.TriggerInstance> {
   static final ResourceLocation ID = new ResourceLocation("levitation");

   public ResourceLocation getId() {
      return ID;
   }

   public LevitationTrigger.TriggerInstance createInstance(JsonObject p_286359_, ContextAwarePredicate p_286588_, DeserializationContext p_286241_) {
      DistancePredicate distancepredicate = DistancePredicate.fromJson(p_286359_.get("distance"));
      MinMaxBounds.Ints minmaxbounds$ints = MinMaxBounds.Ints.fromJson(p_286359_.get("duration"));
      return new LevitationTrigger.TriggerInstance(p_286588_, distancepredicate, minmaxbounds$ints);
   }

   public void trigger(ServerPlayer p_49117_, Vec3 p_49118_, int p_49119_) {
      this.trigger(p_49117_, (p_49124_) -> {
         return p_49124_.matches(p_49117_, p_49118_, p_49119_);
      });
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final DistancePredicate distance;
      private final MinMaxBounds.Ints duration;

      public TriggerInstance(ContextAwarePredicate p_286511_, DistancePredicate p_286806_, MinMaxBounds.Ints p_286676_) {
         super(LevitationTrigger.ID, p_286511_);
         this.distance = p_286806_;
         this.duration = p_286676_;
      }

      public static LevitationTrigger.TriggerInstance levitated(DistancePredicate p_49145_) {
         return new LevitationTrigger.TriggerInstance(ContextAwarePredicate.ANY, p_49145_, MinMaxBounds.Ints.ANY);
      }

      public boolean matches(ServerPlayer p_49141_, Vec3 p_49142_, int p_49143_) {
         if (!this.distance.matches(p_49142_.x, p_49142_.y, p_49142_.z, p_49141_.getX(), p_49141_.getY(), p_49141_.getZ())) {
            return false;
         } else {
            return this.duration.matches(p_49143_);
         }
      }

      public JsonObject serializeToJson(SerializationContext p_49147_) {
         JsonObject jsonobject = super.serializeToJson(p_49147_);
         jsonobject.add("distance", this.distance.serializeToJson());
         jsonobject.add("duration", this.duration.serializeToJson());
         return jsonobject;
      }
   }
}