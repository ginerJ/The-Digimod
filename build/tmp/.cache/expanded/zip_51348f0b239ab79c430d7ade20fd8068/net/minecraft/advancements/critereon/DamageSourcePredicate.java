package net.minecraft.advancements.critereon;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.phys.Vec3;

public class DamageSourcePredicate {
   public static final DamageSourcePredicate ANY = DamageSourcePredicate.Builder.damageType().build();
   private final List<TagPredicate<DamageType>> tags;
   private final EntityPredicate directEntity;
   private final EntityPredicate sourceEntity;

   public DamageSourcePredicate(List<TagPredicate<DamageType>> p_270233_, EntityPredicate p_270167_, EntityPredicate p_270429_) {
      this.tags = p_270233_;
      this.directEntity = p_270167_;
      this.sourceEntity = p_270429_;
   }

   public boolean matches(ServerPlayer p_25449_, DamageSource p_25450_) {
      return this.matches(p_25449_.serverLevel(), p_25449_.position(), p_25450_);
   }

   public boolean matches(ServerLevel p_25445_, Vec3 p_25446_, DamageSource p_25447_) {
      if (this == ANY) {
         return true;
      } else {
         for(TagPredicate<DamageType> tagpredicate : this.tags) {
            if (!tagpredicate.matches(p_25447_.typeHolder())) {
               return false;
            }
         }

         if (!this.directEntity.matches(p_25445_, p_25446_, p_25447_.getDirectEntity())) {
            return false;
         } else {
            return this.sourceEntity.matches(p_25445_, p_25446_, p_25447_.getEntity());
         }
      }
   }

   public static DamageSourcePredicate fromJson(@Nullable JsonElement p_25452_) {
      if (p_25452_ != null && !p_25452_.isJsonNull()) {
         JsonObject jsonobject = GsonHelper.convertToJsonObject(p_25452_, "damage type");
         JsonArray jsonarray = GsonHelper.getAsJsonArray(jsonobject, "tags", (JsonArray)null);
         List<TagPredicate<DamageType>> list;
         if (jsonarray != null) {
            list = new ArrayList<>(jsonarray.size());

            for(JsonElement jsonelement : jsonarray) {
               list.add(TagPredicate.fromJson(jsonelement, Registries.DAMAGE_TYPE));
            }
         } else {
            list = List.of();
         }

         EntityPredicate entitypredicate = EntityPredicate.fromJson(jsonobject.get("direct_entity"));
         EntityPredicate entitypredicate1 = EntityPredicate.fromJson(jsonobject.get("source_entity"));
         return new DamageSourcePredicate(list, entitypredicate, entitypredicate1);
      } else {
         return ANY;
      }
   }

   public JsonElement serializeToJson() {
      if (this == ANY) {
         return JsonNull.INSTANCE;
      } else {
         JsonObject jsonobject = new JsonObject();
         if (!this.tags.isEmpty()) {
            JsonArray jsonarray = new JsonArray(this.tags.size());

            for(int i = 0; i < this.tags.size(); ++i) {
               jsonarray.add(this.tags.get(i).serializeToJson());
            }

            jsonobject.add("tags", jsonarray);
         }

         jsonobject.add("direct_entity", this.directEntity.serializeToJson());
         jsonobject.add("source_entity", this.sourceEntity.serializeToJson());
         return jsonobject;
      }
   }

   public static class Builder {
      private final ImmutableList.Builder<TagPredicate<DamageType>> tags = ImmutableList.builder();
      private EntityPredicate directEntity = EntityPredicate.ANY;
      private EntityPredicate sourceEntity = EntityPredicate.ANY;

      public static DamageSourcePredicate.Builder damageType() {
         return new DamageSourcePredicate.Builder();
      }

      public DamageSourcePredicate.Builder tag(TagPredicate<DamageType> p_270455_) {
         this.tags.add(p_270455_);
         return this;
      }

      public DamageSourcePredicate.Builder direct(EntityPredicate p_148230_) {
         this.directEntity = p_148230_;
         return this;
      }

      public DamageSourcePredicate.Builder direct(EntityPredicate.Builder p_25473_) {
         this.directEntity = p_25473_.build();
         return this;
      }

      public DamageSourcePredicate.Builder source(EntityPredicate p_148234_) {
         this.sourceEntity = p_148234_;
         return this;
      }

      public DamageSourcePredicate.Builder source(EntityPredicate.Builder p_148232_) {
         this.sourceEntity = p_148232_.build();
         return this;
      }

      public DamageSourcePredicate build() {
         return new DamageSourcePredicate(this.tags.build(), this.directEntity, this.sourceEntity);
      }
   }
}