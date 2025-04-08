package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import net.minecraft.Util;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.MultifaceBlock;

public class MultifaceGrowthConfiguration implements FeatureConfiguration {
   public static final Codec<MultifaceGrowthConfiguration> CODEC = RecordCodecBuilder.create((p_225407_) -> {
      return p_225407_.group(BuiltInRegistries.BLOCK.byNameCodec().fieldOf("block").flatXmap(MultifaceGrowthConfiguration::apply, DataResult::success).orElse((MultifaceBlock)Blocks.GLOW_LICHEN).forGetter((p_225424_) -> {
         return p_225424_.placeBlock;
      }), Codec.intRange(1, 64).fieldOf("search_range").orElse(10).forGetter((p_225422_) -> {
         return p_225422_.searchRange;
      }), Codec.BOOL.fieldOf("can_place_on_floor").orElse(false).forGetter((p_225420_) -> {
         return p_225420_.canPlaceOnFloor;
      }), Codec.BOOL.fieldOf("can_place_on_ceiling").orElse(false).forGetter((p_225418_) -> {
         return p_225418_.canPlaceOnCeiling;
      }), Codec.BOOL.fieldOf("can_place_on_wall").orElse(false).forGetter((p_225416_) -> {
         return p_225416_.canPlaceOnWall;
      }), Codec.floatRange(0.0F, 1.0F).fieldOf("chance_of_spreading").orElse(0.5F).forGetter((p_225414_) -> {
         return p_225414_.chanceOfSpreading;
      }), RegistryCodecs.homogeneousList(Registries.BLOCK).fieldOf("can_be_placed_on").forGetter((p_225409_) -> {
         return p_225409_.canBePlacedOn;
      })).apply(p_225407_, MultifaceGrowthConfiguration::new);
   });
   public final MultifaceBlock placeBlock;
   public final int searchRange;
   public final boolean canPlaceOnFloor;
   public final boolean canPlaceOnCeiling;
   public final boolean canPlaceOnWall;
   public final float chanceOfSpreading;
   public final HolderSet<Block> canBePlacedOn;
   private final ObjectArrayList<Direction> validDirections;

   private static DataResult<MultifaceBlock> apply(Block p_225405_) {
      DataResult dataresult;
      if (p_225405_ instanceof MultifaceBlock multifaceblock) {
         dataresult = DataResult.success(multifaceblock);
      } else {
         dataresult = DataResult.error(() -> {
            return "Growth block should be a multiface block";
         });
      }

      return dataresult;
   }

   public MultifaceGrowthConfiguration(MultifaceBlock p_225392_, int p_225393_, boolean p_225394_, boolean p_225395_, boolean p_225396_, float p_225397_, HolderSet<Block> p_225398_) {
      this.placeBlock = p_225392_;
      this.searchRange = p_225393_;
      this.canPlaceOnFloor = p_225394_;
      this.canPlaceOnCeiling = p_225395_;
      this.canPlaceOnWall = p_225396_;
      this.chanceOfSpreading = p_225397_;
      this.canBePlacedOn = p_225398_;
      this.validDirections = new ObjectArrayList<>(6);
      if (p_225395_) {
         this.validDirections.add(Direction.UP);
      }

      if (p_225394_) {
         this.validDirections.add(Direction.DOWN);
      }

      if (p_225396_) {
         Direction.Plane.HORIZONTAL.forEach(this.validDirections::add);
      }

   }

   public List<Direction> getShuffledDirectionsExcept(RandomSource p_225402_, Direction p_225403_) {
      return Util.toShuffledList(this.validDirections.stream().filter((p_225412_) -> {
         return p_225412_ != p_225403_;
      }), p_225402_);
   }

   public List<Direction> getShuffledDirections(RandomSource p_225400_) {
      return Util.shuffledCopy(this.validDirections, p_225400_);
   }
}