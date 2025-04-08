package net.minecraft.advancements.critereon;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import java.util.Optional;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.Registry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class EntityVariantPredicate<V> {
   private static final String VARIANT_KEY = "variant";
   final Codec<V> variantCodec;
   final Function<Entity, Optional<V>> getter;
   final EntitySubPredicate.Type type;

   public static <V> EntityVariantPredicate<V> create(Registry<V> p_219094_, Function<Entity, Optional<V>> p_219095_) {
      return new EntityVariantPredicate<>(p_219094_.byNameCodec(), p_219095_);
   }

   public static <V> EntityVariantPredicate<V> create(Codec<V> p_262671_, Function<Entity, Optional<V>> p_262652_) {
      return new EntityVariantPredicate<>(p_262671_, p_262652_);
   }

   private EntityVariantPredicate(Codec<V> p_262574_, Function<Entity, Optional<V>> p_262610_) {
      this.variantCodec = p_262574_;
      this.getter = p_262610_;
      this.type = (p_262519_) -> {
         JsonElement jsonelement = p_262519_.get("variant");
         if (jsonelement == null) {
            throw new JsonParseException("Missing variant field");
         } else {
            V v = Util.getOrThrow(p_262574_.decode(new Dynamic<>(JsonOps.INSTANCE, jsonelement)), JsonParseException::new).getFirst();
            return this.createPredicate(v);
         }
      };
   }

   public EntitySubPredicate.Type type() {
      return this.type;
   }

   public EntitySubPredicate createPredicate(final V p_219097_) {
      return new EntitySubPredicate() {
         public boolean matches(Entity p_219105_, ServerLevel p_219106_, @Nullable Vec3 p_219107_) {
            return EntityVariantPredicate.this.getter.apply(p_219105_).filter((p_219110_) -> {
               return p_219110_.equals(p_219097_);
            }).isPresent();
         }

         public JsonObject serializeCustomData() {
            JsonObject jsonobject = new JsonObject();
            jsonobject.add("variant", Util.getOrThrow(EntityVariantPredicate.this.variantCodec.encodeStart(JsonOps.INSTANCE, p_219097_), (p_262521_) -> {
               return new JsonParseException("Can't serialize variant " + p_219097_ + ", message " + p_262521_);
            }));
            return jsonobject;
         }

         public EntitySubPredicate.Type type() {
            return EntityVariantPredicate.this.type;
         }
      };
   }
}