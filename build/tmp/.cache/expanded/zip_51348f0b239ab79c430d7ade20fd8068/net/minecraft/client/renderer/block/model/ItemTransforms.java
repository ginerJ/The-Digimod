package net.minecraft.client.renderer.block.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ItemTransforms {
   public static final ItemTransforms NO_TRANSFORMS = new ItemTransforms();
   public final ItemTransform thirdPersonLeftHand;
   public final ItemTransform thirdPersonRightHand;
   public final ItemTransform firstPersonLeftHand;
   public final ItemTransform firstPersonRightHand;
   public final ItemTransform head;
   public final ItemTransform gui;
   public final ItemTransform ground;
   public final ItemTransform fixed;
   public final com.google.common.collect.ImmutableMap<ItemDisplayContext, ItemTransform> moddedTransforms;

   private ItemTransforms() {
      this(ItemTransform.NO_TRANSFORM, ItemTransform.NO_TRANSFORM, ItemTransform.NO_TRANSFORM, ItemTransform.NO_TRANSFORM, ItemTransform.NO_TRANSFORM, ItemTransform.NO_TRANSFORM, ItemTransform.NO_TRANSFORM, ItemTransform.NO_TRANSFORM);
   }

   @Deprecated
   public ItemTransforms(ItemTransforms p_111807_) {
      this.thirdPersonLeftHand = p_111807_.thirdPersonLeftHand;
      this.thirdPersonRightHand = p_111807_.thirdPersonRightHand;
      this.firstPersonLeftHand = p_111807_.firstPersonLeftHand;
      this.firstPersonRightHand = p_111807_.firstPersonRightHand;
      this.head = p_111807_.head;
      this.gui = p_111807_.gui;
      this.ground = p_111807_.ground;
      this.fixed = p_111807_.fixed;
      this.moddedTransforms = p_111807_.moddedTransforms;
   }

   @Deprecated
   public ItemTransforms(ItemTransform p_111798_, ItemTransform p_111799_, ItemTransform p_111800_, ItemTransform p_111801_, ItemTransform p_111802_, ItemTransform p_111803_, ItemTransform p_111804_, ItemTransform p_111805_) {
      this(p_111798_, p_111799_, p_111800_, p_111801_, p_111802_, p_111803_, p_111804_, p_111805_, com.google.common.collect.ImmutableMap.of());
   }

   public ItemTransforms(ItemTransform p_111798_, ItemTransform p_111799_, ItemTransform p_111800_, ItemTransform p_111801_, ItemTransform p_111802_, ItemTransform p_111803_, ItemTransform p_111804_, ItemTransform p_111805_,
           com.google.common.collect.ImmutableMap<ItemDisplayContext, ItemTransform> moddedTransforms) {
      this.thirdPersonLeftHand = p_111798_;
      this.thirdPersonRightHand = p_111799_;
      this.firstPersonLeftHand = p_111800_;
      this.firstPersonRightHand = p_111801_;
      this.head = p_111802_;
      this.gui = p_111803_;
      this.ground = p_111804_;
      this.fixed = p_111805_;
      this.moddedTransforms = moddedTransforms;
   }

   public ItemTransform getTransform(ItemDisplayContext p_270619_) {
      ItemTransform itemtransform;
      switch (p_270619_) {
         case THIRD_PERSON_LEFT_HAND:
            itemtransform = this.thirdPersonLeftHand;
            break;
         case THIRD_PERSON_RIGHT_HAND:
            itemtransform = this.thirdPersonRightHand;
            break;
         case FIRST_PERSON_LEFT_HAND:
            itemtransform = this.firstPersonLeftHand;
            break;
         case FIRST_PERSON_RIGHT_HAND:
            itemtransform = this.firstPersonRightHand;
            break;
         case HEAD:
            itemtransform = this.head;
            break;
         case GUI:
            itemtransform = this.gui;
            break;
         case GROUND:
            itemtransform = this.ground;
            break;
         case FIXED:
            itemtransform = this.fixed;
            break;
         default:
            return moddedTransforms.getOrDefault(p_270619_, ItemTransform.NO_TRANSFORM);
      }

      return itemtransform;
   }

   public boolean hasTransform(ItemDisplayContext p_270365_) {
      return this.getTransform(p_270365_) != ItemTransform.NO_TRANSFORM;
   }

   @OnlyIn(Dist.CLIENT)
   public static class Deserializer implements JsonDeserializer<ItemTransforms> {
      public ItemTransforms deserialize(JsonElement p_111820_, Type p_111821_, JsonDeserializationContext p_111822_) throws JsonParseException {
         JsonObject jsonobject = p_111820_.getAsJsonObject();
         ItemTransform itemtransform = this.getTransform(p_111822_, jsonobject, ItemDisplayContext.THIRD_PERSON_RIGHT_HAND);
         ItemTransform itemtransform1 = this.getTransform(p_111822_, jsonobject, ItemDisplayContext.THIRD_PERSON_LEFT_HAND);
         if (itemtransform1 == ItemTransform.NO_TRANSFORM) {
            itemtransform1 = itemtransform;
         }

         ItemTransform itemtransform2 = this.getTransform(p_111822_, jsonobject, ItemDisplayContext.FIRST_PERSON_RIGHT_HAND);
         ItemTransform itemtransform3 = this.getTransform(p_111822_, jsonobject, ItemDisplayContext.FIRST_PERSON_LEFT_HAND);
         if (itemtransform3 == ItemTransform.NO_TRANSFORM) {
            itemtransform3 = itemtransform2;
         }

         ItemTransform itemtransform4 = this.getTransform(p_111822_, jsonobject, ItemDisplayContext.HEAD);
         ItemTransform itemtransform5 = this.getTransform(p_111822_, jsonobject, ItemDisplayContext.GUI);
         ItemTransform itemtransform6 = this.getTransform(p_111822_, jsonobject, ItemDisplayContext.GROUND);
         ItemTransform itemtransform7 = this.getTransform(p_111822_, jsonobject, ItemDisplayContext.FIXED);

         var builder = com.google.common.collect.ImmutableMap.<ItemDisplayContext, ItemTransform>builder();
         for (ItemDisplayContext type : ItemDisplayContext.values()) {
            if (type.isModded()) {
               var transform = this.getTransform(p_111822_, jsonobject, type);
               var fallbackType = type;
               while (transform == ItemTransform.NO_TRANSFORM && fallbackType.fallback() != null) {
                  fallbackType = fallbackType.fallback();
                  transform = this.getTransform(p_111822_, jsonobject, fallbackType);
               }
               if (transform != ItemTransform.NO_TRANSFORM){
                  builder.put(type, transform);
               }
            }
         }

         return new ItemTransforms(itemtransform1, itemtransform, itemtransform3, itemtransform2, itemtransform4, itemtransform5, itemtransform6, itemtransform7, builder.build());
      }

      private ItemTransform getTransform(JsonDeserializationContext p_270385_, JsonObject p_270436_, ItemDisplayContext p_270100_) {
         String s = p_270100_.getSerializedName();
         return p_270436_.has(s) ? p_270385_.deserialize(p_270436_.get(s), ItemTransform.class) : ItemTransform.NO_TRANSFORM;
      }
   }
}
