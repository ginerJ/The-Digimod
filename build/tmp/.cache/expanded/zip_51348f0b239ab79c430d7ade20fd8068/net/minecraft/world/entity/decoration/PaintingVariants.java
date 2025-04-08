package net.minecraft.world.entity.decoration;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public class PaintingVariants {
   public static final ResourceKey<PaintingVariant> KEBAB = create("kebab");
   public static final ResourceKey<PaintingVariant> AZTEC = create("aztec");
   public static final ResourceKey<PaintingVariant> ALBAN = create("alban");
   public static final ResourceKey<PaintingVariant> AZTEC2 = create("aztec2");
   public static final ResourceKey<PaintingVariant> BOMB = create("bomb");
   public static final ResourceKey<PaintingVariant> PLANT = create("plant");
   public static final ResourceKey<PaintingVariant> WASTELAND = create("wasteland");
   public static final ResourceKey<PaintingVariant> POOL = create("pool");
   public static final ResourceKey<PaintingVariant> COURBET = create("courbet");
   public static final ResourceKey<PaintingVariant> SEA = create("sea");
   public static final ResourceKey<PaintingVariant> SUNSET = create("sunset");
   public static final ResourceKey<PaintingVariant> CREEBET = create("creebet");
   public static final ResourceKey<PaintingVariant> WANDERER = create("wanderer");
   public static final ResourceKey<PaintingVariant> GRAHAM = create("graham");
   public static final ResourceKey<PaintingVariant> MATCH = create("match");
   public static final ResourceKey<PaintingVariant> BUST = create("bust");
   public static final ResourceKey<PaintingVariant> STAGE = create("stage");
   public static final ResourceKey<PaintingVariant> VOID = create("void");
   public static final ResourceKey<PaintingVariant> SKULL_AND_ROSES = create("skull_and_roses");
   public static final ResourceKey<PaintingVariant> WITHER = create("wither");
   public static final ResourceKey<PaintingVariant> FIGHTERS = create("fighters");
   public static final ResourceKey<PaintingVariant> POINTER = create("pointer");
   public static final ResourceKey<PaintingVariant> PIGSCENE = create("pigscene");
   public static final ResourceKey<PaintingVariant> BURNING_SKULL = create("burning_skull");
   public static final ResourceKey<PaintingVariant> SKELETON = create("skeleton");
   public static final ResourceKey<PaintingVariant> DONKEY_KONG = create("donkey_kong");
   public static final ResourceKey<PaintingVariant> EARTH = create("earth");
   public static final ResourceKey<PaintingVariant> WIND = create("wind");
   public static final ResourceKey<PaintingVariant> WATER = create("water");
   public static final ResourceKey<PaintingVariant> FIRE = create("fire");

   public static PaintingVariant bootstrap(Registry<PaintingVariant> p_218943_) {
      Registry.register(p_218943_, KEBAB, new PaintingVariant(16, 16));
      Registry.register(p_218943_, AZTEC, new PaintingVariant(16, 16));
      Registry.register(p_218943_, ALBAN, new PaintingVariant(16, 16));
      Registry.register(p_218943_, AZTEC2, new PaintingVariant(16, 16));
      Registry.register(p_218943_, BOMB, new PaintingVariant(16, 16));
      Registry.register(p_218943_, PLANT, new PaintingVariant(16, 16));
      Registry.register(p_218943_, WASTELAND, new PaintingVariant(16, 16));
      Registry.register(p_218943_, POOL, new PaintingVariant(32, 16));
      Registry.register(p_218943_, COURBET, new PaintingVariant(32, 16));
      Registry.register(p_218943_, SEA, new PaintingVariant(32, 16));
      Registry.register(p_218943_, SUNSET, new PaintingVariant(32, 16));
      Registry.register(p_218943_, CREEBET, new PaintingVariant(32, 16));
      Registry.register(p_218943_, WANDERER, new PaintingVariant(16, 32));
      Registry.register(p_218943_, GRAHAM, new PaintingVariant(16, 32));
      Registry.register(p_218943_, MATCH, new PaintingVariant(32, 32));
      Registry.register(p_218943_, BUST, new PaintingVariant(32, 32));
      Registry.register(p_218943_, STAGE, new PaintingVariant(32, 32));
      Registry.register(p_218943_, VOID, new PaintingVariant(32, 32));
      Registry.register(p_218943_, SKULL_AND_ROSES, new PaintingVariant(32, 32));
      Registry.register(p_218943_, WITHER, new PaintingVariant(32, 32));
      Registry.register(p_218943_, FIGHTERS, new PaintingVariant(64, 32));
      Registry.register(p_218943_, POINTER, new PaintingVariant(64, 64));
      Registry.register(p_218943_, PIGSCENE, new PaintingVariant(64, 64));
      Registry.register(p_218943_, BURNING_SKULL, new PaintingVariant(64, 64));
      Registry.register(p_218943_, SKELETON, new PaintingVariant(64, 48));
      Registry.register(p_218943_, EARTH, new PaintingVariant(32, 32));
      Registry.register(p_218943_, WIND, new PaintingVariant(32, 32));
      Registry.register(p_218943_, WATER, new PaintingVariant(32, 32));
      Registry.register(p_218943_, FIRE, new PaintingVariant(32, 32));
      return Registry.register(p_218943_, DONKEY_KONG, new PaintingVariant(64, 48));
   }

   private static ResourceKey<PaintingVariant> create(String p_218945_) {
      return ResourceKey.create(Registries.PAINTING_VARIANT, new ResourceLocation(p_218945_));
   }
}