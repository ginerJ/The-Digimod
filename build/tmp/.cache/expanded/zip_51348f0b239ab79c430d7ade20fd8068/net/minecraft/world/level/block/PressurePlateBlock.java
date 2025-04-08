package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public class PressurePlateBlock extends BasePressurePlateBlock {
   public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
   private final PressurePlateBlock.Sensitivity sensitivity;

   public PressurePlateBlock(PressurePlateBlock.Sensitivity p_273523_, BlockBehaviour.Properties p_273571_, BlockSetType p_273284_) {
      super(p_273571_, p_273284_);
      this.registerDefaultState(this.stateDefinition.any().setValue(POWERED, Boolean.valueOf(false)));
      this.sensitivity = p_273523_;
   }

   protected int getSignalForState(BlockState p_55270_) {
      return p_55270_.getValue(POWERED) ? 15 : 0;
   }

   protected BlockState setSignalForState(BlockState p_55259_, int p_55260_) {
      return p_55259_.setValue(POWERED, Boolean.valueOf(p_55260_ > 0));
   }

   protected int getSignalStrength(Level p_55264_, BlockPos p_55265_) {
      Class<? extends Entity> oclass1;
      switch (this.sensitivity) {
         case EVERYTHING:
            oclass1 = Entity.class;
            break;
         case MOBS:
            oclass1 = LivingEntity.class;
            break;
         default:
            throw new IncompatibleClassChangeError();
      }

      Class oclass = oclass1;
      return getEntityCount(p_55264_, TOUCH_AABB.move(p_55265_), oclass) > 0 ? 15 : 0;
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_55262_) {
      p_55262_.add(POWERED);
   }

   public static enum Sensitivity {
      EVERYTHING,
      MOBS;
   }
}