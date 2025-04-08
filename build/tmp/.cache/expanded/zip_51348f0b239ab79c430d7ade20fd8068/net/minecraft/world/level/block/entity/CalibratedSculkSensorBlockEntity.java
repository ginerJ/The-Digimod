package net.minecraft.world.level.block.entity;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CalibratedSculkSensorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.vibrations.VibrationSystem;

public class CalibratedSculkSensorBlockEntity extends SculkSensorBlockEntity {
   public CalibratedSculkSensorBlockEntity(BlockPos p_277459_, BlockState p_278100_) {
      super(BlockEntityType.CALIBRATED_SCULK_SENSOR, p_277459_, p_278100_);
   }

   public VibrationSystem.User createVibrationUser() {
      return new CalibratedSculkSensorBlockEntity.VibrationUser(this.getBlockPos());
   }

   protected class VibrationUser extends SculkSensorBlockEntity.VibrationUser {
      public VibrationUser(BlockPos p_281602_) {
         super(p_281602_);
      }

      public int getListenerRadius() {
         return 16;
      }

      public boolean canReceiveVibration(ServerLevel p_282061_, BlockPos p_282550_, GameEvent p_281789_, @Nullable GameEvent.Context p_281456_) {
         int i = this.getBackSignal(p_282061_, this.blockPos, CalibratedSculkSensorBlockEntity.this.getBlockState());
         return i != 0 && VibrationSystem.getGameEventFrequency(p_281789_) != i ? false : super.canReceiveVibration(p_282061_, p_282550_, p_281789_, p_281456_);
      }

      private int getBackSignal(Level p_282204_, BlockPos p_282397_, BlockState p_282240_) {
         Direction direction = p_282240_.getValue(CalibratedSculkSensorBlock.FACING).getOpposite();
         return p_282204_.getSignal(p_282397_.relative(direction), direction);
      }
   }
}