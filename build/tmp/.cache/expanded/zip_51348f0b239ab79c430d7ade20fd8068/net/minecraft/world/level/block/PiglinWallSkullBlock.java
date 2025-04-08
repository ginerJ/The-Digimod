package net.minecraft.world.level.block;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class PiglinWallSkullBlock extends WallSkullBlock {
   private static final Map<Direction, VoxelShape> AABBS = Maps.immutableEnumMap(Map.of(Direction.NORTH, Block.box(3.0D, 4.0D, 8.0D, 13.0D, 12.0D, 16.0D), Direction.SOUTH, Block.box(3.0D, 4.0D, 0.0D, 13.0D, 12.0D, 8.0D), Direction.EAST, Block.box(0.0D, 4.0D, 3.0D, 8.0D, 12.0D, 13.0D), Direction.WEST, Block.box(8.0D, 4.0D, 3.0D, 16.0D, 12.0D, 13.0D)));

   public PiglinWallSkullBlock(BlockBehaviour.Properties p_261530_) {
      super(SkullBlock.Types.PIGLIN, p_261530_);
   }

   public VoxelShape getShape(BlockState p_261765_, BlockGetter p_261604_, BlockPos p_261948_, CollisionContext p_261889_) {
      return AABBS.get(p_261765_.getValue(FACING));
   }
}