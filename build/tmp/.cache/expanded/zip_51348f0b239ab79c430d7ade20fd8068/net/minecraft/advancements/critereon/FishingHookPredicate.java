package net.minecraft.advancements.critereon;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import javax.annotation.Nullable;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.phys.Vec3;

public class FishingHookPredicate implements EntitySubPredicate {
   public static final FishingHookPredicate ANY = new FishingHookPredicate(false);
   private static final String IN_OPEN_WATER_KEY = "in_open_water";
   private final boolean inOpenWater;

   private FishingHookPredicate(boolean p_39760_) {
      this.inOpenWater = p_39760_;
   }

   public static FishingHookPredicate inOpenWater(boolean p_39767_) {
      return new FishingHookPredicate(p_39767_);
   }

   public static FishingHookPredicate fromJson(JsonObject p_219720_) {
      JsonElement jsonelement = p_219720_.get("in_open_water");
      return jsonelement != null ? new FishingHookPredicate(GsonHelper.convertToBoolean(jsonelement, "in_open_water")) : ANY;
   }

   public JsonObject serializeCustomData() {
      if (this == ANY) {
         return new JsonObject();
      } else {
         JsonObject jsonobject = new JsonObject();
         jsonobject.add("in_open_water", new JsonPrimitive(this.inOpenWater));
         return jsonobject;
      }
   }

   public EntitySubPredicate.Type type() {
      return EntitySubPredicate.Types.FISHING_HOOK;
   }

   public boolean matches(Entity p_219716_, ServerLevel p_219717_, @Nullable Vec3 p_219718_) {
      if (this == ANY) {
         return true;
      } else if (!(p_219716_ instanceof FishingHook)) {
         return false;
      } else {
         FishingHook fishinghook = (FishingHook)p_219716_;
         return this.inOpenWater == fishinghook.isOpenWaterFishing();
      }
   }
}