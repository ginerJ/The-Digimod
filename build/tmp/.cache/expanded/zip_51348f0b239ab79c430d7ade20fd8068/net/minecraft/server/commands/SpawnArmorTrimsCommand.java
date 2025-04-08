package net.minecraft.server.commands;

import com.google.common.collect.Maps;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.datafixers.util.Pair;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.ToIntFunction;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.armortrim.ArmorTrim;
import net.minecraft.world.item.armortrim.TrimMaterial;
import net.minecraft.world.item.armortrim.TrimMaterials;
import net.minecraft.world.item.armortrim.TrimPattern;
import net.minecraft.world.item.armortrim.TrimPatterns;
import net.minecraft.world.level.Level;

public class SpawnArmorTrimsCommand {
   private static final Map<Pair<ArmorMaterial, EquipmentSlot>, Item> MATERIAL_AND_SLOT_TO_ITEM = Util.make(Maps.newHashMap(), (p_266706_) -> {
      p_266706_.put(Pair.of(ArmorMaterials.CHAIN, EquipmentSlot.HEAD), Items.CHAINMAIL_HELMET);
      p_266706_.put(Pair.of(ArmorMaterials.CHAIN, EquipmentSlot.CHEST), Items.CHAINMAIL_CHESTPLATE);
      p_266706_.put(Pair.of(ArmorMaterials.CHAIN, EquipmentSlot.LEGS), Items.CHAINMAIL_LEGGINGS);
      p_266706_.put(Pair.of(ArmorMaterials.CHAIN, EquipmentSlot.FEET), Items.CHAINMAIL_BOOTS);
      p_266706_.put(Pair.of(ArmorMaterials.IRON, EquipmentSlot.HEAD), Items.IRON_HELMET);
      p_266706_.put(Pair.of(ArmorMaterials.IRON, EquipmentSlot.CHEST), Items.IRON_CHESTPLATE);
      p_266706_.put(Pair.of(ArmorMaterials.IRON, EquipmentSlot.LEGS), Items.IRON_LEGGINGS);
      p_266706_.put(Pair.of(ArmorMaterials.IRON, EquipmentSlot.FEET), Items.IRON_BOOTS);
      p_266706_.put(Pair.of(ArmorMaterials.GOLD, EquipmentSlot.HEAD), Items.GOLDEN_HELMET);
      p_266706_.put(Pair.of(ArmorMaterials.GOLD, EquipmentSlot.CHEST), Items.GOLDEN_CHESTPLATE);
      p_266706_.put(Pair.of(ArmorMaterials.GOLD, EquipmentSlot.LEGS), Items.GOLDEN_LEGGINGS);
      p_266706_.put(Pair.of(ArmorMaterials.GOLD, EquipmentSlot.FEET), Items.GOLDEN_BOOTS);
      p_266706_.put(Pair.of(ArmorMaterials.NETHERITE, EquipmentSlot.HEAD), Items.NETHERITE_HELMET);
      p_266706_.put(Pair.of(ArmorMaterials.NETHERITE, EquipmentSlot.CHEST), Items.NETHERITE_CHESTPLATE);
      p_266706_.put(Pair.of(ArmorMaterials.NETHERITE, EquipmentSlot.LEGS), Items.NETHERITE_LEGGINGS);
      p_266706_.put(Pair.of(ArmorMaterials.NETHERITE, EquipmentSlot.FEET), Items.NETHERITE_BOOTS);
      p_266706_.put(Pair.of(ArmorMaterials.DIAMOND, EquipmentSlot.HEAD), Items.DIAMOND_HELMET);
      p_266706_.put(Pair.of(ArmorMaterials.DIAMOND, EquipmentSlot.CHEST), Items.DIAMOND_CHESTPLATE);
      p_266706_.put(Pair.of(ArmorMaterials.DIAMOND, EquipmentSlot.LEGS), Items.DIAMOND_LEGGINGS);
      p_266706_.put(Pair.of(ArmorMaterials.DIAMOND, EquipmentSlot.FEET), Items.DIAMOND_BOOTS);
      p_266706_.put(Pair.of(ArmorMaterials.TURTLE, EquipmentSlot.HEAD), Items.TURTLE_HELMET);
   });
   private static final List<ResourceKey<TrimPattern>> VANILLA_TRIM_PATTERNS = List.of(TrimPatterns.SENTRY, TrimPatterns.DUNE, TrimPatterns.COAST, TrimPatterns.WILD, TrimPatterns.WARD, TrimPatterns.EYE, TrimPatterns.VEX, TrimPatterns.TIDE, TrimPatterns.SNOUT, TrimPatterns.RIB, TrimPatterns.SPIRE, TrimPatterns.WAYFINDER, TrimPatterns.SHAPER, TrimPatterns.SILENCE, TrimPatterns.RAISER, TrimPatterns.HOST);
   private static final List<ResourceKey<TrimMaterial>> VANILLA_TRIM_MATERIALS = List.of(TrimMaterials.QUARTZ, TrimMaterials.IRON, TrimMaterials.NETHERITE, TrimMaterials.REDSTONE, TrimMaterials.COPPER, TrimMaterials.GOLD, TrimMaterials.EMERALD, TrimMaterials.DIAMOND, TrimMaterials.LAPIS, TrimMaterials.AMETHYST);
   private static final ToIntFunction<ResourceKey<TrimPattern>> TRIM_PATTERN_ORDER = Util.createIndexLookup(VANILLA_TRIM_PATTERNS);
   private static final ToIntFunction<ResourceKey<TrimMaterial>> TRIM_MATERIAL_ORDER = Util.createIndexLookup(VANILLA_TRIM_MATERIALS);

