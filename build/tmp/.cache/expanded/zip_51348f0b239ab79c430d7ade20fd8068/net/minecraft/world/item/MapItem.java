package net.minecraft.world.item;

import com.google.common.collect.Iterables;
import com.google.common.collect.LinkedHashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.SectionPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;

public class MapItem extends ComplexItem {
   public static final int IMAGE_WIDTH = 128;
   public static final int IMAGE_HEIGHT = 128;
   private static final int DEFAULT_MAP_COLOR = -12173266;
   private static final String TAG_MAP = "map";
   public static final String MAP_SCALE_TAG = "map_scale_direction";
   public static final String MAP_LOCK_TAG = "map_to_lock";

   public MapItem(Item.Properties p_42847_) {
      super(p_42847_);
   }

   public static ItemStack create(Level p_42887_, int p_42888_, int p_42889_, byte p_42890_, boolean p_42891_, boolean p_42892_) {
      ItemStack itemstack = new ItemStack(Items.FILLED_MAP);
      createAndStoreSavedData(itemstack, p_42887_, p_42888_, p_42889_, p_42890_, p_42891_, p_42892_, p_42887_.dimension());
      return itemstack;
   }

   @Nullable
   public static MapItemSavedData getSavedData(@Nullable Integer p_151129_, Level p_151130_) {
      return p_151129_ == null ? null : p_151130_.getMapData(makeKey(p_151129_));
   }

   @Nullable
   public static MapItemSavedData getSavedData(ItemStack p_42854_, Level p_42855_) {
      // Forge: Add instance method so that mods can override
      Item map = p_42854_.getItem();
      if(map instanceof MapItem) {
         return ((MapItem)map).getCustomMapData(p_42854_, p_42855_);
      }
      return null;
   }

   @Nullable
   protected MapItemSavedData getCustomMapData(ItemStack p_42910_, Level p_42911_) {
      Integer integer = getMapId(p_42910_);
      return getSavedData(integer, p_42911_);
   }

   @Nullable
   public static Integer getMapId(ItemStack p_151132_) {
      CompoundTag compoundtag = p_151132_.getTag();
      return compoundtag != null && compoundtag.contains("map", 99) ? compoundtag.getInt("map") : null;
   }

   private static int createNewSavedData(Level p_151121_, int p_151122_, int p_151123_, int p_151124_, boolean p_151125_, boolean p_151126_, ResourceKey<Level> p_151127_) {
      MapItemSavedData mapitemsaveddata = MapItemSavedData.createFresh((double)p_151122_, (double)p_151123_, (byte)p_151124_, p_151125_, p_151126_, p_151127_);
      int i = p_151121_.getFreeMapId();
      p_151121_.setMapData(makeKey(i), mapitemsaveddata);
      return i;
   }

   private static void storeMapData(ItemStack p_151109_, int p_151110_) {
      p_151109_.getOrCreateTag().putInt("map", p_151110_);
   }

   private static void createAndStoreSavedData(ItemStack p_151112_, Level p_151113_, int p_151114_, int p_151115_, int p_151116_, boolean p_151117_, boolean p_151118_, ResourceKey<Level> p_151119_) {
      int i = createNewSavedData(p_151113_, p_151114_, p_151115_, p_151116_, p_151117_, p_151118_, p_151119_);
      storeMapData(p_151112_, i);
   }

   public static String makeKey(int p_42849_) {
      return "map_" + p_42849_;
   }

