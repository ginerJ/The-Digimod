package net.minecraft.advancements.critereon;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;

public class RecipeCraftedTrigger extends SimpleCriterionTrigger<RecipeCraftedTrigger.TriggerInstance> {
   static final ResourceLocation ID = new ResourceLocation("recipe_crafted");

   public ResourceLocation getId() {
      return ID;
   }

   protected RecipeCraftedTrigger.TriggerInstance createInstance(JsonObject p_286541_, ContextAwarePredicate p_286267_, DeserializationContext p_286402_) {
      ResourceLocation resourcelocation = new ResourceLocation(GsonHelper.getAsString(p_286541_, "recipe_id"));
      ItemPredicate[] aitempredicate = ItemPredicate.fromJsonArray(p_286541_.get("ingredients"));
      return new RecipeCraftedTrigger.TriggerInstance(p_286267_, resourcelocation, List.of(aitempredicate));
   }

   public void trigger(ServerPlayer p_281468_, ResourceLocation p_282903_, List<ItemStack> p_282070_) {
      this.trigger(p_281468_, (p_282798_) -> {
         return p_282798_.matches(p_282903_, p_282070_);
      });
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final ResourceLocation recipeId;
      private final List<ItemPredicate> predicates;

      public TriggerInstance(ContextAwarePredicate p_286913_, ResourceLocation p_286906_, List<ItemPredicate> p_286302_) {
         super(RecipeCraftedTrigger.ID, p_286913_);
         this.recipeId = p_286906_;
         this.predicates = p_286302_;
      }

      public static RecipeCraftedTrigger.TriggerInstance craftedItem(ResourceLocation p_282794_, List<ItemPredicate> p_281369_) {
         return new RecipeCraftedTrigger.TriggerInstance(ContextAwarePredicate.ANY, p_282794_, p_281369_);
      }

      public static RecipeCraftedTrigger.TriggerInstance craftedItem(ResourceLocation p_283538_) {
         return new RecipeCraftedTrigger.TriggerInstance(ContextAwarePredicate.ANY, p_283538_, List.of());
      }

      boolean matches(ResourceLocation p_283528_, List<ItemStack> p_283698_) {
         if (!p_283528_.equals(this.recipeId)) {
            return false;
         } else {
            List<ItemStack> list = new ArrayList<>(p_283698_);

            for(ItemPredicate itempredicate : this.predicates) {
               boolean flag = false;
               Iterator<ItemStack> iterator = list.iterator();

               while(iterator.hasNext()) {
                  if (itempredicate.matches(iterator.next())) {
                     iterator.remove();
                     flag = true;
                     break;
                  }
               }

               if (!flag) {
                  return false;
               }
            }

            return true;
         }
      }

      public JsonObject serializeToJson(SerializationContext p_281942_) {
         JsonObject jsonobject = super.serializeToJson(p_281942_);
         jsonobject.addProperty("recipe_id", this.recipeId.toString());
         if (this.predicates.size() > 0) {
            JsonArray jsonarray = new JsonArray();

            for(ItemPredicate itempredicate : this.predicates) {
               jsonarray.add(itempredicate.serializeToJson());
            }

            jsonobject.add("ingredients", jsonarray);
         }

         return jsonobject;
      }
   }
}