package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.phys.Vec3;

public class SlimePredicate implements EntitySubPredicate {
   private final MinMaxBounds.Ints size;

   private SlimePredicate(MinMaxBounds.Ints p_223420_) {
      this.size = p_223420_;
   }

   public static SlimePredicate sized(MinMaxBounds.Ints p_223427_) {
      return new SlimePredicate(p_223427_);
   }

   public static SlimePredicate fromJson(JsonObject p_223429_) {
      MinMaxBounds.Ints minmaxbounds$ints = MinMaxBounds.Ints.fromJson(p_223429_.get("size"));
      return new SlimePredicate(minmaxbounds$ints);
   }

   public JsonObject serializeCustomData() {
      JsonObject jsonobject = new JsonObject();
      jsonobject.add("size", this.size.serializeToJson());
      return jsonobject;
   }

   public boolean matches(Entity p_223423_, ServerLevel p_223424_, @Nullable Vec3 p_223425_) {
      if (p_223423_ instanceof Slime slime) {
         return this.size.matches(slime.getSize());
      } else {
         return false;
      }
   }

   public EntitySubPredicate.Type type() {
      return EntitySubPredicate.Types.SLIME;
   }
}