   public void update(Level p_42894_, Entity p_42895_, MapItemSavedData p_42896_) {
      if (p_42894_.dimension() == p_42896_.dimension && p_42895_ instanceof Player) {
         int i = 1 << p_42896_.scale;
         int j = p_42896_.centerX;
         int k = p_42896_.centerZ;
         int l = Mth.floor(p_42895_.getX() - (double)j) / i + 64;
         int i1 = Mth.floor(p_42895_.getZ() - (double)k) / i + 64;
         int j1 = 128 / i;
         if (p_42894_.dimensionType().hasCeiling()) {
            j1 /= 2;
         }

         MapItemSavedData.HoldingPlayer mapitemsaveddata$holdingplayer = p_42896_.getHoldingPlayer((Player)p_42895_);
         ++mapitemsaveddata$holdingplayer.step;
         BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
         BlockPos.MutableBlockPos blockpos$mutableblockpos1 = new BlockPos.MutableBlockPos();
         boolean flag = false;

         for(int k1 = l - j1 + 1; k1 < l + j1; ++k1) {
            if ((k1 & 15) == (mapitemsaveddata$holdingplayer.step & 15) || flag) {
               flag = false;
               double d0 = 0.0D;

               for(int l1 = i1 - j1 - 1; l1 < i1 + j1; ++l1) {
                  if (k1 >= 0 && l1 >= -1 && k1 < 128 && l1 < 128) {
                     int i2 = Mth.square(k1 - l) + Mth.square(l1 - i1);
                     boolean flag1 = i2 > (j1 - 2) * (j1 - 2);
                     int j2 = (j / i + k1 - 64) * i;
                     int k2 = (k / i + l1 - 64) * i;
                     Multiset<MapColor> multiset = LinkedHashMultiset.create();
                     LevelChunk levelchunk = p_42894_.getChunk(SectionPos.blockToSectionCoord(j2), SectionPos.blockToSectionCoord(k2));
                     if (!levelchunk.isEmpty()) {
                        int l2 = 0;
                        double d1 = 0.0D;
                        if (p_42894_.dimensionType().hasCeiling()) {
                           int i3 = j2 + k2 * 231871;
                           i3 = i3 * i3 * 31287121 + i3 * 11;
                           if ((i3 >> 20 & 1) == 0) {
                              multiset.add(Blocks.DIRT.defaultBlockState().getMapColor(p_42894_, BlockPos.ZERO), 10);
                           } else {
                              multiset.add(Blocks.STONE.defaultBlockState().getMapColor(p_42894_, BlockPos.ZERO), 100);
                           }

                           d1 = 100.0D;
                        } else {
                           for(int i4 = 0; i4 < i; ++i4) {
                              for(int j3 = 0; j3 < i; ++j3) {
                                 blockpos$mutableblockpos.set(j2 + i4, 0, k2 + j3);
                                 int k3 = levelchunk.getHeight(Heightmap.Types.WORLD_SURFACE, blockpos$mutableblockpos.getX(), blockpos$mutableblockpos.getZ()) + 1;
                                 BlockState blockstate;
                                 if (k3 <= p_42894_.getMinBuildHeight() + 1) {
                                    blockstate = Blocks.BEDROCK.defaultBlockState();
                                 } else {
                                    do {
                                       --k3;
                                       blockpos$mutableblockpos.setY(k3);
                                       blockstate = levelchunk.getBlockState(blockpos$mutableblockpos);
                                    } while(blockstate.getMapColor(p_42894_, blockpos$mutableblockpos) == MapColor.NONE && k3 > p_42894_.getMinBuildHeight());

                                    if (k3 > p_42894_.getMinBuildHeight() && !blockstate.getFluidState().isEmpty()) {
                                       int l3 = k3 - 1;
                                       blockpos$mutableblockpos1.set(blockpos$mutableblockpos);

                                       BlockState blockstate1;
                                       do {
                                          blockpos$mutableblockpos1.setY(l3--);
                                          blockstate1 = levelchunk.getBlockState(blockpos$mutableblockpos1);
                                          ++l2;
                                       } while(l3 > p_42894_.getMinBuildHeight() && !blockstate1.getFluidState().isEmpty());

                                       blockstate = this.getCorrectStateForFluidBlock(p_42894_, blockstate, blockpos$mutableblockpos);
                                    }
                                 }

                                 p_42896_.checkBanners(p_42894_, blockpos$mutableblockpos.getX(), blockpos$mutableblockpos.getZ());
                                 d1 += (double)k3 / (double)(i * i);
                                 multiset.add(blockstate.getMapColor(p_42894_, blockpos$mutableblockpos));
                              }
                           }
                        }

                        l2 /= i * i;
                        MapColor mapcolor = Iterables.getFirst(Multisets.copyHighestCountFirst(multiset), MapColor.NONE);
                        MapColor.Brightness mapcolor$brightness;
                        if (mapcolor == MapColor.WATER) {
                           double d2 = (double)l2 * 0.1D + (double)(k1 + l1 & 1) * 0.2D;
                           if (d2 < 0.5D) {
                              mapcolor$brightness = MapColor.Brightness.HIGH;
                           } else if (d2 > 0.9D) {
                              mapcolor$brightness = MapColor.Brightness.LOW;
                           } else {
                              mapcolor$brightness = MapColor.Brightness.NORMAL;
                           }
                        } else {
                           double d3 = (d1 - d0) * 4.0D / (double)(i + 4) + ((double)(k1 + l1 & 1) - 0.5D) * 0.4D;
                           if (d3 > 0.6D) {
                              mapcolor$brightness = MapColor.Brightness.HIGH;
                           } else if (d3 < -0.6D) {
                              mapcolor$brightness = MapColor.Brightness.LOW;
                           } else {
                              mapcolor$brightness = MapColor.Brightness.NORMAL;
                           }
                        }

                        d0 = d1;
                        if (l1 >= 0 && i2 < j1 * j1 && (!flag1 || (k1 + l1 & 1) != 0)) {
                           flag |= p_42896_.updateColor(k1, l1, mapcolor.getPackedId(mapcolor$brightness));
                        }
                     }
                  }
               }
            }
         }

      }
   }