   public static void register(CommandDispatcher<CommandSourceStack> p_266758_) {
      p_266758_.register(Commands.literal("spawn_armor_trims").requires((p_277270_) -> {
         return p_277270_.hasPermission(2);
      }).executes((p_267005_) -> {
         return spawnArmorTrims(p_267005_.getSource(), p_267005_.getSource().getPlayerOrException());
      }));
   }

   private static int spawnArmorTrims(CommandSourceStack p_266993_, Player p_266983_) {
      Level level = p_266983_.level();
      NonNullList<ArmorTrim> nonnulllist = NonNullList.create();
      Registry<TrimPattern> registry = level.registryAccess().registryOrThrow(Registries.TRIM_PATTERN);
      Registry<TrimMaterial> registry1 = level.registryAccess().registryOrThrow(Registries.TRIM_MATERIAL);
      registry.stream().sorted(Comparator.comparing((p_266941_) -> {
         return TRIM_PATTERN_ORDER.applyAsInt(registry.getResourceKey(p_266941_).orElse((ResourceKey<TrimPattern>)null));
      })).forEachOrdered((p_266759_) -> {
         registry1.stream().sorted(Comparator.comparing((p_267239_) -> {
            return TRIM_MATERIAL_ORDER.applyAsInt(registry1.getResourceKey(p_267239_).orElse((ResourceKey<TrimMaterial>)null));
         })).forEachOrdered((p_267162_) -> {
            nonnulllist.add(new ArmorTrim(registry1.wrapAsHolder(p_267162_), registry.wrapAsHolder(p_266759_)));
         });
      });
      BlockPos blockpos = p_266983_.blockPosition().relative(p_266983_.getDirection(), 5);
      int i = ArmorMaterials.values().length - 1;
      double d0 = 3.0D;
      int j = 0;
      int k = 0;

      for(ArmorTrim armortrim : nonnulllist) {
         for(ArmorMaterial armormaterial : ArmorMaterials.values()) {
            if (armormaterial != ArmorMaterials.LEATHER) {
               double d1 = (double)blockpos.getX() + 0.5D - (double)(j % registry1.size()) * 3.0D;
               double d2 = (double)blockpos.getY() + 0.5D + (double)(k % i) * 3.0D;
               double d3 = (double)blockpos.getZ() + 0.5D + (double)(j / registry1.size() * 10);
               ArmorStand armorstand = new ArmorStand(level, d1, d2, d3);
               armorstand.setYRot(180.0F);
               armorstand.setNoGravity(true);

               for(EquipmentSlot equipmentslot : EquipmentSlot.values()) {
                  Item item = MATERIAL_AND_SLOT_TO_ITEM.get(Pair.of(armormaterial, equipmentslot));
                  if (item != null) {
                     ItemStack itemstack = new ItemStack(item);
                     ArmorTrim.setTrim(level.registryAccess(), itemstack, armortrim);
                     armorstand.setItemSlot(equipmentslot, itemstack);
                     if (item instanceof ArmorItem) {
                        ArmorItem armoritem = (ArmorItem)item;
                        if (armoritem.getMaterial() == ArmorMaterials.TURTLE) {
                           armorstand.setCustomName(armortrim.pattern().value().copyWithStyle(armortrim.material()).copy().append(" ").append(armortrim.material().value().description()));
                           armorstand.setCustomNameVisible(true);
                           continue;
                        }
                     }

                     armorstand.setInvisible(true);
                  }
               }

               level.addFreshEntity(armorstand);
               ++k;
            }
         }

         ++j;
      }

      p_266993_.sendSuccess(() -> {
         return Component.literal("Armorstands with trimmed armor spawned around you");
      }, true);
      return 1;
   }
}