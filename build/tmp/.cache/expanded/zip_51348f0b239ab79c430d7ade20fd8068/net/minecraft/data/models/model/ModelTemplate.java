package net.minecraft.data.models.model;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Streams;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

public class ModelTemplate {
   private final Optional<ResourceLocation> model;
   private final Set<TextureSlot> requiredSlots;
   private final Optional<String> suffix;

   public ModelTemplate(Optional<ResourceLocation> p_125589_, Optional<String> p_125590_, TextureSlot... p_125591_) {
      this.model = p_125589_;
      this.suffix = p_125590_;
      this.requiredSlots = ImmutableSet.copyOf(p_125591_);
   }

   public ResourceLocation create(Block p_125593_, TextureMapping p_125594_, BiConsumer<ResourceLocation, Supplier<JsonElement>> p_125595_) {
      return this.create(ModelLocationUtils.getModelLocation(p_125593_, this.suffix.orElse("")), p_125594_, p_125595_);
   }

   public ResourceLocation createWithSuffix(Block p_125597_, String p_125598_, TextureMapping p_125599_, BiConsumer<ResourceLocation, Supplier<JsonElement>> p_125600_) {
      return this.create(ModelLocationUtils.getModelLocation(p_125597_, p_125598_ + (String)this.suffix.orElse("")), p_125599_, p_125600_);
   }

   public ResourceLocation createWithOverride(Block p_125617_, String p_125618_, TextureMapping p_125619_, BiConsumer<ResourceLocation, Supplier<JsonElement>> p_125620_) {
      return this.create(ModelLocationUtils.getModelLocation(p_125617_, p_125618_), p_125619_, p_125620_);
   }

   public ResourceLocation create(ResourceLocation p_125613_, TextureMapping p_125614_, BiConsumer<ResourceLocation, Supplier<JsonElement>> p_125615_) {
      return this.create(p_125613_, p_125614_, p_125615_, this::createBaseTemplate);
   }

   public ResourceLocation create(ResourceLocation p_266990_, TextureMapping p_267329_, BiConsumer<ResourceLocation, Supplier<JsonElement>> p_266768_, ModelTemplate.JsonFactory p_266906_) {
      Map<TextureSlot, ResourceLocation> map = this.createMap(p_267329_);
      p_266768_.accept(p_266990_, () -> {
         return p_266906_.create(p_266990_, map);
      });
      return p_266990_;
   }

   public JsonObject createBaseTemplate(ResourceLocation p_266830_, Map<TextureSlot, ResourceLocation> p_266912_) {
      JsonObject jsonobject = new JsonObject();
      this.model.ifPresent((p_176461_) -> {
         jsonobject.addProperty("parent", p_176461_.toString());
      });
      if (!p_266912_.isEmpty()) {
         JsonObject jsonobject1 = new JsonObject();
         p_266912_.forEach((p_176457_, p_176458_) -> {
            jsonobject1.addProperty(p_176457_.getId(), p_176458_.toString());
         });
         jsonobject.add("textures", jsonobject1);
      }

      return jsonobject;
   }

   private Map<TextureSlot, ResourceLocation> createMap(TextureMapping p_125609_) {
      return Streams.concat(this.requiredSlots.stream(), p_125609_.getForced()).collect(ImmutableMap.toImmutableMap(Function.identity(), p_125609_::get));
   }

   public interface JsonFactory {
      JsonObject create(ResourceLocation p_266987_, Map<TextureSlot, ResourceLocation> p_266933_);
   }
}