   private BlockState getCorrectStateForFluidBlock(Level p_42901_, BlockState p_42902_, BlockPos p_42903_) {
      FluidState fluidstate = p_42902_.getFluidState();
      return !fluidstate.isEmpty() && !p_42902_.isFaceSturdy(p_42901_, p_42903_, Direction.UP) ? fluidstate.createLegacyBlock() : p_42902_;
   }

   private static boolean isBiomeWatery(boolean[] p_212252_, int p_212253_, int p_212254_) {
      return p_212252_[p_212254_ * 128 + p_212253_];
   }

   public static void renderBiomePreviewMap(ServerLevel p_42851_, ItemStack p_42852_) {
      MapItemSavedData mapitemsaveddata = getSavedData(p_42852_, p_42851_);
      if (mapitemsaveddata != null) {
         if (p_42851_.dimension() == mapitemsaveddata.dimension) {
            int i = 1 << mapitemsaveddata.scale;
            int j = mapitemsaveddata.centerX;
            int k = mapitemsaveddata.centerZ;
            boolean[] aboolean = new boolean[16384];
            int l = j / i - 64;
            int i1 = k / i - 64;
            BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

            for(int j1 = 0; j1 < 128; ++j1) {
               for(int k1 = 0; k1 < 128; ++k1) {
                  Holder<Biome> holder = p_42851_.getBiome(blockpos$mutableblockpos.set((l + k1) * i, 0, (i1 + j1) * i));
                  aboolean[j1 * 128 + k1] = holder.is(BiomeTags.WATER_ON_MAP_OUTLINES);
               }
            }

            for(int j2 = 1; j2 < 127; ++j2) {
               for(int k2 = 1; k2 < 127; ++k2) {
                  int l2 = 0;

                  for(int l1 = -1; l1 < 2; ++l1) {
                     for(int i2 = -1; i2 < 2; ++i2) {
                        if ((l1 != 0 || i2 != 0) && isBiomeWatery(aboolean, j2 + l1, k2 + i2)) {
                           ++l2;
                        }
                     }
                  }

                  MapColor.Brightness mapcolor$brightness = MapColor.Brightness.LOWEST;
                  MapColor mapcolor = MapColor.NONE;
                  if (isBiomeWatery(aboolean, j2, k2)) {
                     mapcolor = MapColor.COLOR_ORANGE;
                     if (l2 > 7 && k2 % 2 == 0) {
                        switch ((j2 + (int)(Mth.sin((float)k2 + 0.0F) * 7.0F)) / 8 % 5) {
                           case 0:
                           case 4:
                              mapcolor$brightness = MapColor.Brightness.LOW;
                              break;
                           case 1:
                           case 3:
                              mapcolor$brightness = MapColor.Brightness.NORMAL;
                              break;
                           case 2:
                              mapcolor$brightness = MapColor.Brightness.HIGH;
                        }
                     } else if (l2 > 7) {
                        mapcolor = MapColor.NONE;
                     } else if (l2 > 5) {
                        mapcolor$brightness = MapColor.Brightness.NORMAL;
                     } else if (l2 > 3) {
                        mapcolor$brightness = MapColor.Brightness.LOW;
                     } else if (l2 > 1) {
                        mapcolor$brightness = MapColor.Brightness.LOW;
                     }
                  } else if (l2 > 0) {
                     mapcolor = MapColor.COLOR_BROWN;
                     if (l2 > 3) {
                        mapcolor$brightness = MapColor.Brightness.NORMAL;
                     } else {
                        mapcolor$brightness = MapColor.Brightness.LOWEST;
                     }
                  }

                  if (mapcolor != MapColor.NONE) {
                     mapitemsaveddata.setColor(j2, k2, mapcolor.getPackedId(mapcolor$brightness));
                  }
               }
            }

         }
      }
   }

   public void inventoryTick(ItemStack p_42870_, Level p_42871_, Entity p_42872_, int p_42873_, boolean p_42874_) {
      if (!p_42871_.isClientSide) {
         MapItemSavedData mapitemsaveddata = getSavedData(p_42870_, p_42871_);
         if (mapitemsaveddata != null) {
            if (p_42872_ instanceof Player) {
               Player player = (Player)p_42872_;
               mapitemsaveddata.tickCarriedBy(player, p_42870_);
            }

            if (!mapitemsaveddata.locked && (p_42874_ || p_42872_ instanceof Player && ((Player)p_42872_).getOffhandItem() == p_42870_)) {
               this.update(p_42871_, p_42872_, mapitemsaveddata);
            }

         }
      }
   }

