package net.minecraft.client.renderer;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ItemBlockRenderTypes {
   @Deprecated
   private static final Map<Block, RenderType> TYPE_BY_BLOCK = Util.make(Maps.newHashMap(), (p_277225_) -> {
      RenderType rendertype = RenderType.tripwire();
      p_277225_.put(Blocks.TRIPWIRE, rendertype);
      RenderType rendertype1 = RenderType.cutoutMipped();
      p_277225_.put(Blocks.GRASS_BLOCK, rendertype1);
      p_277225_.put(Blocks.IRON_BARS, rendertype1);
      p_277225_.put(Blocks.GLASS_PANE, rendertype1);
      p_277225_.put(Blocks.TRIPWIRE_HOOK, rendertype1);
      p_277225_.put(Blocks.HOPPER, rendertype1);
      p_277225_.put(Blocks.CHAIN, rendertype1);
      p_277225_.put(Blocks.JUNGLE_LEAVES, rendertype1);
      p_277225_.put(Blocks.OAK_LEAVES, rendertype1);
      p_277225_.put(Blocks.SPRUCE_LEAVES, rendertype1);
      p_277225_.put(Blocks.ACACIA_LEAVES, rendertype1);
      p_277225_.put(Blocks.CHERRY_LEAVES, rendertype1);
      p_277225_.put(Blocks.BIRCH_LEAVES, rendertype1);
      p_277225_.put(Blocks.DARK_OAK_LEAVES, rendertype1);
      p_277225_.put(Blocks.AZALEA_LEAVES, rendertype1);
      p_277225_.put(Blocks.FLOWERING_AZALEA_LEAVES, rendertype1);
      p_277225_.put(Blocks.MANGROVE_ROOTS, rendertype1);
      p_277225_.put(Blocks.MANGROVE_LEAVES, rendertype1);
      RenderType rendertype2 = RenderType.cutout();
      p_277225_.put(Blocks.OAK_SAPLING, rendertype2);
      p_277225_.put(Blocks.SPRUCE_SAPLING, rendertype2);
      p_277225_.put(Blocks.BIRCH_SAPLING, rendertype2);
      p_277225_.put(Blocks.JUNGLE_SAPLING, rendertype2);
      p_277225_.put(Blocks.ACACIA_SAPLING, rendertype2);
      p_277225_.put(Blocks.CHERRY_SAPLING, rendertype2);
      p_277225_.put(Blocks.DARK_OAK_SAPLING, rendertype2);
      p_277225_.put(Blocks.GLASS, rendertype2);
      p_277225_.put(Blocks.WHITE_BED, rendertype2);
      p_277225_.put(Blocks.ORANGE_BED, rendertype2);
      p_277225_.put(Blocks.MAGENTA_BED, rendertype2);
      p_277225_.put(Blocks.LIGHT_BLUE_BED, rendertype2);
      p_277225_.put(Blocks.YELLOW_BED, rendertype2);
      p_277225_.put(Blocks.LIME_BED, rendertype2);
      p_277225_.put(Blocks.PINK_BED, rendertype2);
      p_277225_.put(Blocks.GRAY_BED, rendertype2);
      p_277225_.put(Blocks.LIGHT_GRAY_BED, rendertype2);
      p_277225_.put(Blocks.CYAN_BED, rendertype2);
      p_277225_.put(Blocks.PURPLE_BED, rendertype2);
      p_277225_.put(Blocks.BLUE_BED, rendertype2);
      p_277225_.put(Blocks.BROWN_BED, rendertype2);
      p_277225_.put(Blocks.GREEN_BED, rendertype2);
      p_277225_.put(Blocks.RED_BED, rendertype2);
      p_277225_.put(Blocks.BLACK_BED, rendertype2);
      p_277225_.put(Blocks.POWERED_RAIL, rendertype2);
      p_277225_.put(Blocks.DETECTOR_RAIL, rendertype2);
      p_277225_.put(Blocks.COBWEB, rendertype2);
      p_277225_.put(Blocks.GRASS, rendertype2);
      p_277225_.put(Blocks.FERN, rendertype2);
      p_277225_.put(Blocks.DEAD_BUSH, rendertype2);
      p_277225_.put(Blocks.SEAGRASS, rendertype2);
      p_277225_.put(Blocks.TALL_SEAGRASS, rendertype2);
      p_277225_.put(Blocks.DANDELION, rendertype2);
      p_277225_.put(Blocks.POPPY, rendertype2);
      p_277225_.put(Blocks.BLUE_ORCHID, rendertype2);
      p_277225_.put(Blocks.ALLIUM, rendertype2);
      p_277225_.put(Blocks.AZURE_BLUET, rendertype2);
      p_277225_.put(Blocks.RED_TULIP, rendertype2);
      p_277225_.put(Blocks.ORANGE_TULIP, rendertype2);
      p_277225_.put(Blocks.WHITE_TULIP, rendertype2);
      p_277225_.put(Blocks.PINK_TULIP, rendertype2);
      p_277225_.put(Blocks.OXEYE_DAISY, rendertype2);
      p_277225_.put(Blocks.CORNFLOWER, rendertype2);
      p_277225_.put(Blocks.WITHER_ROSE, rendertype2);
      p_277225_.put(Blocks.LILY_OF_THE_VALLEY, rendertype2);
      p_277225_.put(Blocks.BROWN_MUSHROOM, rendertype2);
      p_277225_.put(Blocks.RED_MUSHROOM, rendertype2);
      p_277225_.put(Blocks.TORCH, rendertype2);
      p_277225_.put(Blocks.WALL_TORCH, rendertype2);
      p_277225_.put(Blocks.SOUL_TORCH, rendertype2);
      p_277225_.put(Blocks.SOUL_WALL_TORCH, rendertype2);
      p_277225_.put(Blocks.FIRE, rendertype2);
      p_277225_.put(Blocks.SOUL_FIRE, rendertype2);
      p_277225_.put(Blocks.SPAWNER, rendertype2);
      p_277225_.put(Blocks.REDSTONE_WIRE, rendertype2);
      p_277225_.put(Blocks.WHEAT, rendertype2);
      p_277225_.put(Blocks.OAK_DOOR, rendertype2);
      p_277225_.put(Blocks.LADDER, rendertype2);
      p_277225_.put(Blocks.RAIL, rendertype2);
      p_277225_.put(Blocks.IRON_DOOR, rendertype2);
      p_277225_.put(Blocks.REDSTONE_TORCH, rendertype2);
      p_277225_.put(Blocks.REDSTONE_WALL_TORCH, rendertype2);
      p_277225_.put(Blocks.CACTUS, rendertype2);
      p_277225_.put(Blocks.SUGAR_CANE, rendertype2);
      p_277225_.put(Blocks.REPEATER, rendertype2);
      p_277225_.put(Blocks.OAK_TRAPDOOR, rendertype2);
      p_277225_.put(Blocks.SPRUCE_TRAPDOOR, rendertype2);
      p_277225_.put(Blocks.BIRCH_TRAPDOOR, rendertype2);
      p_277225_.put(Blocks.JUNGLE_TRAPDOOR, rendertype2);
      p_277225_.put(Blocks.ACACIA_TRAPDOOR, rendertype2);
      p_277225_.put(Blocks.CHERRY_TRAPDOOR, rendertype2);
      p_277225_.put(Blocks.DARK_OAK_TRAPDOOR, rendertype2);
      p_277225_.put(Blocks.CRIMSON_TRAPDOOR, rendertype2);
      p_277225_.put(Blocks.WARPED_TRAPDOOR, rendertype2);
      p_277225_.put(Blocks.MANGROVE_TRAPDOOR, rendertype2);
      p_277225_.put(Blocks.BAMBOO_TRAPDOOR, rendertype2);
      p_277225_.put(Blocks.ATTACHED_PUMPKIN_STEM, rendertype2);
      p_277225_.put(Blocks.ATTACHED_MELON_STEM, rendertype2);
      p_277225_.put(Blocks.PUMPKIN_STEM, rendertype2);
      p_277225_.put(Blocks.MELON_STEM, rendertype2);
      p_277225_.put(Blocks.VINE, rendertype2);
      p_277225_.put(Blocks.GLOW_LICHEN, rendertype2);
      p_277225_.put(Blocks.LILY_PAD, rendertype2);
      p_277225_.put(Blocks.NETHER_WART, rendertype2);
      p_277225_.put(Blocks.BREWING_STAND, rendertype2);
      p_277225_.put(Blocks.COCOA, rendertype2);
      p_277225_.put(Blocks.BEACON, rendertype2);
      p_277225_.put(Blocks.FLOWER_POT, rendertype2);
      p_277225_.put(Blocks.POTTED_OAK_SAPLING, rendertype2);
      p_277225_.put(Blocks.POTTED_SPRUCE_SAPLING, rendertype2);
      p_277225_.put(Blocks.POTTED_BIRCH_SAPLING, rendertype2);
      p_277225_.put(Blocks.POTTED_JUNGLE_SAPLING, rendertype2);
      p_277225_.put(Blocks.POTTED_ACACIA_SAPLING, rendertype2);
      p_277225_.put(Blocks.POTTED_CHERRY_SAPLING, rendertype2);
      p_277225_.put(Blocks.POTTED_DARK_OAK_SAPLING, rendertype2);
      p_277225_.put(Blocks.POTTED_MANGROVE_PROPAGULE, rendertype2);
      p_277225_.put(Blocks.POTTED_FERN, rendertype2);
      p_277225_.put(Blocks.POTTED_DANDELION, rendertype2);
      p_277225_.put(Blocks.POTTED_POPPY, rendertype2);
      p_277225_.put(Blocks.POTTED_BLUE_ORCHID, rendertype2);
      p_277225_.put(Blocks.POTTED_ALLIUM, rendertype2);
      p_277225_.put(Blocks.POTTED_AZURE_BLUET, rendertype2);
      p_277225_.put(Blocks.POTTED_RED_TULIP, rendertype2);
      p_277225_.put(Blocks.POTTED_ORANGE_TULIP, rendertype2);
      p_277225_.put(Blocks.POTTED_WHITE_TULIP, rendertype2);
      p_277225_.put(Blocks.POTTED_PINK_TULIP, rendertype2);
      p_277225_.put(Blocks.POTTED_OXEYE_DAISY, rendertype2);
      p_277225_.put(Blocks.POTTED_CORNFLOWER, rendertype2);
      p_277225_.put(Blocks.POTTED_LILY_OF_THE_VALLEY, rendertype2);
      p_277225_.put(Blocks.POTTED_WITHER_ROSE, rendertype2);
      p_277225_.put(Blocks.POTTED_RED_MUSHROOM, rendertype2);
      p_277225_.put(Blocks.POTTED_BROWN_MUSHROOM, rendertype2);
      p_277225_.put(Blocks.POTTED_DEAD_BUSH, rendertype2);
      p_277225_.put(Blocks.POTTED_CACTUS, rendertype2);
      p_277225_.put(Blocks.POTTED_AZALEA, rendertype2);
      p_277225_.put(Blocks.POTTED_FLOWERING_AZALEA, rendertype2);
      p_277225_.put(Blocks.POTTED_TORCHFLOWER, rendertype2);
      p_277225_.put(Blocks.CARROTS, rendertype2);
      p_277225_.put(Blocks.POTATOES, rendertype2);
      p_277225_.put(Blocks.COMPARATOR, rendertype2);
      p_277225_.put(Blocks.ACTIVATOR_RAIL, rendertype2);
      p_277225_.put(Blocks.IRON_TRAPDOOR, rendertype2);
      p_277225_.put(Blocks.SUNFLOWER, rendertype2);
      p_277225_.put(Blocks.LILAC, rendertype2);
      p_277225_.put(Blocks.ROSE_BUSH, rendertype2);
      p_277225_.put(Blocks.PEONY, rendertype2);
      p_277225_.put(Blocks.TALL_GRASS, rendertype2);
      p_277225_.put(Blocks.LARGE_FERN, rendertype2);
      p_277225_.put(Blocks.SPRUCE_DOOR, rendertype2);
      p_277225_.put(Blocks.BIRCH_DOOR, rendertype2);
      p_277225_.put(Blocks.JUNGLE_DOOR, rendertype2);
      p_277225_.put(Blocks.ACACIA_DOOR, rendertype2);
      p_277225_.put(Blocks.CHERRY_DOOR, rendertype2);
      p_277225_.put(Blocks.DARK_OAK_DOOR, rendertype2);
      p_277225_.put(Blocks.MANGROVE_DOOR, rendertype2);
      p_277225_.put(Blocks.BAMBOO_DOOR, rendertype2);
      p_277225_.put(Blocks.END_ROD, rendertype2);
      p_277225_.put(Blocks.CHORUS_PLANT, rendertype2);
      p_277225_.put(Blocks.CHORUS_FLOWER, rendertype2);
      p_277225_.put(Blocks.TORCHFLOWER, rendertype2);
      p_277225_.put(Blocks.TORCHFLOWER_CROP, rendertype2);
      p_277225_.put(Blocks.PITCHER_PLANT, rendertype2);
      p_277225_.put(Blocks.PITCHER_CROP, rendertype2);
      p_277225_.put(Blocks.BEETROOTS, rendertype2);
      p_277225_.put(Blocks.KELP, rendertype2);
      p_277225_.put(Blocks.KELP_PLANT, rendertype2);
      p_277225_.put(Blocks.TURTLE_EGG, rendertype2);
      p_277225_.put(Blocks.DEAD_TUBE_CORAL, rendertype2);
      p_277225_.put(Blocks.DEAD_BRAIN_CORAL, rendertype2);
      p_277225_.put(Blocks.DEAD_BUBBLE_CORAL, rendertype2);
      p_277225_.put(Blocks.DEAD_FIRE_CORAL, rendertype2);
      p_277225_.put(Blocks.DEAD_HORN_CORAL, rendertype2);
      p_277225_.put(Blocks.TUBE_CORAL, rendertype2);
      p_277225_.put(Blocks.BRAIN_CORAL, rendertype2);
      p_277225_.put(Blocks.BUBBLE_CORAL, rendertype2);
      p_277225_.put(Blocks.FIRE_CORAL, rendertype2);
      p_277225_.put(Blocks.HORN_CORAL, rendertype2);
      p_277225_.put(Blocks.DEAD_TUBE_CORAL_FAN, rendertype2);
      p_277225_.put(Blocks.DEAD_BRAIN_CORAL_FAN, rendertype2);
      p_277225_.put(Blocks.DEAD_BUBBLE_CORAL_FAN, rendertype2);
      p_277225_.put(Blocks.DEAD_FIRE_CORAL_FAN, rendertype2);
      p_277225_.put(Blocks.DEAD_HORN_CORAL_FAN, rendertype2);
      p_277225_.put(Blocks.TUBE_CORAL_FAN, rendertype2);
      p_277225_.put(Blocks.BRAIN_CORAL_FAN, rendertype2);
      p_277225_.put(Blocks.BUBBLE_CORAL_FAN, rendertype2);
      p_277225_.put(Blocks.FIRE_CORAL_FAN, rendertype2);
      p_277225_.put(Blocks.HORN_CORAL_FAN, rendertype2);
      p_277225_.put(Blocks.DEAD_TUBE_CORAL_WALL_FAN, rendertype2);
      p_277225_.put(Blocks.DEAD_BRAIN_CORAL_WALL_FAN, rendertype2);
      p_277225_.put(Blocks.DEAD_BUBBLE_CORAL_WALL_FAN, rendertype2);
      p_277225_.put(Blocks.DEAD_FIRE_CORAL_WALL_FAN, rendertype2);
      p_277225_.put(Blocks.DEAD_HORN_CORAL_WALL_FAN, rendertype2);
      p_277225_.put(Blocks.TUBE_CORAL_WALL_FAN, rendertype2);
      p_277225_.put(Blocks.BRAIN_CORAL_WALL_FAN, rendertype2);
      p_277225_.put(Blocks.BUBBLE_CORAL_WALL_FAN, rendertype2);
      p_277225_.put(Blocks.FIRE_CORAL_WALL_FAN, rendertype2);
      p_277225_.put(Blocks.HORN_CORAL_WALL_FAN, rendertype2);
      p_277225_.put(Blocks.SEA_PICKLE, rendertype2);
      p_277225_.put(Blocks.CONDUIT, rendertype2);
      p_277225_.put(Blocks.BAMBOO_SAPLING, rendertype2);
      p_277225_.put(Blocks.BAMBOO, rendertype2);
      p_277225_.put(Blocks.POTTED_BAMBOO, rendertype2);
      p_277225_.put(Blocks.SCAFFOLDING, rendertype2);
      p_277225_.put(Blocks.STONECUTTER, rendertype2);
      p_277225_.put(Blocks.LANTERN, rendertype2);
      p_277225_.put(Blocks.SOUL_LANTERN, rendertype2);
      p_277225_.put(Blocks.CAMPFIRE, rendertype2);
      p_277225_.put(Blocks.SOUL_CAMPFIRE, rendertype2);
      p_277225_.put(Blocks.SWEET_BERRY_BUSH, rendertype2);
      p_277225_.put(Blocks.WEEPING_VINES, rendertype2);
      p_277225_.put(Blocks.WEEPING_VINES_PLANT, rendertype2);
      p_277225_.put(Blocks.TWISTING_VINES, rendertype2);
      p_277225_.put(Blocks.TWISTING_VINES_PLANT, rendertype2);
      p_277225_.put(Blocks.NETHER_SPROUTS, rendertype2);
      p_277225_.put(Blocks.CRIMSON_FUNGUS, rendertype2);
      p_277225_.put(Blocks.WARPED_FUNGUS, rendertype2);
      p_277225_.put(Blocks.CRIMSON_ROOTS, rendertype2);
      p_277225_.put(Blocks.WARPED_ROOTS, rendertype2);
      p_277225_.put(Blocks.POTTED_CRIMSON_FUNGUS, rendertype2);
      p_277225_.put(Blocks.POTTED_WARPED_FUNGUS, rendertype2);
      p_277225_.put(Blocks.POTTED_CRIMSON_ROOTS, rendertype2);
      p_277225_.put(Blocks.POTTED_WARPED_ROOTS, rendertype2);
      p_277225_.put(Blocks.CRIMSON_DOOR, rendertype2);
      p_277225_.put(Blocks.WARPED_DOOR, rendertype2);
      p_277225_.put(Blocks.POINTED_DRIPSTONE, rendertype2);
      p_277225_.put(Blocks.SMALL_AMETHYST_BUD, rendertype2);
      p_277225_.put(Blocks.MEDIUM_AMETHYST_BUD, rendertype2);
      p_277225_.put(Blocks.LARGE_AMETHYST_BUD, rendertype2);
      p_277225_.put(Blocks.AMETHYST_CLUSTER, rendertype2);
      p_277225_.put(Blocks.LIGHTNING_ROD, rendertype2);
      p_277225_.put(Blocks.CAVE_VINES, rendertype2);
      p_277225_.put(Blocks.CAVE_VINES_PLANT, rendertype2);
      p_277225_.put(Blocks.SPORE_BLOSSOM, rendertype2);
      p_277225_.put(Blocks.FLOWERING_AZALEA, rendertype2);
      p_277225_.put(Blocks.AZALEA, rendertype2);
      p_277225_.put(Blocks.MOSS_CARPET, rendertype2);
      p_277225_.put(Blocks.PINK_PETALS, rendertype2);
      p_277225_.put(Blocks.BIG_DRIPLEAF, rendertype2);
      p_277225_.put(Blocks.BIG_DRIPLEAF_STEM, rendertype2);
      p_277225_.put(Blocks.SMALL_DRIPLEAF, rendertype2);
      p_277225_.put(Blocks.HANGING_ROOTS, rendertype2);
      p_277225_.put(Blocks.SCULK_SENSOR, rendertype2);
      p_277225_.put(Blocks.CALIBRATED_SCULK_SENSOR, rendertype2);
      p_277225_.put(Blocks.SCULK_VEIN, rendertype2);
      p_277225_.put(Blocks.SCULK_SHRIEKER, rendertype2);
      p_277225_.put(Blocks.MANGROVE_PROPAGULE, rendertype2);
      p_277225_.put(Blocks.MANGROVE_LOG, rendertype2);
      p_277225_.put(Blocks.FROGSPAWN, rendertype2);
      RenderType rendertype3 = RenderType.translucent();
      p_277225_.put(Blocks.ICE, rendertype3);
      p_277225_.put(Blocks.NETHER_PORTAL, rendertype3);
      p_277225_.put(Blocks.WHITE_STAINED_GLASS, rendertype3);
      p_277225_.put(Blocks.ORANGE_STAINED_GLASS, rendertype3);
      p_277225_.put(Blocks.MAGENTA_STAINED_GLASS, rendertype3);
      p_277225_.put(Blocks.LIGHT_BLUE_STAINED_GLASS, rendertype3);
      p_277225_.put(Blocks.YELLOW_STAINED_GLASS, rendertype3);
      p_277225_.put(Blocks.LIME_STAINED_GLASS, rendertype3);
      p_277225_.put(Blocks.PINK_STAINED_GLASS, rendertype3);
      p_277225_.put(Blocks.GRAY_STAINED_GLASS, rendertype3);
      p_277225_.put(Blocks.LIGHT_GRAY_STAINED_GLASS, rendertype3);
      p_277225_.put(Blocks.CYAN_STAINED_GLASS, rendertype3);
      p_277225_.put(Blocks.PURPLE_STAINED_GLASS, rendertype3);
      p_277225_.put(Blocks.BLUE_STAINED_GLASS, rendertype3);
      p_277225_.put(Blocks.BROWN_STAINED_GLASS, rendertype3);
      p_277225_.put(Blocks.GREEN_STAINED_GLASS, rendertype3);
      p_277225_.put(Blocks.RED_STAINED_GLASS, rendertype3);
      p_277225_.put(Blocks.BLACK_STAINED_GLASS, rendertype3);
      p_277225_.put(Blocks.WHITE_STAINED_GLASS_PANE, rendertype3);
      p_277225_.put(Blocks.ORANGE_STAINED_GLASS_PANE, rendertype3);
      p_277225_.put(Blocks.MAGENTA_STAINED_GLASS_PANE, rendertype3);
      p_277225_.put(Blocks.LIGHT_BLUE_STAINED_GLASS_PANE, rendertype3);
      p_277225_.put(Blocks.YELLOW_STAINED_GLASS_PANE, rendertype3);
      p_277225_.put(Blocks.LIME_STAINED_GLASS_PANE, rendertype3);
      p_277225_.put(Blocks.PINK_STAINED_GLASS_PANE, rendertype3);
      p_277225_.put(Blocks.GRAY_STAINED_GLASS_PANE, rendertype3);
      p_277225_.put(Blocks.LIGHT_GRAY_STAINED_GLASS_PANE, rendertype3);
      p_277225_.put(Blocks.CYAN_STAINED_GLASS_PANE, rendertype3);
      p_277225_.put(Blocks.PURPLE_STAINED_GLASS_PANE, rendertype3);
      p_277225_.put(Blocks.BLUE_STAINED_GLASS_PANE, rendertype3);
      p_277225_.put(Blocks.BROWN_STAINED_GLASS_PANE, rendertype3);
      p_277225_.put(Blocks.GREEN_STAINED_GLASS_PANE, rendertype3);
      p_277225_.put(Blocks.RED_STAINED_GLASS_PANE, rendertype3);
      p_277225_.put(Blocks.BLACK_STAINED_GLASS_PANE, rendertype3);
      p_277225_.put(Blocks.SLIME_BLOCK, rendertype3);
      p_277225_.put(Blocks.HONEY_BLOCK, rendertype3);
      p_277225_.put(Blocks.FROSTED_ICE, rendertype3);
      p_277225_.put(Blocks.BUBBLE_COLUMN, rendertype3);
      p_277225_.put(Blocks.TINTED_GLASS, rendertype3);
   });
   @Deprecated
   private static final Map<Fluid, RenderType> TYPE_BY_FLUID = Util.make(Maps.newHashMap(), (p_109290_) -> {
      RenderType rendertype = RenderType.translucent();
      p_109290_.put(Fluids.FLOWING_WATER, rendertype);
      p_109290_.put(Fluids.WATER, rendertype);
   });
   private static boolean renderCutout;

   /** @deprecated Forge: Use {@link net.minecraft.client.resources.model.BakedModel#getRenderTypes(BlockState, net.minecraft.util.RandomSource, net.minecraftforge.client.model.data.ModelData)}. */
   @Deprecated // Note: this method does NOT support model-based render types
   public static RenderType getChunkRenderType(BlockState p_109283_) {
      Block block = p_109283_.getBlock();
      if (block instanceof LeavesBlock) {
         return renderCutout ? RenderType.cutoutMipped() : RenderType.solid();
      } else {
         RenderType rendertype = TYPE_BY_BLOCK.get(block);
         return rendertype != null ? rendertype : RenderType.solid();
      }
   }

   /** @deprecated Forge: Use {@link net.minecraftforge.client.RenderTypeHelper#getMovingBlockRenderType(RenderType)} while iterating through {@link net.minecraft.client.resources.model.BakedModel#getRenderTypes(BlockState, net.minecraft.util.RandomSource, net.minecraftforge.client.model.data.ModelData)}. */
   @Deprecated // Note: this method does NOT support model-based render types
   public static RenderType getMovingBlockRenderType(BlockState p_109294_) {
      Block block = p_109294_.getBlock();
      if (block instanceof LeavesBlock) {
         return renderCutout ? RenderType.cutoutMipped() : RenderType.solid();
      } else {
         RenderType rendertype = TYPE_BY_BLOCK.get(block);
         if (rendertype != null) {
            return rendertype == RenderType.translucent() ? RenderType.translucentMovingBlock() : rendertype;
         } else {
            return RenderType.solid();
         }
      }
   }

   /** @deprecated Forge: Use {@link net.minecraftforge.client.RenderTypeHelper#getEntityRenderType(RenderType, boolean)} while iterating through {@link net.minecraft.client.resources.model.BakedModel#getRenderTypes(BlockState, net.minecraft.util.RandomSource, net.minecraftforge.client.model.data.ModelData)}. */
   @Deprecated // Note: this method does NOT support model-based render types
   public static RenderType getRenderType(BlockState p_109285_, boolean p_109286_) {
      RenderType rendertype = getChunkRenderType(p_109285_);
      if (rendertype == RenderType.translucent()) {
         if (!Minecraft.useShaderTransparency()) {
            return Sheets.translucentCullBlockSheet();
         } else {
            return p_109286_ ? Sheets.translucentCullBlockSheet() : Sheets.translucentItemSheet();
         }
      } else {
         return Sheets.cutoutBlockSheet();
      }
   }

   /** @deprecated Forge: Use {@link net.minecraft.client.resources.model.BakedModel#getRenderPasses(ItemStack, boolean)} and {@link net.minecraft.client.resources.model.BakedModel#getRenderTypes(ItemStack, boolean)}. */
   @Deprecated // Note: this method does NOT support model-based render types
   public static RenderType getRenderType(ItemStack p_109280_, boolean p_109281_) {
      Item item = p_109280_.getItem();
      if (item instanceof BlockItem) {
         Block block = ((BlockItem)item).getBlock();
         return getRenderType(block.defaultBlockState(), p_109281_);
      } else {
         return p_109281_ ? Sheets.translucentCullBlockSheet() : Sheets.translucentItemSheet();
      }
   }

   public static RenderType getRenderLayer(FluidState p_109288_) {
      RenderType rendertype = FLUID_RENDER_TYPES.get(net.minecraftforge.registries.ForgeRegistries.FLUIDS.getDelegateOrThrow(p_109288_.getType()));
      return rendertype != null ? rendertype : RenderType.solid();
   }

   // FORGE START

   private static final net.minecraftforge.client.ChunkRenderTypeSet CUTOUT_MIPPED = net.minecraftforge.client.ChunkRenderTypeSet.of(RenderType.cutoutMipped());
   private static final net.minecraftforge.client.ChunkRenderTypeSet SOLID = net.minecraftforge.client.ChunkRenderTypeSet.of(RenderType.solid());
   private static final Map<net.minecraft.core.Holder.Reference<Block>, net.minecraftforge.client.ChunkRenderTypeSet> BLOCK_RENDER_TYPES = Util.make(new it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap<>(TYPE_BY_BLOCK.size(), 0.5F), map -> {
      map.defaultReturnValue(SOLID);
      for(Map.Entry<Block, RenderType> entry : TYPE_BY_BLOCK.entrySet()) {
         map.put(net.minecraftforge.registries.ForgeRegistries.BLOCKS.getDelegateOrThrow(entry.getKey()), net.minecraftforge.client.ChunkRenderTypeSet.of(entry.getValue()));
      }
   });
   private static final Map<net.minecraft.core.Holder.Reference<Fluid>, RenderType> FLUID_RENDER_TYPES = Util.make(new it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap<>(TYPE_BY_FLUID.size(), 0.5F), map -> {
      map.defaultReturnValue(RenderType.solid());
      for(Map.Entry<Fluid, RenderType> entry : TYPE_BY_FLUID.entrySet()) {
         map.put(net.minecraftforge.registries.ForgeRegistries.FLUIDS.getDelegateOrThrow(entry.getKey()), entry.getValue());
      }
   });

   /** @deprecated Use {@link net.minecraft.client.resources.model.BakedModel#getRenderTypes(BlockState, net.minecraft.util.RandomSource, net.minecraftforge.client.model.data.ModelData)}. */
   @Deprecated(since = "1.19")
   public static net.minecraftforge.client.ChunkRenderTypeSet getRenderLayers(BlockState state) {
      Block block = state.getBlock();
      if (block instanceof LeavesBlock) {
         return renderCutout ? CUTOUT_MIPPED : SOLID;
      } else {
         return BLOCK_RENDER_TYPES.get(net.minecraftforge.registries.ForgeRegistries.BLOCKS.getDelegateOrThrow(block));
      }
   }

   /** @deprecated Set your render type in your block model's JSON (eg. {@code "render_type": "cutout"}) or override {@link net.minecraft.client.resources.model.BakedModel#getRenderTypes(BlockState, net.minecraft.util.RandomSource, net.minecraftforge.client.model.data.ModelData)} */
   @Deprecated(since = "1.19")
   public static void setRenderLayer(Block block, RenderType type) {
      com.google.common.base.Preconditions.checkArgument(type.getChunkLayerId() >= 0, "The argument must be a valid chunk render type returned by RenderType#chunkBufferLayers().");
      setRenderLayer(block, net.minecraftforge.client.ChunkRenderTypeSet.of(type));
   }

   /** @deprecated Set your render type in your block model's JSON (eg. {@code "render_type": "cutout"}) or override {@link net.minecraft.client.resources.model.BakedModel#getRenderTypes(BlockState, net.minecraft.util.RandomSource, net.minecraftforge.client.model.data.ModelData)} */
   @Deprecated(since = "1.19")
   public static synchronized void setRenderLayer(Block block, java.util.function.Predicate<RenderType> predicate) {
      setRenderLayer(block, createSetFromPredicate(predicate));
   }

   /** @deprecated Set your render type in your block model's JSON (eg. {@code "render_type": "cutout"}) or override {@link net.minecraft.client.resources.model.BakedModel#getRenderTypes(BlockState, net.minecraft.util.RandomSource, net.minecraftforge.client.model.data.ModelData)} */
   @Deprecated(since = "1.19")
   public static synchronized void setRenderLayer(Block block, net.minecraftforge.client.ChunkRenderTypeSet layers) {
      checkClientLoading();
      BLOCK_RENDER_TYPES.put(net.minecraftforge.registries.ForgeRegistries.BLOCKS.getDelegateOrThrow(block), layers);
   }

   public static synchronized void setRenderLayer(Fluid fluid, RenderType type) {
      com.google.common.base.Preconditions.checkArgument(type.getChunkLayerId() >= 0, "The argument must be a valid chunk render type returned by RenderType#chunkBufferLayers().");
      checkClientLoading();
      FLUID_RENDER_TYPES.put(net.minecraftforge.registries.ForgeRegistries.FLUIDS.getDelegateOrThrow(fluid), type);
   }

   private static void checkClientLoading() {
      com.google.common.base.Preconditions.checkState(net.minecraftforge.client.loading.ClientModLoader.isLoading(),
              "Render layers can only be set during client loading! " +
                      "This might ideally be done from `FMLClientSetupEvent`."
      );
   }

   private static net.minecraftforge.client.ChunkRenderTypeSet createSetFromPredicate(java.util.function.Predicate<RenderType> predicate) {
      return net.minecraftforge.client.ChunkRenderTypeSet.of(RenderType.chunkBufferLayers().stream().filter(predicate).toArray(RenderType[]::new));
   }

   public static void setFancy(boolean p_109292_) {
      renderCutout = p_109292_;
   }
}
