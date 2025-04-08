package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class WeatheringCopperSlabBlock extends SlabBlock implements WeatheringCopper {
   private final WeatheringCopper.WeatherState weatherState;

   public WeatheringCopperSlabBlock(WeatheringCopper.WeatherState p_154938_, BlockBehaviour.Properties p_154939_) {
      super(p_154939_);
      this.weatherState = p_154938_;
   }

   public void randomTick(BlockState p_222670_, ServerLevel p_222671_, BlockPos p_222672_, RandomSource p_222673_) {
      this.onRandomTick(p_222670_, p_222671_, p_222672_, p_222673_);
   }

   public boolean isRandomlyTicking(BlockState p_154947_) {
      return WeatheringCopper.getNext(p_154947_.getBlock()).isPresent();
   }

   public WeatheringCopper.WeatherState getAge() {
      return this.weatherState;
   }
}