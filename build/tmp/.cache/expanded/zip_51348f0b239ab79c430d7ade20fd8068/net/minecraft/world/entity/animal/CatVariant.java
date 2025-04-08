package net.minecraft.world.entity.animal;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public record CatVariant(ResourceLocation texture) {
   public static final ResourceKey<CatVariant> TABBY = createKey("tabby");
   public static final ResourceKey<CatVariant> BLACK = createKey("black");
   public static final ResourceKey<CatVariant> RED = createKey("red");
   public static final ResourceKey<CatVariant> SIAMESE = createKey("siamese");
   public static final ResourceKey<CatVariant> BRITISH_SHORTHAIR = createKey("british_shorthair");
   public static final ResourceKey<CatVariant> CALICO = createKey("calico");
   public static final ResourceKey<CatVariant> PERSIAN = createKey("persian");
   public static final ResourceKey<CatVariant> RAGDOLL = createKey("ragdoll");
   public static final ResourceKey<CatVariant> WHITE = createKey("white");
   public static final ResourceKey<CatVariant> JELLIE = createKey("jellie");
   public static final ResourceKey<CatVariant> ALL_BLACK = createKey("all_black");

   private static ResourceKey<CatVariant> createKey(String p_256044_) {
      return ResourceKey.create(Registries.CAT_VARIANT, new ResourceLocation(p_256044_));
   }

   public static CatVariant bootstrap(Registry<CatVariant> p_256435_) {
      register(p_256435_, TABBY, "textures/entity/cat/tabby.png");
      register(p_256435_, BLACK, "textures/entity/cat/black.png");
      register(p_256435_, RED, "textures/entity/cat/red.png");
      register(p_256435_, SIAMESE, "textures/entity/cat/siamese.png");
      register(p_256435_, BRITISH_SHORTHAIR, "textures/entity/cat/british_shorthair.png");
      register(p_256435_, CALICO, "textures/entity/cat/calico.png");
      register(p_256435_, PERSIAN, "textures/entity/cat/persian.png");
      register(p_256435_, RAGDOLL, "textures/entity/cat/ragdoll.png");
      register(p_256435_, WHITE, "textures/entity/cat/white.png");
      register(p_256435_, JELLIE, "textures/entity/cat/jellie.png");
      return register(p_256435_, ALL_BLACK, "textures/entity/cat/all_black.png");
   }

   private static CatVariant register(Registry<CatVariant> p_255735_, ResourceKey<CatVariant> p_256159_, String p_256466_) {
      return Registry.register(p_255735_, p_256159_, new CatVariant(new ResourceLocation(p_256466_)));
   }
}