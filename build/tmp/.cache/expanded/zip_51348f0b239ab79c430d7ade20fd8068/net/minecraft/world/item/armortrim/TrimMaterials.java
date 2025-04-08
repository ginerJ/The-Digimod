package net.minecraft.world.item.armortrim;

import java.util.Map;
import java.util.Optional;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class TrimMaterials {
   public static final ResourceKey<TrimMaterial> QUARTZ = registryKey("quartz");
   public static final ResourceKey<TrimMaterial> IRON = registryKey("iron");
   public static final ResourceKey<TrimMaterial> NETHERITE = registryKey("netherite");
   public static final ResourceKey<TrimMaterial> REDSTONE = registryKey("redstone");
   public static final ResourceKey<TrimMaterial> COPPER = registryKey("copper");
   public static final ResourceKey<TrimMaterial> GOLD = registryKey("gold");
   public static final ResourceKey<TrimMaterial> EMERALD = registryKey("emerald");
   public static final ResourceKey<TrimMaterial> DIAMOND = registryKey("diamond");
   public static final ResourceKey<TrimMaterial> LAPIS = registryKey("lapis");
   public static final ResourceKey<TrimMaterial> AMETHYST = registryKey("amethyst");

   public static void bootstrap(BootstapContext<TrimMaterial> p_267033_) {
      register(p_267033_, QUARTZ, Items.QUARTZ, Style.EMPTY.withColor(14931140), 0.1F);
      register(p_267033_, IRON, Items.IRON_INGOT, Style.EMPTY.withColor(15527148), 0.2F, Map.of(ArmorMaterials.IRON, "iron_darker"));
      register(p_267033_, NETHERITE, Items.NETHERITE_INGOT, Style.EMPTY.withColor(6445145), 0.3F, Map.of(ArmorMaterials.NETHERITE, "netherite_darker"));
      register(p_267033_, REDSTONE, Items.REDSTONE, Style.EMPTY.withColor(9901575), 0.4F);
      register(p_267033_, COPPER, Items.COPPER_INGOT, Style.EMPTY.withColor(11823181), 0.5F);
      register(p_267033_, GOLD, Items.GOLD_INGOT, Style.EMPTY.withColor(14594349), 0.6F, Map.of(ArmorMaterials.GOLD, "gold_darker"));
      register(p_267033_, EMERALD, Items.EMERALD, Style.EMPTY.withColor(1155126), 0.7F);
      register(p_267033_, DIAMOND, Items.DIAMOND, Style.EMPTY.withColor(7269586), 0.8F, Map.of(ArmorMaterials.DIAMOND, "diamond_darker"));
      register(p_267033_, LAPIS, Items.LAPIS_LAZULI, Style.EMPTY.withColor(4288151), 0.9F);
      register(p_267033_, AMETHYST, Items.AMETHYST_SHARD, Style.EMPTY.withColor(10116294), 1.0F);
   }

   public static Optional<Holder.Reference<TrimMaterial>> getFromIngredient(RegistryAccess p_266981_, ItemStack p_267327_) {
      return p_266981_.registryOrThrow(Registries.TRIM_MATERIAL).holders().filter((p_266876_) -> {
         return p_267327_.is(p_266876_.value().ingredient());
      }).findFirst();
   }

   private static void register(BootstapContext<TrimMaterial> p_268176_, ResourceKey<TrimMaterial> p_268293_, Item p_268156_, Style p_268174_, float p_268274_) {
      register(p_268176_, p_268293_, p_268156_, p_268174_, p_268274_, Map.of());
   }

   private static void register(BootstapContext<TrimMaterial> p_268244_, ResourceKey<TrimMaterial> p_268139_, Item p_268311_, Style p_268232_, float p_268197_, Map<ArmorMaterials, String> p_268352_) {
      TrimMaterial trimmaterial = TrimMaterial.create(p_268139_.location().getPath(), p_268311_, p_268197_, Component.translatable(Util.makeDescriptionId("trim_material", p_268139_.location())).withStyle(p_268232_), p_268352_);
      p_268244_.register(p_268139_, trimmaterial);
   }

   private static ResourceKey<TrimMaterial> registryKey(String p_266965_) {
      return ResourceKey.create(Registries.TRIM_MATERIAL, new ResourceLocation(p_266965_));
   }
}