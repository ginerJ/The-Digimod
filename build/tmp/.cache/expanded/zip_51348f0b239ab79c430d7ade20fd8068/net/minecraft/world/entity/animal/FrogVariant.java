package net.minecraft.world.entity.animal;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

public record FrogVariant(ResourceLocation texture) {
   public static final FrogVariant TEMPERATE = register("temperate", "textures/entity/frog/temperate_frog.png");
   public static final FrogVariant WARM = register("warm", "textures/entity/frog/warm_frog.png");
   public static final FrogVariant COLD = register("cold", "textures/entity/frog/cold_frog.png");

   private static FrogVariant register(String p_218194_, String p_218195_) {
      return Registry.register(BuiltInRegistries.FROG_VARIANT, p_218194_, new FrogVariant(new ResourceLocation(p_218195_)));
   }
}