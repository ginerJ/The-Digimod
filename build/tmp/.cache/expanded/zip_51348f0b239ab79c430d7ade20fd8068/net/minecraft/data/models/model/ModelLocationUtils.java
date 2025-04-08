package net.minecraft.data.models.model;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class ModelLocationUtils {
   /** @deprecated */
   @Deprecated
   public static ResourceLocation decorateBlockModelLocation(String p_125582_) {
      return new ResourceLocation("minecraft", "block/" + p_125582_);
   }

   public static ResourceLocation decorateItemModelLocation(String p_125584_) {
      return new ResourceLocation("minecraft", "item/" + p_125584_);
   }

   public static ResourceLocation getModelLocation(Block p_125579_, String p_125580_) {
      ResourceLocation resourcelocation = BuiltInRegistries.BLOCK.getKey(p_125579_);
      return resourcelocation.withPath((p_251253_) -> {
         return "block/" + p_251253_ + p_125580_;
      });
   }

   public static ResourceLocation getModelLocation(Block p_125577_) {
      ResourceLocation resourcelocation = BuiltInRegistries.BLOCK.getKey(p_125577_);
      return resourcelocation.withPrefix("block/");
   }

   public static ResourceLocation getModelLocation(Item p_125572_) {
      ResourceLocation resourcelocation = BuiltInRegistries.ITEM.getKey(p_125572_);
      return resourcelocation.withPrefix("item/");
   }

   public static ResourceLocation getModelLocation(Item p_125574_, String p_125575_) {
      ResourceLocation resourcelocation = BuiltInRegistries.ITEM.getKey(p_125574_);
      return resourcelocation.withPath((p_251542_) -> {
         return "item/" + p_251542_ + p_125575_;
      });
   }
}