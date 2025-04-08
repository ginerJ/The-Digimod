package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootContext;

public class EffectsChangedTrigger extends SimpleCriterionTrigger<EffectsChangedTrigger.TriggerInstance> {
   static final ResourceLocation ID = new ResourceLocation("effects_changed");

   public ResourceLocation getId() {
      return ID;
   }

   public EffectsChangedTrigger.TriggerInstance createInstance(JsonObject p_286892_, ContextAwarePredicate p_286547_, DeserializationContext p_286271_) {
      MobEffectsPredicate mobeffectspredicate = MobEffectsPredicate.fromJson(p_286892_.get("effects"));
      ContextAwarePredicate contextawarepredicate = EntityPredicate.fromJson(p_286892_, "source", p_286271_);
      return new EffectsChangedTrigger.TriggerInstance(p_286547_, mobeffectspredicate, contextawarepredicate);
   }

   public void trigger(ServerPlayer p_149263_, @Nullable Entity p_149264_) {
      LootContext lootcontext = p_149264_ != null ? EntityPredicate.createContext(p_149263_, p_149264_) : null;
      this.trigger(p_149263_, (p_149268_) -> {
         return p_149268_.matches(p_149263_, lootcontext);
      });
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final MobEffectsPredicate effects;
      private final ContextAwarePredicate source;

      public TriggerInstance(ContextAwarePredicate p_286580_, MobEffectsPredicate p_286820_, ContextAwarePredicate p_286703_) {
         super(EffectsChangedTrigger.ID, p_286580_);
         this.effects = p_286820_;
         this.source = p_286703_;
      }

      public static EffectsChangedTrigger.TriggerInstance hasEffects(MobEffectsPredicate p_26781_) {
         return new EffectsChangedTrigger.TriggerInstance(ContextAwarePredicate.ANY, p_26781_, ContextAwarePredicate.ANY);
      }

      public static EffectsChangedTrigger.TriggerInstance gotEffectsFrom(EntityPredicate p_149278_) {
         return new EffectsChangedTrigger.TriggerInstance(ContextAwarePredicate.ANY, MobEffectsPredicate.ANY, EntityPredicate.wrap(p_149278_));
      }

      public boolean matches(ServerPlayer p_149275_, @Nullable LootContext p_149276_) {
         if (!this.effects.matches(p_149275_)) {
            return false;
         } else {
            return this.source == ContextAwarePredicate.ANY || p_149276_ != null && this.source.matches(p_149276_);
         }
      }

      public JsonObject serializeToJson(SerializationContext p_26783_) {
         JsonObject jsonobject = super.serializeToJson(p_26783_);
         jsonobject.add("effects", this.effects.serializeToJson());
         jsonobject.add("source", this.source.toJson(p_26783_));
         return jsonobject;
      }
   }
}