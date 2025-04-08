package net.minecraft.advancements.critereon;

import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import java.util.Map;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.CriterionProgress;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.ServerAdvancementManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.RecipeBook;
import net.minecraft.stats.Stat;
import net.minecraft.stats.StatType;
import net.minecraft.stats.StatsCounter;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class PlayerPredicate implements EntitySubPredicate {
   public static final int LOOKING_AT_RANGE = 100;
   private final MinMaxBounds.Ints level;
   @Nullable
   private final GameType gameType;
   private final Map<Stat<?>, MinMaxBounds.Ints> stats;
   private final Object2BooleanMap<ResourceLocation> recipes;
   private final Map<ResourceLocation, PlayerPredicate.AdvancementPredicate> advancements;
   private final EntityPredicate lookingAt;

   private static PlayerPredicate.AdvancementPredicate advancementPredicateFromJson(JsonElement p_62290_) {
      if (p_62290_.isJsonPrimitive()) {
         boolean flag = p_62290_.getAsBoolean();
         return new PlayerPredicate.AdvancementDonePredicate(flag);
      } else {
         Object2BooleanMap<String> object2booleanmap = new Object2BooleanOpenHashMap<>();
         JsonObject jsonobject = GsonHelper.convertToJsonObject(p_62290_, "criterion data");
         jsonobject.entrySet().forEach((p_62288_) -> {
            boolean flag1 = GsonHelper.convertToBoolean(p_62288_.getValue(), "criterion test");
            object2booleanmap.put(p_62288_.getKey(), flag1);
         });
         return new PlayerPredicate.AdvancementCriterionsPredicate(object2booleanmap);
      }
   }

   PlayerPredicate(MinMaxBounds.Ints p_156746_, @Nullable GameType p_156747_, Map<Stat<?>, MinMaxBounds.Ints> p_156748_, Object2BooleanMap<ResourceLocation> p_156749_, Map<ResourceLocation, PlayerPredicate.AdvancementPredicate> p_156750_, EntityPredicate p_156751_) {
      this.level = p_156746_;
      this.gameType = p_156747_;
      this.stats = p_156748_;
      this.recipes = p_156749_;
      this.advancements = p_156750_;
      this.lookingAt = p_156751_;
   }

   public boolean matches(Entity p_222484_, ServerLevel p_222485_, @Nullable Vec3 p_222486_) {
      if (!(p_222484_ instanceof ServerPlayer serverplayer)) {
         return false;
      } else if (!this.level.matches(serverplayer.experienceLevel)) {
         return false;
      } else if (this.gameType != null && this.gameType != serverplayer.gameMode.getGameModeForPlayer()) {
         return false;
      } else {
         StatsCounter statscounter = serverplayer.getStats();

         for(Map.Entry<Stat<?>, MinMaxBounds.Ints> entry : this.stats.entrySet()) {
            int i = statscounter.getValue(entry.getKey());
            if (!entry.getValue().matches(i)) {
               return false;
            }
         }

         RecipeBook recipebook = serverplayer.getRecipeBook();

         for(Object2BooleanMap.Entry<ResourceLocation> entry2 : this.recipes.object2BooleanEntrySet()) {
            if (recipebook.contains(entry2.getKey()) != entry2.getBooleanValue()) {
               return false;
            }
         }

         if (!this.advancements.isEmpty()) {
            PlayerAdvancements playeradvancements = serverplayer.getAdvancements();
            ServerAdvancementManager serveradvancementmanager = serverplayer.getServer().getAdvancements();

            for(Map.Entry<ResourceLocation, PlayerPredicate.AdvancementPredicate> entry1 : this.advancements.entrySet()) {
               Advancement advancement = serveradvancementmanager.getAdvancement(entry1.getKey());
               if (advancement == null || !entry1.getValue().test(playeradvancements.getOrStartProgress(advancement))) {
                  return false;
               }
            }
         }

         if (this.lookingAt != EntityPredicate.ANY) {
            Vec3 vec3 = serverplayer.getEyePosition();
            Vec3 vec31 = serverplayer.getViewVector(1.0F);
            Vec3 vec32 = vec3.add(vec31.x * 100.0D, vec31.y * 100.0D, vec31.z * 100.0D);
            EntityHitResult entityhitresult = ProjectileUtil.getEntityHitResult(serverplayer.level(), serverplayer, vec3, vec32, (new AABB(vec3, vec32)).inflate(1.0D), (p_156765_) -> {
               return !p_156765_.isSpectator();
            }, 0.0F);
            if (entityhitresult == null || entityhitresult.getType() != HitResult.Type.ENTITY) {
               return false;
            }

            Entity entity = entityhitresult.getEntity();
            if (!this.lookingAt.matches(serverplayer, entity) || !serverplayer.hasLineOfSight(entity)) {
               return false;
            }
         }

         return true;
      }
   }

   public static PlayerPredicate fromJson(JsonObject p_222492_) {
      MinMaxBounds.Ints minmaxbounds$ints = MinMaxBounds.Ints.fromJson(p_222492_.get("level"));
      String s = GsonHelper.getAsString(p_222492_, "gamemode", "");
      GameType gametype = GameType.byName(s, (GameType)null);
      Map<Stat<?>, MinMaxBounds.Ints> map = Maps.newHashMap();
      JsonArray jsonarray = GsonHelper.getAsJsonArray(p_222492_, "stats", (JsonArray)null);
      if (jsonarray != null) {
         for(JsonElement jsonelement : jsonarray) {
            JsonObject jsonobject = GsonHelper.convertToJsonObject(jsonelement, "stats entry");
            ResourceLocation resourcelocation = new ResourceLocation(GsonHelper.getAsString(jsonobject, "type"));
            StatType<?> stattype = BuiltInRegistries.STAT_TYPE.get(resourcelocation);
            if (stattype == null) {
               throw new JsonParseException("Invalid stat type: " + resourcelocation);
            }

            ResourceLocation resourcelocation1 = new ResourceLocation(GsonHelper.getAsString(jsonobject, "stat"));
            Stat<?> stat = getStat(stattype, resourcelocation1);
            MinMaxBounds.Ints minmaxbounds$ints1 = MinMaxBounds.Ints.fromJson(jsonobject.get("value"));
            map.put(stat, minmaxbounds$ints1);
         }
      }

      Object2BooleanMap<ResourceLocation> object2booleanmap = new Object2BooleanOpenHashMap<>();
      JsonObject jsonobject1 = GsonHelper.getAsJsonObject(p_222492_, "recipes", new JsonObject());

      for(Map.Entry<String, JsonElement> entry : jsonobject1.entrySet()) {
         ResourceLocation resourcelocation2 = new ResourceLocation(entry.getKey());
         boolean flag = GsonHelper.convertToBoolean(entry.getValue(), "recipe present");
         object2booleanmap.put(resourcelocation2, flag);
      }

      Map<ResourceLocation, PlayerPredicate.AdvancementPredicate> map1 = Maps.newHashMap();
      JsonObject jsonobject2 = GsonHelper.getAsJsonObject(p_222492_, "advancements", new JsonObject());

      for(Map.Entry<String, JsonElement> entry1 : jsonobject2.entrySet()) {
         ResourceLocation resourcelocation3 = new ResourceLocation(entry1.getKey());
         PlayerPredicate.AdvancementPredicate playerpredicate$advancementpredicate = advancementPredicateFromJson(entry1.getValue());
         map1.put(resourcelocation3, playerpredicate$advancementpredicate);
      }

      EntityPredicate entitypredicate = EntityPredicate.fromJson(p_222492_.get("looking_at"));
      return new PlayerPredicate(minmaxbounds$ints, gametype, map, object2booleanmap, map1, entitypredicate);
   }

   private static <T> Stat<T> getStat(StatType<T> p_62268_, ResourceLocation p_62269_) {
      Registry<T> registry = p_62268_.getRegistry();
      T t = registry.get(p_62269_);
      if (t == null) {
         throw new JsonParseException("Unknown object " + p_62269_ + " for stat type " + BuiltInRegistries.STAT_TYPE.getKey(p_62268_));
      } else {
         return p_62268_.get(t);
      }
   }

   private static <T> ResourceLocation getStatValueId(Stat<T> p_62266_) {
      return p_62266_.getType().getRegistry().getKey(p_62266_.getValue());
   }

   public JsonObject serializeCustomData() {
      JsonObject jsonobject = new JsonObject();
      jsonobject.add("level", this.level.serializeToJson());
      if (this.gameType != null) {
         jsonobject.addProperty("gamemode", this.gameType.getName());
      }

      if (!this.stats.isEmpty()) {
         JsonArray jsonarray = new JsonArray();
         this.stats.forEach((p_222489_, p_222490_) -> {
            JsonObject jsonobject3 = new JsonObject();
            jsonobject3.addProperty("type", BuiltInRegistries.STAT_TYPE.getKey(p_222489_.getType()).toString());
            jsonobject3.addProperty("stat", getStatValueId(p_222489_).toString());
            jsonobject3.add("value", p_222490_.serializeToJson());
            jsonarray.add(jsonobject3);
         });
         jsonobject.add("stats", jsonarray);
      }

      if (!this.recipes.isEmpty()) {
         JsonObject jsonobject1 = new JsonObject();
         this.recipes.forEach((p_222499_, p_222500_) -> {
            jsonobject1.addProperty(p_222499_.toString(), p_222500_);
         });
         jsonobject.add("recipes", jsonobject1);
      }

      if (!this.advancements.isEmpty()) {
         JsonObject jsonobject2 = new JsonObject();
         this.advancements.forEach((p_222495_, p_222496_) -> {
            jsonobject2.add(p_222495_.toString(), p_222496_.toJson());
         });
         jsonobject.add("advancements", jsonobject2);
      }

      jsonobject.add("looking_at", this.lookingAt.serializeToJson());
      return jsonobject;
   }

   public EntitySubPredicate.Type type() {
      return EntitySubPredicate.Types.PLAYER;
   }

   static class AdvancementCriterionsPredicate implements PlayerPredicate.AdvancementPredicate {
      private final Object2BooleanMap<String> criterions;

      public AdvancementCriterionsPredicate(Object2BooleanMap<String> p_62293_) {
         this.criterions = p_62293_;
      }

      public JsonElement toJson() {
         JsonObject jsonobject = new JsonObject();
         this.criterions.forEach(jsonobject::addProperty);
         return jsonobject;
      }

      public boolean test(AdvancementProgress p_62296_) {
         for(Object2BooleanMap.Entry<String> entry : this.criterions.object2BooleanEntrySet()) {
            CriterionProgress criterionprogress = p_62296_.getCriterion(entry.getKey());
            if (criterionprogress == null || criterionprogress.isDone() != entry.getBooleanValue()) {
               return false;
            }
         }

         return true;
      }
   }

   static class AdvancementDonePredicate implements PlayerPredicate.AdvancementPredicate {
      private final boolean state;

      public AdvancementDonePredicate(boolean p_62301_) {
         this.state = p_62301_;
      }

      public JsonElement toJson() {
         return new JsonPrimitive(this.state);
      }

      public boolean test(AdvancementProgress p_62304_) {
         return p_62304_.isDone() == this.state;
      }
   }

   interface AdvancementPredicate extends Predicate<AdvancementProgress> {
      JsonElement toJson();
   }

   public static class Builder {
      private MinMaxBounds.Ints level = MinMaxBounds.Ints.ANY;
      @Nullable
      private GameType gameType;
      private final Map<Stat<?>, MinMaxBounds.Ints> stats = Maps.newHashMap();
      private final Object2BooleanMap<ResourceLocation> recipes = new Object2BooleanOpenHashMap<>();
      private final Map<ResourceLocation, PlayerPredicate.AdvancementPredicate> advancements = Maps.newHashMap();
      private EntityPredicate lookingAt = EntityPredicate.ANY;

      public static PlayerPredicate.Builder player() {
         return new PlayerPredicate.Builder();
      }

      public PlayerPredicate.Builder setLevel(MinMaxBounds.Ints p_156776_) {
         this.level = p_156776_;
         return this;
      }

      public PlayerPredicate.Builder addStat(Stat<?> p_156769_, MinMaxBounds.Ints p_156770_) {
         this.stats.put(p_156769_, p_156770_);
         return this;
      }

      public PlayerPredicate.Builder addRecipe(ResourceLocation p_156781_, boolean p_156782_) {
         this.recipes.put(p_156781_, p_156782_);
         return this;
      }

      public PlayerPredicate.Builder setGameType(GameType p_156774_) {
         this.gameType = p_156774_;
         return this;
      }

      public PlayerPredicate.Builder setLookingAt(EntityPredicate p_156772_) {
         this.lookingAt = p_156772_;
         return this;
      }

      public PlayerPredicate.Builder checkAdvancementDone(ResourceLocation p_156784_, boolean p_156785_) {
         this.advancements.put(p_156784_, new PlayerPredicate.AdvancementDonePredicate(p_156785_));
         return this;
      }

      public PlayerPredicate.Builder checkAdvancementCriterions(ResourceLocation p_156778_, Map<String, Boolean> p_156779_) {
         this.advancements.put(p_156778_, new PlayerPredicate.AdvancementCriterionsPredicate(new Object2BooleanOpenHashMap<>(p_156779_)));
         return this;
      }

      public PlayerPredicate build() {
         return new PlayerPredicate(this.level, this.gameType, this.stats, this.recipes, this.advancements, this.lookingAt);
      }
   }
}