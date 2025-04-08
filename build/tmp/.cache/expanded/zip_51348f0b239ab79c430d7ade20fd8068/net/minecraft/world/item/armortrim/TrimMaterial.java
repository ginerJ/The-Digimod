package net.minecraft.world.item.armortrim;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.Item;

public record TrimMaterial(String assetName, Holder<Item> ingredient, float itemModelIndex, Map<ArmorMaterials, String> overrideArmorMaterials, Component description) {
   public static final Codec<TrimMaterial> DIRECT_CODEC = RecordCodecBuilder.create((p_267935_) -> {
      return p_267935_.group(Codec.STRING.fieldOf("asset_name").forGetter(TrimMaterial::assetName), RegistryFixedCodec.create(Registries.ITEM).fieldOf("ingredient").forGetter(TrimMaterial::ingredient), Codec.FLOAT.fieldOf("item_model_index").forGetter(TrimMaterial::itemModelIndex), Codec.unboundedMap(ArmorMaterials.CODEC, Codec.STRING).optionalFieldOf("override_armor_materials", Map.of()).forGetter(TrimMaterial::overrideArmorMaterials), ExtraCodecs.COMPONENT.fieldOf("description").forGetter(TrimMaterial::description)).apply(p_267935_, TrimMaterial::new);
   });
   public static final Codec<Holder<TrimMaterial>> CODEC = RegistryFileCodec.create(Registries.TRIM_MATERIAL, DIRECT_CODEC);

   public static TrimMaterial create(String p_268108_, Item p_268361_, float p_268202_, Component p_268273_, Map<ArmorMaterials, String> p_267977_) {
      return new TrimMaterial(p_268108_, BuiltInRegistries.ITEM.wrapAsHolder(p_268361_), p_268202_, p_267977_, p_268273_);
   }
}