   @Nullable
   public Packet<?> getUpdatePacket(ItemStack p_42876_, Level p_42877_, Player p_42878_) {
      Integer integer = getMapId(p_42876_);
      MapItemSavedData mapitemsaveddata = getSavedData(integer, p_42877_);
      return mapitemsaveddata != null ? mapitemsaveddata.getUpdatePacket(integer, p_42878_) : null;
   }

   public void onCraftedBy(ItemStack p_42913_, Level p_42914_, Player p_42915_) {
      CompoundTag compoundtag = p_42913_.getTag();
      if (compoundtag != null && compoundtag.contains("map_scale_direction", 99)) {
         scaleMap(p_42913_, p_42914_, compoundtag.getInt("map_scale_direction"));
         compoundtag.remove("map_scale_direction");
      } else if (compoundtag != null && compoundtag.contains("map_to_lock", 1) && compoundtag.getBoolean("map_to_lock")) {
         lockMap(p_42914_, p_42913_);
         compoundtag.remove("map_to_lock");
      }

   }

   private static void scaleMap(ItemStack p_42857_, Level p_42858_, int p_42859_) {
      MapItemSavedData mapitemsaveddata = getSavedData(p_42857_, p_42858_);
      if (mapitemsaveddata != null) {
         int i = p_42858_.getFreeMapId();
         p_42858_.setMapData(makeKey(i), mapitemsaveddata.scaled(p_42859_));
         storeMapData(p_42857_, i);
      }

   }

   public static void lockMap(Level p_42898_, ItemStack p_42899_) {
      MapItemSavedData mapitemsaveddata = getSavedData(p_42899_, p_42898_);
      if (mapitemsaveddata != null) {
         int i = p_42898_.getFreeMapId();
         String s = makeKey(i);
         MapItemSavedData mapitemsaveddata1 = mapitemsaveddata.locked();
         p_42898_.setMapData(s, mapitemsaveddata1);
         storeMapData(p_42899_, i);
      }

   }

   public void appendHoverText(ItemStack p_42880_, @Nullable Level p_42881_, List<Component> p_42882_, TooltipFlag p_42883_) {
      Integer integer = getMapId(p_42880_);
      MapItemSavedData mapitemsaveddata = p_42881_ == null ? null : getSavedData(integer, p_42881_);
      CompoundTag compoundtag = p_42880_.getTag();
      boolean flag;
      byte b0;
      if (compoundtag != null) {
         flag = compoundtag.getBoolean("map_to_lock");
         b0 = compoundtag.getByte("map_scale_direction");
      } else {
         flag = false;
         b0 = 0;
      }

      if (mapitemsaveddata != null && (mapitemsaveddata.locked || flag)) {
         p_42882_.add(Component.translatable("filled_map.locked", integer).withStyle(ChatFormatting.GRAY));
      }

      if (p_42883_.isAdvanced()) {
         if (mapitemsaveddata != null) {
            if (!flag && b0 == 0) {
               p_42882_.add(Component.translatable("filled_map.id", integer).withStyle(ChatFormatting.GRAY));
            }

            int i = Math.min(mapitemsaveddata.scale + b0, 4);
            p_42882_.add(Component.translatable("filled_map.scale", 1 << i).withStyle(ChatFormatting.GRAY));
            p_42882_.add(Component.translatable("filled_map.level", i, 4).withStyle(ChatFormatting.GRAY));
         } else {
            p_42882_.add(Component.translatable("filled_map.unknown").withStyle(ChatFormatting.GRAY));
         }
      }

   }

   public static int getColor(ItemStack p_42919_) {
      CompoundTag compoundtag = p_42919_.getTagElement("display");
      if (compoundtag != null && compoundtag.contains("MapColor", 99)) {
         int i = compoundtag.getInt("MapColor");
         return -16777216 | i & 16777215;
      } else {
         return -12173266;
      }
   }

   public InteractionResult useOn(UseOnContext p_42885_) {
      BlockState blockstate = p_42885_.getLevel().getBlockState(p_42885_.getClickedPos());
      if (blockstate.is(BlockTags.BANNERS)) {
         if (!p_42885_.getLevel().isClientSide) {
            MapItemSavedData mapitemsaveddata = getSavedData(p_42885_.getItemInHand(), p_42885_.getLevel());
            if (mapitemsaveddata != null && !mapitemsaveddata.toggleBanner(p_42885_.getLevel(), p_42885_.getClickedPos())) {
               return InteractionResult.FAIL;
            }
         }

         return InteractionResult.sidedSuccess(p_42885_.getLevel().isClientSide);
      } else {
         return super.useOn(p_42885_);
      }
   }
}
