package net.minecraft.world.level.block.entity;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public class BannerPatterns {
   public static final ResourceKey<BannerPattern> BASE = create("base");
   public static final ResourceKey<BannerPattern> SQUARE_BOTTOM_LEFT = create("square_bottom_left");
   public static final ResourceKey<BannerPattern> SQUARE_BOTTOM_RIGHT = create("square_bottom_right");
   public static final ResourceKey<BannerPattern> SQUARE_TOP_LEFT = create("square_top_left");
   public static final ResourceKey<BannerPattern> SQUARE_TOP_RIGHT = create("square_top_right");
   public static final ResourceKey<BannerPattern> STRIPE_BOTTOM = create("stripe_bottom");
   public static final ResourceKey<BannerPattern> STRIPE_TOP = create("stripe_top");
   public static final ResourceKey<BannerPattern> STRIPE_LEFT = create("stripe_left");
   public static final ResourceKey<BannerPattern> STRIPE_RIGHT = create("stripe_right");
   public static final ResourceKey<BannerPattern> STRIPE_CENTER = create("stripe_center");
   public static final ResourceKey<BannerPattern> STRIPE_MIDDLE = create("stripe_middle");
   public static final ResourceKey<BannerPattern> STRIPE_DOWNRIGHT = create("stripe_downright");
   public static final ResourceKey<BannerPattern> STRIPE_DOWNLEFT = create("stripe_downleft");
   public static final ResourceKey<BannerPattern> STRIPE_SMALL = create("small_stripes");
   public static final ResourceKey<BannerPattern> CROSS = create("cross");
   public static final ResourceKey<BannerPattern> STRAIGHT_CROSS = create("straight_cross");
   public static final ResourceKey<BannerPattern> TRIANGLE_BOTTOM = create("triangle_bottom");
   public static final ResourceKey<BannerPattern> TRIANGLE_TOP = create("triangle_top");
   public static final ResourceKey<BannerPattern> TRIANGLES_BOTTOM = create("triangles_bottom");
   public static final ResourceKey<BannerPattern> TRIANGLES_TOP = create("triangles_top");
   public static final ResourceKey<BannerPattern> DIAGONAL_LEFT = create("diagonal_left");
   public static final ResourceKey<BannerPattern> DIAGONAL_RIGHT = create("diagonal_up_right");
   public static final ResourceKey<BannerPattern> DIAGONAL_LEFT_MIRROR = create("diagonal_up_left");
   public static final ResourceKey<BannerPattern> DIAGONAL_RIGHT_MIRROR = create("diagonal_right");
   public static final ResourceKey<BannerPattern> CIRCLE_MIDDLE = create("circle");
   public static final ResourceKey<BannerPattern> RHOMBUS_MIDDLE = create("rhombus");
   public static final ResourceKey<BannerPattern> HALF_VERTICAL = create("half_vertical");
   public static final ResourceKey<BannerPattern> HALF_HORIZONTAL = create("half_horizontal");
   public static final ResourceKey<BannerPattern> HALF_VERTICAL_MIRROR = create("half_vertical_right");
   public static final ResourceKey<BannerPattern> HALF_HORIZONTAL_MIRROR = create("half_horizontal_bottom");
   public static final ResourceKey<BannerPattern> BORDER = create("border");
   public static final ResourceKey<BannerPattern> CURLY_BORDER = create("curly_border");
   public static final ResourceKey<BannerPattern> GRADIENT = create("gradient");
   public static final ResourceKey<BannerPattern> GRADIENT_UP = create("gradient_up");
   public static final ResourceKey<BannerPattern> BRICKS = create("bricks");
   public static final ResourceKey<BannerPattern> GLOBE = create("globe");
   public static final ResourceKey<BannerPattern> CREEPER = create("creeper");
   public static final ResourceKey<BannerPattern> SKULL = create("skull");
   public static final ResourceKey<BannerPattern> FLOWER = create("flower");
   public static final ResourceKey<BannerPattern> MOJANG = create("mojang");
   public static final ResourceKey<BannerPattern> PIGLIN = create("piglin");

   private static ResourceKey<BannerPattern> create(String p_222757_) {
      return ResourceKey.create(Registries.BANNER_PATTERN, new ResourceLocation(p_222757_));
   }

   public static BannerPattern bootstrap(Registry<BannerPattern> p_222755_) {
      Registry.register(p_222755_, BASE, new BannerPattern("b"));
      Registry.register(p_222755_, SQUARE_BOTTOM_LEFT, new BannerPattern("bl"));
      Registry.register(p_222755_, SQUARE_BOTTOM_RIGHT, new BannerPattern("br"));
      Registry.register(p_222755_, SQUARE_TOP_LEFT, new BannerPattern("tl"));
      Registry.register(p_222755_, SQUARE_TOP_RIGHT, new BannerPattern("tr"));
      Registry.register(p_222755_, STRIPE_BOTTOM, new BannerPattern("bs"));
      Registry.register(p_222755_, STRIPE_TOP, new BannerPattern("ts"));
      Registry.register(p_222755_, STRIPE_LEFT, new BannerPattern("ls"));
      Registry.register(p_222755_, STRIPE_RIGHT, new BannerPattern("rs"));
      Registry.register(p_222755_, STRIPE_CENTER, new BannerPattern("cs"));
      Registry.register(p_222755_, STRIPE_MIDDLE, new BannerPattern("ms"));
      Registry.register(p_222755_, STRIPE_DOWNRIGHT, new BannerPattern("drs"));
      Registry.register(p_222755_, STRIPE_DOWNLEFT, new BannerPattern("dls"));
      Registry.register(p_222755_, STRIPE_SMALL, new BannerPattern("ss"));
      Registry.register(p_222755_, CROSS, new BannerPattern("cr"));
      Registry.register(p_222755_, STRAIGHT_CROSS, new BannerPattern("sc"));
      Registry.register(p_222755_, TRIANGLE_BOTTOM, new BannerPattern("bt"));
      Registry.register(p_222755_, TRIANGLE_TOP, new BannerPattern("tt"));
      Registry.register(p_222755_, TRIANGLES_BOTTOM, new BannerPattern("bts"));
      Registry.register(p_222755_, TRIANGLES_TOP, new BannerPattern("tts"));
      Registry.register(p_222755_, DIAGONAL_LEFT, new BannerPattern("ld"));
      Registry.register(p_222755_, DIAGONAL_RIGHT, new BannerPattern("rd"));
      Registry.register(p_222755_, DIAGONAL_LEFT_MIRROR, new BannerPattern("lud"));
      Registry.register(p_222755_, DIAGONAL_RIGHT_MIRROR, new BannerPattern("rud"));
      Registry.register(p_222755_, CIRCLE_MIDDLE, new BannerPattern("mc"));
      Registry.register(p_222755_, RHOMBUS_MIDDLE, new BannerPattern("mr"));
      Registry.register(p_222755_, HALF_VERTICAL, new BannerPattern("vh"));
      Registry.register(p_222755_, HALF_HORIZONTAL, new BannerPattern("hh"));
      Registry.register(p_222755_, HALF_VERTICAL_MIRROR, new BannerPattern("vhr"));
      Registry.register(p_222755_, HALF_HORIZONTAL_MIRROR, new BannerPattern("hhb"));
      Registry.register(p_222755_, BORDER, new BannerPattern("bo"));
      Registry.register(p_222755_, CURLY_BORDER, new BannerPattern("cbo"));
      Registry.register(p_222755_, GRADIENT, new BannerPattern("gra"));
      Registry.register(p_222755_, GRADIENT_UP, new BannerPattern("gru"));
      Registry.register(p_222755_, BRICKS, new BannerPattern("bri"));
      Registry.register(p_222755_, GLOBE, new BannerPattern("glb"));
      Registry.register(p_222755_, CREEPER, new BannerPattern("cre"));
      Registry.register(p_222755_, SKULL, new BannerPattern("sku"));
      Registry.register(p_222755_, FLOWER, new BannerPattern("flo"));
      Registry.register(p_222755_, MOJANG, new BannerPattern("moj"));
      return Registry.register(p_222755_, PIGLIN, new BannerPattern("pig"));
   }
}