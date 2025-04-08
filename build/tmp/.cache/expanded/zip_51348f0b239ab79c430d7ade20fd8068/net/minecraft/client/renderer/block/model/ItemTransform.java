package net.minecraft.client.renderer.block.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.blaze3d.vertex.PoseStack;
import java.lang.reflect.Type;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector3fc;

@OnlyIn(Dist.CLIENT)
public class ItemTransform {
   public static final ItemTransform NO_TRANSFORM = new ItemTransform(new Vector3f(), new Vector3f(), new Vector3f(1.0F, 1.0F, 1.0F));
   public final Vector3f rotation;
   public final Vector3f translation;
   public final Vector3f scale;
   public final Vector3f rightRotation;

   public ItemTransform(Vector3f p_254427_, Vector3f p_254496_, Vector3f p_254022_) {
      this(p_254427_, p_254496_, p_254022_, new Vector3f());
   }

   public ItemTransform(Vector3f p_254427_, Vector3f p_254496_, Vector3f p_254022_, Vector3f rightRotation) {
      this.rotation = new Vector3f((Vector3fc)p_254427_);
      this.translation = new Vector3f((Vector3fc)p_254496_);
      this.scale = new Vector3f((Vector3fc)p_254022_);
      this.rightRotation = new Vector3f(rightRotation);
   }

   public void apply(boolean p_111764_, PoseStack p_111765_) {
      if (this != NO_TRANSFORM) {
         float f = this.rotation.x();
         float f1 = this.rotation.y();
         float f2 = this.rotation.z();
         if (p_111764_) {
            f1 = -f1;
            f2 = -f2;
         }

         int i = p_111764_ ? -1 : 1;
         p_111765_.translate((float)i * this.translation.x(), this.translation.y(), this.translation.z());
         p_111765_.mulPose((new Quaternionf()).rotationXYZ(f * ((float)Math.PI / 180F), f1 * ((float)Math.PI / 180F), f2 * ((float)Math.PI / 180F)));
         p_111765_.scale(this.scale.x(), this.scale.y(), this.scale.z());
         p_111765_.mulPose(net.minecraftforge.common.util.TransformationHelper.quatFromXYZ(rightRotation.x(), rightRotation.y() * (p_111764_ ? -1 : 1), rightRotation.z() * (p_111764_ ? -1 : 1), true));
      }
   }

   public boolean equals(Object p_111767_) {
      if (this == p_111767_) {
         return true;
      } else if (this.getClass() != p_111767_.getClass()) {
         return false;
      } else {
         ItemTransform itemtransform = (ItemTransform)p_111767_;
         return this.rotation.equals(itemtransform.rotation) && this.scale.equals(itemtransform.scale) && this.translation.equals(itemtransform.translation);
      }
   }

   public int hashCode() {
      int i = this.rotation.hashCode();
      i = 31 * i + this.translation.hashCode();
      return 31 * i + this.scale.hashCode();
   }

   @OnlyIn(Dist.CLIENT)
   public static class Deserializer implements JsonDeserializer<ItemTransform> {
      public static final Vector3f DEFAULT_ROTATION = new Vector3f(0.0F, 0.0F, 0.0F);
      public static final Vector3f DEFAULT_TRANSLATION = new Vector3f(0.0F, 0.0F, 0.0F);
      public static final Vector3f DEFAULT_SCALE = new Vector3f(1.0F, 1.0F, 1.0F);
      public static final float MAX_TRANSLATION = 5.0F;
      public static final float MAX_SCALE = 4.0F;

      public ItemTransform deserialize(JsonElement p_111775_, Type p_111776_, JsonDeserializationContext p_111777_) throws JsonParseException {
         JsonObject jsonobject = p_111775_.getAsJsonObject();
         Vector3f vector3f = this.getVector3f(jsonobject, "rotation", DEFAULT_ROTATION);
         Vector3f vector3f1 = this.getVector3f(jsonobject, "translation", DEFAULT_TRANSLATION);
         vector3f1.mul(0.0625F);
         vector3f1.set(Mth.clamp(vector3f1.x, -5.0F, 5.0F), Mth.clamp(vector3f1.y, -5.0F, 5.0F), Mth.clamp(vector3f1.z, -5.0F, 5.0F));
         Vector3f vector3f2 = this.getVector3f(jsonobject, "scale", DEFAULT_SCALE);
         vector3f2.set(Mth.clamp(vector3f2.x, -4.0F, 4.0F), Mth.clamp(vector3f2.y, -4.0F, 4.0F), Mth.clamp(vector3f2.z, -4.0F, 4.0F));
         Vector3f rightRotation = this.getVector3f(jsonobject, "right_rotation", DEFAULT_ROTATION);
         return new ItemTransform(vector3f, vector3f1, vector3f2, rightRotation);
      }

      private Vector3f getVector3f(JsonObject p_111779_, String p_111780_, Vector3f p_253777_) {
         if (!p_111779_.has(p_111780_)) {
            return p_253777_;
         } else {
            JsonArray jsonarray = GsonHelper.getAsJsonArray(p_111779_, p_111780_);
            if (jsonarray.size() != 3) {
               throw new JsonParseException("Expected 3 " + p_111780_ + " values, found: " + jsonarray.size());
            } else {
               float[] afloat = new float[3];

               for(int i = 0; i < afloat.length; ++i) {
                  afloat[i] = GsonHelper.convertToFloat(jsonarray.get(i), p_111780_ + "[" + i + "]");
               }

               return new Vector3f(afloat[0], afloat[1], afloat[2]);
            }
         }
      }
   }
}
