package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class NetherWartBlock extends BushBlock {
   public static final int MAX_AGE = 3;
   public static final IntegerProperty AGE = BlockStateProperties.AGE_3;
   private static final VoxelShape[] SHAPE_BY_AGE = new VoxelShape[]{Block.box(0.0D, 0.0D, 0.0D, 16.0D, 5.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 11.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 14.0D, 16.0D)};

   public NetherWartBlock(BlockBehaviour.Properties p_54971_) {
      super(p_54971_);
      this.registerDefaultState(this.stateDefinition.any().setValue(AGE, Integer.valueOf(0)));
   }

   public VoxelShape getShape(BlockState p_54986_, BlockGetter p_54987_, BlockPos p_54988_, CollisionContext p_54989_) {
      return SHAPE_BY_AGE[p_54986_.getValue(AGE)];
   }

   protected boolean mayPlaceOn(BlockState p_54991_, BlockGetter p_54992_, BlockPos p_54993_) {
      return p_54991_.is(Blocks.SOUL_SAND);
   }

   public boolean isRandomlyTicking(BlockState p_54979_) {
      return p_54979_.getValue(AGE) < 3;
   }

   public void randomTick(BlockState p_221806_, ServerLevel p_221807_, BlockPos p_221808_, RandomSource p_221809_) {
      int i = p_221806_.getValue(AGE);
      if (i < 3 && net.minecraftforge.common.ForgeHooks.onCropsGrowPre(p_221807_, p_221808_, p_221806_, p_221809_.nextInt(10) == 0)) {
         p_221806_ = p_221806_.setValue(AGE, Integer.valueOf(i + 1));
         p_221807_.setBlock(p_221808_, p_221806_, 2);
         net.minecraftforge.common.ForgeHooks.onCropsGrowPost(p_221807_, p_221808_, p_221806_);
      }

   }

   public ItemStack getCloneItemStack(BlockGetter p_54973_, BlockPos p_54974_, BlockState p_54975_) {
      return new ItemStack(Items.NETHER_WART);
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_54977_) {
      p_54977_.add(AGE);
   }
}
