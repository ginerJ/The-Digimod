package net.minecraft.world.level.block.entity;

import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public class DecoratedPotPatterns {
   private static final String BASE_NAME = "decorated_pot_base";
   public static final ResourceKey<String> BASE = create("decorated_pot_base");
   private static final String BRICK_NAME = "decorated_pot_side";
   private static final String ANGLER_NAME = "angler_pottery_pattern";
   private static final String ARCHER_NAME = "archer_pottery_pattern";
   private static final String ARMS_UP_NAME = "arms_up_pottery_pattern";
   private static final String BLADE_NAME = "blade_pottery_pattern";
   private static final String BREWER_NAME = "brewer_pottery_pattern";
   private static final String BURN_NAME = "burn_pottery_pattern";
   private static final String DANGER_NAME = "danger_pottery_pattern";
   private static final String EXPLORER_NAME = "explorer_pottery_pattern";
   private static final String FRIEND_NAME = "friend_pottery_pattern";
   private static final String HEART_NAME = "heart_pottery_pattern";
   private static final String HEARTBREAK_NAME = "heartbreak_pottery_pattern";
   private static final String HOWL_NAME = "howl_pottery_pattern";
   private static final String MINER_NAME = "miner_pottery_pattern";
   private static final String MOURNER_NAME = "mourner_pottery_pattern";
   private static final String PLENTY_NAME = "plenty_pottery_pattern";
   private static final String PRIZE_NAME = "prize_pottery_pattern";
   private static final String SHEAF_NAME = "sheaf_pottery_pattern";
   private static final String SHELTER_NAME = "shelter_pottery_pattern";
   private static final String SKULL_NAME = "skull_pottery_pattern";
   private static final String SNORT_NAME = "snort_pottery_pattern";
   private static final ResourceKey<String> BRICK = create("decorated_pot_side");
   private static final ResourceKey<String> ANGLER = create("angler_pottery_pattern");
   private static final ResourceKey<String> ARCHER = create("archer_pottery_pattern");
   private static final ResourceKey<String> ARMS_UP = create("arms_up_pottery_pattern");
   private static final ResourceKey<String> BLADE = create("blade_pottery_pattern");
   private static final ResourceKey<String> BREWER = create("brewer_pottery_pattern");
   private static final ResourceKey<String> BURN = create("burn_pottery_pattern");
   private static final ResourceKey<String> DANGER = create("danger_pottery_pattern");
   private static final ResourceKey<String> EXPLORER = create("explorer_pottery_pattern");
   private static final ResourceKey<String> FRIEND = create("friend_pottery_pattern");
   private static final ResourceKey<String> HEART = create("heart_pottery_pattern");
   private static final ResourceKey<String> HEARTBREAK = create("heartbreak_pottery_pattern");
   private static final ResourceKey<String> HOWL = create("howl_pottery_pattern");
   private static final ResourceKey<String> MINER = create("miner_pottery_pattern");
   private static final ResourceKey<String> MOURNER = create("mourner_pottery_pattern");
   private static final ResourceKey<String> PLENTY = create("plenty_pottery_pattern");
   private static final ResourceKey<String> PRIZE = create("prize_pottery_pattern");
   private static final ResourceKey<String> SHEAF = create("sheaf_pottery_pattern");
   private static final ResourceKey<String> SHELTER = create("shelter_pottery_pattern");
   private static final ResourceKey<String> SKULL = create("skull_pottery_pattern");
   private static final ResourceKey<String> SNORT = create("snort_pottery_pattern");
   private static final Map<Item, ResourceKey<String>> ITEM_TO_POT_TEXTURE = Map.ofEntries(Map.entry(Items.BRICK, BRICK), Map.entry(Items.ANGLER_POTTERY_SHERD, ANGLER), Map.entry(Items.ARCHER_POTTERY_SHERD, ARCHER), Map.entry(Items.ARMS_UP_POTTERY_SHERD, ARMS_UP), Map.entry(Items.BLADE_POTTERY_SHERD, BLADE), Map.entry(Items.BREWER_POTTERY_SHERD, BREWER), Map.entry(Items.BURN_POTTERY_SHERD, BURN), Map.entry(Items.DANGER_POTTERY_SHERD, DANGER), Map.entry(Items.EXPLORER_POTTERY_SHERD, EXPLORER), Map.entry(Items.FRIEND_POTTERY_SHERD, FRIEND), Map.entry(Items.HEART_POTTERY_SHERD, HEART), Map.entry(Items.HEARTBREAK_POTTERY_SHERD, HEARTBREAK), Map.entry(Items.HOWL_POTTERY_SHERD, HOWL), Map.entry(Items.MINER_POTTERY_SHERD, MINER), Map.entry(Items.MOURNER_POTTERY_SHERD, MOURNER), Map.entry(Items.PLENTY_POTTERY_SHERD, PLENTY), Map.entry(Items.PRIZE_POTTERY_SHERD, PRIZE), Map.entry(Items.SHEAF_POTTERY_SHERD, SHEAF), Map.entry(Items.SHELTER_POTTERY_SHERD, SHELTER), Map.entry(Items.SKULL_POTTERY_SHERD, SKULL), Map.entry(Items.SNORT_POTTERY_SHERD, SNORT));

   private static ResourceKey<String> create(String p_272919_) {
      return ResourceKey.create(Registries.DECORATED_POT_PATTERNS, new ResourceLocation(p_272919_));
   }

   public static ResourceLocation location(ResourceKey<String> p_273198_) {
      return p_273198_.location().withPrefix("entity/decorated_pot/");
   }

   @Nullable
   public static ResourceKey<String> getResourceKey(Item p_273094_) {
      return ITEM_TO_POT_TEXTURE.get(p_273094_);
   }

   public static String bootstrap(Registry<String> p_273479_) {
      Registry.register(p_273479_, BRICK, "decorated_pot_side");
      Registry.register(p_273479_, ANGLER, "angler_pottery_pattern");
      Registry.register(p_273479_, ARCHER, "archer_pottery_pattern");
      Registry.register(p_273479_, ARMS_UP, "arms_up_pottery_pattern");
      Registry.register(p_273479_, BLADE, "blade_pottery_pattern");
      Registry.register(p_273479_, BREWER, "brewer_pottery_pattern");
      Registry.register(p_273479_, BURN, "burn_pottery_pattern");
      Registry.register(p_273479_, DANGER, "danger_pottery_pattern");
      Registry.register(p_273479_, EXPLORER, "explorer_pottery_pattern");
      Registry.register(p_273479_, FRIEND, "friend_pottery_pattern");
      Registry.register(p_273479_, HEART, "heart_pottery_pattern");
      Registry.register(p_273479_, HEARTBREAK, "heartbreak_pottery_pattern");
      Registry.register(p_273479_, HOWL, "howl_pottery_pattern");
      Registry.register(p_273479_, MINER, "miner_pottery_pattern");
      Registry.register(p_273479_, MOURNER, "mourner_pottery_pattern");
      Registry.register(p_273479_, PLENTY, "plenty_pottery_pattern");
      Registry.register(p_273479_, PRIZE, "prize_pottery_pattern");
      Registry.register(p_273479_, SHEAF, "sheaf_pottery_pattern");
      Registry.register(p_273479_, SHELTER, "shelter_pottery_pattern");
      Registry.register(p_273479_, SKULL, "skull_pottery_pattern");
      Registry.register(p_273479_, SNORT, "snort_pottery_pattern");
      return Registry.register(p_273479_, BASE, "decorated_pot_base");
   }
}