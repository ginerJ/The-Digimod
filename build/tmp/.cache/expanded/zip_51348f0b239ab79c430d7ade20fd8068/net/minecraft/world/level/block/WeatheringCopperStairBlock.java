package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class WeatheringCopperStairBlock extends StairBlock implements WeatheringCopper {
   private final WeatheringCopper.WeatherState weatherState;

   public WeatheringCopperStairBlock(WeatheringCopper.WeatherState p_154951_, BlockState p_154952_, BlockBehaviour.Properties p_154953_) {
      super(p_154952_, p_154953_);
      this.weatherState = p_154951_;
   }

   public void randomTick(BlockState p_222675_, ServerLevel p_222676_, BlockPos p_222677_, RandomSource p_222678_) {
      this.onRandomTick(p_222675_, p_222676_, p_222677_, p_222678_);
   }

   public boolean isRandomlyTicking(BlockState p_154961_) {
      return WeatheringCopper.getNext(p_154961_.getBlock()).isPresent();
   }

   public WeatheringCopper.WeatherState getAge() {
      return this.weatherState;
   }
}